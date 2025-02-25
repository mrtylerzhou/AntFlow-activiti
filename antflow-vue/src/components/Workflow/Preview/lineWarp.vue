<!--
 * @Date: 2022-09-21 14:41:53
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2023-05-24 15:20:24
 * @FilePath: /src/components/Workflow/Preview/lineWarp.vue
-->
<template>
    <div class="node-wrap">
        <div class="node-wrap-box" :class="(nodeConfig.nodeType == 1 ? 'start-node ' : 'active')">
            <div class="title" :style="(nodeConfig.isNodeDeduplication == 1 ? `background: rgb(${bgColors[0]});` : `background: rgb(${bgColors[nodeConfig.nodeType]});`)">
                <span>{{ nodeConfig.nodeName }}</span>
            </div>
            <div class="content" @click="setPerson">
                <div v-html="nodeConfig.nodeDisplayName" class="text"></div>
                <!-- <div class="text">  
                    {{ nodeConfig.nodeDisplayName ?? '未获取到' }}
                </div> -->
            </div>
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
    height: 30px;
    background-color: black;
    transform: scaleX(0.001);
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
</style>