<template>
    <div v-if="visible" ref="printSection" id="printSection"  class="drawer-div" >
        <div v-if="loadFaild">
            <p style="font-size: small;color: red;text-align: center;margin: 0 10%;">
                {{ tips }}
            </p>
        </div>
        <el-scrollbar >
            <el-card>
                <template v-slot:header>
                    <div class="clearfix">
                        <span>表单信息</span>
                    </div>
                </template>
                <el-container>
                    <el-main>
                        <template v-slot:header>
                            <div class="clearfix">
                                <span>可用流程(DIY)</span>
                            </div>
                        </template>
                        <div v-if="componentLoaded" class="component">
                            <component :is="loadedComponent" :previewData="componentData" :lfFormData="lfFormDataConfig"
                                :lfFieldsData="lfFieldsConfig" :lfFieldPerm="lfFieldControlVOs" :isPreview="isPreview">
                            </component>
                        </div>
                    </el-main>
                </el-container>
            </el-card>
            <el-card>
                <template v-slot:header>
                    <div class="clearfix">
                        <span>审批记录</span>
                    </div>
                </template>
                <el-container>
                    <el-main>
                        <FlowStepTable />
                    </el-main>
                </el-container>
            </el-card>
            

            <ProcessStateImg :process-state="processState" />
            
        </el-scrollbar>
    </div>
</template>
<script setup>
import { ref, computed } from 'vue';
import { useWindowSize } from '@vueuse/core'
import { getViewBusinessProcess, processOperation } from "@/api/workflow/index";
import { useStore } from '@/store/modules/workflow';
import { loadDIYComponent, loadLFComponent } from '@/views/workflow/components/componentload.js';
import { isTrue } from '@/utils/antflow/ObjectUtils';
import FlowStepTable from "@/components/Workflow/Preview/flowStepTable.vue"
import ProcessStateImg from '@/views/workflow/components/ProcessStateImg.vue'
import html2canvas from 'html2canvas';

let store = useStore()
let viewConfig = computed(() => store.instanceViewConfig1)
let processState = computed(() => viewConfig.value.processState)
const { proxy } = getCurrentInstance();
const { width, height } = useWindowSize()

const printSection = ref(null);

const printLoaded = inject('printLoaded');

let props = defineProps({
    isPreview: {
        type: Boolean,
        default: false,
    }
});
let viewHeight = computed(() => {
    return height.value - 170 + 'px';
})
let loadFaild = ref(false);
let componentData = ref(null);
let componentLoaded = ref(false);
let loadedComponent = ref(null);
let lfFormDataConfig = ref(null);
let lfFieldsConfig = ref(null);
let lfFieldControlVOs = ref(null);

let tips = "*未获取到外部表单信息，请联系管理员。";
let visible = computed({
    get() {
        return true;
    }
})

onMounted(async () => {
  await nextTick(()=>{
      // 等待DOM更新
      
       setTimeout(() => {
            printhtml2canvas();
        }, 3300);
       
  }); 
 
});

const printhtml2canvas = () => {
    const element = proxy.$refs['printSection']; // 获取需要打印的DOM元素
    html2canvas(element, {
        useCORS:true,
        width: element.width,
        height: element.height,
        scale: 2,
        onclone: function (clonedDoc) {
            // I made the div hidden and here I am changing it to visible
            clonedDoc.getElementById('printSection').style.display = 'block';

            // 解决select内容遮挡
            clonedDoc.querySelectorAll('div.el-select__placeholder').forEach(ele => {
                const height = ele.getBoundingClientRect().height
                // 将transform设置为none，并改用calc的方式计算定位
                ele.setAttribute('style', `transform: none;top: calc(50% - ${height / 2}px);`)
            })

            // 解决input内容向上偏移问题
            clonedDoc.querySelectorAll('input').forEach(input => {
                input.style.height = '32px'
                input.style.lineHeight = '14px'
                input.style.paddingTop = '6px'
            })

            // 解决boxShadow边框渲染粗问题
            clonedDoc.querySelectorAll('.el-input__wrapper,.el-select__wrapper,.el-textarea__inner').forEach(border => {
                border.style.border = '1px solid var(--el-input-border-color,var(--el-border-color))'
                border.style.boxShadow = 'none'
            })

            const tableNode = clonedDoc.querySelectorAll('.el-table__header,.el-table__body');
            //el-table 打印不全的问题
            for (let k6 = 0; k6 < tableNode.length; k6++) {
                const tableItem = tableNode[k6];
                tableItem.style.width = '100%';
                const child = tableItem.childNodes;
                for (let i = 0; i < child.length; i++) {
                    const element = child[i];
                    if (element.localName === 'colgroup') {
                        element.innerHTML = '';
                    }
                }
            }
            //el-table 格子里面打印超过格子的问题
            let cells = clonedDoc.querySelectorAll('.cell');
            for (let k7 = 0; k7 < cells.length; k7++) {
                const cell = cells[k7];
                cell.style.width = '100%';
                cell.removeAttribute('style')
            }
        }
        }).then(canvas => {
        // 将canvas转换为图片并打印
        const imgData = canvas.toDataURL('image/png');
        
        const newWin = window.open("");
        newWin.document.body.style.height = "95%";
        var img = newWin.document.createElement('img');
        img.src = imgData;
        img.style.height = "auto";
        img.style.width = "100%";
        newWin.document.body.appendChild(img);
        
        setTimeout(() => {
            newWin.print();
            newWin.close();

            printLoaded.value = false;
        }, 300);
    
    });
}


/**预览 */
const preview = async (param) => {
    let queryParams = ref({
        formCode: param.formCode,
        processNumber: param.processNumber,
        type: 2,
        isOutSideAccessProc: param.isOutSideAccess || false,
        isLowCodeFlow: param.isLowCodeFlow || false
    });
    proxy.$modal.loading();
    await getViewBusinessProcess(queryParams.value).then(async (response) => {
        if (response.code == 200) {
            const responseData = response.data;
            componentLoaded.value = true
            
            if (isTrue(responseData.isLowCodeFlow)) {//低代码表单 和 三方接入
                lfFormDataConfig.value = responseData.lfFormData;
                lfFieldsConfig.value = JSON.stringify(responseData.lfFields);
                lfFieldControlVOs.value = JSON.stringify(responseData.processRecordInfo.lfFieldControlVOs);
                loadedComponent.value = await loadLFComponent(param.formCode);
            }
            else {//自定义开发表单
                loadedComponent.value = await loadDIYComponent(param.formCode).catch((err) => { proxy.$modal.msgError(err); });
                componentData.value = responseData;
            }
        } else {
            loadFaild.value = true
        }
        proxy.$modal.closeLoading();
    });
}
preview(viewConfig.value);

</script>
<style lang="scss" scoped>
.component {
    left: 0 !important;
    right: 0 !important;
    max-width: 720px !important;
    min-height: auto !important;
    margin: auto !important;
    background: white !important;
}

.drawer-div {
    border: 1px solid #eee;
    border-radius: 4px;
    background: #fff;
    display: none;
}

.el-header {
    background-color: #eee;
    display: flex;
    align-items: center;
}
</style>