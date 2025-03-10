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
        <el-button type="primary" icon="Plus" @click="handleAdd">注册业务表单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" icon="Setting" :disabled="single" @click="approveTempVisible = true">设置审批人</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" icon="Setting" :disabled="single"
          @click="conditionTempVisible = true">设置条件</el-button>
      </el-col>
    </el-row>
    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" align="center" width="35" />
      <el-table-column label="项目标识" align="center" prop="businessCode" width="150" />
      <el-table-column label="项目名称" align="center" prop="businessName" width="220" />
      <el-table-column align="center" prop="processKey" width="280">
        <template #header>
          <span>
            业务标识
            <el-tooltip content="唯一标识即：FormCode" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="业务名称" align="center" prop="name" width="220" />
      <el-table-column label="业务类型" align="center" prop="applyTypeName" width="120" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="150">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button link type="primary" icon="View" @click="viewConditionList(scope.row)">条件</el-button>
          <el-button link type="primary" icon="View" @click="viewApproveList(scope.row)">审批人</el-button>
          <el-button link type="primary" icon="Promotion" @click="handleFlowDesign(scope.row)">设计流程</el-button>
          <!-- <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button> -->
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="page.page" v-model:limit="page.pageSize"
      @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" v-model="open" width="550px" append-to-body>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="130px" label-position="top"
        style="margin: 0 20px;">
        <el-row>
          <el-col :span="24">
            <el-form-item label="业务方" prop="businessCode">
              <el-select filterable v-model="form.businessCode" placeholder="请选择业务方" :style="{ width: '100%' }">
                <el-option v-for="(item, index) in partyMarkOptions" :key="index" :label="item.value" :value="item.key">
                  <span style="float: left">【{{ item.key }}】 {{ item.value }}</span>
                </el-option>
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
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <conditionForm v-model:visible="conditionTempVisible" v-model:bizformData="bizAppForm" />
    <approveForm v-model:visible="approveTempVisible" v-model:bizformData="bizAppForm" />
    <conditionTemplateList ref="conditionListRef" v-model:visible="conditionListVisible" />
    <approveTemplateList ref="approveListRef" v-model:visible="approveListVisible" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { 
  getApplicationsPageList, 
  addApplication, 
  getApplicationDetail, 
  getPartyMarkKV, 
  getConditionTemplatelist, 
  getApproveTemplatelist } from "@/api/outsideApi";
import conditionForm from "./condition/form.vue";
import conditionTemplateList from "./condition/list.vue";
import approveForm from "./approve/form.vue";
import approveTemplateList from "./approve/list.vue";
const { proxy } = getCurrentInstance();
const list = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const conditionTempVisible = ref(false);
const approveTempVisible = ref(false);

const title = ref("");
const single = ref(true);
const multiple = ref(true);

let partyMarkOptions = ref([]);
let conditionListVisible = ref(false);
let approveListVisible = ref(false);
const data = reactive({
  form: {
    title: undefined,
    businessCode: undefined,
    applyType: '2',
    isSon: '1',
  },
  page: {
    page: 1,
    pageSize: 10
  },
  vo: {
    title: undefined
  },
  rules: {
    businessCode: [{ required: true, message: '请选择业务方', trigger: 'blur' }],
    title: [{ required: true, message: '请填写业务表单名称', trigger: 'blur' }],
  }
});
const { page, vo, form, rules } = toRefs(data);

let bizAppForm = reactive({
  businessPartyId: undefined,
  applicationId: undefined,
  businessPartyName: undefined,
  applicationName: undefined,
});

onMounted(async () => {
  getList();
  getPartyMarkList();
})

/** 获取业务方标识 */
function getPartyMarkList() {
  getPartyMarkKV().then(response => {
    partyMarkOptions.value = response.data;
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
  single.value = selection.length != 1;
  multiple.value = !selection.length;
  bizAppForm.businessPartyId = selection.map(item => item.businessPartyId) ?? [0];
  bizAppForm.applicationId = selection.map(item => item.id) ?? [0];
  bizAppForm.businessPartyName = selection.map(item => item.businessName) ?? [0];
  bizAppForm.applicationName = selection.map(item => item.name) ?? [0];
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
      proxy.$modal.loading();
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
      proxy.$modal.closeLoading();
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
  proxy.$modal.loading();
  getApplicationDetail(id).then(response => {
    form.value = response.data;
    form.value.applyType = form.value.applyType.toString();
    form.value.isSon = form.value.isSon?.toString();
    open.value = true;
    title.value = "编辑业务表单";
  });
  proxy.$modal.closeLoading();
}
/** 取消操作表单 */
function cancel() {
  open.value = false;
  reset();
}
function reset() {
  form.value = {
    title: undefined,
    businessCode: undefined,
    applyType: '2',
    isSon: '1',
  };
}
/** 重置按钮操作 */
function resetQuery() {
  vo.value = {};
  proxy.resetForm("queryRef");
  handleQuery();
}
/** 搜索按钮操作 */
function handleQuery() {
  page.value.page = 1;
  getList();
}
/** 删除按钮操作 */
function handleDelete(row) {
  proxy.$modal.msgError("演示环境不允许删除操作！");
}
/** 查看条件列表 */
function viewConditionList(row) {
  proxy.$refs["conditionListRef"].show(row.businessPartyId, row.id);
}
/** 查看条件列表 */
function viewApproveList(row) {
  proxy.$refs["approveListRef"].show(row.businessPartyId, row.id);
}

async function handleFlowDesign(row) {
  proxy.$modal.loading();
  const resultCheckApprove =await checkApproveConfig(row);
  const resultCheckCondition =await checkConditionConfig(row); 
  if(!resultCheckApprove){
    proxy.$modal.closeLoading();
    proxy.$modal.msgError("请先设置审批人");
    return;
  }
  if(!resultCheckCondition){
    proxy.$modal.closeLoading();
    proxy.$modal.msgError("至少设置一个条件");
    return;
  }  
  const param = {
    bizid: row.businessPartyId,
    bizname: encodeURIComponent(row.businessName),
    bizcode: row.businessCode,
    appid: row.id,
    fc: row.processKey,
    fcname: encodeURIComponent(row.name),
  };
  proxy.$modal.closeLoading();
  const obj = { path: "/outsideMgt/outsideDesign", query: param };
  proxy.$tab.openPage(obj);
}  
const checkApproveConfig =async (row)=> {
 return await getApproveTemplatelist(row.id).then(response => {
    if(response.code == 200){ 
      return response.data.length > 0
    }else{
      return false;
    }
  }).catch(err => {
    return false;
  });
} 
const checkConditionConfig =async (row)=> {
  return await getConditionTemplatelist(row.id).then(response => {
    if(response.code == 200){
      return response.data.length > 0
    }else{
      return false;
    }
  }).catch(err => {
    return false;
  });
}
</script>