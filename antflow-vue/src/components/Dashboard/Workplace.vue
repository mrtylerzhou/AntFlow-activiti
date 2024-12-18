<template>
    <div>
        <el-card>
            <template v-slot:header>
                <div class="clearfix">
                    <span>可用流程(DIY)</span>
                </div>
            </template>
            <el-row :gutter="10">
                <el-col :md="6" v-for="(item, index) in worlflowList">
                    <el-card shadow="always" class="card-col" @click="handleStart(item)">
                        <div slot="title">
                            <div class="card-icon">
                                <el-avatar size="large">
                                    <img :src="item.IconUrl" />
                                </el-avatar>
                            </div>
                            <div class="card-title">
                                <a>{{ item.title }}</a>
                                <p>{{ item.description }}</p>
                            </div>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </el-card>
        <el-card>
            <template v-slot:header>
                <div class="clearfix">
                    <span>低代码表单(LF)</span>
                </div>
            </template>
            <el-row :gutter="10">
                <el-col :md="6" v-for="(item, index) in lfFlowList">
                    <el-card shadow="always" class="card-col" @click="handleStart(item)">
                        <div slot="title">
                            <div class="card-icon">
                                <el-avatar size="large">
                                    <img :src="item.IconUrl" />
                                </el-avatar>
                            </div>
                            <div class="card-title">
                                <a>{{ item.title }}</a>
                                <p>{{ item.description }}</p>
                            </div>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </el-card>
        <el-card>
            <template v-slot:header>
                <div class="clearfix">
                    <span>第三方流程[1]（业务方流程）【*业务方（第三方）系统的表单，需要审批流程，接入本流程引擎*】</span>
                </div>
            </template>
            <el-row :gutter="10">
                <el-col :md="6" v-for="(item, index) in outsideflowList">
                    <el-card shadow="always" class="card-col" @click="handleOutSide(item)">
                        <div slot="title">
                            <div class="card-icon">
                                <el-avatar size="large">
                                    <img :src="item.IconUrl" />
                                </el-avatar>
                            </div>
                            <div class="card-title">
                                <a>{{ item.title }}</a>
                                <p>{{ item.description }}</p>
                            </div>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { getAllFormCodes } from "@/api/workflow"
const { proxy } = getCurrentInstance();
let worlflowList = ref([]);
let lfFlowList = ref([]);
function handleFlow(row) {
    proxy.$modal.msgSuccess("演示环境努力开发中！");
}
const outsideflowList = [
    {
        title: "第三方流程",
        description: "接入测试",
        IconUrl: getAssetsFile("jiejing")
    }
];
let statusColor = {
    "LEAVE_WMA": 'leave',
    "DSFZH_WMA": 'jiejing',
    "PURCHASE_WMA": 'bought',
    "UCARREFUEl_WMA": 'trip2',
    "LFTEST_WMA": 'zhushou',
};

onMounted(async () => {
    proxy.$modal.loading();
    await getAllFormCodes().then((res) => {
        if (res.code == 200) {
            const totalData = res.data.reverse().map(c => {
                return {
                    formCode: c.key,
                    title: c.value,
                    formType: c.type,
                    description: c.value + '流程办理',
                    IconUrl: getAssetsFile(statusColor[c.key] || 'icon-manage-19')
                }
            });
            lfFlowList.value = totalData.filter(c => c.formType == 'LF');
            worlflowList.value = totalData.filter(c => c.formType == 'DIY');
            proxy.$modal.closeLoading();
        }
    });
});

function handleStart(row) {
    const params = {
        formType: row.formType,
        formCode: row.formCode
    };
    if ('PURCHASE_WMA' == row.formCode) {
        proxy.$modal.msgWarning("采购表单努力开发中！^-^");
        return;
    }
    const obj = { path: '/bizentry/index', query: params };
    proxy.$tab.openPage(obj);
}
function handleOutSide(row) {
    proxy.$tab.openPage("/outsideMgt/bizForm", "采购");
    return;
}
function getAssetsFile(pathUrl) {
    return new URL(`../../assets/images/work/${pathUrl}.png`, import.meta.url).href;
}

</script>
<style lang="scss" scoped>
.card-col {
    cursor: pointer;
    margin-bottom: 10px;
    overflow: hidden;

    &:hover {
        background: rgba(0, 0, 0, 0.45);

        .card-title a,
        p {
            color: #ffff;
        }
    }

}

.card-title {
    float: left;
    margin-left: 15px;

    a {
        margin-bottom: 8px;
        font-size: 14px;
        color: #1890ff;
        width: 95px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    p {
        width: 95px;
        font-size: 12px;
        font-weight: 300;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
}

.card-icon {
    float: left;
    transform: translate(5%, -10%);
}
</style>