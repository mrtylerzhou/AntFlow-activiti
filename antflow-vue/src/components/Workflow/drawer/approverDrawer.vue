<template>
    <el-drawer :append-to-body="true" title="审批人设置" v-model="visible" :with-header="false" :size="680">
        <div class="el-drawer__header">
            <span class="drawer-title">审批人</span>
        </div>
        <el-tabs v-model="activeName" @tab-click="handleTabClick">
            <el-tab-pane label="审批人设置" name="approverStep">
                <div v-if="approverStepShow">
                    <div class="approver_content">
                        <div>
                            <el-radio-group v-model="displaySetType" class="clear" @change="changeType">
                                <el-radio v-for="({ value, label }) in setTypes" :value="value">{{ label
                                }}</el-radio>
                            </el-radio-group>
                        </div>
                        <div v-show="approverConfig.setType == 5 && displaySetType != 19">
                            <el-button type="primary" plain icon="Plus" @click="addApprover">添加/修改人员</el-button>
                            <div class="gap-2">
                                <el-tag v-for="(item, index) in approverConfig.nodeApproveList" :key="item.targetId"
                                    size="large" closable
                                    @close="$func.removeEle(approverConfig.nodeApproveList, item, 'targetId')">
                                    {{ item.name }}
                                </el-tag>
                            </div>
                        </div>
                        <div v-if="displaySetType == 19" class="approver_text">
                            <p class="tip">该节点设置"上一节点指定"后，审批人由上一节点审批人在审批时通过[指定下一节点审批人]按钮指定。</p>
                        </div>

                        <div v-show="approverConfig.setType == 4">
                            <el-button type="primary" plain icon="Plus" @click="addRoleApprover">添加/修改角色</el-button>
                            <div class="gap-2">
                                <el-tag v-for="(item, index) in approverConfig.nodeApproveList" :key="item.targetId"
                                    size="large" closable
                                    @close="$func.removeEle(approverConfig.nodeApproveList, item, 'targetId')">
                                    {{ item.name }}
                                </el-tag>
                            </div>
                        </div>

                        <div v-show="approverConfig.setType == 14">
                            <el-button type="primary" @click="addRoleApprover">添加/修改部门</el-button>
                            <div class="gap-2">
                                <el-tag v-for="(item, index) in approverConfig.nodeApproveList" :key="item.targetId"
                                    size="large" closable
                                    @close="$func.removeEle(approverConfig.nodeApproveList, item, 'targetId')">
                                    {{ item.name }}
                                </el-tag>
                            </div>
                        </div>

                        <div v-if="approverConfig.setType == 3">
                            <div>
                                <span>发起人的：</span>
                                <el-select v-model="approverConfig.directorLevel" placeholder="请选择"
                                    style="width: 300px">
                                    <el-option v-for="item in directorMaxLevel" :key="item" :value="item"
                                        :label="item == 1 ? '直接主管' : '第' + item + '级' + '主管'" />
                                </el-select>
                            </div>
                            <p class="tip">找不到主管时，由上级主管代审批</p>
                        </div>

                        <div v-if="approverConfig.setType == 2">
                            <div style="margin-bottom: 12px;">
                                <span>结束条件：</span>
                                <el-radio-group v-model="loopEndType" @change="changeLoopEndType">
                                    <el-radio :value="1">按层级数</el-radio>
                                    <el-radio :value="2">按结束人</el-radio>
                                </el-radio-group>
                            </div>
                            <div v-if="loopEndType == 1" style="margin-bottom: 12px;">
                                <span>向上审批层数：</span>
                                <el-input-number v-model="loopNumberPlies" :min="1" :max="20"
                                    style="width: 200px" />
                            </div>
                            <div v-if="loopEndType == 2" style="margin-bottom: 12px;">
                                <el-button type="primary" plain icon="Plus"
                                    @click="addLoopEndPerson">选择结束人</el-button>
                                <div class="gap-2">
                                    <el-tag v-for="(item, index) in loopEndPersonList" :key="item.targetId"
                                        size="large" closable
                                        @close="removeLoopEndPerson(index)">
                                        {{ item.name }}
                                    </el-tag>
                                </div>
                            </div>
                        </div>

                        <div v-if="approverConfig.setType == 6">
                            <div>
                                <span>HRBP选择：</span>
                                <el-select v-model="checkedHRBP" placeholder="请选择" style="width: 300px">
                                    <el-option v-for="item in hrbpOptions" :key="item.value" :label="item.label"
                                        :value="item.value" />
                                </el-select>
                            </div>
                        </div>
                        <div class="approver_text" v-if="approverConfig.setType == 12">
                            <p class="tip">该审批节点设置“发起人自己”后，审批人默认为发起人</p>
                        </div>
                        <div class="approver_text" v-if="approverConfig.setType == 13">
                            <p class="tip">该审批节点设置“直属领导”后，审批人默认为发起人的直属领导</p>
                        </div>
                        <div class="approver_text" v-if="approverConfig.setType == 7">
                            <p class="tip">该审批节点设置“发起人自选审批人”后，审批人在发起业务表单时由发起人选择</p>
                        </div>
                        <div class="approver_text" v-if="approverConfig.setType == 16">
                            <div>
                                <p><i style="color: red;">*</i>审批人类型:</p>
                                <el-select v-model="approverConfig.property.formAssigneeProperty" placeholder="请选审批人类型"
                                    style="width: 300px">
                                    <el-option v-for="item in formUserOptionSet" :key="item.value" :label="item.label"
                                        :value="item.value" />
                                </el-select>
                            </div>
                            <div>
                                <p><i style="color: red;">*</i>表单中的人员组件</p>
                                <el-select v-model="formInfoSelected" placeholder="请选择人员组件" style="width: 300px">
                                    <el-option v-for="item in formInfoOptions" :key="item.id" :label="item.name"
                                        :value="item.id" />
                                </el-select>
                            </div>
                        </div>
                        <div class="approver_text" v-if="approverConfig.setType == 18">
                            <div>
                                <p><i style="color: red;">*</i>审批人类型:</p>
                                <el-select v-model="approverConfig.property.formAssigneeProperty" placeholder="请选审批人类型"
                                    style="width: 300px">
                                    <el-option v-for="item in formPrevNodeApproverOptionSet" :key="item.value" :label="item.label"
                                        :value="item.value" />
                                </el-select>
                            </div> 
                        </div>
                        <div class="approver_text" v-if="approverConfig.setType == 17">
                            <div>
                                <p><i style="color: red;">*</i>自定义审批规则:</p>
                                <el-select v-model="udrSelectedId" placeholder="请选择自定义审批规则" style="width: 300px">
                                    <el-option v-for="item in udrOptions" :key="item.id" :label="item.name" :value="item.id" />
                                </el-select>
                            </div>
                        </div>
                    </div>
                    <div class="approver_block" v-if="approverConfig.nodeType == 4">
                        <p>额外增加审批</p>
                        <div>
                            <el-button type="primary" plain icon="Plus" @click="openExtraDialog(1, 5)">指定人员</el-button>
                            <el-button type="primary" plain icon="Plus" @click="openExtraDialog(1, 4)">指定角色</el-button>
                        </div>
                        <div class="gap-2">
                            <template v-for="item in additionalSignInfoAddList" :key="item.nodeProperty">
                                <el-tag v-for="(info, idx) in item.signInfos" :key="info.id" size="large" closable
                                    @close="removeExtraSignInfo(1, item.nodeProperty, idx)">
                                    {{ info.name }}{{ item.nodeProperty == 5 ? '（人员）' : '（角色）' }}
                                </el-tag>
                            </template>
                        </div>
                    </div>
                    <div class="approver_block" v-if="approverConfig.nodeType == 4">
                        <p>额外排除审批</p>
                        <div>
                            <el-button type="primary" plain icon="Plus" @click="openExtraDialog(2, 5)">指定人员</el-button>
                            <el-button type="primary" plain icon="Plus" @click="openExtraDialog(2, 4)">指定角色</el-button>
                        </div>
                        <div class="gap-2">
                            <template v-for="item in additionalSignInfoExcludeList" :key="item.nodeProperty">
                                <el-tag v-for="(info, idx) in item.signInfos" :key="info.id" size="large" closable
                                    @close="removeExtraSignInfo(2, item.nodeProperty, idx)">
                                    {{ info.name }}{{ item.nodeProperty == 5 ? '（人员）' : '（角色）' }}
                                </el-tag>
                            </template>
                        </div>
                    </div>
                    <div class="approver_block" v-if="approverConfig.setType != 2">
                        <p>✍多人审批时采用的审批方式</p>
                        <el-radio-group v-model="approverConfig.signType" class="clear">
                            <el-radio :value="1">会签（需所有审批人同意，不限顺序）</el-radio>
                            <br />
                            <el-radio :value="2">或签（只需一名审批人同意或拒绝即可）</el-radio>
                            <br />
                            <el-radio :value="3" v-if="approverConfig.setType == 5">顺序会签（需要所有审批人同意，根据前端传入的顺序）</el-radio>
                        </el-radio-group>
                    </div>
                    <div class="approver_block">
                        <p>✍审批人为空时</p>
                        <el-radio-group v-model="approverConfig.noHeaderAction" class="clear">
                            <el-radio :value="0">不允许发起</el-radio>
                            <br />
                            <!--注意,这里的跳过指的是不生成审批任务节点,即流程图里没有当前缺失审批人的节点-->
                            <el-radio :value="1">跳过</el-radio>
                            <br />
                            <!--转给管理员需实现BpmnProcessAdminProvider接口-->
                            <el-radio :value="2">转交给审核管理员</el-radio>
                        </el-radio-group>
                    </div>
                </div>
            </el-tab-pane>
            <el-tab-pane lazy v-if="approverConfig.nodeType === 12" label="条件设置" name="conditionStep">
                <ConditionGroupEditor
                    :conditionList="approverConfig.conditionList"
                    v-model:groupRelation="approverConfig.groupRelation"
                    v-model:nodeApproveList="approverConfig.nodeApproveList">
                    <template #tip>当满足以下条件时, 当前节点将自动审批通过; 条件不满足时由审批人人工处理</template>
                </ConditionGroupEditor>
            </el-tab-pane>
            <el-tab-pane lazy label="按钮权限设置" name="buttonStep">
                <p>【审批页面】按钮权限显示控制</p>
                <el-checkbox-group class="clear" v-model="checkApprovalPageBtns">
                    <div class="btn-row" v-for="opt in approvalPageButtons" :key="opt.value">
                        <el-checkbox :value="opt.value" :disabled="opt.type === 'default'"
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

                <div v-if="afterSignUpWayVisible">
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
            </el-tab-pane>
            <el-tab-pane lazy label="表单权限设置" name="formStep">
                <form-perm-conf v-if="formStepShow" default-perm="R" v-model:formItems="formItems"
                    @changePermVal="changePermVal" />
            </el-tab-pane>
            <el-tab-pane lazy label="通知设置" name="noticeStep">
                <notice-conf v-if="noticeStepShow" :formData="templateVos" @changeFlowMsgSet="handleFlowMsgSet" />
            </el-tab-pane>
            <el-tab-pane lazy label="高级设置" name="advancedStep">
                <div v-if="advancedStepShow" class="advanced-setting-content">
                    <div class="setting-group">
                        <p class="setting-group-title">流程标签</p>
                        <el-select v-model="selectedLabelValues" multiple filterable clearable
                            placeholder="请选择流程标签" style="width: 100%">
                            <el-option v-for="item in labelOptions" :key="item.id" :label="item.name"
                                :value="item.id" />
                        </el-select>
                        <p class="tip">为当前节点设置流程标签，用于后续分类和统计</p>
                    </div>
                </div>
            </el-tab-pane>
        </el-tabs>
        <div class="demo-drawer__footer clear">
            <el-button type="primary" @click="saveApprover">确 定</el-button>
            <el-button @click="closeDrawer">取 消</el-button>
        </div>
        <select-user-dialog v-model:visible="approverUserVisible" :data="checkedUserList" @change="sureUserApprover" />
        <select-role-dialog v-model:visible="approverRoleVisible" :data="checkedRoleList" @change="sureRoleApprover" />
        <select-user-dialog v-model:visible="extraUserVisible" :data="extraUserList" @change="confirmExtraUser" />
        <select-role-dialog v-model:visible="extraRoleVisible" :data="extraRoleList" @change="confirmExtraRole" />
        <select-user-dialog v-model:visible="loopEndPersonVisible" :data="loopEndPersonCheckedList" @change="confirmLoopEndPerson" />
    </el-drawer>
