<template>
  <div class="node-table-wrapper">
    <div class="section-title">节点配置</div>
    <el-table :data="flatNodes" border stripe size="small" style="width: 100%;" row-key="nodeId">
      <!-- 节点名称 -->
      <el-table-column label="节点名称" min-width="160" fixed="left">
        <template #default="{ row }">
          <div class="node-name-cell">
            <span>{{ row.nodeName }}</span>
            <el-tag v-if="row.branchLabel" size="small" type="info" style="margin-left: 4px;">{{ row.branchLabel }}</el-tag>
            <el-tag size="small" :type="nodeTypeTagType(row.nodeType)" style="margin-left: 4px;">
              {{ nodeTypeLabel(row.nodeType) }}
            </el-tag>
            <el-tag v-if="row.isPickCondition" size="small" type="warning" style="margin-left: 4px;">选择条件</el-tag>
          </div>
        </template>
      </el-table-column>

      <!-- 审批人设置 -->
      <el-table-column label="审批人设置" min-width="180">
        <template #default="{ row }">
          <template v-if="isAutoNode(row)">—</template>
          <template v-else>
            <span class="approver-summary">{{ getApproverSummary(row) }}</span>
          </template>
        </template>
      </el-table-column>

      <!-- 多人审批方式 -->
      <el-table-column label="审批方式" width="130">
        <template #default="{ row }">
          <template v-if="isAutoNode(row) || !isApproverLike(row)">—</template>
          <template v-else>
            <el-select v-model="row.signType" size="small" :disabled="row.setType === 2"
              @change="(val) => onSignTypeChange(row, val)" style="width: 110px;">
              <el-option :value="1" label="会签" />
              <el-option :value="2" label="或签" />
              <el-option :value="3" label="顺序会签" v-if="row.setType === 5" />
              <el-option :value="4" label="仲裁签" />
            </el-select>
          </template>
        </template>
      </el-table-column>

      <!-- 审批人为空处理 -->
      <el-table-column label="为空处理" width="130">
        <template #default="{ row }">
          <template v-if="isAutoNode(row) || !isApproverLike(row)">—</template>
          <template v-else>
            <el-select v-model="row.noHeaderAction" size="small" @change="emitDirty" style="width: 110px;">
              <el-option :value="0" label="不允许发起" />
              <el-option :value="1" label="跳过" />
              <el-option :value="2" label="转交管理员" />
            </el-select>
          </template>
        </template>
      </el-table-column>

      <!-- 按钮权限 -->
      <el-table-column label="按钮权限" min-width="180">
        <template #default="{ row }">
          <template v-if="isAutoNode(row) || !isApproverLike(row)">—</template>
          <template v-else>
            <div class="btn-perm-cell">
              <span class="btn-summary">{{ getButtonSummary(row) }}</span>
              <el-button link type="primary" size="small" @click="openButtonPerm(row)">编辑</el-button>
            </div>
          </template>
        </template>
      </el-table-column>

      <!-- 表单权限 -->
      <el-table-column label="表单权限" width="100">
        <template #default="{ row }">
          <template v-if="isAutoNode(row) || !isApproverLike(row)">—</template>
          <template v-else>
            <el-button link type="primary" size="small" @click="openFormPerm(row)">
              {{ (row.lfFieldControlVOs || []).length > 0 ? '已配置' : '配置' }}
            </el-button>
          </template>
        </template>
      </el-table-column>

      <!-- 通知设置 -->
      <el-table-column label="通知设置" min-width="160">
        <template #default="{ row }">
          <template v-if="isAutoNode(row)">—</template>
          <template v-else>
            <div class="btn-perm-cell">
              <span class="notice-summary">{{ getNoticeSummary(row) }}</span>
              <el-button link type="primary" size="small" @click="openNotice(row)">编辑</el-button>
            </div>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <!-- 按钮权限弹窗 -->
    <ButtonPermDialog v-model:visible="buttonPermVisible" :node="currentEditNode" @confirm="onButtonPermConfirm" />
    <!-- 通知设置弹窗 -->
    <NoticeDialog v-model:visible="noticeVisible" :node="currentEditNode" @confirm="onNoticeConfirm" />
    <!-- 表单权限弹窗 -->
    <FormPermDialog v-model:visible="formPermVisible" :node="currentEditNode" @confirm="onFormPermConfirm" />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { nodeTypeList, signTypeObj, setTypes, approvalButtonConf } from '@/utils/antflow/const';
