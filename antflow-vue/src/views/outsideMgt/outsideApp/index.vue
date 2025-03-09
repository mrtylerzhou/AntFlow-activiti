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
        <el-button type="primary"  icon="Plus" @click="handleAdd">注册业务表单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning"  icon="Setting" :disabled="single"
          @click="openApproveTemplate = true">设置审批人</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success"  icon="Setting" :disabled="single"
          @click="addConditionsTemplate">设置条件</el-button>
      </el-col>
    </el-row>
    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="项目标识" align="center" prop="businessCode" />
      <el-table-column label="项目名称" align="center" prop="businessName" />
      <el-table-column align="center" prop="processKey">
        <template #header>
          <span>
            业务表单标识
            <el-tooltip content="唯一标识即：FormCode" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </span>
        </template>
      </el-table-column>
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
 
    <TemplateList ref="templateListRef" :visible="templateListVisible" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getApplicationsPageList, addApplication, getApplicationDetail, getPartyMarkKV } from "@/api/outsideApi";
import TemplateList from "./template.vue";
import { getDynamicsList } from "@/api/mock";
const { proxy } = getCurrentInstance();
const list = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const openTemplate = ref(false);
const openApproveTemplate = ref(false);
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
  page: {
    page: 1,
    pageSize: 10
  },
  vo: {} 
});
const { page, vo, form } = toRefs(data);

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


/** 取消操作表单 */
function cancel() {
  open.value = false;
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
    if (!regex.test(url)) {
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
    if (!regex.test(url)) {
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