<template>
    <div class="form-container">
        <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="auto"
            style="max-width: 600px;margin: auto;">
            <!-- <el-form-item label="流程分组" prop="bpmnType">
                    <el-select v-model="form.bpmnType" placeholder="请选择分组" :style="{ width: '100%' }">
                        <el-option v-for="(item, index) in bpmnTypeOptions" :key="index" :label="item.label"
                            :value="item.value"></el-option>
                    </el-select>
                </el-form-item> -->

            <!--  <el-form-item label="模板类型" prop="formCode">
                <el-select filterable v-model="form.formCode" placeholder="请选择模板类型" :style="{ width: '100%' }">
                    <el-option v-for="(item, index) in formCodeOptions" :key="index" :label="item.value" :value="item.key">
                        <span style="float: left">【{{ item.key }}】 {{ item.value }}</span> 
                    </el-option>
                </el-select>
            </el-form-item> -->


            <el-form-item v-if="!copyOpt" label="类型标识" prop="formCode">
                <el-input v-model="form.formCode" :disabled="true" :style="{ width: '100%' }" />
            </el-form-item>

            <el-form-item v-else label="类型标识" prop="formCode">
                <el-select filterable v-model="form.formCode" placeholder="请选类型标识" :style="{ width: '100%' }">
                    <el-option v-for="(item, index) in formCodeOptions" :key="index" :label="item.value"
                        :value="item.key">
                        <span style="float: left">【{{ item.key }}】 {{ item.value }}</span>
                    </el-option>
                </el-select>
            </el-form-item>

            <el-form-item v-if="!copyOpt" label="流程名称" prop="bpmnName">
                <el-input v-model="form.bpmnName" :disabled="true" :style="{ width: '100%' }" readonly />
            </el-form-item>

            <el-form-item v-else label="流程名称" prop="bpmnName">
                <template #label>
                    <span>
                        <el-tooltip content="同【模板类型】名称一致，不需手动输入" placement="top">
                            <el-icon><question-filled /></el-icon>
                        </el-tooltip>
                        流程名称
                    </span>
                </template>
                <el-input v-model="form.bpmnName" placeholder="请输入审批名称" :style="{ width: '100%' }" readonly />
            </el-form-item>

            <el-form-item label=" 审批人去重" prop="deduplicationType">
                <el-select v-model="form.deduplicationType" placeholder="请选择去重类型" :style="{ width: '100%' }">
                    <el-option v-for="(item, index) in duplicateOptions" :key="index" :label="item.label"
                        :value="item.value"></el-option>
                </el-select>
            </el-form-item>

            <!-- <el-form-item label="启用流程" prop="effectiveStatus">
                <el-switch v-model="form.effectiveStatus" />
            </el-form-item> -->
            <el-form-item label="发起人权限" prop="viewPageStart">
                <el-checkbox-group v-model="form.viewPageButtons.viewPageStart">
                    <el-checkbox v-for="item in viewPageButtons" :key="item.value" :label="item.label"
                        :value="item.value">
                        {{ item.label }}
                    </el-checkbox>
                </el-checkbox-group>
            </el-form-item>
            <!-- 外部表单模式(仅低代码流程可用) -->
            <el-form-item v-if="flowType === 'LF'" label="外部表单" prop="useExternalForm">
                <el-checkbox v-model="form.useExternalForm">使用外部表单(多表单)</el-checkbox>
            </el-form-item>
            <el-form-item v-if="flowType === 'LF' && form.useExternalForm" label="关联表单" prop="lfFormdataIdsArr">
                <el-select v-model="form.lfFormdataIdsArr" multiple filterable placeholder="请选择关联表单"
                    :style="{ width: '100%' }" @visible-change="onFormSelectVisible">
                    <el-option v-for="item in externalFormOptions" :key="item.id" :label="item.formName"
                        :value="item.id">
                        <span style="float: left">【{{ item.formCode }}】 {{ item.formName }}</span>
                    </el-option>
                </el-select>
                <div class="ext-form-tip">启用后,本流程将引用独立表单管理中已生效的表单版本;表单设计步骤将被禁用。</div>
            </el-form-item>
            <el-form-item label=" 流程说明" prop="remark">
                <el-input v-model="form.remark" type="textarea" placeholder="请输入流程说明" :maxlength="100" show-word-limit
                    :autosize="{ minRows: 4, maxRows: 4 }" :style="{ width: '100%' }"></el-input>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, getCurrentInstance } from 'vue'
