<!--
 * @Date:  2024-05-25 14:05:59
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2023-05-24 15:20:53
 * @FilePath: /ant-flow/src/components/drawer/copyerDrawer.vue
-->
<template>
    <el-drawer :append-to-body="true" title="抄送人设置" v-model="visible" class="set_copyer" :with-header="false"
        destory-on-close :size="680">
        <div class="el-drawer__header">
            <span class="drawer-title">抄送人V2</span>
        </div>
        <el-tabs v-model="activeName" @tab-click="handleTabClick">
            <el-tab-pane label="抄送人设置" name="copyStep">
                <div v-if="copyStepShow">
                    <div class="approver_content">
                        <div>
                            <el-radio-group v-model="copyerConfig.setType" class="clear" @change="changeType">
                                <el-radio v-for="({ value, label }) in setCopyerTypes" :value="value">{{ label
                                    }}</el-radio>
                            </el-radio-group>
                        </div>
                        <div v-show="copyerConfig.setType == 5">
                            <el-button type="primary" plain icon="Plus" @click="addCopyer">添加/修改人员</el-button>
                            <div class="gap-2">
                                <el-tag class="gap-tag" v-for="(item, index) in copyerConfig.nodeApproveList"
                                    :key="item.targetId" size="large" closable
                                    @close="$func.removeEle(copyerConfig.nodeApproveList, item, 'targetId')">
                                    {{ item.name }}
                                </el-tag>
                            </div>
                        </div>

                        <div v-show="copyerConfig.setType == 4">
                            <el-button type="primary" plain icon="Plus" @click="addRoleApprover">添加/修改角色</el-button>
                            <div class="gap-2">
                                <el-tag class="gap-tag" v-for="(item, index) in copyerConfig.nodeApproveList"
                                    :key="item.targetId" size="large" closable
                                    @close="$func.removeEle(copyerConfig.nodeApproveList, item, 'targetId')">
                                    {{ item.name }}
                                </el-tag>
                            </div>
                        </div>

                        <div v-show="copyerConfig.setType == 14">
                            <el-button type="primary" @click="addRoleApprover">添加/修改部门</el-button>
                            <div class="gap-2">
                                <el-tag class="gap-tag" v-for="(item, index) in copyerConfig.nodeApproveList"
                                    :key="item.targetId" size="large" closable
                                    @close="$func.removeEle(copyerConfig.nodeApproveList, item, 'targetId')">
                                    {{ item.name }}
                                </el-tag>
                            </div>
                        </div>

                        <div v-if="copyerConfig.setType == 3">
                            <div>
                                <span>发起人的：</span>
                                <el-select v-model="copyerConfig.directorLevel" placeholder="请选择" style="width: 300px">
                                    <el-option v-for="item in directorMaxLevel" :key="item" :value="item"
                                        :label="item == 1 ? '直接主管' : '第' + item + '级' + '主管'" />
                                </el-select>
                            </div>
                            <p class="tip">找不到主管时，由上级主管代收</p>
                        </div>

                        <div class="approver_text" v-if="copyerConfig.setType == 12">
                            <p class="tip">该节点设置“发起人自己”后，抄送人默认为发起人</p>
                        </div>
                        <div class="approver_text" v-if="copyerConfig.setType == 13">
                            <p class="tip">该节点设置“直属领导”后，抄送人默认为发起人的直属领导</p>
                        </div>
                        <div class="approver_text" v-if="copyerConfig.setType == 7">
                            <p class="tip">该节点设置“发起人自选抄送人”后，抄送人在发起业务表单时由发起人选择</p>
                        </div>
                    </div>
                    <div class="approver_block">
                        <p>✍抄送人为空时</p>
                        <el-radio-group v-model="copyerConfig.noHeaderAction" class="clear">
                            <el-radio :value="0">不允许发起</el-radio>
                            <br />
                            <!--注意,这里的跳过指的是不生成审批任务节点,即流程图里没有当前缺失审批人的节点-->
                            <el-radio :value="1">跳过</el-radio>
                            <br />
                            <!--转给管理员需实现BpmnProcessAdminProvider接口-->
                            <el-radio :value="2">转交给审核管理员</el-radio>
                        </el-radio-group>
                    </div>
                </div>
            </el-tab-pane>
            <el-tab-pane lazy label="表单权限设置" name="formStep">
                <div class="drawer_content">
                    <form-perm-conf v-if="formStepShow" default-perm="R" :show-e="false" v-model:formItems="formItems"
                        :formHidden="formHiddenMap"
                        @changePermVal="changePermVal" @changeFormHidden="changeFormHidden" />
                </div>
            </el-tab-pane>
            <el-tab-pane lazy label="通知设置" name="noticeStep">
                <notice-conf v-if="noticeStepShow" :formData="templateVos" @changeFlowMsgSet="handleFlowMsgSet" />
            </el-tab-pane>
        </el-tabs>
        <div class="demo-drawer__footer clear">
            <el-button type="primary" @click="saveCopyer">确 定</el-button>
            <el-button @click="closeDrawer">取 消</el-button>
        </div>
        <select-user-dialog v-model:visible="copyerVisible" :data="checkedList" @change="sureCopyer" />
        <select-role-dialog v-model:visible="approverRoleVisible" :data="checkedRoleList" @change="sureRoleApprover" />
    </el-drawer>
