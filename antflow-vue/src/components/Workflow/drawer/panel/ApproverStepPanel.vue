<template>
    <div v-if="node && node.nodeType !== 18 && node.nodeType !== 19" class="approver_content-wrap">
        <div class="approver_content">
            <div>
                <el-radio-group v-model="displaySetType" class="clear" @change="changeType">
                    <el-radio v-for="({ value, label }) in visibleSetTypes" :value="value">{{ label }}</el-radio>
                </el-radio-group>
            </div>
            <div v-show="node.setType == 5 && displaySetType != 19 && displaySetType != 21">
                <el-button type="primary" plain icon="Plus" @click="addApprover">添加/修改人员</el-button>
                <div class="gap-2">
                    <el-tag v-for="(item, index) in node.nodeApproveList" :key="item.targetId"
                        size="large" closable
                        @close="$func.removeEle(node.nodeApproveList, item, 'targetId')">
                        {{ item.name }}
                    </el-tag>
                </div>
            </div>
            <div v-if="displaySetType == 19" class="approver_text">
                <p class="tip">该节点设置"上一节点指定"后，审批人由上一节点审批人在审批时通过[指定下一节点审批人]按钮指定。</p>
            </div>
            <div v-if="displaySetType == 21" class="approver_text">
                <p class="tip">该节点设置"到达前设置"后，审批人在流程运行到该节点时由业务代码(FormOperationAdaptor.provideCurrentNodeAssignees)动态查询指定。</p>
            </div>

            <div v-show="node.setType == 4">
                <el-button type="primary" plain icon="Plus" @click="addRoleApprover">添加/修改角色</el-button>
                <div class="gap-2">
                    <el-tag v-for="(item, index) in node.nodeApproveList" :key="item.targetId"
                        size="large" closable
                        @close="$func.removeEle(node.nodeApproveList, item, 'targetId')">
                        {{ item.name }}
                    </el-tag>
                </div>
            </div>

            <div v-show="node.setType == 14">
                <el-button type="primary" @click="addRoleApprover">添加/修改部门</el-button>
                <div class="gap-2">
                    <el-tag v-for="(item, index) in node.nodeApproveList" :key="item.targetId"
                        size="large" closable
                        @close="$func.removeEle(node.nodeApproveList, item, 'targetId')">
                        {{ item.name }}
                    </el-tag>
                </div>
            </div>

            <div v-if="node.setType == 3">
                <div>
                    <span>发起人的：</span>
                    <el-select v-model="node.directorLevel" placeholder="请选择"
                        style="width: 300px">
                        <el-option v-for="item in directorMaxLevel" :key="item" :value="item"
                            :label="item == 1 ? '直接主管' : '第' + item + '级' + '主管'" />
                    </el-select>
                </div>
                <p class="tip">找不到主管时，由上级主管代审批</p>
            </div>

            <div v-if="node.setType == 2">
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

            <div v-if="node.setType == 6">
                <div>
                    <span>HRBP选择：</span>
                    <el-select v-model="checkedHRBP" placeholder="请选择" style="width: 300px">
                        <el-option v-for="item in hrbpOptions" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </div>
            </div>
            <div class="approver_text" v-if="node.setType == 12">
                <p class="tip">该审批节点设置"发起人自己"后，审批人默认为发起人</p>
            </div>
            <div class="approver_text" v-if="node.setType == 13">
                <p class="tip">该审批节点设置"直属领导"后，审批人默认为发起人的直属领导</p>
            </div>
            <div class="approver_text" v-if="node.setType == 7">
                <p class="tip">该审批节点设置"发起人自选审批人"后，审批人在发起业务表单时由发起人选择</p>
            </div>
            <div class="approver_text" v-if="node.setType == 16">
                <div>
                    <p><i style="color: red;">*</i>审批人类型:</p>
                    <el-select v-model="node.property.formAssigneeProperty" placeholder="请选审批人类型"
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
            <div class="approver_text" v-if="node.setType == 18">
                <div>
                    <p><i style="color: red;">*</i>审批人类型:</p>
                    <el-select v-model="node.property.formAssigneeProperty" placeholder="请选审批人类型"
                        style="width: 300px">
                        <el-option v-for="item in formPrevNodeApproverOptionSet" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </div>
            </div>
            <div class="approver_text" v-if="node.setType == 17">
                <div>
                    <p><i style="color: red;">*</i>自定义审批规则:</p>
                    <el-select v-model="udrSelectedId" placeholder="请选择自定义审批规则" style="width: 300px">
                        <el-option v-for="item in udrOptions" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </div>
            </div>
            <div class="approver_text" v-if="node.setType == 20">
                <div style="margin-bottom: 12px;">
                    <p><i style="color: red;">*</i>选择标签:</p>
                    <el-select v-model="labelBasedSelectedLabelKey" filterable clearable
                        placeholder="请选择流程标签" style="width: 100%"
                        @change="onLabelBasedLabelChange">
                        <el-option v-for="item in labelOptions" :key="item.id" :label="item.name"
                            :value="item.id" />
                    </el-select>
                    <p class="tip">发起时透传给 AfUserService 里的queryApproversByLabel方法找人</p>
                </div>
                <div>
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <p>自定义变量(可选,最多{{ LABEL_BASED_MAX_CUSTOM_VARS }}组):</p>
                        <el-button type="primary" plain icon="Plus" size="small"
                            :disabled="(node.property?.labelBasedApproverRule?.customVars?.length || 0) >= LABEL_BASED_MAX_CUSTOM_VARS"
                            @click="addLabelBasedCustomVar">添加变量</el-button>
                    </div>
                    <div v-for="(group, idx) in labelBasedCustomVars" :key="idx"
                        style="border: 1px solid #ebeef5; padding: 10px; margin-bottom: 8px; border-radius: 4px;">
                        <div style="display: flex; gap: 8px; align-items: center;">
                            <el-input v-model="group.displayName" placeholder="显示标签(可选)"
                                style="flex: 1;" />
                            <el-input v-model="group.varName" placeholder="变量名(必填,不可重复)"
                                style="flex: 1;" />
                            <el-input v-model="group.varValue" placeholder="变量值(必填)"
                                style="flex: 1;" />
                            <el-button type="danger" plain icon="Delete" size="small"
                                @click="removeLabelBasedCustomVar(idx)">删除</el-button>
                        </div>
                    </div>
                    <p class="tip">每组包含显示标签(可选)、变量名(Map key,必填不重复)、变量值(Map value,必填非空)</p>
                </div>
                <p class="tip">"根据标签选择"规则不支持仲裁签,默认会签</p>
            </div>
        </div>
        <div class="approver_block" v-if="node.nodeType == 4">
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
        <div class="approver_block" v-if="node.nodeType == 4">
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
        <div class="approver_block" v-if="node.setType != 2 && !hideSignType">
            <p>✍多人审批时采用的审批方式</p>
            <el-radio-group v-model="node.signType" class="clear">
                <el-radio :value="1">会签（需所有审批人同意，不限顺序）</el-radio>
                <br />
                <el-radio :value="2">或签（只需一名审批人同意或拒绝即可）</el-radio>
                <br />
                <el-radio :value="3" v-if="node.setType == 5">顺序会签（需要所有审批人同意，根据前端传入的顺序）</el-radio>
                <br />
                <el-radio :value="4" :disabled="node.setType == 20">仲裁签(按通过比例完成)</el-radio>
            </el-radio-group>
            <div v-if="node.signType == 4" style="margin-top: 10px;">
                <span>通过比例:</span>
                <el-input-number v-model="node.property.arbitrationRatio" :min="1" :max="100" />
                <span>%</span>
            </div>
        </div>
        <div class="approver_block" v-if="!hideNoHeaderAction">
            <p>✍审批人为空时</p>
            <el-radio-group v-model="node.noHeaderAction" class="clear">
                <el-radio :value="0">不允许发起</el-radio>
                <br />
                <!--注意,这里的跳过指的是不生成审批任务节点,即流程图里没有当前缺失审批人的节点-->
                <el-radio :value="1">跳过</el-radio>
                <br />
                <!--转给管理员需实现BpmnProcessAdminProvider接口-->
                <el-radio :value="2">转交给审核管理员</el-radio>
            </el-radio-group>
        </div>
        <select-user-dialog v-model:visible="approverUserVisible" :data="checkedUserList" @change="sureUserApprover" />
        <select-role-dialog v-model:visible="approverRoleVisible" :data="checkedRoleList" @change="sureRoleApprover" />
        <select-user-dialog v-model:visible="extraUserVisible" :data="extraUserList" @change="confirmExtraUser" />
        <select-role-dialog v-model:visible="extraRoleVisible" :data="extraRoleList" @change="confirmExtraRole" />
        <select-user-dialog v-model:visible="loopEndPersonVisible" :data="loopEndPersonCheckedList" @change="confirmLoopEndPerson" />
    </div>
