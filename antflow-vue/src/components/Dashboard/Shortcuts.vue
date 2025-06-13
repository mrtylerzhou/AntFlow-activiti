<!--
 * @Author: lidonghui
 * @Date: 2024-08-08 15:09:41
 * @LastEditTime: 2024-08-08 15:09:41  
 * @FilePath: \components\dashboard\LiveChart.vue
-->
<template>
    <el-row :gutter="20">
        <el-col :lg="6" :md="8" :sm="12" :xs="24">
            <div class="ve-card ve_card1" @click="handleTodo()">
                <el-icon>
                    <bell />
                </el-icon>
                <div>
                    <p>我的待办</p>
                    <span>{{ todoFrom.todoCount }}</span>
                </div>
            </div>
        </el-col>
        <el-col :lg="6" :md="8" :sm="12" :xs="24">
            <div class="ve-card ve_card2" @click="handleTodayDone()">
                <el-icon>
                    <EditPen />
                </el-icon>
                <div>
                    <p>今日已办</p>
                    <span>{{ todoFrom.doneTodayCount }}</span>
                </div>
            </div>
        </el-col>
        <el-col :lg="6" :md="8" :sm="12" :xs="24">
            <div class="ve-card ve_card3" @click="handleTodayCreate()">
                <el-icon>
                    <TakeawayBox />
                </el-icon>
                <div>
                    <p>今日发起</p>
                    <span>{{ todoFrom.doneCreateCount }}</span>
                </div>
            </div>
        </el-col>
        <el-col :lg="6" :md="8" :sm="12" :xs="24">
            <div class="ve-card ve_card4" @click="handleTodayDraft()">
                <el-icon>
                    <document />
                </el-icon>
                <div>
                    <p>我的草稿</p>
                    <span>0</span>
                </div>
            </div>
        </el-col>
    </el-row>
</template>

<script setup name="Index">
import { getTodoList } from "@/api/workflow/index";
const { proxy } = getCurrentInstance();

let todoFrom = ref({
    todoCount: 0,
    doneTodayCount: 0,
    doneCreateCount: 0,
})
const getTodo = () => {
    getTodoList().then(res => {
        todoFrom.value = res.data;
    }).catch((err) => {
        if (err && err.msg)
            console.log("获取todolist失败=" + JSON.stringify(err.msg));
    })
}
getTodo();
const handleTodo = () => {
    const obj = { path: "/flowTask/pendding" };
    proxy.$tab.openPage(obj);
}
const handleTodayDone = () => {
    const obj = { path: "/flowTask/approved" };
    proxy.$tab.openPage(obj);
}
const handleTodayCreate = () => {
    const obj = { path: "/flowTask/mytask" };
    proxy.$tab.openPage(obj);
}
const handleTodayDraft = () => {
    const obj = { path: "/approveV2" };
    proxy.$tab.openPage(obj);
}
</script>
<style lang="scss" scoped>
.el-row {
    height: 50%;
    padding-top: 10px;
    padding-right: 10px;
    padding-left: 10px;
}

.el-col {
    padding-bottom: 10px;
}

.ve-card {
    cursor: pointer;
    border-radius: 10px;
    height: 100%;
    display: flex;
    align-items: center;
    transition: all 500ms;
    color: #fff;

    &:hover {
        box-shadow: 3px 3px 6px 1px rgba(0, 0, 0, 0.2);
        background: #fff;
        border: 2px solid #1890ff;
    }

    i {
        font-size: 90px;
        margin: 10px 20px;
    }

    div {
        flex: 1;
        padding-right: 12px;

        p {
            margin: 0;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        span {
            font-size: 55px;
            font-weight: bold;
        }
    }
}

.ve_card1 {
    background: #1890ff;

    &:hover {
        color: #1890ff;
    }
}

.ve_card2 {
    background: #13ce66;

    &:hover {
        color: #13ce66;
    }
}

.ve_card3 {
    background: #FE7300;

    &:hover {
        color: #FE7300;
    }
}

.ve_card4 {
    background: #ff4949;

    &:hover {
        color: #ff4949;
    }
}
</style>