</template>
<script setup>
import { ref, watch, computed } from 'vue';
import $func from '@/utils/antflow/index';
import { setTypes, hrbpOptions, approvalPageButtons, nodeViewPageButtons, NO_USER_FIELD_WIDGETS, formUserOptionSet,formPrevNodeApproverOptionSet, PREV_NODE_APPOINTED_SET_TYPE, PREV_NODE_APPOINTED_VIRTUAL_USER_ID, PREV_NODE_APPOINTED_VIRTUAL_USER_NAME } from '@/utils/antflow/const';
import { QuestionFilled } from '@element-plus/icons-vue';
import { useStore } from '@/store/modules/workflow';
import { getUDROptions, getDictDataByType } from '@/api/workflow/index';
import selectUserDialog from '../dialog/selectUserDialog.vue';
import selectRoleDialog from '../dialog/selectRoleDialog.vue';
import formPermConf from "./permConfig/FormPermConf.vue";
import noticeConf from "./noticeConfig/index.vue";
import ConditionGroupEditor from "./condition/ConditionGroupEditor.vue";
const { proxy } = getCurrentInstance();
const store = useStore()
const props = defineProps({
    directorMaxLevel: {
        type: Number,
        default: 0
    }
});
const lowCodeFormFields = computed(() => store.lowCodeFormField)
let approverConfig = ref({});
let approverUserVisible = ref(false);
let approverRoleVisible = ref(false);
let checkedRoleList = ref([]);
let checkedUserList = ref([]);
let checkApprovalPageBtns = ref([]);
let checkViewPageBtns = ref([]);
let checkedHRBP = ref('');
let approvalPageBtns = ref([]);
/**按钮自定义名称映射 { [buttonType]: 自定义名称 } */
let buttonCustomNames = ref({});
/**查看页按钮自定义名称映射 { [buttonType]: 自定义名称 } */
let viewButtonCustomNames = ref({});
let udrOptions = ref([]);
let udrSelectedId = ref(null);
let labelOptions = ref([]);
let selectedLabelValues = ref([]);
let afterSignUpWayVisible = computed(() => approverConfig.value?.isSignUp == 1);
let approvalBtnSubOption = ref(1);