</template>
<script setup>
import { ref, watch, computed } from 'vue';
import $func from '@/utils/antflow/index';
import { setTypes, hrbpOptions, NO_USER_FIELD_WIDGETS, formUserOptionSet, formPrevNodeApproverOptionSet, PREV_NODE_APPOINTED_SET_TYPE, PREV_NODE_APPOINTED_VIRTUAL_USER_ID, PREV_NODE_APPOINTED_VIRTUAL_USER_NAME, ARRIVAL_DYNAMIC_SET_TYPE, ARRIVAL_DYNAMIC_VIRTUAL_USER_ID, ARRIVAL_DYNAMIC_VIRTUAL_USER_NAME, LABEL_BASED_SET_TYPE, LABEL_BASED_MAX_CUSTOM_VARS } from '@/utils/antflow/const';
import { useStore } from '@/store/modules/workflow';
import { getUDROptions, getDictDataByType } from '@/api/workflow/index';
import selectUserDialog from '../../dialog/selectUserDialog.vue';
import selectRoleDialog from '../../dialog/selectRoleDialog.vue';

const { proxy } = getCurrentInstance();
const store = useStore();

const props = defineProps({
    /** 当前节点数据（直接 mutate，drawer 传副本、Zen 传副本树节点） */
    approverConfig: {
        type: Object,
        default: () => ({})
    },
    directorMaxLevel: {
        type: Number,
        default: 0
    },
    /** 隐藏的审批人类型列表(嵌入场景裁剪用, 如条件自动加批不支持交互型/无解析实现的类型) */
    excludeSetTypes: {
        type: Array,
        default: () => []
    },
    /** 隐藏「多人审批时采用的审批方式」块(加批场景由外部 signUpType 控制) */
    hideSignType: {
        type: Boolean,
        default: false
    },
    /** 隐藏「审批人为空时」处理策略块(嵌入场景未实现该语义时使用, 如条件自动加批) */
    hideNoHeaderAction: {
        type: Boolean,
        default: false
    }
});

