<template>
    <!-- 添加条件模板对话框 -->
    <el-dialog title="添加条件" v-model="dialogVisible" width="650px" append-to-body>
        <el-form :model="templateForm" :rules="templateRules" ref="templateRef" label-width="130px" label-position="top"
            style="margin: 0 20px;">
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
                    <el-form-item label="模板名称" prop="name">
                        <el-input v-model="templateForm.name" placeholder="请输入唯一模板名称" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="24">
                    <el-form-item label="主题" prop="systemTitle">
                        <el-input v-model="templateForm.systemTitle" type="textarea" placeholder="请输入主题"
                            :autosize="{ minRows: 3, maxRows: 3 }" style="height: 55px;" />
                    </el-form-item>
                </el-col>
            </el-row>
            <div class="mb-4">
                <el-button style="margin: 5px;" type="success" link v-for="btnTxt in wildcardList"
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
import { getAllNoticeTypes, saveInformationTemp, getWildcardCharacter } from "@/api/workflow/flowMsgApi";
const { proxy } = getCurrentInstance();
const notifyTypeList = ref([]);
const wildcardList = ref([]);
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
const data = reactive({
    templateForm: {},
    templateRules: {
        name: [{ required: true, message: '', trigger: 'blur' }],
        notifyType: [{ required: true, message: '请选择通知类型', trigger: 'blur' }],
        systemTitle: [{ required: true, message: '', trigger: 'blur' }],
        systemContent: [{ required: true, message: '', trigger: 'blur' }],
    }
});
const { templateForm, templateRules } = toRefs(data);

watch(() => dialogVisible.value, (val) => {
    if (val) {
        reset();
        templateForm.value = props.formData;
        getAllNoticeTypesList();
        getWildcardCharacterList();
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
/** 获取所有通知类型列表 */
const getWildcardCharacterList = () => {
    getWildcardCharacter().then(res => {
        if (res && res.code == 200) {
            wildcardList.value = res.data.map(x => {
                return x.desc
            });
        } else {
            proxy.$modal.msgError("获取通知类型失败" + res.errMsg);
        }
    }).catch(err => {
        console.log(err);
    });
};
/** 提交条件模板表单 */
function submitFormBtn() {
    proxy.$refs["templateRef"].validate(valid => {
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