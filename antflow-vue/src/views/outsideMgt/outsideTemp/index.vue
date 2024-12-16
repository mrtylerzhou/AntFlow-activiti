<template>
  <div class="app-container">
     <el-form :model="taskMgmtVO" ref="queryRef" :inline="true" v-show="showSearch">
        <el-form-item label="流程编号" prop="bpmnCode">
           <el-input v-model="taskMgmtVO.bpmnCode" placeholder="请输入关键字" clearable style="width: 200px"
              @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="流程名称" prop="bpmnName">
           <el-input v-model="taskMgmtVO.bpmnName" placeholder="请输入关键字" clearable style="width: 200px"
              @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="effectiveStatus">
            <el-select
               v-model="taskMgmtVO.effectiveStatus"
               placeholder="状态"
               clearable
               style="width: 240px"
            >        
            <el-option label="禁用" value="0" />
            <el-option label="启动" value="1" /> 
            </el-select>
         </el-form-item>
        <el-form-item>
           <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
           <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
     </el-form>

     <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
           <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
        </el-col>
        <el-col :span="1.5">
           <el-button type="success" plain icon="Edit"  @click="handleUpdate">修改</el-button>
        </el-col>
        <el-col :span="1.5">
           <el-button type="danger" plain icon="Delete"  @click="handleDelete">删除</el-button>
        </el-col>
     </el-row>

     <el-table v-loading="loading" :data="configList">
      <el-table-column label="业务方名称" align="center" prop="businessPartyId">
           <template #default="item">
              {{ getPartyMarkName(item.row.businessPartyId) }}
           </template>
        </el-table-column>
        <el-table-column label="模板类型" align="center" prop="formCode" />
        <el-table-column label="模板名称" align="center" prop="formCodeName">
           <template #default="item">
              {{ getFromCodeName(item.row.formCode) }}
           </template>
        </el-table-column>
        <el-table-column label="流程编号" align="center" prop="bpmnCode" />
        <el-table-column label="流程名称" align="center" prop="bpmnName" />
        <el-table-column label="是否去重" align="center" prop="deduplicationType">
           <template #default="item">
              {{ item.row.deduplicationType == 1 ? '否' : '是' }}
           </template>
        </el-table-column>

        <el-table-column label="状态" align="center" prop="effectiveStatus">
           <template #default="item">
              <el-tag>{{ item.row.effectiveStatus == 1 ? '活跃' : '不活跃' }}</el-tag> 
           </template>
        </el-table-column>

        <el-table-column label="修改时间" align="center" prop="updateTime" width="180">
           <template #default="scope">
              <span>{{ parseTime(scope.row.updateTime) }}</span>
           </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" class-name="small-padding fixed-width">
           <template #default="scope">
              <el-button v-if="scope.row.effectiveStatus == 1" type="info" disabled link>启动</el-button>
              <el-button v-else type="success" link @click="effectiveById(scope.row)">启动</el-button>
              <el-button link type="primary" @click="handlePreview(scope.row)">预览</el-button>
              <el-button link type="primary" @click="handleEdit(scope.row)">复制</el-button> 
              <el-button link type="primary"  @click="handleDelete(scope.row)">删除</el-button>
           </template>
        </el-table-column>
     </el-table>

     <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
        @pagination="getList" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from 'element-plus';
import { getBpmnConflistPage,getEffectiveBpmn } from "@/api/mockflow";
import { getApplicationsPageList , getBusinessPartyList } from "@/api/mockoutside";
const router = useRouter();
const { proxy } = getCurrentInstance();
let formCodeOptions = ref([]);
let partyMarkOptions = ref([]);
const configList = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const total = ref(0);
const data = reactive({
  form: {},
  pageDto: {
     page: 1,
     pageSize: 10
  },
  taskMgmtVO: { 
     effectiveStatus: undefined,
     isOutSideProcess : 1,
     bpmnCode: undefined,
     bpmnName: undefined
  },
  rules: {
     bpmnCode: [{ required: true, message: "流程编号不能为空", trigger: "blur" }],
     bpmnName: [{ required: true, message: "流程名称不能为空", trigger: "blur" }],
  }
});
const { pageDto, taskMgmtVO } = toRefs(data);
onMounted(async() => {
  await initFromCode();
  getList(); 
})
const initFromCode = async () => {
 await getApplicationsPageList().then((res) => {
     if (res.code == 200) {
        formCodeOptions.value = res.data.data;
     }
  });

  await getBusinessPartyList().then((res) => {
     if (res.code == 200) {
      partyMarkOptions.value = res.data.data;
     }
  });
}
/**获取三方注册应用名称 */
const getFromCodeName = (formCode) => { 
 const result= formCodeOptions.value.filter(item => item.processKey == formCode)[0]; 
 return result?.title;
}
/**获取业务方名称 */
const getPartyMarkName = (id) => { 
 const result= partyMarkOptions.value.filter(item => item.id == id)[0]; 
 return result?.name;
}
/** 查询列表 */
function getList() {
  loading.value = true;
  getBpmnConflistPage(pageDto.value,taskMgmtVO.value).then(response => {
     let res = response.data;
     configList.value = res.data;
     total.value = res.pagination.totalCount;
     loading.value = false;
  }); 
}
/**流程复制&编辑 */
const handleEdit =  (row) => {
  const params ={
     id: row.id
  };
  const obj = {path: "/outsideMgt/outsideDesign",query:params};
  proxy.$tab.openPage(obj);
}
/**流程启用 */
const effectiveById = async (data) => {
   await getEffectiveBpmn(data).then(async (res) => {
       if (res.code == 200) {
           getList();
           ElMessage.success("操作成功");
       } else {
           ElMessage.error("操作失败");
       }
   });

}
/** 搜索按钮操作 */
function handleQuery() {
  pageDto.value.page = 1;
  getList();
}

function handleAdd() {
  router.push({ path: "/outsideMgt/outsideDesign"});
}

function handleUpdate() {
  proxy.$modal.msgError("演示环境不允许删除操作！");
}

/** 重置按钮操作 */
function resetQuery() {
  taskMgmtVO.value = {
     isOutSideProcess : 1,
     bpmnCode: undefined,
     bpmnName: undefined
 };
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 删除按钮操作 */
function handleDelete(row) {
  proxy.$modal.msgError("演示环境不允许删除操作！");
}
/** 预览 */
function handlePreview(row) { 
  const params ={
     id: row.id
  };
  const obj = {path: "/outsideMgt/preview",query:params};
  proxy.$tab.openPage(obj);
}

</script>
