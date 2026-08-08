<template>
    <div class="zen-page">
        <errorDialog v-model:visible="tipVisible" :list="tipList" />
        <div class="zen-header">
            <div class="zen-header-title">
                <span>Zen模式</span>
                <el-tag v-if="flowType" size="small" type="info">{{ flowType === 'LF' ? '低代码流程' : 'DIY流程' }}</el-tag>
            </div>
            <div class="zen-header-toolbar">
                <el-button type="primary" @click="handleBack" :loading="backing">返回</el-button>
            </div>
        </div>
        <div class="zen-top-setting">
            <TopSetting :config="zenConfig" :flowType="flowType" />
        </div>
        <div class="zen-body" :class="{ 'zen-fill-mode': fillMode }">
            <Splitpanes ref="splitpanesRef" class="default-theme" :maximize-panes="false" @splitter-dblclick="handleResetSplitter">
                <!-- 左侧：流程图 -->
                <Pane class="zen-pane-left" :size="paneSizes.left" min-size="12">
                    <div class="zen-left">
                        <div class="zen-panel-title">流程图</div>
                        <div class="zen-flow-wrap">
                            <section class="antflow-design zen-flow-canvas" ref="flowCanvasRef">
                                <div class="zen-flow-zoom">
                                    <div class="zoom-out" @click="zoomOut" title="缩小"></div>
                                    <span>{{ zoomVal }}%</span>
                                    <div class="zoom-in" @click="zoomIn" title="放大"></div>
                                    <div class="zoom-reset" @click="zoomReset" title="还原缩放比例">&#10227</div>
                                </div>
                                <div class="box-scale" ref="flowScaleRef">
                                    <LineWarp v-if="nodeConfig" :nodeConfig="nodeConfig" />
                                    <div class="end-node">
                                        <div class="end-node-circle"></div>
                                        <div class="end-node-text">流程结束</div>
                                    </div>
                                </div>
                            </section>
                        </div>
                    </div>
                </Pane>
                <!-- 中间：节点属性面板（2×2 网格：第一排 审批人|按钮，第二排 通知|高级） -->
                <Pane class="zen-pane-center" :size="paneSizes.center" min-size="25">
                    <div class="zen-center">
                        <div class="zen-panel-title zen-center-title">
                            <span>节点设置</span>
                            <div class="zen-center-tools">
                                <el-button size="small" text type="primary" @click="toggleExpandCollapse">{{ allExpanded ? '全部折叠' : '全部展开' }}</el-button>
                                <el-button size="small" text type="primary" @click="toggleFill">{{ fillMode ? '退出充满' : '充满' }}</el-button>
                                <el-button size="small" text @click="resetLayout">重置</el-button>
                            </div>
                        </div>
                        <div class="zen-center-body">
                            <!-- 未选中节点：提示 -->
                            <div v-if="!selectedNode" class="zen-empty-tip">
                                <el-empty description="点击左侧流程节点，查看并编辑该节点属性" :image-size="80" />
                            </div>
                            <!-- 无属性节点：空白占位 -->
                            <div v-else-if="!isNodeEditable" class="zen-empty-tip">
                                <el-empty :description="`该节点（${selectedNode.nodeName}）无属性配置`" :image-size="80" />
                            </div>
                            <!-- 可编辑节点：两排网格 -->
                            <template v-else>
                                <!-- 第一排：审批人设置（左45%） | 按钮权限设置（右55%），完全联动——一个折叠两个都折叠 -->
                                <div class="zen-grid-row zen-grid-row-top" :class="{ 'zen-row-collapsed': firstRowCollapsed }">
                                    <div class="zen-grid-col zen-grid-col-left zen-panel-card">
                                        <div class="zen-panel-block-title collapsible" @click="toggleFirstRow">
                                            <span>审批人设置</span>
                                            <el-icon class="panel-caret">
                                                <ArrowDown v-if="!firstRowCollapsed" />
                                                <ArrowRight v-else />
                                            </el-icon>
                                        </div>
                                        <div v-show="!firstRowCollapsed" class="zen-panel-scroll">
                                            <ApproverStepPanel :approverConfig="selectedNode" :directorMaxLevel="3" />
                                        </div>
                                    </div>
                                    <div class="zen-grid-col zen-grid-col-right zen-panel-card">
                                        <div class="zen-panel-block-title collapsible" @click="toggleFirstRow">
                                            <span>按钮权限设置</span>
                                            <el-icon class="panel-caret">
                                                <ArrowDown v-if="!firstRowCollapsed" />
                                                <ArrowRight v-else />
                                            </el-icon>
                                        </div>
                                        <div v-show="!firstRowCollapsed" class="zen-panel-scroll">
                                            <ButtonStepPanel :approverConfig="selectedNode" :rootNode="nodeConfig"
                                                v-model:forwardFixedNodeId="forwardFixedNodeId"
                                                :availableForwardNodes="availableForwardNodes" :availableBackNodes="availableBackNodes" />
                                        </div>
                                    </div>
                                </div>
                                <!-- 第二排：通知设置（左45%） | 高级设置（右55%），完全联动——一个折叠两个都折叠 -->
                                <div class="zen-grid-row zen-grid-row-bottom" :class="{ 'zen-row-expanded': firstRowCollapsed }">
                                    <div class="zen-grid-col zen-grid-col-left zen-panel-card">
                                        <div class="zen-panel-block-title collapsible" @click="toggleSecondRow">
                                            <span>通知设置</span>
                                            <el-icon class="panel-caret">
                                                <ArrowDown v-if="!secondRowCollapsed" />
                                                <ArrowRight v-else />
                                            </el-icon>
                                        </div>
                                        <div v-show="!secondRowCollapsed" class="zen-panel-scroll">
                                            <NoticeStepPanel :approverConfig="selectedNode" />
                                        </div>
                                    </div>
                                    <div class="zen-grid-col zen-grid-col-right zen-panel-card">
                                        <div class="zen-panel-block-title collapsible" @click="toggleSecondRow">
                                            <span>高级设置</span>
                                            <el-icon class="panel-caret">
                                                <ArrowDown v-if="!secondRowCollapsed" />
                                                <ArrowRight v-else />
                                            </el-icon>
                                        </div>
                                        <div v-show="!secondRowCollapsed" class="zen-panel-scroll">
                                            <AdvancedStepPanel :approverConfig="selectedNode" />
                                        </div>
                                    </div>
                                </div>
                            </template>
                        </div>
                    </div>
                </Pane>
                <!-- 右侧：业务表单预览 + 字段三态 -->
                <Pane class="zen-pane-right" :size="paneSizes.right" min-size="15">
                    <div class="zen-right">
                        <div class="zen-panel-title">业务表单预览</div>
                        <div class="zen-right-body">
                            <RightFormPreview :config="zenConfig" :flowType="flowType" :selectedNode="selectedNode" />
                        </div>
                    </div>
                </Pane>
            </Splitpanes>
        </div>
    </div>
