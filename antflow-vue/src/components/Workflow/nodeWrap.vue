<!--
 * @Date: 2022-09-21 14:41:53
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2023-05-24 15:20:24
 * @FilePath: /ant-flow/src/components/nodeWrap.vue
-->
<template>
    <!--审批人，抄送人分支-->
    <div class="node-wrap" v-if="nodeConfig.nodeType != 2 && nodeConfig.nodeType != 7">
        <div class="node-wrap-box"
            :class="(nodeConfig.nodeType == 1 ? 'start-node ' : '') + (isTried && nodeConfig.error ? 'active error' : '')">
            <div class="title" :style="`background: rgb(${titleBgColor});`">
                <span v-if="nodeConfig.nodeType == 1">{{ nodeConfig.nodeName }}</span>
                <template v-else>
                    <svg-icon icon-class="copy-user" class="iconfont" v-if="nodeConfig.nodeType == 6" />
                    <svg-icon icon-class="copy-user" class="iconfont" v-else-if="nodeConfig.nodeType == 8 || nodeConfig.nodeType == 13" />
                    <svg-icon icon-class="assist" class="iconfont" v-else-if="nodeConfig.nodeType == 17" />
                    <svg-icon icon-class="auto-finish" class="iconfont" v-else-if="nodeConfig.nodeType == 18 && Array.isArray(nodeConfig.labelList) && nodeConfig.labelList.some(l => l.labelValue === 'auto_complete_node')" />
                    <svg-icon icon-class="auto-drive-ahead" class="iconfont" v-else-if="nodeConfig.nodeType == 18" />
                    <svg-icon icon-class="auto-drive-to-starter" class="iconfont" v-else-if="nodeConfig.nodeType == 19 && nodeConfig.drawBackType == 2" />
                    <svg-icon icon-class="auto-drive-back" class="iconfont" v-else-if="nodeConfig.nodeType == 19" />
                    <svg-icon icon-class="conditional-drive-back" class="iconfont" v-else-if="nodeConfig.nodeType == 20" />
                                        <svg-icon icon-class="conditional-drive-to-starter" class="iconfont" v-else-if="nodeConfig.nodeType == 21" />
                    <svg-icon icon-class="finish-process" class="iconfont" v-else-if="nodeConfig.nodeType == 4 && Array.isArray(nodeConfig.labelList) && nodeConfig.labelList.some(l => l.labelValue === 'finish_approve_node')" />
                    <svg-icon icon-class="approver-drive-ahead" class="iconfont" v-else-if="nodeConfig.nodeType == 4 && Array.isArray(nodeConfig.labelList) && nodeConfig.labelList.some(l => l.labelValue === 'approve_forward_node')" />
                    <svg-icon icon-class="approver-drive-ahead" class="iconfont" v-else-if="nodeConfig.nodeType == 4 && hasButtonType(nodeConfig.buttons?.approvalPage, 42)" />
                    <svg-icon icon-class="drive-back" class="iconfont" v-else-if="nodeConfig.nodeType == 4 && Array.isArray(nodeConfig.labelList) && nodeConfig.labelList.some(l => l.labelValue === 'af_syslabel_disagree_back')" />
                    <svg-icon icon-class="condition-finish-process" class="iconfont" v-else-if="nodeConfig.nodeType == 12 && Array.isArray(nodeConfig.labelList) && nodeConfig.labelList.some(l => l.labelValue === 'condition_finish_node')" />
                    <svg-icon icon-class="conditional-drive-ahead" class="iconfont" v-else-if="nodeConfig.nodeType == 12 && Array.isArray(nodeConfig.labelList) && nodeConfig.labelList.some(l => l.labelValue === 'condition_advance_node')" />
                    <svg-icon icon-class="approve" class="iconfont" v-else />
                    <input v-if="isInput" type="text" class="fd-input editable-title-input" @blur="blurEvent()"
                        @focus="$event.currentTarget.select()" v-focus v-model="nodeConfig.nodeName"
                        :placeholder="defaultText" />
                    <span v-else class="editable-title" @click="clickEvent()">{{ nodeConfig.nodeName }}</span>
                    <i class="anticon anticon-close close" @click="delNode()"></i>
                    <span class="node-badges">
                        <template v-for="(b, idx) in visibleBadges" :key="b.key">
                            <i v-if="b.type === 'anticon'" :class="b.cls"></i>
                            <span v-else-if="b.type === 'num'" :class="[b.cls, (b.num + '').length > 1 ? 'wide' : '']" :title="b.tip">{{ b.num }}</span>
                            <svg-icon v-else :icon-class="b.icon" :class="b.cls" />
                        </template>
                        <svg-icon v-if="showMoreBadges" icon-class="more-up" class="node-badge-icon" />
                    </span>
                </template>
            </div>
            <div class="content" @click="setNodeInfo">
                <div class="text">
                    <span class="placeholder" v-if="!showText">请选择{{ defaultText }}</span>
                    {{ showText }}
                </div>
                <i class="anticon anticon-right arrow"></i>
            </div>
            <div class="error_tip" v-if="isTried && nodeConfig.error">
                <i class="anticon anticon-exclamation-circle"></i>
            </div>
        </div>
        <addNode v-model:childNodeP="nodeConfig.childNode" />
    </div>
    <!--条件分支-->
    <div class="branch-wrap" v-if="nodeConfig.nodeType == 2">
        <div class="branch-box-wrap">
            <div class="branch-box">
                <button class="add-branch" @click="addTerm">添加条件</button>
                <div class="col-box" v-for="(item, index) in nodeConfig.conditionNodes" :key="index">
                    <div class="condition-node">
                        <div class="condition-node-box">
                            <div class="auto-judge" :class="isTried && item.error ? 'error active' : ''">
                                <div class="sort-left" v-if="index != 0" @click="arrTransfer(index, -1)">&lt;</div>
                                <div class="title-wrapper">
                                    <svg-icon icon-class="dynamic-condition" class="iconfont"
                                        v-if="nodeConfig.isDynamicCondition == true" />
                                    <svg-icon icon-class="parallel-condition" class="iconfont"
                                        v-else-if="nodeConfig.isParallel == true" />
                                    <svg-icon icon-class="condition" class="iconfont" v-else />
                                    <input v-if="isInputList[index]" type="text" class="fd-input editable-title-input"
                                        @blur="blurEvent(index)" @focus="$event.currentTarget.select()" v-focus
                                        v-model="item.nodeName" />
                                    <span v-else class="editable-title" @click="clickEvent(index)">{{ item.nodeName
                                    }}</span>
                                    <span class="priority-title" @click="setNodeInfo(item.priorityLevel)">优先级{{
                                        item.priorityLevel }}</span>
                                    <i class="anticon anticon-close close" @click="delTerm(index)"></i>
                                </div>
                                <div class="sort-right" v-if="index != nodeConfig.conditionNodes.length - 1"
                                    @click="arrTransfer(index)">&gt;</div>
                                <div class="content" @click="setNodeInfo(item.priorityLevel)">
                                    {{ item.nodeDisplayName || $func.conditionStr(nodeConfig, index) }}</div>
                                <div class="error_tip" v-if="isTried && item.error">
                                    <i class="anticon anticon-exclamation-circle"></i>
                                </div>
                            </div>
                            <addNode v-model:childNodeP="item.childNode" />
                        </div>
                    </div>
                    <nodeWrap v-if="item.childNode" v-model:nodeConfig="item.childNode" />
                    <template v-if="index == 0">
                        <div class="top-left-cover-line"></div>
                        <div class="bottom-left-cover-line"></div>
                    </template>
                    <template v-if="index == nodeConfig.conditionNodes.length - 1">
                        <div class="top-right-cover-line"></div>
                        <div class="bottom-right-cover-line"></div>
                    </template>
                </div>
            </div>
            <addNode v-model:childNodeP="nodeConfig.childNode" />
        </div>
    </div>
    <!--并行审批分支-->
    <div class="branch-wrap" v-if="nodeConfig.nodeType == 7">
        <div class="branch-box-wrap">
            <div class="branch-box">
                <button class="add-branch" @click="addTerm">添加审批节点</button>
                <div class="col-box" v-for="(item, index) in nodeConfig.parallelNodes" :key="index">
                    <div class="condition-node">
                        <div class="condition-node-box">
                            <div class="node-wrap-box" :class="isTried && item.error ? 'error active' : ''">
                                <div class="title" :style="`background: rgb(${bgColors[4]});`">
                                    <svg-icon icon-class="approve" class="iconfont" />
                                    <input v-if="isInputList[index]" type="text" class="fd-input editable-title-input"
                                        @blur="blurEvent(index)" @focus="$event.currentTarget.select()" v-focus
                                        v-model="item.nodeName" />
                                    <span v-else class="editable-title" @click="clickEvent(index)">{{ item.nodeName
                                    }}</span>
                                    <i class="anticon anticon-close close" @click="delTerm(index)"></i>
                                    <span class="node-badges">
                                        <template v-for="(b, idx) in getBadgeList(item).list" :key="b.key">
                                            <i v-if="b.type === 'anticon'" :class="b.cls"></i>
                                            <span v-else-if="b.type === 'num'" :class="[b.cls, (b.num + '').length > 1 ? 'wide' : '']" :title="b.tip">{{ b.num }}</span>
                                            <svg-icon v-else :icon-class="b.icon" :class="b.cls" />
                                        </template>
                                        <svg-icon v-if="getBadgeList(item).more" icon-class="more-up" class="node-badge-icon" />
                                    </span>
                                </div>

                                <div class="content" @click="setNodeInfo(index)">
                                    <div class="text">
                                        <span class="placeholder" v-if="!item.nodeDisplayName">请选择{{ defaultText
                                        }}</span>
                                        {{ item.nodeDisplayName }}
                                    </div>
                                    <i class="anticon anticon-right arrow"></i>
                                </div>
                                <div class="error_tip" v-if="isTried && item.error">
                                    <i class="anticon anticon-exclamation-circle"></i>
                                </div>
                            </div>
                            <addNode v-model:childNodeP="item.childNode" />
                        </div>
                    </div>
                    <nodeWrap v-if="item.childNode" v-model:nodeConfig="item.childNode" />
                    <template v-if="index == 0">
                        <div class="top-left-cover-line"></div>
                        <div class="bottom-left-cover-line"></div>
                    </template>
                    <template v-if="index == nodeConfig.parallelNodes.length - 1">
                        <div class="top-right-cover-line"></div>
                        <div class="bottom-right-cover-line"></div>
                    </template>
                </div>
            </div>
            <addNode v-model:childNodeP="nodeConfig.childNode" />
        </div>
    </div>
    <nodeWrap v-if="nodeConfig.childNode" v-model:nodeConfig="nodeConfig.childNode" />
