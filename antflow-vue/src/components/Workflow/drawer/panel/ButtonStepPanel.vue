<template>
    <div v-if="node && node.nodeType !== 18 && node.nodeType !== 19">
        <p>【审批页面】按钮权限显示控制</p>
        <el-checkbox-group class="clear" v-model="checkApprovalPageBtns">
            <div class="btn-row" v-for="opt in approvalPageButtons" :key="opt.value">
                <el-checkbox :value="opt.value" :disabled="opt.type === 'default' || (node.nodeType === 17 && opt.value === 41) || (opt.value === approvalButtonConf.forwardToNode && approveForwardBehavior === 2) || (node.isConditionAutoSignUpNode && opt.value === approvalButtonConf.addApproval)"
                    @change="handleCheckedButtonsChange(opt.value)">
                    【{{ opt.label }}】
                </el-checkbox>
                <el-popover placement="top" :width="240" trigger="click" popper-class="btn-desc-popover">
                    <template #reference>
                        <el-icon class="btn-help-icon" @click.stop><QuestionFilled /></el-icon>
                    </template>
                    <div>{{ opt.description }}</div>
                </el-popover>
                <el-input class="btn-name-input" v-model="buttonCustomNames[opt.value]"
                    maxlength="8" placeholder="自定义名称" size="small"
                    :disabled="!checkApprovalPageBtns.includes(opt.value)"
                    @input="syncApprovalPageButtons" />
            </div>
        </el-checkbox-group>

        <div v-if="afterSignUpWayVisible && !node.isConditionAutoSignUpNode">
            <el-radio-group v-model="approvalBtnSubOption"
                @change="handleApprovalBtnSubOption(approvalBtnSubOption)" class="clear">
                <el-radio :value="1" class="auth-btn" border>
                    【顺序会签】
                    <el-popover placement="top" :width="240" trigger="click" popper-class="btn-desc-popover">
                        <template #reference>
                            <el-icon class="btn-help-icon" @click.stop><QuestionFilled /></el-icon>
                        </template>
                        <div>多个会签人员，依次进行审批</div>
                    </el-popover>
                </el-radio>
                <el-radio :value="2" class="auth-btn" border>
                    【会签】
                    <el-popover placement="top" :width="240" trigger="click" popper-class="btn-desc-popover">
                        <template #reference>
                            <el-icon class="btn-help-icon" @click.stop><QuestionFilled /></el-icon>
                        </template>
                        <div>多个会签人员，同步进行审批</div>
                    </el-popover>
                </el-radio>

                <el-radio :value="3" class="auth-btn" border>
                    【或签】
                    <el-popover placement="top" :width="240" trigger="click" popper-class="btn-desc-popover">
                        <template #reference>
                            <el-icon class="btn-help-icon" @click.stop><QuestionFilled /></el-icon>
                        </template>
                        <div>只需一名审批人同意或拒绝即可</div>
                    </el-popover>
                </el-radio>
                <el-radio :value="9" class="auth-btn" border>
                    【回到加批人】
                    <el-popover placement="top" :width="240" trigger="click" popper-class="btn-desc-popover">
                        <template #reference>
                            <el-icon class="btn-help-icon" @click.stop><QuestionFilled /></el-icon>
                        </template>
                        <div>只能是顺序会签，加批人审批完之后，会回到本节点的审批人再次审批</div>
                    </el-popover>
                </el-radio>
            </el-radio-group>
        </div>

        <!-- 不同意按钮行为配置 -->
        <div v-if="checkApprovalPageBtns.includes(approvalButtonConf.noAgree)" class="disagree-back-conf">
            <p class="setting-group-title">不同意按钮行为</p>
            <el-radio-group v-model="disagreeBackType" @change="onDisagreeBackTypeChange">
                <el-radio :value="0" :disabled="node.nodeType === 20 || node.nodeType === 21">结束流程</el-radio>
                <el-radio :value="2" :disabled="node.nodeType === 20">退回发起人</el-radio>
                <el-radio :value="4" :disabled="node.nodeType === 21">退回指定节点（重新开始）</el-radio>
                <el-radio :value="5" :disabled="node.nodeType === 21">退回指定节点（回到当前节点）</el-radio>
            </el-radio-group>
            <div v-if="(disagreeBackType === 4 || disagreeBackType === 5) && node.nodeType !== 21" style="margin-top: 8px;">
                <span>退回目标节点：</span>
                <el-select v-model="disagreeBackToNodeId" placeholder="请选择目标节点" style="width: 260px;">
                    <el-option v-for="item in availableBackNodes" :key="item.nodeId"
                        :label="item.nodeName" :value="item.nodeId" />
                </el-select>
            </div>
            <div v-if="node.nodeType === 21" style="margin-top: 8px;">
                <el-checkbox v-model="conditionReturnStarterBackToCurrent">退回后重新审批经过该节点时回到退回人</el-checkbox>
            </div>
        </div>

        <!-- 退回按钮行为配置 -->
        <div v-if="checkApprovalPageBtns.includes(approvalButtonConf.repulse) && node.nodeType !== 20 && node.nodeType !== 21" class="disagree-back-conf">
            <p class="setting-group-title">退回按钮行为</p>
            <el-radio-group v-model="drawBackBehavior" @change="onDrawBackBehaviorChange">
                <el-radio :value="0">无限制（审批人自选）</el-radio>
                <el-radio :value="1">退回发起人</el-radio>
                <el-radio :value="2">退回上一节点</el-radio>
                <el-radio :value="3">退回指定节点</el-radio>
            </el-radio-group>
            <div v-if="drawBackBehavior === 1" style="margin-top: 8px;">
                <el-checkbox v-model="drawBackReturnToSender">退回后重新审批经过该节点时回到退回人</el-checkbox>
            </div>
            <div v-if="drawBackBehavior === 3" style="margin-top: 8px;">
                <span>退回目标节点：</span>
                <el-select v-model="drawBackNodeIds" multiple placeholder="请选择目标节点" style="width: 320px;">
                    <el-option v-for="item in availableBackNodes" :key="item.nodeId"
                        :label="item.nodeName" :value="item.nodeId" />
                </el-select>
                <div style="margin-top: 8px;">
                    <el-checkbox v-model="drawBackReturnToSender">退回后重新审批经过该节点时回到退回人</el-checkbox>
                </div>
            </div>
        </div>

        <!-- 推进按钮行为配置 -->
        <div v-if="checkApprovalPageBtns.includes(approvalButtonConf.forwardToNode)" class="disagree-back-conf">
            <p class="setting-group-title">推进按钮行为</p>
            <el-radio-group v-model="forwardBehavior" @change="onForwardBehaviorChange"
                :disabled="node?.isFinishApproveNode || node?.isConditionAdvanceNode || node?.isConditionFinishNode || approveForwardBehavior === 2">
                <el-radio :value="0">任意未来节点</el-radio>
                <el-radio :value="1">指定节点</el-radio>
                <el-radio :value="2">跳转至固定节点</el-radio>
            </el-radio-group>
            <div v-if="forwardBehavior === 1" style="margin-top: 8px;">
                <span>推进目标节点：</span>
                <el-select v-model="forwardNodeIds" multiple placeholder="请选择目标节点" style="width: 320px;">
                    <el-option v-for="item in availableForwardNodes" :key="item.nodeId"
                        :label="item.nodeName" :value="item.nodeId" />
                </el-select>
            </div>
            <div v-if="forwardBehavior === 2" style="margin-top: 8px;">
                <span>推进目标节点：</span>
                <el-select v-model="forwardFixedNodeId" placeholder="请选择目标节点" style="width: 320px;"
                    :disabled="node?.isFinishApproveNode">
                    <el-option v-for="item in availableForwardNodes" :key="item.nodeId"
                        :label="item.nodeName" :value="item.nodeId" />
                </el-select>
                <p v-if="node?.isFinishApproveNode && availableForwardNodes.length === 0"
                    style="color: #f56c6c; margin-top: 4px;">
                    完成审批节点不能是最后一个审批人节点，请在后面添加审批人节点或调整位置。
                </p>
            </div>
        </div>

        <!-- 同意按钮行为配置 -->
        <div v-if="checkApprovalPageBtns.includes(approvalButtonConf.agree)" class="disagree-back-conf">
            <p class="setting-group-title">同意按钮行为</p>
            <el-radio-group v-model="approveForwardBehavior" @change="onApproveForwardBehaviorChange"
                :disabled="checkApprovalPageBtns.includes(approvalButtonConf.forwardToNode) || node?.isFinishApproveNode || node?.isConditionAdvanceNode || node?.isConditionFinishNode">
                <el-radio :value="0">默认</el-radio>
                <el-radio :value="2">跳转至固定节点</el-radio>
            </el-radio-group>
            <div v-if="approveForwardBehavior === 2" style="margin-top: 8px;">
                <span>同意推进目标节点：</span>
                <el-select v-model="approveForwardFixedNodeId" placeholder="请选择目标节点" style="width: 320px;">
                    <el-option v-for="item in availableForwardNodes" :key="item.nodeId"
                        :label="item.nodeName" :value="item.nodeId" />
                </el-select>
                <p v-if="availableForwardNodes.length === 0"
                    style="color: #f56c6c; margin-top: 4px;">
                    当前节点之后没有可选的审批人节点。
                </p>
            </div>
        </div>

        <p style="margin-top: 16px;">【查看页面】按钮权限显示控制</p>
        <el-checkbox-group class="clear" v-model="checkViewPageBtns">
            <div class="btn-row" v-for="opt in nodeViewPageButtons" :key="opt.value">
                <el-checkbox :value="opt.value" :disabled="opt.type === 'default'"
                    @change="handleCheckedViewButtonsChange(opt.value)">
                    【{{ opt.label }}】
                </el-checkbox>
                <el-popover placement="top" :width="240" trigger="click" popper-class="btn-desc-popover">
                    <template #reference>
                        <el-icon class="btn-help-icon" @click.stop><QuestionFilled /></el-icon>
                    </template>
                    <div>{{ opt.description }}</div>
                </el-popover>
                <el-input class="btn-name-input" v-model="viewButtonCustomNames[opt.value]"
                    maxlength="8" placeholder="自定义名称" size="small"
                    :disabled="!checkViewPageBtns.includes(opt.value)"
                    @input="syncViewPageButtons" />
            </div>
        </el-checkbox-group>
    </div>
