<!--
 * 横向设计器 - 拖线松手后弹出的"添加节点"菜单
 * 内容与 addNode.vue 完全一致(29 种节点 + 克隆器), 创建逻辑复用 NodeUtils 工厂
 * 创建后: newNode.childNode = fromNode.childNode(原后续), 插入 fromNode 之后
-->
<template>
  <Teleport to="body">
    <div v-if="visible" class="hd-menu-mask" @mousedown.self="close">
      <div class="hd-menu" :style="{ left: x + 'px', top: y + 'px' }">
        <div class="hd-menu-groups">
          <div class="hd-menu-row">
            <a class="hd-menu-item" v-for="it in items" :key="it.type" :class="it.cls" @click="pick(it.type)">
              <div class="hd-menu-item-box">
                <svg-icon :icon-class="it.icon" class="hd-menu-icon" />
                <p>{{ it.name }}</p>
              </div>
            </a>
          </div>
        </div>
      </div>
    </div>
    <!-- 克隆器弹窗 -->
    <el-dialog v-model="cloneDialogVisible" title="克隆节点" width="420px" append-to-body>
      <div v-if="cloneableNodes.length === 0" style="text-align: center; padding: 20px 0; color: #999;">
        当前流程没有可克隆的审批人/抄送人V2节点
      </div>
      <div v-else>
        <el-form label-width="80px">
          <el-form-item label="选择节点">
            <el-select v-model="selectedCloneNodeId" placeholder="请选择要克隆的节点" style="width: 100%;">
              <el-option v-for="item in cloneableNodes" :key="item.nodeId"
                :label="item.nodeName + (item.nodeType === 4 ? '（审批人）' : '（抄送人V2）')" :value="item.nodeId" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="cloneDialogVisible = false">取 消</el-button>
        <el-button type="primary" :disabled="!selectedCloneNodeId" @click="confirmClone">确 定</el-button>
      </template>
    </el-dialog>
  </Teleport>
</template>
<script setup>
import { ref, inject } from 'vue'
import { NodeUtils } from '@/utils/antflow/nodeUtils'

const props = defineProps({
  visible: { type: Boolean, default: false },
  x: { type: Number, default: 0 },
  y: { type: Number, default: 0 },
  fromNode: { type: Object, default: null }, // 拖线来源节点(树引用)
})
const emit = defineEmits(['close', 'created'])
const rootNode = inject('rootNode', null)
const cloneDialogVisible = ref(false)
const selectedCloneNodeId = ref(null)
const cloneableNodes = ref([])

