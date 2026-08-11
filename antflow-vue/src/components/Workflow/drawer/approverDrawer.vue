<template>
    <el-drawer v-if="!embed" :append-to-body="true" title="审批人设置" v-model="visible" :with-header="false" :size="680">
        <div class="el-drawer__header">
            <span class="drawer-title">{{ drawerTitle }}</span>
        </div>
        <el-tabs v-model="activeName" @tab-click="handleTabClick">
            <el-tab-pane v-if="approverConfig.nodeType !== 18 && approverConfig.nodeType !== 19" label="审批人设置" name="approverStep">
                <div v-if="approverStepShow">
                    <ApproverStepPanel :approverConfig="approverConfig" :directorMaxLevel="directorMaxLevel" />
                </div>
            </el-tab-pane>
            <el-tab-pane v-if="approverConfig.nodeType === 18 || (approverConfig.nodeType === 12 && (approverConfig.isConditionAdvanceNode || approverConfig.isConditionFinishNode))" lazy label="推进设置" name="forwardStep">
                <div class="disagree-back-conf">
                    <!-- 自动完成节点: 目标自动为最后一个审批人, 只读展示不可编辑 -->
                    <template v-if="approverConfig?.isAutoCompleteNode">
                        <p class="setting-group-title">推进目标节点</p>
                        <p style="color: #909399; font-size: 12px; margin-bottom: 12px;">
                            满足条件时自动推进到流程最后一个审批人节点(完成节点),不可编辑
                        </p>
                        <el-input :model-value="forwardFixedNodeName" placeholder="自动推进至完成节点" readonly disabled style="width: 320px;" />
                        <p v-if="availableForwardNodes.length === 0"
                            style="color: #f56c6c; margin-top: 4px;">
                            自动完成节点不能是最后一个审批人节点，请在后面添加审批人节点或调整位置。
                        </p>
                    </template>
                    <!-- 条件完成节点: 目标自动为最后一个审批人, 只读展示不可编辑 -->
                    <template v-else-if="approverConfig?.isConditionFinishNode">
                        <p class="setting-group-title">推进目标节点</p>
                        <p style="color: #909399; font-size: 12px; margin-bottom: 12px;">
                            满足条件时自动推进到流程最后一个审批人节点(完成节点),不可编辑
                        </p>
                        <el-input :model-value="forwardFixedNodeName" placeholder="自动推进至完成节点" readonly disabled style="width: 320px;" />
                        <p v-if="availableForwardNodes.length === 0"
                            style="color: #f56c6c; margin-top: 4px;">
                            条件完成节点不能是最后一个审批人节点，请在后面添加审批人节点或调整位置。
                        </p>
                    </template>
                    <!-- 条件推进节点: 固定目标节点(满足条件时自动推进到该目标) -->
                    <template v-else-if="approverConfig?.isConditionAdvanceNode">
                        <p class="setting-group-title">推进目标节点</p>
                        <p style="color: #909399; font-size: 12px; margin-bottom: 12px;">
                            满足条件时自动推进到此节点;不满足时交由审批人处理(点"同意"推进到此节点)
                        </p>
                        <el-select v-model="forwardFixedNodeId" placeholder="请选择目标节点" style="width: 320px;">
                            <el-option v-for="item in availableForwardNodes" :key="item.nodeId"
                                :label="item.nodeName" :value="item.nodeId" />
                        </el-select>
                    </template>
                    <!-- 自动推进节点: 用户手动选择目标节点 -->
                    <template v-else>
                        <p class="setting-group-title">推进目标节点</p>
                        <p style="color: #909399; font-size: 12px; margin-bottom: 12px;">
                            满足条件时推进到此节点,不满足时自动完成(不跳跃)
                        </p>
                        <el-select v-model="forwardFixedNodeId" placeholder="请选择目标节点" style="width: 320px;">
                            <el-option v-for="item in availableForwardNodes" :key="item.nodeId"
                                :label="item.nodeName" :value="item.nodeId" />
                        </el-select>
                    </template>
                </div>
            </el-tab-pane>
            <el-tab-pane v-if="approverConfig.nodeType === 19" lazy label="退回设置" name="returnStep">
                <div class="disagree-back-conf">
                    <p class="setting-group-title">退回目标节点</p>
                    <p style="color: #909399; font-size: 12px; margin-bottom: 12px;">
                        {{ approverConfig.drawBackType === 2 ? '固定退回至发起人节点;不满足条件时自动完成(不跳跃)' : '满足条件时自动退回到此节点;不满足时自动完成(不跳跃)' }}
                    </p>
                    <el-select v-model="autoReturnTargetNodeId" placeholder="请选择退回目标节点" style="width: 320px;"
                        :disabled="approverConfig.drawBackType === 2">
                        <el-option v-for="item in availableBackNodes" :key="item.nodeId"
                            :label="item.nodeName" :value="item.nodeId" />
                    </el-select>
                </div>
            </el-tab-pane>
            <el-tab-pane lazy v-if="approverConfig.nodeType === 12 || approverConfig.nodeType === 18 || approverConfig.nodeType === 19 || approverConfig.nodeType === 20 || approverConfig.nodeType === 21" label="条件设置" name="conditionStep">
                <ConditionGroupEditor
                    :conditionList="approverConfig.conditionList"
                    v-model:groupRelation="approverConfig.groupRelation"
                    v-model:nodeApproveList="approverConfig.nodeApproveList">
                    <template #tip>{{ approverConfig.isConditionDisagreeNode ? '当满足以下条件时, 当前节点将自动拒绝(终止流程); 条件不满足时由审批人人工处理' : approverConfig.nodeType === 19 ? '当满足以下条件时, 当前节点将自动退回到指定节点; 条件不满足时自动完成(不跳跃)' : approverConfig.nodeType === 20 ? '当满足以下条件时, 当前节点将自动退回到不同意按钮配置的目标节点; 条件不满足时由审批人人工处理' : approverConfig.nodeType === 21 ? '当满足以下条件时, 当前节点将自动退回发起人节点; 条件不满足时由审批人人工处理' : '当满足以下条件时, 当前节点将自动审批通过; 条件不满足时由审批人人工处理' }}</template>
                </ConditionGroupEditor>
            </el-tab-pane>
            <el-tab-pane v-if="approverConfig.isConditionAutoSignUpNode" lazy label="加批设置" name="autoSignUpStep">
                <ApproverStepPanel :approver-config="autoSignUpConf" :director-max-level="directorMaxLevel"
                    :exclude-set-types="[7, 18, 19, 20, 2, 16]" :hide-sign-type="true" :hide-no-header-action="true" />
                <div class="disagree-back-conf" style="margin-top: 12px;">
                    <p class="setting-group-title">多人审批方式（加批人审批时采用）</p>
                    <el-radio-group v-model="autoSignUpType">
                        <el-radio :value="1">顺序会签</el-radio>
                        <el-radio :value="2">会签</el-radio>
                        <el-radio :value="3">或签</el-radio>
                    </el-radio-group>
                    <div style="margin-top: 12px;">
                        <el-checkbox v-model="autoSignUpBackToApprover">加批后回到审批人</el-checkbox>
                    </div>
                </div>
            </el-tab-pane>
            <el-tab-pane v-if="approverConfig.isConditionAutoTransferNode" lazy label="转办设置" name="autoTransferStep">
                <div class="disagree-back-conf">
                    <p class="setting-group-title">转办类型</p>
                    <el-radio-group v-model="autoTransferType">
                        <el-radio :value="1">节点任务全部转给指定人</el-radio>
                        <el-radio :value="2">指定转办关系</el-radio>
                    </el-radio-group>
                    <div v-if="autoTransferType === 1" style="margin-top: 12px;">
                        <el-button type="primary" size="small" @click="openTransferSingleDialog">选择指定人</el-button>
                        <el-tag v-if="autoTransferConf.transferToUser" closable style="margin-left: 8px;"
                            @close="autoTransferConf.transferToUser = null">{{ autoTransferConf.transferToUser.name }}</el-tag>
                        <p v-else style="color: #f56c6c; font-size: 12px;">请选择一个人（当前节点任务全部转给该人）</p>
                    </div>
                    <div v-else style="margin-top: 12px;">
                        <div v-for="(pair, idx) in autoTransferConf.transferPairs" :key="idx"
                            style="display: flex; align-items: center; gap: 8px; margin-bottom: 8px;">
                            <el-tag v-if="pair.from" closable @close="pair.from = null">{{ pair.from.name }}</el-tag>
                            <el-button v-else size="small" @click="openTransferPairDialog(idx, 'from')">原审批人</el-button>
                            <span>→</span>
                            <el-tag v-if="pair.to" closable @close="pair.to = null">{{ pair.to.name }}</el-tag>
                            <el-button v-else size="small" @click="openTransferPairDialog(idx, 'to')">转办人</el-button>
                            <el-button link type="danger" @click="autoTransferConf.transferPairs.splice(idx, 1)">删除</el-button>
                        </div>
                        <el-button size="small" @click="autoTransferConf.transferPairs.push({ from: null, to: null })">添加转办关系</el-button>
                        <p style="color: #909399; font-size: 12px; margin-top: 4px;">运行时审批人为「原审批人」时转给对应「转办人」，不在关系中则保留原审批人</p>
                    </div>
                </div>
                <select-user-dialog v-model:visible="transferUserVisible" :data="transferDialogData" @change="onTransferUserChange" />
            </el-tab-pane>
            <el-tab-pane v-if="approverConfig.nodeType !== 18 && approverConfig.nodeType !== 19" lazy label="按钮权限设置" name="buttonStep">
                <ButtonStepPanel ref="buttonStepRef" :approverConfig="approverConfig" :rootNode="rootNode"
                    v-model:forwardFixedNodeId="forwardFixedNodeId"
                    :availableForwardNodes="availableForwardNodes" :availableBackNodes="availableBackNodes" />
            </el-tab-pane>
            <el-tab-pane v-if="formPermTabVisible && approverConfig.nodeType !== 18 && approverConfig.nodeType !== 19" lazy label="表单权限设置" name="formStep">
                <FormStepPanel v-if="formStepShow" :approverConfig="approverConfig" :flowType="flowType" />
            </el-tab-pane>
            <el-tab-pane v-if="approverConfig.nodeType !== 18 && approverConfig.nodeType !== 19" lazy label="通知设置" name="noticeStep">
                <NoticeStepPanel v-if="noticeStepShow" :approverConfig="approverConfig" @changeFlowMsgSet="handleFlowMsgSet" />
            </el-tab-pane>
            <el-tab-pane v-if="approverConfig.nodeType !== 18 && approverConfig.nodeType !== 19" lazy label="高级设置" name="advancedStep">
                <AdvancedStepPanel v-if="advancedStepShow" :approverConfig="approverConfig" />
            </el-tab-pane>
        </el-tabs>
        <div class="demo-drawer__footer clear">
            <el-button type="primary" @click="saveApprover">确 定</el-button>
            <el-button @click="closeDrawer">取 消</el-button>
        </div>
    </el-drawer>
    <!-- 横向设计器 embed 模式: 常驻面板内嵌 -->
    <div v-else class="hd-embed-panel" v-show="visible">
        <div class="el-drawer__header">
            <span class="drawer-title">{{ drawerTitle }}</span>
        </div>
        <el-tabs v-model="activeName" @tab-click="handleTabClick">
            <el-tab-pane v-if="approverConfig.nodeType !== 18 && approverConfig.nodeType !== 19" label="审批人设置" name="approverStep">
                <div v-if="approverStepShow">
                    <ApproverStepPanel :approverConfig="approverConfig" :directorMaxLevel="directorMaxLevel" />
                </div>
            </el-tab-pane>
            <el-tab-pane v-if="approverConfig.nodeType === 18 || (approverConfig.nodeType === 12 && (approverConfig.isConditionAdvanceNode || approverConfig.isConditionFinishNode))" lazy label="推进设置" name="forwardStep">
                <div class="disagree-back-conf">
                    <template v-if="approverConfig?.isAutoCompleteNode">
                        <p class="setting-group-title">推进目标节点</p>
                        <p style="color: #909399; font-size: 12px; margin-bottom: 12px;">
                            满足条件时自动推进到流程最后一个审批人节点(完成节点),不可编辑
                        </p>
                        <el-input :model-value="forwardFixedNodeName" placeholder="自动推进至完成节点" readonly disabled style="width: 320px;" />
                        <p v-if="availableForwardNodes.length === 0"
                            style="color: #f56c6c; margin-top: 4px;">
                            自动完成节点不能是最后一个审批人节点，请在后面添加审批人节点或调整位置。
                        </p>
                    </template>
                    <template v-else-if="approverConfig?.isConditionFinishNode">
                        <p class="setting-group-title">推进目标节点</p>
                        <p style="color: #909399; font-size: 12px; margin-bottom: 12px;">
                            满足条件时自动推进到流程最后一个审批人节点(完成节点),不可编辑
                        </p>
                        <el-input :model-value="forwardFixedNodeName" placeholder="自动推进至完成节点" readonly disabled style="width: 320px;" />
                        <p v-if="availableForwardNodes.length === 0"
                            style="color: #f56c6c; margin-top: 4px;">
                            条件完成节点不能是最后一个审批人节点，请在后面添加审批人节点或调整位置。
                        </p>
                    </template>
                    <template v-else-if="approverConfig?.isConditionAdvanceNode">
                        <p class="setting-group-title">推进目标节点</p>
                        <p style="color: #909399; font-size: 12px; margin-bottom: 12px;">
                            满足条件时自动推进到此节点;不满足时交由审批人处理(点"同意"推进到此节点)
                        </p>
                        <el-select v-model="forwardFixedNodeId" placeholder="请选择目标节点" style="width: 320px;">
                            <el-option v-for="item in availableForwardNodes" :key="item.nodeId"
                                :label="item.nodeName" :value="item.nodeId" />
                        </el-select>
                    </template>
                    <template v-else>
                        <p class="setting-group-title">推进目标节点</p>
                        <p style="color: #909399; font-size: 12px; margin-bottom: 12px;">
                            满足条件时推进到此节点,不满足时自动完成(不跳跃)
                        </p>
                        <el-select v-model="forwardFixedNodeId" placeholder="请选择目标节点" style="width: 320px;">
                            <el-option v-for="item in availableForwardNodes" :key="item.nodeId"
                                :label="item.nodeName" :value="item.nodeId" />
                        </el-select>
                    </template>
                </div>
            </el-tab-pane>
            <el-tab-pane v-if="approverConfig.nodeType === 19" lazy label="退回设置" name="returnStep">
                <div class="disagree-back-conf">
                    <p class="setting-group-title">退回目标节点</p>
                    <p style="color: #909399; font-size: 12px; margin-bottom: 12px;">
                        {{ approverConfig.drawBackType === 2 ? '固定退回至发起人节点;不满足条件时自动完成(不跳跃)' : '满足条件时自动退回到此节点;不满足时自动完成(不跳跃)' }}
                    </p>
                    <el-select v-model="autoReturnTargetNodeId" placeholder="请选择退回目标节点" style="width: 320px;"
                        :disabled="approverConfig.drawBackType === 2">
                        <el-option v-for="item in availableBackNodes" :key="item.nodeId"
                            :label="item.nodeName" :value="item.nodeId" />
                    </el-select>
                </div>
            </el-tab-pane>
            <el-tab-pane lazy v-if="approverConfig.nodeType === 12 || approverConfig.nodeType === 18 || approverConfig.nodeType === 19 || approverConfig.nodeType === 20 || approverConfig.nodeType === 21" label="条件设置" name="conditionStep">
                <ConditionGroupEditor
                    :conditionList="approverConfig.conditionList"
                    v-model:groupRelation="approverConfig.groupRelation"
                    v-model:nodeApproveList="approverConfig.nodeApproveList">
                    <template #tip>{{ approverConfig.isConditionDisagreeNode ? '当满足以下条件时, 当前节点将自动拒绝(终止流程); 条件不满足时由审批人人工处理' : approverConfig.nodeType === 19 ? '当满足以下条件时, 当前节点将自动退回到指定节点; 条件不满足时自动完成(不跳跃)' : approverConfig.nodeType === 20 ? '当满足以下条件时, 当前节点将自动退回到不同意按钮配置的目标节点; 条件不满足时由审批人人工处理' : approverConfig.nodeType === 21 ? '当满足以下条件时, 当前节点将自动退回发起人节点; 条件不满足时由审批人人工处理' : '当满足以下条件时, 当前节点将自动审批通过; 条件不满足时由审批人人工处理' }}</template>
                </ConditionGroupEditor>
            </el-tab-pane>
            <el-tab-pane v-if="approverConfig.isConditionAutoSignUpNode" lazy label="加批设置" name="autoSignUpStep">
                <ApproverStepPanel :approver-config="autoSignUpConf" :director-max-level="directorMaxLevel"
                    :exclude-set-types="[7, 18, 19, 20, 2, 16]" :hide-sign-type="true" :hide-no-header-action="true" />
                <div class="disagree-back-conf" style="margin-top: 12px;">
                    <p class="setting-group-title">多人审批方式（加批人审批时采用）</p>
                    <el-radio-group v-model="autoSignUpType">
                        <el-radio :value="1">顺序会签</el-radio>
                        <el-radio :value="2">会签</el-radio>
                        <el-radio :value="3">或签</el-radio>
                    </el-radio-group>
                    <div style="margin-top: 12px;">
                        <el-checkbox v-model="autoSignUpBackToApprover">加批后回到审批人</el-checkbox>
                    </div>
                </div>
            </el-tab-pane>
            <el-tab-pane v-if="approverConfig.isConditionAutoTransferNode" lazy label="转办设置" name="autoTransferStep">
                <div class="disagree-back-conf">
                    <p class="setting-group-title">转办类型</p>
                    <el-radio-group v-model="autoTransferType">
                        <el-radio :value="1">节点任务全部转给指定人</el-radio>
                        <el-radio :value="2">指定转办关系</el-radio>
                    </el-radio-group>
                    <div v-if="autoTransferType === 1" style="margin-top: 12px;">
                        <el-button type="primary" size="small" @click="openTransferSingleDialog">选择指定人</el-button>
                        <el-tag v-if="autoTransferConf.transferToUser" closable style="margin-left: 8px;"
                            @close="autoTransferConf.transferToUser = null">{{ autoTransferConf.transferToUser.name }}</el-tag>
                        <p v-else style="color: #f56c6c; font-size: 12px;">请选择一个人（当前节点任务全部转给该人）</p>
                    </div>
                    <div v-else style="margin-top: 12px;">
                        <div v-for="(pair, idx) in autoTransferConf.transferPairs" :key="idx"
                            style="display: flex; align-items: center; gap: 8px; margin-bottom: 8px;">
                            <el-tag v-if="pair.from" closable @close="pair.from = null">{{ pair.from.name }}</el-tag>
                            <el-button v-else size="small" @click="openTransferPairDialog(idx, 'from')">原审批人</el-button>
                            <span>→</span>
                            <el-tag v-if="pair.to" closable @close="pair.to = null">{{ pair.to.name }}</el-tag>
                            <el-button v-else size="small" @click="openTransferPairDialog(idx, 'to')">转办人</el-button>
                            <el-button link type="danger" @click="autoTransferConf.transferPairs.splice(idx, 1)">删除</el-button>
                        </div>
                        <el-button size="small" @click="autoTransferConf.transferPairs.push({ from: null, to: null })">添加转办关系</el-button>
                        <p style="color: #909399; font-size: 12px; margin-top: 4px;">运行时审批人为「原审批人」时转给对应「转办人」，不在关系中则保留原审批人</p>
                    </div>
                </div>
                <select-user-dialog v-model:visible="transferUserVisible" :data="transferDialogData" @change="onTransferUserChange" />
            </el-tab-pane>
            <el-tab-pane v-if="approverConfig.nodeType !== 18 && approverConfig.nodeType !== 19" lazy label="按钮权限设置" name="buttonStep">
                <ButtonStepPanel ref="buttonStepRef" :approverConfig="approverConfig" :rootNode="rootNode"
                    v-model:forwardFixedNodeId="forwardFixedNodeId"
                    :availableForwardNodes="availableForwardNodes" :availableBackNodes="availableBackNodes" />
            </el-tab-pane>
            <el-tab-pane v-if="formPermTabVisible && approverConfig.nodeType !== 18 && approverConfig.nodeType !== 19" lazy label="表单权限设置" name="formStep">
                <FormStepPanel v-if="formStepShow" :approverConfig="approverConfig" :flowType="flowType" />
            </el-tab-pane>
            <el-tab-pane v-if="approverConfig.nodeType !== 18 && approverConfig.nodeType !== 19" lazy label="通知设置" name="noticeStep">
                <NoticeStepPanel v-if="noticeStepShow" :approverConfig="approverConfig" @changeFlowMsgSet="handleFlowMsgSet" />
            </el-tab-pane>
            <el-tab-pane v-if="approverConfig.nodeType !== 18 && approverConfig.nodeType !== 19" lazy label="高级设置" name="advancedStep">
                <AdvancedStepPanel v-if="advancedStepShow" :approverConfig="approverConfig" />
            </el-tab-pane>
        </el-tabs>
        <div class="demo-drawer__footer clear">
            <el-button type="primary" @click="saveApprover">确 定</el-button>
            <el-button @click="closeDrawer">取 消</el-button>
        </div>
    </div>