</template>
<script setup>
import { ref, watch, computed } from 'vue';
import { approvalPageButtons, nodeViewPageButtons, approvalButtonConf } from '@/utils/antflow/const';
import { QuestionFilled } from '@element-plus/icons-vue';
import { NodeUtils } from '@/utils/antflow/nodeUtils';

const props = defineProps({
    /** 当前节点数据（直接 mutate，drawer 传副本、Zen 传副本树节点） */
    approverConfig: {
        type: Object,
        default: () => ({})
    },
    /** 节点树根节点（计算可选推进/退回节点、自动完成节点目标） */
    rootNode: {
        type: Object,
        default: () => ({})
    },
    /** 可选推进目标节点列表（由父级计算传入，drawer 与推进设置 tab 共享） */
    availableForwardNodes: {
        type: Array,
        default: () => []
    },
    /** 可选退回目标节点列表（由父级计算传入，drawer 与退回设置 tab 共享） */
    availableBackNodes: {
        type: Array,
        default: () => []
    }
});
/** 固定节点单选: 与父级(抽屉推进设置tab/返回值)共享, v-model 双向绑定 */
const forwardFixedNodeId = defineModel('forwardFixedNodeId', { type: [String, Number], default: null });

/** 节点对象别名 */
const node = computed(() => props.approverConfig);