import { NodeUtils } from '@/utils/antflow/nodeUtils'
import { getDIYFromCodeData } from "@/api/workflow/index";
import { getLowCodeFlowFormCodes, listEffectiveForSelect } from "@/api/workflow/lowcodeApi";
const { query } = useRoute();
const { proxy } = getCurrentInstance()
const emit = defineEmits(['nextChange', 'externalFormChange'])
let loading = ref(false);
const copyOpt = query?.copy ?? 0 > 0 ? true : false;
// 外部表单模式常量: BpmnConfFlagsEnum.USE_EXTERNAL_FORM = 0b1000000 = 64
const USE_EXTERNAL_FORM_FLAG = 64;
// 外部表单可选项(独立表单管理中已生效的版本)
let externalFormOptions = ref([]);
let externalFormLoaded = ref(false);
let props = defineProps({
    flowType: {
        type: String,
        default: () => (''),
    },
    basicData: {
        type: Object,
        default: () => (null),
    }
});

const generatorID = "PROJECT_" + NodeUtils.idGenerator();
const ruleFormRef = ref(null);
const duplicateOptions = [{
    "label": "不去重",
    "value": 1
}, {
    "label": "前去重",
    "value": 2
}, {
    "label": "后去重",
    "value": 3
}, {
    "label": "相邻节点去重",
    "value": 4
}];

const viewPageButtons = [{
    "label": "撤回",
    "value": 29
}, {
    "label": "作废",
    "value": 7
}];

let formCodeOptions = ref([]);
const form = reactive({
    bpmnName: '',
    bpmnCode: generatorID,
    bpmnType: 1,
    formCode: '',
    remark: '',
    effectiveStatus: false,
    deduplicationType: 1,
    //外部表单模式相关字段
    extraFlags: 0,
    useExternalForm: false,         //由 extraFlags & USE_EXTERNAL_FORM 派生
    lfFormdataIds: '',              //CSV 字符串,提交后端
    lfFormdataIdsArr: [],           //Number 数组,前端编辑用
    viewPageButtons: {
        viewPageStart: [],
        viewPageOther: [],
    }
})
// 复制操作 监听formCode的变化
watch(() => form.formCode, (val) => {
    if (val) {
        formCodeOptions.value.forEach(item => {
            if (item.key == val) {
                form.bpmnName = item.value;
            }
        })
    }
});
// 监听外部表单勾选变化,通知父组件
watch(() => form.useExternalForm, (val) => {
    emitExternalFormState();
});
// 监听关联表单选择变化,通知父组件(用于 store 同步多表单字段)
watch(() => form.lfFormdataIdsArr, () => {
    emitExternalFormState();
}, { deep: true });

/**向父组件发射外部表单模式当前状态(含选中的表单定义列表) */
const emitExternalFormState = () => {
    if (!form.useExternalForm) {
        emit('externalFormChange', { useExternalForm: false, lfFormdataList: [] });
        return;
    }
    //根据选中ID从已加载选项中取出完整表单定义(含 formdata JSON)
    const selectedIds = new Set(form.lfFormdataIdsArr || []);
    const selectedForms = externalFormOptions.value.filter(f => selectedIds.has(f.id));
    emit('externalFormChange', { useExternalForm: true, lfFormdataList: selectedForms });
};
onMounted(async () => {
    if (!proxy.isEmpty(props.basicData) && !proxy.isEmpty(props.basicData.formCode)) {
        form.bpmnName = props.basicData.bpmnName;
        form.bpmnCode = props.basicData.bpmnCode;
        form.formCode = props.basicData.formCode;
        form.remark = props.basicData.remark;
        form.deduplicationType = props.basicData.deduplicationType;
        form.viewPageButtons = props.basicData.viewPageButtons;
        //回显外部表单模式
        const flags = Number(props.basicData.extraFlags || 0);
        form.extraFlags = flags;
        //先加载选项再设置勾选状态,避免 watch 在选项未就绪时发射空列表
        const isExternal = (flags & USE_EXTERNAL_FORM_FLAG) === USE_EXTERNAL_FORM_FLAG;
        if (isExternal) {
            await loadExternalFormOptions();
        }
        form.useExternalForm = isExternal;
        form.lfFormdataIds = props.basicData.lfFormdataIds || '';
        form.lfFormdataIdsArr = form.lfFormdataIds
            ? form.lfFormdataIds.split(',').map(s => Number(s)).filter(n => !isNaN(n))
            : [];
        //选项已就绪,手动发射一次同步给父组件
        if (isExternal) {
            emitExternalFormState();
        }
    }
    else {
        form.bpmnCode = generatorID;
        form.formCode = query.fc;
        form.bpmnName = decodeURIComponent(query.fcname ?? '');
    }
    if (props.flowType == 'DIY') {
        getDIYFromCodeList();
    } else if (props.flowType == 'LF') {
        getLFFromCodeList();
    }
});

