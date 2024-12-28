<template>
    <el-drawer :append-to-body="true" title="审批人设置" v-model="visible" class="set_approver" :with-header="false"
        :size="680">
        <div class="el-drawer__header">
            <span class="drawer-title">审批人</span>
        </div>
        <el-tabs v-model="activeName" @tab-click="handleTabClick">
            <el-tab-pane label="审批人设置" name="approverStep">
                <div v-if="approverStepShow">
                    <div class="drawer_content">
                        <div class="approver_content">
                            <el-radio-group v-model="approverConfig.setType" class="clear" @change="changeType">
                                <el-radio v-for="({ value, label }) in setTypes" :value="value">{{ label }}</el-radio>
                            </el-radio-group>
                            <div class="approver_Btn" v-show="approverConfig.setType == 5">
                                <el-button type="primary" @click="addApprover">添加/修改人员</el-button>
                                <p class="selected_list">
                                    <span v-for="(item, index) in approverConfig.nodeApproveList"
                                        :key="index">🙍‍♂️ {{ item.name }}
                                        <img src="@/assets/images/add-close1.png"
                                            @click="$func.removeEle(approverConfig.nodeApproveList, item, 'targetId')">
                                    </span>
                                    <a v-if="approverConfig.nodeApproveList.length != 0"
                                        @click="approverConfig.nodeApproveList = []">清除</a>
                                </p>
                            </div>

                            <div class="approver_Btn" v-show="approverConfig.setType == 4">
                                <el-button type="primary" @click="addRoleApprover">添加/修改角色</el-button>
                                <p class="selected_list">
                                    <span v-for="(item, index) in approverConfig.nodeApproveList" :key="index">{{
                                        item.name
                                    }}
                                        <img src="@/assets/images/add-close1.png"
                                            @click="$func.removeEle(approverConfig.nodeApproveList, item, 'targetId')">
                                    </span>
                                    <a v-if="approverConfig?.nodeApproveList?.length != 0"
                                        @click="approverConfig.nodeApproveList = []">清除</a>
                                </p>
                            </div>

                            <div class="approver_Btn" v-show="approverConfig.setType == 14">
                                <el-button type="primary" @click="addRoleApprover">添加/修改部门</el-button>
                                <p class="selected_list">
                                    <span v-for="(item, index) in approverConfig.nodeApproveList" :key="index">{{
                                        item.name
                                    }}
                                        <img src="@/assets/images/add-close1.png"
                                            @click="$func.removeEle(approverConfig.nodeApproveList, item, 'targetId')">
                                    </span>
                                    <a v-if="approverConfig.nodeApproveList?.length != 0"
                                        @click="approverConfig.nodeApproveList = []">清除</a>
                                </p>
                            </div>

                            <div class="approver_select" v-if="approverConfig.setType == 3">
                                <p>
                                    <span>发起人的：</span>
                                    <select v-model="approverConfig.directorLevel" style="width: 300px;"> 
                                        <option disabled selected value>--请选择--</option>
                                        <option v-for="item in directorMaxLevel" :value="item" :key="item">
                                            {{ item == 1 ? '直接' : '第' + item + '级' }}主管</option>
                                    </select>
                                </p>
                                <p class="tip">找不到主管时，由上级主管代审批</p>
                            </div>

                            <div class="approver_select" v-if="approverConfig.setType == 6">
                                <p>
                                    <span>HRBP选择：</span>
                                    <select v-model="checkedHRBP" style="width: 300px;">
                                        <option disabled selected value>--请选择--</option>
                                        <option v-for="item in hrbpOptions" required :key="item.value"
                                            :value="item.value">
                                            {{ item.label }}</option>
                                    </select>
                                </p>
                            </div>
                            <div class="approver_text" v-if="approverConfig.setType == 12">
                                <p>该审批节点设置“发起人自己”后，审批人默认为发起人</p>
                            </div>
                            <div class="approver_text" v-if="approverConfig.setType == 13">
                                <p>该审批节点设置“直属领导”后，审批人默认为发起人的直属领导</p>
                            </div>
                        </div>
                        <div class="approver_block">
                            <p>✍多人审批时采用的审批方式</p>
                            <el-radio-group v-model="approverConfig.signType" class="clear">
                                <el-radio :value="1">会签（需所有审批人同意，不限顺序）</el-radio>
                                <br />
                                <el-radio :value="2">或签（只需一名审批人同意或拒绝即可）</el-radio>
                                <br />
                                <el-radio :value="3"
                                    v-if="approverConfig.setType == 5">顺序会签（需要所有审批人同意，根据前端传入的顺序）</el-radio>
                            </el-radio-group>
                        </div>
                        <div class="approver_block">
                            <p>✍审批人为空时</p>
                            <el-radio-group v-model="approverConfig.noHeaderAction" class="clear">
                                <el-radio :value="1">自动审批通过/不允许发起</el-radio>
                                <br />
                                <el-radio :value="2">转交给审核管理员</el-radio>
                            </el-radio-group>
                        </div>
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
            <el-tab-pane lazy label="表单权限设置" name="formStep">
                <div class="drawer_content">
                    <form-perm-conf v-if="formStepShow" default-perm="R" v-model:formItems="formItems"
                        @changePermVal="changePermVal" />
                </div>
            </el-tab-pane>
        </el-tabs>
        <div class="demo-drawer__footer clear">
            <el-button type="primary" @click="saveApprover">确 定</el-button>
            <el-button @click="closeDrawer">取 消</el-button>
        </div>
        <employees-dialog v-model:visible="approverVisible" :data="checkedList" @change="sureApprover" />
        <role-dialog v-model:visible="approverRoleVisible" :data="checkedRoleList" @change="sureRoleApprover" />
    </el-drawer>