</template>
<script setup>
import { onMounted, ref, watch, getCurrentInstance, computed, inject } from "vue";
import $func from "@/utils/antflow/index";
import { useStore } from '@/store/modules/workflow'
import { bgColors, placeholderList, setTypes, PICK_CONDITION_COLOR, FORWARD_APPROVE_COLOR, FINISH_APPROVE_COLOR, AUTO_COMPLETE_COLOR, CONDITION_ADVANCE_COLOR, CONDITION_FINISH_COLOR, BACK_APPROVE_COLOR } from '@/utils/antflow/const'
import { NodeUtils } from '@/utils/antflow/nodeUtils'
const { proxy } = getCurrentInstance();
let _uid = getCurrentInstance().uid;
let props = defineProps({
    nodeConfig: {
        type: Object,
        default: () => ({}),
    },
    flowPermission: {
        type: Object,
        default: () => [],
    },
});

let isInputList = ref([]);
let isInput = ref(false);

let emits = defineEmits(["update:flowPermission", "update:nodeConfig"]);
let store = useStore();
let {
    setPromoter,
    setPromoterConfig,
    setApprover,
    setCopyer,
    setCondition,
    setFlowPermission,
    setApproverConfig,
    setCopyerConfig,
    setConditionsConfig,
    setCopyerV2,
    setCopyerConfigV2,
    setAutoNode,
    setAutoNodeConfig,
} = store;
const rootNode = inject('rootNode', ref({}));
let isTried = computed(() => store.isTried)
let flowPermission1 = computed(() => store.flowPermission1)
let promoterConfig1 = computed(() => store.promoterConfig)
let approverConfig1 = computed(() => store.approverConfig1)
let copyerConfig1 = computed(() => store.copyerConfig1)
let conditionsConfig1 = computed(() => store.conditionsConfig1)
let copyerConfigV2 = computed(() => store.copyerConfigV2)
let autoNodeConfig1 = computed(() => store.autoNodeConfig1)
let defaultText = computed(() => {
    return placeholderList[props.nodeConfig.nodeType]
});

