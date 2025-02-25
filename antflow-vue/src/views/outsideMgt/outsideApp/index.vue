<template>
  <div class="app-container">
    <el-form :model="vo" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="业务表单名称" prop="title">
        <el-input v-model="vo.title" placeholder="请输入关键字" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">注册业务表单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Setting" :disabled="single"
          @click="addConditionsTemplate">设置条件</el-button>
      </el-col>
    </el-row>
    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="项目标识" align="center" prop="businessCode" />
      <el-table-column label="项目名称" align="center" prop="businessName" /> 
      <el-table-column label="业务表单标识" align="center" prop="processKey" />
      <el-table-column label="业务表单名称" align="center" prop="title" />
      <el-table-column label="业务表单类型" align="center" prop="applyTypeName" />
      <el-table-column label="创建时间" align="center" prop="createTime">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button link type="primary" icon="View" @click="handleTemplateList(scope.row)">查看条件</el-button>
          <!-- <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button> -->
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="page.page" v-model:limit="page.pageSize"
      @pagination="getList" />

    <!-- 添加或修改委托对话框 -->
    <el-dialog :title="title" v-model="open" width="550px" append-to-body>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="130px" style="margin: 0 20px;">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
            </div>
          </template>
          <el-row>
            <el-col :span="24">
              <el-form-item label="业务方" prop="businessCode">
                <el-select v-model="form.businessCode" placeholder="请选择业务方" :style="{ width: '100%' }">
                  <el-option v-for="(item, index) in partyMarkOptions" :key="index" :label="item.value"
                    :value="item.key"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="业务表单名称" prop="title">
                <el-input v-model="form.title" placeholder="请输入业务表单名称" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="业务表单类型" prop="applyType">
                <el-radio-group v-model="form.applyType">
                  <el-radio value="1" :disabled=true>流程</el-radio>
                  <el-radio value="2" :disabled=true>应用</el-radio>
                  <el-radio value="3" :disabled=true>父级应用</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="子业务表单" prop="isSon">
                <el-radio-group v-model="form.isSon">
                  <el-radio value="1" :disabled=true>是</el-radio>
                  <el-radio value="2" :disabled=true>否</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>

        </el-card>
        <el-card>
          <template #header>
            <div class="card-header">
              <span>人员设置</span>
            </div>
          </template>
          <!-- <el-row>
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
          </el-row> -->

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
          <!-- <el-row>
                <el-col :span="24">
                  <el-form-item label="业务表单URL" prop="applicationUrl">
                    <el-input v-model="form.applicationUrl" placeholder="请输入业务表单URL" />
                  </el-form-item>
                </el-col>
              </el-row> -->
          <!-- <el-row>
                <el-col :span="24">
                  <el-form-item label="icon图(app)" prop="effectiveSource">
                    <ImageUpload :limit="1" :fileSize="3" :fileType="['jpg']"/>
                  </el-form-item>
                </el-col>
              </el-row> -->
        </el-card>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加条件模板对话框 -->
    <el-dialog title="添加条件" v-model="openTemplate" width="550px" append-to-body>
      <el-form :model="templateForm" :rules="templateRules" ref="formTemplateRef" label-width="130px"
        style="margin: 0 20px;">
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

    <TemplateList ref="templateListRef" :visible="templateListVisible" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getApplicationsPageList, addApplication, getApplicationDetail, getPartyMarkKV, setTemplateConf } from "@/api/outsideApi";
import TemplateList from "./template.vue";
import { getDynamicsList } from "@/api/mock";
const { proxy } = getCurrentInstance();
const list = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const openTemplate = ref(false);
const title = ref("");
const appIds = ref([]);
const single = ref(true);
const multiple = ref(true);
const businessPartyName = ref("");
const applicationName = ref("");
const processKey = ref("");

let partyMarkOptions = ref([]);
let templateListVisible = ref(false)
const data = reactive({
  form: {},
  templateForm: {},
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
  },
  templateRules: {
    businessPartyName: [{ required: true, message: '', trigger: 'blur' }],
    applicationName: [{ required: true, message: '', trigger: 'blur' }],
    templateMark: [{ required: true, message: '请输入条件模板ID', trigger: 'blur' }],
    name: [{ required: true, message: '请输入条件模板名称', trigger: 'blur' }]
  }
});
const { page, vo, form, rules, templateForm, templateRules } = toRefs(data);

onMounted(async () => {
  getList();
  getListAA();
})


function getListAA() {
  loading.value = true;
  getPartyMarkKV().then(response => {
    partyMarkOptions.value = response.data;
  }).catch(() => {

  });
}

