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
                    <template #tip>{{ approverConfig.nodeType === 19 ? '当满足以下条件时, 当前节点将自动退回到指定节点; 条件不满足时自动完成(不跳跃)' : approverConfig.nodeType === 20 ? '当满足以下条件时, 当前节点将自动退回到不同意按钮配置的目标节点; 条件不满足时由审批人人工处理' : approverConfig.nodeType === 21 ? '当满足以下条件时, 当前节点将自动退回发起人节点; 条件不满足时由审批人人工处理' : '当满足以下条件时, 当前节点将自动审批通过; 条件不满足时由审批人人工处理' }}</template>
                </ConditionGroupEditor>
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
                    <template #tip>{{ approverConfig.nodeType === 19 ? '当满足以下条件时, 当前节点将自动退回到指定节点; 条件不满足时自动完成(不跳跃)' : approverConfig.nodeType === 20 ? '当满足以下条件时, 当前节点将自动退回到不同意按钮配置的目标节点; 条件不满足时由审批人人工处理' : approverConfig.nodeType === 21 ? '当满足以下条件时, 当前节点将自动退回发起人节点; 条件不满足时由审批人人工处理' : '当满足以下条件时, 当前节点将自动审批通过; 条件不满足时由审批人人工处理' }}</template>
                </ConditionGroupEditor>
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
    return "审批人";
});
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
