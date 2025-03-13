<template>
  <div class="app-container">
      <div class="fd-nav">
          <div class="fd-nav-left"> 
              <div class="fd-nav-title">{{ title }}</div>
          </div>

          <div class="fd-nav-center">
              <div class="step-tab">
                  <div v-for="(item, index) in steps" :key="index" class="step"
                      :class="[activeStep == item.key ? 'active' : '']" @click="changeSteps(item)">
                      <span class="step-index">{{ index + 1 }}</span>
                      {{ item.label }}
                  </div>
              </div>
          </div>
          <div class="fd-nav-right">
              <button type="button" class="fd-btn button-publish" @click="publish">
                  <span>发 布</span>
              </button>
          </div>
      </div>
      <div v-if="processConfig" v-show="activeStep === 'basicSetting'">
          <BasicSetting ref="basicSetting" :basicData="processConfig" @nextChange="changeSteps" />
      </div>
      <div v-if="nodeConfig" v-show="activeStep === 'processDesign'">
          <Process ref="processDesign" :processData="nodeConfig" @nextChange="changeSteps" />
      </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from 'element-plus';
import { useRoute } from 'vue-router';
import BasicSetting from "@/components/OutsideFlow/BasicSetting/index.vue";
import Process from "@/components/OutsideFlow/Process/index.vue";  
import { getApiWorkFlowData, setApiWorkFlowData } from '@/api/outsideApi'; 
import { FormatUtils } from '@/utils/flow/formatcommit_data'; 
import { FormatDisplayUtils } from '@/utils/flow/formatdisplay_data'; 
import { NodeUtils } from '@/utils/flow/nodeUtils';
const { proxy } = getCurrentInstance()
const route = useRoute();

const basicSetting = ref(null);
const processDesign = ref(null);

let activeStep = ref("basicSetting"); // 激活的步骤面板

let steps = ref([
 { label: "基础设置", key: "basicSetting" },
 { label: "流程设计", key: "processDesign" },
]);

const changeSteps = (item) => {
 activeStep.value = item.key;
};

let processConfig = ref(null);
let nodeConfig = ref(null);
let title = ref('');


onMounted(async () => {  
 let mockjson = {};
 proxy.$modal.loading();
 if (route.query.id && route.query.id != 0) { 
     mockjson = await getApiWorkFlowData({ id: route.query.id });
 } else { 
     mockjson = NodeUtils.createStartNode();
 }
 let data = FormatDisplayUtils.getToTree(mockjson.data);
 // console.log("old===data=nodes==========", JSON.stringify(data.nodes)); 
 proxy.$modal.closeLoading();

 processConfig.value = data;
 title.value = data?.bpmnName;
 nodeConfig.value = data?.nodeConfig;  
});


const publish = () => { 
  const step1 = basicSetting.value.getData();
  const step2 = processDesign.value.getData(); 
  Promise.all([step1, step2])
      .then((res) => { 
          proxy.$modal.loading(); 
          ElMessage.success("设置成功,F12控制台查看数据");
          let basicData = res[0].formData;
          var nodes = FormatUtils.formatSettings(res[1].formData);
          Object.assign(basicData, { nodes: nodes });
          return basicData;
      })
      .then((data) => {       
          console.log("提交到API=====data=", JSON.stringify(data));  
          //proxy.$modal.closeLoading();
           setApiWorkFlowData(data).then((resLog) => {
               proxy.$modal.closeLoading();
               if (resLog.code == 200) { 
                   ElMessage.success("设置成功,F12控制台查看数据");
                   const obj = { path: "/outsideMgt/outsideTemp" };
                   proxy.$tab.openPage(obj);
               } else { 
                   ElMessage.error("提交到API返回失败" + JSON.stringify(resLog.errMsg));
               }
           });
      })
      .catch((err) => {  
          proxy.$modal.closeLoading();
          if (err){
            console.log("设置失败" + JSON.stringify(err));
            proxy.$modal.msgError("至少配置一个有效审批人节点");
          } 
      }).finally(() => {
          //proxy.$modal.closeLoading();
      });
};

</script>
<style scoped lang="scss">
@import "@/assets/styles/flow/workflow.scss";

.app-container{
  position: relative;
  background-color: #f5f5f7;
  min-height: calc(100vh - 84px); 
  padding-top: 15px;
  overflow: auto;
}

.step-tab {
 display: flex;
 justify-content: center;
 position: relative;
 height: 60px;
 font-size: 14px;
 border-right: 0px solid #1583f2;
 text-align: center;
 cursor: pointer
}
.fd-nav{ 
 background: #00CED1;
}

 
.fd-nav .step {
 width: 140px;
 line-height: 100%;
 padding-left: 30px;
 padding-right: 30px;
 line-height: 60px;
 cursor: pointer;
 position: relative;
}

.fd-nav .step:hover {
 background: #20B2AA;
}

.fd-nav .step:active {
 background: #20B2AA;
}

.fd-nav .active {
 background: #20B2AA;
}

.fd-nav .step-index {
 display: inline-block;
 width: 18px;
 height: 18px;
 border: 1px solid #fff;
 border-radius: 8px;
 line-height: 18px;
 text-align: center;
 box-sizing: border-box;
}
</style>