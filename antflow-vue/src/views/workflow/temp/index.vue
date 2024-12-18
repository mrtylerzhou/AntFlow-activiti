<template>
    <div class="app-container">
        <el-form :model="vo" ref="queryRef" :inline="true" v-show="showSearch">
            <el-form-item label="关键字" prop="key">
                <el-input v-model="vo.关键字" placeholder="请输入关键字" clearable style="width: 200px"
                    @keyup.enter="handleQuery" />
            </el-form-item>

            <el-form-item>
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
        </el-form>
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" plain icon="CirclePlus" @click="handleDIYTemp">模板类型(DIY)</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="success" plain icon="CirclePlus" @click="createLFTemp">模板类型(LF)</el-button>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="list">
            <el-table-column label="模板标识" align="center" prop="key" v-if="columns[0].visible"
                :show-overflow-tooltip="true" />
            <el-table-column label="模板名称" align="center" prop="value" v-if="columns[1].visible"
                :show-overflow-tooltip="true" />
            <el-table-column label="模板类型" align="center" prop="type" v-if="columns[2].visible"
                :show-overflow-tooltip="true">
                <template #default="item">
                    <el-tag v-if="item.row.type === 'LF'" type="success">{{ item.row.type }}</el-tag>
                    <el-tag v-else type="warning">{{ item.row.type }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="备注" align="center" prop="remark" v-if="columns[3].visible"
                :show-overflow-tooltip="true" />
            <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[4].visible">
                <template #default="scope">
                    <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="320" align="center" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-button link type="primary" icon="ZoomIn" @click="handleLFTemp(scope.row)">查看表单</el-button>
                    <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
                    <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 添加模板类型 -->
        <el-dialog :title="title" v-model="openForm" append-to-body>
            <el-form :model="form" :rules="rules" ref="formRef" label-width="130px" style="margin: 0 20px;">
          
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="模板标识" prop="key">
                        <el-input v-model="form.key" placeholder="请输入模板唯一标识" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="模板名称" prop="value">
                        <el-input v-model="form.value" placeholder="请输入模板名称" />
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
                    <el-button @click="closeDialog">关 闭</el-button>
                    <el-button type="primary" @click="submitForm">确 定</el-button>                   
                </div>
            </template>
        </el-dialog>

        <!-- 查看表单 -->
        <el-dialog :title="title" v-model="open" append-to-body>
            <div class="component"> 
                <component v-if="componentLoaded" :is="loadedComponent" :lfFormData="lfFormDataConfig"
                    :isPreview="true">
                </component>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="closeDialog">关 闭</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getAllFormCodes } from "@/api/workflow";
import { getLowCodeFromCodeData,createLFFormCode } from '@/api/lowcodeApi'
import { loadDIYComponent, loadLFComponent } from '@/views/workflow/components/componentload.js';
const { proxy } = getCurrentInstance();
const list = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const open = ref(false);
const openForm = ref(false); 
const title = ref("");

let lfFormDataConfig = ref(null)
let loadedComponent = ref(null)
let componentLoaded = ref(null)

const data = reactive({
    form: {},
    vo: {},
    rules: {
        key: [{ 
            required: true, 
            pattern: /^[A-Z]+$/,
            message: '请输入模板唯一标识(只能是大写字母)', 
            trigger: ['blur', 'change'] 
        }],
        value: [{ 
            required: true, 
            pattern: /[\u4e00-\u9fa5]+/,
            message: '请输入模板名称(必须包含汉字)', 
            trigger: ['blur', 'change'] 
        }], 
    }
});
const { vo, form,rules } = toRefs(data);
onMounted(async () => {
    getList();
})

// 列显隐信息
const columns = ref([
    { key: 0, label: `模板标识`, visible: true },
    { key: 1, label: `模板名称`, visible: true },
    { key: 2, label: `模板类型`, visible: true },
    { key: 3, label: `备注`, visible: true },
    { key: 4, label: `创建时间`, visible: false }
]);

/** 查询接入业务方列表 */
function getList() {
    loading.value = true;
    getAllFormCodes().then(response => {
        list.value = response.data;
        loading.value = false;
    }).catch(() => {
        loading.value = false;
    });
}

/** 添加自定义业务表单FromCode */
function handleDIYTemp() {
    proxy.$modal.msgSuccess("后端添加流程适配以后自动查询出来");
}
/** 添加低代码业务表单FromCode */
function createLFTemp() {
    reset();
    title.value = "添加模板";
    openForm.value = true;  
 
}
/** 提交表单 */
function submitForm() {  
    proxy.$refs["formRef"].validate(valid => {
    if (valid) { 
        createLFFormCode(form.value).then(response => {
          if (response.code != 200) {
            proxy.$modal.msgError("新增失败");
            return;
          }
          proxy.$modal.msgSuccess("新增成功");
          openForm.value = false;
          getList();
        }); 
    }
  });
}

 
/** 查看表单操作 */
const handleLFTemp = async (row) => {
    loadedComponent.value =null;
    title.value = "查看表单";
    proxy.$modal.loading();
    if (row.type == 'LF') {//低代码表单
        await getLowCodeFromCodeData(row.key).then(async (res) => { 
            if (res.code == 200) {
                lfFormDataConfig.value = res.data
                loadedComponent.value = await loadLFComponent();
                componentLoaded.value = true;
            }else {
                proxy.$modal.msgWarning("未定义业务表单组件");
            }
        });    
    } else {//自定义表单
        loadedComponent.value = await loadDIYComponent(row.key)
        .catch((err) => { proxy.$modal.msgWarning(err); });
        componentLoaded.value = true;
    }
    proxy.$modal.closeLoading();
    open.value = true;
}

/** 修改按钮操作 */
function handleEdit(row) {
    proxy.$modal.msgError("演示数据不允许修改操作！");
}

/** 删除按钮操作 */
function handleDelete(row) {
    proxy.$modal.msgError("演示环境不允许删除操作！");
}

/** 搜索按钮操作 */
function handleQuery() {
    getList();
}

/** 重置按钮操作 */
function resetQuery() {
    proxy.resetForm("queryRef");
    handleQuery();
}
function closeDialog() {
    open.value = false;
    openForm.value = false;
}
/** 重置操作表单 */
function reset() {
  form.value = {
    key: undefined,
    value: undefined, 
    remark: undefined
  };
  proxy.resetForm("formRef");
};
</script>
<style lang="scss" scoped> 
.component{
    background: white !important; 
    padding: 30px !important;
    max-width: 720px !important; 
    left: 0 !important;
    right: 0 !important;
    margin: auto !important;
}
</style> 