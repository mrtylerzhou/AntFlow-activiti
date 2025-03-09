<template> 
    <!-- 添加条件模板对话框 -->
    <el-dialog title="添加条件" v-model="openTemplate" width="550px" append-to-body>
      <el-form :model="templateForm" :rules="templateRules" ref="conditionTemplateRef" label-width="130px"
        label-position="top" style="margin: 0 20px;">
        <el-row>
          <el-col :span="24">
            <el-form-item label="业务方名称" prop="businessPartyName">
              <el-input v-model="templateForm.businessPartyName" :disabled=true placeholder="请输入业务方名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="业务表单名称" prop="applicationName">
              <el-input v-model="templateForm.applicationName" :disabled=true placeholder="请输入业务表单名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="条件模板ID" prop="templateMark">
              <el-input v-model="templateForm.templateMark" placeholder="请输入条件模板唯一标识" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="条件模板名称" prop="name">
              <el-input v-model="templateForm.name" placeholder="请输入条件模板名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="templateForm.remark" type="textarea" placeholder="请输入内容"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitTemplateForm">确 定</el-button>
          <el-button @click="cancelTemplate">取 消</el-button>
        </div>
      </template>
    </el-dialog>

</template>
<script setup>
import { ref, onMounted } from "vue";
import { getApproveTemplateDetail,setConditionTemplate } from "@/api/outsideApi";
let openTemplate = ref(false);
const data = reactive({
  templateForm: {},  
  templateRules: {
    businessPartyName: [{ required: true, message: '', trigger: 'blur' }],
    applicationName: [{ required: true, message: '', trigger: 'blur' }],
    templateMark: [{ required: true, message: '请输入条件模板ID', trigger: 'blur' }],
    name: [{ required: true, message: '请输入条件模板名称', trigger: 'blur' }]
  }
});
const { templateForm, templateRules } = toRefs(data);

/** 提交条件模板表单 */
function submitTemplateForm() {
  proxy.$refs["conditionTemplateRef"].validate(valid => {
    if (valid) {
      setConditionTemplate(templateForm.value).then(response => {
        proxy.$modal.msgSuccess("添加成功");
        openTemplate.value = false;
      });
    }
  });
}
/** 取消操作添加条件模板表单 */
function cancelTemplate() {
  openTemplate.value = false;
  reset();
}
</script>