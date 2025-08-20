<template>
    <div class="app-container">
        <el-scrollbar height="83vh">
            <el-row>
                <el-col :span="24">
                    <div class="mb10">
                        <el-descriptions title="流程信息" :column="3" border>
                            <el-descriptions-item label="流程类型">
                                {{ queryForm.processCode }}
                            </el-descriptions-item>
                            <el-descriptions-item label="流程名称">
                                {{ queryForm.processTypeName }}
                            </el-descriptions-item>
                            <el-descriptions-item label="流程编号">
                                {{ queryForm.processNumber }}
                            </el-descriptions-item>
                            <el-descriptions-item label="流程描述">
                                {{ queryForm.description }}
                            </el-descriptions-item>
                            <el-descriptions-item label="发起人">
                                {{ queryForm.actualName }}
                            </el-descriptions-item>
                            <el-descriptions-item label="发起时间">
                                {{ queryForm.createTime }}
                            </el-descriptions-item>
                            <!-- <el-descriptions-item label="流程状态" :span="2">
                                <el-tag type="success"> {{ queryForm.taskState }}</el-tag>
                            </el-descriptions-item> -->
                        </el-descriptions>
                    </div>
                </el-col>
            </el-row>
            <el-row :gutter="20" class="mb10">
                <el-col :span="10">
                    <div>
                        <ReviewWarp />
                    </div>
                </el-col>
                <el-col :span="14" style="background-color: #f5f5f5;">
                    <div class="p20">
                        <el-alert title="加/减/变更都是针对当前正在审批的节点,每次都能只处理一个人.不允许多个人同时操作." type="warning" show-icon
                            :closable="false" />
                        <slot name="userChoose" :clickNodeMethod="clickNode"></slot>
                    </div>
                </el-col>
            </el-row>
        </el-scrollbar>
        <label class="page-close-box" @click="handleCancel"><img src="@/assets/images/antflow/back-close.png"></label>
    </div>
</template>

<script setup>
import { provide, onBeforeMount } from 'vue';
import { processOperation } from '@/api/workflow/index';
import ReviewWarp from "@/components/Workflow/Preview/reviewWarp.vue"
import { useStore } from '@/store/modules/workflow';
const { proxy } = getCurrentInstance();
const router = useRouter();
const route = useRoute()
const queryForm = route.query;

let emits = defineEmits(["clickNodeOpt"]);

let store = useStore()
let { setPreviewDrawerConfig } = store
let optFrom = ref({
    operationType: 24,
    formCode: queryForm.processKey,
    processNumber: queryForm.processNumber,
    taskDefKey: queryForm.taskName,
    nodeId: null,
    nodeName: null,
    userInfos: []
});

onBeforeMount(() => {
    /**预览流程图 */
    setPreviewDrawerConfig({
        formCode: queryForm.processKey,
        processNumber: queryForm.processNumber,
        isOutSideAccess: queryForm.isOutSideProcess,
        isLowCodeFlow: queryForm.isLowCodeFlow,
        processState: queryForm.processState,
    })
})

/**点击流程图节点 */
const clickNode = (data) => {
    if (data.beforeNodeIds.includes(data.nodeId) || data.params.isNodeDeduplication == 1) {
        proxy.$modal.msgWarning("该节点不能操作，请选择其他节点")
        return;
    }
    optFrom.value.nodeName = data.nodeName;
    optFrom.value.nodeId = data.Id;
    optFrom.value.operationType = data.currentNodeId == data.nodeId ? 24 : 27; //当前节点加签 未来节点加签 
    //isChangedCount = data.params?.assigneeList.length || 0;
    optFrom.value.userInfos = data.params?.assigneeList
        .map(item => {
            return {
                id: item.assignee,
                name: item.assigneeName,
                isDeduplication: item.isDeduplication
            }
        }) || [];
    //.filter((c) => c.isDeduplication == 0)
    emits("clickNodeOpt", optFrom);
}
provide("onClickNode", clickNode)
const handleCancel = () => {
    proxy.$tab.closePage();
    router.push({
        path: "/workflow/instance",
    });
}
const handleSubmit = (data) => {
    if (proxy.isEmptyArray(data.userInfos)) {
        proxy.$modal.msgWarning("没有任何改动");
        return;
    }
    proxy.$modal.loading();
    processOperation(data).then((res) => {
        if (res.code == 200) {
            proxy.$modal.msgSuccess("操作成功");
            //close();
        } else {
            proxy.$modal.msgError("操作失败:" + res.errMsg);
        }
    });
    proxy.$modal.closeLoading();
}
defineExpose({
    handleSubmit,
    handleCancel,
    optFrom
})
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