/** 可见的审批人类型(排除 excludeSetTypes) */
const visibleSetTypes = computed(() => setTypes.filter(t => !props.excludeSetTypes.includes(t.value)));

/** 节点对象别名（computed 包装 props，模板与脚本统一通过 node 访问） */
const node = computed(() => props.approverConfig);
const lowCodeFormFields = computed(() => store.lowCodeFormField);

let approverUserVisible = ref(false);
let approverRoleVisible = ref(false);
let checkedRoleList = ref([]);
let checkedUserList = ref([]);
let checkedHRBP = ref('');
let udrOptions = ref([]);
let udrSelectedId = ref(null);
let formInfoSelected = ref(null);
let formInfoOptions = ref([]);

// 根据标签选择 (setType == 20) 状态
let labelOptions = ref([]);
let labelBasedSelectedLabelKey = ref(null);
//直接读 node 嵌套数组,避免独立 ref 与 node 双向同步导致 watch 递归
const labelBasedCustomVars = computed(() => node.value?.property?.labelBasedApproverRule?.customVars || []);

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
    return (node.value?.property?.additionalSignInfoList || []).filter(a => a.propertyType == 1);
});
const additionalSignInfoExcludeList = computed(() => {
    return (node.value?.property?.additionalSignInfoList || []).filter(a => a.propertyType == 2);
});

