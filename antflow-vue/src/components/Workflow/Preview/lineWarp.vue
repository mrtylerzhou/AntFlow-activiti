<!--
 * @Date: 2022-09-21 14:41:53
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2023-05-24 15:20:24
 * @FilePath: /src/components/Workflow/Preview/lineWarp.vue
-->
<template>
    <div class="node-wrap" v-if="nodeConfig.nodeType != 7 && nodeConfig.parallelChildNode == 0">
        <div class="node-wrap-box" :class="(nodeConfig.nodeType == 1 ? 'start-node ' : 'active')">
            <div class="title"
                :style="(nodeConfig.isNodeDeduplication == 1 ? `background: rgb(${bgColors[0]});` : `background: rgb(${bgColors[nodeConfig.nodeType]});`)">
                <span>{{ nodeConfig.nodeName }}</span>
            </div>
            <div class="content">
                <div v-html="nodeConfig.nodeDisplayName" class="text"></div>
                <!-- <div class="text">  
                    {{ nodeConfig.nodeDisplayName ?? '未获取到' }}
                </div> -->
            </div>
        </div>  
        <div class="pixel-line"></div> 
        <div class="testtt"></div> 
    </div>
    <!--并行审批分支-->
    <div class="branch-wrap" v-if="nodeConfig.nodeType == 7">
        <div class="branch-box-wrap">
            <div class="branch-box"> 
                <div class="col-box" v-for="(item, index) in nodeConfig.parallelNodes" :key="index">
                    <div class="condition-node">
                        <div class="condition-node-box">
                            <div class="node-wrap-box active">
                                <div class="title" :style="`background: rgb(${bgColors[4]});`">
                                    <span class="iconfont"></span>
                                    <span class="editable-title">{{ item.nodeName }}</span>
                                </div>
                                <div class="content">
                                    <div class="text">
                                        <span class="placeholder" v-if="!item.nodeDisplayName">请选择{{ defaultText }}</span>
                                        {{ item.nodeDisplayName }}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <nodeWrap v-if="item.childNode" v-model:nodeConfig="item.childNode" />
                    <template v-if="index == 0">
                        <div class="top-left-cover-line"></div>
                        <div class="bottom-left-cover-line"></div>
                    </template>
                    <template v-if="index == nodeConfig.parallelNodes.length - 1">
                        <div class="top-right-cover-line"></div>
                        <div class="bottom-right-cover-line"></div>
                    </template>
                </div>
            </div>
            <div class="pixel-line"></div>
        </div>
        <div class="pixel-line"></div>
    </div>
    <LineWarp v-if="nodeConfig.childNode" v-model:nodeConfig="nodeConfig.childNode" />
</template>
<script setup> 
import { bgColors } from '@/utils/flow/const' 
let props = defineProps({
    nodeConfig: {
        type: Object,
        default: () => ({}),
    }
}); 
let nodeConfig = computed(() => {   
    let nameStr= '';
    if(props.nodeConfig.nodeType == 1){
        props.nodeConfig.nodeDisplayName = props.nodeConfig.nodeName;
        return props.nodeConfig;
    }        
    let approvers =props.nodeConfig.property?.emplList; 
    if(!Array.isArray(approvers) || approvers.length == 0){
        return props.nodeConfig;
    }
    for (let item of approvers) {  
        if(item.isDeduplication == 1){ 
            nameStr +='<del><em>'+ item.name +'</em></del>' + '  ';
        }else{
            nameStr += item.name + '  ';
        }  
      }
      props.nodeConfig.nodeDisplayName =nameStr;
    return props.nodeConfig
})
//console.log("nodeConfig==============",JSON.stringify(props.nodeConfig)) 
</script>
<style scoped lang="scss">
@import "@/assets/styles/flow/workflow.scss"; 
.pixel-line {
    width: 2px;
    height: 50px;
    background-color: #cacaca; 
}
.end-node-circle {
    width: 20px;
    height: 20px;
    margin: auto;
    border-radius: 50%;
    background: #dbdcdc
}
.line-through {
    text-decoration: line-through
}
.testtt { 
    width: 2px; 
    border-style: solid;
    border-width: 8px 6px 4px;
    border-color: #cacaca transparent transparent;
    background: #f5f5f7;
}
</style>