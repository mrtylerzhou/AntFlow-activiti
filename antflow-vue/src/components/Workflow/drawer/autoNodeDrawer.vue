<!--
 * 自动节点设置抽屉
 * 条件编辑逻辑已抽出到 ConditionGroupEditor 组件
 * 动作设置tab: 满足条件时(5选项单选) / 不满足条件时(3选项单选) + 条件子配置
-->
<template>
    <el-drawer v-if="!embed" :append-to-body="true" title="自动节点设置" v-model="visible" class="set_auto_node" :with-header="false"
        :size="680">
        <span class="drawer-title">自动节点设置</span>
        <template #header="{ titleId, titleClass }">
            <h3 :id="titleId" :class="titleClass">自动节点设置</h3>
        </template>
        <el-container>
            <el-main>
                <div class="demo-drawer__content">
                    <el-tabs v-model="activeName">
                        <el-tab-pane label="条件设置" name="conditionStep">
                            <ConditionGroupEditor
                                :conditionList="originalConfigData.conditionList"
                                v-model:groupRelation="originalConfigData.groupRelation"
                                v-model:nodeApproveList="originalConfigData.nodeApproveList">
                                <template #tip>当满足以下条件时，自动节点将执行自定义动作</template>
                            </ConditionGroupEditor>
                        </el-tab-pane>
                        <el-tab-pane label="动作设置" name="actionStep" lazy>
                            <div class="disagree-back-conf">
                                <p class="setting-group-title">满足条件时</p>
                                <el-radio-group v-model="actionConf.satisfiedAction">
                                    <el-radio :value="0">默认(自动完成当前节点)</el-radio>
                                    <el-radio :value="1">跳转至固定节点</el-radio>
                                    <el-radio :value="2">加批</el-radio>
                                    <el-radio :value="3">转办</el-radio>
                                    <el-radio :value="4">抄送</el-radio>
                                </el-radio-group>
                                <div v-if="actionConf.satisfiedAction === 1" style="margin-top: 8px;">
                                    <span>跳转目标节点：</span>
                                    <el-select v-model="actionConf.forwardNodeId" placeholder="请选择目标节点" style="width: 320px;">
                                        <el-option v-for="item in availableForwardNodes" :key="item.nodeId"
                                            :label="item.nodeName" :value="item.nodeId" />
                                    </el-select>
                                    <p v-if="availableForwardNodes.length === 0" style="color: #f56c6c; margin-top: 4px;">
                                        当前节点之后没有可选的审批人节点。
                                    </p>
                                </div>
                                <div v-if="actionConf.satisfiedAction === 2" style="margin-top: 8px;">
                                    <ApproverStepPanel :approver-config="signUpConf" :director-max-level="directorMaxLevel"
                                        :exclude-set-types="[7, 18, 19, 20, 2, 16]" :hide-sign-type="true" :hide-no-header-action="true" />
                                    <p class="setting-group-title" style="margin-top: 12px;">多人审批方式（加批人审批时采用）</p>
                                    <el-radio-group v-model="signUpTypeVal">
                                        <el-radio :value="1">顺序会签</el-radio>
                                        <el-radio :value="2">会签</el-radio>
                                        <el-radio :value="3">或签</el-radio>
                                    </el-radio-group>
                                    <p style="color: #909399; font-size: 12px; margin-top: 4px;">自动节点加批后不回到审批人(加批人审批完直接进入下一节点)</p>
                                </div>
                                <div v-if="actionConf.satisfiedAction === 3" style="margin-top: 8px;">
                                    <span>转办目标人：</span>
                                    <el-button size="small" @click="openTransferDialog">
                                        {{ actionConf.transferToUser ? actionConf.transferToUser.name : '选择人员' }}
                                    </el-button>
                                    <p style="color: #909399; font-size: 12px; margin-top: 4px;">满足条件时任务转给该人人工审批(不自动完成)</p>
                                </div>
                                <div v-if="actionConf.satisfiedAction === 4" style="margin-top: 8px;">
                                    <ApproverStepPanel :approver-config="copyConf" :director-max-level="directorMaxLevel"
                                        :exclude-set-types="[7, 18, 19, 20, 2, 16]" :hide-sign-type="true" :hide-no-header-action="true" />
                                </div>
                            </div>
                            <div class="disagree-back-conf" style="margin-top: 16px;">
                                <p class="setting-group-title">不满足条件时</p>
                                <el-radio-group v-model="actionConf.unsatisfiedAction">
                                    <el-radio :value="0">默认(自动完成当前节点)</el-radio>
                                    <el-radio :value="1">结束流程</el-radio>
                                    <el-radio :value="2">退回指定节点(重新开始)</el-radio>
                                </el-radio-group>
                                <div v-if="actionConf.unsatisfiedAction === 2" style="margin-top: 8px;">
                                    <span>退回目标节点：</span>
                                    <el-select v-model="actionConf.backToNodeId" placeholder="请选择退回目标节点" style="width: 320px;">
                                        <el-option v-for="item in availableBackNodes" :key="item.nodeId"
                                            :label="item.nodeName" :value="item.nodeId" />
                                    </el-select>
                                    <p v-if="availableBackNodes.length === 0" style="color: #f56c6c; margin-top: 4px;">
                                        当前节点之前没有可选的退回目标节点。
                                    </p>
                                </div>
                            </div>
                        </el-tab-pane>
                    </el-tabs>
                    <div class="demo-drawer__footer clear">
                        <el-button type="primary" @click="saveAutoNode">确 定</el-button>
                        <el-button @click="closeDrawer">取 消</el-button>
                    </div>
                </div>
            </el-main>
        </el-container>
        <select-user-dialog v-model:visible="transferUserVisible" :data="transferDialogData" @change="onTransferUserChange" />
    </el-drawer>
    <!-- 横向设计器 embed 模式: 常驻面板内嵌 -->
    <div v-else class="hd-embed-panel set_auto_node" v-show="visible">
        <span class="drawer-title">自动节点设置</span>
        <el-tabs v-model="activeName">
            <el-tab-pane label="条件设置" name="conditionStep">
                <ConditionGroupEditor
                    :conditionList="originalConfigData.conditionList"
                    v-model:groupRelation="originalConfigData.groupRelation"
                    v-model:nodeApproveList="originalConfigData.nodeApproveList">
                    <template #tip>当满足以下条件时，自动节点将执行自定义动作</template>
                </ConditionGroupEditor>
            </el-tab-pane>
            <el-tab-pane label="动作设置" name="actionStep" lazy>
                <div class="disagree-back-conf">
                    <p class="setting-group-title">满足条件时</p>
                    <el-radio-group v-model="actionConf.satisfiedAction">
                        <el-radio :value="0">默认(自动完成当前节点)</el-radio>
                        <el-radio :value="1">跳转至固定节点</el-radio>
                        <el-radio :value="2">加批</el-radio>
                        <el-radio :value="3">转办</el-radio>
                        <el-radio :value="4">抄送</el-radio>
                    </el-radio-group>
                    <div v-if="actionConf.satisfiedAction === 1" style="margin-top: 8px;">
                        <span>跳转目标节点：</span>
                        <el-select v-model="actionConf.forwardNodeId" placeholder="请选择目标节点" style="width: 320px;">
                            <el-option v-for="item in availableForwardNodes" :key="item.nodeId"
                                :label="item.nodeName" :value="item.nodeId" />
                        </el-select>
                    </div>
                    <div v-if="actionConf.satisfiedAction === 2" style="margin-top: 8px;">
                        <ApproverStepPanel :approver-config="signUpConf" :director-max-level="directorMaxLevel"
                            :exclude-set-types="[7, 18, 19, 20, 2, 16]" :hide-sign-type="true" :hide-no-header-action="true" />
                        <p class="setting-group-title" style="margin-top: 12px;">多人审批方式（加批人审批时采用）</p>
                        <el-radio-group v-model="signUpTypeVal">
                            <el-radio :value="1">顺序会签</el-radio>
                            <el-radio :value="2">会签</el-radio>
                            <el-radio :value="3">或签</el-radio>
                        </el-radio-group>
                    </div>
                    <div v-if="actionConf.satisfiedAction === 3" style="margin-top: 8px;">
                        <span>转办目标人：</span>
                        <el-button size="small" @click="openTransferDialog">
                            {{ actionConf.transferToUser ? actionConf.transferToUser.name : '选择人员' }}
                        </el-button>
                    </div>
                    <div v-if="actionConf.satisfiedAction === 4" style="margin-top: 8px;">
                        <ApproverStepPanel :approver-config="copyConf" :director-max-level="directorMaxLevel"
                            :exclude-set-types="[7, 18, 19, 20, 2, 16]" :hide-sign-type="true" :hide-no-header-action="true" />
                    </div>
                </div>
                <div class="disagree-back-conf" style="margin-top: 16px;">
                    <p class="setting-group-title">不满足条件时</p>
                    <el-radio-group v-model="actionConf.unsatisfiedAction">
                        <el-radio :value="0">默认(自动完成当前节点A)</el-radio>
                        <el-radio :value="1">结束流程</el-radio>
                        <el-radio :value="2">退回指定节点(重新开始)</el-radio>
                    </el-radio-group>
                    <div v-if="actionConf.unsatisfiedAction === 2" style="margin-top: 8px;">
                        <span>退回目标节点：</span>
                        <el-select v-model="actionConf.backToNodeId" placeholder="请选择退回目标节点" style="width: 320px;">
                            <el-option v-for="item in availableBackNodes" :key="item.nodeId"
                                :label="item.nodeName" :value="item.nodeId" />
                        </el-select>
                    </div>
                </div>
            </el-tab-pane>
        </el-tabs>
        <div class="demo-drawer__footer clear">
            <el-button type="primary" @click="saveAutoNode">确 定</el-button>
            <el-button @click="closeDrawer">取 消</el-button>
        </div>
        <select-user-dialog v-model:visible="transferUserVisible" :data="transferDialogData" @change="onTransferUserChange" />
    </div>
