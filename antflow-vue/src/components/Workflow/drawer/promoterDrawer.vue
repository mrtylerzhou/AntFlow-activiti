<!--
 * @Date:  2024-05-25 14:05:59
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2023-05-24 15:17:13
 * @FilePath: /ant-flow/src/components/drawer/promoterDrawer.vue
-->
<template>
    <el-drawer :append-to-body="true" title="发起人" v-model="visible" class="set_promoter" :with-header="false"
        :size="680">
        <div class="el-drawer__header">
            <span class="drawer-title">发起人</span>
        </div>
        <el-tabs v-model="activeName" @tab-click="handleTabClick">
            <el-tab-pane label="发起人设置" name="promoterStep">
                <div class="promoter_content drawer_content">
                    <p>发起人在流程发起时自动获取，无需设置</p>
                </div>
            </el-tab-pane>
            <el-tab-pane v-if="formPermTabVisible" lazy label="表单权限设置" name="formStep">
                <form-perm-conf v-if="formStepShow" default-perm="E" v-model:formItems="formItems"
                    :formHidden="formHiddenMap"
                    @changePermVal="changePermVal" @changeFormHidden="changeFormHidden" />
            </el-tab-pane>
        </el-tabs>
        <div class="demo-drawer__footer clear">
            <el-button type="primary" @click="savePromoter">确 定</el-button>
            <el-button @click="closeDrawer">取 消</el-button>
        </div>
    </el-drawer>
</template>
<script setup>
import $func from '@/utils/antflow/index'
import { useStore } from '@/store/modules/workflow'
import { computed, ref, watch } from 'vue'
import formPermConf from "./permConfig/FormPermConf.vue";

let flowPermission = ref([])
let checkedList = ref([])
let formItems = ref([])
let formHiddenMap = ref({})
let activeName = ref('promoterStep')
let formStepShow = ref(false)

let store = useStore()
const route = useRoute()
//字段权限tab可见性:DIY流程仅在启用辅助表单时显示;LF流程始终显示
let formPermTabVisible = computed(() => {
    const isDIYRoute = (route.path || '').indexOf('diy-design') > 0;
    return isDIYRoute ? !!store.useAuxiliaryForm : true;
})
let { setPromoter, setPromoterConfig, setFlowPermission } = store
let promoterDrawerVisible = computed(() => store.promoterDrawer)
let promoterConfig1 = computed(() => store.promoterConfig)
let flowPermission1 = computed(() => store.flowPermission1)

let localNodeConfig = ref({})

let visible = computed({
    get() {
        handleTabClick({ paneName: "promoterStep" })
        return promoterDrawerVisible.value
    },
    set() {
        closeDrawer()
    }
})

/** 监听 store 中传入的节点配置 */
watch(promoterConfig1, (val) => {
    if (val && val.value) {
        localNodeConfig.value = val.value;
        formItems.value = val.value.lfFieldControlVOs || [];
        formHiddenMap.value = val.value.formHidden || {};
    }
})

watch(flowPermission1, (flow) => {
    flowPermission.value = flow.value
})

const addPromoter = () => {
    checkedList.value = flowPermission.value
    // TODO: open user select dialog if needed
}

/**低代码表单字段权限 */
const changePermVal = (data) => {
    localNodeConfig.value.lfFieldControlVOs = data;
}

/**外部表单模式: 整表隐藏标记变化 */
const changeFormHidden = (data) => {
    localNodeConfig.value.formHidden = data;
}

const savePromoter = () => {
    setFlowPermission({
        value: flowPermission.value,
        flag: true,
        id: promoterConfig1.value.id,
    })
    setPromoterConfig({
        value: localNodeConfig.value,
        flag: true,
        id: promoterConfig1.value.id,
    })
    closeDrawer()
}

const closeDrawer = () => {
    setPromoter(false)
}

/**
 * 切换tab
 */
const handleTabClick = (tab) => {
    activeName.value = tab.paneName;
    if (tab.paneName == 'formStep') {
        formStepShow.value = true;
    } else {
        formStepShow.value = false;
    }
}
</script>
<style scoped lang="scss">
@use "@/assets/styles/antflow/dialog.scss";

.el-drawer__header {
    margin-bottom: 5px !important;
}

.el-tabs {
    margin-left: 20px !important;
}

.set_promoter {
    .promoter_content {
        padding: 0 20px;

        .el-button {
            margin-bottom: 20px;
        }

        p {
            padding: 18px 0;
            font-size: 14px;
            line-height: 20px;
            color: #000000;
        }
    }
}
</style>
