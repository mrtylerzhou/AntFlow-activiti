<template>
    <el-container class="layout-setup" style="height: calc(100vh - 84px);">
        <el-aside class="layout-aside">
            <el-container>
                <el-header>
                    <div class="toolbar">
                        <el-input placeholder="请输入关键字" clearable style="width: 200px">
                            <template #append>
                                <el-button icon="Search" />
                            </template>
                        </el-input>
                    </div>
                </el-header>

                <el-main>
                    <el-scrollbar>
                        <div v-loading="loading" class="list-flex-cards" @click="toggleActive()">
                            <el-card class="item-card" v-for="item in dataList" :key="item.id">
                                <div class="card-content pointer">
                                    <div>
                                        <p class="card-title">
                                            {{ item.processTypeName }}
                                        </p>
                                        <p class="card-detail">
                                            <span>描述：</span>
                                            <span class="card-reason">{{ item.description }}</span>
                                        </p>
                                        <div class="card-time">
                                            <span>审批状态：</span>
                                            <span class="card-time-value">{{ item.taskState }}</span>
                                        </div>
                                        <div class="card-time">
                                            <span>发起时间：</span>
                                            <span class="card-time-value">
                                                {{ parseTime(item.createTime, '{y}-{m}-{d} {h}: {i}') }}</span>
                                        </div>
                                    </div>
                                    <p class="card-user">
                                        <span>
                                            <el-avatar :size="20"
                                                src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png">
                                            </el-avatar>
                                        </span>
                                        <span class="card-username">{{ item.actualName }}</span>
                                        <span>
                                            <el-tag type="success">{{ item.isLowCodeFlow ? 'LF' : 'DIY' }}</el-tag>
                                        </span>
                                    </p>
                                </div>
                            </el-card>
                        </div>
                    </el-scrollbar>
                </el-main>

            </el-container>
        </el-aside>
        <el-container class="layout-middle">
            <el-header>
                <div class="toolbar">
                    <el-button type="primary">同意</el-button>
                    <el-button type="success">拒绝</el-button>
                    <el-button type="danger">退回修改</el-button>
                </div>
            </el-header>
            <el-main>
                <el-scrollbar>
                    <div class="form-content">
                        CSS 如何在点击一个元素后保持 :active 的样式
                        在本文中，我们将介绍如何在点击一个元素后保持 :active 的样式。在CSS中，:active 伪类表示元素在被激活（点击或按下）时的样式。默认情况下，当鼠标点击一个元素后，:active
                        样式会立即消失。然而，有时我们希望保持这个样式，以提供更好的用户体验。
                        一种常见的方法是使用JavaScript来实现保持 :active 样式的效果。我们可以通过在点击事件中添加或删除一个类来控制元素的样式。下面是一个简单的示例：
                        HTML
                        在上面的示例中，我们通过使用classList.toggle() 方法，在按钮被点击时添加或移除了一个名为 “active” 的类。这个类定义了 :active
                        样式的样式规则，即背景颜色为红色，文本颜色为白色。这样一来，当按钮被点击后，:active 样式就会一直保持。
                    </div>
                </el-scrollbar>
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { getPenddinglistPage } from "@/api/workflow/index";
const dataList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);

const data = reactive({
    form: {},
    pageDto: {
        page: 1,
        pageSize: 10
    },
    taskMgmtVO: {
        processNumber: undefined,
        processTypeName: undefined
    },
    rules: {
        bpmnCode: [{ required: true, message: "关键字不能为空", trigger: "blur" }],
        bpmnName: [{ required: true, message: "关键字不能为空", trigger: "blur" }],
    }
});
const { pageDto, taskMgmtVO } = toRefs(data);

onMounted(async () => {
    await getList();
});

/** 查询岗位列表 */
async function getList() {
    loading.value = true;
    await getPenddinglistPage(pageDto.value, taskMgmtVO.value).then(response => {
        dataList.value = response.data;
        total.value = response.pagination.totalCount;
        loading.value = false;
    }).catch((r) => {
        loading.value = false;
        console.log(r);
        proxy.$modal.msgError("加载列表失败:" + r.message);
    });
}

const toggleActive = () => {
    console.log('Card clicked');
}
</script>

<style lang="scss" scoped>
* {
    margin: 0;
    border: 0;
    outline: 0;
    font-size: 100%;
    vertical-align: baseline;
    font-size: 12px;
}

.layout-setup .el-aside {
    width: 260px;
    color: var(--el-text-color-primary);
    background: #cccccc59;
}

.layout-aside .el-header {
    position: relative;
    background-color: #f2f3f4f5;
    color: var(--el-text-color-primary);
    border-radius: 5px;
}

.layout-aside .el-footer {
    position: relative;
    background-color: #f2f3f4f5;
    color: var(--el-text-color-primary);
    border-radius: 5px;
    margin-top: 10px;
}

.layout-aside .toolbar {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    right: 20px;
}

.layout-middle {
    margin-right: 10px;
    margin-top: 10px;
}

.layout-middle .el-header {
    box-shadow: var(--el-box-shadow-light);
    background-color: #fff;
    border-bottom: 1px solid #000;
}

.layout-middle .toolbar {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    right: 20px;
}

.layout-middle .form-content {
    box-shadow: var(--el-box-shadow-light);
    border-bottom: 10px;
    margin-top: 2px;
    background-color: #fff;
    height: calc(-170px + 100vh);
    font-size: 14px;
}

.list-flex-cards {
    display: flex;
    flex-wrap: wrap;
    gap: 5px;
    padding-top: 5px;
}

.item-card {
    width: 260px;
    box-shadow: var(--el-box-shadow-light);
}

.card-content {
    display: flex;
    flex-direction: column;
    border-radius: 20px;
    min-height: 140px;
    box-sizing: border-box;
    transition: box-shadow 0.2s;
}

.item-card:hover {
    border: 1px solid var(--current-color);
}

.active {
    border: 1px solid var(--current-color);
}

.card-title {
    font-size: 14px;
    font-weight: 600;
    color: #222;
    margin-bottom: 2px;
    letter-spacing: 1px;
    border-left: 3px solid var(--current-color);
    padding-left: 8px;
}

.card-detail {
    display: flex;
    align-items: center;
    color: #666;
    font-size: 12px;
}

.card-detail .card-reason {
    color: #fa541c;
    font-weight: 500;
    max-width: 200px;
    /* 根据需要调整宽度 */
    white-space: nowrap;
    /* 不换行 */
    overflow: hidden;
    /* 超出隐藏 */
    text-overflow: ellipsis;
    /* 超出显示省略号 */
    margin-left: 4px;
    display: block;
    /* 或 inline-block，根据需要 */
}

.card-time {
    font-size: 13px;
    color: #888;
    display: flex;
    align-items: center;
    gap: 4px;
    margin-bottom: 0;
}

.card-time-value {
    color: #409eff;
    font-weight: 500;
    margin-left: 2px;
}

.card-user {
    display: flex;
    align-items: center;
}

.card-username {
    font-size: 12px;
    font-weight: 500;
    color: #888;
    margin-left: 2px;
}
</style>