</template>
<script setup>
import { ref, watch, computed, inject } from 'vue'
import ConditionGroupEditor from "./condition/ConditionGroupEditor.vue";
import ApproverStepPanel from "./panel/ApproverStepPanel.vue";
import selectUserDialog from "../dialog/selectUserDialog.vue";
import { useStore } from '@/store/modules/workflow'
import $func from '@/utils/antflow/index'
import { useNodeForwardBack } from './useNodeForwardBack'
import { initAutoSignUpConf, finalizeAutoSignUpConf } from '@/utils/antflow/autoSignUpConfUtils'

const { proxy } = getCurrentInstance()

defineProps({
    embed: { type: Boolean, default: false },
    directorMaxLevel: { type: Number, default: 0 },
})

let store = useStore()
let { setAutoNode, setAutoNodeConfig } = store
let originalConfigData = ref({ conditionList: [[]], groupRelation: false, nodeApproveList: [] })
let autoNodeConfig1 = computed(() => store.autoNodeConfig1)
let autoNodeDrawer = computed(() => store.autoNodeDrawer)
const rootNode = inject('rootNode', ref({}))
const nodeRef = computed(() => (autoNodeConfig1.value && autoNodeConfig1.value.value) ? autoNodeConfig1.value.value : {})
const { availableForwardNodes, availableBackNodes } = useNodeForwardBack(nodeRef, rootNode)

