<template>
    <section class="antflow-design" ref="antflowDesignRef">
        <div class="zoom">
            <div class="zoom-out" @click="zoomOut" title="缩小"></div>
            <span>{{ nowVal }}%</span>
            <div class="zoom-in" @click="zoomIn" title="放大"></div>
            <!--刷新图标代码-->
            <div class="zoom-reset" @click="zoomReset" title="还原缩放比例">&#10227</div>
        </div>
        <div class="box-scale" ref="boxScaleRef">
            <nodeWrap v-model:nodeConfig="nodeConfig" />
            <div class="end-node">
                <div class="end-node-circle"></div>
                <div class="end-node-text">流程结束</div>
            </div>
        </div>
    </section>
    <errorDialog v-model:visible="tipVisible" :list="tipList" />
    <promoterDrawer />
    <approverDrawer :directorMaxLevel="directorMaxLevel" />
    <copyerDrawer />
    <conditionDrawer />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useStore } from '@/store/modules/workflow'
import { nodeTypeList } from '@/utils/flow/const'
import errorDialog from "@/components/Workflow/dialog/errorDialog.vue";
import promoterDrawer from "@/components/Workflow/drawer/promoterDrawer.vue";
import approverDrawer from "@/components/Workflow/drawer/approverDrawer.vue";
import copyerDrawer from "@/components/Workflow/drawer/copyerDrawer.vue";
import conditionDrawer from "@/components/Workflow/drawer/conditionDrawer.vue";
import {wheelZoomFunc, zoomInit,resetImage} from "@/utils/zoom.js";
const { proxy } = getCurrentInstance();
let { setIsTried } = useStore()
const emit = defineEmits(['nextChange'])
let props = defineProps({
    processData: {
        type: Object,
        default: () => (null),
    }
});
const antflowDesignRef = ref(null);
const boxScaleRef = ref(null);
let tipList = ref([]);
let tipVisible = ref(false);
let nowVal = ref(100);
let nodeConfig = ref({});
let directorMaxLevel = ref(3);
onMounted(async () => { 
    zoomInit(antflowDesignRef, boxScaleRef, (val) => { 
        nowVal.value = val
    });
    if (props.processData) {
        nodeConfig.value = props.processData;
    }
});

/**
 * 判断流程中是否有审批节点 Demo 预览需要，项目中不使用可以去掉这步验证
 * @param treeNode 
 */
 const preTreeIsApproveNode = (treeNode) =>  { 
  if(!treeNode) return false;  
  if(treeNode.nodeType == 4) { 
    return true;
  }
  else{
    return preTreeIsApproveNode(treeNode.childNode);
  } 
}
/**
 * 并行审批节点验证
 * 判断存在并行审批就必须有聚合节点
 * @param treeNode 
 */
const preTreeIsParallelNode = (treeNode) => { 
    if (proxy.isObjEmpty(treeNode)) return true;
    if (treeNode.nodeType == 7) {
        if(proxy.isObjEmpty(treeNode.childNode)){
            return false;
        }else{
            return preTreeIsParallelNode(treeNode.childNode);
        }
    }
    else {
        return preTreeIsParallelNode(treeNode.childNode);
    }
}
/**
 * 节点必填校验
 * @param childNode 
 */
const reErr = ({ childNode }) => {
    if (childNode) {
        let { nodeType, error, nodeName, conditionNodes } = childNode;
        if (nodeType == 1) {
            reErr(childNode);
        }
        else if (nodeType == 2) {
            reErr(childNode);
            for (var i = 0; i < conditionNodes.length; i++) {
                if (conditionNodes[i].error) {
                    tipList.value.push({ name: conditionNodes[i].nodeName, nodeType: "条件" });
                }
                reErr(conditionNodes[i]);
            }
        }
        else if (nodeType == 3) {
            reErr(childNode);
        }
        else if (nodeType == 4 || nodeType == 5) {
            if (error) {
                tipList.value.push({
                    name: nodeName,
                    nodeType: nodeTypeList[nodeType],
                });
            }
            reErr(childNode);
        }
    } else {
        childNode = null;
    }
};
/** 页面放大 */
function zoomIn() {
  wheelZoomFunc({scaleFactor: parseInt(nowVal.value) / 100 + 0.1, isExternalCall: true})
}

/** 页面缩小 */
function zoomOut() {
  wheelZoomFunc({scaleFactor: parseInt(nowVal.value) / 100 - 0.1, isExternalCall: true})
}
/** 还原缩放比例 */
function zoomReset() {
  resetImage()
}

const getJson = () => {
    setIsTried(true); 
     /**并行审批验证 */
     let verifyParallelNode = preTreeIsParallelNode(nodeConfig.value); 
    if (!verifyParallelNode) {
        proxy.$modal.msgError("并行审批下必须有一个审批人节点作为聚合节点");
        emit('nextChange', { label: "流程设计", key: "processDesign" });
        return false;
    }  
    let verifyApproveNode = preTreeIsApproveNode(nodeConfig.value);   
    if (!nodeConfig.value || !nodeConfig.value.childNode || !verifyApproveNode) {
        emit('nextChange', { label: "流程设计", key: "processDesign" }); 
        return false;
    } 
   
    tipList.value = [];
    reErr(nodeConfig.value);
    if (tipList.value.length != 0) {
        emit('nextChange', { label: "流程设计", key: "processDesign" });
        tipVisible.value = true;
        return false;
    }
    let submitData = JSON.parse(JSON.stringify(nodeConfig.value));
    return submitData;
};

// 给父级页面提供得获取本页数据得方法
const getData = () => {
    let resData = getJson();
    return new Promise((resolve, reject) => {
        if (!resData) {
            reject({ formData: null });
        }
        resolve({ formData: resData })
    })
};
defineExpose({
    getData
})
</script>
<style scoped lang="scss">
@import "@/assets/styles/flow/workflow.scss"; 
</style>