<!--
 * @Date:  2024-05-25 14:05:59
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2023-03-15 14:59:19
 * @FilePath: /flow/src/components/dialog/roleDialog.vue
-->
<template>
   <el-dialog title="选择角色" v-model="visibleDialog" style="width: 680px !important;"  append-to-body class="promoter_person">
      <div class="person_body clear">
          <div class="person_tree l">
              <input type="text" placeholder="搜索角色" v-model="searchVal">
              <selectBox :list="list" />
          </div>
          <selectResult :total="total" @del="delList" :list="resList"/>
      </div>
      <template #footer>
          <el-button @click="closeDialog">取 消</el-button>
          <el-button type="primary" @click="saveDialog">确 定</el-button>
      </template>
  </el-dialog>
</template>
<script setup>
import selectBox from '../selectBox.vue';
import selectResult from '../selectResult.vue';
import { computed, watch, ref } from 'vue'
import $func from '@/utils/flow/index.js'
import { roles, getRoleList, searchVal } from './common'
let props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  data: {
    type: Array,
    default: () => []
  }
});
let checkedRoleList = ref([])
let emits = defineEmits(['update:visible', 'change'])
let list = computed(() => {
  return [{
    type: 'role',
    not: true,
    data: roles.value,
    isActive: (item) => $func.toggleClass(checkedRoleList.value, item, 'roleId'),
    change: (item) => {
      checkedRoleList.value = [item]
    }
  }]
})
let resList = computed(() => {
  return [{
    type: 'role',
    data: checkedRoleList.value,
    cancel: (item) => $func.removeEle(checkedRoleList.value, item, 'roleId')
  }]
})
let visibleDialog = computed({
  get() {
    return props.visible
  },
  set(val) {
    closeDialog()
  }
})
watch(() => props.visible, (val) => {
  if (val) {
    getRoleList();
    searchVal.value = "";
    checkedRoleList.value = props.data.map(({ name, targetId }) => ({
      roleName: name,
      roleId: targetId
    }));
  }
})
let total = computed(() => checkedRoleList.value.length)
const saveDialog = () => {
  let checkedList = checkedRoleList.value.map(item => ({
    type: 4,
    targetId: item.roleId,
    name: item.roleName
  }))
  emits('change', checkedList)
}
const delList = () => {
  checkedRoleList.value = [];
}

const closeDialog = () => {
  emits('update:visible', false)
}
</script>

<style scoped lang="scss"> 
@import "@/assets/styles/flow/dialog.scss";
</style>