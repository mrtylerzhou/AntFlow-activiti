import { bizFormMaps } from '@/utils/antflow/const';
import { isTrue } from '@/utils/antflow/ObjectUtils';

/**
 * BusinessDataVo(含父类 PageDto) + UDLFApplyVo 的属性名集合,用于回填时跳过引擎控制/结构字段。
 * 必须与后端 FormFactory.RESERVED_PROPS_UDLF 同构——两者均排除 remark(remark 双性,需摊平回显)。
 * 后端字段变更时此处同步。详见 .scratch/design-page-added-diy.md。
 */
export const RESERVED_NAMES = new Set([
  // PageDto
  'page', 'pageSize', 'totalCount', 'pageCount', 'startIndex', 'orderColumn', 'orderType',
  // BusinessDataVo
  'processNumber', 'processKey', 'businessId', 'params', 'processTitle', 'approvalComment',
  'entityName', 'processRecordInfo', 'type', 'processState', 'taskId', 'taskDefKey', 'nodeId',
  'elementId', 'nodeName', 'objectMap', 'moreHandlers', 'formCode', 'operationType', 'userIds',
  'userInfos', 'approversList', 'flag', 'initDatas', 'startUserId', 'startUserName', 'bpmnCode',
  'bpmnName', 'approvalEmpls', 'nextNodeApprovers', 'paramStr', 'empId', 'processDigest',
  'dataSourceId', 'empIds', 'isSignUpNode', 'signUpUsers', 'isStartPagePreview',
  'backToEmployeeId', 'backToModifyType', 'backToNodeId', 'formData', 'bpmnConfVo', 'accountType',
  'jobLevelVo', 'assignee', 'isOutSideAccessProc', 'isOutSideChecked', 'isLowCodeFlow',
  'isFreeRide', 'bpmFlowCallbackUrl', 'viewUrl', 'submitUrl', 'submitUser', 'conditionsUrl',
  'outSideType', 'templateMarks', 'templateMarkIds', 'embedNodes', 'outSideLevelNodes',
  'msgProcessEventEnum', 'lfFields', 'lfConditions', 'isMigration', 'node2formRelatedAssignees',
  'verifyAttachments', 'class',
  // UDLFApplyVo(remark 已排除——双性,需摊平到顶层供"备注"回显)
  'lfFormData',
]);

/**
 * 是否用自定义 Vue 组件渲染(coded DIY + page-added DIY 自定义 Vue 子模式),否则用 vform/多表单。
 * 判别: bizFormMaps 命中该 formCode(已注册自定义 Vue 组件)。
 * 不再用 !isLowCodeFlow 作判别——page-added DIY 是 formType=DIY 但 is_lowcode_flow=1(LF 后端),
 * 其中"外部表单"子模式无需自定义 Vue(走多表单渲染器),用 bizFormMaps.has 才是"需要自定义 Vue"的真信号。
 * USE_AUXILIARY_FORM 不进前端——它是纯后端/设计期信号(强制勾选、Tab 归类、字段契约校验门控)。
 */
export const useCustomForm = (isLowCodeFlow, formCode) => bizFormMaps.has(formCode);

/**
 * page-added DIY 回填: 把 response.data.lfFields 摊平到顶层,使自定义组件看到扁平字段(与 coded DIY 一致)。
 * 仅当 isLowCodeFlow 且 bizFormMaps 命中(page-added DIY)时摊平;coded DIY 与纯 LF 原样返回。
 * 跳过 RESERVED_NAMES(引擎控制字段),但放行 remark(双性,"备注"需回显)。
 */
export const flattenLfFieldsForCustomForm = (responseData, isLowCodeFlow, formCode) => {
  if (!isTrue(isLowCodeFlow) || !bizFormMaps.has(formCode)) {
    return responseData;
  }
  const lfFields = responseData.lfFields || {};
  const flat = { ...responseData };
  for (const k in lfFields) {
    if (!RESERVED_NAMES.has(k)) {
      flat[k] = lfFields[k];
    }
  }
  return flat;
};
