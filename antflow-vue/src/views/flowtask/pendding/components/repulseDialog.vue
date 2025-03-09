<template>
    <div>
        <!-- 退回对话框 -->
        <el-dialog title="退回" v-model="openVisible" append-to-body>
            <el-form :model="repulseForm" :rules="rules" ref="repulseFormRef" label-width="130px" label-position="top"
                style="margin: 0 20px;">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="退回类型" prop="backToModifyType">
                            <el-radio-group v-model="repulseForm.backToModifyType">
                                <el-radio-button value=1>
                                    上一节点
                                    <el-popover placement="top-start" :width="200"
                                        :visible="openVisible && tipsVisible1" effect="dark" content="退回上一个审批节点">
                                        <template #reference>
                                            <el-icon>
                                                <Comment />
                                            </el-icon>
                                        </template>
                                    </el-popover>
                                </el-radio-button>
                                <el-radio-button value=2>
                                    发起人
                                    <el-popover placement="top-start" title="【重新流转】" :width="200"
                                        :visible="openVisible && tipsVisible2" effect="dark"
                                        content="退回到发起人，发起人重新提交后流程重新开始流转">
                                        <template #reference>
                                            <el-icon>
                                                <Comment />
                                            </el-icon>
                                        </template>
                                    </el-popover>
                                </el-radio-button>
                                <el-radio-button value=3>
                                    发起人
                                    <el-popover placement="top-start" title="【回到当前节点】" :width="200"
                                        :visible="openVisible && tipsVisible3" effect="dark"
                                        content="退回到发起人，发起人重新提交后流程回到当前审批人">
                                        <template #reference>
                                            <el-icon>
                                                <Comment />
                                            </el-icon>
                                        </template>
                                    </el-popover>
                                </el-radio-button>
                                <el-radio-button value=4>
                                    任意节点
                                    <el-popover placement="top-start" title="【回到下一节点】" :width="200"
                                        :visible="openVisible && tipsVisible4" effect="dark"
                                        content="退回历史任意审批节点,提交后回到下一个节点">
                                        <template #reference>
                                            <el-icon>
                                                <Comment />
                                            </el-icon>
                                        </template>
                                    </el-popover>
                                </el-radio-button>
                                <el-radio-button value=5>
                                    任意节点
                                    <el-popover placement="top-start" :width="200" title="【回到当前节点】"
                                        :visible="openVisible && tipsVisible5" effect="dark"
                                        content="退回历史任意审批节点，提交后回到当前审批人">
                                        <template #reference>
                                            <el-icon>
                                                <Comment />
                                            </el-icon>
                                        </template>
                                    </el-popover>
                                </el-radio-button>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="19" v-if="chooseNodeVisible">
                        <el-form-item label="审批节点" prop="backToNodeId">    
                            <TagFlowNodeSelect v-model:value="repulseForm.backToNodeId" :flowNode="checkedFlowNode">
                                <template #append> 
                                    <el-button class="append-add" type="default" icon="Search" @click="handleChooseNode" />
                                </template>
                            </TagFlowNodeSelect>   
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注/说明" prop="remark">
                            <el-input v-model="repulseForm.remark" type="textarea" placeholder="请输入审批备注"
                                :maxlength="100" show-word-limit :autosize="{ minRows: 4, maxRows: 4 }"
                                :style="{ width: '100%' }"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="openVisible = false">取 消</el-button>
                    <el-button type="primary" @click="clickSubmit(repulseFormRef)">确 定</el-button>       
                </div>
            </template>
        </el-dialog>
        <el-dialog title="选择审批流程" v-model="openFlowNodeVisible" :before-close="handleFlowNodeClose" append-to-body>
            <ReviewWarp />
        </el-dialog>
    </div>
</template>
<script setup>
import { ref } from 'vue';
import ReviewWarp from './chooseFlowNode/reviewWarp.vue';
import TagFlowNodeSelect from './TagFlowNodeSelect/index.vue';
import { useStore } from '@/store/modules/workflow';   
let store = useStore();
let { setApproveChooseFlowNodeConfig } = store;
let props = defineProps({
    visible: {
        type: Boolean,
        default: false,
    }
});
const emits = defineEmits(['update:visible', 'clickConfirm']);
let checkedFlowNode = ref(null);
const repulseFormRef = ref(null);
const repulseForm = reactive({
    backToModifyType: 3,
    backToNodeId:'',
    remark: ''
});

let openFlowNodeVisible = computed({
    get() {
        return store.approveChooseFlowNode.visible
    },
    set() { 
        repulseForm.backToNodeId = store.approveChooseFlowNode.nodeId; 
        checkedFlowNode.value = store.approveChooseFlowNode;
    }
})

let openVisible = computed({
    get() {
        return props.visible
    },
    set(val) { 
        emits('update:visible', val)
    }
})
const chooseNodeVisible = computed({
    get() {
        return repulseForm.backToModifyType == 4 || repulseForm.backToModifyType == 5
    }
});

const tipsVisible1 = computed(() => repulseForm.backToModifyType == 1);
const tipsVisible2 = computed(() => repulseForm.backToModifyType == 2);
const tipsVisible3 = computed(() => repulseForm.backToModifyType == 3);
const tipsVisible4 = computed(() => repulseForm.backToModifyType == 4);
const tipsVisible5 = computed(() => repulseForm.backToModifyType == 5);


let rules = {
    backToModifyType: [{
        required: true,
        message: '请选择退回类型',
        trigger: 'blur'
    }],
    backToNodeId: [{
        required: true,
        message: '请选择审批节点',
        trigger: 'blur'
    }],
    remark: [{
        required: true,
        message: '请输入备注',
        trigger: 'blur'
    }]
};

const clickSubmit = (repulseFormRef) => {
    if (!repulseFormRef) return;
    repulseFormRef.validate(async (valid) => {
        if (valid) {
            openVisible = false;
            emits('clickConfirm', repulseForm)
        }
    });
} 

const handleChooseNode = () => { 
    setApproveChooseFlowNodeConfig({
        visible: true
    });
}
const handleFlowNodeClose = () => {  
    setApproveChooseFlowNodeConfig({
        visible: false
    });
}

</script> 