let formItems = ref([]);
let templateVos = ref([]);
let activeName = ref('approverStep');
let approverStepShow = ref(true);
let formStepShow = ref(false);
let noticeStepShow = ref(false);
let advancedStepShow = ref(false);
let approverConfig1 = computed(() => store.approverConfig1);
let approverDrawer = computed(() => store.approverDrawer);

/**
 * 上一节点指定审批人: radio 显示值映射
 * - get: 当 isPrevNodeAppointed==true 时, 返回 19; 否则返回 setType
 * - set: val==19 时, 设置 isPrevNodeAppointed=true + setType=5 + 虚拟用户 + signType=1
 *        val!=19 时, 设置 isPrevNodeAppointed=false + setType=val
 * 标签不由前端传,而是通过 isPrevNodeAppointed 标识让后端自动贴(防止 nodeSpecialProcess 清空 labelList)
 */
const displaySetType = computed({
    get() {
        if (!approverConfig.value) return null;
        if (approverConfig.value.isPrevNodeAppointed) {
            return PREV_NODE_APPOINTED_SET_TYPE;
        }
        return approverConfig.value.setType;
    },
    set(val) {
        if (val === PREV_NODE_APPOINTED_SET_TYPE) {
            approverConfig.value.isPrevNodeAppointed = true;
            approverConfig.value.setType = 5;
            approverConfig.value.signType = 1;
            approverConfig.value.noHeaderAction = 0;
            approverConfig.value.nodeApproveList = [{
                type: 1,
                targetId: PREV_NODE_APPOINTED_VIRTUAL_USER_ID,
                name: PREV_NODE_APPOINTED_VIRTUAL_USER_NAME
            }];
        } else {
            approverConfig.value.isPrevNodeAppointed = false;
            approverConfig.value.setType = val;
        }
    }
});