import ButtonPermDialog from './ButtonPermDialog.vue';
import NoticeDialog from './NoticeDialog.vue';
import FormPermDialog from './FormPermDialog.vue';

const props = defineProps({
  nodeConfig: {
    type: Object,
    required: true
  },
  flowType: {
    type: String,
    default: 'LF'
  }
});

const emit = defineEmits(['dirty']);

// 排除的节点类型：网关(2)、条件(3)、条件审批(12)、条件抄送(13)
const EXCLUDED_TYPES = [2, 3, 12, 13];

/** 将树形结构展平为列表（DFS） */
const flatNodes = computed(() => {
  const result = [];
  const traverse = (node, branchLabel) => {
    if (!node) return;
    // 并行网关节点本身不显示，显示其子分支
    if (node.nodeType === 7) {
      if (node.parallelNodes && node.parallelNodes.length > 0) {
        node.parallelNodes.forEach((pn, idx) => {
          const label = `[分支${idx + 1}]`;
          addNode(pn, label, result);
          // 并行分支内的后续节点
          if (pn.childNode) traverse(pn.childNode, label);
        });
      }
      // 并行网关的聚合后续节点
      if (node.childNode) traverse(node.childNode, branchLabel);
      return;
    }
    // 条件网关：遍历各条件分支
    if (node.nodeType === 2) {
      if (node.conditionNodes && node.conditionNodes.length > 0) {
        node.conditionNodes.forEach(cn => {
          const condLabel = cn.nodeName || '条件';
          if (cn.childNode) traverse(cn.childNode, `[${condLabel}]`);
        });
      }
      if (node.childNode) traverse(node.childNode, branchLabel);
      return;
    }
    // 普通节点
    addNode(node, branchLabel, result);
    // 如果有条件分支（某些节点可能带 conditionNodes）
    if (node.conditionNodes && node.conditionNodes.length > 0) {
      node.conditionNodes.forEach(cn => {
        const condLabel = cn.nodeName || '条件';
        if (cn.childNode) traverse(cn.childNode, `[${condLabel}]`);
      });
    }
    if (node.childNode) traverse(node.childNode, branchLabel);
  };

  const addNode = (node, branchLabel, list) => {
    if (EXCLUDED_TYPES.includes(node.nodeType)) return;
    list.push({ ...node, _raw: node, branchLabel: branchLabel || '' });
  };

  traverse(props.nodeConfig, '');
  // 返回时引用原始对象以便直接修改
  return result.map(item => item._raw ? Object.assign(item._raw, { branchLabel: item.branchLabel }) : item);
});

/** 判断是否自动节点 */
const isAutoNode = (row) => [9, 11].includes(row.nodeType);

/** 判断是否审批类节点（有审批方式/按钮等配置） */
const isApproverLike = (row) => [1, 4, 7, 10].includes(row.nodeType);

/** 节点类型标签颜色 */
const nodeTypeTagType = (type) => {
  const map = { 1: 'success', 4: 'primary', 6: 'warning', 7: 'primary', 8: 'warning', 9: 'info', 10: 'primary', 11: 'info' };
  return map[type] || 'info';
};

/** 节点类型文字 */
const nodeTypeLabel = (type) => nodeTypeList[type] || '未知';

/** 审批人摘要 */
const getApproverSummary = (row) => {
  if (row.nodeType === 1) return '发起人';
  const typeLabel = setTypes.find(t => t.value === row.setType)?.label || '';
  const names = (row.nodeApproveList || []).map(a => a.name).join('、');
  let summary = typeLabel;
  if (names) summary += `：${names}`;
  // 额外增加/排除
  const additional = row.property?.additionalSignInfoList || [];
  if (additional.length > 0) {
    const addCount = additional.filter(a => a.propertyType === 1).reduce((s, a) => s + (a.signInfos?.length || 0), 0);
    const exCount = additional.filter(a => a.propertyType === 2).reduce((s, a) => s + (a.signInfos?.length || 0), 0);
    if (addCount > 0) summary += ` [+${addCount}]`;
    if (exCount > 0) summary += ` [-${exCount}]`;
  }
  return summary || '—';
};

