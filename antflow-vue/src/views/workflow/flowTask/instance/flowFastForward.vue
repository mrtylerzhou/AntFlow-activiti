<template>
    <div>
        <common ref="commonRef" @clickNodeOpt="handleClickNode" :currentOptId="33" :afterOptId="33"
            :requireUserInfos="false"
            alertText="流程推进是将流程跳转到当前节点之后的某个未来节点,中间的节点将被自动跳过.请点击左侧要推进到的节点,填写推进原因后确认.">
            <template #userChoose>
                <el-empty v-if="!optFrom" description="请点击左侧要推进到的审批人节点" />
                <div v-else>
                    <el-form :inline="true">
                        <el-form-item label="推进到节点">
                            <el-input v-model="optFrom.nodeName" disabled style="width: 200px" />
                        </el-form-item>
                        <el-form-item label="推进原因">
                            <el-input v-model="approvalComment" placeholder="请输入推进原因" style="width: 300px" />
                        </el-form-item>
                    </el-form>
                    <el-button @click="handleCancel">返回</el-button>
                    <el-button type="primary" @click="handleSubmit">确定</el-button>
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
let approvalComment = ref('');

/**点击流程图节点回调*/
const handleClickNode = (data, nodeUsers) => {
    optFrom.value = data.value;
    approvalComment.value = '';
}

/**提交流程推进*/
const handleSubmit = () => {
    if (!optFrom.value) {
        proxy.$modal.msgWarning("请先选择要推进到的节点");
        return;
    }
    const submitData = {
        ...optFrom.value,
        taskDefKey: null,
        approvalComment: approvalComment.value
    };
    proxy.$confirm('确认推进流程到节点"' + optFrom.value.nodeName + '"吗？中间节点将被自动跳过！', "温馨提示", {
        type: "warning"
    }).then(() => {
        proxy.$modal.loading();
        processOperation(submitData).then((res) => {
            if (res.code == 200) {
                proxy.$modal.msgSuccess("推进成功");
                //刷新流程图显示最新状态
                commonRef.value.loadNodeOperationUserList();
            } else {
                proxy.$modal.msgError("推进失败:" + res.errMsg);
            }
        });
        proxy.$modal.closeLoading();
    }).catch(() => { });
}

const handleCancel = () => {
    commonRef.value.handleCancel();
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
