<template>
    <div>
        <section class="antflow-design" ref="antflowDesignRef">
            <div class="zoom">
                <div class="zoom-out" @click="zoomOut" title="缩小"></div>
                <span>{{ nowVal }}%</span>
                <div class="zoom-in" @click="zoomIn" title="放大"></div>
                <!--刷新图标代码-->
                <div class="zoom-reset" @click="zoomReset" title="还原缩放比例">&#10227</div>
            </div>
            <div class="box-scale" ref="boxScaleRef">
                <outsideNodeWrap v-model:nodeConfig="nodeConfig" />
                <div class="end-node">
                    <div class="end-node-circle"></div>
                    <div class="end-node-text">流程结束</div>
                </div>
            </div>
        </section>

    </div>
    <errorDialog v-model:visible="tipVisible" :list="tipList" />
    <promoterDrawer />
    <approverDrawer :directorMaxLevel="directorMaxLevel" />
    <copyerDrawer />
    <conditionDrawer />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useStore } from '@/store/modules/outsideflow'
import { nodeTypeList } from '@/utils/flow/const'
import errorDialog from "@/components/OutsideFlow/dialog/errorDialog.vue";
import promoterDrawer from "@/components/OutsideFlow/drawer/promoterDrawer.vue";
import approverDrawer from "@/components/OutsideFlow/drawer/approverDrawer.vue";
import copyerDrawer from "@/components/OutsideFlow/drawer/copyerDrawer.vue";
import conditionDrawer from "@/components/OutsideFlow/drawer/conditionDrawer.vue"; 
import outsideNodeWrap from "@/components/OutsideFlow/nodeWrap.vue";
import {wheelZoomFunc, zoomInit,resetImage} from "@/utils/zoom.js"; 
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
    })
    if (props.processData) {
        nodeConfig.value = props.processData;
    }
});
/**
 * 判断流程中是否有审批节点
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
        else if (nodeType == 4 || nodeType == 6) {
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
    tipList.value = []; 
    let isApproveNode = preTreeIsApproveNode(nodeConfig.value);
    if (!nodeConfig.value || !nodeConfig.value.childNode || !isApproveNode) {
        emit('nextChange', { label: "流程设计", key: "processDesign" }); 
        return false;
    } 
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

.app-container {
    position: relative;
    background-color: #f5f5f7;
    min-height: calc(100vh - 100px);
    padding-top: 5px;
    margin-top: 20px !important;
    height: auto;
    overflow: auto;
}

.clearfix {
    zoom: 1
}
 
</style>