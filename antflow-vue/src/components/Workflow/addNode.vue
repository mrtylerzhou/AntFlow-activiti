<template>
    <div class="add-node-btn-box">
        <div class="add-node-btn">
            <el-popover placement="right-start" v-model="visible" aria-hidden="true" width="auto">
                <div class="add-node-popover-body">
                    <a class="add-node-popover-item approver" @click="addType(1)">
                        <div class="item-wrapper">
                            <svg-icon icon-class="approve" class="iconfont" />
                            <p>审批人</p>
                        </div>
                    </a>
                    <a class="add-node-popover-item approver" @click="addType(3)">
                        <div class="item-wrapper">
                            <svg-icon icon-class="parallel-approve" class="iconfont" />
                            <p>并行审批</p>
                        </div>
                    </a>
                    <a class="add-node-popover-item notifier" @click="addType(2)">
                        <div class="item-wrapper">
                            <svg-icon icon-class="copy-user" class="iconfont" />
                            <p>抄送人</p>
                        </div>
                    </a>
                    <a class="add-node-popover-item notifier-v2" @click="addType(8)">
                        <div class="item-wrapper">
                            <svg-icon icon-class="copy-user" class="iconfont" />
                            <p>抄送人V2</p>
                        </div>
                    </a>
                </div>
                <div class="add-node-popover-body">
                    <a class="add-node-popover-item condition" @click="addType(4)">
                        <div class="item-wrapper">
                            <svg-icon icon-class="condition" class="iconfont" />
                            <p>条件分支</p>
                        </div>
                    </a>
                    <a class="add-node-popover-item condition" @click="addType(5)">
                        <div class="item-wrapper">
                            <svg-icon icon-class="dynamic-condition" class="iconfont" />
                            <p>动态条件</p>
                        </div>
                    </a>
                    <a class="add-node-popover-item condition" @click="addType(6)">
                        <div class="item-wrapper">
                            <svg-icon icon-class="parallel-condition" class="iconfont" />
                            <p>条件并行</p>
                        </div>
                    </a>
                    <a class="add-node-popover-item auto-node" @click="addType(9)">
                        <div class="item-wrapper">
                            <svg-icon icon-class="auto-approve" class="iconfont" />
                            <p>自动节点</p>
                        </div>
                    </a>
                </div>
                <div class="add-node-popover-body align-left">
                    <a class="add-node-popover-item process-node" @click="addType(10)">
                        <div class="item-wrapper">
                            <svg-icon icon-class="handle-task" class="iconfont" />
                            <p>办理节点</p>
                        </div>
                    </a>
                    <a class="add-node-popover-item auto-process-node" @click="addType(11)">
                        <div class="item-wrapper">
                            <svg-icon icon-class="auto-task" class="iconfont" />
                            <p>自动办理</p>
                        </div>
                    </a>
                    <a class="add-node-popover-item cloner-node" @click="openCloneDialog()">
                        <div class="item-wrapper">
                            <svg-icon icon-class="approver-clone" class="iconfont" />
                            <p>克隆器</p>
                        </div>
                    </a>
                    <a class="add-node-popover-item condition-approver-node" @click="addType(12)">
                        <div class="item-wrapper">
                            <svg-icon icon-class="condition-approve" class="iconfont" />
                            <p>条件审批</p>
                        </div>
                    </a>
                </div>
                <div class="add-node-popover-body align-left">
                    <a class="add-node-popover-item condition-copy-node" @click="addType(13)">
                        <div class="item-wrapper">
                            <svg-icon icon-class="condition-copy" class="iconfont" />
                            <p>条件抄送</p>
                        </div>
                    </a>
                    <a class="add-node-popover-item pick-condition-node" @click="addType(14)">
                        <div class="item-wrapper">
                            <svg-icon icon-class="pick-condition" class="iconfont" />
                            <p>选择条件</p>
                        </div>
                    </a>
                </div>
                <template #reference>
                    <button class="btn" type="button">
                        <svg-icon icon-class="addbtn" class="iconfont" />
                    </button>
                </template>
            </el-popover>
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
                        <el-option
                            v-for="item in cloneableNodes"
                            :key="item.nodeId"
                            :label="item.nodeName + (item.nodeType === 4 ? '（审批人）' : '（抄送人V2）')"
                            :value="item.nodeId"
                        />
                    </el-select>
                </el-form-item>
            </el-form>
        </div>
        <template #footer>
            <el-button @click="cloneDialogVisible = false">取 消</el-button>
            <el-button type="primary" :disabled="!selectedCloneNodeId" @click="confirmClone">确 定</el-button>
        </template>
    </el-dialog>