/**加载独立表单管理中已生效的表单列表 */
const loadExternalFormOptions = async () => {
    if (externalFormLoaded.value) return;
    loading.value = true;
    try {
        const res = await listEffectiveForSelect();
        if (res.code == 200) {
            externalFormOptions.value = res.data || [];
            externalFormLoaded.value = true;
        }
    } finally {
        loading.value = false;
    }
}
/**下拉框展开时懒加载 */
const onFormSelectVisible = (visible) => {
    if (visible && !externalFormLoaded.value) {
        loadExternalFormOptions();
    }
}

/**获取全部DIY FromCode */
const getDIYFromCodeList = async () => {
    loading.value = true;
    await getDIYFromCodeData().then((res) => {
        loading.value = false;
        if (res.code == 200) {
            formCodeOptions.value = res.data;
        }
    });
}
/**获取全部LF FromCode */
const getLFFromCodeList = async () => {
    loading.value = true;
    await getLowCodeFlowFormCodes().then((res) => {
        loading.value = false;
        if (res.code == 200) {
            formCodeOptions.value = res.data;
        }
    });
}

const rules = {
    formCode: [{
        required: true,
        message: '请选择分类',
        trigger: 'blur'
    }],
    bpmnName: [{
        required: true,
        message: '请输入流程名称',
        trigger: 'change'
    }],
    bpmnCode: [{
        required: true,
        message: '请输入流程编号',
        trigger: 'blur'
    }],
    lfFormdataIdsArr: [{
        validator: (rule, value, callback) => {
            if (form.useExternalForm && (!value || value.length === 0)) {
                callback(new Error('启用外部表单后,请至少选择一个关联表单'));
            } else {
                callback();
            }
        },
        trigger: 'change'
    }],
};

// 给父级页面提供得获取本页数据得方法
const getData = () => {
    return new Promise((resolve, reject) => {
        proxy.$refs['ruleFormRef'].validate((valid, fields) => {
            if (!valid) {
                emit('nextChange', { label: "基础设置", key: "basicSetting" })
                reject({ valid: false });
            }
            form.effectiveStatus = form.effectiveStatus ? 1 : 0;
            //序列化外部表单字段
            let flags = Number(form.extraFlags || 0);
            if (form.useExternalForm) {
                flags = flags | USE_EXTERNAL_FORM_FLAG;
                form.lfFormdataIds = (form.lfFormdataIdsArr || []).join(',');
            } else {
                flags = flags & ~USE_EXTERNAL_FORM_FLAG;
                form.lfFormdataIds = '';
                form.lfFormdataIdsArr = [];
            }
            form.extraFlags = flags;
            resolve({ formData: form })  // TODO 提交表单
        })
    })
};
defineExpose({
    getData
})
</script>
<style scoped>
.form-container {
    background: white !important;
    padding: 10px;
    max-width: 750px;
    min-height: 80vh;
    left: 0;
    bottom: 0;
    right: 0;
    margin: auto;
}

.ext-form-tip {
    margin-top: 6px;
    font-size: 12px;
    color: #909399;
    line-height: 1.4;
}
</style>