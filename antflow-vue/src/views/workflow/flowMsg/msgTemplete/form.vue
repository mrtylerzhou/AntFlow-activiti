<template>
    <!-- 添加条件模板对话框 -->
    <el-dialog title="添加条件" v-model="dialogVisible" width="650px" append-to-body>
        <el-form :model="templateForm" :rules="templateRules" ref="conditionTemplateRef" label-width="130px"
            label-position="top" style="margin: 0 20px;">
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
                    <el-form-item label="主题" prop="systemTitle">
                        <el-input v-model="templateForm.systemTitle" type="textarea" placeholder="请输入主题"
                            :autosize="{ minRows: 3, maxRows: 3 }" style="height: 80px;" />
                    </el-form-item>
                </el-col>
            </el-row>
            <div class="mb-4">
                <el-button style="margin: 5px;" type="success" plain round v-for="btnTxt in quickAnswerList"
                    @click="templateForm.systemTitle += btnTxt">
                    {{ btnTxt }}
                </el-button>
            </div>
            <el-row>
                <el-col :span="24">
                    <el-form-item label="通知内容" prop="systemContent">
                        <el-input v-model="templateForm.systemContent" type="textarea" placeholder="请输入通知内容"
                            :autosize="{ minRows: 5, maxRows: 5 }" style="height: 120px;" />
                    </el-form-item>
                </el-col>
            </el-row>

        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button type="primary" @click="submitFormBtn">确 定</el-button>
                <el-button @click="closeDialog">取 消</el-button>
            </div>
        </template>
    </el-dialog>

</template>
<script setup>
import { ref, watch } from "vue";
import { getAllNoticeTypes, saveInformationTemp } from "@/api/workflow/flowMsgApi";
const { proxy } = getCurrentInstance();
const notifyTypeList = ref([]);
const quickAnswerList = ["流程类型", "流程名称", "流程编号", "当前审批人"];
let props = defineProps({
    visible: {
        type: Boolean,
        default: false,
    },
    formData: {
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
const emits = defineEmits(["update:visible"]);

watch(() => dialogVisible.value, (val) => {
    if (val) {
        reset();
        templateForm.value = props.formData;
        getAllNoticeTypesList();
    }
});
const data = reactive({
    templateForm: {},
    templateRules: {
        notifyType: [{ required: true, message: '', trigger: 'blur' }],
        systemTitle: [{ required: true, message: '', trigger: 'blur' }],
        systemContent: [{ required: true, message: '', trigger: 'blur' }],
    }
});
const { templateForm, templateRules } = toRefs(data);
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
/** 提交条件模板表单 */
function submitFormBtn() {
    proxy.$refs["conditionTemplateRef"].validate(valid => {
        if (valid) {
            proxy.$modal.loading();
            saveInformationTemp(templateForm.value).then(res => {
                if (res && res.code == 200) {
                    proxy.$modal.msgSuccess("添加成功");
                    emits("update:visible", false);
                } else {
                    proxy.$modal.msgError("添加失败" + res.errMsg);
                }
            }).catch(err => {
                console.log(err);
            });
            proxy.$modal.closeLoading();
        }
    });
}
/** 取消操作添加条件模板表单 */
function closeDialog() {
    emits("update:visible", false);
    reset();
}
/** 重置操作表单 */
function reset() {
    templateForm.value = {
        notifyType: [],
        systemTitle: "工作流名称是:",
        systemContent: ""
    };
}
</script>