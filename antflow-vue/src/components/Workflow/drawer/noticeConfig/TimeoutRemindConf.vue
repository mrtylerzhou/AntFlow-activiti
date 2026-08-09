<template>
    <el-form label-width="130px" label-position="top" style="margin-top: 10px;border:1px solid #bfcbd9;padding:20px;">
        <el-row>
            <el-col :span="24">
                <el-form-item label="节点标准时效">
                    <div style="display:flex;gap:8px;">
                        <el-select v-model="standardSelect" placeholder="请选择标准时效" style="width: 200px"
                            @change="handleStandardChange">
                            <el-option v-for="item in standardOptions" :key="item.value" :label="item.label"
                                :value="item.value" />
                        </el-select>
                        <template v-if="standardSelect === -1">
                            <el-input-number v-model="customNum" :min="1" :max="99999" controls-position="right"
                                style="width:120px" @change="applyCustom" />
                            <el-select v-model="customUnit" style="width:90px" @change="applyCustom">
                                <el-option label="分钟" :value="1" />
                                <el-option label="小时" :value="60" />
                                <el-option label="天" :value="1440" />
                            </el-select>
                        </template>
                    </div>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="24">
                <el-form-item label="超时后提醒">
                    <el-checkbox-group v-model="dayList" @change="writeBack">
                        <el-checkbox v-for="d in 7" :key="d" :value="d" border style="margin:5px;">第{{ d }}天
                        </el-checkbox>
                    </el-checkbox-group>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="24">
                <el-form-item label="通知渠道">
                    <el-checkbox-group v-model="noticeTypes" @change="writeBack">
                        <el-checkbox v-for="item in channelOptions" :key="item.id" :value="item.id" border
                            style="margin:5px;">{{ item.name }}</el-checkbox>
                    </el-checkbox-group>
                    <p class="tip">未选择渠道时默认仅发送站内信</p>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="24">
                <el-form-item label="消息模板(可选)">
                    <el-button type="primary" plain icon="Plus" @click="dialogMsgVisible = true">选择消息模板</el-button>
                    <p v-for="tag in selectValues" style="margin-top:5px;">
                        <el-tag v-if="tag.name" type="warning" size="large">{{ tag.name }}</el-tag>
                    </p>
                    <p class="tip">不选择模板时使用系统默认超时通知文案</p>
                </el-form-item>
            </el-col>
        </el-row>
        <el-button type="primary" plain icon="Refresh" @click="resetRemind">重置超时提醒</el-button>
        <flow-msg-templete v-model:visible="dialogMsgVisible" v-model:checkedData="selectValues"
            @change="saveFlowMsgTempDialog" />
    </el-form>
</template>
<script setup>
import { ref, watch } from "vue";
import flowMsgTemplete from "./flowMsgTemplateDialog.vue";

const props = defineProps({
    /** 当前节点数据（直接 mutate） */
    approverConfig: {
        type: Object,
        default: () => ({})
    }
});

/** 标准时效固定选项: 30分钟 / 1~23小时 / 1~7天, 值即分钟; -1=自定义 */
const standardOptions = (() => {
    const opts = [{ value: 30, label: "30分钟" }];
    for (let h = 1; h <= 23; h++) opts.push({ value: h * 60, label: `${h}小时` });
    for (let d = 1; d <= 7; d++) opts.push({ value: d * 1440, label: `${d}天` });
    opts.push({ value: -1, label: "自定义" });
    return opts;
})();

const channelOptions = [
    { id: 4, name: "站内信" },
    { id: 1, name: "邮件" },
    { id: 2, name: "短信" },
    { id: 3, name: "APP-PUSH" },
    { id: 5, name: "企微消息" },
    { id: 6, name: "钉钉" },
    { id: 7, name: "飞书" },
];

const standardSelect = ref(null);
const standardMinutes = ref(null);
const customNum = ref(1);
const customUnit = ref(60);
const dayList = ref([]);
const noticeTypes = ref([4]);
const templateId = ref(null);
const templateName = ref(null);
const selectValues = ref([]);
const dialogMsgVisible = ref(false);

/** 节点切换时反显超时提醒配置 */
watch(() => props.approverConfig, (val) => {
    if (!val) return;
    const vo = val.approveRemindVo;
    if (vo && vo.standardMinutes != null) {
        standardMinutes.value = vo.standardMinutes;
        const hit = standardOptions.find(o => o.value === vo.standardMinutes);
        if (hit) {
            standardSelect.value = hit.value;
        } else {
            standardSelect.value = -1;
            decomposeCustom(vo.standardMinutes);
        }
    } else {
        standardSelect.value = null;
        standardMinutes.value = null;
    }
    dayList.value = vo?.days ? [...vo.days] : [];
    noticeTypes.value = vo?.noticeTypes?.length ? [...vo.noticeTypes] : [4];
    templateId.value = vo?.templateId ?? null;
    templateName.value = vo?.templateName ?? null;
    selectValues.value = vo?.templateId ? [{ id: vo.templateId, name: vo.templateName }] : [];
}, { immediate: true });

/** 分钟数拆解为 数字+单位 */
const decomposeCustom = (minutes) => {
    if (minutes % 1440 === 0) {
        customUnit.value = 1440;
        customNum.value = minutes / 1440;
    } else if (minutes % 60 === 0) {
        customUnit.value = 60;
        customNum.value = minutes / 60;
    } else {
        customUnit.value = 1;
        customNum.value = minutes;
    }
};

const handleStandardChange = (v) => {
    if (v === -1) {
        applyCustom();
    } else {
        standardMinutes.value = v;
        writeBack();
    }
};

const applyCustom = () => {
    standardMinutes.value = (customNum.value || 0) * customUnit.value;
    writeBack();
};

/** 消息模板选择 */
const saveFlowMsgTempDialog = (data) => {
    selectValues.value = data;
    templateId.value = data[0]?.id ?? null;
    templateName.value = data[0]?.name ?? null;
    writeBack();
};

/** 写回节点配置 */
const writeBack = () => {
    if (!props.approverConfig) return;
    props.approverConfig.approveRemindVo = {
        standardMinutes: standardMinutes.value,
        days: [...dayList.value],
        noticeTypes: [...noticeTypes.value],
        templateId: templateId.value,
        templateName: templateName.value,
        isInuse: standardMinutes.value != null && dayList.value.length > 0,
    };
};

/** 重置超时提醒 */
const resetRemind = () => {
    standardSelect.value = null;
    standardMinutes.value = null;
    customNum.value = 1;
    customUnit.value = 60;
    dayList.value = [];
    noticeTypes.value = [4];
    templateId.value = null;
    templateName.value = null;
    selectValues.value = [];
    if (props.approverConfig) {
        props.approverConfig.approveRemindVo = null;
    }
};
</script>
<style scoped>
.tip {
    color: #909399;
    font-size: 12px;
    line-height: 1.5;
    margin-top: 4px;
}
</style>