const formInfoSelected = ref(null);
const formInfoOptions = ref([]);

// 额外增加/排除审批人相关状态
let extraUserVisible = ref(false);
let extraRoleVisible = ref(false);
let extraUserList = ref([]);
let extraRoleList = ref([]);
let currentEditPropertyType = ref(1); // 1:增加, 2:排除
let currentEditNodeProperty = ref(5); // 5:指定人员, 4:指定角色

// 层层审批 (setType == 2) 状态
let loopEndType = ref(1); // 1=按层级数, 2=按结束人
let loopNumberPlies = ref(10);
let loopEndPersonList = ref([]); // [{targetId, name}]
let loopEndPersonVisible = ref(false);
let loopEndPersonCheckedList = ref([]);

const additionalSignInfoAddList = computed(() => {
    return (approverConfig.value?.property?.additionalSignInfoList || []).filter(a => a.propertyType == 1);
});
const additionalSignInfoExcludeList = computed(() => {
    return (approverConfig.value?.property?.additionalSignInfoList || []).filter(a => a.propertyType == 2);
});
let visible = computed({
    get() {
        handleTabClick({ paneName: "approverStep" })
        return approverDrawer.value
    },
    set() {
        closeDrawer()
    }
});
/**页面加载监听事件 */
watch(approverConfig1, (val) => {
    if (val.value.nodeType == 7) {//nodeType == 7 是并行审批
        let currParallel = val.value.parallelNodes[val.value.index]
        approverConfig.value = currParallel;
        formItems.value = currParallel.lfFieldControlVOs || [];
        templateVos.value = currParallel.templateVos || [];
        loadApprovalPageButtons(currParallel.buttons?.approvalPage);
        loadViewPageButtons(currParallel.buttons?.viewPage);
        selectedLabelValues.value = (currParallel.labelList || []).map(l => l.labelValue);
    }
    else {
        approverConfig.value = val.value;
        formItems.value = val.value.lfFieldControlVOs || [];
        templateVos.value = val.value.templateVos || [];
        loadApprovalPageButtons(val.value.buttons?.approvalPage);
        loadViewPageButtons(val.value.buttons?.viewPage);
        selectedLabelValues.value = (val.value.labelList || []).map(l => l.labelValue);
    }
});