let checkApprovalPageBtns = ref([]);
let checkViewPageBtns = ref([]);
/**按钮自定义名称映射 { [buttonType]: 自定义名称 } */
let buttonCustomNames = ref({});
/**查看页按钮自定义名称映射 { [buttonType]: 自定义名称 } */
let viewButtonCustomNames = ref({});
let afterSignUpWayVisible = computed(() => node.value?.isSignUp == 1);
let approvalBtnSubOption = ref(1);

// 反显加载标志
let isLoading = ref(false);
// 同意推进同步标志
let isApproveForwardSyncing = ref(false);

// ========== 退回按钮行为配置 ==========
let drawBackBehavior = ref(0); // UI radio: 0=无限制, 1=退回发起人, 2=退回上一节点, 3=退回指定节点
let drawBackReturnToSender = ref(false); // "回到退回人" checkbox
let drawBackNodeIds = ref([]); // 退回指定节点时的多选目标

const onDrawBackBehaviorChange = (val) => {
    drawBackReturnToSender.value = false;
    drawBackNodeIds.value = [];
    syncDrawBackToConfig();
};

const syncDrawBackToConfig = () => {
    if (!node.value) return;
    if (isLoading.value) return;
    // 自动退回节点(nodeType=19)/条件退回节点(nodeType=20)的 drawBack 配置由其他机制管理, 此处跳过
    if (node.value.nodeType === 19 || node.value.nodeType === 20 || node.value.nodeType === 21) return;
    let type = 0;
    if (drawBackBehavior.value === 1) {
        type = drawBackReturnToSender.value ? 3 : 2;
    } else if (drawBackBehavior.value === 2) {
        type = 1;
    } else if (drawBackBehavior.value === 3) {
        type = drawBackReturnToSender.value ? 5 : 4;
    }
    node.value.drawBackType = type || null;
    node.value.drawBackNodeIds = (type === 4 || type === 5) ? drawBackNodeIds.value : null;
};
watch(drawBackReturnToSender, () => { syncDrawBackToConfig(); });
watch(drawBackNodeIds, () => { syncDrawBackToConfig(); }, { deep: true });