</template>
<script setup>
import { ref, watch, computed, inject } from 'vue';
import $func from '@/utils/antflow/index';
import { useStore } from '@/store/modules/workflow';
import { useNodeForwardBack } from './useNodeForwardBack';
import ConditionGroupEditor from "./condition/ConditionGroupEditor.vue";
import selectUserDialog from "../dialog/selectUserDialog.vue";
import ApproverStepPanel from "./panel/ApproverStepPanel.vue";
import ButtonStepPanel from "./panel/ButtonStepPanel.vue";
import FormStepPanel from "./panel/FormStepPanel.vue";
import NoticeStepPanel from "./panel/NoticeStepPanel.vue";
import AdvancedStepPanel from "./panel/AdvancedStepPanel.vue";
const { proxy } = getCurrentInstance();
const store = useStore()
const route = useRoute()

const props = defineProps({
    directorMaxLevel: {
        type: Number,
        default: 0
    },
    embed: {
        type: Boolean,
        default: false
    }
});

/** 表单权限 tab 可见性: DIY 流程仅在启用辅助表单时显示; LF 流程始终显示 */
const formPermTabVisible = computed(() => {
    const isDIYRoute = (route.path || '').indexOf('diy-design') > 0;
    return isDIYRoute ? !!store.useAuxiliaryForm : true;
});
/** 流程类型标识（供表单权限面板判断 DIY 辅助表单模式） */
const flowType = computed(() => {
    const isDIYRoute = (route.path || '').indexOf('diy-design') > 0;
    return isDIYRoute ? 'DIY' : 'LF';
});