/**监听 approverConfig 对象*/
watch(approverConfig, (val) => {
    
    console.log('approverConfig.value====', approverConfig.value)
    if (!approverConfig.value.property) {
        approverConfig.value.property = {};
    }
    if (!approverConfig.value.property.additionalSignInfoList) {
        approverConfig.value.property.additionalSignInfoList = [];
    }
    if (!approverConfig.value.property.formAssigneeProperty) {
        approverConfig.value.property.formAssigneeProperty = 1;
    }
    if (val.nodeProperty == 6) {//nodeProperty == 6 指 HRBP
        checkedHRBP.value = val.property.hrbpConfType
    }
    if (approverConfig.value?.property?.afterSignUpWay == 1) {
        approvalBtnSubOption.value = 9;//审批完之后，会回到本节点的审批人再次审批
    } else {
        approvalBtnSubOption.value = approverConfig.value?.property?.signUpType;
    }
    if (val.nodeProperty == 16) {//nodeProperty == 16 指 表单中人员
        initFormInfoOptions();
        formInfoSelected.value = approverConfig.value.property.formInfos?.[0]?.id;
    }
    if (val.setType == 17) {//setType == 17 指 自定义审批规则
        initUdrOptions();
        udrSelectedId.value = approverConfig.value.property.udrAssigneeProperty?.id || null;
    }
    if (val.setType == 2) {//setType == 2 指 层层审批
        const prop = approverConfig.value.property || {};
        loopEndType.value = prop.loopEndType || 1;
        loopNumberPlies.value = prop.loopNumberPlies || 10;
        // 回填已有人员对象
        if (prop.loopEndPersonObjList && prop.loopEndPersonObjList.length > 0) {
            loopEndPersonList.value = prop.loopEndPersonObjList.map(
                item => ({ targetId: item.id, name: item.name }));
        } else {
            loopEndPersonList.value = [];
        }
    }
}, { deep: true });


watch(formInfoSelected, (val) => {
    const property = approverConfig.value.property;
    if (!property) {
        approverConfig.value.property = {};
    }
    if (!approverConfig.value.property.formInfos) {
        approverConfig.value.property.formInfos = [];
    }
    const info = formInfoOptions.value.find(item => item.id === val);
    if (info) {
        property.formInfos = [{
            id: info.id,
            name: info.name
        }];
    }
}, { immediate: true });

watch(udrSelectedId, (val) => {
    const property = approverConfig.value.property;
    if (!property) {
        approverConfig.value.property = {};
    }
    const info = udrOptions.value.find(item => item.id === val);
    if (info) {
        property.udrAssigneeProperty = {
            id: info.id,
            name: info.name
        };
    } else {
        property.udrAssigneeProperty = null;
    }
}, { immediate: true });