// ========== 推进按钮行为配置 ==========
let forwardBehavior = ref(0); // UI radio: 0=任意未来节点, 1=指定节点(多选), 2=固定节点(单选)
let forwardNodeIds = ref([]); // 指定节点多选

const onForwardBehaviorChange = (val) => {
    forwardNodeIds.value = [];
    forwardFixedNodeId.value = null;
    syncForwardToConfig();
};

const syncForwardToConfig = () => {
    if (!node.value) return;
    if (isLoading.value) return;
    if (isApproveForwardSyncing.value) return; // 同意推进同步期间跳过, 避免覆盖
    node.value.forwardType = forwardBehavior.value;
    if (forwardBehavior.value === 1) {
        node.value.forwardNodeIds = forwardNodeIds.value;
    } else if (forwardBehavior.value === 2) {
        node.value.forwardNodeIds = forwardFixedNodeId.value ? [forwardFixedNodeId.value] : [];
    } else {
        node.value.forwardNodeIds = null;
    }
};
watch(forwardNodeIds, () => { syncForwardToConfig(); }, { deep: true });
watch(forwardFixedNodeId, () => { syncForwardToConfig(); });

// ========== 同意按钮行为配置 ==========
let approveForwardBehavior = ref(0); // UI radio: 0=默认, 2=固定节点(单选)
let approveForwardFixedNodeId = ref(null); // 固定节点单选

const onApproveForwardBehaviorChange = (val) => {
    approveForwardFixedNodeId.value = null;
    syncApproveForwardToConfig();
};

const syncApproveForwardToConfig = () => {
    if (!node.value) return;
    if (isLoading.value) return;
    if (!node.value.labelList) {
        node.value.labelList = [];
    }
    const ensureLabel = (labelValue, labelName) => {
        const exists = node.value.labelList.some(l => l && l.labelValue === labelValue);
        if (!exists) {
            node.value.labelList.push({ labelValue, labelName });
        }
    };
    const removeLabel = (labelValue) => {
        node.value.labelList = node.value.labelList.filter(l => !(l && l.labelValue === labelValue));
    };

    if (approveForwardBehavior.value === 2) {
        node.value.isApproveForwardNode = true;
        node.value.forwardType = 2;
        node.value.forwardNodeIds = approveForwardFixedNodeId.value ? [approveForwardFixedNodeId.value] : [];
        ensureLabel('approve_forward_node', '同意推进节点');
        isApproveForwardSyncing.value = true;
        forwardBehavior.value = 0;
        forwardNodeIds.value = [];
        forwardFixedNodeId.value = null;
        const fwdIdx = checkApprovalPageBtns.value.indexOf(approvalButtonConf.forwardToNode);
        if (fwdIdx !== -1) {
            checkApprovalPageBtns.value.splice(fwdIdx, 1);
        }
        setTimeout(() => { isApproveForwardSyncing.value = false; }, 0);
    } else {
        if (node.value.isApproveForwardNode) {
            node.value.forwardType = null;
            node.value.forwardNodeIds = null;
        }
        node.value.isApproveForwardNode = false;
        removeLabel('approve_forward_node');
    }
};
watch(approveForwardFixedNodeId, () => { syncApproveForwardToConfig(); });

