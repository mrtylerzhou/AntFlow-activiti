package org.openoa.base.constant;

/**
 * @Classname StringConstant
 * @Description TODO
 * @Date 2021-10-31 11:15
 * @Created by AntOffice
 */
public class StringConstants {
    public static final String SCAN_BASE_PACKAGES="org.openoa";
    public static final String SPECIAL_CHARACTERS = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
    public static final String BPMN_CODE_SPLITMARK = "-";

    public static final String FORM_CODE_LINKMARK = "_";
    public static final String CREATEUSERNAME = "defaultUser";
    public static final Integer CREATEUSERID = 1;
    public static final String JOBNUM="9527";
    public static final String MOCK_LOGIN_USER_KEY="mockedloginuser";
    public static final String DB_NAME_1="activiti_main";
    public static final String DB_NAME_2="jimu_biz";
    public static final String DRUID_POOL_NAME_PREFIX="druidDataSourcePool_";
    public static final String DB_TRANSACTION_MANAGERNAME_SUFFIX="transactionmanager";
    public static final String NEXT_NODE_APPROVER="nextNodeApprover";

    public static final String CURRENT_USER_ALREADY_PROCESSED="currentUserAlreadyProcessed";

    public static final String ADAPTOR_FACTORY_BEANNAME="jimuAdaptorFactory";

    public static final String TASK_ASSIGNEE_NAME="assigneeName";
    public static final String PROJECT_NAME="antFlow";
    public static final String LOWFLOW_FORM_CODE="LF";
    public static final String LOWFLOW_FORM_CONTAINER_TYPE="container";
    public static final String LOWFLOW_CONDITION_CONTAINER_FIELD_NAME="lfConditions";
    public static final String LOWFLOW_FORM_DATA_MAIN_TABLE_NAME="t_lf_main";
    public static final String LOWFLOW_FORM_DATA_FIELD_TABLE_NAME="t_lf_main_field";


    public static final String FORMCODE_NO_CAMAL="formCode";
    public static final String FORM_CODE="form_code";

    public static final String DEFAULT_TENANT="default";
    public static final String TENANT_ID="tenantId";
    public static final String TENANT_USER="tenantUser";
    public static final String LOWCODE_FLOW_DICT_TYPE="lowcodeflow";
    public static final String DYNAMIC_CONDITION_NODE="af_syslabel_dynamiccondition";
    public static final String COPY_NODE="af_syslabel_copynode";
    public static final String COPY_NODEV2="af_syslabel_copynodeV2";
    public static final String CONDITION_CHANGED="condition_changed";
    public static final String LASTNODE_COPY="af_syslabel_lastnode_copy";
    public static final String EXPRESSION_FIELD_NAME="expression";
    public static final String AUTOMATIC_NODE="auto_node";
    public static final String CONDITION_APPROVE_NODE="condition_approve_node";
    public static final String CONDITION_COPY_NODE="condition_copy_node";
    public static final String ASSIST_NODE="assist_node";
    public static final String SKIPPED_ASSIGNEE="lbl_skipped_assignee";
    /**当前节点为"上一节点指定"审批人类型时贴此标签,触发运行时替换虚拟审批人*/
    public static final String AF_SYSLABEL_PREV_NODE_APPOINTED="af_syslabel_prev_node_appointed";
    /**上一节点贴此标签,审批页渲染[指定下一节点审批人]按钮*/
    public static final String AF_SYSLABEL_APPOINT_NEXT_NODE_APPROVER="af_syslabel_appoint_next_node_approver";
    /**选择条件:审批人节点贴此标签,运行时渲染[选择分支]按钮,审批时强制走用户选定的动态条件分支*/
    public static final String AF_SYSLABEL_PICK_CONDITION="af_syslabel_pick_condition";
    /**不同意按钮配置了退回行为时贴此标签,运行时EndProcessImpl据此转发BackToModifyImpl*/
    public static final String AF_SYSLABEL_DISAGREE_BACK="af_syslabel_disagree_back";
    /**退回按钮行为:退回发起人*/
    public static final String AF_SYSLABEL_BACK_INITIATOR="af_syslabel_back_initiator";
    /**退回按钮行为:退回上一节点*/
    public static final String AF_SYSLABEL_BACK_PREV="af_syslabel_back_prev";
    /**退回按钮行为:退回指定节点*/
    public static final String AF_SYSLABEL_BACK_SPECIFIED="af_syslabel_back_specified";
    /**推进按钮:节点配置了推进行为时贴此标签,运行时据此渲染推进按钮*/
    public static final String AF_SYSLABEL_FORWARD="af_syslabel_forward";
    public static final String AF_RUNTIME_BUISINESS_INFO ="af_runtime_business_info";
    public static final String AF_RUNTIME_BPMN_CONF ="af_runtime_bpmn_conf";
    public static final String AF_AUTO_SKIP_COMMENT ="相同审批人自动跳过";
    public static final String AF_AUTO_EVALUATE_SKIP_COMMENT ="自动节点自动跳过,条件评估结果:%s";
    public static final String AF_CONDITION_APPROVE_AUTO_PASS_COMMENT ="条件审批自动通过,条件评估结果:%s";
    public static final String AF_CONDITION_COPY_SKIP_COMMENT ="条件抄送跳过,条件评估结果:%s";
    public static final String AF_COPY_V2_NODE_SUFFIX ="\uD83D\uDCE2";
    public static final String AF_SKIP_ASSIGNEE_NODE_SUFFIX ="⬇\uFE0F";
    public static final String AF_NODE_OR_SIGN_SUFFIX ="\uD83D\uDD02";
    public static final String AF_NODE_SIGN_SUFFIX ="\uD83D\uDD00";
    public static final String AF_NODE_SIGN_IN_ORDER_SUFFIX ="\uD83D\uDD03";
    public static final String AF_VERIFYSTATUS_IN_PROCESS="⌛⌛⌛";
    public static final String AF_VERIFYSTATUS_TO_BE_PROCESS="\uD83D\uDD53";
    public static final String AF_VERIFYSTATUS_REJECT="⛔";
    public static final String AF_DEFAULT_NODE_NAME ="审核人";

    public static final String HIDDEN_FIELD_PERMISSION="H";
    public static final String READ_ONLY_FIELD_PERMISSION="R";

    public static class ActVarKeys {
        public static String PROCINSTID="procInstId";
        public static String ENTRY_ID="entryId";
        public static String BUSINESS_ID = "businessId";
        public static String BPMNCODE = "bpmnCode";
        public static String FORMCODE = "formCode";
        public static String PROCERSS_NUMBER = "processNumber";
        public static String Is_OUTSIDEPROC = "isOutsideProc";
        public static String BPMN_NAME = "bpmnName";
    }
}
