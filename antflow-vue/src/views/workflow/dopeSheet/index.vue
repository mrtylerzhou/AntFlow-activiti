<template>
  <div class="dope-sheet-container">
    <!-- 顶部工具栏 -->
    <div class="ds-toolbar">
      <div class="ds-toolbar-left">
        <span class="ds-title">Dope Sheet</span>
        <el-tag type="info" size="small">{{ dopeStore.formCodeName }}</el-tag>
        <el-tag :type="dopeStore.flowType === 'LF' ? 'success' : 'warning'" size="small">{{ dopeStore.flowType }}</el-tag>
      </div>
      <div class="ds-toolbar-right" v-if="!isEmpty">
        <el-select v-model="selectedVersionId" placeholder="选择版本" style="width: 200px; margin-right: 12px;"
          @change="handleVersionChange">
          <el-option v-for="v in versionList" :key="v.id" :label="v.bpmnCode + ' - ' + (v.bpmnName || '未命名')"
            :value="v.id" />
        </el-select>
        <el-tag v-if="currentVersionEffective" type="success" size="small" style="margin-right: 12px;">生效中</el-tag>
        <el-tag v-else type="info" size="small" style="margin-right: 12px;">未生效</el-tag>
        <el-button type="primary" @click="handlePublish" :loading="publishing">发布</el-button>
        <el-button @click="handlePreview">查看完整信息</el-button>
        <el-button @click="handleFullEdit">完整编辑</el-button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="isEmpty" class="ds-empty">
      <el-empty description="暂无版本数据，请先进行流程设计">
        <el-button type="primary" @click="handleDesignFlow">设计流程</el-button>
      </el-empty>
    </div>

    <!-- 主内容区 -->
    <div v-else class="ds-content" v-loading="loading">
      <!-- 流程级配置 -->
      <ProcessLevelConfig v-if="processConfig" :processConfig="processConfig" @dirty="onDirty" />
      <!-- 节点表格 -->
      <NodeTable v-if="processConfig && processConfig.nodeConfig" :nodeConfig="processConfig.nodeConfig"
        :flowType="dopeStore.flowType" @dirty="onDirty" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getBpmnConflistPage, getApiWorkFlowData, setApiWorkFlowData } from '@/api/workflow/index';
import { FormatDisplayUtils } from '@/utils/antflow/formatdisplay_data';
import { FormatCommitUtils } from '@/utils/antflow/formatcommit_data';
import { useDopeSheetStore } from '@/store/modules/dopeSheet';
import ProcessLevelConfig from './components/ProcessLevelConfig.vue';
import NodeTable from './components/NodeTable.vue';

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();
const dopeStore = useDopeSheetStore();

const loading = ref(false);
const publishing = ref(false);
const versionList = ref([]);
const selectedVersionId = ref(null);
const processConfig = ref(null);

const isEmpty = computed(() => versionList.value.length === 0);

/** 当前选中版本是否生效 */
const currentVersionEffective = computed(() => {
  const v = versionList.value.find(item => item.id === selectedVersionId.value);
  return v?.effectiveStatus == 1;
});

onMounted(async () => {
  // 确保 store 已初始化（刷新页面时从 query 恢复）
  if (!dopeStore.formCode && route.query.formCode) {
    dopeStore.init({
      formCode: route.query.formCode,
      formCodeName: '',
      flowType: ''
    });
  }
  // 从完整编辑返回：store 中已有最新数据，直接使用
  if (dopeStore.processConfig && dopeStore.dirty) {
    processConfig.value = dopeStore.processConfig;
    // 仍需加载版本列表供下拉框使用
    await loadVersionListOnly();
    return;
  }
  await loadVersionList();
});