/**同步层层审批 loopNumberPlies 到 approverConfig.property */
watch(loopNumberPlies, (val) => {
    if (approverConfig.value.setType != 2) return;
    if (!approverConfig.value.property) {
        approverConfig.value.property = {};
    }
    approverConfig.value.property.loopNumberPlies = val;
});

/**同步流程标签选择到 approverConfig.labelList */
watch(selectedLabelValues, (vals) => {
    if (!approverConfig.value) return;
    const existingList = approverConfig.value.labelList || [];
    approverConfig.value.labelList = (vals || []).map(v => {
        const opt = labelOptions.value.find(item => item.id === v);
        const existing = existingList.find(item => item.labelValue === v);
        return {
            labelValue: v,
            labelName: opt ? opt.name : (existing ? existing.labelName : '')
        };
    });
}, { deep: true });

/**处理HRBP选项 */
watch(checkedHRBP, (val) => {
    if (approverConfig.value.setType != 6) {
        return;
    }
    approverConfig.value.property.hrbpConfType = val;
    let labelName = hrbpOptions.find(item => item.value == val)?.label;
    if (labelName) {
        approverConfig.value.nodeApproveList = [{ "type": 6, "targetId": val, "name": labelName }];
    }
});



/**选择审批人类型更改事件 */
const changeType = (val) => {
    //上一节点指定: displaySetType 的 setter 已完成 setType/虚拟用户/标签设置, 此处跳过清空逻辑
    if (val == PREV_NODE_APPOINTED_SET_TYPE) {
        return;
    }
    formInfoOptions.value = [];
    approverConfig.value.nodeApproveList = [];
    approverConfig.value.signType = 1;
    approverConfig.value.noHeaderAction = 0;
    checkedHRBP.value = '';
    if (val == 3) {
        approverConfig.value.directorLevel = 1;
    }
    if (val == 2) {
        // 层层审批: 强制顺序会签，初始化 loop 字段
        approverConfig.value.signType = 3;
        if (!approverConfig.value.property) {
            approverConfig.value.property = {};
        }
        loopEndType.value = approverConfig.value.property.loopEndType || 1;
        loopNumberPlies.value = approverConfig.value.property.loopNumberPlies || 10;
        loopEndPersonList.value = [];
        // 回填已有的人员列表
        if (approverConfig.value.property.loopEndPersonObjList) {
            loopEndPersonList.value = approverConfig.value.property.loopEndPersonObjList.map(
                item => ({ targetId: item.id, name: item.name }));
        }
    }
    if (val == 16) {
        initFormInfoOptions();
    }
    else {
        formInfoOptions.value = [];
    }
}

const initFormInfoOptions = () => {
    formInfoOptions.value = [];
    if (!lowCodeFormFields.value.hasOwnProperty("formFields")) {
        return;
    }
    lowCodeFormFields.value.formFields.filter(item => {
        if (item.options.required && item.options.label) {
            return item.type;
        }
    }).map((item, index) => {
        if (NO_USER_FIELD_WIDGETS.has(item.type)) {
            return;
        }
        formInfoOptions.value.push({
            id: item.id,
            name: item.options.label
        });
    });
}

const initUdrOptions = async () => {
    if (udrOptions.value.length > 0) {
        return;
    }
    try {
        const res = await getUDROptions();
        udrOptions.value = res.data || [];
    } catch (error) {
        proxy.$modal.msgError("获取自定义审批规则失败");
    }
}

const initLabelOptions = async () => {
    if (labelOptions.value.length > 0) {
        return;
    }
    try {
        const res = await getDictDataByType('processlabel');
        labelOptions.value = res.data || [];
    } catch (error) {
        proxy.$modal.msgError("获取流程标签失败");
    }
}

/**添加审批人 */
const addApprover = () => {
    approverUserVisible.value = true;
    checkedUserList.value = approverConfig.value.nodeApproveList
}
/**添加审批角色 */
const addRoleApprover = () => {
    approverRoleVisible.value = true;
    checkedRoleList.value = approverConfig.value.nodeApproveList
}
/**选择审批人确认按钮 */
const sureUserApprover = (data) => {
    approverConfig.value.nodeApproveList = data;
    approverUserVisible.value = false;
}
/**选择角色确认按钮 */
const sureRoleApprover = (data) => {
    approverConfig.value.nodeApproveList = data;
    approverRoleVisible.value = false;
}

