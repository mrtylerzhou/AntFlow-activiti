<template>
    <div class="app-container start-flow-list">
        <div class="query-box">
            <el-form :model="query" ref="queryRef" :inline="true">
                <el-form-item label="流程类型" prop="categoryId">
                    <el-select v-model="query.categoryId" placeholder="请选择流程类型" clearable filterable style="width: 180px">
                        <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="流程名称" prop="bpmnName">
                    <el-input v-model="query.bpmnName" placeholder="请输入流程名称" clearable style="width: 180px"
                        @keyup.enter="handleQuery" />
                </el-form-item>
                <el-form-item label="formCode" prop="formCode">
                    <el-input v-model="query.formCode" placeholder="请输入formCode" clearable style="width: 180px"
                        @keyup.enter="handleQuery" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                    <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                </el-form-item>
                <div class="filter-tip">过滤优先级:流程名称 &gt; formCode &gt; 流程类型(命中前者忽略后者)</div>
            </el-form>
        </div>

        <div v-loading="loading" class="columns">
            <div v-for="col in 3" :key="col" class="column">
                <template v-for="block in blocksByColumn(col - 1)" :key="block.categoryId + block.categoryName">
                    <div class="category-block" :class="{ 'block-overflow': isOverflow(block) }">
                        <div class="category-header">
                            <span class="category-name">{{ block.categoryName }}</span>
                            <span class="category-count">{{ block.flows.length }}</span>
                        </div>
                        <div class="category-body">
                            <div v-for="item in block.flows" :key="item.formCode" class="flow-card"
                                @click="handleStart(item)">
                                <el-avatar :size="34" :style="{ backgroundColor: getColor(item.formCode) }">
                                    <span style="font-size: 15px; font-weight: 700;">{{ (item.bpmnName || '?').substring(0, 1) }}</span>
                                </el-avatar>
                                <div class="flow-card-info">
                                    <span class="flow-card-title">{{ item.bpmnName }}</span>
                                    <span class="flow-card-desc">{{ item.formCode }}</span>
                                </div>
                            </div>
                            <el-empty v-if="block.flows.length === 0" description="暂无流程" :image-size="60" />
                        </div>
                    </div>
                </template>
            </div>
            <el-empty v-if="!loading && list.length === 0" description="暂无匹配的流程" />
        </div>
        <div class="page-bar" v-if="pageCount > 1">
            <el-button :disabled="page <= 1" icon="ArrowLeft" @click="changePage(page - 1)">上一页</el-button>
            <span class="page-info">第 {{ page }} / {{ pageCount }} 页</span>
            <el-button :disabled="page >= pageCount" icon="ArrowRight" @click="changePage(page + 1)">下一页</el-button>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, getCurrentInstance } from "vue";
import { getStartFlowListPage } from "@/api/workflow/startFlowListApi";
import { getProcessCategoryOptions } from "@/api/workflow/processCategoryApi";
const { proxy } = getCurrentInstance();

const list = ref([]);
const loading = ref(false);
const page = ref(1);
const pageCount = ref(1);
const categoryOptions = ref([]);

const query = ref({
    categoryId: undefined,
    bpmnName: undefined,
    formCode: undefined,
});

const metaColor = ['#ff4d4f', '#bae637', '#73d13d', '#36cfc9', '#40a9ff', '#597ef7', '#9254de', '#f759ab', '#ff7a45', '#ffa940', '#ffc53d', '#ffec3d'];
function getColor(key = '') {
    let hash = 0;
    for (let i = 0; i < key.length; i++) {
        hash = (hash * 31 + key.charCodeAt(i)) >>> 0;
    }
    return metaColor[hash % metaColor.length];
}

const blocksByColumn = (col) => list.value.filter(b => b.column === col);

/** 分类块是否超栏(卡片 >= 8 时块高固定并内滚) */
function isOverflow(block) {
    return (block.flows.length + 1) > 8;
}

onMounted(() => {
    getList();
    //流程类型下拉:分类选项 + 未分类
    getProcessCategoryOptions().then(res => {
        const options = (res.data ?? []).map(c => ({ value: c.id, label: c.processTypeName }));
        options.push({ value: -1, label: "未分类" });
        categoryOptions.value = options;
    });
});

async function getList() {
    loading.value = true;
    await getStartFlowListPage(page.value, query.value).then(response => {
        list.value = response.data ?? [];
        pageCount.value = response.pagination?.pageCount ?? 1;
        page.value = response.pagination?.page ?? 1;
        loading.value = false;
    }).catch(() => {
        loading.value = false;
    });
}

/** 搜索按钮操作(条件变化回第 1 页) */
function handleQuery() {
    page.value = 1;
    getList();
}

/** 重置按钮操作 */
function resetQuery() {
    query.value = { categoryId: undefined, bpmnName: undefined, formCode: undefined };
    handleQuery();
}

function changePage(p) {
    if (p < 1 || p > pageCount.value) return;
    page.value = p;
    getList();
}

/** 点击卡片发起 */
function handleStart(row) {
    if (row.type === 'OUTSIDE') {
        const params = {
            ft: row.type,
            fc: row.formCode,
            appid: row.applicationId,
            fcname: encodeURIComponent(row.bpmnName),
        };
        proxy.$tab.openPage({ path: '/startOutside/index', query: params });
    } else {
        const params = {
            formType: row.type,
            formCode: row.formCode,
        };
        proxy.$tab.openPage({ path: '/startFlow/index', query: params });
    }
}
</script>

<style scoped lang="scss">
.start-flow-list {
    .filter-tip {
        font-size: 12px;
        color: #909399;
        line-height: 32px;
        margin-left: 4px;
    }

    .columns {
        display: flex;
        gap: 16px;
        align-items: flex-start;
    }

    .column {
        flex: 1;
        min-width: 0;
        display: flex;
        flex-direction: column;
        gap: 12px;
    }

    .category-block {
        background: #fff;
        border: 1px solid #e5e6eb;
        border-radius: 6px;
        overflow: hidden;

        .category-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 10px 14px;
            background: #f7f8fa;
            border-bottom: 1px solid #e5e6eb;

            .category-name {
                font-size: 14px;
                font-weight: 600;
                color: #1d2129;
            }

            .category-count {
                font-size: 12px;
                color: #86909c;
                background: #e5e6eb;
                border-radius: 8px;
                padding: 1px 8px;
            }
        }

        .category-body {
            padding: 6px 10px;
        }

        &.block-overflow {
            .category-body {
                max-height: 480px;
                overflow-y: auto;
            }
        }
    }

    .flow-card {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 8px;
        margin-bottom: 4px;
        border-radius: 6px;
        cursor: pointer;
        transition: background-color 0.2s;

        &:hover {
            background: #f2f3f5;
        }

        .flow-card-info {
            display: flex;
            flex-direction: column;
            min-width: 0;

            .flow-card-title {
                font-size: 14px;
                color: #1d2129;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
            }

            .flow-card-desc {
                font-size: 12px;
                color: #86909c;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
            }
        }
    }

    .page-bar {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 16px;
        margin-top: 20px;

        .page-info {
            font-size: 14px;
            color: #4e5969;
        }
    }
}
</style>
