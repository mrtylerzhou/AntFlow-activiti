<template>
    <!-- 选择部门(树形懒加载, 默认展示两级, 支持关键字搜索树内高亮) -->
    <el-dialog title="选择部门" v-model="visibleDialog" width="640px" append-to-body :before-close="handleClose">
        <el-form :model="qform" ref="queryRef" :inline="true">
            <el-form-item label="部门名称">
                <el-input v-model="qform.name" placeholder="请输入部门名称关键字" clearable style="width: 200px" size="default"
                    @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" size="default" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" size="default" @click="resetQuery">重置</el-button>
            </el-form-item>
        </el-form>

        <!-- 浏览模式: 树形懒加载(展开节点时按需加载子级, 默认展开根展示两级) -->
        <div v-if="!searchMode" class="dept-tree-wrap" v-loading="loading">
            <el-tree ref="treeRef" lazy :load="loadNode" :props="treeProps" node-key="id" show-checkbox
                check-strictly :default-expanded-keys="defaultExpandedKeys" @check="handleTreeCheck" />
        </div>

        <!-- 搜索模式: 结果仍以树展示, 命中节点高亮(后端已补祖先链) -->
        <div v-else class="dept-tree-wrap" v-loading="loading">
            <el-tree ref="searchTreeRef" :data="searchTreeData" :props="treeProps" node-key="id" show-checkbox
                check-strictly default-expand-all @check="handleSearchTreeCheck">
                <template #default="{ data }">
                    <span :class="{ 'search-hit': data.name && qform.name && data.name.includes(qform.name) }">
                        {{ data.name }}
                    </span>
                </template>
            </el-tree>
        </div>

        <!-- 已选部门 -->
        <div class="selected-box" v-if="selectedDepts.length > 0">
            <span class="selected-label">已选：</span>
            <el-tag v-for="(item, idx) in selectedDepts" :key="item.id" closable @close="removeSelected(idx)"
                style="margin-right: 6px; margin-bottom: 4px">
                {{ item.name }}
            </el-tag>
        </div>

        <template #footer>
            <div class="dialog-footer">
                <el-button type="success" :disabled="selectedDepts.length === 0" @click="saveDialog">确定</el-button>
                <el-button type="warning" @click="handleClose">取消</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref, computed } from "vue";
import { getDepartmentsByParentId, queryDepartmentsByName } from "@/api/workflow/processPermissionsApi";
const { proxy } = getCurrentInstance();

const props = defineProps({
    visible: { type: Boolean, default: false },
    checkedData: { type: Array, default: () => [] },
});

const emits = defineEmits(["update:visible", "update:checkedData", "change"]);

const treeRef = ref(null);
const searchTreeRef = ref(null);
const loading = ref(false);
const searchMode = ref(false);
const searchTreeData = ref([]); // 搜索结果树(匹配节点+祖先链)
const defaultExpandedKeys = ref([]); // 懒加载默认展开根节点(展示两级)
const selectedDepts = ref([]);

const qform = ref({ name: undefined });

const treeProps = {
    label: "name",
    children: "children",
    isLeaf: "isLeaf",
};

const visibleDialog = computed({
    get() {
        return props.visible;
    },
    set() {
        handleClose();
    },
});

/** 树懒加载: 根节点(parentId为空, path深度=1) + 展开时按需加载子级 */
const loadNode = (node, resolve) => {
    const parentId = node && node.level > 0 ? node.data.id : undefined;
    loading.value = true;
    getDepartmentsByParentId(parentId).then((res) => {
        loading.value = false;
        const deps = (res.data ?? []).map((d) => ({
            id: String(d.id),
            name: d.name,
            path: d.path,
            isLeaf: d.isLeaf === true || d.isLeaf === 1,
        }));
        resolve(deps);
        // 根节点加载完成后默认展开, 展示两级
        if (node.level === 0) {
            defaultExpandedKeys.value = deps.map((d) => d.id);
        }
    }).catch(() => {
        loading.value = false;
        resolve([]);
    });
};

/** 按 path 组装部门树(搜索结果用, demo 数据 parent_id 不可靠, 真实层级在 path) */
function buildDeptTree(list) {
    const pathMap = {};
    const nodes = {};
    list.forEach((d) => {
        const node = { id: String(d.id), name: d.name, children: [] };
        nodes[d.id] = node;
        if (d.path) pathMap[d.path] = node;
    });
    const roots = [];
    list.forEach((d) => {
        const node = nodes[d.id];
        const seg = (d.path || "").split("/").filter(Boolean);
        if (seg.length <= 1) {
            roots.push(node);
            return;
        }
        seg.pop();
        const parent = pathMap["/" + seg.join("/")];
        if (parent) {
            parent.children.push(node);
        } else {
            roots.push(node); // path 找不到父级时按平铺展示
        }
    });
    return roots;
}

/** 搜索: 后端返回匹配部门+祖先链, 前端组树展示(保持树形态, 命中节点高亮) */
const handleQuery = () => {
    if (!qform.value.name) {
        searchMode.value = false;
        return;
    }
    loading.value = true;
    queryDepartmentsByName(qform.value.name).then((res) => {
        searchMode.value = true;
        searchTreeData.value = buildDeptTree(res.data ?? []);
        loading.value = false;
    }).catch(() => {
        loading.value = false;
        proxy.$modal.msgError("查询部门失败");
    });
};

const resetQuery = () => {
    qform.value.name = undefined;
    searchMode.value = false;
    searchTreeData.value = [];
};

/** 浏览树勾选(任意层级可选) */
const handleTreeCheck = () => {
    syncFromTree(treeRef.value);
};

/** 搜索树勾选 */
const handleSearchTreeCheck = () => {
    syncFromTree(searchTreeRef.value);
};

const syncFromTree = (tree) => {
    const checked = tree?.getCheckedNodes() ?? [];
    selectedDepts.value = checked.map((n) => ({ id: n.id, name: n.name }));
};

const removeSelected = (idx) => {
    selectedDepts.value.splice(idx, 1);
    //同步当前模式树的勾选状态
    const tree = searchMode.value ? searchTreeRef.value : treeRef.value;
    if (tree) {
        selectedDepts.value.forEach((d) => tree.setChecked(d.id, true));
    }
};

const saveDialog = () => {
    emits("change", [...selectedDepts.value]);
    handleClose();
};

const handleClose = () => {
    searchMode.value = false;
    qform.value.name = undefined;
    searchTreeData.value = [];
    selectedDepts.value = [];
    //清空两棵树的历史勾选, 避免重开弹窗状态残留
    treeRef.value?.setCheckedKeys([]);
    searchTreeRef.value?.setCheckedKeys([]);
    emits("update:visible", false);
};
</script>

<style scoped lang="scss">
.dept-tree-wrap {
    max-height: 320px;
    overflow: auto;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    padding: 6px;
}

.search-hit {
    color: #e6a23c;
    font-weight: 600;
}

.selected-box {
    margin-top: 10px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;

    .selected-label {
        font-size: 13px;
        color: #606266;
        margin-right: 4px;
    }
}
</style>
