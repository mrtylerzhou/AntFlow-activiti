<!--
 * @Date:  2024-05-25 14:05:59
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2023-05-24 15:20:53
 * @FilePath: /ant-flow/src/components/drawer/copyerDrawer.vue
-->
<template>
    <el-drawer :append-to-body="true" title="抄送人设置" v-model="visible" class="set_copyer"  :with-header="false" :size="680">
        <span class="drawer-title">抄送人设置</span> 
        <div class="demo-drawer__content">
            <div class="copyer_content drawer_content">
                <el-button type="primary" size="default" @click="addCopyer">添加成员</el-button>
                <p class="selected_list">
                    <span v-for="(item,index) in copyerConfig.nodeApproveList" :key="index">{{item.name}}
                        <img src="@/assets/images/add-close1.png" @click="$func.removeEle(copyerConfig.nodeApproveList,item,'targetId')">
                    </span>
                    <a v-if="copyerConfig.nodeApproveList&&copyerConfig.nodeApproveList.length!=0" @click="copyerConfig.nodeApproveList=[]">清除</a>
                </p>
                <el-checkbox-group v-model="ccSelfSelectFlag" class="clear">
                    <el-checkbox :value="1">允许发起人自选抄送人</el-checkbox>            
                </el-checkbox-group>       
            </div>
            <div class="demo-drawer__footer clear">
                <el-button type="primary" size="default" @click="saveCopyer">确 定</el-button>
                <el-button size="default" @click="closeDrawer">取 消</el-button>
            </div>
            <selectUser
                v-model:visible="copyerVisible"
                :data="checkedList"
                @change="sureCopyer"
            />
        </div>
    </el-drawer>
</template>
<script setup>
import { ref, watch, computed } from 'vue'
import $func from '@/utils/flow/index'
import { useStore } from '@/store/modules/outsideflow'

import selectUser from '../dialog/selectUserDialog.vue'

let copyerConfig = ref({})
let ccSelfSelectFlag = ref([])
let copyerVisible = ref(false)
let checkedList = ref([])
let store = useStore()
let { setCopyerConfig, setCopyer } = store
let copyerDrawer = computed(()=> store.copyerDrawer)
let copyerConfig1 = computed(()=> store.copyerConfig1)
let visible = computed({
    get() {
        return copyerDrawer.value
    },
    set() {
        closeDrawer()
    }
})
/**监听对象 */
watch(copyerConfig1, (val) => {
    copyerConfig.value = val.value;
    ccSelfSelectFlag.value = copyerConfig.value.ccSelfSelectFlag == 0 ? [] : [copyerConfig.value.ccSelfSelectFlag]
})
/**打开人员选择dialog */
const addCopyer = () => {
    copyerVisible.value = true;
    checkedList.value = copyerConfig.value.nodeApproveList;
}
/**确定人员选择并关闭dialog */
const sureCopyer = (data) => {
    copyerConfig.value.nodeApproveList = data;
    copyerVisible.value = false;
}
/**保存抄送人并关闭抽屉 */
const saveCopyer = () => {
    copyerConfig.value.ccSelfSelectFlag = ccSelfSelectFlag.value.length == 0 ? 0 : 1;
    copyerConfig.value.error = !$func.copyerStr(copyerConfig.value);
    setCopyerConfig({
        value: copyerConfig.value,
        flag: true,
        id: copyerConfig1.value.id
    })
    closeDrawer();
}
/**关闭抽屉 */
const closeDrawer = () => {
    setCopyer(false)
}    
</script>

<style scoped lang="scss">  
@import "@/assets/styles/flow/dialog.scss";
.selected_list {
    margin-bottom: 20px;
    line-height: 30px;
}

.selected_list span {
    margin-right: 10px;
    padding: 10px 15px 10px 20px;
    line-height: 12px;
    white-space: nowrap;
    border-radius: 20px;
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
</style>