</template>
<script setup>
import { ref, computed, onMounted, provide } from 'vue';
import { Splitpanes, Pane } from 'splitpanes';
import 'splitpanes/dist/splitpanes.css';
import LineWarp from '@/components/Workflow/Preview/lineWarp.vue';
import ApproverStepPanel from '@/components/Workflow/drawer/panel/ApproverStepPanel.vue';
import ButtonStepPanel from '@/components/Workflow/drawer/panel/ButtonStepPanel.vue';

import NoticeStepPanel from '@/components/Workflow/drawer/panel/NoticeStepPanel.vue';
import AdvancedStepPanel from '@/components/Workflow/drawer/panel/AdvancedStepPanel.vue';
import TopSetting from './components/TopSetting.vue';
import RightFormPreview from './components/RightFormPreview.vue';
import errorDialog from '@/components/Workflow/dialog/errorDialog.vue';
import { useNodeForwardBack } from '@/components/Workflow/drawer/useNodeForwardBack';
import { validateBeforeReturn } from '@/utils/antflow/zenValidate';
import { wheelZoomFunc, zoomInit, resetImage } from '@/utils/antflow/zoom';
import { useStore } from '@/store/modules/workflow';
import { useDopeSheetStore } from '@/store/modules/dopeSheet';
import { extractFormFieldsMulti, extractFormFields } from '@/utils/antflow/formFieldExtractor';
import { ArrowDown, ArrowRight } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance();
const route = useRoute();
const store = useStore();
const dopeStore = useDopeSheetStore();

// ===== 表单 store 同步相关状态 =====
const USE_EXTERNAL_FORM_FLAG = 64;
const USE_AUXILIARY_FORM_FLAG = 128;

const zenConfig = ref(null);
const nodeConfig = ref(null);
const flowType = ref('LF');
const selectedNode = ref(null);
const backing = ref(false);
// 节点必填校验错误弹窗
const tipVisible = ref(false);
const tipList = ref([]);

