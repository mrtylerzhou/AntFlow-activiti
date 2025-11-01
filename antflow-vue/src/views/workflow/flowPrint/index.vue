<template>
    <div class="drawer-div">
        <div style="margin:10px auto; text-align:center;">
            <el-button type="success" @click="clickPrint()">打 印</el-button>
            <el-button @click="closeWin()">关 闭</el-button>
        </div>
        <div ref="printSection" id="printSection">
            <el-card style="margin-bottom: 10px;">
                <template v-slot:header>
                    <div class="clearfix">
                        <span>表单信息</span>
                    </div>
                </template>
                <el-container>
                    <el-main>
                        <flow-biz-form />
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
                        <flow-step-table />
                    </el-main>
                </el-container>
            </el-card>
        </div>

    </div>
</template>
<script setup>
import { ref } from 'vue';
import html2canvas from 'html2canvas';
import flowBizForm from "./components/flowBizForm.vue";
import flowStepTable from "./components/flowStepTable.vue";
const { proxy } = getCurrentInstance();
const printSection = ref(null);

const printhtml2canvas = () => {
    const element = proxy.$refs['printSection']; // 获取需要打印的DOM元素
    const options = {
        useCORS: true,
        width: element.width,
        height: element.height,
        scale: 2,
        onclone: function (clonedDoc) {
            const ps = clonedDoc.getElementById('printSection');
            Object.assign(ps.style, {
                display: 'block',
                margin: '0 auto',
                width: '780px',
            });
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
    }; html2canvas(element, options).then(canvas => {
        // 将canvas转换为图片并打印
        const imgData = canvas.toDataURL('image/png');
        const newWin = window.open("");

        Object.assign(newWin.document.body.style, {
            display: 'flex',
            height: '720px',
            margin: '0',
            padding: '0',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'flex-start',
        });

        const img = newWin.document.createElement('img');
        Object.assign(img.style, {
            display: 'block',
            width: '720px',
            height: 'auto',
            margin: '20px auto',
        });
        img.src = imgData;
        newWin.document.body.appendChild(img);
        img.onload = () => {
            newWin.focus();
            newWin.print();
            newWin.close();
        };
        img.onerror = () => {
            newWin.close();
        };
    });
}
const clickPrint = () => {
    printhtml2canvas();
}
const closeWin = () => {
    window.close();
}
</script>
<style lang="scss" scoped>
.drawer-div {
    width: 1280px;
    margin: 0 auto;
    border-radius: 4px;
    background: #fff;
    display: block;
}
</style>