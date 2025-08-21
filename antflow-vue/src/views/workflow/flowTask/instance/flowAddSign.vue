<template>
    <div>
        <common ref="commonRef" @clickNodeOpt="handleClickNode" :currentOptId="25" :afterOptId="28">
            <template #userChoose>
                <el-empty v-if="checkedUserList.length === 0" description="请点击左侧审批人节点" />
                <div v-else>
                    <el-form :inline="true">
                        <el-form-item label="节点名称">
                            <el-input v-model="optFrom.nodeName" disabled style="width: 200px" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="success" icon="CirclePlus" @click="addApproveUser">新增审批人</el-button>
                        </el-form-item>
                    </el-form>
                    <el-table v-loading="loading" :data="checkedUserList" class="mb10"
                        style="height: 400px; width: 97%">
                        <el-table-column prop="id" label="审批人ID" width="180" />
                        <el-table-column prop="name" label="审批人姓名" width="180" />
                        <el-table-column label="操作" fixed="right" align="left" class-name="small-padding fixed-width">
                            <template #default="scope">
                                <el-button link type="danger" icon="Delete" :disabled="scope.row.canDelete === false"
                                    @click="handleDeleteUser(scope.row)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-button @click="handleCancel">返回</el-button>
                    <el-button type="warning" @click="handleReset">重置操作</el-button>
                    <el-button type="primary" @click="handleSubmit">提交修改</el-button>
                </div>
            </template>
        </common>
        <select-user-dialog v-model:visible="approverUserVisible" @change="sureUserApprover" />
    </div>
</template>

<script setup>
import { ref, useTemplateRef } from 'vue';
import common from "./components/common.vue"
import selectUserDialog from '@/components/Workflow/dialog/selectUserDialog.vue';
const { proxy } = getCurrentInstance();
const commonRef = useTemplateRef("commonRef");
let loading = ref(false);
let isChangedCount = 0;
let approverUserVisible = ref(false);
let checkedUserList = ref([]);
let optFrom = ref(null)
/**点击流程图节点回调*/
const handleClickNode = (data) => {
    optFrom.value = data.value;
    isChangedCount = data.value.userInfos.length || 0;
    checkedUserList.value = data.value.userInfos.map(item => {
        return {
            id: item.id,
            name: item.name,
            canDelete: false
        }
    });
    optFrom.value.userInfos = [];
}

/**选择审批人确认按钮 */
const sureUserApprover = (data) => {
    if (proxy.isEmptyArray(data)) {
        return;
    }
    if (data.length > 1) {
        proxy.$modal.msgError("每次最多添加一个审批人");
        return;
    }
    if (checkedUserList.value.length > isChangedCount) {
        proxy.$modal.msgError("每次最多添加一个审批人");
        return;
    }
    if (checkedUserList.value.some((c) => c.id == data[0].targetId)) {
        proxy.$modal.msgError("用户已被选中");
    } else {
        const checkedList = data.map(item => {
            return {
                id: item.targetId,
                name: item.name,
                canDelete: true
            }
        })
        checkedUserList.value.push(...checkedList)
        optFrom.value.userInfos = checkedList
    }
    approverUserVisible.value = false;
}
const handleDeleteUser = (row) => {
    checkedUserList.value = checkedUserList.value.filter(item => item.id != row.id)
    optFrom.value.userInfos = optFrom.value.userInfos.filter(item => item.id != row.id)
}

const addApproveUser = () => {
    approverUserVisible.value = true;
}
const handleSubmit = () => {
    commonRef.value.handleSubmit(optFrom.value);
}
const handleCancel = () => {
    commonRef.value.handleCancel();
}
const handleReset = () => {
    loading.value = true;
    checkedUserList.value = commonRef.value.originalNodeUserList.map(item => {
        return {
            id: item.id,
            name: item.name,
            canDelete: false
        }
    });
    setTimeout(() => {
        loading.value = false;
    }, 300);
} 
</script>
<style lang="scss" scoped>
.empty-text {
    display: block;
    text-align: center;
    width: 100%;
    color: #888;
    margin: 20px 0;
}
</style>