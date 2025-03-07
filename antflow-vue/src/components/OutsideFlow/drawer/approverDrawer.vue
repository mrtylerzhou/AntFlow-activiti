<template>
    <el-drawer :append-to-body="true" title="审批人设置" v-model="visible" class="set_approver" :with-header="false"
        :size="680">
        <div class="el-drawer__header">
            <span class="drawer-title">审批人</span>
        </div>
        <el-tabs v-model="activeName">
            <el-tab-pane label="审批人设置" name="approverStep">
                <div class="drawer_content">
                    <div class="approver_content">
                        <el-radio-group v-model="approverConfig.setType" class="clear" @change="changeType">
                            <el-radio v-for="({ value, label }) in setTypes" :value="value">{{ label }}</el-radio>
                        </el-radio-group>

                        <div class="approver_btn" v-show="approverConfig.setType == 5">
                            <el-button type="primary" @click="addApprover">添加/修改人员</el-button>
                            <p class="selected_list">
                                <span v-for="(item, index) in approverConfig.nodeApproveList" :key="index">{{ item.name
                                    }}
                                    <img src="@/assets/images/add-close1.png"
                                        @click="$func.removeEle(approverConfig.nodeApproveList, item, 'targetId')">
                                </span>
                                <a v-if="approverConfig.nodeApproveList.length != 0"
                                    @click="approverConfig.nodeApproveList = []">清除</a>
                            </p>
                        </div>

                        <div class="approver_btn" v-show="approverConfig.setType == 4">
                            <p style="color: red;">*预览地址暂未开放,请不要使用</p>
                            <!-- <el-button type="primary" @click="addApprover">添加/修改角色</el-button>
                            <p class="selected_list">
                                <span v-for="(item, index) in approverConfig.nodeApproveList" :key="index">{{
                                    item.name
                                    }}
                                    <img src="@/assets/images/add-close1.png"
                                        @click="$func.removeEle(approverConfig.nodeApproveList, item, 'targetId')">
                                </span>
                                <a v-if="approverConfig?.nodeApproveList?.length != 0"
                                    @click="approverConfig.nodeApproveList = []">清除</a>
                            </p> -->
                        </div>
                        <div class="approver_text" v-if="approverConfig.setType == 6">
                            <p style="color: red;">*预览地址暂未开放,请不要使用</p>
                        </div>

                        <div class="approver_select" v-if="approverConfig.setType == 3">
                            <p style="color: red;">*预览地址暂未开放,请不要使用</p>
                            <!-- <p>
                                <span>发起人的：</span>
                                <select v-model="approverConfig.directorLevel" style="width: 300px;">
                                    <option v-for="item in directorMaxLevel" :value="item" :key="item">
                                        {{ item == 1 ? '直接' : '第' + item + '级' }}主管</option>
                                </select>
                            </p>
                            <p class="tip">找不到主管时，由上级主管代审批</p> -->
                        </div>

                        <div class="approver_text" v-if="approverConfig.setType == 12">
                            <p style="color: red;">*预览地址暂未开放,请不要使用</p>
                            <!-- <p>该审批节点设置“发起人自己”后，审批人默认为发起人</p> -->
                        </div>
                        <div class="approver_text" v-if="approverConfig.setType == 13">
                            <p style="color: red;">*预览地址暂未开放,请不要使用</p>
                            <!-- <p>该审批节点设置“直属领导”后，审批人默认为发起人的直属领导</p> -->
                        </div>
                    </div>
                    <div class="approver_block">
                        <p>多人审批时采用的审批方式</p>
                        <el-radio-group v-model="approverConfig.signType" class="clear">
                            <el-radio :value="1">会签（需所有审批人同意，不限顺序）</el-radio>
                            <br />
                            <el-radio :value="2">或签（只需一名审批人同意或拒绝即可）</el-radio>
                            <br />
                            <el-radio :value="3" v-if="approverConfig.setType == 5">顺序会签（需要所有审批人同意，根据前端传入的顺序）</el-radio>
                        </el-radio-group>
                    </div>
                    <div class="approver_block">
                        <p>审批人为空时</p> 
                        <p style="color: red;">*该功能暂时未开放，请忽略</p>
                        <el-radio-group v-model="approverConfig.noHeaderAction" class="clear">
                            <el-radio :value="1">自动审批通过/不允许发起</el-radio>
                            <br />
                            <el-radio :value="2">转交给审核管理员</el-radio>
                        </el-radio-group>
                    </div>
                </div>
            </el-tab-pane>
            <el-tab-pane lazy label="按钮权限设置" name="buttonStep">
                <div class="approver_block drawer_content">
                    <p>【审批页面】按钮权限显示控制</p>
                    <el-checkbox-group class="clear" v-model="checkApprovalPageBtns">
                        <el-checkbox style="margin: 6px 0;width: 100%;height: 45px;" border
                            v-for="opt in approvalPageButtons" :value="opt.value" :disabled="opt.type === 'default'"
                            @change="handleCheckedButtonsChange(opt.value)">
                            【{{ opt.label }}】
                            <span class="opt-description">
                                {{ opt.description }}
                            </span>
                        </el-checkbox>
                    </el-checkbox-group>
                    <el-checkbox border style="margin-top: 6px;width: 100%;height: 45px;" v-if="afterSignUpWayVisible"
                        v-model="checkAfterSignUpWay" @change="handleAfterSignUpWay(checkAfterSignUpWay)">
                        是否回到加批人
                        <span class="opt-description">
                            选中后，加批人审批完之后，会回到本节点的审批人再次审批
                        </span>
                    </el-checkbox>
                </div>
            </el-tab-pane>
        </el-tabs>
        <div class="demo-drawer__footer clear">
            <el-button type="primary" @click="saveApprover">确 定</el-button>
            <el-button @click="closeDrawer">取 消</el-button>
        </div>
    </el-drawer>
    <selectUser v-model:visible="approverVisible" :data="userSelectedList" @change="sureApprover" />