/** 查询注册业务表单列表 */
function getList() {
  loading.value = true;
  getApplicationsPageList(page.value, vo.value).then(response => {
    list.value = response.data.data;
    total.value = response.data.pagination.totalCount;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  appIds.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;

  processKey.value = selection.map(item => item.processKey);
  businessPartyName.value = selection.map(item => item.businessName);
  applicationName.value = selection.map(item => item.name);
}

/** 新增接入业务方 */
function handleAdd() {
  reset();
  title.value = "注册业务";
  open.value = true;
}
/** 提交表单 */
function submitForm() {
  proxy.$refs["formRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        addApplication(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addApplication(form.value).then(response => {
          if (response.code != 200) {
            proxy.$modal.msgError("注册失败");
            return;
          }
          proxy.$modal.msgSuccess("注册成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 修改按钮操作 */
function handleEdit(row) {
  reset();
  const id = row.id;
  if (id == 1 || id == 2) {
    proxy.$modal.msgError("演示数据不允许修改操作！");
    return;
  }
  getApplicationDetail(id).then(response => {
    form.value = response.data;
    form.value.applyType = form.value.applyType.toString();
    form.value.isSon = form.value.isSon?.toString();
    open.value = true;
    title.value = "编辑业务表单";
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  proxy.$modal.msgError("演示环境不允许删除操作！");
}

/** 添加条件模板 */
function addConditionsTemplate(row) {
  reset();
  const appId = row.id || appIds.value[0];
  templateForm.value.applicationId = appId;
  templateForm.value.businessPartyName = businessPartyName.value[0];
  templateForm.value.applicationName = applicationName.value[0];
  templateForm.value.applicationFormCode = processKey.value[0];
  openTemplate.value = true;
  //console.log(JSON.stringify(templateForm.value)); 
}

/** 提交条件模板表单 */
function submitTemplateForm() {
  proxy.$refs["formTemplateRef"].validate(valid => {
    if (valid) {
      setTemplateConf(templateForm.value).then(response => {
        proxy.$modal.msgSuccess("添加成功");
        openTemplate.value = false;
      });
    }
  });
}
/** 取消操作表单 */
function cancel() {
  open.value = false;
  reset();
}

/** 取消操作添加条件模板表单 */
function cancelTemplate() {
  openTemplate.value = false;
  reset();
}

function handleTemplateList(row) {

  proxy.$refs["templateListRef"].show(row.businessPartyId, row.id);
}

function handleCheckUserUrl() { 
  let url = form.value.userRequestUri;
  if (!url) {
    proxy.$modal.msgError("审批人模板URL不能为空");
    return;
  }
  else {
    const regex = /^https?:\/\//;
    if(!regex.test(url)) {
      proxy.$modal.msgError("请输入正确审批人模板URL");
      return;
    } 
    getDynamicsList(url).then((res) => {
      proxy.$modal.msgSuccess("审批人模板URL链接成功");
    }).catch((res) => {
      proxy.$modal.msgError("请输入正确审批人模板URL");
    });
  }
}
function handleCheckRoleUrl() { 
  let url = form.value.roleRequestUri;
  if (!url) {
    proxy.$modal.msgError("角色模板URL不能为空");
    return;
  }
  else {
    const regex = /^https?:\/\//;
    if(!regex.test(url)) {
      proxy.$modal.msgError("请输入正确角色模板URL");
      return;
    } 
    getDynamicsList(url).then((res) => {
      proxy.$modal.msgSuccess("角色模板URL链接成功");
    }).catch((res) => {
      proxy.$modal.msgError("请输入正确角色模板URL");
    });
  }
}
/** 重置操作表单 */
function reset() {
  form.value = {
    id: undefined,
    businessCode: undefined,
    title: undefined,
    applyType: '2',
    isSon: '2',
    applicationUrl: undefined,
    pcIcon: undefined,
    effectiveSource: undefined,
    userRequestUri: undefined,
    roleRequestUri: undefined,
    remark: undefined
  };
  templateForm.value = {
    businessPartyName: undefined,
    applicationName: undefined,
    businessPartyId: undefined,
    applicationId: undefined,
    applicationFormCode: undefined,
    templateMark: undefined,
    name: undefined,
    remark: undefined
  };
  proxy.resetForm("queryRef");
};

/** 搜索按钮操作 */
function handleQuery() {
  page.value.page = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  vo.value = {};
  proxy.resetForm("queryRef");
  handleQuery();
}

</script>