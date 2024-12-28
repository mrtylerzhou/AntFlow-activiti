<!--
 * @Date: 2022-09-21 14:41:53
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2023-05-24 15:20:24
 * @FilePath: /ant-flow/src/components/nodeWrap.vue
-->
<template>
  <div class="node-wrap" v-if="nodeConfig.nodeType != 2">
    <div class="node-wrap-box" :class="(nodeConfig.nodeType == 1 ? 'start-node ' : '') +(isTried && nodeConfig.error ? 'active error' : '')">
        <div class="title" :style="`background: rgb(${bgColors[nodeConfig.nodeType]});`">
          <span v-if="nodeConfig.nodeType == 1">{{ nodeConfig.nodeName }}</span>
          <template v-else>
            <span class="iconfont">{{nodeConfig.nodeType == 4?'':''}}</span>
            <input
              v-if="isInput"
              type="text"
              class="fd-input editable-title-input"
              @blur="blurEvent()"
              @focus="$event.currentTarget.select()"
              v-focus
              v-model="nodeConfig.nodeName"
              :placeholder="defaultText"
            />
            <span v-else class="editable-title" @click="clickEvent()">{{ nodeConfig.nodeName }}</span>
            <i class="anticon anticon-close close" @click="delNode"></i>
          </template>
        </div>
        <div class="content" @click="setPerson">
          <div class="text">
              <span class="placeholder" v-if="!showText">请选择{{defaultText}}</span>
              {{showText}}
          </div>
          <i class="anticon anticon-right arrow"></i>
        </div>
        <div class="error_tip" v-if="isTried && nodeConfig.error">
          <i class="anticon anticon-exclamation-circle"></i>
        </div>
    </div>
    <addNode v-model:childNodeP="nodeConfig.childNode" />
  </div>
  <div class="branch-wrap" v-if="nodeConfig.nodeType == 2">
    <div class="branch-box-wrap">
      <div class="branch-box">
        <button class="add-branch" @click="addTerm">添加条件</button>
        <div class="col-box" v-for="(item, index) in nodeConfig.conditionNodes" :key="index">
          <div class="condition-node">
            <div class="condition-node-box">
              <div class="auto-judge" :class="isTried && item.error ? 'error active' : ''">
                <div class="sort-left" v-if="index != 0" @click="arrTransfer(index, -1)">&lt;</div>
                <div class="title-wrapper">
                  <input
                    v-if="isInputList[index]"
                    type="text"
                    class="fd-input editable-title-input"
                    @blur="blurEvent(index)"
                    @focus="$event.currentTarget.select()"
                    v-focus
                    v-model="item.nodeName"
                  />
                  <span v-else class="editable-title" @click="clickEvent(index)">{{ item.nodeName }}</span>
                  <span class="priority-title" @click="setPerson(item.priorityLevel)">优先级{{ item.priorityLevel }}</span>
                  <i class="anticon anticon-close close" @click="delTerm(index)"></i>
                </div>
                <div class="sort-right" v-if="index != nodeConfig.conditionNodes.length - 1" @click="arrTransfer(index)">&gt;</div>
                <div class="content" @click="setPerson(item.priorityLevel)">{{item.nodeDisplayName||$func.conditionStr(nodeConfig, index) }}</div>
                <div class="error_tip" v-if="isTried && item.error">
                    <i class="anticon anticon-exclamation-circle"></i>
                </div>
              </div>
              <addNode v-model:childNodeP="item.childNode" />
            </div>
          </div>
          <nodeWrap v-if="item.childNode" v-model:nodeConfig="item.childNode" />
          <template v-if="index == 0">
            <div class="top-left-cover-line"></div>
            <div class="bottom-left-cover-line"></div>
          </template>
          <template v-if="index == nodeConfig.conditionNodes.length - 1">
            <div class="top-right-cover-line"></div>
            <div class="bottom-right-cover-line"></div>
          </template>
        </div>
      </div>
      <addNode v-model:childNodeP="nodeConfig.childNode" />
    </div>
  </div>
  <nodeWrap v-if="nodeConfig.childNode" v-model:nodeConfig="nodeConfig.childNode"/>
