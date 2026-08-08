<template>
    <notice-conf v-if="props.approverConfig" :formData="templateVos" @changeFlowMsgSet="handleFlowMsgSet" />
</template>
<script setup>
import { ref, watch } from 'vue';
import noticeConf from "../noticeConfig/index.vue";

const props = defineProps({
    /** 当前节点数据（直接 mutate，drawer 传副本、Zen 传副本树节点） */
    approverConfig: {
        type: Object,
        default: () => ({})
    }
});
const emit = defineEmits(['changeFlowMsgSet']);

const templateVos = ref([]);

/** 节点切换时反显通知设置 */
watch(() => props.approverConfig, (val) => {
    if (!val) return;
    templateVos.value = val.templateVos || [];
}, { immediate: true });

/** 消息设置: 写回节点 + 通知父级（drawer 需刷新 store，Zen 直接 mutate 即可） */
const handleFlowMsgSet = (data) => {
    const { proxy } = getCurrentInstance();
    if (props.approverConfig) {
        props.approverConfig.templateVos = !proxy.isEmpty(data) ? [data] : [];
    }
    emit('changeFlowMsgSet', data);
};
</script>