const items = [
  { type: 1, name: '审批人', icon: 'approve', cls: 'approver' },
  { type: 3, name: '并行审批', icon: 'parallel-approve', cls: 'approver' },
  { type: 2, name: '抄送人', icon: 'copy-user', cls: 'notifier' },
  { type: 8, name: '抄送人V2', icon: 'copy-user', cls: 'notifier-v2' },
  { type: 4, name: '条件分支', icon: 'condition', cls: 'condition' },
  { type: 5, name: '动态条件', icon: 'dynamic-condition', cls: 'condition' },
  { type: 6, name: '条件并行', icon: 'parallel-condition', cls: 'condition' },
  { type: 9, name: '自动节点', icon: 'auto-approve', cls: 'auto-node' },
  { type: 10, name: '办理节点', icon: 'handle-task', cls: 'process-node' },
  { type: 11, name: '自动办理', icon: 'auto-task', cls: 'auto-process-node' },
  { type: 99, name: '克隆器', icon: 'approver-clone', cls: 'cloner-node' },
  { type: 12, name: '条件审批', icon: 'condition-approve', cls: 'condition-approver-node' },
  { type: 13, name: '条件抄送', icon: 'condition-copy', cls: 'condition-copy-node' },
  { type: 14, name: '选择条件', icon: 'pick-condition', cls: 'pick-condition-node' },
  { type: 15, name: '动态条件并行', icon: 'dynamic-condition', cls: 'dynamic-parallel-node' },
  { type: 16, name: '选择动态条件并行', icon: 'select-dynamic-parallel', cls: 'select-dynamic-parallel-node' },
  { type: 17, name: '协助', icon: 'assist', cls: 'assist-node' },
  { type: 18, name: '自动推进', icon: 'auto-drive-ahead', cls: 'auto-advance-node' },
  { type: 19, name: '推进审批', icon: 'approver-drive-ahead', cls: 'forward-approver-node' },
  { type: 20, name: '完成审批', icon: 'finish-process', cls: 'finish-approver-node' },
  { type: 21, name: '自动完成', icon: 'auto-finish', cls: 'auto-complete-node' },
  { type: 22, name: '条件推进', icon: 'conditional-drive-ahead', cls: 'condition-advance-node' },
  { type: 23, name: '条件完成', icon: 'condition-finish-process', cls: 'condition-finish-node' },
  { type: 24, name: '退回审批', icon: 'drive-back', cls: 'back-approver-node' },
  { type: 25, name: '退回发起人', icon: 'drive-tostarter', cls: 'back-starter-node' },
  { type: 26, name: '自动退回', icon: 'auto-drive-back', cls: 'auto-return-node' },
  { type: 27, name: '自动退回发起人', icon: 'auto-drive-to-starter', cls: 'auto-return-starter-node' },
  { type: 28, name: '条件退回', icon: 'conditional-drive-back', cls: 'condition-return-node' },
  { type: 29, name: '条件退回发起人', icon: 'conditional-drive-to-starter', cls: 'condition-return-starter-node' },
]

const createNodeMap = new Map([
  [1, (c) => NodeUtils.createApproveNode(c)],
  [2, (c) => NodeUtils.createCopyNode(c)],
  [3, (c) => NodeUtils.createParallelWayNode(c)],
  [4, (c) => NodeUtils.createGatewayNode(c)],
  [5, (c) => NodeUtils.createDynamicConditionWayNode(c)],
  [6, (c) => NodeUtils.createParallelConditionWayNode(c)],
  [8, (c) => NodeUtils.createCopyNodeV2(c)],
  [9, (c) => NodeUtils.createAutoNode(c)],
  [10, (c) => NodeUtils.createProcessNode(c)],
  [11, (c) => NodeUtils.createAutoProcessNode(c)],
  [12, (c) => NodeUtils.createConditionApproveNode(c)],
  [13, (c) => NodeUtils.createConditionCopyNode(c)],
  [14, (c) => NodeUtils.createPickConditionNode(c)],
  [15, (c) => NodeUtils.createDynamicConditionParallelNode(c)],
  [16, (c) => NodeUtils.createSelectDynamicParallelNode(c)],
  [17, (c) => NodeUtils.createAssistNode(c)],
  [18, (c) => NodeUtils.createAutoAdvanceNode(c)],
  [19, (c) => NodeUtils.createForwardApproveNode(c)],
  [20, (c) => NodeUtils.createFinishApproveNode(c)],
  [21, (c) => NodeUtils.createAutoCompleteNode(c)],
  [22, (c) => NodeUtils.createConditionAdvanceNode(c)],
  [23, (c) => NodeUtils.createConditionFinishNode(c)],
  [24, (c) => NodeUtils.createBackApproveNode(c)],
  [25, (c) => NodeUtils.createBackStarterNode(c)],
  [26, (c) => NodeUtils.createAutoReturnNode(c)],
  [27, (c) => {
    let starterNodeId = null
    if (rootNode && rootNode.value) {
      const starters = NodeUtils.collectNodesByType(rootNode.value, [1])
      if (starters.length > 0) starterNodeId = starters[0].nodeId
    }
    return NodeUtils.createAutoReturnStarterNode(c, starterNodeId)
  }],
  [28, (c) => NodeUtils.createConditionReturnNode(c)],
  [29, (c) => NodeUtils.createConditionReturnStarterNode(c, rootNode ? rootNode.value : null)],
])