</template>
<script setup>
import { onMounted, ref, watch, getCurrentInstance, computed } from "vue";
import $func from "@/utils/flow/index";
import { useStore } from '@/store/modules/workflow'
import { bgColors, placeholderList } from '@/utils/flow/const'
import { NodeUtils } from '@/utils/flow/nodeUtils'
let _uid = getCurrentInstance().uid;

let props = defineProps({
    nodeConfig: {
        type: Object,
        default: () => ({}),
    },
    flowPermission: {
        type: Object,
        default: () => [],
    },
});
//console.log("props==========", JSON.stringify(props.nodeConfig)); 

let defaultText = computed(() => {
    return placeholderList[props.nodeConfig.nodeType]
});
let showText = computed(() => {
    if (props.nodeConfig.nodeType == 1) return $func.arrToStr(props.flowPermission) || '所有人';
    if (props.nodeConfig.nodeType == 4) return $func.setApproverStr(props.nodeConfig);
    if (props.nodeConfig.nodeType == 6) return $func.copyerStr(props.nodeConfig);
});

props.nodeConfig.nodeDisplayName = showText;

let isInputList = ref([]);
let isInput = ref(false);
const resetConditionNodesErr = () => {
  
    for (var i = 0; i < props.nodeConfig.conditionNodes.length; i++) { 
        let conditionTitle= $func.conditionStr(props.nodeConfig, i); 
        props.nodeConfig.conditionNodes[i].error = conditionTitle == "请设置条件" && i != props.nodeConfig.conditionNodes.length - 1; 
        props.nodeConfig.conditionNodes[i].isDefault = 0;   
        props.nodeConfig.conditionNodes[i].nodeDisplayName = conditionTitle=='null' || conditionTitle==''?props.nodeConfig.conditionNodes[i].nodeDisplayName:conditionTitle;
    }
    let maxLen = props.nodeConfig.conditionNodes.length-1;
    let node = props.nodeConfig.conditionNodes[maxLen];
    if(node && node.conditionList.length <= 0){
        node.isDefault = 1;
        node.error = false;
    }
}
onMounted(() => {
    if (props.nodeConfig.nodeType == 4) { 
        props.nodeConfig.error = !$func.setApproverStr(props.nodeConfig);
        console.log("props.nodeConfig.error", props.nodeConfig.error);
    } else if (props.nodeConfig.nodeType == 6) {
        props.nodeConfig.error = !$func.copyerStr(props.nodeConfig);
    } else if (props.nodeConfig.nodeType == 2) {
        resetConditionNodesErr()
    }
});
let emits = defineEmits(["update:flowPermission", "update:nodeConfig"]);
let store = useStore();
let {
    setPromoter,
    setApprover,
    setCopyer,
    setCondition,
    setFlowPermission,
    setApproverConfig,
    setCopyerConfig,
    setConditionsConfig,
} = store;
let isTried = computed(()=> store.isTried)
let flowPermission1 = computed(()=> store.flowPermission1)
let approverConfig1 = computed(()=> store.approverConfig1)
let copyerConfig1 = computed(()=> store.copyerConfig1)
let conditionsConfig1 = computed(()=> store.conditionsConfig1)
 
watch(flowPermission1, (flow) => {
    if (flow.flag && flow.id === _uid) {
        emits("update:flowPermission", flow.value);
    }
});
watch(approverConfig1, (approver) => { 
    if (approver.flag && approver.id === _uid) {
        emits("update:nodeConfig", approver.value);
    }
});
watch(copyerConfig1, (copyer) => {
    if (copyer.flag && copyer.id === _uid) {
        emits("update:nodeConfig", copyer.value);
    }
});
watch(conditionsConfig1, (condition) => {
    if (condition.flag && condition.id === _uid) {
        emits("update:nodeConfig", condition.value);
    }
});

