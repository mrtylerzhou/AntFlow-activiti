<template>
    <div>
        <el-form ref="templateRef" label-width="130px" label-position="top" style="margin: 0 20px;">
            <el-row>
                <el-col :span="24">
                    <el-form-item label="通知类型" prop="notifyType">
                        <el-checkbox-group v-model="templateForm.notifyType">
                            <el-checkbox style="margin: 5px;" v-for="(item, index) in notifyTypeList" :value="item.id"
                                :key="item.id" border>
                                {{ item.name }}
                            </el-checkbox>
                        </el-checkbox-group>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="24">
                    <el-form-item label="事件类型" prop="event">
                        <el-select v-model="templateForm.event" placeholder="请选择事件类型" style="width: 350px">
                            <el-option v-for="item in eventOptions" :key="item.id" :label="item.name"
                                :value="item.id" />
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="24">
                    <el-form-item label="消息模板" prop="templateId">
                        <el-button type="primary" plain icon="Plus" @click="dialogMsgVisible = true">选择消息模板</el-button>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item>
                        <el-tag v-for="tag in selectValues" :key="tag.id" type="success" size="large">
                            [{{ tag.num }}] {{ tag.name }}
                        </el-tag>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="24">
                    <el-form-item label="通知人选择" prop="name">
                        <el-radio-group v-model="noticeUserType" class="clear" @change="changeUserType">
                            <el-radio v-for="({ value, label }) in noticeUserList" :value="value">{{ label }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="24">
                    <el-form-item prop="name">
                        <div v-show="noticeUserType == 5">
                            <el-button type="primary" plain icon="Plus"
                                @click="chooseUserVisible = true">添加/修改人员</el-button>
                        </div>
                        <div v-show="noticeUserType == 6">
                            <el-button type="primary" plain icon="Plus"
                                @click="chooseRoleVisible = true">添加/修改角色</el-button>
                        </div>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <div class="gap-2">
                        <el-tag v-for="userTag in checkedUserList" :key="userTag.targetId" type="success" size="large"
                            closable @close="handleRemoveUser(userTag)">
                            {{ userTag.name }}
                        </el-tag>

                        <el-tag v-for="roleTag in checkedRoleList" :key="roleTag.targetId" type="success" size="large"
                            closable @close="handleRemoveRole(roleTag)">
                            {{ roleTag.name }}
                        </el-tag>
                    </div>
                </el-col>
            </el-row>
        </el-form>
        <flow-msg-templete v-model:visible="dialogMsgVisible" v-model:checkedData="selectValues"
            @change="saveFlowMsgTempDialog" />
        <select-user-dialog v-model:visible="chooseUserVisible" :data="checkedUserList" @change="sureUserDialog" />
        <select-role-dialog v-model:visible="chooseRoleVisible" :data="checkedRoleList" @change="sureRoleDialog" />
    </div>
</template>
<script setup>
import { onMounted, ref } from "vue";
import { getAllNoticeTypes, getProcessEvents } from "@/api/workflow/flowMsgApi";
import FlowMsgTemplete from "./flowMsgTemplateDialog.vue";
import selectUserDialog from '../../dialog/selectUserDialog.vue';
import selectRoleDialog from '../../dialog/selectRoleDialog.vue';
const { proxy } = getCurrentInstance();
const notifyTypeList = ref([]);
const eventOptions = ref([]);
const noticeUserList = ref([{
    value: "1",
    label: "申请人"
}, {
    value: "2",
    label: "所有已审批人"
}, {
    value: "3",
    label: "当前节点审批人"
}, {
    value: "4",
    label: "被转发人"
}, {
    value: "5",
    label: "指定人员"
}, {
    value: "6",
    label: "指定角色"
}]);

const dialogMsgVisible = ref(false);
const chooseUserVisible = ref(false);
const chooseRoleVisible = ref(false);
const noticeUserType = ref("1");
const selectValues = ref([]);

const checkedUserList = ref([]);
const checkedRoleList = ref([]);

const templateForm = ref({
    notifyType: [],
    event: "",
    name: ""
});

let props = defineProps({
    visible: {
        type: Boolean,
        default: false,
    }
});

const emits = defineEmits(["update:visible"]);
onMounted(() => {
    getAllNoticeTypesList();
    getProcessEventsList();
})

/** 获取所有通知类型列表 */
const getAllNoticeTypesList = () => {
    getAllNoticeTypes().then(res => {
        if (res && res.code == 200) {
            notifyTypeList.value = res.data;
        } else {
            proxy.$modal.msgError("获取通知类型失败" + res.errMsg);
        }
    }).catch(err => {
        console.log(err);
    });
};
/** 获取事件列表 */
const getProcessEventsList = () => {
    getProcessEvents().then(res => {
        if (res && res.code == 200) {
            eventOptions.value = res.data;
        } else {
            proxy.$modal.msgError("获取事件列表失败" + res.errMsg);
        }
    }).catch(err => {
        console.log(err);
    });
};

/**选择审批人类型更改事件 */
const changeUserType = (val) => {
    console.log(val);
    checkedUserList.value = [];
    checkedRoleList.value = [];
}

const saveFlowMsgTempDialog = (data) => {
    selectValues.value = data;
}

const sureUserDialog = (data) => {
    checkedUserList.value = data;
}

const sureRoleDialog = (data) => {
    checkedRoleList.value = data;
}

const handleRemoveUser = (data) => {
    checkedUserList.value = checkedUserList.value.filter(item => item.targetId != data.targetId);
}
const handleRemoveRole = (data) => {

    checkedRoleList.value = checkedRoleList.value.filter(item => item.targetId != data.targetId);
}
</script>

<style lang="scss" scoped>
.gap-2 {
    display: flex;
    gap: 0.5rem;
}
</style>