/**加载同意按钮行为配置(反显) */
const loadApproveForwardConfig = (nodeData) => {
    const hasLabel = node.value?.labelList
        && node.value.labelList.some(l => l && l.labelValue === 'approve_forward_node');
    if (hasLabel || node.value?.isApproveForwardNode) {
        approveForwardBehavior.value = 2;
        approveForwardFixedNodeId.value = (nodeData?.forwardNodeIds && nodeData.forwardNodeIds.length > 0)
            ? nodeData.forwardNodeIds[0] : null;
    } else {
        approveForwardBehavior.value = 0;
        approveForwardFixedNodeId.value = null;
    }
};

/**加载推进按钮行为配置(反显) */
const loadForwardConfig = (nodeData) => {
    // 自动完成节点: 强制固定节点模式, 自动填充最后一个审批人节点(不可编辑, 同完成审批)
    if (node.value?.isAutoCompleteNode) {
        forwardBehavior.value = 2;
        forwardNodeIds.value = [];
        const lastNode = NodeUtils.findLastApproveNode(props.rootNode, node.value?.nodeId);
        if (lastNode) {
            forwardFixedNodeId.value = lastNode.nodeId;
            node.value.forwardNodeIds = [lastNode.nodeId];
        } else {
            forwardFixedNodeId.value = null;
            node.value.forwardNodeIds = [];
        }
        return;
    }
    // 条件完成节点: 强制固定节点模式, 目标自动为最后一个审批人节点(不可编辑, 同完成审批/自动完成)
    if (node.value?.isConditionFinishNode) {
        forwardBehavior.value = 2;
        forwardNodeIds.value = [];
        const lastNode = NodeUtils.findLastApproveNode(props.rootNode, node.value?.nodeId);
        if (lastNode) {
            forwardFixedNodeId.value = lastNode.nodeId;
            node.value.forwardNodeIds = [lastNode.nodeId];
        } else {
            forwardFixedNodeId.value = null;
            node.value.forwardNodeIds = [];
        }
        return;
    }
    // 条件推进节点: 强制固定节点模式(forwardType=2), 满足条件时自动推进到该固定目标
    if (node.value?.isConditionAdvanceNode) {
        forwardBehavior.value = 2;
        forwardNodeIds.value = [];
        forwardFixedNodeId.value = (nodeData?.forwardNodeIds && nodeData.forwardNodeIds.length > 0)
            ? nodeData.forwardNodeIds[0] : null;
        return;
    }
    // 自动推进节点(nodeType=18)强制固定节点模式(forwardType=2)
    if (node.value?.nodeType === 18) {
        forwardBehavior.value = 2;
        forwardNodeIds.value = [];
        forwardFixedNodeId.value = (nodeData?.forwardNodeIds && nodeData.forwardNodeIds.length > 0)
            ? nodeData.forwardNodeIds[0] : null;
        return;
    }
    // 完成审批节点: 强制固定节点模式, 自动填充最后一个审批人节点
    if (node.value?.isFinishApproveNode) {
        forwardBehavior.value = 2;
        forwardNodeIds.value = [];
        const lastNode = NodeUtils.findLastApproveNode(props.rootNode, node.value?.nodeId);
        if (lastNode) {
            forwardFixedNodeId.value = lastNode.nodeId;
            node.value.forwardNodeIds = [lastNode.nodeId];
        } else {
            forwardFixedNodeId.value = null;
            node.value.forwardNodeIds = [];
        }
        return;
    }
    // 同意推进节点: 推进按钮行为区显示为默认, 不反显 forwardType
    const hasApproveForwardLabel = node.value?.labelList
        && node.value.labelList.some(l => l && l.labelValue === 'approve_forward_node');
    if (hasApproveForwardLabel) {
        forwardBehavior.value = 0;
        forwardNodeIds.value = [];
        forwardFixedNodeId.value = null;
        return;
    }
    const ft = nodeData?.forwardType;
    if (ft === 0) {
        forwardBehavior.value = 0;
        forwardNodeIds.value = [];
        forwardFixedNodeId.value = null;
    } else if (ft === 1) {
        forwardBehavior.value = 1;
        forwardNodeIds.value = nodeData.forwardNodeIds || [];
        forwardFixedNodeId.value = null;
    } else if (ft === 2) {
        forwardBehavior.value = 2;
        forwardNodeIds.value = [];
        forwardFixedNodeId.value = (nodeData.forwardNodeIds && nodeData.forwardNodeIds.length > 0) ? nodeData.forwardNodeIds[0] : null;
    } else {
        forwardBehavior.value = 0;
        forwardNodeIds.value = [];
        forwardFixedNodeId.value = null;
    }
};