</template>
<script setup>
import { ref, watch, computed } from 'vue'
import selectUser from '../dialog/selectUserDialog.vue'
import $func from '@/utils/flow/index'
import { setTypes, approvalPageButtons } from '@/utils/flow/const'
import { useStore } from '@/store/modules/outsideflow'
let store = useStore()
let props = defineProps({
    directorMaxLevel: {
        type: Number,
        default: 0
    }
});
let approverVisible = ref(false)

const activeName = ref('approverStep')
let approverConfig = ref({})
let checkApprovalPageBtns = ref([])
let approvalPageBtns = ref([])

const userSelectedList = ref([])

let afterSignUpWayVisible = computed(() => approverConfig.value?.isSignUp == 1)
let checkAfterSignUpWay = ref(false)

let approverConfig1 = computed(() => store.approverConfig1)
let approverDrawer = computed(() => store.approverDrawer)

let visible = computed({
    get() {
        return approverDrawer.value
    },
    set() {
        closeDrawer()
    }
})
/**监听对象 */
watch(approverConfig1, (val) => {
    approverConfig.value = val.value;
    checkApprovalPageBtns.value = val.value.buttons?.approvalPage;
})
/**监听属性 */
watch(() => approverConfig.value?.property?.afterSignUpWay, (newVal, oldVal) => {
    checkAfterSignUpWay.value = newVal == 1 ? true : false
})
/**选择审批人类型 */
const changeType = (val) => {
    approverConfig.value.nodeApproveList = [];
    approverConfig.value.signType = 1;
    approverConfig.value.noHeaderAction = 2;
    approverConfig.value.directorLevel = 1;
}
/**选择人员 */
const addApprover = () => {
    approverVisible.value = true;
    userSelectedList.value = approverConfig.value.nodeApproveList;
}
/**保存选中人员 */
const sureApprover = (data) => {
    approverConfig.value.nodeApproveList = data;
    approverVisible.value = false;
}
/**保存并关闭抽屉 */
const saveApprover = () => {
    approverConfig.value.error = !$func.setApproverStr(approverConfig.value)
    store.setApproverConfig({
        value: approverConfig.value,
        flag: true,
        id: approverConfig1.value.id
    })
    closeDrawer()
}
/**选择审批页面按钮显示 */
const handleCheckedButtonsChange = (val) => {
    const index = approvalPageBtns.value.indexOf(val);
    index < 0 ? approvalPageBtns.value.push(val) : approvalPageBtns.value.splice(index, 1);
    const indexSet = approverConfig.value.buttons.approvalPage.indexOf(val);
    indexSet < 0 ? approverConfig.value.buttons.approvalPage.push(val) : approverConfig.value.buttons.approvalPage.splice(indexSet, 1);
    const isAddStep = approvalPageBtns.value.indexOf(19);
    if (isAddStep >= 0) {
        approverConfig.value.isSignUp = 1;
    } else {
        approverConfig.value.isSignUp = 0;
    }
}
/**选择审批页面 退回后是否回到审批人 */
const handleAfterSignUpWay = (val) => {
    const isTure = approverConfig.value.hasOwnProperty("property");
    if (isTure) {
        if (approverConfig.value.property.hasOwnProperty("afterSignUpWay")) {
            approverConfig.value.property.afterSignUpWay = val ? 1 : 2;
        } else {
            Object.assign(approverConfig.value.property, { afterSignUpWay: val ? 1 : 2 });
        }
    } else {
        Object.assign(approverConfig.value, {
            property: { afterSignUpWay: val ? 1 : 2 }
        });
    }
}
/**关闭抽屉 */
const closeDrawer = () => {
    store.setApprover(false)
}

