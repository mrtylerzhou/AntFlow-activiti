<template>
    <div>
        <!-- 退回对话框 -->
        <el-dialog :title="title" v-model="openVisible" width="550px" append-to-body>
            <el-form :model="approveForm" :rules="rules" ref="approveFormRef" label-width="130px" label-position="top"
                style="margin: 0 20px;">
                <el-row>  
                    <el-col :span="24">
                        <el-form-item label="备注/说明" prop="remark">
                            <el-input v-model="approveForm.remark" type="textarea" placeholder="请输入审批备注"
                                :maxlength="100" show-word-limit :autosize="{ minRows: 4, maxRows: 4 }"
                                :style="{ width: '100%' }"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="openVisible = false">取 消</el-button>
                    <el-button type="primary" @click="clickSubmit(approveFormRef)">确 定</el-button>  
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<script setup>
import { ref } from 'vue' 
let props = defineProps({
    title: {
        type: String,
        default: '流程审批'
    },
    visible: {
        type: Boolean,
        default: false,
    }
});   
const emits = defineEmits(['update:visible','clickConfirm']);  
let openVisible = computed({
  get() { 
    return props.visible
  },
  set(val) { 
    emits('update:visible', val)
  }
}) 
const approveFormRef = ref(null);
const approveForm = reactive({ 
    remark: ''
});

let rules = { 
    remark: [{
        required: true,
        message: '请输入备注',
        trigger: 'blur'
    }]
};

const clickSubmit = (approveFormRef) => { 
    if (!approveFormRef) return;
    approveFormRef.validate(async (valid) => {
        if (valid) {
            emits('update:visible', false);
            emits('clickConfirm', approveForm);
        }
    });
}

</script>