/**加载退回按钮行为配置(反显) */
const loadDrawBackConfig = (nodeData) => {
    const bt = nodeData?.drawBackType;
    if (bt === 2 || bt === 3) {
        drawBackBehavior.value = 1;
        drawBackReturnToSender.value = (bt === 3);
        drawBackNodeIds.value = [];
    } else if (bt === 1) {
        drawBackBehavior.value = 2;
        drawBackReturnToSender.value = false;
        drawBackNodeIds.value = [];
    } else if (bt === 4 || bt === 5) {
        drawBackBehavior.value = 3;
        drawBackReturnToSender.value = (bt === 5);
        drawBackNodeIds.value = nodeData.drawBackNodeIds || [];
    } else {
        drawBackBehavior.value = 0;
        drawBackReturnToSender.value = false;
        drawBackNodeIds.value = [];
    }
};

// ========== 不同意退回配置 ==========
let disagreeBackType = ref(0);
let disagreeBackToNodeId = ref(null);
const onDisagreeBackTypeChange = (val) => {
    if (val === 0) {
        disagreeBackToNodeId.value = null;
        node.value.disagreeBackType = null;
        node.value.disagreeBackToNodeId = null;
    } else if (val === 2) {
        disagreeBackToNodeId.value = null;
        node.value.disagreeBackType = val;
        node.value.disagreeBackToNodeId = null;
    } else {
        node.value.disagreeBackType = val;
    }
};
const syncDisagreeBackToConfig = () => {
    if (!node.value) return;
    if (disagreeBackType.value === 4 || disagreeBackType.value === 5) {
        node.value.disagreeBackType = disagreeBackType.value;
        node.value.disagreeBackToNodeId = disagreeBackToNodeId.value;
    } else if (disagreeBackType.value === 2) {
        node.value.disagreeBackType = 2;
        node.value.disagreeBackToNodeId = null;
    } else {
        node.value.disagreeBackType = null;
        node.value.disagreeBackToNodeId = null;
    }
};
watch(disagreeBackToNodeId, () => { syncDisagreeBackToConfig(); });

/**处理权限按钮变更事件 */
const handleCheckedButtonsChange = () => {
    const isAddStep = checkApprovalPageBtns.value.indexOf(19);
    node.value.isSignUp = isAddStep >= 0 ? 1 : 0;
    syncApprovalPageButtons();
}

/**从后端返回的 approvalPage(对象数组)解析出勾选值与自定义名称 */
const loadApprovalPageButtons = (approvalPageList) => {
    const list = approvalPageList || [];
    checkApprovalPageBtns.value = list.map(item => typeof item === 'number' ? item : item.buttonType);
    //协助节点(nodeType=17):强制包含协助按钮(41)
    if (node.value && node.value.nodeType === 17 && !checkApprovalPageBtns.value.includes(41)) {
        checkApprovalPageBtns.value.push(41);
    }
    const names = {};
    approvalPageButtons.forEach(opt => {
        const item = list.find(i => typeof i === 'object' && i.buttonType === opt.value);
        names[opt.value] = (item && item.buttonName && item.buttonName !== opt.label) ? item.buttonName : '';
    });
    buttonCustomNames.value = names;
    //同步isSignUp状态:加批按钮(19)未勾选时确保isSignUp=0,防止选项区异常显示
    //条件自动加批节点例外: 屏蔽人工加批按钮但仍需 isSignUp=1 部署 signUp 子元素(自动加批用)
    if (node.value && !node.value.isConditionAutoSignUpNode && !checkApprovalPageBtns.value.includes(approvalButtonConf.addApproval)) {
        node.value.isSignUp = 0;
    }
    syncApprovalPageButtons();
}

/**加载不同意退回配置(反显) */
const loadDisagreeBackConfig = (nodeData) => {
    const bt = nodeData?.disagreeBackType;
    if (bt === 2 || bt === 4 || bt === 5) {
        disagreeBackType.value = bt;
        disagreeBackToNodeId.value = (bt === 4 || bt === 5) ? (nodeData.disagreeBackToNodeId || null) : null;
    } else {
        disagreeBackType.value = 0;
        disagreeBackToNodeId.value = null;
    }
}