/** 仅加载版本列表（不加载版本数据） */
const loadVersionListOnly = async () => {
  try {
    const pageDto = { page: 1, pageSize: 100 };
    const entity = { formCode: dopeStore.formCode, isOutSideProcess: 0 };
    const res = await getBpmnConflistPage(pageDto, entity);
    versionList.value = res.data?.data || [];
    if (versionList.value.length > 0) {
      selectedVersionId.value = dopeStore.currentVersionId || versionList.value[0].id;
    }
  } catch (err) {
    console.error('加载版本列表失败', err);
  }
};

/** 加载版本列表 */
const loadVersionList = async () => {
  loading.value = true;
  try {
    const pageDto = { page: 1, pageSize: 100 };
    const entity = { formCode: dopeStore.formCode, isOutSideProcess: 0 };
    const res = await getBpmnConflistPage(pageDto, entity);
    versionList.value = res.data?.data || [];
    if (versionList.value.length > 0) {
      // 默认选中最新版本（列表第一条）
      selectedVersionId.value = versionList.value[0].id;
      await loadVersionData(selectedVersionId.value);
    }
  } catch (err) {
    proxy.$modal.msgError("加载版本列表失败: " + err.message);
  } finally {
    loading.value = false;
  }
};

/** 加载指定版本的设计数据 */
const loadVersionData = async (versionId) => {
  loading.value = true;
  try {
    const res = await getApiWorkFlowData({ id: versionId });
    if (res.code != 200) {
      proxy.$modal.msgError("获取数据失败: " + res.errMsg);
      return;
    }
    const data = FormatDisplayUtils.getToTree(res.data);
    processConfig.value = data;
    dopeStore.setProcessConfig(data);
    dopeStore.setCurrentVersionId(versionId);
    dopeStore.resetDirty();
    // 同步 flowType
    if (data.isLowCodeFlow == '1') {
      dopeStore.flowType = 'LF';
    } else {
      dopeStore.flowType = 'DIY';
    }
  } catch (err) {
    proxy.$modal.msgError("加载版本数据失败: " + err.message);
  } finally {
    loading.value = false;
  }
};

/** 版本切换 */
const handleVersionChange = async (newId) => {
  if (dopeStore.dirty) {
    try {
      await proxy.$modal.confirm("当前有未保存的修改，切换将丢失，是否继续？");
    } catch {
      // 用户取消，恢复选中
      selectedVersionId.value = dopeStore.currentVersionId;
      return;
    }
  }
  await loadVersionData(newId);
};

/** 标记脏数据 */
const onDirty = () => {
  dopeStore.markDirty();
};

/** 发布 */
const handlePublish = async () => {
  if (!processConfig.value) return;
  // 校验按钮权限
  const emptyBtnNodes = validateButtonPerms(processConfig.value.nodeConfig);
  if (emptyBtnNodes.length > 0) {
    proxy.$modal.msgError("以下节点未配置任何审批按钮：" + emptyBtnNodes.join("、"));
    return;
  }
  publishing.value = true;
  try {
    const basicData = buildPublishData();
    const res = await setApiWorkFlowData(basicData);
    if (res.code == 200) {
      proxy.$modal.msgSuccess("发布成功，已创建新版本");
      dopeStore.resetDirty();
      // 重新加载版本列表并选中新版本
      await loadVersionList();
    } else {
      proxy.$modal.msgError("发布失败: " + res.errMsg);
    }
  } catch (err) {
    proxy.$modal.msgError("发布失败: " + err.message);
  } finally {
    publishing.value = false;
  }
};