// 第一排（审批人/按钮）折叠状态：完全联动，默认展开
const firstRowCollapsed = ref(false);
// 第二排（通知/高级）折叠状态：完全联动，默认展开
const secondRowCollapsed = ref(false);

// 三栏默认宽度比例（左/中/右）
const DEFAULT_PANE_SIZES = { left: 20, center: 50, right: 30 };
const paneSizes = reactive({ ...DEFAULT_PANE_SIZES });
const splitpanesRef = ref(null);
// 充满模式：隐藏左右两侧，中间占满
const fillMode = ref(false);

// 缩放相关
const flowCanvasRef = ref(null);
const flowScaleRef = ref(null);
const zoomVal = ref(100);

/** 当前节点是否可编辑（仅审批/办理/并行/条件审批/协助节点显示属性面板；发起人/抄送/条件/自动等显示空白） */
const isNodeEditable = computed(() => {
    if (!selectedNode.value) return false;
    const t = selectedNode.value.nodeType;
    return [4, 7, 10, 12, 17].includes(t);
});

// 推进/退回共享状态（按钮权限面板需要）
const {
    forwardFixedNodeId,
    availableForwardNodes,
    availableBackNodes,
    loadForwardConfig,
    loadAutoReturnConfig,
} = useNodeForwardBack(selectedNode, nodeConfig);

/** 点击流程图节点：设置当前选中节点 */
const handleClickNode = (data) => {
    selectedNode.value = data;
    // 同步推进/退回反显（与抽屉 watch 一致）
    loadForwardConfig(data);
    loadAutoReturnConfig(data);
};
provide('onClickNode', handleClickNode);

const toggleFirstRow = () => {
    firstRowCollapsed.value = !firstRowCollapsed.value;
};

const toggleSecondRow = () => {
    secondRowCollapsed.value = !secondRowCollapsed.value;
};

/** 两排是否全部展开（用于 toggle 按钮文案） */
const allExpanded = computed(() => !firstRowCollapsed.value && !secondRowCollapsed.value);

/** 全部展开/全部折叠 toggle */
const toggleExpandCollapse = () => {
    if (allExpanded.value) {
        // 当前全部展开 → 全部折叠
        firstRowCollapsed.value = true;
        secondRowCollapsed.value = true;
    } else {
        // 当前有折叠 → 全部展开
        firstRowCollapsed.value = false;
        secondRowCollapsed.value = false;
    }
};

/** 充满/退出充满：隐藏或显示左右两侧 */
const toggleFill = () => {
    fillMode.value = !fillMode.value;
};

/** 重置：恢复默认布局（默认比例 + 两排展开 + 退出充满） */
const resetLayout = () => {
    fillMode.value = false;
    firstRowCollapsed.value = false;
    secondRowCollapsed.value = false;
    handleResetSplitter();
};

/**
 * 双击分割线：重置三栏为默认比例
 * splitpanes 拖拽时只改内部状态、不回写 Pane 的 size prop，因此直接写默认值可能因
 * prop 未变化而不触发重新布局。这里先写一次偏离值，下一帧再写默认值，强制 prop 变化。
 */
const handleResetSplitter = () => {
    // 第一步：写一个偏离默认的比例，确保 prop 发生变化
    paneSizes.left = 33;
    paneSizes.center = 33;
    paneSizes.right = 34;
    nextTick(() => {
        // 第二步：恢复默认比例
        paneSizes.left = DEFAULT_PANE_SIZES.left;
        paneSizes.center = DEFAULT_PANE_SIZES.center;
        paneSizes.right = DEFAULT_PANE_SIZES.right;
    });
};

/** 同步表单相关 store 状态（面板组件依赖 store.lowCodeFormField / useExternalForm 等） */
const syncFormStore = (config) => {
    const flags = Number(config?.extraFlags || 0);
    const isExternal = (flags & USE_EXTERNAL_FORM_FLAG) === USE_EXTERNAL_FORM_FLAG;
    const isAux = (flags & USE_AUXILIARY_FORM_FLAG) === USE_AUXILIARY_FORM_FLAG;

    if (isExternal) {
        // 外部表单模式（LF / DIY 均可能）：字段来自多表单列表
        const formdataList = config?.lfFormdataList || [];
        store.setUseExternalForm(true);
        store.setUseAuxiliaryForm(false);
        store.setLfFormdataList(formdataList);
        store.setLowCodeFormFieldsMulti(extractFormFieldsMulti(formdataList));
        store.setLowCodeFormField({});
    } else if (isAux) {
        // DIY 辅助表单模式：字段来自辅助 vform
        store.setUseExternalForm(false);
        store.setUseAuxiliaryForm(true);
        store.setLfFormdataList([]);
        store.setLowCodeFormFieldsMulti([]);
        store.setLowCodeFormField(extractFormFields(config?.lfFormData));
    } else {
        // 内联表单（LF 内联）或无表单（DIY 普通）
        store.setUseExternalForm(false);
        store.setUseAuxiliaryForm(false);
        store.setLfFormdataList([]);
        store.setLowCodeFormFieldsMulti([]);
        store.setLowCodeFormField(extractFormFields(config?.lfFormData));
    }
};

