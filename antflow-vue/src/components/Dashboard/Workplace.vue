<template>
    <div>
        <el-scrollbar>
            <el-card>
                <template v-slot:header>
                    <div class="clearfix">
                        <span>可用流程(DIY)</span>
                    </div>
                </template>
                <el-row :gutter="5">
                    <el-col :lg="8" :md="12" :sm="12" :xs="24" v-for="(item, index) in worlflowList">
                        <el-card shadow="always" class="card-col" @click="handleStart(item)">
                            <div class="card-icon">
                                <el-avatar v-if="item.title" size="large" :style="{ backgroundColor: item.iconColor }">
                                    <span style="font-size: 18px; font-weight: 700;">{{ item.title.substring(0, 1)
                                    }}</span>
                                </el-avatar>
                            </div>
                            <div class="card-title">
                                <a>{{ item.title }}</a>
                                <p>{{ item.description }}</p>
                            </div>
                            <!-- 新增底部遮罩层和图标 -->
                            <div class="card-bottom-mask">
                                <!-- <p v-for="n in 4" :key="n" style="margin-left: 5px;">
                                    <msgIcon :iconValue="n" viewValue="warning" :sizeValue="25" />
                                </p> -->
                            </div>
                        </el-card>
                    </el-col>
                </el-row>
            </el-card>
            <el-card>
                <template v-slot:header>
                    <div class="clearfix">
                        <span>低代码表单(LF)
                            <el-tooltip placement="right">
                                <template #content>
                                    <span>
                                        <el-alert style="margin-bottom: 5px;" title="第一步：新增：流程管理->流程类型->新增"
                                            type="success" effect="dark" :closable="false" />
                                        <el-alert style="margin-bottom: 5px;" title="第二步：设计：流程管理->流程类型->点击【流程设计】"
                                            type="success" effect="dark" :closable="false" />
                                        <el-alert style="margin-bottom: 5px;" title="第三步：启用：流程管理->流程设计->版本管理->点击【启用】"
                                            type="success" effect="dark" :closable="false" />
                                    </span>
                                </template>
                                <el-icon><question-filled /></el-icon>
                            </el-tooltip>
                        </span>
                    </div>
                </template>
                <el-row :gutter="5">
                    <el-col :lg="8" :md="12" :sm="12" :xs="24" v-for="(item, index) in lfFlowList">
                        <el-card shadow="always" class="card-col" @click="handleStart(item)">
                            <div class="card-icon">
                                <el-avatar v-if="item.title" size="large" :style="{ backgroundColor: item.iconColor }">
                                    <span style="font-size: 18px; font-weight: 700;">{{ item.title.substring(0, 1)
                                        }}</span>
                                </el-avatar>
                            </div>
                            <div class="card-title">
                                <a>{{ item.title }}</a>
                                <p>【{{ substringHidden(item.formCode) }}】{{ item.description }}</p>
                            </div>
                            <div class="card-bottom-mask"></div>
                        </el-card>
                    </el-col>
                    <el-col :md="24">
                        <pagination v-show="lfTotal > 0" :total="lfTotal" v-model:page="lfPageDto.page"
                            v-model:limit="lfPageDto.pageSize" @pagination="getLFFormCodePageList" />
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
                            <el-tooltip content="更多体验三方接入，点击跳转若依管理系统" placement="right">
                                <a href="http://antflow.top/ruoyi/#/hr/leavetime" target="_blank">
                                    <el-button type="success" plain icon="Guide">更多体验,点击跳转至若依管理系统</el-button>
                                </a>
                            </el-tooltip>

                        </span>
                    </div>
                </template>
                <el-row :gutter="5">
                    <el-col :lg="8" :md="12" :sm="12" :xs="24" v-for="(item, index) in outsideFlowList">
                        <el-card shadow="always" class="card-col" @click="handleOutSide(item)">
                            <div class="card-icon">
                                <el-avatar v-if="item.title" size="large" :style="{ backgroundColor: item.iconColor }">
                                    <span style="font-size: 18px; font-weight: 700;">{{ item.title.substring(0, 1)
                                    }}</span>
                                </el-avatar>
                            </div>
                            <div class="card-title">
                                <a>{{ item.title }}</a>
                                <p>【{{ substringHidden(item.formCode) }}】{{ item.description }}</p>
                            </div>
                            <div class="card-bottom-mask"></div>
                        </el-card>
                    </el-col>
                    <el-col :md="24">
                        <pagination v-show="outsideTotal > 0" :total="outsideTotal" v-model:page="outsidePage.page"
                            v-model:limit="outsidePage.pageSize" @pagination="getOutSideFormCodeList" />
                    </el-col>
                </el-row>
            </el-card>
        </el-scrollbar>
    </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { getDIYFromCodeData } from "@/api/workflow/index"