let noticeIconShow = computed(() => {
    return !proxy.isEmptyArray(props.nodeConfig.templateVos);
});

/**是否配置了超时提醒: 标准时效非空且选择了提醒天数 */
const hasTimeoutRemind = (n) => {
    const vo = n && n.approveRemindVo;
    return !!vo && vo.standardMinutes != null && !proxy.isEmptyArray(vo.days);
};
let timeoutNoticeIconShow = computed(() => hasTimeoutRemind(props.nodeConfig));

let labelIconShow = computed(() => {
    return !proxy.isEmptyArray(props.nodeConfig.labelList);
});

/**表单权限角标: 任一字段设置为可编辑(E) */
let formPermEditableShow = computed(() =>
    Array.isArray(props.nodeConfig.lfFieldControlVOs)
    && props.nodeConfig.lfFieldControlVOs.some(v => v && v.perm === 'E')
);
/**表单权限角标: 任一字段设置为隐藏(H) */
let formPermHiddenShow = computed(() =>
    Array.isArray(props.nodeConfig.lfFieldControlVOs)
    && props.nodeConfig.lfFieldControlVOs.some(v => v && v.perm === 'H')
);
/**加批角标: 审批页按钮权限选中了加批(buttonType=19) */
let signUpShow = computed(() => hasButtonType(props.nodeConfig.buttons?.approvalPage, 19));
/**转办角标: 审批页按钮权限选中了转办(buttonType=21) */
let transferShow = computed(() => hasButtonType(props.nodeConfig.buttons?.approvalPage, 21));
/**退回角标: 审批页选中退回按钮(18), 或不同意按钮行为非默认(结束流程), 即退回语义(disagreeBackType 为 2/4/5) */
let backShow = computed(() =>
    hasButtonType(props.nodeConfig.buttons?.approvalPage, 18)
    || !!props.nodeConfig.disagreeBackType
);
/**推进角标: 选中推进按钮(42), 或同意按钮行为非默认(同意推进, label approve_forward_node / isApproveForwardNode) */
let jumpAheadShow = computed(() =>
    hasButtonType(props.nodeConfig.buttons?.approvalPage, 42)
    || (Array.isArray(props.nodeConfig.labelList) && props.nodeConfig.labelList.some(l => l && l.labelValue === 'approve_forward_node'))
    || props.nodeConfig.isApproveForwardNode === true
);
/**协助角标: 审批页按钮权限选中了协助(buttonType=41) */
let assistShow = computed(() => hasButtonType(props.nodeConfig.buttons?.approvalPage, 41));

/**审批方式角标图标: 1会签/2或签/3顺序会签/4仲裁签, 无则空 */
let signModeIcon = computed(() => {
    const st = props.nodeConfig.signType;
    if (st == 1) return 'all-sign';
    if (st == 2) return 'or-sign';
    if (st == 3) return 'all-sign-in-order';
    if (st == 4) return 'arb-sign';
    return '';
});

/**审批人类型数字徽章: node.setType 有值则显示对应数字(见 NodePropertyEnum/setTypes) */
let setTypeNum = computed(() => {
    const t = props.nodeConfig.setType;
    return t ? t : '';
});
/**审批人类型名称(悬停提示) */
let setTypeTip = computed(() => {
    const t = setTypes.find(s => s.value === props.nodeConfig.setType);
    return t ? t.label : '';
});

/**额外审批角标图标: 额外增加(propertyType=1)/额外排除(propertyType=2), 两者都有时显示合并图标 */
let extraSignIcon = computed(() => {
    const list = props.nodeConfig.property && Array.isArray(props.nodeConfig.property.additionalSignInfoList)
        ? props.nodeConfig.property.additionalSignInfoList : [];
    const hasAdd = list.some(a => a && a.propertyType == 1 && a.signInfos && a.signInfos.length > 0);
    const hasExclude = list.some(a => a && a.propertyType == 2 && a.signInfos && a.signInfos.length > 0);
    if (hasAdd && hasExclude) return 'extra-add-and-minus';
    if (hasAdd) return 'extra-add-sign';
    if (hasExclude) return 'extra-minus-sign';
    return '';
});