</template>
<script setup>
import { ref, watch, computed, getCurrentInstance } from 'vue'
import selectUserDialog from '../dialog/selectUserDialog.vue'
import selectRoleDialog from '../dialog/selectRoleDialog.vue';
import FormPermConf from "./permConfig/FormPermConf.vue"
import noticeConf from "./noticeConfig/index.vue";
import $func from '@/utils/antflow/index'
import { setCopyerTypes } from '@/utils/antflow/const';
import { useStore } from '@/store/modules/workflow'
const { proxy } = getCurrentInstance();
const props = defineProps({
    directorMaxLevel: {
        type: Number,
        default: 3
    }
});
let copyerConfig = ref({})
let ccSelfSelectFlag = ref([])
let copyerVisible = ref(false)
let checkedList = ref([])
let formItems = ref([])
let formHiddenMap = ref({})

let approverRoleVisible = ref(false);
let checkedRoleList = ref([]);
let noticeStepShow = ref(false);
let templateVos = ref([]);

let activeName = ref('copyStep')
let copyStepShow = ref(true)
let formStepShow = ref(false)

//let testObj = JSON.parse("{\"lfFieldControlVOs\":[{\"fieldId\":\"input12931\",\"fieldName\":\"发件人姓名\",\"perm\":\"R\"},{\"fieldId\":\"switch96070\",\"fieldName\":\"是否保密\",\"perm\":\"E\"},{\"fieldId\":\"input23031\",\"fieldName\":\"发件人号码\",\"perm\":\"H\"}]}");

let store = useStore()
let { setCopyerConfigV2, setCopyerV2 } = store
let copyerDrawerV2 = computed(() => store.copyerDrawerV2)
let copyerConfigV2 = computed(() => store.copyerConfigV2)
let visible = computed({
    get() {
        handleTabClick({ paneName: "copyStep" })
        return copyerDrawerV2.value
    },
    set() {
        closeDrawer()
    }
})

watch(copyerConfigV2, (val) => {
    copyerConfig.value = val.value;
    formItems.value = copyerConfig.value.lfFieldControlVOs || [];
    formHiddenMap.value = copyerConfig.value.formHidden || {};
    templateVos.value = copyerConfig.value.templateVos || [];
    //console.log("copyerConfig.value========", JSON.stringify(copyerConfig.value))
    ccSelfSelectFlag.value = copyerConfig.value.ccSelfSelectFlag == 0 ? [] : [copyerConfig.value.ccSelfSelectFlag]
})

