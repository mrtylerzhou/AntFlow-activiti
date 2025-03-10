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
            <el-select v-model="taskMgmtVO.effectiveStatus" placeholder="状态" clearable style="width: 240px">
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
            <el-tooltip class="box-item" effect="dark" content="低代码表单+流程设计器" placement="bottom">
               <el-button type="success" plain icon="Edit" @click="handleLFDesign">流程设计(LF)</el-button>
            </el-tooltip>
         </el-col>
         <el-col :span="1.5">
            <el-tooltip class="box-item" effect="dark" content="需提前开发业务表单" placement="bottom">
               <el-button type="primary" plain icon="Edit" @click="handleDIYDesign">流程设计(DIY)</el-button>
            </el-tooltip>
         </el-col>
      </el-row>

      <el-table v-loading="loading" :data="configList">
         <el-table-column label="模板类型" align="center" prop="formCode" />
         <el-table-column label="模板名称" align="center" prop="formCodeDisplayName" /> 
         <el-table-column label="流程编号" align="center" prop="bpmnCode" />
         <el-table-column label="流程名称" align="center" prop="bpmnName" />
         <el-table-column label="流程类型" align="center" prop="isLowCodeFlow">
            <template #default="item">
               <el-tooltip v-if="item.row.isLowCodeFlow != 1" content="自定义表单+流程设计器" placement="top">
                  <el-tag type="warning" round>DIY</el-tag>
               </el-tooltip>
               <el-tooltip v-if="item.row.isLowCodeFlow == 1" content="低代码表单+流程设计器" placement="top">
                  <el-tag type="success" round>LF</el-tag>
               </el-tooltip>
            </template>
         </el-table-column>
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
               <el-button link type="primary" @click="handleCopy(scope.row)">复制</el-button>
               <el-button link type="primary" @click="handleDelete(scope.row)">删除</el-button>
            </template>
         </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
         @pagination="getList" />
   </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from "vue";
import { getBpmnConflistPage, getEffectiveBpmn } from "@/api/workflow";
const router = useRouter();
const { proxy } = getCurrentInstance(); 
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
      isOutSideProcess: 0,
      isLowCodeFlow: undefined,
      bpmnCode: undefined,
      bpmnName: undefined
   },
   rules: {
      bpmnCode: [{ required: true, message: "流程编号不能为空", trigger: "blur" }],
      bpmnName: [{ required: true, message: "流程名称不能为空", trigger: "blur" }],
   }
});
const { pageDto, taskMgmtVO } = toRefs(data);
onMounted(async () => {
   loading.value = true; 
   await getList();
   loading.value = false;
}) 
/** 查询列表 */
const getList = async () => {
   await getBpmnConflistPage(pageDto.value, taskMgmtVO.value).then(response => {
      let res = response.data;
      configList.value = res.data;
      total.value = res.pagination.totalCount;
   }).catch((r) => {
      console.log(r);
      proxy.$modal.msgError("加载列表失败:" + r.message);
   });
}
/**
 * 复制操作
 * @param row
 */
const handleCopy = (row) => {
   const params = {
      id: row.id
   };
   let obj = {};
   if (row.isLowCodeFlow == '1') {
      obj = { path: "/workflow/lf-design", query: params };
   } else {
      obj = { path: "/workflow/diy-design", query: params };
   }
   proxy.$tab.openPage(obj);
}
/**
 * 启动流程
 * @param data
 */
const effectiveById = async (data) => {
   await getEffectiveBpmn(data).then(async (res) => {
      if (res.code == 200) {
         getList();
         proxy.$modal.msgSuccess("操作成功");
      } else {
         proxy.$modal.msgError("操作失败");
      }
   });

}
/** 搜索按钮操作 */
function handleQuery() {
   pageDto.value.page = 1;
   getList();
}
/** 跳转到低代码流程设计器 */
function handleLFDesign() {
   router.push({ path: "lf-design" });
}
/** 跳转到自定义流程设计器 */
function handleDIYDesign() {
   router.push({ path: "diy-design" });
}

/** 重置按钮操作 */
function resetQuery() {
   taskMgmtVO.value = {
      isOutSideProcess: 0,
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
   const params = {
      id: row.id
   };
   const obj = { path: "/workflow/preview", query: params };
   proxy.$tab.openPage(obj);
}

</script>