</script>
<style scoped lang="scss">
@import "@/assets/styles/flow/dialog.scss";

.selected_list {
    margin-bottom: 20px;
    line-height: 30px;
}

.selected_list span {
    margin-right: 10px;
    padding: 8px 15px 8px 20px;
    line-height: 40px;
    white-space: nowrap;
    border-radius: 20px;
    border: 1px solid rgba(220, 220, 220, 1);
}

.selected_list img {
    margin-left: 5px;
    width: 7px;
    height: 7px;
    cursor: pointer;
}

.selected_list a {
    font-size: 10 !important;
    color: #46a6fe;
    cursor: pointer;
}

.el-tabs {
    margin-left: 20px !important;
}

.set_approver {
    .approver_content {
        padding-bottom: 10px;
        border-bottom: 1px solid #f2f2f2;
        min-height: 250px;
    }

    .approver_content {
        .el-button {
            margin-bottom: 20px;
        }
    }

    .approver_content,
    .approver_block,
    .approver_btn {
        padding-top: 5px;

        .el-radio-group {
            display: unset;
        }

        .el-radio {
            width: 27%;
            margin-bottom: 20px;
            height: 16px;
        }
    }

    .approver_block p {
        line-height: 19px;
        font-size: 14px;
        margin-bottom: 14px;
    }
}

.opt-description {
    margin-top: 3px;
    font-size: 12px;
    color: var(--el-text-color-placeholder);
}

.approver_select p {
    line-height: 32px;
}

.approver_select select {
    width: 420px;
    height: 32px;
    background: rgba(255, 255, 255, 1);
    border-radius: 4px;
    border: 1px solid rgba(217, 217, 217, 1);
}

.approver_select p.tip {
    margin: 10px 0 22px 0;
    font-size: 12px;
    line-height: 16px;
    color: #f8642d;
}

.approver_select p:first-of-type,
.approver_block p {
    line-height: 19px;
    font-size: 14px;
    margin-bottom: 14px;
}
</style>