let activeName = ref('conditionStep')
/**动作配置本地状态(保存时写回节点) */
let actionConf = ref({
    satisfiedAction: 0,
    unsatisfiedAction: 0,
    forwardNodeId: null,
    backToNodeId: null,
    transferToUser: null,
})
let signUpTypeVal = ref(1)
let transferUserVisible = ref(false)
const transferDialogData = computed(() => (actionConf.value.transferToUser ? [actionConf.value.transferToUser] : []))
const openTransferDialog = () => { transferUserVisible.value = true }
const onTransferUserChange = (list) => {
    const first = (list || [])[0]
    actionConf.value.transferToUser = first ? { id: first.targetId, name: first.name } : null
    transferUserVisible.value = false
}
/**加批规则子配置(懒初始化, 绑定嵌入的 ApproverStepPanel) */
const signUpConf = computed(() => {
    actionConf.value._signUpConf = initAutoSignUpConf(actionConf.value._signUpConf)
    return actionConf.value._signUpConf
})
/**抄送规则子配置(结构同加批) */
const copyConf = computed(() => {
    actionConf.value._copyConf = initAutoSignUpConf(actionConf.value._copyConf)
    return actionConf.value._copyConf
})

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
        // 加载动作配置反显
        actionConf.value = {
            satisfiedAction: nodeData.satisfiedAction || 0,
            unsatisfiedAction: nodeData.unsatisfiedAction || 0,
            forwardNodeId: (nodeData.forwardNodeIds && nodeData.forwardNodeIds.length > 0) ? nodeData.forwardNodeIds[0] : null,
            backToNodeId: nodeData.backToNodeId || null,
            transferToUser: nodeData.transferToUser || null,
            _signUpConf: initAutoSignUpConf(nodeData.autoSignUpConf),
            _copyConf: initAutoSignUpConf(nodeData.autoCopyConf),
        }
        signUpTypeVal.value = (nodeData.property && nodeData.property.signUpType) || 1
    }
})
// 条件组关系实时同步回 store 配置(横向设计器"切换节点自动保存"依赖 store 中的实时编辑对象)
watch(() => originalConfigData.value.groupRelation, (v) => {
    if (autoNodeConfig1.value && autoNodeConfig1.value.value) {
        autoNodeConfig1.value.value.groupRelation = v
    }
})