/** 按钮权限摘要 */
const getButtonSummary = (row) => {
  const btns = row.buttons?.approvalPage || [];
  if (btns.length === 0) return '未配置';
  const names = btns.map(b => {
    const defaultName = approvalButtonConf.buttonsObj[b.buttonType] || '';
    return b.buttonName || defaultName;
  });
  return names.join('、');
};

/** 通知设置摘要 */
const getNoticeSummary = (row) => {
  const tvs = row.templateVos || [];
  if (tvs.length === 0) return '未配置';
  const tv = tvs[0];
  const parts = [];
  if (tv.messageSendTypeList?.length) parts.push(`${tv.messageSendTypeList.length}种通知`);
  if (tv.event) parts.push(`事件${tv.event}`);
  return parts.join('，') || '已配置';
};

/** signType 变更处理（含业务约束） */
const onSignTypeChange = (row, val) => {
  if (!row.property) row.property = {};
  if (val === 4) {
    // 仲裁签：初始化通过比例，勾选反对，取消不同意，同意自定义名设为"赞成"
    if (!row.property.arbitrationRatio) row.property.arbitrationRatio = 100;
    if (!row.buttons) row.buttons = {};
    let approvalPage = row.buttons.approvalPage || [];
    // 移除不同意(4)
    approvalPage = approvalPage.filter(b => b.buttonType !== approvalButtonConf.noAgree);
    // 添加反对(39)
    if (!approvalPage.find(b => b.buttonType === approvalButtonConf.oppose)) {
      approvalPage.push({ buttonType: approvalButtonConf.oppose, buttonName: '' });
    }
    // 同意(3)自定义名设为"赞成"
    const agreeBtn = approvalPage.find(b => b.buttonType === approvalButtonConf.agree);
    if (agreeBtn) agreeBtn.buttonName = '赞成';
    else approvalPage.push({ buttonType: approvalButtonConf.agree, buttonName: '赞成' });
    row.buttons.approvalPage = approvalPage;
  } else if (row.signType !== 4 && val !== 4) {
    // 切离仲裁签：清空"赞成"自定义名
    const approvalPage = row.buttons?.approvalPage || [];
    const agreeBtn = approvalPage.find(b => b.buttonType === approvalButtonConf.agree);
    if (agreeBtn && agreeBtn.buttonName === '赞成') agreeBtn.buttonName = '';
  }
  emitDirty();
};

// ===== 弹窗相关 =====
const buttonPermVisible = ref(false);
const noticeVisible = ref(false);
const formPermVisible = ref(false);
const currentEditNode = ref(null);

const openButtonPerm = (row) => {
  currentEditNode.value = row;
  buttonPermVisible.value = true;
};
const openNotice = (row) => {
  currentEditNode.value = row;
  noticeVisible.value = true;
};
const openFormPerm = (row) => {
  currentEditNode.value = row;
  formPermVisible.value = true;
};

const onButtonPermConfirm = () => {
  emitDirty();
};
const onNoticeConfirm = (data) => {
  if (currentEditNode.value) {
    currentEditNode.value.templateVos = data ? [data] : [];
  }
  emitDirty();
};
const onFormPermConfirm = (data) => {
  if (currentEditNode.value) {
    currentEditNode.value.lfFieldControlVOs = data;
  }
  emitDirty();
};

const emitDirty = () => emit('dirty');
</script>

<style scoped lang="scss">
.node-table-wrapper {
  background: #fff;
  border-radius: 6px;
  padding: 16px 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-left: 8px;
  border-left: 3px solid #409eff;
}

.node-name-cell {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.approver-summary {
  font-size: 12px;
  color: #606266;
}

.btn-perm-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-summary,
.notice-summary {
  font-size: 12px;
  color: #606266;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
