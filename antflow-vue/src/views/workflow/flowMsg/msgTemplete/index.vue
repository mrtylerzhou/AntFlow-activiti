<template>
    <div class="app-container">
        <div class="query-box">
            <el-form :model="taskMgmtVO" ref="queryRef" :inline="true" v-show="showSearch">
                <el-form-item label="模板名称" prop="name">
                    <el-input v-model="taskMgmtVO.name" placeholder="请输入关键字" clearable style="width: 200px"
                        @keyup.enter="handleQuery" />
                </el-form-item>
                <el-form-item label="模板标题" prop="mailTitle">
                    <el-input v-model="taskMgmtVO.mailTitle" placeholder="请输入关键字" clearable style="width: 200px"
                        @keyup.enter="handleQuery" />
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
            </el-row>
        </div>
        <div class="table-box">
            <el-table v-loading="loading" :data="dataList">
                <el-table-column label="模板名称" align="center" prop="name" width="160" :show-overflow-tooltip="true">
                    <template #default="item">
                        <el-tooltip class="box-item" effect="dark" placement="right">
                            <template #content>
                                <span>{{ item.row.name }}</span>
                            </template>
                            {{ substringHidden(item.row.name) }}
                        </el-tooltip>
                    </template>
                </el-table-column>
                <el-table-column label="模板标题" align="center" prop="mailTitle" :show-overflow-tooltip="true" />
                <el-table-column label="状态" align="center" prop="statusValue">
                    <template #default="item">
                        <el-tag>{{ item.row.statusValue }}</el-tag>
                    </template>
                </el-table-column>
                <!-- <el-table-column label="创建时间" align="center" prop="createTime" width="180">
             <template #default="scope">
                <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
             </template>
          </el-table-column> -->
                <el-table-column label="更新时间" align="center" prop="updateTime" width="160">
                    <template #default="scope">
                        <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d} {h}:{i}') }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" fixed="right" width="160" class-name="small-padding fixed-width">
                    <template #default="scope">
                        <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button link type="primary" icon="ZoomIn" @click="handlePreview(scope.row)">查看</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
            @pagination="getList" />
        <msg-temp-form v-model:visible="visible" :formData="dataTempForm" />
    </div>
</template>

<script setup>
import { getFlowMsgTempleteList } from "@/api/workflow/flowMsgApi";
import MsgTempForm from "@/views/workflow/flowMsg/msgTemplete/form.vue";
const { proxy } = getCurrentInstance();
const dataList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);

let visible = ref(false);
let dataTempForm = ref({});

const data = reactive({
    form: {},
    pageDto: {
        page: 1,
        pageSize: 10
    },
    taskMgmtVO: {
        name: undefined,
        mailTitle: undefined
    },
    rules: {
        name: [{ required: true, message: "关键字不能为空", trigger: "blur" }],
        mailTitle: [{ required: true, message: "关键字不能为空", trigger: "blur" }],
    }
});
const { pageDto, taskMgmtVO } = toRefs(data);

/** 查询流程监控列表 */
const getList = async () => {
    loading.value = true;
    await getFlowMsgTempleteList(pageDto.value, taskMgmtVO.value).then(response => {
        dataList.value = response.data;
        total.value = response.pagination.totalCount;
        loading.value = false;
    }).catch((r) => {
        loading.value = false;
        console.log(r);
        proxy.$modal.msgError("加载列表失败:" + r.message);
    });
}

getList();

/** 搜索按钮操作 */
const handleQuery = async () => {
    pageDto.value.page = 1;
    await getList();
}
function resetQuery() {
    taskMgmtVO.value = {
        name: undefined,
        mailTitle: undefined
    };
    handleQuery();
}

function handleAdd() {
    visible.value = true;
    dataTempForm.value = {
        notifyType: [],
        systemTitle: "工作流名称是:",
        systemContent: ""
    };
}
function handleEdit(row) {
    visible.value = true;
    dataTempForm.value = row;
}
const handlePreview = (row) => {
    visible.value = true;
    dataTempForm.value = row;
}
</script>