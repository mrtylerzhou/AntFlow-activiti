<template>
  <el-dialog title="选择成员" v-model="visibleDialog" style="width: 680px !important;" append-to-body
    class="promoter_person">
    <div class="person_body clear">
      <div class="person_tree l">
        <input type="text" placeholder="搜索人员" v-model="searchVal">
        <selectBox :list="list" />
      </div>
      <selectResult :total="total" @del="delList" :list="resList" />
    </div>
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取 消</el-button>
      <el-button type="primary" @click="saveDialog">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import selectBox from '../selectBox.vue';
import selectResult from '../selectResult.vue';
import { computed, watch, ref } from 'vue';
import { departments, getEmployeeList, searchVal } from './common'
import $func from '@/utils/antflow/index.js'
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
let emits = defineEmits(['update:visible', 'change'])
let visibleDialog = computed({
  get() {
    return props.visible
  },
  set() {
    closeDialog()
  }
});
let checkedEmployessList = ref([])
let list = computed(() => {
  return [{
    type: 'employee',
    data: departments.value.employees,
    isActive: (item) => $func.toggleClass(checkedEmployessList.value, item),
    change: (item) => $func.toChecked(checkedEmployessList.value, item),
  }]
})
let resList = computed(() => {
  let data = [{
    type: 'employee',
    data: checkedEmployessList.value,
    cancel: (item) => $func.removeEle(checkedEmployessList.value, item)
  }]
  return data
})
watch(() => props.visible, (val) => {
  if (val) {
    getEmployeeList();
    searchVal.value = "";
    checkedEmployessList.value = props.data.filter(item => item.type === 5).map(({ name, targetId }) => ({
      employeeName: name,
      id: targetId
    }));
  }
})

const closeDialog = () => {
  emits('update:visible', false)
}

let total = computed(() => checkedEmployessList.value.length)

let saveDialog = () => {
  let checkedList = [
    ...checkedEmployessList.value
  ].map(item => ({
    type: 5,
    targetId: item.id,
    name: item.employeeName
  }))
  emits('change', checkedList)
}
const delList = () => {
  checkedEmployessList.value = []
}
</script>
<style scoped lang="scss">
@import "@/assets/styles/flow/dialog.scss";
</style>