/**将勾选状态与自定义名称同步到 node.buttons.approvalPage(对象数组) */
const syncApprovalPageButtons = () => {
    if (!node.value) return;
    if (!node.value.buttons) node.value.buttons = {};
    node.value.buttons.approvalPage = checkApprovalPageBtns.value.map(bt => ({
        buttonType: bt,
        buttonName: buttonCustomNames.value[bt] || ''
    }));
}

/**处理查看页权限按钮变更事件 */
const handleCheckedViewButtonsChange = () => {
    syncViewPageButtons();
}

/**从后端返回的 viewPage(对象数组)解析出勾选值与自定义名称 */
const loadViewPageButtons = (viewPageList) => {
    const list = viewPageList || [];
    checkViewPageBtns.value = list.map(item => typeof item === 'number' ? item : item.buttonType);
    const names = {};
    nodeViewPageButtons.forEach(opt => {
        const item = list.find(i => typeof i === 'object' && i.buttonType === opt.value);
        names[opt.value] = (item && item.buttonName && item.buttonName !== opt.label) ? item.buttonName : '';
    });
    viewButtonCustomNames.value = names;
    syncViewPageButtons();
}

/**将勾选状态与自定义名称同步到 node.buttons.viewPage(对象数组) */
const syncViewPageButtons = () => {
    if (!node.value) return;
    if (!node.value.buttons) node.value.buttons = {};
    node.value.buttons.viewPage = checkViewPageBtns.value.map(bt => ({
        buttonType: bt,
        buttonName: viewButtonCustomNames.value[bt] || ''
    }));
}

/**处理加批按钮 子操作 */
const handleApprovalBtnSubOption = (val) => {
    node.value.property.afterSignUpWay = val && val == 9 ? 1 : 2;
    node.value.property.signUpType = val && val == 9 ? 1 : val;
}

/**仲裁签 signType 切换处理: 调整按钮配置和通过比例 */
watch(() => node.value?.signType, (newVal, oldVal) => {
    if (!node.value) return;
    if (newVal == 4) {
        if (!node.value.property) {
            node.value.property = {};
        }
        if (!node.value.property.arbitrationRatio) {
            node.value.property.arbitrationRatio = 100;
        }
        const noAgreeIdx = checkApprovalPageBtns.value.indexOf(approvalButtonConf.noAgree);
        if (noAgreeIdx >= 0) {
            checkApprovalPageBtns.value.splice(noAgreeIdx, 1);
        }
        if (!checkApprovalPageBtns.value.includes(approvalButtonConf.oppose)) {
            checkApprovalPageBtns.value.push(approvalButtonConf.oppose);
        }
        buttonCustomNames.value[approvalButtonConf.agree] = '赞成';
        syncApprovalPageButtons();
    } else if (oldVal == 4) {
        buttonCustomNames.value[approvalButtonConf.agree] = '';
        syncApprovalPageButtons();
    }
});

/** 节点切换时统一反显所有按钮配置 */
watch(() => props.approverConfig, (val) => {
    if (!val) return;
    isLoading.value = true;
    try {
        loadApprovalPageButtons(val.buttons?.approvalPage);
        loadViewPageButtons(val.buttons?.viewPage);
        loadDisagreeBackConfig(val);
        loadDrawBackConfig(val);
        loadForwardConfig(val);
        loadApproveForwardConfig(val);
    } finally {
        setTimeout(() => { isLoading.value = false; }, 0);
    }
}, { immediate: true });

defineExpose({
    forwardFixedNodeId,
    forwardNodeIds,
    forwardBehavior
});
</script>
<style scoped lang="scss">
.btn-row {
    display: flex;
    align-items: center;
    margin: 6px 0;
    width: 100%;
    gap: 8px;
}

.btn-help-icon {
    color: #909399;
    cursor: pointer;
    font-size: 16px;
    flex-shrink: 0;
}

.btn-help-icon:hover {
    color: #409eff;
}

.btn-name-input {
    margin-left: auto;
    width: 160px;
    flex-shrink: 0;
}

.auth-btn {
    margin-top: 6px;
    width: 95%;
    height: 45px;
}

.disagree-back-conf {
    margin-top: 12px;
    padding: 10px;
    background: #f9f9f9;
    border-radius: 4px;
}

.setting-group-title {
    margin: 0 0 10px 0;
    font-size: 14px;
    font-weight: 600;
}
</style>