const pick = (type) => {
  if (type === 99) {
    cloneDialogVisible.value = true
    selectedCloneNodeId.value = null
    if (rootNode && rootNode.value) {
      cloneableNodes.value = NodeUtils.collectNodesByType(rootNode.value, [4, 8])
    } else {
      cloneableNodes.value = []
    }
    return
  }
  if (!props.fromNode) return
  const factory = createNodeMap.get(type)
  if (!factory) return
  const newNode = factory(props.fromNode.childNode) // 新节点后续 = 原后续
  props.fromNode.childNode = newNode
  emit('created')
  close()
}

const confirmClone = () => {
  const sourceNode = cloneableNodes.value.find((n) => n.nodeId === selectedCloneNodeId.value)
  if (!sourceNode || !props.fromNode) return
  const clonedNode = NodeUtils.cloneNode(sourceNode, props.fromNode.childNode)
  props.fromNode.childNode = clonedNode
  emit('created')
  cloneDialogVisible.value = false
  close()
}

const close = () => emit('close')
</script>
<style scoped>
.hd-menu-mask {
  position: fixed;
  inset: 0;
  z-index: 3000;
  background: transparent;
}
.hd-menu {
  position: absolute;
  background: #fff;
  border: 1px solid #e2e2e2;
  border-radius: 8px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  padding: 10px;
  max-width: 320px;
  max-height: 70vh;
  overflow: auto;
}
.hd-menu-row {
  display: flex;
  flex-wrap: wrap;
}
.hd-menu-item {
  cursor: pointer;
  text-align: center;
  color: #191f25;
  margin: 4px;
  text-decoration: none;
}
.hd-menu-item-box {
  width: 60px;
  height: 62px;
  background: #fff;
  border: 1px solid #e2e2e2;
  border-radius: 10%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}
.hd-menu-item:hover .hd-menu-item-box {
  background: #3296fa;
  box-shadow: 0 10px 20px rgba(50, 150, 250, 0.4);
}
.hd-menu-item:hover .hd-menu-icon,
.hd-menu-item:hover p {
  color: #fff;
}
.hd-menu-icon {
  font-size: 24px;
}
.hd-menu-item p {
  margin: 2px 0 0;
  font-size: 12px;
  font-weight: 500;
  color: #000;
}
.approver .hd-menu-icon { color: #ff943e; }
.notifier .hd-menu-icon { color: #3296fa; }
.notifier-v2 .hd-menu-icon { color: #0460bb; }
.condition .hd-menu-icon { color: #15bc83; }
.auto-node .hd-menu-icon { color: #9b59b6; }
.process-node .hd-menu-icon { color: #15bc83; }
.auto-process-node .hd-menu-icon { color: #9b59b6; }
.cloner-node .hd-menu-icon { color: #e67e22; }
.condition-approver-node .hd-menu-icon { color: #2ea7a7; }
.condition-copy-node .hd-menu-icon { color: #4682b4; }
.pick-condition-node .hd-menu-icon { color: #e74c3c; }
.dynamic-parallel-node .hd-menu-icon { color: #e74c3c; }
.select-dynamic-parallel-node .hd-menu-icon { color: #8e44ad; }
.assist-node .hd-menu-icon { color: #5c6bc0; }
.auto-advance-node .hd-menu-icon { color: #45b7a5; }
.forward-approver-node .hd-menu-icon { color: #059669; }
.finish-approver-node .hd-menu-icon { color: #96286e; }
.auto-complete-node .hd-menu-icon { color: #3f51b5; }
.condition-advance-node .hd-menu-icon { color: #e6a23c; }
.condition-finish-node .hd-menu-icon { color: #96286e; }
.back-approver-node .hd-menu-icon,
.back-starter-node .hd-menu-icon,
.auto-return-node .hd-menu-icon,
.auto-return-starter-node .hd-menu-icon,
.condition-return-node .hd-menu-icon,
.condition-return-starter-node .hd-menu-icon { color: #e53935; }
</style>
