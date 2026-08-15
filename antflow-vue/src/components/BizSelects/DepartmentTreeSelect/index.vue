<template>
    <!-- 选择部门(树形结构, 支持勾选任意部门节点与关键字搜索) -->
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

        <!-- 搜索结果(搜索模式) -->
        <el-table v-if="searchMode" v-loading="loading" :data="searchList" height="320px"
            @selection-change="handleSearchSelection">
            <el-table-column type="selection" width="50px" />
            <el-table-column prop="name" label="部门名称" :show-overflow-tooltip="true" />
        </el-table>

        <!-- 树形结构(浏览模式, 全量加载后按path组装) -->
        <div v-else class="dept-tree-wrap" v-loading="loading">
            <el-tree ref="treeRef" :data="treeData" :props="treeProps" node-key="id" show-checkbox
                check-strictly default-expand-all @check="handleTreeCheck" />
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
import { ref, computed, watch } from "vue";
import { getAllDepartments, queryDepartmentsByName } from "@/api/workflow/processPermissionsApi";
const { proxy } = getCurrentInstance();

const props = defineProps({
    visible: { type: Boolean, default: false },
    checkedData: { type: Array, default: () => [] },
});

const emits = defineEmits(["update:visible", "update:checkedData", "change"]);

const treeRef = ref(null);
const loading = ref(false);
const searchMode = ref(false);
const searchList = ref([]);
const treeData = ref([]); // 全量部门组装后的树
const selectedDepts = ref([]);

const qform = ref({ name: undefined });

const treeProps = {
    label: "name",
    children: "children",
};

const visibleDialog = computed({
    get() {
        return props.visible;
    },
    set() {
        handleClose();
    },
});

/** 打开弹窗时加载全量部门并组装树 */
watch(() => props.visible, (v) => {
    if (v) loadTree();
});

/**
 * 按 path 组装部门树
 * 注意: demo 数据的 parent_id 字段不可靠(指向比自己大的id), 真实层级关系在 path 字段(/1 -> /1/2 -> /1/2/3 ...)
 */
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

function loadTree() {
    loading.value = true;
    getAllDepartments().then((res) => {
        treeData.value = buildDeptTree(res.data ?? []);
        loading.value = false;
    }).catch(() => {
        loading.value = false;
        proxy.$modal.msgError("加载部门失败");
    });
}

/** 勾选任意部门节点, 同步已选列表 */
const handleTreeCheck = () => {
    syncFromTree();
};

const syncFromTree = () => {
    const checked = treeRef.value?.getCheckedNodes() ?? [];
    selectedDepts.value = checked.map((n) => ({ id: n.id, name: n.name }));
};

/** 搜索模式: 按名称模糊查询 */
const handleQuery = () => {
    if (!qform.value.name) {
        searchMode.value = false;
        return;
    }
    loading.value = true;
    queryDepartmentsByName(qform.value.name).then((res) => {
        searchMode.value = true;
        searchList.value = (res.data ?? []).map((d) => ({ id: String(d.id), name: d.name }));
        loading.value = false;
    }).catch(() => {
        loading.value = false;
        proxy.$modal.msgError("查询部门失败");
    });
};

const resetQuery = () => {
    qform.value.name = undefined;
    searchMode.value = false;
};

const handleSearchSelection = (selection) => {
    selectedDepts.value = selection;
};

const removeSelected = (idx) => {
    selectedDepts.value.splice(idx, 1);
    //同步树勾选状态(仅浏览模式)
    if (!searchMode.value && treeRef.value) {
        selectedDepts.value.forEach((d) => treeRef.value?.setChecked(d.id, true));
    }
};

const saveDialog = () => {
    emits("change", [...selectedDepts.value]);
    handleClose();
};

const handleClose = () => {
    searchMode.value = false;
    qform.value.name = undefined;
    searchList.value = [];
    selectedDepts.value = [];
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
