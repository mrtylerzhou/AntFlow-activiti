import { setTypes } from './const'

/**
 * 节点右上角角标(node-badges)统一判定逻辑 —— 竖向(nodeWrap.vue)与横向(HorizontalDesigner)设计器共用。
 * 以后新增角标只改本文件的 getBadgeList 数组。
 */

/**按钮数组是否包含指定 buttonType: 兼容后端数字数组 [3,4,19] 与前端对象数组 [{buttonType:19}] */
export const hasButtonType = (btnList, type) =>
    Array.isArray(btnList) && btnList.some(b => b === type || (b && b.buttonType == type));

/**是否配置了超时提醒: 标准时效非空且选择了提醒天数 */
export const hasTimeoutRemind = (n) => {
    const vo = n && n.approveRemindVo;
    return !!vo && vo.standardMinutes != null && Array.isArray(vo.days) && vo.days.length > 0;
};

/**通知设置非空 */
export const hasNotice = (n) => Array.isArray(n && n.templateVos) && n.templateVos.length > 0;

/**标签列表非空 */
export const hasLabel = (n) => Array.isArray(n && n.labelList) && n.labelList.length > 0;

/**表单权限: 任一字段可编辑(E) */
export const hasFormPermEditable = (n) =>
    Array.isArray(n && n.lfFieldControlVOs) && n.lfFieldControlVOs.some(v => v && v.perm === 'E');

/**表单权限: 任一字段隐藏(H) */
export const hasFormPermHidden = (n) =>
    Array.isArray(n && n.lfFieldControlVOs) && n.lfFieldControlVOs.some(v => v && v.perm === 'H');

/**加批按钮(buttonType=19) */
export const hasSignUpButton = (n) => hasButtonType(n && n.buttons?.approvalPage, 19);

/**转办按钮(buttonType=21) */
export const hasTransferButton = (n) => hasButtonType(n && n.buttons?.approvalPage, 21);

/**协助按钮(buttonType=41) */
export const hasAssistButton = (n) => hasButtonType(n && n.buttons?.approvalPage, 41);

/**退回语义: 选中退回按钮(18) 或 不同意按钮行为非默认(disagreeBackType 2/4/5) */
export const hasBackSemantics = (n) =>
    hasButtonType(n && n.buttons?.approvalPage, 18)
    || !!(n && n.disagreeBackType);

/**推进语义: 选中推进按钮(42) 或 同意按钮行为非默认(同意推进 label/isApproveForwardNode) */
export const hasJumpAhead = (n) =>
    hasButtonType(n && n.buttons?.approvalPage, 42)
    || (Array.isArray(n && n.labelList) && n.labelList.some(l => l && l.labelValue === 'approve_forward_node'))
    || (n && n.isApproveForwardNode === true);

/**审批方式图标: 1会签/2或签/3顺序会签/4仲裁签, 无则空 */
export const getSignModeIcon = (n) => {
    const st = n && n.signType;
    if (st == 1) return 'all-sign';
    if (st == 2) return 'or-sign';
    if (st == 3) return 'all-sign-in-order';
    if (st == 4) return 'arb-sign';
    return '';
};

/**额外审批图标: 增加(propertyType=1)/排除(2), 双有合并, 无则空 */
export const getExtraSignIcon = (n) => {
    const list = n && n.property && Array.isArray(n.property.additionalSignInfoList)
        ? n.property.additionalSignInfoList : [];
    const hasAdd = list.some(a => a && a.propertyType == 1 && a.signInfos && a.signInfos.length > 0);
    const hasExclude = list.some(a => a && a.propertyType == 2 && a.signInfos && a.signInfos.length > 0);
    if (hasAdd && hasExclude) return 'extra-add-and-minus';
    if (hasAdd) return 'extra-add-sign';
    if (hasExclude) return 'extra-minus-sign';
    return '';
};

/**审批人类型数字徽章: node.setType 有值则返回数字(见 NodePropertyEnum/setTypes) */
export const getSetTypeNum = (n) => (n && n.setType ? n.setType : '');

/**审批人类型名称(悬停提示) */
export const getSetTypeTip = (n) => {
    const t = setTypes.find(s => s.value === (n && n.setType));
    return t ? t.label : '';
};

/**
 * 节点角标渲染列表(与竖向 nodeWrap 的 node-badges 一致)
 * @returns {{list: Array<{key, type: 'svg'|'num'|'anticon', icon?, num?, tip?, kind?, cls}>, more: boolean}}
 * type: svg=图标(sprite), num=数字徽章, anticon=字体图标(kind: notice=铃铛PNG, label=# 字形)
 * 超过 6 个时截断前 5 个 + more
 */
export function getBadgeList(node) {
    const items = [
        { key: 'set-type', show: getSetTypeNum(node) !== '', type: 'num', num: getSetTypeNum(node), tip: getSetTypeTip(node), cls: 'node-badge-num' },
        { key: 'sign-mode', show: !!getSignModeIcon(node), type: 'svg', icon: getSignModeIcon(node), cls: 'node-badge-icon' },
        { key: 'extra-sign', show: !!getExtraSignIcon(node), type: 'svg', icon: getExtraSignIcon(node), cls: 'node-badge-icon' },
        { key: 'notice', show: hasNotice(node), type: 'anticon', kind: 'notice', cls: 'anticon anticon-notice notice' },
        { key: 'timeout', show: hasTimeoutRemind(node), type: 'svg', icon: 'time', cls: 'timeout-notice' },
        { key: 'label', show: hasLabel(node), type: 'anticon', kind: 'label', cls: 'anticon anticon-tag label-icon' },
        { key: 'edit', show: hasFormPermEditable(node), type: 'svg', icon: 'edit', cls: 'node-badge-icon' },
        { key: 'eye', show: hasFormPermHidden(node), type: 'svg', icon: 'eye', cls: 'node-badge-icon' },
        { key: 'add-sign', show: hasSignUpButton(node), type: 'svg', icon: 'add-sign', cls: 'node-badge-icon' },
        { key: 'transfer', show: hasTransferButton(node), type: 'svg', icon: 'transfer-assignee', cls: 'node-badge-icon' },
        { key: 'back', show: hasBackSemantics(node), type: 'svg', icon: 'process-back', cls: 'node-badge-icon' },
        { key: 'jump', show: hasJumpAhead(node), type: 'svg', icon: 'process-jump-ahead', cls: 'node-badge-icon' },
        { key: 'assist', show: hasAssistButton(node), type: 'svg', icon: 'assist', cls: 'node-badge-icon' },
    ].filter(i => i.show);
    return { list: items.length > 6 ? items.slice(0, 5) : items, more: items.length > 6 };
}