</template>
<script setup>
import { ref, watch, computed } from 'vue'
import $func from '@/utils/flow/index'
import { setTypes, hrbpOptions, approvalPageButtons } from '@/utils/flow/const'
import { useStore } from '@/store/modules/workflow'
import employeesDialog from '../dialog/employeesDialog.vue'
import roleDialog from '../dialog/roleDialog.vue'
import FormPermConf from "../config/FormPermConf.vue";
let store = useStore()
let props = defineProps({
    directorMaxLevel: {
        type: Number,
        default: 0
    }
});

let approverConfig = ref({})
let approverVisible = ref(false)
let approverRoleVisible = ref(false)
let checkedRoleList = ref([])
let checkedList = ref([])
let checkApprovalPageBtns = ref([])
let checkedHRBP = ref('')
let approvalPageBtns = ref([])

let afterSignUpWayVisible = computed(() => approverConfig.value?.isSignUp == 1)
let checkAfterSignUpWay = ref(false)

let formItems = ref([])

let activeName = ref('approverStep')
let approverStepShow = ref(true)
let formStepShow = ref(false)

let approverConfig1 = computed(() => store.approverConfig1)
let approverDrawer = computed(() => store.approverDrawer)
let visible = computed({
    get() {
        handleTabClick({ paneName: "approverStep" })
        return approverDrawer.value
    },
    set() {
        closeDrawer()
    }
})

watch(approverConfig1, (val) => {
    approverConfig.value = val.value;
    formItems.value = approverConfig.value.lfFieldControlVOs || []; 
    checkApprovalPageBtns.value = val.value.buttons?.approvalPage;
})
watch(approverConfig1, (val) => {
    approvalPageBtns.value = val.value.buttons?.approvalPage;
    if (val.value.nodeProperty == 6) {
        checkedHRBP.value =  val.value.property.hrbpConfType 
    }
})
watch(() => approverConfig.value?.property?.afterSignUpWay, (newVal, oldVal) => {
    checkAfterSignUpWay.value = newVal == 1 ? true : false
})
watch(checkedHRBP, (val) => { 
    let labelName = hrbpOptions.find(item => item.value == val)?.label;
    approverConfig.value.nodeApproveList = [{ "type": 6, "targetId": val, "name": labelName }]; 
})

