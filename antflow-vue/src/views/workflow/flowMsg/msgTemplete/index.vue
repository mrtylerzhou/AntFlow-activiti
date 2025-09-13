<template>
    <div class="app-container">
        <el-tabs :tab-position="tabPosition" v-model="activeName" class="demo-tabs">
            <el-tab-pane label="通用模板" name="comTemplate">
                <com-template-list v-if="activeName == 'comTemplate'" />
            </el-tab-pane>
            <el-tab-pane label="短信模板" name="smsTemplate">
                <sms-template-list v-if="activeName == 'smsTemplate'" />
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<script setup>
import comTemplateList from "./templates/comTemplateList.vue";
import smsTemplateList from "./templates/smsTemplateList.vue";
const tabPosition = ref('left')
const activeName = ref('comTemplate')
</script>
<style scoped>
.demo-tabs>.el-tabs__content {
    padding: 32px;
    color: #6b778c;
    font-size: 32px;
    font-weight: 600;
}

.el-tabs--left .el-tabs__item.is-left {
    text-align: right;
    background: #fff;
}

.el-tabs--right .el-tabs__content,
.el-tabs--left .el-tabs__content {
    height: 100%;
}
</style>