const clickEvent = (index) => {
    if (index || index === 0) {
        isInputList.value[index] = true;
    } else {
        isInput.value = true;
    }
};
const blurEvent = (index) => {
    if (index || index === 0) {
        isInputList.value[index] = false;
        props.nodeConfig.conditionNodes[index].nodeName = props.nodeConfig.conditionNodes[index].nodeName || "条件";
    } else {
        isInput.value = false;
        props.nodeConfig.nodeName = props.nodeConfig.nodeName || defaultText
    }
};
const delNode = () => {
    emits("update:nodeConfig", props.nodeConfig.childNode);
};
const addTerm = () => { 
    let len = props.nodeConfig.conditionNodes.length + 1;
    let n_name='条件' + len;
    props.nodeConfig.conditionNodes.push(NodeUtils.createConditionNode(n_name,null,len,0));
    resetConditionNodesErr()
    emits("update:nodeConfig", props.nodeConfig);
};
const delTerm = (index) => {
    props.nodeConfig.conditionNodes.splice(index, 1);
    props.nodeConfig.conditionNodes.map((item, index) => {
        item.priorityLevel = index + 1;
        item.nodeName = `条件${index + 1}`;
    });
    resetConditionNodesErr()
    emits("update:nodeConfig", props.nodeConfig);
    if (props.nodeConfig.conditionNodes.length == 1) {
        if (props.nodeConfig.childNode) {
            if (props.nodeConfig.conditionNodes[0].childNode) {
                reData(props.nodeConfig.conditionNodes[0].childNode, props.nodeConfig.childNode);
            } else {
                props.nodeConfig.conditionNodes[0].childNode = props.nodeConfig.childNode;
            }
        }
        emits("update:nodeConfig", props.nodeConfig.conditionNodes[0].childNode);
    }
};
const reData = (data, addData) => {
    if (!data.childNode) {
        data.childNode = addData;
    } else {
        reData(data.childNode, addData);
    }
};
const setPerson = (priorityLevel) => {
    var { nodeType } = props.nodeConfig;
    if (nodeType == 1) {
        setPromoter(true);
        setFlowPermission({
            value: props.flowPermission,
            flag: false,
            id: _uid,
        });
    } else if (nodeType == 4) {
        setApprover(true);
        setApproverConfig({
            value: {
                ...JSON.parse(JSON.stringify(props.nodeConfig)),
                ...{ setType: props.nodeConfig.setType ? props.nodeConfig.setType : 1 },
            },
            flag: false,
            id: _uid,
        });
    } else if (nodeType == 6) {
        setCopyer(true);
        setCopyerConfig({
            value: JSON.parse(JSON.stringify(props.nodeConfig)),
            flag: false,
            id: _uid,
        });
    } else {
        setCondition(true);
        setConditionsConfig({
            value: JSON.parse(JSON.stringify(props.nodeConfig)),
            priorityLevel,
            flag: false,
            id: _uid,
        });
    }
};
const arrTransfer = (index, type = 1) => {
    //向左-1,向右1
    props.nodeConfig.conditionNodes[index] = props.nodeConfig.conditionNodes.splice(
        index + type,
        1,
        props.nodeConfig.conditionNodes[index]
    )[0];
    props.nodeConfig.conditionNodes.map((item, index) => {
        item.priorityLevel = index + 1;
    });
    resetConditionNodesErr()
    emits("update:nodeConfig", props.nodeConfig);
};
</script>
<style scoped lang="scss"> 
@import "@/assets/styles/flow/workflow.scss"; 
</style>
<style>  
.error_tip {
    position: absolute;
    top: 0px;
    right: 0px;
    transform: translate(150%, 0px);
    font-size: 24px;
}

.promoter_person .el-dialog__body {
    padding: 10px 20px 14px 20px;
}

.selected_list {
    margin-bottom: 20px;
    line-height: 30px;
}

.selected_list span {
    margin-right: 10px;
    padding: 3px 6px 3px 9px;
    line-height: 12px;
    white-space: nowrap;
    border-radius: 2px;
    border: 1px solid rgba(220, 220, 220, 1);
}

.selected_list img {
    margin-left: 5px;
    width: 7px;
    height: 7px;
    cursor: pointer;
}
</style>