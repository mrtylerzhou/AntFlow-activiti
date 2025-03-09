<template>
    <!-- 添加审批人模板对话框 -->
    <el-dialog title="审批人配置" v-model="Visible" width="550px" append-to-body>
      <el-form :model="form" :rules="templateRules" ref="approveTemplateRef" label-width="130px" style="margin: 0 20px;">
        <el-row>
          <el-col :span="24">
            <el-form-item label="业务方名称" prop="businessPartyName">
              <el-input v-model="form.businessPartyName" :disabled=true placeholder="请输入业务方名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="业务表单名称" prop="applicationName">
              <el-input v-model="form.applicationName" :disabled=true placeholder="请输入业务表单名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="业务表单ID" prop="clientId">
              <el-input v-model="form.clientId" placeholder="请输入业务表单唯一标识" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="业务表单密钥" prop="clientSecret">
              <el-input v-model="form.clientSecret" placeholder="请输入业务表单密钥" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="审批人模板URL" prop="userRequestUri">
              <el-input v-model="form.userRequestUri" placeholder="请输入审批人模板URL(必须http或https开头)">
                <template #append>
                  <el-tooltip class="box-item" effect="dark" content="检查审批人模板URL是否连通" placement="bottom-end">
                    <el-button @click="handleCheckUserUrl">
                      <el-icon>
                        <CircleCheck />
                      </el-icon>
                    </el-button>
                  </el-tooltip>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="角色模板URL" prop="roleRequestUri">
              <el-input v-model="form.roleRequestUri" placeholder="请输入审批角色模板URL(必须http或https开头)">
                <template #append>
                  <el-tooltip class="box-item" effect="dark" content="检查角色模板URL是否连通" placement="bottom-end">
                    <el-button @click="handleCheckRoleUrl">
                      <el-icon>
                        <CircleCheck />
                      </el-icon>
                    </el-button>
                  </el-tooltip>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
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
import { getApproveTemplateDetail,setApproveTemplate } from "@/api/outsideApi";
let Visible = ref(false)
const data = reactive({
  form: {}, 
  page: {
    page: 1,
    pageSize: 10
  },
  vo: {},
  rules: {
    businessCode: [{ required: true, message: '请选择业务方', trigger: 'change' }],
    title: [{ required: true, message: '请输入业务表单名称', trigger: 'blur' }],
    applyType: [{ required: true, message: '', trigger: 'change' }],
    clientId: [{ required: true, pattern: /^[^\u4e00-\u9fff]+$/, message: '请输入业务表单唯一标识(不能输入中文)', trigger: 'blur' }],
    clientSecret: [{ required: true, pattern: /^[^\u4e00-\u9fff]+$/, message: '请输入业务表单密钥(不能输入中文)', trigger: 'blur' }],
    userRequestUri: [
      {
        required: true,
        pattern: /^https?:\/\//,
        message: '请输入正确的URL',
        trigger: 'blur'
      }],
    roleRequestUri: [{ required: false, pattern: /^https?:\/\//, message: '请输入正确的URL', trigger: 'blur' }]
  }
});
const { page, vo, form, rules } = toRefs(data);

</script>