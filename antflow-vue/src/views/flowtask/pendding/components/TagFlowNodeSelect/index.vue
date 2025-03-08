<template>
    <div class="tag-select">
        <div class="tag-box"> 
            <el-tag v-if="selectValue" type="warning" effect="dark"  :size="layoutSize"  closable :key="selectValue.nodeId" @close="onDeleteTag(selectValue)">
                {{ selectValue.nodeName  }}【{{ selectValue.nodeDisplayName }}】
            </el-tag>
        </div>
        <slot name="append" />
    </div>
</template>

<script setup>
import { ref,watch } from 'vue'; 
const emits = defineEmits(['update:value']);
const props = defineProps({
    value: {
        type: String,
        default: ''
    },
    flowNode: {
        type: Object,
        default: () => undefined
    }
})
const layoutSize = ref('default');
const selectValue = ref(null);

const onDeleteTag = (tag) => { 
    emits('update:value', null);
};
watch(() => props.flowNode, newValue => { 
    if (newValue && newValue != null && newValue != '') {
        selectValue.value = newValue;
    }
});

watch(() => props.value, newValue => {
    if (newValue && newValue != null && newValue != '') {
        selectValue.value = props.flowNode;
    }else {
        selectValue.value = null;
    }
}, { immediate: true, deep: true, });
 
</script>

<style scoped lang="scss">
.tag-select {
    display: flex;
    width: 100%;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
}

.tag-box {
    flex-grow: 1;
    padding: 0 5px;
}
</style>