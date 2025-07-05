<template>
    <!-- 添加消息模板对话框 -->
    <el-dialog title="添加消息模板" v-model="dialogVisible" width="650px" append-to-body>
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
        </el-form>
    </el-dialog>

</template>
<script setup>
import { ref, watch } from "vue";
import { getAllNoticeTypes } from "@/api/workflow/flowMsgApi";
const { proxy } = getCurrentInstance();
const notifyTypeList = ref([]);
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
        systemTitle: [{ required: true, message: '', trigger: 'blur' }],
        systemContent: [{ required: true, message: '', trigger: 'blur' }],
        event: [{ required: true, message: '请选择事件类型', trigger: 'blur' }],
        jumpUrl: [{ required: true, message: '请选择跳转页面', trigger: 'blur' }],
    }
});
const { templateForm, templateRules } = toRefs(data);

watch(() => dialogVisible.value, (val) => {
    if (val) {
        reset();
        templateForm.value = props.formData;
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

/** 重置操作表单 */
function reset() {
    templateForm.value = {
        notifyType: [1],
        systemTitle: "工作流名称是:",
        systemContent: "",
    };
}
</script>