</template>
<script setup>
import { ref, inject } from 'vue'
import { NodeUtils } from '@/utils/antflow/nodeUtils'
let props = defineProps({
    childNodeP: {
        type: Object,
        default: () => (null)
    }
})
let emits = defineEmits(['update:childNodeP'])
let visible = ref(false)
const rootNode = inject('rootNode', null)
// 克隆器状态
let cloneDialogVisible = ref(false)
let selectedCloneNodeId = ref(null)
let cloneableNodes = ref([])
/**创建审批人节点 */
const createApproveNode = (childNode) => {
    return NodeUtils.createApproveNode(childNode);
}
/**创建抄送人节点 */
const createCopyNode = (childNode) => {
    return NodeUtils.createCopyNode(childNode);
}

const createCopyNodeV2 = (childNode) => {
    return NodeUtils.createCopyNodeV2(childNode);
}
/**创建自动节点 */
const createAutoNode = (childNode) => {
    return NodeUtils.createAutoNode(childNode);
}
/**创建条件审批节点 */
const createConditionApproveNode = (childNode) => {
    return NodeUtils.createConditionApproveNode(childNode);
}
/**创建条件抄送节点 */
const createConditionCopyNode = (childNode) => {
    return NodeUtils.createConditionCopyNode(childNode);
}
/**创建选择条件组合节点（审批人 + 动态条件网关） */
const createPickConditionNode = (childNode) => {
    return NodeUtils.createPickConditionNode(childNode);
}
/**创建办理节点（一次性生成两个审批人节点） */
const createProcessNode = (childNode) => {
    return NodeUtils.createProcessNode(childNode);
}
/**创建自动办理节点（自动节点 + 发起人确认审批人节点） */
const createAutoProcessNode = (childNode) => {
    return NodeUtils.createAutoProcessNode(childNode);
}
/**创建并行审批人节点 */
const createParallelWayNode = (childNode) => {
    return NodeUtils.createParallelWayNode(childNode);
}
/**创建条件网关节点 */
const createGatewayNode = (childNode) => {
    return NodeUtils.createGatewayNode(childNode);
}
/**创建动态网关节点 */
const createDynamicConditionWayNode = (childNode) => {
    return NodeUtils.createDynamicConditionWayNode(childNode);
}
/**创建并行网关节点 */
const createParallelConditionWayNode = (childNode) => {
    return NodeUtils.createParallelConditionWayNode(childNode);
}
// 创建节点 Map集合
const createNodeMap = new Map([
    [1, createApproveNode],
    [2, createCopyNode],
    [3, createParallelWayNode],
    [4, createGatewayNode],
    [5, createDynamicConditionWayNode],
    [6, createParallelConditionWayNode],
    [8, createCopyNodeV2],
    [9, createAutoNode],
    [10, createProcessNode],
    [11, createAutoProcessNode],
    [12, createConditionApproveNode],
    [13, createConditionCopyNode],
    [14, createPickConditionNode],
]);
const addType = (type) => {
    visible.value = false;
    const handleCreateNodeFunc = createNodeMap.get(type);
    const newNodeInfo = handleCreateNodeFunc(props.childNodeP);
    emits("update:childNodeP", newNodeInfo)
}
/**打开克隆器弹窗：收集整棵树中 nodeType=4 和 nodeType=8 的节点 */
const openCloneDialog = () => {
    visible.value = false;
    selectedCloneNodeId.value = null;
    if (rootNode && rootNode.value) {
        cloneableNodes.value = NodeUtils.collectNodesByType(rootNode.value, [4, 8]);
    } else {
        cloneableNodes.value = [];
    }
    cloneDialogVisible.value = true;
}
/**确认克隆：深拷贝源节点并插入当前位置 */
const confirmClone = () => {
    const sourceNode = cloneableNodes.value.find(n => n.nodeId === selectedCloneNodeId.value);
    if (!sourceNode) return;
    const clonedNode = NodeUtils.cloneNode(sourceNode, props.childNodeP);
    emits("update:childNodeP", clonedNode);
    cloneDialogVisible.value = false;
}
</script>
<style scoped lang="scss">
@use "@/assets/styles/antflow/workflow.scss";

