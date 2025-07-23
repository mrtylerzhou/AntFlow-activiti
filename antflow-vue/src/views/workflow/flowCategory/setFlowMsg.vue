<template>
    <!-- 设置通知消息 -->
    <el-dialog title="设置通知消息" v-model="dialogVisible" width="650px" append-to-body>
        <el-form ref="templateRef" label-width="130px" label-position="top" style="margin: 0 20px;">
            <el-form-item label="通知类型" prop="checkedMsgSendTypeList">
                <el-checkbox-group v-model="checkedMsgSendTypeList">
                    <el-checkbox style="margin: 5px;" v-for="(item, index) in notifyTypeList" :value="item.id"
                        :key="item.id" border>
                        {{ item.name }}
                        <msgIcon v-model:iconValue="item.id" viewValue="primary" />
                    </el-checkbox>
                </el-checkbox-group>
            </el-form-item>
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button type="primary" @click="submitDialog">确认</el-button>
                <el-button @click="closeDialog">关闭</el-button>
            </div>
        </template>
    </el-dialog>

</template>
<script setup>
import { ref, watch } from "vue";
import msgIcon from '@/components/Workflow/components/msgIcon.vue';
import { getAllNoticeTypes, saveTaskMgmt } from "@/api/workflow/flowMsgApi";
const { proxy } = getCurrentInstance();
const notifyTypeList = ref([]);
const checkedMsgSendTypeList = ref([]);
let props = defineProps({
    visible: {
        type: Boolean,
        default: false,
    },
    formMsgData: {
        type: Object,
        default: () => { },
    }
});

let dialogVisible = computed({
    get() {
        return props.visible
    },
    set() {
        closeDialog()
    }
})
const emits = defineEmits(["update:visible", "refresh"]);

watch(() => dialogVisible.value, (val) => {
    if (val) {
        checkedMsgSendTypeList.value = props.formMsgData.processNotices.filter(c => c.active).map(item => {
            return item.id;
        });
        getAllNoticeTypesList();
    }
});

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

function submitDialog() {
    let params = {
        processKey: props.formMsgData.formCode,
        notifyTypeIds: checkedMsgSendTypeList.value
    }
    saveTaskMgmt(params).then(res => {
        if (res && res.code == 200) {
            proxy.$modal.msgSuccess("保存成功");
            emits("refresh", props.formMsgData.flowType);
            closeDialog();
        } else {
            proxy.$modal.msgError("保存失败" + res.errMsg);
        }
    }).catch(err => {
        console.log(err);
    });
}
/** 取消操作添加条件模板表单 */
function closeDialog() {
    emits("update:visible", false);
} 
</script>