/**保存自动节点设置 */
const saveAutoNode = () => {
    // 动作子配置校验
    const a = actionConf.value
    if (a.satisfiedAction === 1 && !a.forwardNodeId) {
        proxy.$modal.msgError('请选择跳转目标节点'); return
    }
    if (a.satisfiedAction === 2 && !finalizeAutoSignUpConf(a._signUpConf)) {
        proxy.$modal.msgError('请完善加批设置的审批人规则'); return
    }
    if (a.satisfiedAction === 3 && !a.transferToUser) {
        proxy.$modal.msgError('请选择转办目标人'); return
    }
    if (a.satisfiedAction === 4 && !finalizeAutoSignUpConf(a._copyConf)) {
        proxy.$modal.msgError('请完善抄送设置的抄送人规则'); return
    }
    if (a.unsatisfiedAction === 2 && !a.backToNodeId) {
        proxy.$modal.msgError('请选择退回目标节点'); return
    }
    // 提交前转为后端存储格式
    $func.convertConditionNodeValue(originalConfigData.value.conditionList, false)
    // Update the auto node's conditionList and groupRelation
    let updatedNode = autoNodeConfig1.value.value
    if (updatedNode) {
        updatedNode.conditionList = originalConfigData.value.conditionList
        updatedNode.groupRelation = originalConfigData.value.groupRelation
        // 动作配置写回节点
        updatedNode.satisfiedAction = a.satisfiedAction || 0
        updatedNode.unsatisfiedAction = a.unsatisfiedAction || 0
        updatedNode.forwardNodeIds = a.satisfiedAction === 1 && a.forwardNodeId ? [a.forwardNodeId] : null
        updatedNode.backToNodeId = a.unsatisfiedAction === 2 ? (a.backToNodeId || null) : null
        updatedNode.autoSignUpConf = a.satisfiedAction === 2 ? a._signUpConf : null
        updatedNode.transferToUser = a.satisfiedAction === 3 ? a.transferToUser : null
        updatedNode.autoCopyConf = a.satisfiedAction === 4 ? a._copyConf : null
        // 加批多人审批方式; 自动节点加批强制不回到审批人
        if (!updatedNode.property) updatedNode.property = {}
        updatedNode.property.signUpType = signUpTypeVal.value || 1
        if (a.satisfiedAction === 2) updatedNode.property.afterSignUpWay = 2
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
