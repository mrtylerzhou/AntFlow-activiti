<!--
 * 自动节点设置抽屉
 * 条件编辑逻辑已抽出到 ConditionGroupEditor 组件
-->
<template>
    <el-drawer :append-to-body="true" title="自动节点设置" v-model="visible" class="set_auto_node" :with-header="false"
        :size="680">
        <span class="drawer-title">自动节点设置</span>
        <template #header="{ titleId, titleClass }">
            <h3 :id="titleId" :class="titleClass">自动节点设置</h3>
        </template>
        <el-container>
            <el-main>
                <div class="demo-drawer__content">
                    <ConditionGroupEditor
                        :conditionList="originalConfigData.conditionList"
                        v-model:groupRelation="originalConfigData.groupRelation"
                        v-model:nodeApproveList="originalConfigData.nodeApproveList">
                        <template #tip>当满足以下条件时，自动节点将执行自定义动作</template>
                    </ConditionGroupEditor>
                    <div class="demo-drawer__footer clear">
                        <el-button type="primary" @click="saveAutoNode">确 定</el-button>
                        <el-button @click="closeDrawer">取 消</el-button>
                    </div>
                </div>
            </el-main>
        </el-container>
    </el-drawer>
</template>
<script setup>
import { ref, watch, computed } from 'vue'
import ConditionGroupEditor from "./condition/ConditionGroupEditor.vue";
import { useStore } from '@/store/modules/workflow'
import $func from '@/utils/antflow/index'

let store = useStore()
let { setAutoNode, setAutoNodeConfig } = store
let originalConfigData = ref({ conditionList: [[]], groupRelation: false, nodeApproveList: [] })
let autoNodeConfig1 = computed(() => store.autoNodeConfig1)
let autoNodeDrawer = computed(() => store.autoNodeDrawer)
let visible = computed({
    get() {
        return autoNodeDrawer.value
    },
    set() {
        closeDrawer()
    }
})
watch(autoNodeConfig1, (val) => {
    if (val && val.value) {
        let nodeData = val.value
        originalConfigData.value = {
            conditionList: nodeData.conditionList && nodeData.conditionList.length > 0
                ? nodeData.conditionList : [[]],
            groupRelation: nodeData.groupRelation || false,
            nodeApproveList: nodeData.nodeApproveList || [],
            nodeId: nodeData.nodeId,
            nodeName: nodeData.nodeName,
        }
        // 加载时转为前端显示格式 (由 ConditionGroupEditor 内部 watch 处理)
    }
})

/**保存自动节点设置 */
const saveAutoNode = () => {
    // 提交前转为后端存储格式
    $func.convertConditionNodeValue(originalConfigData.value.conditionList, false)
    // Update the auto node's conditionList and groupRelation
    let updatedNode = autoNodeConfig1.value.value
    if (updatedNode) {
        updatedNode.conditionList = originalConfigData.value.conditionList
        updatedNode.groupRelation = originalConfigData.value.groupRelation
        // Update error state: if no conditions configured, no error
        let hasConditions = originalConfigData.value.conditionList.some(group =>
            group.some(item => item.columnId && item.columnId !== 0)
        )
        updatedNode.error = !hasConditions
        // Build display text
        updatedNode.nodeDisplayName = $func.buildConditionDisplayText(
            originalConfigData.value.conditionList,
            originalConfigData.value.groupRelation
        )
    }
    setAutoNodeConfig({
        value: updatedNode,
        flag: true,
        id: autoNodeConfig1.value.id,
    })
    closeDrawer()
}
/**关闭抽屉 */
const closeDrawer = () => {
    setAutoNode(false)
}
</script>
<style scoped lang="scss">
@use "@/assets/styles/antflow/dialog.scss";

.set_auto_node {
    .demo-drawer__footer {
        margin-top: 20px;
        text-align: right;
    }
}
</style>