/**
 * 审批人类型 radio 显示值映射: "上一节点指定"(19) 与 "到达前设置"(21) 均映射到 setType=5 + 虚拟用户。
 * 到达前设置的反显靠 nodeApproveList 含虚拟人 -5 检测(持久化数据, 可靠),
 * 不依赖标志位(标志位在 save→load 往返中可能丢失)。
 */
const displaySetType = computed({
    get() {
        if (!node.value) return null;
        // 到达前设置: setType=5 且 nodeApproveList 含虚拟人 -5
        if (node.value.setType == 5
            && Array.isArray(node.value.nodeApproveList)
            && node.value.nodeApproveList.some(p => p && p.targetId === ARRIVAL_DYNAMIC_VIRTUAL_USER_ID)) {
            return ARRIVAL_DYNAMIC_SET_TYPE;
        }
        if (node.value.isPrevNodeAppointed) {
            return PREV_NODE_APPOINTED_SET_TYPE;
        }
        return node.value.setType;
    },
    set(val) {
        if (val === ARRIVAL_DYNAMIC_SET_TYPE) {
            node.value.isPrevNodeAppointed = false;
            node.value.setType = 5;
            node.value.signType = 1;
            node.value.noHeaderAction = 0;
            node.value.nodeApproveList = [{
                type: 1,
                targetId: ARRIVAL_DYNAMIC_VIRTUAL_USER_ID,
                name: ARRIVAL_DYNAMIC_VIRTUAL_USER_NAME
            }];
        } else if (val === PREV_NODE_APPOINTED_SET_TYPE) {
            node.value.isPrevNodeAppointed = true;
            node.value.setType = 5;
            node.value.signType = 1;
            node.value.noHeaderAction = 0;
            node.value.nodeApproveList = [{
                type: 1,
                targetId: PREV_NODE_APPOINTED_VIRTUAL_USER_ID,
                name: PREV_NODE_APPOINTED_VIRTUAL_USER_NAME
            }];
        } else {
            node.value.isPrevNodeAppointed = false;
            node.value.setType = val;
        }
    }
});

/** 节点切换时反显 */
watch(() => props.approverConfig, (val) => {
    if (!val) return;
    // 确保 property 结构存在
    if (!val.property) {
        val.property = {};
    }
    if (!val.property.additionalSignInfoList) {
        val.property.additionalSignInfoList = [];
    }
    if (!val.property.formAssigneeProperty) {
        val.property.formAssigneeProperty = 1;
    }
    if (val.nodeProperty == 6) {//nodeProperty == 6 指 HRBP
        checkedHRBP.value = val.property.hrbpConfType;
    }
    if (val.nodeProperty == 16) {//nodeProperty == 16 指 表单中人员
        initFormInfoOptions();
        formInfoSelected.value = val.property.formInfos?.[0]?.id;
    }
    if (val.setType == 17) {//setType == 17 指 自定义审批规则
        initUdrOptions();
        udrSelectedId.value = val.property.udrAssigneeProperty?.id || null;
    }
    if (val.setType == 2) {//setType == 2 指 层层审批
        const prop = val.property || {};
        loopEndType.value = prop.loopEndType || 1;
        loopNumberPlies.value = prop.loopNumberPlies || 10;
        if (prop.loopEndPersonObjList && prop.loopEndPersonObjList.length > 0) {
            loopEndPersonList.value = prop.loopEndPersonObjList.map(
                item => ({ targetId: item.id, name: item.name }));
        } else {
            loopEndPersonList.value = [];
        }
    }
    if (val.setType == LABEL_BASED_SET_TYPE) {//setType == 20 指 根据标签选择
        labelBasedSelectedLabelKey.value = val.property?.labelBasedApproverRule?.labelKey || null;
        initLabelOptions();
    }
}, { immediate: true });

watch(formInfoSelected, (val) => {
    const property = node.value.property;
    if (!property) {
        node.value.property = {};
    }
    if (!node.value.property.formInfos) {
        node.value.property.formInfos = [];
    }
    const info = formInfoOptions.value.find(item => item.id === val);
    if (info) {
        const formInfo = {
            id: info.id,
            name: info.name
        };
        //外部表单模式: 附带 formdataId 便于后端定位人员所属表单
        if (info.formdataId != null) {
            formInfo.formdataId = info.formdataId;
        }
        property.formInfos = [formInfo];
    }
}, { immediate: true });

