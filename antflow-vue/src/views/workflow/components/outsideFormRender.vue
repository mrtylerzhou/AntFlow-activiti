<template>
    <div style="max-width: 600px;min-height: 100px; margin: 50px auto;background-color: #ffff;">
        <div>
            <el-row>
                <el-col :span="24" class="mb20">
                    <el-alert title="*模拟外部系统表单，接入本流程引擎" type="warning" center effect="dark" :closable="false" />
                </el-col>
            </el-row>
        </div>
        <el-form label-position="top" v-if="formDataArr">
            <el-row>
                <el-col :span="24"  v-for="item in formDataArr">
                    <el-form-item>
                        <template #label>
                            {{ item.label }}
                        </template>
                        <el-input :disabled="true" :style="{ width: '100%' }" :value="item.value" />
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <p v-else="formData" v-html="formData"></p> 
    </div>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
const { proxy } = getCurrentInstance()
let formDataArr = ref(null);
let props = defineProps({
    formData: {
        type: String,
        default: '',
    }
});

onMounted(() => {
    if (!proxy.isObjEmpty(props.formData) && props.formData.startsWith('[')) {
        formDataArr.value = JSON.parse(props.formData);
    }
}); 
</script>
<style scoped lang="scss">
.el-form-item__label-wrap {
    margin-right: 0px !important;
}
</style>