let approverConfig = ref({});
/**抽屉标题: 自动完成节点显示"自动完成", 其余保持默认 */
let drawerTitle = computed(() => {
    if (approverConfig.value?.isAutoCompleteNode) return "自动完成";
    if (approverConfig.value?.isConditionAdvanceNode) return "条件推进";
    if (approverConfig.value?.isConditionFinishNode) return "条件完成";
    if (approverConfig.value?.isConditionDisagreeNode) return "条件拒绝";
    if (approverConfig.value?.isConditionAutoSignUpNode) return "条件自动加批";
    if (approverConfig.value?.isConditionAutoTransferNode) return "条件自动转办";
    return "审批人";
});
// ========== 条件自动加批: 加批设置 tab 状态 ==========
/**加批规则子配置(懒初始化, 绑定嵌入的 ApproverStepPanel) */
const autoSignUpConf = computed(() => {
    if (!approverConfig.value.autoSignUpConf || !approverConfig.value.autoSignUpConf.setType) {
        approverConfig.value.autoSignUpConf = { setType: 5, nodeApproveList: [], property: {} };
    }
    return approverConfig.value.autoSignUpConf;
});
/**加批规则校验: 按类型检查必要数据 */
const validateAutoSignUpConf = (conf) => {
    const list = conf.nodeApproveList || [];
    if (conf.setType == 5 || conf.setType == 4 || conf.setType == 6) return list.length > 0;
    if (conf.setType == 3) return !!conf.directorLevel;
    if (conf.setType == 17) return !!(conf.property && conf.property.udrAssigneeProperty);
    return true; // 12 发起人自己 / 13 直属领导 无需额外数据
};
/**镜像 formatcommit_data 的映射, 构建运行期解析所需的 resolvedProperty */
const buildAutoSignUpResolvedProperty = (conf) => {
    const p = { emplIds: [], emplList: [], roleIds: [], roleList: [] };
    const list = conf.nodeApproveList || [];
    if (conf.setType == 4) {
        list.forEach(a => { p.roleIds.push(a.targetId); p.roleList.push({ id: a.targetId, name: a.name }); });
    } else if (conf.setType == 5) {
        list.forEach(a => { p.emplIds.push(a.targetId); p.emplList.push({ id: a.targetId, name: a.name }); });
    } else if (conf.setType == 6) {
        p.hrbpConfType = list.length ? list[0].targetId : 0;
    } else if (conf.setType == 3) {
        p.assignLevelGrade = conf.directorLevel;
    } else if (conf.setType == 17) {
        p.udrAssigneeProperty = (conf.property && conf.property.udrAssigneeProperty) || null;
        p.udrValueJson = (conf.property && conf.property.udrValueJson) || null;
    }
    return p;
};
/**多人审批方式(1顺序会签/2会签/3或签), 复用 property.signUpType */
const autoSignUpType = computed({
    get: () => (approverConfig.value.property && approverConfig.value.property.signUpType) || 1,
    set: (v) => { if (!approverConfig.value.property) approverConfig.value.property = {}; approverConfig.value.property.signUpType = v; }
});
/**加批后是否回到审批人(默认是), 复用 property.afterSignUpWay(1=回到/2=不回到) */
const autoSignUpBackToApprover = computed({
    get: () => ((approverConfig.value.property && approverConfig.value.property.afterSignUpWay) ?? 1) === 1,
    set: (v) => { if (!approverConfig.value.property) approverConfig.value.property = {}; approverConfig.value.property.afterSignUpWay = v ? 1 : 2; }
});
// ========== 条件自动转办: 转办设置 tab 状态 ==========
/**转办配置子配置(懒初始化) */
const autoTransferConf = computed(() => {
    if (!approverConfig.value.autoTransferConf || !approverConfig.value.autoTransferConf.transferType) {
        approverConfig.value.autoTransferConf = { transferType: 1, transferToUser: null, transferPairs: [] };
    }
    return approverConfig.value.autoTransferConf;
});
const autoTransferType = computed({
    get: () => autoTransferConf.value.transferType || 1,
    set: (v) => { autoTransferConf.value.transferType = v; }
});
let transferUserVisible = ref(false);
let transferDialogTarget = ref(null); // {kind:'single'} | {kind:'pair', idx, field}
const transferDialogData = computed(() => {
    const t = transferDialogTarget.value;
    if (!t) return [];
    if (t.kind === 'single') return autoTransferConf.value.transferToUser ? [autoTransferConf.value.transferToUser] : [];
    const pair = autoTransferConf.value.transferPairs[t.idx];
    const val = pair ? pair[t.field] : null;
    return val ? [val] : [];
});
const openTransferSingleDialog = () => { transferDialogTarget.value = { kind: 'single' }; transferUserVisible.value = true; };
const openTransferPairDialog = (idx, field) => { transferDialogTarget.value = { kind: 'pair', idx, field }; transferUserVisible.value = true; };
const onTransferUserChange = (list) => {
    const first = (list || [])[0];
    const user = first ? { id: first.targetId, name: first.name } : null;
    const t = transferDialogTarget.value;
    if (!t || !user) return;
    if (t.kind === 'single') autoTransferConf.value.transferToUser = user;
    else autoTransferConf.value.transferPairs[t.idx][t.field] = user;
    transferUserVisible.value = false;
};
/**转办配置校验: 类型1 恰好1人; 类型2 ≥1对且 from/to 完整、from 不重复 */
const validateAutoTransferConf = (conf) => {
    if (!conf || !conf.transferType) return false;
    if (conf.transferType === 1) return !!conf.transferToUser;
    const pairs = conf.transferPairs || [];
    if (!pairs.length) return false;
    const froms = new Set();
    for (const p of pairs) {
        if (!p.from || !p.to) return false;
        if (froms.has(p.from.id)) return false;
        froms.add(p.from.id);
    }
    return true;
};
let activeName = ref('approverStep');
let approverStepShow = ref(true);
let formStepShow = ref(false);
let noticeStepShow = ref(false);
let advancedStepShow = ref(false);
let approverConfig1 = computed(() => store.approverConfig1);
let approverDrawer = computed(() => store.approverDrawer);
const rootNode = inject('rootNode', ref({}));
const buttonStepRef = ref(null);

