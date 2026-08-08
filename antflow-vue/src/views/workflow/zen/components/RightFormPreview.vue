<template>
    <div class="right-form-preview">
        <!-- 表单渲染区域（字段label后直接展示可点击的三态徽标 R/W/H） -->
        <div class="form-preview-area">
            <template v-if="loadedComponent">
                <!-- :key 绑定当前节点，切换节点时重建表单以应用该节点字段权限标识 -->
                <component :is="loadedComponent" :key="formKey"
                    :lfFormData="lfFormDataConfig" :lfFieldPerm="lfFieldPermConfig"
                    :isPreview="true" :showFieldPermLabel="true" :fieldPermEditable="true"
                    :lfFormdataList="lfFormdataListConfig" :lfFieldsMulti="lfFieldsMultiConfig"
                    :lfFieldControlVOs="lfFieldControlVOsConfig" :formHidden="formHiddenConfig"
                    @updateFieldPerm="handleUpdateFieldPerm">
                </component>
            </template>
            <el-empty v-else-if="noForm" description="该流程未配置表单（DIY普通模式）" :image-size="80" />
            <el-empty v-else description="表单加载中..." :image-size="80" />
        </div>
        <div class="perm-tip">点击字段后的 R/W/H 徽标可切换该节点字段权限（R只读 / W可编辑 / H隐藏）</div>
    </div>
</template>
<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { loadLFComponent, loadLFMultiFormComponent } from '@/views/workflow/components/componentload.js';

const props = defineProps({
    config: {
        type: Object,
        default: () => null
    },
    flowType: {
        type: String,
        default: 'LF'
    },
    selectedNode: {
        type: Object,
        default: () => null
    }
});

const USE_EXTERNAL_FORM_FLAG = 64;
const USE_AUXILIARY_FORM_FLAG = 128;

const loadedComponent = ref(null);
const noForm = ref(false);
const lfFormDataConfig = ref(null);
const lfFieldPermConfig = ref('[]');
const lfFormdataListConfig = ref([]);
const lfFieldsMultiConfig = ref({});
const lfFieldControlVOsConfig = ref([]);
const formHiddenConfig = ref({});

/** 重建 key：绑定当前节点，节点切换时重建表单以应用该节点字段权限标识 */
const formKey = computed(() => props.selectedNode?.nodeId || 'none');

/** 当前节点是否展示字段权限标识（审批类节点） */
const shouldShowPerm = computed(() => {
    const node = props.selectedNode;
    return !!node && [4, 7, 10, 12, 17].includes(node.nodeType);
});

/** 初始化表单渲染 */
const initFormRender = async () => {
    const config = props.config;
    if (!config) return;
    const flags = Number(config?.extraFlags || 0);
    if (props.flowType === 'LF') {
        const isExternal = (flags & USE_EXTERNAL_FORM_FLAG) === USE_EXTERNAL_FORM_FLAG;
        if (isExternal) {
            lfFormdataListConfig.value = config?.lfFormdataList || [];
            lfFieldsMultiConfig.value = {};
            formHiddenConfig.value = {};
            lfFormDataConfig.value = null;
            lfFieldPermConfig.value = '[]';
            loadedComponent.value = await loadLFMultiFormComponent();
        } else {
            lfFormDataConfig.value = config?.lfFormData;
            lfFieldPermConfig.value = '[]';
            lfFormdataListConfig.value = [];
            lfFieldsMultiConfig.value = {};
            formHiddenConfig.value = {};
            loadedComponent.value = await loadLFComponent();
        }
        noForm.value = false;
    } else {
        const isExternal = (flags & USE_EXTERNAL_FORM_FLAG) === USE_EXTERNAL_FORM_FLAG;
        const isAux = (flags & USE_AUXILIARY_FORM_FLAG) === USE_AUXILIARY_FORM_FLAG;
        if (isExternal) {
            // DIY + 外部表单：与 LF 外部表单一致，多表单渲染
            lfFormdataListConfig.value = config?.lfFormdataList || [];
            lfFieldsMultiConfig.value = {};
            formHiddenConfig.value = {};
            lfFormDataConfig.value = null;
            lfFieldPermConfig.value = '[]';
            loadedComponent.value = await loadLFMultiFormComponent();
            noForm.value = false;
        } else if (isAux) {
            // DIY + 辅助表单：辅助表单以只读 LF 表单渲染
            lfFormDataConfig.value = config?.lfFormData;
            lfFieldPermConfig.value = '[]';
            lfFormdataListConfig.value = [];
            lfFieldsMultiConfig.value = {};
            formHiddenConfig.value = {};
            loadedComponent.value = await loadLFComponent();
            noForm.value = false;
        } else {
            // 普通 DIY：无表单
            loadedComponent.value = null;
            noForm.value = true;
        }
    }
};

/** 节点切换时更新字段权限（formRender 通过 label 后徽标展示三态） */
const applyNodePerm = (node) => {
    const perms = (node && shouldShowPerm.value) ? (node.lfFieldControlVOs || []) : [];
    // 内联表单: formRender 读取 lfFieldPerm
    lfFieldPermConfig.value = JSON.stringify(perms);
    // 外部表单: MultiFormRender 读取 lfFieldControlVOs
    lfFieldControlVOsConfig.value = perms;
};

/** 点击徽标切换字段权限：写回当前节点 lfFieldControlVOs（Zen 右侧直接编辑） */
const handleUpdateFieldPerm = ({ fieldId, perm }) => {
    const node = props.selectedNode;
    if (!node) return;
    if (!node.lfFieldControlVOs) {
        node.lfFieldControlVOs = [];
    }
    const list = node.lfFieldControlVOs;
    const info = list.find(p => p.fieldId === fieldId);
    if (info) {
        info.perm = perm;
    } else {
        list.push({ fieldId, perm });
    }
    // 同步更新表单权限数据（保持徽标与数据一致）
    lfFieldPermConfig.value = JSON.stringify(list);
    lfFieldControlVOsConfig.value = list;
};

watch(() => props.selectedNode, (node) => {
    applyNodePerm(node);
}, { deep: true });

onMounted(async () => {
    await initFormRender();
    applyNodePerm(props.selectedNode);
});
</script>
<style scoped lang="scss">
.right-form-preview {
    display: flex;
    flex-direction: column;
    height: 100%;
}

.form-preview-area {
    flex: 1;
    overflow: auto;
    padding: 12px;
    background: #fafafa;
    min-height: 200px;
}

.perm-tip {
    padding: 8px 12px;
    font-size: 12px;
    color: #909399;
    border-top: 1px solid #f0f0f0;
    background: #fff;
    flex-shrink: 0;
}
</style>