/**全部可见角标配置列表(供 node-badges v-for 渲染) */
let badgeList = computed(() => {
    const items = [
        { key: 'set-type', show: setTypeNum.value !== '', type: 'num', num: setTypeNum.value, tip: setTypeTip.value, cls: 'node-badge-num' },
        { key: 'sign-mode', show: !!signModeIcon.value, type: 'svg', icon: signModeIcon.value, cls: 'node-badge-icon' },
        { key: 'extra-sign', show: !!extraSignIcon.value, type: 'svg', icon: extraSignIcon.value, cls: 'node-badge-icon' },
        { key: 'notice', show: noticeIconShow.value, type: 'anticon', cls: 'anticon anticon-notice notice' },
        { key: 'timeout', show: timeoutNoticeIconShow.value, type: 'svg', icon: 'time', cls: 'timeout-notice' },
        { key: 'label', show: labelIconShow.value, type: 'anticon', cls: 'anticon anticon-tag label-icon' },
        { key: 'edit', show: formPermEditableShow.value, type: 'svg', icon: 'edit', cls: 'node-badge-icon' },
        { key: 'eye', show: formPermHiddenShow.value, type: 'svg', icon: 'eye', cls: 'node-badge-icon' },
        { key: 'add-sign', show: signUpShow.value, type: 'svg', icon: 'add-sign', cls: 'node-badge-icon' },
        { key: 'transfer', show: transferShow.value, type: 'svg', icon: 'transfer-assignee', cls: 'node-badge-icon' },
        { key: 'back', show: backShow.value, type: 'svg', icon: 'process-back', cls: 'node-badge-icon' },
        { key: 'jump', show: jumpAheadShow.value, type: 'svg', icon: 'process-jump-ahead', cls: 'node-badge-icon' },
        { key: 'assist', show: assistShow.value, type: 'svg', icon: 'assist', cls: 'node-badge-icon' },
    ].filter(i => i.show);
    return items;
});
/**实际显示角标: 总数超过 6 个时只显示前 5 个, 多余部分折叠进 more 图标 */
let visibleBadges = computed(() => {
    const list = badgeList.value;
    return list.length > 6 ? list.slice(0, 5) : list;
});
/**角标总数超过 6 个时显示 more-up 图标 */
let showMoreBadges = computed(() => badgeList.value.length > 6);

/**按钮数组是否包含指定 buttonType: 兼容后端数字数组 [3,4,19] 与前端对象数组 [{buttonType:19}] */
const hasButtonType = (btnList, type) =>
    Array.isArray(btnList) && btnList.some(b => b === type || (b && b.buttonType == type));

/**并行分支子节点: 表单权限任一可编辑(E) */
const hasFormPermEditable = (n) =>
    Array.isArray(n && n.lfFieldControlVOs)
    && n.lfFieldControlVOs.some(v => v && v.perm === 'E');
/**并行分支子节点: 表单权限任一隐藏(H) */
const hasFormPermHidden = (n) =>
    Array.isArray(n && n.lfFieldControlVOs)
    && n.lfFieldControlVOs.some(v => v && v.perm === 'H');
/**并行分支子节点: 审批页按钮选中加批(buttonType=19) */
const hasSignUpButton = (n) => hasButtonType(n && n.buttons?.approvalPage, 19);
/**并行分支子节点: 审批页按钮选中转办(buttonType=21) */
const hasTransferButton = (n) => hasButtonType(n && n.buttons?.approvalPage, 21);
/**并行分支子节点: 退回语义(退回按钮 18 或不同意行为非默认) */
const hasBackSemantics = (n) =>
    hasButtonType(n && n.buttons?.approvalPage, 18)
    || !!(n && n.disagreeBackType);
/**并行分支子节点: 推进语义(推进按钮 42 或同意行为非默认) */
const hasJumpAhead = (n) =>
    hasButtonType(n && n.buttons?.approvalPage, 42)
    || (Array.isArray(n && n.labelList) && n.labelList.some(l => l && l.labelValue === 'approve_forward_node'))
    || (n && n.isApproveForwardNode === true);
/**并行分支子节点: 审批页按钮选中协助(buttonType=41) */
const hasAssistButton = (n) => hasButtonType(n && n.buttons?.approvalPage, 41);

/**并行分支子节点: 审批方式图标(1会签/2或签/3顺序会签/4仲裁签, 无则空) */
const getSignModeIcon = (n) => {
    const st = n && n.signType;
    if (st == 1) return 'all-sign';
    if (st == 2) return 'or-sign';
    if (st == 3) return 'all-sign-in-order';
    if (st == 4) return 'arb-sign';
    return '';
};

/**并行分支子节点: 额外审批图标(增加/排除, 双有合并) */
const getExtraSignIcon = (n) => {
    const list = n && n.property && Array.isArray(n.property.additionalSignInfoList)
        ? n.property.additionalSignInfoList : [];
    const hasAdd = list.some(a => a && a.propertyType == 1 && a.signInfos && a.signInfos.length > 0);
    const hasExclude = list.some(a => a && a.propertyType == 2 && a.signInfos && a.signInfos.length > 0);
    if (hasAdd && hasExclude) return 'extra-add-and-minus';
    if (hasAdd) return 'extra-add-sign';
    if (hasExclude) return 'extra-minus-sign';
    return '';
};

/**并行分支子节点: 审批人类型数字徽章(有值返回数字) */
const getSetTypeNum = (n) => (n && n.setType ? n.setType : '');
/**并行分支子节点: 审批人类型名称(悬停提示) */
const getSetTypeTip = (n) => {
    const t = setTypes.find(s => s.value === (n && n.setType));
    return t ? t.label : '';
};