/**打开额外审批人弹窗 */
const openExtraDialog = (propertyType, nodeProperty) => {
    currentEditPropertyType.value = propertyType;
    currentEditNodeProperty.value = nodeProperty;
    const list = getExtraSignInfos(propertyType, nodeProperty);
    if (nodeProperty == 5) {
        extraUserList.value = list.map(item => ({ targetId: item.id, name: item.name }));
        extraUserVisible.value = true;
    } else if (nodeProperty == 4) {
        extraRoleList.value = list.map(item => ({ targetId: item.id, name: item.name }));
        extraRoleVisible.value = true;
    }
}
/**获取额外审批人列表 */
const getExtraSignInfos = (propertyType, nodeProperty) => {
    const list = approverConfig.value?.property?.additionalSignInfoList || [];
    const item = list.find(a => a.propertyType == propertyType && a.nodeProperty == nodeProperty);
    return item ? item.signInfos : [];
}
/**设置额外审批人列表 */
const setExtraSignInfos = (propertyType, nodeProperty, signInfos) => {
    if (!approverConfig.value.property) {
        approverConfig.value.property = {};
    }
    if (!approverConfig.value.property.additionalSignInfoList) {
        approverConfig.value.property.additionalSignInfoList = [];
    }
    const list = approverConfig.value.property.additionalSignInfoList;
    const idx = list.findIndex(a => a.propertyType == propertyType && a.nodeProperty == nodeProperty);
    if (signInfos && signInfos.length > 0) {
        const item = { propertyType, nodeProperty, signInfos };
        if (idx >= 0) {
            list[idx] = item;
        } else {
            list.push(item);
        }
    } else if (idx >= 0) {
        list.splice(idx, 1);
    }
}
/**移除单个额外审批人 */
const removeExtraSignInfo = (propertyType, nodeProperty, index) => {
    const list = approverConfig.value?.property?.additionalSignInfoList || [];
    const idx = list.findIndex(a => a.propertyType == propertyType && a.nodeProperty == nodeProperty);
    if (idx >= 0) {
        list[idx].signInfos.splice(index, 1);
        if (list[idx].signInfos.length == 0) {
            list.splice(idx, 1);
        }
    }
}
/**选择额外人员确认 */
const confirmExtraUser = (data) => {
    const signInfos = data.map(item => ({ id: item.targetId ?? item.id, name: item.name }));
    setExtraSignInfos(currentEditPropertyType.value, currentEditNodeProperty.value, signInfos);
    extraUserVisible.value = false;
}
/**选择额外角色确认 */
const confirmExtraRole = (data) => {
    const signInfos = data.map(item => ({ id: item.targetId ?? item.id, name: item.name }));
    setExtraSignInfos(currentEditPropertyType.value, currentEditNodeProperty.value, signInfos);
    extraRoleVisible.value = false;
}

/**层层审批: 切换结束类型 */
const changeLoopEndType = (val) => {
    loopEndType.value = val;
    if (!approverConfig.value.property) {
        approverConfig.value.property = {};
    }
    approverConfig.value.property.loopEndType = val;
    if (val == 1) {
        // 切到按层级数，清空结束人
        loopEndPersonList.value = [];
        approverConfig.value.property.loopEndPersonList = [];
    } else {
        // 切到按结束人，重置层数为默认值
        loopNumberPlies.value = 10;
        approverConfig.value.property.loopNumberPlies = 10;
    }
}

/**层层审批: 打开结束人选择弹窗 */
const addLoopEndPerson = () => {
    loopEndPersonCheckedList.value = [...loopEndPersonList.value];
    loopEndPersonVisible.value = true;
}

/**层层审批: 确认结束人 */
const confirmLoopEndPerson = (data) => {
    loopEndPersonList.value = data;
    if (!approverConfig.value.property) {
        approverConfig.value.property = {};
    }
    approverConfig.value.property.loopEndPersonList = data.map(item => item.targetId ?? item.id);
    loopEndPersonVisible.value = false;
}

/**层层审批: 移除结束人 */
const removeLoopEndPerson = (index) => {
    loopEndPersonList.value.splice(index, 1);
    if (approverConfig.value.property) {
        approverConfig.value.property.loopEndPersonList = loopEndPersonList.value.map(item => item.targetId ?? item.id);
    }
}

