<template>
    <div>
        <common ref="commonRef" @clickNodeOpt="handleClickNode" :currentOptId="34" :afterOptId="35"
            :requireUserInfos="false"
            alertText="删除节点会移除当前或未来的审批节点.点击左侧节点后确认删除.删除当前节点时后端自动跳转到下一节点;删除未来节点会移除其全部审批人.">
            <template #userChoose>
                <el-empty v-if="!optFrom" description="请点击左侧要删除的审批人节点" />
                <div v-else>
                    <el-form :inline="true">
                        <el-form-item label="当前操作节点名称">
                            <el-input v-model="optFrom.nodeName" disabled style="width: 200px" />
                        </el-form-item>
                        <el-form-item>
                            <el-tag v-if="optFrom.operationType == 34" type="danger">删除当前节点</el-tag>
                            <el-tag v-else type="warning">删除未来节点</el-tag>
                        </el-form-item>
                    </el-form>
                    <el-button @click="handleCancel">返回</el-button>
                    <el-button type="warning" @click="handleReset">重置操作</el-button>
                    <el-button type="danger" icon="Delete" @click="handleDeleteNode">删除该节点</el-button>
                </div>
            </template>
        </common>
    </div>
</template>

<script setup>
import { ref, useTemplateRef } from 'vue';
import { processOperation } from '@/api/workflow/index';
import common from "./components/common.vue"
const { proxy } = getCurrentInstance();
const commonRef = useTemplateRef("commonRef");
let optFrom = ref(null);

/**点击流程图节点回调*/
const handleClickNode = (data, nodeUsers) => {
    optFrom.value = data.value;
}

/**删除节点*/
const handleDeleteNode = () => {
    if (!optFrom.value) {
        proxy.$modal.msgWarning("请先选择要删除的节点");
        return;
    }
    const isCurrent = optFrom.value.operationType == 34;
    const opDesc = isCurrent ? "当前节点" : "未来节点";
    proxy.$confirm('确认删除' + opDesc + '"' + optFrom.value.nodeName + '"吗？此操作不可逆！', "温馨提示", {
        type: "warning"
    }).then(() => {
        proxy.$modal.loading();
        processOperation(optFrom.value).then((res) => {
            if (res.code == 200) {
                proxy.$modal.msgSuccess("操作成功");
                //删除成功后清空选择,便于继续操作其他节点
                optFrom.value = null;
            } else {
                proxy.$modal.msgError("操作失败:" + res.errMsg);
            }
        });
        proxy.$modal.closeLoading();
    }).catch(() => { });
}

const handleCancel = () => {
    commonRef.value.handleCancel();
}
const handleReset = () => {
    optFrom.value = null;
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