/**并行分支子节点: 角标渲染列表(超过 6 个截断前 5 个 + more) */
const getBadgeList = (n) => {
    const items = [
        { key: 'set-type', show: getSetTypeNum(n) !== '', type: 'num', num: getSetTypeNum(n), tip: getSetTypeTip(n), cls: 'node-badge-num' },
        { key: 'sign-mode', show: !!getSignModeIcon(n), type: 'svg', icon: getSignModeIcon(n), cls: 'node-badge-icon' },
        { key: 'extra-sign', show: !!getExtraSignIcon(n), type: 'svg', icon: getExtraSignIcon(n), cls: 'node-badge-icon' },
        { key: 'notice', show: !!(n && n.templateVos && n.templateVos.length > 0), type: 'anticon', cls: 'anticon anticon-notice notice' },
        { key: 'timeout', show: hasTimeoutRemind(n), type: 'svg', icon: 'time', cls: 'timeout-notice' },
        { key: 'label', show: !!(n && n.labelList && n.labelList.length > 0), type: 'anticon', cls: 'anticon anticon-tag label-icon' },
        { key: 'edit', show: hasFormPermEditable(n), type: 'svg', icon: 'edit', cls: 'node-badge-icon' },
        { key: 'eye', show: hasFormPermHidden(n), type: 'svg', icon: 'eye', cls: 'node-badge-icon' },
        { key: 'add-sign', show: hasSignUpButton(n), type: 'svg', icon: 'add-sign', cls: 'node-badge-icon' },
        { key: 'transfer', show: hasTransferButton(n), type: 'svg', icon: 'transfer-assignee', cls: 'node-badge-icon' },
        { key: 'back', show: hasBackSemantics(n), type: 'svg', icon: 'process-back', cls: 'node-badge-icon' },
        { key: 'jump', show: hasJumpAhead(n), type: 'svg', icon: 'process-jump-ahead', cls: 'node-badge-icon' },
        { key: 'assist', show: hasAssistButton(n), type: 'svg', icon: 'assist', cls: 'node-badge-icon' },
    ].filter(i => i.show);
    return { list: items.length > 6 ? items.slice(0, 5) : items, more: items.length > 6 };
};

/**节点标题背景色:选择条件节点使用专属树莓红,完成审批深紫红,推进审批翠绿色,其余按nodeType取色 */
let titleBgColor = computed(() => {
    if (props.nodeConfig.isPickCondition) {
        return PICK_CONDITION_COLOR;
    }
    // 完成审批节点着色: labelList 含 finish_approve_node (优先于推进审批判据)
    if (props.nodeConfig.nodeType == 4
        && Array.isArray(props.nodeConfig.labelList)
        && props.nodeConfig.labelList.some(l => l.labelValue === 'finish_approve_node')) {
        return FINISH_APPROVE_COLOR;
    }
    // 同意推进节点着色: labelList 含 approve_forward_node (与推进按钮 42 互斥, 优先于 42 的判据)
    // 与推进审批节点(FORWARD_APPROVE_COLOR)同色, 语义都是"推进"
    if (props.nodeConfig.nodeType == 4
        && Array.isArray(props.nodeConfig.labelList)
        && props.nodeConfig.labelList.some(l => l.labelValue === 'approve_forward_node')) {
        return FORWARD_APPROVE_COLOR;
    }
    // 推进审批节点着色: 审批人节点(nodeType=4)的审批页按钮配置包含推进按钮(42)
    // 以按钮配置为判据, 避免后端 forwardType 默认值/反显回填导致的误判
    if (props.nodeConfig.nodeType == 4
        && hasButtonType(props.nodeConfig.buttons?.approvalPage, 42)) {
        return FORWARD_APPROVE_COLOR;
    }
    // 自动完成节点着色: nodeType=18 + auto_complete_node 标签 (深靛蓝, 与自动推进翠绿色区分)
    if (props.nodeConfig.nodeType == 18
        && Array.isArray(props.nodeConfig.labelList)
        && props.nodeConfig.labelList.some(l => l.labelValue === 'auto_complete_node')) {
        return AUTO_COMPLETE_COLOR;
    }
    // 条件完成节点着色: nodeType=12 + condition_finish_node 标签 (深紫红, 与条件推进琥珀橙区分)
    if (props.nodeConfig.nodeType == 12
        && Array.isArray(props.nodeConfig.labelList)
        && props.nodeConfig.labelList.some(l => l.labelValue === 'condition_finish_node')) {
        return CONDITION_FINISH_COLOR;
    }
    // 条件推进节点着色: nodeType=12 + condition_advance_node 标签 (琥珀橙, 与条件审批青绿色区分)
    if (props.nodeConfig.nodeType == 12
        && Array.isArray(props.nodeConfig.labelList)
        && props.nodeConfig.labelList.some(l => l.labelValue === 'condition_advance_node')) {
        return CONDITION_ADVANCE_COLOR;
    }
    // 退回审批/退回指定节点着色: nodeType=4 + af_syslabel_disagree_back 标签 (亮红, 不同意行为=退回指定节点)
    // 任何配置了不同意退回指定节点的审批人节点(含退回审批节点)都显示此色
    if (props.nodeConfig.nodeType == 4
        && Array.isArray(props.nodeConfig.labelList)
        && props.nodeConfig.labelList.some(l => l.labelValue === 'af_syslabel_disagree_back')) {
        return BACK_APPROVE_COLOR;
    }
    // 自动退回节点着色: nodeType=19 (亮红, 与退回审批同类色)
    if (props.nodeConfig.nodeType == 19) {
        return BACK_APPROVE_COLOR;
    }
    // 条件退回节点着色: nodeType=20 (亮红, 退回类统一色)
    if (props.nodeConfig.nodeType == 20) {
        return BACK_APPROVE_COLOR;
    }
    // 条件退回发起人节点着色: nodeType=21 (亮红, 退回类统一色)
    if (props.nodeConfig.nodeType == 21) {
        return BACK_APPROVE_COLOR;
    }
    return bgColors[props.nodeConfig.nodeType];
});