.add-node-btn-box {
    width: 240px;
    display: -webkit-inline-box;
    display: -ms-inline-flexbox;
    display: inline-flex;
    -ms-flex-negative: 0;
    flex-shrink: 0;
    -webkit-box-flex: 1;
    -ms-flex-positive: 1;
    position: relative;

    &:before {
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: -1;
        margin: auto;
        width: 2px;
        height: 100%;
        background-color: #cacaca
    }

    .add-node-btn {
        user-select: none;
        width: 240px;
        padding: 20px 0 32px;
        display: flex;
        -webkit-box-pack: center;
        justify-content: center;
        flex-shrink: 0;
        -webkit-box-flex: 1;
        flex-grow: 1;

        .btn {
            outline: none;
            box-shadow: 0 2px 4px 0 rgba(0, 0, 0, .1);
            width: 30px;
            height: 30px;
            background: #3296fa;
            border-radius: 50%;
            position: relative;
            border: none;
            line-height: 30px;
            -webkit-transition: all .3s cubic-bezier(.645, .045, .355, 1);
            transition: all .3s cubic-bezier(.645, .045, .355, 1);

            .iconfont {
                color: #fff;
                font-size: 16px
            }

            &:hover {
                transform: scale(1.3);
                box-shadow: 0 13px 27px 0 rgba(0, 0, 0, .1)
            }

            &:active {
                transform: none;
                background: #1e83e9;
                box-shadow: 0 2px 4px 0 rgba(0, 0, 0, .1)
            }
        }
    }
}

.add-node-popover-body {
    display: flex;

    &.align-left {
        .add-node-popover-item {
            flex: 0 0 auto;
        }
    }

    .add-node-popover-item {
        margin-right: 10px;
        cursor: pointer;
        text-align: center;
        flex: 1;
        color: #191f25 !important;

        .item-wrapper {
            user-select: none;
            display: inline-block;
            width: 60px;
            height: 66px;
            margin-bottom: 5px;
            background: #fff;
            border: 1px solid #e2e2e2;
            border-radius: 10%;
            transition: all .3s cubic-bezier(.645, .045, .355, 1);

            .iconfont {
                margin-top: 5px;
                font-size: 35px;
                line-height: 65px
            }
        }

        p {
            margin: 0;
            font-size: 12px;
            font-weight: 900;
            color: #000;
        }

        &.approver {
            .item-wrapper {
                color: #ff943e
            }
        }

        &.notifier {
            .item-wrapper {
                color: #3296fa
            }
        }

        &.notifier-v2 {
            .item-wrapper {
                color: #0460bb
            }
        }

        &.auto-node {
            .item-wrapper {
                color: #9b59b6
            }
        }

        &.condition-approver-node {
            .item-wrapper {
                color: #2ea7a7
            }
        }

        &.condition-copy-node {
            .item-wrapper {
                color: #4682b4
            }
        }

        &.process-node {
            .item-wrapper {
                color: #15bc83
            }
        }

        &.auto-process-node {
            .item-wrapper {
                color: #9b59b6
            }
        }

        &.cloner-node {
            .item-wrapper {
                color: #e67e22
            }
        }

        &.condition {
            .item-wrapper {
                color: #15bc83
            }
        }

        &:hover {
            .item-wrapper {
                background: #3296fa;
                box-shadow: 0 10px 20px 0 rgba(50, 150, 250, .4)
            }

            .iconfont {
                color: #fff
            }
        }

        &:active {
            .item-wrapper {
                box-shadow: none;
                background: #eaeaea
            }

            .iconfont {
                color: inherit
            }
        }
    }
}
</style>