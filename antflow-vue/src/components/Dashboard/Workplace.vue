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
                    <span>低代码表单(LF)
                        <el-tooltip placement="right" >
                            <template #content> 
                                <span> 
                                    <el-alert style="margin-bottom: 5px;" title="第一步：添加模板类型(LF)：流程管理->流程模板" type="success" effect="dark"  :closable="false" /> 
                                    <el-alert style="margin-bottom: 5px;" title="第二步：流程设计：流程管理->流程设计(LF)" type="success" effect="dark"  :closable="false" /> 
                                    <el-alert style="margin-bottom: 5px;" title="第三步：启用流程：流程管理->流程设计列表点击【启用】" type="success" effect="dark"  :closable="false" /> 
                                </span>
                            </template>
                            <el-icon><question-filled /></el-icon>
                        </el-tooltip>  
                    </span>
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
                                <a>【{{ substringHidden(item.formCode) }}】</a>
                                <a>{{ item.title }}</a>
                                <p>{{ item.description }}</p>
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :md="24">
                    <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page"
                        v-model:limit="pageDto.pageSize" @pagination="getLFFormCodePageList" />
                </el-col>
            </el-row>

        </el-card>
        <el-card>
            <template v-slot:header>
                <div class="clearfix">
                    <span>
                        第三方流程[1]
                        <el-tooltip content="【*第三方流程（又称：业务方流程），外部系统的业务表单，需要审批流程，接入本流程引擎*】" placement="right">
                            <el-icon><question-filled /></el-icon>
                        </el-tooltip> 
                    </span>
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
import { getDIYFromCodeData } from "@/api/workflow"
import { getLFActiveFormCodePageList } from "@/api/lowcodeApi"
const { proxy } = getCurrentInstance();
let worlflowList = ref([]);
let lfFlowList = ref([]);
const total = ref(0);
const data = reactive({ 
    pageDto: {
        page: 1,
        pageSize: 12
    },
    taskMgmtVO: {
        processState:1,
        description: undefined
    }  
});
const { pageDto, taskMgmtVO } = toRefs(data);
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
    "DSFZH_WMA": 'hire',
    "PURCHASE_WMA": 'bought',
    "UCARREFUEl_WMA": 'trip',
    "LFTEST_WMA": 'zhushou',
    "BXSP_WMA": 'seal',
};

onMounted(async () => {
    proxy.$modal.loading();
    await getDIYFormCodeList();
    await getLFFormCodePageList();
    proxy.$modal.closeLoading();
})
/**
 * 获取自定义表单FormCode List
 */
async  function getDIYFormCodeList(){
    await getDIYFromCodeData().then((res) => {
        if (res.code == 200) {
            const totalData = res.data.map(c => {
                return {
                    formCode: c.key,
                    title: c.value,
                    formType: c.type,
                    hasChooseApprove:c.hasStarUserChooseModule,
                    description: c.value + '流程办理',
                    IconUrl: getAssetsFile(statusColor[c.key] || 'FF8BA7')
                }
            }); 
            worlflowList.value = totalData;
        }
    })
}
/**
 * 获取低代码表单FormCode Page List
 */
async  function getLFFormCodePageList(){ 
    await getLFActiveFormCodePageList(pageDto.value,taskMgmtVO.value).then((res) => {
        if (res.code == 200) {
            const totalData = res.data.map(c => {
                return {
                    formCode: c.key,
                    title: c.value,
                    formType: c.type,
                    hasChooseApprove:c.hasStarUserChooseModule,
                    description: c.value + '流程办理',
                    IconUrl: getAssetsFile(statusColor[c.key] || 'FF8BA7')
                }
            });
            lfFlowList.value = totalData;  
            total.value = res.pagination.totalCount;
        }
    });
}
function handleStart(row) {
    const params = {
        formType: row.formType,
        formCode: row.formCode,
        hasChooseApprove: row.hasChooseApprove
    };
    const obj = { path: '/bizentry/index', query: params };
    proxy.$tab.openPage(obj);
}
function handleOutSide(row) {
    proxy.$tab.openPage("/outsideMgt/bizForm", "三方接入表单");
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
.el-card{
    margin-bottom: 5px !important;
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