/** 构建发布数据（与设计器一致） */
const buildPublishData = () => {
  const config = processConfig.value;
  const nodes = FormatCommitUtils.formatSettings(JSON.parse(JSON.stringify(config.nodeConfig)));
  const data = {
    formCode: config.formCode,
    bpmnName: config.bpmnName,
    bpmnType: config.bpmnType,
    isLowCodeFlow: config.isLowCodeFlow,
    remark: config.remark,
    deduplicationType: config.deduplicationType,
    viewPageButtons: config.viewPageButtons,
    nodes: nodes,
  };
  // LF 流程附带表单数据
  if (config.isLowCodeFlow == '1' && config.lfFormData) {
    data.lfFormData = typeof config.lfFormData === 'string' ? config.lfFormData : JSON.stringify(config.lfFormData);
  }
  if (config.lfFormDataId) {
    data.lfFormDataId = config.lfFormDataId;
  }
  if (config.extraFlags) {
    data.extraFlags = config.extraFlags;
  }
  if (config.lfFormdataIds) {
    data.lfFormdataIds = config.lfFormdataIds;
  }
  if (config.templateVos && config.templateVos.length > 0) {
    data.templateVos = config.templateVos;
  }
  return data;
};

/** 校验按钮权限：审批节点(nodeType=4/7/10/12)必须有按钮 */
const validateButtonPerms = (nodeConfig) => {
  const emptyNodes = [];
  const traverse = (node) => {
    if (!node) return;
    // 审批人节点、办理节点、条件审批节点需要按钮
    if ([4, 10, 12].includes(node.nodeType)) {
      const btns = node.buttons?.approvalPage || [];
      if (btns.length === 0) {
        emptyNodes.push(node.nodeName || '未命名节点');
      }
    }
    // 并行审批的子节点
    if (node.nodeType === 7 && node.parallelNodes) {
      node.parallelNodes.forEach(pn => {
        const btns = pn.buttons?.approvalPage || [];
        if (btns.length === 0) {
          emptyNodes.push(pn.nodeName || '未命名节点');
        }
      });
    }
    if (node.childNode) traverse(node.childNode);
    if (node.conditionNodes) {
      node.conditionNodes.forEach(cn => {
        if (cn.childNode) traverse(cn.childNode);
      });
    }
  };
  traverse(nodeConfig);
  return emptyNodes;
};

/** 查看完整信息（预览） */
const handlePreview = () => {
  dopeStore.setProcessConfig(processConfig.value);
  const obj = { path: "/workflow/flowPreview", query: { mode: 'store' } };
  proxy.$tab.openPage(obj);
};

/** 完整编辑 */
const handleFullEdit = () => {
  dopeStore.setProcessConfig(processConfig.value);
  dopeStore.enterFullEdit();
  const designPath = dopeStore.flowType === 'LF' ? "/workflow/lf-design" : "/workflow/diy-design";
  const obj = { path: designPath, query: { mode: 'store' } };
  proxy.$tab.openPage(obj);
};

/** 空状态：设计流程 */
const handleDesignFlow = () => {
  const param = {
    fcname: encodeURIComponent(dopeStore.formCodeName),
    fc: dopeStore.formCode
  };
  const designPath = dopeStore.flowType === 'LF' ? "/workflow/lf-design" : "/workflow/diy-design";
  const obj = { path: designPath, query: param };
  proxy.$tab.closeOpenPage(obj);
};

// 监听从完整编辑返回
watch(() => route.query, (newQuery) => {
  if (newQuery.formCode && dopeStore.active && !dopeStore.fullEditMode) {
    // 从完整编辑返回，store 中已有最新数据
    if (dopeStore.processConfig) {
      processConfig.value = dopeStore.processConfig;
    }
  }
}, { deep: true });

// 组件激活时（从设计器返回）同步数据
onActivated(() => {
  if (dopeStore.processConfig && dopeStore.active) {
    processConfig.value = dopeStore.processConfig;
  }
});
</script>

<script>
export default { name: 'dopeSheet' };
</script>

<style scoped lang="scss">
.dope-sheet-container {
  padding: 16px;
  background: #f5f5f7;
  min-height: calc(100vh - 85px);
}

.ds-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 12px 20px;
  border-radius: 6px;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.ds-toolbar-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.ds-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.ds-toolbar-right {
  display: flex;
  align-items: center;
}

.ds-empty {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  background: #fff;
  border-radius: 6px;
}

.ds-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
</style>