/**处理权限按钮变更事件 */
const handleCheckedButtonsChange = () => {
    //checkApprovalPageBtns 由 v-model 自动维护勾选状态
    const isAddStep = checkApprovalPageBtns.value.indexOf(19);
    approverConfig.value.isSignUp = isAddStep >= 0 ? 1 : 0;
    syncApprovalPageButtons();
}

/**从后端返回的 approvalPage(对象数组)解析出勾选值与自定义名称 */
const loadApprovalPageButtons = (approvalPageList) => {
    const list = approvalPageList || [];
    //兼容老数据:元素可能是数字或对象
    checkApprovalPageBtns.value = list.map(item => typeof item === 'number' ? item : item.buttonType);
    const names = {};
    approvalPageButtons.forEach(opt => {
        const item = list.find(i => typeof i === 'object' && i.buttonType === opt.value);
        //只有当 buttonName 与默认 label 不同时才回填(否则视为未自定义,显示空)
        names[opt.value] = (item && item.buttonName && item.buttonName !== opt.label) ? item.buttonName : '';
    });
    buttonCustomNames.value = names;
    syncApprovalPageButtons();
}

/**将勾选状态与自定义名称同步到 approverConfig.buttons.approvalPage(对象数组) */
const syncApprovalPageButtons = () => {
    if (!approverConfig.value) return;
    if (!approverConfig.value.buttons) approverConfig.value.buttons = {};
    approverConfig.value.buttons.approvalPage = checkApprovalPageBtns.value.map(bt => ({
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

/**将勾选状态与自定义名称同步到 approverConfig.buttons.viewPage(对象数组) */
const syncViewPageButtons = () => {
    if (!approverConfig.value) return;
    if (!approverConfig.value.buttons) approverConfig.value.buttons = {};
    approverConfig.value.buttons.viewPage = checkViewPageBtns.value.map(bt => ({
        buttonType: bt,
        buttonName: viewButtonCustomNames.value[bt] || ''
    }));
}

/**处理加批按钮 子操作 */
const handleApprovalBtnSubOption = (val) => {
    //signType 指的是当前节点审批方式 1:会签，2:或签，3:顺序会签
    //signUpType 指的是加批审批操作 1:顺序会签，2:会签，3:或签 
    //val加批类型 1:顺序会签，2:会签，3:或签 特别 9指: 回到加批人，则afterSignUpWay赋值为1，signUpType赋值为1
    approverConfig.value.property.afterSignUpWay = val && val == 9 ? 1 : 2;
    approverConfig.value.property.signUpType = val && val == 9 ? 1 : val;
}

/**条件抽屉的确认 */
const saveApprover = () => {
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
/**低代码表单字段权限 */
const changePermVal = (data) => {
    approverConfig.value.lfFieldControlVOs = data;
}
/**消息设置 */
const handleFlowMsgSet = (data) => {
    approverConfig.value.templateVos = !proxy.isEmpty(data) ? [data] : [];
    store.setApproverConfig({
        value: approverConfig1.value.value,
        flag: true,
        id: approverConfig1.value.id
    })
}
/**
 * 切换tab
 * @param tab 当前tab
 * @param event 
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
        initLabelOptions();
    } else {
        advancedStepShow.value = false;
    }
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

.approver_content {
    min-height: 250px;
    padding-bottom: 10px;
    border-bottom: 1px solid #f2f2f2;
    font-size: 14px;
}

.approver_content,
.approver_block {
    padding-top: 10px;

    .el-radio-group {
        display: unset;
    }

    .el-radio {
        width: 27%;
        margin-bottom: 20px;
        height: 16px;
    }
}

.tip {
    margin: 10px 0 22px 0;
    font-size: 12px;
    line-height: 16px;
    color: #f8642d;
    font-size: 16px;
}

.approver_text {
    padding: 8px 15px;
}

.opt-description {
    font-size: smaller;
    color: gray;
}

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

.gap-2 {
    display: flex;
    gap: 0.5rem;
    flex-wrap: wrap;
    margin-top: 10px;
}

.advanced-setting-content {
    padding: 10px 0;
}

.setting-group {
    padding: 10px 0;
    border-bottom: 1px solid #f2f2f2;
}

.setting-group-title {
    margin: 0 0 10px 0;
    font-size: 14px;
    font-weight: 600;
}
</style>