/**节点名称展示 */
let showText = computed(() => {
    if (!props.nodeConfig.nodeType) return '';
    if (props.nodeConfig.nodeType == 1) return $func.arrToStr(props.flowPermission) || '发起人';
    if (props.nodeConfig.nodeType == 4) return $func.setApproverStr(props.nodeConfig);
    if (props.nodeConfig.nodeType == 6) return $func.copyerStr(props.nodeConfig);
    if (props.nodeConfig.nodeType == 8) return $func.setCopyStrV2(props.nodeConfig);
    if (props.nodeConfig.nodeType == 9) return $func.autoNodeConditionStr(props.nodeConfig);
    if (props.nodeConfig.nodeType == 12 && Array.isArray(props.nodeConfig.labelList) && props.nodeConfig.labelList.some(l => l.labelValue === 'condition_finish_node')) {
        const targetNodeId = props.nodeConfig.forwardNodeIds && props.nodeConfig.forwardNodeIds[0];
        const findNodeName = (node) => {
            if (!node) return null;
            if (targetNodeId && node.nodeId === targetNodeId) return node.nodeName;
            if (node.childNode) {
                const found = findNodeName(node.childNode);
                if (found) return found;
            }
            if (node.conditionNodes) {
                for (const cond of node.conditionNodes) {
                    const found = findNodeName(cond);
                    if (found) return found;
                }
            }
            if (node.parallelNodes) {
                for (const par of node.parallelNodes) {
                    const found = findNodeName(par);
                    if (found) return found;
                }
            }
            return null;
        };
        const targetName = rootNode.value ? findNodeName(rootNode.value) : null;
        const approverStr = $func.setApproverStr(props.nodeConfig);
        return targetName ? `${approverStr}，条件满足推进至:${targetName}` : approverStr;
    }
    if (props.nodeConfig.nodeType == 12 && Array.isArray(props.nodeConfig.labelList) && props.nodeConfig.labelList.some(l => l.labelValue === 'condition_advance_node')) {
        const targetNodeId = props.nodeConfig.forwardNodeIds && props.nodeConfig.forwardNodeIds[0];
        const findNodeName = (node) => {
            if (!node) return null;
            if (targetNodeId && node.nodeId === targetNodeId) return node.nodeName;
            if (node.childNode) {
                const found = findNodeName(node.childNode);
                if (found) return found;
            }
            if (node.conditionNodes) {
                for (const cond of node.conditionNodes) {
                    const found = findNodeName(cond);
                    if (found) return found;
                }
            }
            if (node.parallelNodes) {
                for (const par of node.parallelNodes) {
                    const found = findNodeName(par);
                    if (found) return found;
                }
            }
            return null;
        };
        const targetName = rootNode.value ? findNodeName(rootNode.value) : null;
        const approverStr = $func.setApproverStr(props.nodeConfig);
        return targetName ? `${approverStr}，条件满足推进至:${targetName}` : approverStr;
    }
    if (props.nodeConfig.nodeType == 12) return $func.setApproverStr(props.nodeConfig);
    if (props.nodeConfig.nodeType == 20) return $func.setApproverStr(props.nodeConfig);
        if (props.nodeConfig.nodeType == 21) return $func.setApproverStr(props.nodeConfig);
    if (props.nodeConfig.nodeType == 13) return $func.setCopyStrV2(props.nodeConfig);
    if (props.nodeConfig.nodeType == 17) return $func.setApproverStr(props.nodeConfig);
    if (props.nodeConfig.nodeType == 18) {
        // 自动完成节点: 目标自动为最后一个审批人, 只读展示
        if (Array.isArray(props.nodeConfig.labelList)
            && props.nodeConfig.labelList.some(l => l.labelValue === 'auto_complete_node')) {
            const targetNodeId = props.nodeConfig.forwardNodeIds && props.nodeConfig.forwardNodeIds[0];
            const findNodeName = (node) => {
                if (!node) return null;
                if (targetNodeId && node.nodeId === targetNodeId) return node.nodeName;
                if (node.childNode) {
                    const found = findNodeName(node.childNode);
                    if (found) return found;
                }
                if (node.conditionNodes) {
                    for (const cond of node.conditionNodes) {
                        const found = findNodeName(cond);
                        if (found) return found;
                    }
                }
                if (node.parallelNodes) {
                    for (const par of node.parallelNodes) {
                        const found = findNodeName(par);
                        if (found) return found;
                    }
                }
                return null;
            };
            const targetName = rootNode.value ? findNodeName(rootNode.value) : null;
            return targetName ? `自动推进至完成节点:${targetName}` : '自动推进至完成节点';
        }
        if (props.nodeConfig.forwardNodeIds && props.nodeConfig.forwardNodeIds.length > 0) {
            const targetNodeId = props.nodeConfig.forwardNodeIds[0];
            const findNodeName = (node) => {
                if (!node) return null;
                if (node.nodeId === targetNodeId) return node.nodeName;
                if (node.childNode) {
                    const found = findNodeName(node.childNode);
                    if (found) return found;
                }
                if (node.conditionNodes) {
                    for (const cond of node.conditionNodes) {
                        const found = findNodeName(cond);
                        if (found) return found;
                    }
                }
                if (node.parallelNodes) {
                    for (const par of node.parallelNodes) {
                        const found = findNodeName(par);
                        if (found) return found;
                    }
                }
                return null;
            };
            const targetName = rootNode.value ? findNodeName(rootNode.value) : null;
            return targetName ? `推进至:${targetName}` : '推进至:已配置';
        }
        return '请配置推进目标节点';
    }
    if (props.nodeConfig.nodeType == 19) {
        if (props.nodeConfig.drawBackNodeIds && props.nodeConfig.drawBackNodeIds.length > 0) {
            const targetNodeId = props.nodeConfig.drawBackNodeIds[0];
            const findNodeName = (node) => {
                if (!node) return null;
                if (node.nodeId === targetNodeId) return node.nodeName;
                if (node.childNode) {
                    const found = findNodeName(node.childNode);
                    if (found) return found;
                }
                if (node.conditionNodes) {
                    for (const cond of node.conditionNodes) {
                        const found = findNodeName(cond);
                        if (found) return found;
                    }
                }
                if (node.parallelNodes) {
                    for (const par of node.parallelNodes) {
                        const found = findNodeName(par);
                        if (found) return found;
                    }
                }
                return null;
            };
            const targetName = rootNode.value ? findNodeName(rootNode.value) : null;
            return targetName ? `条件满足退回至:${targetName}` : '条件满足退回至:已配置';
        }
        return '请配置退回目标节点';
    }
});
/**
* 重置条件节点错误状态和展示名称
*/
const resetConditionNodesErr = () => {
    for (var i = 0; i < props.nodeConfig.conditionNodes.length; i++) {
        let conditionTitle = $func.conditionStr(props.nodeConfig, i);
        props.nodeConfig.conditionNodes[i].error = conditionTitle.indexOf("请设置条件") > -1;
        const defaultCond = i == props.nodeConfig.conditionNodes.length - 1 && props.nodeConfig.conditionNodes[i].conditionList.flat().filter(
            (item) => item.columnId && item.columnId !== 0
        ).length == 0;
        props.nodeConfig.conditionNodes[i].isDefault = defaultCond ? 1 : 0;
        props.nodeConfig.conditionNodes[i].nodeDisplayName = proxy.isEmpty(conditionTitle) ? props.nodeConfig.conditionNodes[i].nodeDisplayName : conditionTitle;
    }
    let maxLen = props.nodeConfig.conditionNodes.length - 1;
    let node = props.nodeConfig.conditionNodes[maxLen];
    if (node && node.conditionList.length <= 0) {
        node.isDefault = 1;
        node.error = false;
    }
}
/**
 * 重置并行节点错误状态和展示名称
 */