const changeType = (val) => {
    //console.log('typevale=====>',val);
    approverConfig.value.nodeApproveList = [];
    approverConfig.value.signType = 1;
    approverConfig.value.noHeaderAction = 2;
    checkedHRBP.value = '';
    if (val == 3) {
        approverConfig.value.directorLevel = 1;
    } else {

    }
}
const addApprover = () => {
    approverVisible.value = true;
    checkedList.value = approverConfig.value.nodeApproveList
}
const addRoleApprover = () => {
    approverRoleVisible.value = true;
    checkedRoleList.value = approverConfig.value.nodeApproveList
}
const sureApprover = (data) => {
    approverConfig.value.nodeApproveList = data;
    approverVisible.value = false;
}
const sureRoleApprover = (data) => {
    approverConfig.value.nodeApproveList = data;
    approverRoleVisible.value = false;
}
const saveApprover = () => {
    approverConfig.value.error = !$func.setApproverStr(approverConfig.value)
    //console.log('approverConfig.===saveApprover=======',JSON.stringify(approverConfig.value)) 
    store.setApproverConfig({
        value: approverConfig.value,
        flag: true,
        id: approverConfig1.value.id
    })
    closeDrawer()
}
const closeDrawer = () => {
    store.setApprover(false)
}

const handleCheckedButtonsChange = (val) => {
    const index = approvalPageBtns.value.indexOf(val);
    index < 0 ? approvalPageBtns.value.push(val) : approvalPageBtns.value.splice(index, 1);
    const isAddStep = approvalPageBtns.value.indexOf(19);
    if (isAddStep >= 0) {
        approverConfig.value.isSignUp = 1;
    } else {
        approverConfig.value.isSignUp = 0;
    }
}
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
    //console.log('approverConfig.value=============', JSON.stringify(approverConfig.value))
}
const handleTabClick = (tab, event) => {
    activeName.value = tab.paneName;
    if (tab.paneName == 'formStep') {
        formStepShow.value = true;
    } else {
        formStepShow.value = false;
    }
}
const changePermVal = (data) => {
    approverConfig.value.lfFieldControlVOs = data;
}    
</script>
<style scoped lang="scss">
@import "@/assets/styles/flow/dialog.scss";

.el-drawer__header {
    margin-bottom: 5px !important;
}

.selected_list {
    margin-bottom: 20px;
    line-height: 40px;
}

.selected_list span {
    margin-right: 10px; 
    padding: 5px;
    line-height: 12px;
    white-space: nowrap;
    border-radius: 5px;
    border: 1px solid #9fceff;
    background-color: #9fceff;
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
        min-height: 250px;
        padding-bottom: 10px;
        border-bottom: 1px solid #f2f2f2;
    }

    .approver_Btn,
    .approver_content {
        .el-button {
            margin-bottom: 20px;
        }
    }

    .approver_content,
    .approver_block,
    .approver_Btn {
        padding-top: 10px;

        .el-radio-group {
            display: unset;
        }

        .el-radio {
            width: 27%;
            margin-bottom: 20px;
            height: 16px;
        }
    }

    .approver_select p {
        line-height: 32px;
    }

    .approver_select select {
        width: 420px;
        height: 32px;
        background: rgba(255, 255, 255, 1);
        border-radius: 5px;
        border: 1px solid rgba(217, 217, 217, 1);
    }

    .approver_select p.tip {
        margin: 10px 0 22px 0;
        font-size: 12px;
        line-height: 16px;
        color: #f8642d;
    }

    .approver_text {
        padding: 28px 0px;
    }

    .approver_select p:first-of-type,
    .approver_block p {
        line-height: 19px;
        font-size: 14px;
        margin-bottom: 14px;
    }

    .approver_Btn h3 {
        margin: 5px 0 20px;
        font-size: 14px;
        font-weight: bold;
        line-height: 19px;
    }
}
</style>