<template>
    <form-perm-conf v-if="formPermTabVisible" default-perm="R" v-model:formItems="formItems"
        :formHidden="formHiddenMap"
        @changePermVal="changePermVal" @changeFormHidden="changeFormHidden" />
</template>
<script setup>
import { ref, watch, computed } from 'vue';
import { useStore } from '@/store/modules/workflow';
import formPermConf from "../permConfig/FormPermConf.vue";

const props = defineProps({
    /** 当前节点数据（直接 mutate，drawer 传副本、Zen 传副本树节点） */
    approverConfig: {
        type: Object,
        default: () => ({})
    },
    /** 流程类型 LF / DIY（DIY 仅在辅助表单模式下显示） */
    flowType: {
        type: String,
        default: 'LF'
    }
});
const store = useStore();

const formItems = ref([]);
const formHiddenMap = ref({});

/** 字段权限 tab 可见性: LF 始终显示; DIY 流程在启用外部表单或辅助表单时显示 */
const formPermTabVisible = computed(() => {
    const isDIY = props.flowType === 'DIY';
    return isDIY ? (!!store.useAuxiliaryForm || !!store.useExternalForm) : true;
});

/** 节点切换时反显字段权限 */
watch(() => props.approverConfig, (val) => {
    if (!val) return;
    formItems.value = val.lfFieldControlVOs || [];
    formHiddenMap.value = val.formHidden || {};
}, { immediate: true });

const changePermVal = (data) => {
    if (props.approverConfig) {
        props.approverConfig.lfFieldControlVOs = data;
    }
};

const changeFormHidden = (data) => {
    if (props.approverConfig) {
        props.approverConfig.formHidden = data;
    }
};
</script>
