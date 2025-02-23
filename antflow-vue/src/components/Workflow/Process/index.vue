<template>
    <div class="app-container">
        <section class="dingflow-design">
            <div class="zoom">
                <div class="zoom-out" :class="nowVal == 50 && 'disabled'" @click="zoomSize(1)"></div>
                <span>{{ nowVal }}%</span>
                <div class="zoom-in" :class="nowVal == 300 && 'disabled'" @click="zoomSize(2)"></div>
            </div>
            <div class="box-scale" :style="`transform: scale(${nowVal / 100});`">
                <nodeWrap v-model:nodeConfig="nodeConfig" />
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
import { useStore } from '@/store/modules/workflow'
import { nodeTypeList } from '@/utils/flow/const'
import errorDialog from "@/components/Workflow/dialog/errorDialog.vue";
import promoterDrawer from "@/components/Workflow/drawer/promoterDrawer.vue";
import approverDrawer from "@/components/Workflow/drawer/approverDrawer.vue";
import copyerDrawer from "@/components/Workflow/drawer/copyerDrawer.vue";
import conditionDrawer from "@/components/Workflow/drawer/conditionDrawer.vue";
const { proxy } = getCurrentInstance();
let { setIsTried } = useStore()
const emit = defineEmits(['nextChange'])
let props = defineProps({
    processData: {
        type: Object,
        default: () => (null),
    }
});

let tipList = ref([]);
let tipVisible = ref(false);
let nowVal = ref(100);
let nodeConfig = ref({});
let directorMaxLevel = ref(3);
onMounted(async () => {
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

const zoomSize = (type) => {
    if (type == 1) {
        if (nowVal.value == 50) {
            return;
        }
        nowVal.value -= 10;
    } else {
        if (nowVal.value == 300) {
            return;
        }
        nowVal.value += 10;
    }
};

const getJson = () => {
    setIsTried(true); 
    let isApproveNode = preTreeIsApproveNode(nodeConfig.value);
    if (!nodeConfig.value || !nodeConfig.value.childNode || !isApproveNode) {
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

.zoom {
    display: flex;
    position: fixed;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    height: 40px;
    width: 125px;
    right: 40px;
    margin-top: 30px;
    z-index: 10
}

.zoom .zoom-in,
.zoom .zoom-out {
    width: 30px;
    height: 30px;
    background: #fff;
    color: #c1c1cd;
    cursor: pointer;
    background-size: 100%;
    background-repeat: no-repeat
}

.zoom .zoom-out {
    background-image: url(https://gw.alicdn.com/tfs/TB1s0qhBHGYBuNjy0FoXXciBFXa-90-90.png)
}

.zoom .zoom-out.disabled {
    opacity: .5
}

.zoom .zoom-in {
    background-image: url(https://gw.alicdn.com/tfs/TB1UIgJBTtYBeNjy1XdXXXXyVXa-90-90.png)
}

.zoom .zoom-in.disabled {
    opacity: .5
}
</style>