/**选择审批人类型更改事件 */
const changeType = (val) => {
    copyerConfig.value.nodeApproveList = [];
    copyerConfig.value.signType = 1;
    copyerConfig.value.noHeaderAction = 0;
    if (val == 3) {
        copyerConfig.value.directorLevel = 1;
    }
}

/**添加审批角色 */
const addRoleApprover = () => {
    approverRoleVisible.value = true;
    checkedList.value = copyerConfig.value.nodeApproveList
}
/**选择角色确认按钮 */
const sureRoleApprover = (data) => {
    copyerConfig.value.nodeApproveList = data;
    approverRoleVisible.value = false;
}
const addCopyer = () => {
    copyerVisible.value = true;
    checkedList.value = copyerConfig.value.nodeApproveList
}
const sureCopyer = (data) => {
    copyerConfig.value.nodeApproveList = data;
    copyerVisible.value = false;
}
const saveCopyer = () => {
    copyerConfig.value.ccSelfSelectFlag = ccSelfSelectFlag.value.length == 0 ? 0 : 1;
    copyerConfig.value.nodeDisplayName = $func.setCopyStrV2(copyerConfig.value);
    copyerConfig.value.error = !$func.setCopyStrV2(copyerConfig.value);
    console.log('保存copyerConfig审批人配置==', JSON.stringify(copyerConfig.value));
    setCopyerConfigV2({
        value: copyerConfig.value,
        flag: true,
        id: copyerConfigV2.value.id
    })
    closeDrawer();
}
const closeDrawer = () => {
    //console.log("copyerConfig.value.lfFieldControlVOs========", JSON.stringify(copyerConfig.value))
    setCopyerV2(false)
}
const handleTabClick = (tab, event) => {
    activeName.value = tab.paneName;
    if (tab.paneName == 'copyStep') {
        copyStepShow.value = true;
    }
    if (tab.paneName == 'formStep') {
        formStepShow.value = true;
    }
    if (tab.paneName == 'noticeStep') {
        noticeStepShow.value = true;
    }
}
const changePermVal = (data) => {
    copyerConfig.value.lfFieldControlVOs = data;
    //console.log("copyerConfig.value.lfFieldControlVOs========",JSON.stringify(copyerConfig.value))
}
/**外部表单模式: 整表隐藏标记变化 */
const changeFormHidden = (data) => {
    copyerConfig.value.formHidden = data;
}

/**消息设置 */
const handleFlowMsgSet = (data) => {
    copyerConfig.value.templateVos = !proxy.isEmpty(data) ? [data] : [];
    store.setCopyerConfigV2({
        value: copyerConfigV2.value.value,
        flag: true,
        id: copyerConfigV2.value.id
    })
}

</script>

<style scoped lang="scss">
@use "@/assets/styles/antflow/dialog.scss";

.selected_list {
    margin-bottom: 20px;
    line-height: 30px;
}

.selected_list span {
    margin-right: 10px;
    padding: 3px 6px 3px 9px;
    line-height: 12px;
    white-space: nowrap;
    border-radius: 2px;
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

.set_copyer {
    .copyer_content {
        padding: 20px 20px 0;

        .el-button {
            margin-bottom: 20px;
        }

        .el-checkbox {
            margin-bottom: 20px;
        }
    }
}

.approver_content {
    min-height: 250px;
    padding-bottom: 10px;
    border-bottom: 1px solid #f2f2f2;
    font-size: 14px;
}

.approver_content,
.approver_block {
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

.gap-2 {
    margin-top: 10px;
    margin-bottom: 10px;

    .gap-tag {
        margin-right: 10px;
    }
}

.tip {
    margin: 10px 0 22px 0;
    font-size: 12px;
    line-height: 16px;
    color: #f8642d;
    font-size: 16px;
}

.approver_text {
    padding: 28px 0px;
}
</style>