// 从 store 读取 Zen 工作副本（setup 阶段立即执行，确保子组件挂载前 store 已同步）
const bootFromStore = () => {
    if (!dopeStore.zenConfig) {
        proxy.$modal.msgError('Zen模式数据不存在，请从DopeSheet进入');
        proxy.$tab.closePage();
        return false;
    }
    zenConfig.value = dopeStore.zenConfig;
    nodeConfig.value = dopeStore.zenConfig?.nodeConfig || null;
    // 优先使用 DopeSheet 已同步的 flowType（进入 Zen 前 DopeSheet 已加载版本并同步该值）
    flowType.value = dopeStore.flowType === 'DIY' ? 'DIY' : 'LF';
    syncFormStore(dopeStore.zenConfig);
    // 默认选中发起人节点
    if (nodeConfig.value) {
        selectedNode.value = nodeConfig.value;
        loadForwardConfig(nodeConfig.value);
        loadAutoReturnConfig(nodeConfig.value);
    }
    return true;
};
bootFromStore();

onMounted(() => {
    // 初始化缩放
    if (flowCanvasRef.value && flowScaleRef.value) {
        zoomInit(flowCanvasRef, flowScaleRef, (val) => { zoomVal.value = val });
    }
});

const zoomIn = () => {
    wheelZoomFunc({ scaleFactor: parseInt(zoomVal.value) / 100 + 0.1, isExternalCall: true })
};
const zoomOut = () => {
    wheelZoomFunc({ scaleFactor: parseInt(zoomVal.value) / 100 - 0.1, isExternalCall: true })
};
const zoomReset = () => {
    resetImage();
};

/** 返回：校验通过后写回 store 并跳转回 Dope Sheet（与完整编辑返回一致） */
const handleBack = async () => {
    if (!zenConfig.value) {
        proxy.$tab.closePage();
        return;
    }
    backing.value = true;
    try {
        const result = validateBeforeReturn(zenConfig.value.nodeConfig);
        if (!result.isSuccess) {
            if (result.tipList && result.tipList.length > 0) {
                // 节点必填错误：弹 errorDialog 展示具体节点（与完整设计一致）
                tipList.value = result.tipList;
                tipVisible.value = true;
                return;
            }
            if (result.emptyBtnNodes && result.emptyBtnNodes.length > 0) {
                proxy.$modal.msgError('以下节点未配置任何审批按钮：' + result.emptyBtnNodes.join('、'));
                return;
            }
            if (result.msg) {
                proxy.$modal.msgError(result.msg);
                return;
            }
            return;
        }
        // 校验通过：写回 store
        dopeStore.commitZen();
        // 跳转回 Dope Sheet（保留 formCode 以便 DopeSheet 定位版本）
        const formCode = dopeStore.formCode;
        proxy.$tab.closeOpenPage({ path: '/workflow/dopeSheet', query: { formCode } });
        proxy.$router.push({ path: '/workflow/dopeSheet', query: { formCode } });
    } finally {
        backing.value = false;
    }
};
</script>
<script>
export default { name: 'zen' };
</script>
<style scoped lang="scss">
@use "@/assets/styles/antflow/workflow.scss";

.zen-page {
    display: flex;
    flex-direction: column;
    height: calc(100vh - 84px);
    background: #f5f5f7;
    overflow: hidden;
}

.zen-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff;
    padding: 10px 16px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    z-index: 10;
}

.zen-top-setting {
    flex-shrink: 0;
    margin: 10px 10px 0;
}

.zen-header-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
}

.zen-body {
    flex: 1;
    min-height: 0;
    padding: 10px;
}

.zen-body :deep(.splitpanes) {
    height: 100%;
}

.zen-body :deep(.splitpanes__pane) {
    display: flex;
}