const resetParallelNodesErr = () => {
    if (!props.nodeConfig.parallelNodes) return;
    for (var i = 0; i < props.nodeConfig.parallelNodes.length; i++) {
        let parallTitle = $func.setApproverStr(props.nodeConfig.parallelNodes[i]);
        props.nodeConfig.parallelNodes[i].error = (props.nodeConfig.setType == 4 || props.nodeConfig.setType == 5)
            && proxy.isEmptyArray(props.nodeConfig.parallelNodes[i].nodeApproveList);
        props.nodeConfig.parallelNodes[i].nodeDisplayName = parallTitle;
    }
}
onMounted(() => {
    if (props.nodeConfig.nodeType == 2) {
        resetConditionNodesErr();
    } else if (props.nodeConfig.nodeType == 7) {
        resetParallelNodesErr();
    }
});
/**权限暂未实现 */
watch(flowPermission1, (flow) => {
    if (flow.flag && flow.id === _uid) {
        emits("update:flowPermission", flow.value);
    }
});
/**发起人节点配置监听(含表单权限) */
watch(promoterConfig1, (promoter) => {
    if (promoter.flag && promoter.id === _uid) {
        emits("update:nodeConfig", promoter.value);
    }
});
/**审批人节点监听 */
watch(approverConfig1, (approver) => {
    if (approver.flag && approver.id === _uid) {
        emits("update:nodeConfig", approver.value);
    }
});
/**抄送人节点监听 */
watch(copyerConfig1, (copyer) => {
    if (copyer.flag && copyer.id === _uid) {
        emits("update:nodeConfig", copyer.value);
    }
});
/**抄送人节点监听 */
watch(copyerConfigV2, (copyerV2) => {
    if (copyerV2.flag && copyerV2.id === _uid) {
        emits("update:nodeConfig", copyerV2.value);
    }
});
/**自动节点监听 */
watch(autoNodeConfig1, (autoNode) => {
    if (autoNode.flag && autoNode.id === _uid) {
        emits("update:nodeConfig", autoNode.value);
    }
});
/**条件节点监听 */
watch(conditionsConfig1, (condition) => {
    if (condition.flag && condition.id === _uid) {
        emits("update:nodeConfig", condition.value);
    }
});
/**
 * 点击节点名称
 * 点击事件
 * @param index 条件索引
 */
const clickEvent = (index) => {
    if (index || index === 0) {
        isInputList.value[index] = true;
    } else {
        isInput.value = true;
    }
};
/**
 * 修改节点名称
 * 失焦事件
 * @param index 条件索引
 */
const blurEvent = (index) => {
    if (index || index === 0) {
        isInputList.value[index] = false;
        if (props.nodeConfig.nodeType == 2) {
            props.nodeConfig.conditionNodes[index].nodeName = props.nodeConfig.conditionNodes[index].nodeName || "条件";
        } else if (props.nodeConfig.nodeType == 7) {
            props.nodeConfig.parallelNodes[index].nodeName = props.nodeConfig.parallelNodes[index].nodeName || "审批人";
        }
    } else {
        isInput.value = false;
        props.nodeConfig.nodeName = props.nodeConfig.nodeName || defaultText
    }
};

/**
 * 添加网关下节点
 */
const addTerm = () => {
    if (props.nodeConfig.nodeType == 2) {
        const len = props.nodeConfig.conditionNodes.length;
        const fistConditionNode = props.nodeConfig;
        //console.log('props.nodeConfig==', JSON.stringify(props.nodeConfig))
        const n_name = resetConditionNodesTitle(fistConditionNode, len);
        props.nodeConfig.conditionNodes.push(NodeUtils.createConditionNode(n_name, null, len, 0));
        resetConditionNodesErr()
    } else if (props.nodeConfig.nodeType == 7) {
        const len = props.nodeConfig.parallelNodes.length + 1;
        const n_name = '并行审核人' + len;
        props.nodeConfig.parallelNodes.push(NodeUtils.createParallelNode(n_name, null, len, 0));
        resetParallelNodesErr();
    }
    emits("update:nodeConfig", props.nodeConfig);
};

/**
 * 删除普通审批人或抄送人节点
 */
const delNode = () => {
    emits("update:nodeConfig", props.nodeConfig.childNode);
};

/**
 * 删除网关下节点
 * @param index 条件索引
 */
const delTerm = (index) => {
    if (props.nodeConfig.nodeType == 2) {
        delConditionNodeTerm(index);
    } else if (props.nodeConfig.nodeType == 7) {
        delParallelNodeTerm(index);
    }
};
/**
 * 删除条件
 * @param index 条件索引
 */