// ========== 推进/退回共享状态（推进设置/退回设置 tab 与按钮权限面板共享） ==========
const {
    forwardFixedNodeId,
    autoReturnTargetNodeId,
    availableForwardNodes,
    forwardFixedNodeName,
    availableBackNodes,
    loadAutoReturnConfig,
    loadForwardConfig,
} = useNodeForwardBack(approverConfig, rootNode);

let visible = computed({
    get() {
        const defaultTab = approverConfig.value?.nodeType === 18 ? 'forwardStep' : (approverConfig.value?.nodeType === 19 ? 'returnStep' : 'approverStep');
        handleTabClick({ paneName: defaultTab })
        return approverDrawer.value
    },
    set() {
        closeDrawer()
    }
});

/**页面加载监听事件 */
watch(approverConfig1, (val) => {
    if (val.value && val.value.nodeType == 7) {//nodeType == 7 是并行审批
        let currParallel = val.value.parallelNodes[val.value.index]
        approverConfig.value = currParallel;
    }
    else {
        approverConfig.value = val.value || {};
    }
    loadForwardConfig(val.value || {});
    loadAutoReturnConfig(val.value || {});
});

/**消息设置 */
const handleFlowMsgSet = (data) => {
    store.setApproverConfig({
        value: approverConfig1.value.value,
        flag: true,
        id: approverConfig1.value.id
    })
}

