<template>
  <div class="app-container">
    <div class="query-box">
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
          <el-button type="warning" icon="Setting" :disabled="single"
            @click="approveTempVisible = true">设置审批人</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" icon="Setting" :disabled="single"
            @click="conditionTempVisible = true">设置条件</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="primary" icon="Setting" :disabled="single"
            @click="callbackConfVisible = true">设置流程回调</el-button>
        </el-col>
      </el-row>
    </div>
    <div class="table-box">
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column type="selection" align="center" width="35" />
        <el-table-column label="项目标识" align="center" prop="businessCode" width="150" />
        <el-table-column label="项目名称" align="center" prop="businessName" width="220" />
        <el-table-column align="center" prop="processKey" width="280">
          <template #header>
            <span>
              应用标识
              <el-tooltip content="唯一标识即：FormCode" placement="top">
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
            </span>
          </template>
        </el-table-column>
        <el-table-column label="应用名称" align="center" prop="name" width="220" />
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
    </div> 

    <conditionForm v-model:visible="conditionTempVisible" v-model:bizformData="bizAppForm" />
    <approveForm v-model:visible="approveTempVisible" v-model:bizformData="bizAppForm" />
    <conditionTemplateList ref="conditionListRef" v-model:visible="conditionListVisible" />
    <approveTemplateList ref="approveListRef" v-model:visible="approveListVisible" />
    <callbackConf v-model:visible="callbackConfVisible" v-model:bizformData="bizAppForm" /> 
    <addform v-model:visible="open" v-model:appformData="appForm" @refresh="getList" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import {
  getApplicationsPageList, 
  getApplicationDetail,
  getPartyMarkKV,
  getConditionTemplatelist,
  getApproveTemplatelist
} from "@/api/outsideApi";
import conditionForm from "./condition/form.vue";
import conditionTemplateList from "./condition/list.vue";
import approveForm from "./approve/form.vue";
import approveTemplateList from "./approve/list.vue";
import callbackConf from "./callbackConf/form.vue";
import addform from "./form.vue";
const { proxy } = getCurrentInstance();
const list = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const conditionTempVisible = ref(false);
const approveTempVisible = ref(false);
const callbackConfVisible = ref(false);
const title = ref("");
const single = ref(true);
const multiple = ref(true);

let partyMarkOptions = ref([]);
let conditionListVisible = ref(false);
let approveListVisible = ref(false);
const data = reactive({ 
  appForm: {},
  page: {
    page: 1,
    pageSize: 10
  },
  vo: {
    title: undefined
  }
});
const { page, vo, appForm } = toRefs(data);

let bizAppForm = reactive({
  businessPartyId: undefined,
  applicationId: undefined,
  formCode: undefined,
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
  bizAppForm.businessPartyId = selection.map(item => item.businessPartyId)[0];
  bizAppForm.applicationId = selection.map(item => item.id)[0];
  bizAppForm.businessPartyName = selection.map(item => item.businessName)[0];
  bizAppForm.applicationName = selection.map(item => item.name)[0];
  bizAppForm.formCode = selection.map(item => item.processKey)[0];
}
 
/** 修改按钮操作 */
function handleEdit(row) { 
  const id = row.id;
  if (id == 1 || id == 2) {
    proxy.$modal.msgError("演示数据不允许修改操作！");
    return;
  }
  proxy.$modal.loading();
  getApplicationDetail(id).then(response => {
    appForm.value = response.data;
    appForm.value.applyType = appForm.value.applyType.toString();
    appForm.value.isSon = appForm.value.isSon?.toString();
    appForm.value.businessName = row.businessName;
    open.value = true;
    title.value = "编辑应用";
  });
  proxy.$modal.closeLoading();
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
  const resultCheckApprove = await checkApproveConfig(row);
  const resultCheckCondition = true;//await checkConditionConfig(row); 
  if (!resultCheckApprove) {
    proxy.$modal.closeLoading();
    proxy.$modal.msgError("请先设置审批人");
    return;
  }
  if (!resultCheckCondition) {
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
const checkApproveConfig = async (row) => {
  return await getApproveTemplatelist(row.id).then(response => {
    if (response.code == 200) {
      return response.data.length > 0
    } else {
      return false;
    }
  }).catch(err => {
    return false;
  });
}
const checkConditionConfig = async (row) => {
  return await getConditionTemplatelist(row.id).then(response => {
    if (response.code == 200) {
      return response.data.length > 0
    } else {
      return false;
    }
  }).catch(err => {
    return false;
  });
}
</script>