import { getLFActiveFormCodePageList } from "@/api/workflow/lowcodeApi"
import { getOutSideFormCodePageList } from "@/api/workflow/outsideApi"
const { proxy } = getCurrentInstance();
let worlflowList = ref([]);
let lfFlowList = ref([]);
let outsideFlowList = ref([]);
const lfTotal = ref(0);
const outsideTotal = ref(0);
const data = reactive({
    lfPageDto: {
        page: 1,
        pageSize: 12
    },
    lfVO: {
        processState: 1,
        description: undefined
    },
    outsidePage: {
        page: 1,
        pageSize: 6
    },
    outsideVO: {
        description: undefined
    }
});
const { lfPageDto, lfVO, outsidePage, outsideVO } = toRefs(data);

onMounted(async () => {
    proxy.$modal.loading();
    await getDIYFormCodeList();
    proxy.$modal.closeLoading();
    await getLFFormCodePageList();
    await getOutSideFormCodeList();
})
/**
 * 获取自定义表单FormCode List
 */
async function getDIYFormCodeList() {
    await getDIYFromCodeData().then((res) => {
        if (res.code == 200) {
            const totalData = res.data.map((c, index) => {
                return {
                    formCode: c.key,
                    title: c.value,
                    formType: c.type,
                    hasChooseApprove: c.hasStarUserChooseModule,
                    description: c.value + '流程办理',
                    iconColor: getRandomColor(index)
                }
            });
            worlflowList.value = totalData;
        }
    })
}
/**
 * 获取低代码表单FormCode Page List
 */
async function getLFFormCodePageList() {
    await getLFActiveFormCodePageList(lfPageDto.value, lfVO.value).then((res) => {
        if (res.code == 200) {
            const totalData = res.data.map((c, index) => {
                return {
                    formCode: c.key,
                    title: c.value,
                    formType: c.type,
                    hasChooseApprove: c.hasStarUserChooseModule,
                    description: c.value + '流程办理',
                    iconColor: getRandomColor(index)
                }
            });
            lfFlowList.value = totalData;
            lfTotal.value = res.pagination.totalCount;
        }
    });
}

/**
 * 获取三方接入FormCode Page List
 */
async function getOutSideFormCodeList() {
    await getOutSideFormCodePageList(outsidePage.value, outsideVO.value).then((res) => {
        if (res.code == 200) {
            const totalData = res.data.map((c, index) => {
                return {
                    formCode: c.formCode,
                    title: c.bpmnName,
                    formType: 'outside',
                    applicationId: c.applicationId,
                    hasChooseApprove: false,
                    description: c.bpmnName + c.remark + '流程办理',
                    iconColor: getRandomColor(index)
                }
            });
            outsideFlowList.value = totalData;
            outsideTotal.value = res.pagination.totalCount;
        }
    });
}

function handleStart(row) {
    const params = {
        formType: row.formType,
        formCode: row.formCode,
        hasChooseApprove: row.hasChooseApprove
    };
    const obj = { path: '/startFlow/index', query: params };
    proxy.$tab.openPage(obj);
}
function handleOutSide(row) {
    const params = {
        ft: row.formType,
        fc: row.formCode,
        appid: row.applicationId,
        ha: row.hasChooseApprove,
        fcname: encodeURIComponent(row.title)
    };
    const obj = { path: '/startOutside/index', query: params };
    proxy.$tab.openPage(obj);
}
/**
 * 根据索引获取随机颜色
 * @param index 
 */
function getRandomColor(index = 0) {
    const metaColor = ['#ff4d4f', '#bae637', '#73d13d', '#36cfc9', '#40a9ff', '#597ef7', '#9254de', '#f759ab', '#ff7a45', '#ffa940', '#ffc53d', '#ffec3d'];
    const idx = Number.isFinite(index) ? Math.floor(index) : 0;
    const safeIndex = ((idx % metaColor.length) + metaColor.length) % metaColor.length;
    return metaColor[safeIndex];
}

</script>

<style lang="scss" scoped>
.card-col {
    cursor: pointer;
    margin-bottom: 10px;
    overflow: hidden;
    position: relative;

    &:hover .card-bottom-mask {
        display: flex;
    }
}

.card-bottom-mask {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    // height: 25px;
    background: rgba(0, 0, 0, 0.4);
    display: none;
    align-items: center;
    justify-content: center;
    z-index: 3;
    pointer-events: none;
    transition: all 0.2s;
}

.el-card {
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
        width: 150px;
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