/**
 * 切换tab
 * @param tab 当前tab
 */
const handleTabClick = (tab, event) => {
    activeName.value = tab.paneName;
    if (tab.paneName == 'formStep') {
        formStepShow.value = true;
    } else {
        formStepShow.value = false;
    }
    if (tab.paneName == 'noticeStep') {
        noticeStepShow.value = true;
    } else {
        noticeStepShow.value = false;
    }
    if (tab.paneName == 'advancedStep') {
        advancedStepShow.value = true;
    } else {
        advancedStepShow.value = false;
    }
}

/**条件抽屉的确认 */
const saveApprover = () => {
    // 条件自动加批: 加批规则必填校验 + 构建运行期解析所需的 resolvedProperty
    if (approverConfig.value.isConditionAutoSignUpNode) {
        const conf = approverConfig.value.autoSignUpConf;
        if (conf && conf.setType) {
            if (!validateAutoSignUpConf(conf)) {
                proxy.$modal.msgError('请完善加批设置的审批人规则');
                return;
            }
            conf.nodeProperty = conf.setType;
            conf.resolvedProperty = buildAutoSignUpResolvedProperty(conf);
        } else if (!(approverConfig.value.autoSignUpUsers || []).length) {
            proxy.$modal.msgError('请选择加批人');
            return;
        }
    }
    // 条件自动转办: 转办配置必填校验
    if (approverConfig.value.isConditionAutoTransferNode) {
        if (!validateAutoTransferConf(approverConfig.value.autoTransferConf)) {
            proxy.$modal.msgError('请完善转办设置（类型1需选择指定人，类型2需完整且不重复的转办关系）');
            return;
        }
    }
    // 仲裁签通过比例校验
    if (approverConfig.value.signType == 4) {
        const r = approverConfig.value.property?.arbitrationRatio;
        if (!r || r < 1 || r > 100) {
            proxy.$modal.msgError('请填写仲裁签通过比例(1-100)');
            return;
        }
    }
    approverConfig.value.nodeDisplayName = $func.setApproverStr(approverConfig.value);
    approverConfig.value.error = !$func.setApproverStr(approverConfig.value);
    // 条件自动加批: 加批规则为空同样置 error 阻发布
    if (approverConfig.value.isConditionAutoSignUpNode) {
        const hasConf = !!(approverConfig.value.autoSignUpConf && approverConfig.value.autoSignUpConf.setType);
        approverConfig.value.error = approverConfig.value.error || (!hasConf && !(approverConfig.value.autoSignUpUsers || []).length);
    }
    // 条件自动转办: 转办配置为空同样置 error 阻发布
    if (approverConfig.value.isConditionAutoTransferNode) {
        approverConfig.value.error = approverConfig.value.error || !validateAutoTransferConf(approverConfig.value.autoTransferConf);
    }
    console.log('保存审批人配置==', JSON.stringify(approverConfig1.value));
    store.setApproverConfig({
        value: approverConfig1.value.value,
        flag: true,
        id: approverConfig1.value.id
    })
    closeDrawer()
}
/**关闭抽屉 */
const closeDrawer = () => {
    store.setApprover(false)
}
</script>
<style scoped lang="scss">
@use "@/assets/styles/antflow/dialog.scss";

.el-drawer__header {
    margin-bottom: 5px !important;
}

.el-tabs {
    margin-left: 20px !important;
}

.setting-group-title {
    margin: 0 0 10px 0;
    font-size: 14px;
    font-weight: 600;
}

.disagree-back-conf {
    margin-top: 12px;
    padding: 10px;
    background: #f9f9f9;
    border-radius: 4px;
}
</style>
