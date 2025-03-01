<template>
  <!-- 选择用户 -->
  <el-dialog title="选择用户" v-model="visibleDialog" style="width: 800px !important" :before-close="handleClose" append-to-body>
    <el-form :model="qform" ref="queryRef" :inline="true">
      <el-form-item label="用户名称" prop="userName">
        <el-input v-model="qform.userName" placeholder="请输入用户名称" clearable style="width: 150px" size="default"
          @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="default" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
      <el-form-item class="pull-right">
        <el-button type="success" size="default" :disabled="!canCommit" @click="saveDialog">确定</el-button>
      </el-form-item>
    </el-form>
    <el-radio-group class="radio-table" v-model="selectUserId" style="width: 100%">
      <el-table ref="refTable" row-key="userId" :data="userList" v-loading="loading" height="350px">
        <el-table-column v-if="multiple" align="center" type="selection" width="50px" :reserve-selection="multiple" />
        <el-table-column v-else align="center" width="50px">
          <template v-slot="scope">
            <el-radio :value="scope.row.userId"></el-radio>
          </template>
        </el-table-column>
        <el-table-column label="用户名称" prop="userName" :show-overflow-tooltip="true" />
        <el-table-column label="邮箱" prop="email" :show-overflow-tooltip="true" />
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <el-tag size="default">
              {{ scope.row.status === "0" ? "正常" : "停用" }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-radio-group>
    <template #footer>
      <div class="dialog-footer">
        <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
          :layout="layoutSize" @pagination="getList" />
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { watch } from "vue";
import { getUsers } from "@/api/mock";
const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  multiple: {
    type: Boolean,
    default: false,
  },
  data: {
    type: Array,
    default: [],
  },
});
const { proxy } = getCurrentInstance();
let emits = defineEmits(["update:visible", "update:value", "change"]);
const loading = ref(false);
const userList = ref([]);
const total = ref(23);
const layoutSize = 'total, prev, pager, next';
let checkedUsersList = ref([]);

let canSelect = ref(false);
// 单选下选中的用户
const selectUserId = ref('5');
// 多选下选中的用户列表
const multiSelectUser = ref([]);

const queryParams = reactive({
  qform: {},
  pageDto: {
    page: 1,
    pageSize: 10
  }
});
const { pageDto, qform } = toRefs(queryParams);

const canCommit = computed(() => {
  return props.multiple ? multiSelectUser.value.length > 0 : (selectUserId.value != null && selectUserId.value !== '');
});

let visibleDialog = computed({
  get() {
    return props.visible;
  },
  set() {
    closeDialog();
  },
});

// watch(() => props.data, (newVal) => {
//   console.log('watch==props.data======newVal======',JSON.stringify(newVal));
//   checkedUsersList.value = newVal.map((item) => {
//     return {
//       userId: item.id,
//       userName: item.name,
//     };
//   });
// },
//   { deep: true, immediate: true }
// );
// 查询表数据
const getList = async () => {
  loading.value = true;
  await getUsers().then((res) => {
    loading.value = false;
    userList.value = res.data.map((item) => {
      return {
        userId: Number(item.id),
        userName: item.name,
        email: "574427343@qq.com",
        status: 1,
      };
    });
  }).catch((res) => {
    proxy.$modal.msgError("获取用户列表失败" + res.message);
  });
}

getList();

/** 搜索按钮操作 */
function handleQuery() {
  qform.pageNum = 1;
  getList();
}

/**
 * 确认/保存
 */
let saveDialog = () => {
  console.log('selectUserId======', selectUserId.value);
  let checkedList = checkedUsersList.value.push(selectUserId.value);
  // let checkedList = [...checkedUsersList.value].map((item) => ({
  //   type: 1,
  //   targetId: item.userId,
  //   name: item.userName,
  // }));
  // handleClose();
  //emits("change", checkedList);
};

/**
 * 关闭弹窗
 */
const closeDialog = () => {
  handleClose();
};
const handleClose = () => {
  userList.value = [];
  checkedUsersList.value = [];
  emits("update:visible", false);
};
/** 重置按钮操作 */
function resetQuery() {
  qform = {};
  proxy.resetForm("queryRef");
  handleQuery();
}
</script>
<style lang="css" scoped>
.tip {
  padding: 8px 16px;
  background-color: rgb(197.7, 225.9, 255);
  border-left: 5px solid #409eff;
}
</style>