const delConditionNodeTerm = (index) => {
    props.nodeConfig.conditionNodes.splice(index, 1);
    props.nodeConfig.conditionNodes.map((item, index) => {
        item.priorityLevel = index + 1;
        item.nodeName = resetConditionNodesTitle(props.nodeConfig, index);
    });
    resetConditionNodesErr()
    emits("update:nodeConfig", props.nodeConfig);

    if (props.nodeConfig.conditionNodes.length == 1) {
        if (props.nodeConfig.childNode) {
            if (props.nodeConfig.conditionNodes[0].childNode) {
                reData(props.nodeConfig.conditionNodes[0].childNode, props.nodeConfig.childNode);
            } else {
                props.nodeConfig.conditionNodes[0].childNode = props.nodeConfig.childNode;
            }
        }
        emits("update:nodeConfig", props.nodeConfig.conditionNodes[0].childNode);
    }
}
/**
 * 删除并行审批节点
 * @param index 条件索引
 */
const delParallelNodeTerm = (index) => {
    props.nodeConfig.parallelNodes.splice(index, 1);
    props.nodeConfig.parallelNodes.map((item, index) => {
        item.priorityLevel = index + 1;
        item.nodeName = `审批人${index + 1}`;
    });
    resetParallelNodesErr();
    emits("update:nodeConfig", props.nodeConfig);
    if (props.nodeConfig.parallelNodes.length == 1) {
        if (props.nodeConfig.childNode) {
            if (props.nodeConfig.parallelNodes[0].childNode) {
                reData(props.nodeConfig.parallelNodes[0].childNode, props.nodeConfig.childNode);
            } else {
                props.nodeConfig.parallelNodes[0].childNode = props.nodeConfig.childNode;
            }
        }
        emits("update:nodeConfig", props.nodeConfig.parallelNodes[0].childNode);
    }
}
/**重置子节点 */
const reData = (data, addData) => {
    if (!data.childNode) {
        data.childNode = addData;
    } else {
        reData(data.childNode, addData);
    }
};

/**删除或添加条件节点 重置节点标题 */
const resetConditionNodesTitle = (conditionNode, len) => {
    if (!conditionNode) {
        return `条件`;
    }
    let isDynamicCondition = conditionNode.isDynamicCondition;
    let isParallel = conditionNode.isParallel;
    if (isDynamicCondition == true) {
        return `动态条件${len + 1}`;
    }
    if (isParallel == true) {
        return `并行条件${len + 1}`;
    }
    return `条件${len + 1}`;
}

/**
 * 设置节点信息
 */
const setNodeInfo = (index) => {
    var { nodeType } = props.nodeConfig;
    if (nodeType == 1) {
        setPromoter(true);
        setPromoterConfig({
            value: {
                ...JSON.parse(JSON.stringify(props.nodeConfig))
            },
            flag: false,
            id: _uid,
        });
        setFlowPermission({
            value: props.flowPermission,
            flag: false,
            id: _uid,
        });
    } else if (nodeType == 4 || nodeType == 12 || nodeType == 17 || nodeType == 18 || nodeType == 19 || nodeType == 20 || nodeType == 21) {
        setApprover(true);
        setApproverConfig({
            value: {
                ...JSON.parse(JSON.stringify(props.nodeConfig))
            },
            flag: false,
            id: _uid,
        });
    } else if (nodeType == 6) {
        setCopyer(true);
        setCopyerConfig({
            value: JSON.parse(JSON.stringify(props.nodeConfig)),
            flag: false,
            id: _uid,
        });
    }
    else if (nodeType == 7) {
        setApprover(true);
        setApproverConfig({
            value: {
                ...JSON.parse(JSON.stringify(props.nodeConfig)),
                index: index,
            },
            flag: false,
            id: _uid,
        });
    }
    else if (nodeType == 8 || nodeType == 13) {
        setCopyerV2(true);
        setCopyerConfigV2({
            value: JSON.parse(JSON.stringify(props.nodeConfig)),
            flag: false,
            id: _uid,
        });
    }
    else if (nodeType == 9) {
        setAutoNode(true);
        setAutoNodeConfig({
            value: JSON.parse(JSON.stringify(props.nodeConfig)),
            flag: false,
            id: _uid,
        });
    }
    else {
        setCondition(true);
        setConditionsConfig({
            value: JSON.parse(JSON.stringify(props.nodeConfig)),
            priorityLevel: index,
            flag: false,
            id: _uid,
        });
    }
};
/**
 * 条件排序
 * @param index 条件索引
 * @param type 排序类型
 */
const arrTransfer = (index, type = 1) => {
    //向左-1,向右1
    props.nodeConfig.conditionNodes[index] = props.nodeConfig.conditionNodes.splice(
        index + type,
        1,
        props.nodeConfig.conditionNodes[index]
    )[0];
    props.nodeConfig.conditionNodes.map((item, index) => {
        item.priorityLevel = index + 1;
    });
    resetConditionNodesErr()
    emits("update:nodeConfig", props.nodeConfig);
};
</script>
<style scoped lang="scss">
@use "@/assets/styles/antflow/workflow.scss";

.error_tip {
    position: absolute;
    top: 0px;
    right: 0px;
    transform: translate(150%, 0px);
    font-size: 24px;
}

.promoter_person .el-dialog__body {
    padding: 10px 20px 14px 20px;
}

.selected_list {
    margin-bottom: 20px;
    line-height: 30px;
}

.selected_list span {
    margin-right: 10px;
    padding: 3px 6px 3px 9px;
    line-height: 12px;
    white-space: nowrap;
    border-radius: 2px;
    border: 1px solid rgba(220, 220, 220, 1);
}

.selected_list img {
    margin-left: 5px;
    width: 7px;
    height: 7px;
    cursor: pointer;
}
</style>