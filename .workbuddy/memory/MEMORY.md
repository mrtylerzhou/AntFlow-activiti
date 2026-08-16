# antflow / jimuoffice 项目 MEMORY

## 架构关键路径

- 审批按钮入口: `BpmnConfController.buttonsOperation` → `ProcessApprovalServiceImpl.buttonsOperation` → `ButtonPreOperationService.buttonsPreOperation` (AOP 拦截) → `DoButtonOperationAspect.around` → `ButtonOperationServiceImpl.buttonsOperationTransactional` → `ProcessOperationAdaptor.doProcessButton` (按 operationType 分发).
- 同意/重提/加批都走 `ResubmitProcessImpl.executeTaskCompletion`, 里面调 `formAdaptor.consentData(vo)` 写业务表; 这是表单数据最终落地处.
- 协办(`BUTTON_TYPE_ASSIST`)由 `AssistProcessImpl` 委托给 `ResubmitProcessImpl`, 行为一致.
- 表单适配器: `FormFactory.getFormAdaptor(vo)` 返回 `FormOperationAdaptor<T extends BusinessDataVo>`, 适配器由 `@ActivitiServiceAnno(svcName=formCode)` 注解注册.
- 低代码流程的统一 formCode = `LOWFLOW_FORM_CODE`, 适配器 = `LowFlowApprovalService`, 实体 = `UDLFApplyVo`, 字段在 `lfFields` / `lfFieldsMulti` Map 中; 外部分多表单模式用 `lfFieldsMulti`.
- DIY 流程: formCode 即业务标识, 适配器继承 `AbstractFormOperationAdaptor<BizXxxVo>`, 实体是 `BusinessDataVo` 子类, 字段是子类 declared fields.
- 版本存储: 每版本 = `t_bpmn_conf` 一行(`edit()` 恒 insert 新行, 主键必变); `bpmn_code` 编码版本号. `t_bpmn_node.node_id`(String 列, 前端 idGenerator 生成, 保存透传)拷贝链路下**跨版本稳定** → 版本比较节点对齐的第一优先键; 版本列表 `POST /bpmnConf/listPage`(带 formCode), 详情 `GET /bpmnConf/detail/{id}` → BpmnConfVo 全量(nodes/lfFormData/confConfigJson/extraFlags).

## 重要文件索引

- `antflow-engine/.../bpmnconf/service/biz/ProcessAuditBizServiceImpl.java`: 表单字段变更审计 (javers).
- `antflow-engine/.../bpmnconf/controller/BpmnConfController.java`: 主审批/草稿/列表接口.
- `antflow-engine/.../engine/lowflow/service/LowFlowApprovalService.java`: 低代码流程核心, consentData/queryData/submitData 三方法都分内联/外部表单两模式.
- `antflow-engine/.../bpmnconf/adp/processoperation/ResubmitProcessImpl.java`: 同意/重提/加批 + 协助入口.
- `antflow-base/.../vo/BusinessDataVo.java`: 所有业务数据基类, 含 lfFields / bpmnConfVo 等.
- `antflow-base/.../vo/UDLFApplyVo.java`: 低代码 vo, 多加 lfFieldsMulti / lfFormdataList / lfFormData / remark.
- `antflow-vue/.../api/workflow/index.js`: 所有工作流接口.
- `antflow-vue/.../views/workflow/components/auditDrawer.vue`: 字段变更审计抽屉 (新).
- `antflow-vue/.../views/workflow/components/previewDrawer.vue`: 已办/我的发起查看流程表单的 drawer.
- `antflow-vue/.../views/workflow/flowTask/pendding/approve.vue`: 待办审批页(从 pendding/index.vue 的"审批"按钮跳转).
- `antflow-vue/.../views/workflow/flowTask/pendding/components/approveForm.vue`: 审批表单核心组件(各容器页都引用).

## 约定

- 后端模块 `antflow-engine` 不要直接 import `org.openoa.base.vo.UDLFApplyVo`(业务字段是 UDLFApplyVo 上的); 跨模块访问用反射或交给适配器.
- `BusinessDataVo.isLowCodeFlow` 是 `Integer` 不是 boolean, 用 `Objects.equals(vo.getIsLowCodeFlow(), 1)` 判断.
- `BizService<M, T, Entity>` 默认方法 `getMapper()` / `getService()` 通过反射拿 Spring Bean, 写 impl 时直接 `this.getMapper()` / `this.getService()` 即可.
- 前端 `pinia` store `store.modules.workflow.instanceViewConfig1` 集中保存当前查看/审批的流程配置(processNumber/formCode/isLowCodeFlow 等).
- 菜单路由是后端动态配置, 前端从 `public/mock/menu.json` 加载. 待办任务菜单 `path: pendding` 对应 `component: workflow/flowTask/pendding/approveV2`(**不是** approve.vue). 新加"全场景生效"特性优先放在各场景共用的子组件(如 `approveForm.vue`)里.
- 低代码字段定义: `BpmnConfLfFormdataField` 表, `qryFormDataFieldMap(confId)` 拿内联模式 fieldId -> label; `qryFieldMapByFormdataId(formdataId)` 拿外部表单模式 fieldId -> label. 低代码 vo 有 bpmnConfVo 拿 confId; 拿不到时用 processNumber 查 bpm_business_process.VERSION(bpmn_code) 再查 bpmn_conf(form_code + bpmn_code) 唯一确定.
- 审批人非办公状态自动转办: `BpmnConfFlagsEnum.AUTO_DELEGATE_OFF_DUTY(512)`. 门禁在 `NextNodeForwardProcessor`(优先级低于全局委托), 调 `AfUserService.checkEmployeeEffective`(骨架接口, 返回 UserAvailableVo: available/unavailableBeginTime/unavailableEndTime/delegateUser, 默认空实现恒可用)做四象限时间判断, 命中且 delegateUser 非空则转办并写 bpm_flowrun_entrust. 前端开关在 `AdvancedSetting/index.vue`, lf/diy 保存链路需对 extraFlags 做 OR 合并(高级设置只控制自己的位).
- Maven 编译: mvnw 与本机 Git Bash 下 mvn 均不可用, 用 PowerShell 调 `C:\greensoft\apache-maven-3.6.3\bin\mvn.cmd`; antflow-base 增量编译会报 lombok 假错(OperationResp 等), 需 clean 后全量编译.

## Vue 3 响应式陷阱

- `computed` 返回的数组/对象里**元素是普通对象**, 直接修改元素属性不触发更新. 要么 `reactive(...)` 包装元素, 要么把可变状态抽到顶层 `ref`.
- `ref(new Set())` / `ref(new Map())` 必须**整体重新赋值** (`xxx.value = new Set(...)`) 才能触发响应式更新; add/delete/clear 不会.
- 想在 computed 初始化副作用(如默认展开第一条), 用 `watch(groups, cb, { immediate: true })`, 不要写在 computed 里.