/* 充满模式：隐藏左右两侧，中间占满 */
.zen-body.zen-fill-mode :deep(.splitpanes__pane.zen-pane-left),
.zen-body.zen-fill-mode :deep(.splitpanes__pane.zen-pane-right) {
    display: none;
}

.zen-body.zen-fill-mode :deep(.splitpanes__pane.zen-pane-center) {
    flex: 1;
    width: 100%;
}

/* 中间标题栏：左侧标题 + 右侧工具按钮 */
.zen-center-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.zen-center-tools {
    display: flex;
    align-items: center;
    gap: 2px;
}

.zen-left,
.zen-center,
.zen-right {
    flex: 1;
    width: 100%;
    min-width: 0;
    height: 100%;
    background: #fff;
    border-radius: 6px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.zen-panel-title {
    padding: 10px 14px;
    font-size: 14px;
    font-weight: 600;
    border-bottom: 1px solid #f0f0f0;
    flex-shrink: 0;
}

.zen-flow-wrap {
    flex: 1;
    overflow: auto;
    padding: 10px;
}

.zen-flow-canvas {
    text-align: center;
}

.zen-flow-zoom {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
    color: #909399;
    font-size: 13px;

    .zoom-out,
    .zoom-in {
        width: 24px;
        height: 24px;
        border: 1px solid #dcdfe6;
        border-radius: 4px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        font-size: 16px;
        line-height: 1;
        color: #606266;
    }

    .zoom-out::before {
        content: "-";
    }

    .zoom-in::before {
        content: "+";
    }

    .zoom-reset {
        cursor: pointer;
        color: #606266;
    }
}

.box-scale {
    display: inline-block;
    transform-origin: top center;
}

.end-node {
    padding-top: 6px;

    .end-node-circle {
        width: 10px;
        height: 10px;
        margin: auto;
        border-radius: 50%;
        background: #dbdcdc;
    }

    .end-node-text {
        font-size: 12px;
        color: #909399;
        text-align: center;
        padding-top: 4px;
    }
}

.zen-center-body {
    flex: 1;
    min-height: 0;
    display: flex;
    flex-direction: column;
    gap: 12px;
    padding: 10px 12px;
}

.zen-empty-tip {
    display: flex;
    justify-content: center;
    align-items: center;
    flex: 1;
    min-height: 0;
}

/* ===== 2×2 网格布局 ===== */
/* 第一排：弹性占满剩余高度，两个面板各自内部滚动 */
.zen-grid-row-top {
    flex: 1;
    min-height: 0;
    display: flex;
    gap: 12px;
}

/* 第一排折叠后：收缩为标题条高度，把空间让给第二排 */
.zen-grid-row-top.zen-row-collapsed {
    flex: 0 0 auto;
}

/* 第二排：自适应高度，限高 40%（相对中间区域），两个面板各自内部滚动 */
.zen-grid-row-bottom {
    flex-shrink: 0;
    max-height: 40%;
    min-height: 0;
    display: flex;
    gap: 12px;
}

/* 第一排折叠时：第二排占满剩余高度 */
.zen-grid-row-bottom.zen-row-expanded {
    flex: 1;
    max-height: none;
}

/* 左列统一 45%，右列统一 55%，两排分栏线对齐 */
.zen-grid-col-left {
    flex: 0 0 45%;
    min-width: 0;
}

.zen-grid-col-right {
    flex: 1;
    min-width: 0;
}

/* 卡片：白底 + 圆角 + 浅阴影 */
.zen-panel-card {
    display: flex;
    flex-direction: column;
    min-height: 0;
    background: #fff;
    border-radius: 6px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
    overflow: hidden;
}

/* 卡片内容区：可滚动 */
.zen-panel-scroll {
    flex: 1;
    min-height: 0;
    overflow: auto;
    padding: 8px 12px;
}

.zen-panel-scroll :deep(.approver_content),
.zen-panel-scroll :deep(.approver_block),
.zen-panel-scroll :deep(.btn-row),
.zen-panel-scroll :deep(.disagree-back-conf) {
    margin-left: 0;
    margin-right: 0;
}

.zen-panel-block-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 12px;
    font-size: 14px;
    font-weight: 600;
    background: #f5f7fa;
    border-bottom: 1px solid #ebeef5;
    flex-shrink: 0;

    &.collapsible {
        cursor: pointer;
        user-select: none;
    }
}

.panel-caret {
    color: #909399;
    font-size: 14px;
}

.zen-right-body {
    flex: 1;
    overflow: auto;
}
</style>