watch(udrSelectedId, (val) => {
    const property = node.value.property;
    if (!property) {
        node.value.property = {};
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

/**同步层层审批 loopNumberPlies 到 node.property */
watch(loopNumberPlies, (val) => {
    if (node.value.setType != 2) return;
    if (!node.value.property) {
        node.value.property = {};
    }
    node.value.property.loopNumberPlies = val;
});

/**处理HRBP选项 */
watch(checkedHRBP, (val) => {
    if (node.value.setType != 6) {
        return;
    }
    node.value.property.hrbpConfType = val;
    let labelName = hrbpOptions.find(item => item.value == val)?.label;
    if (labelName) {
        node.value.nodeApproveList = [{ "type": 6, "targetId": val, "name": labelName }];
    }
});

/**选择审批人类型更改事件 */
const changeType = (val) => {
    //上一节点指定 / 到达前设置: displaySetType 的 setter 已完成 setType/虚拟用户设置, 此处跳过清空逻辑
    if (val == PREV_NODE_APPOINTED_SET_TYPE || val == ARRIVAL_DYNAMIC_SET_TYPE) {
        return;
    }
    formInfoOptions.value = [];
    node.value.nodeApproveList = [];
    node.value.signType = 1;
    node.value.noHeaderAction = 0;
    checkedHRBP.value = '';
    if (val == 3) {
        node.value.directorLevel = 1;
    }
    if (val == 2) {
        // 层层审批: 强制顺序会签，初始化 loop 字段
        node.value.signType = 3;
        if (!node.value.property) {
            node.value.property = {};
        }
        loopEndType.value = node.value.property.loopEndType || 1;
        loopNumberPlies.value = node.value.property.loopNumberPlies || 10;
        loopEndPersonList.value = [];
        // 回填已有的人员列表
        if (node.value.property.loopEndPersonObjList) {
            loopEndPersonList.value = node.value.property.loopEndPersonObjList.map(
                item => ({ targetId: item.id, name: item.name }));
        }
    }
    if (val == 16) {
        initFormInfoOptions();
    }
    else {
        formInfoOptions.value = [];
    }
    if (val == LABEL_BASED_SET_TYPE) {
        //根据标签选择: 默认会签, 初始化 labelBasedApproverRule, 加载流程标签选项
        node.value.signType = 2;
        if (!node.value.property) {
            node.value.property = {};
        }
        if (!node.value.property.labelBasedApproverRule) {
            node.value.property.labelBasedApproverRule = {
                labelName: '',
                labelKey: '',
                customVars: []
            };
        }
        labelBasedSelectedLabelKey.value = node.value.property.labelBasedApproverRule.labelKey || null;
        initLabelOptions();
    } else {
        // 切换离开根据标签选择: 清空配置(由 formatcommit_data 按 setType 决定是否提交)
        if (node.value.property) {
            node.value.property.labelBasedApproverRule = null;
        }
        labelBasedSelectedLabelKey.value = null;
    }
}

const initFormInfoOptions = () => {
    formInfoOptions.value = [];
    //外部表单模式: 从多表单字段列表加载人员组件
    if (store.useExternalForm) {
        const forms = store.lowCodeFormFieldsMulti || [];
        for (const form of forms) {
            const fields = form.formFields || [];
            fields.forEach(item => {
                if (!item.options || !item.options.required || !item.options.label) return;
                if (NO_USER_FIELD_WIDGETS.has(item.type)) return;
                formInfoOptions.value.push({
                    id: item.id,
                    name: forms.length > 1
                        ? `【${form.formName || form.formCode}】${item.options.label}`
                        : item.options.label,
                    formdataId: form.formdataId
                });
            });
        }
        return;
    }
    //内联表单模式: 原逻辑
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

// ========== 根据标签选择: 标签与变量组操作 ==========
/**标签下拉变更: 同步 labelName/labelKey 到 node.property.labelBasedApproverRule */
const onLabelBasedLabelChange = (val) => {
    if (!node.value.property) {
        node.value.property = {};
    }
    if (!node.value.property.labelBasedApproverRule) {
        node.value.property.labelBasedApproverRule = { labelName: '', labelKey: '', customVars: [] };
    }
    const opt = labelOptions.value.find(item => item.id === val);
    node.value.property.labelBasedApproverRule.labelKey = val || '';
    node.value.property.labelBasedApproverRule.labelName = opt ? opt.name : '';
}

/**添加自定义变量组(上限 LABEL_BASED_MAX_CUSTOM_VARS) */
const addLabelBasedCustomVar = () => {
    if (!node.value.property) {
        node.value.property = {};
    }
    if (!node.value.property.labelBasedApproverRule) {
        node.value.property.labelBasedApproverRule = { labelName: '', labelKey: '', customVars: [] };
    }
    const rule = node.value.property.labelBasedApproverRule;
    if (!rule.customVars) {
        rule.customVars = [];
    }
    if (rule.customVars.length >= LABEL_BASED_MAX_CUSTOM_VARS) {
        proxy.$modal.msgError(`自定义变量组不能超过${LABEL_BASED_MAX_CUSTOM_VARS}组`);
        return;
    }
    rule.customVars.push({ displayName: '', varName: '', varValue: '' });
}

/**移除自定义变量组 */
const removeLabelBasedCustomVar = (idx) => {
    const rule = node.value.property?.labelBasedApproverRule;
    if (rule && rule.customVars) {
        rule.customVars.splice(idx, 1);
    }
}

/**添加审批人 */
const addApprover = () => {
    approverUserVisible.value = true;
    checkedUserList.value = node.value.nodeApproveList
}
/**添加审批角色 */
const addRoleApprover = () => {
    approverRoleVisible.value = true;
    checkedRoleList.value = node.value.nodeApproveList
}
/**选择审批人确认按钮 */
const sureUserApprover = (data) => {
    node.value.nodeApproveList = data;
    approverUserVisible.value = false;
}
/**选择角色确认按钮 */
const sureRoleApprover = (data) => {
    node.value.nodeApproveList = data;
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
    const list = node.value?.property?.additionalSignInfoList || [];
    const item = list.find(a => a.propertyType == propertyType && a.nodeProperty == nodeProperty);
    return item ? item.signInfos : [];
}
/**设置额外审批人列表 */
const setExtraSignInfos = (propertyType, nodeProperty, signInfos) => {
    if (!node.value.property) {
        node.value.property = {};
    }
    if (!node.value.property.additionalSignInfoList) {
        node.value.property.additionalSignInfoList = [];
    }
    const list = node.value.property.additionalSignInfoList;
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
    const list = node.value?.property?.additionalSignInfoList || [];
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
    if (!node.value.property) {
        node.value.property = {};
    }
    node.value.property.loopEndType = val;
    if (val == 1) {
        // 切到按层级数，清空结束人
        loopEndPersonList.value = [];
        node.value.property.loopEndPersonList = [];
        node.value.property.loopEndPersonObjList = [];
    } else {
        // 切到按结束人，重置层数为默认值
        loopNumberPlies.value = 10;
        node.value.property.loopNumberPlies = 10;
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
    if (!node.value.property) {
        node.value.property = {};
    }
    const ids = data.map(item => item.targetId ?? item.id);
    const objs = data.map(item => ({ id: item.targetId ?? item.id, name: item.name }));
    node.value.property.loopEndPersonList = ids;
    node.value.property.loopEndPersonObjList = objs;
    loopEndPersonVisible.value = false;
}

/**层层审批: 移除结束人 */
const removeLoopEndPerson = (index) => {
    loopEndPersonList.value.splice(index, 1);
    if (node.value.property) {
        node.value.property.loopEndPersonList = loopEndPersonList.value.map(item => item.targetId ?? item.id);
        node.value.property.loopEndPersonObjList = loopEndPersonList.value.map(item => ({ id: item.targetId ?? item.id, name: item.name }));
    }
}
</script>
<style scoped lang="scss">
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

.gap-2 {
    display: flex;
    gap: 0.5rem;
    flex-wrap: wrap;
    margin-top: 10px;
}
</style>
