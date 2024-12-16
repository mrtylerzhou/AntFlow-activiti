<template>
  <!-- 授权用户 -->
  <el-dialog
    title="选择用户"
    v-model="visibleDialog"
    style="width: 800px !important"
    :before-close="handleClose"
    append-to-body
  >
    <el-row :gutter="10">
      <el-col :span="16">
        <div>
          <el-form :model="queryParams" ref="queryRef" :inline="true">
            <el-form-item label="用户名称" prop="userName">
              <el-input
                v-model="queryParams.userName"
                placeholder="请输入用户名称"
                clearable
                style="width: 150px"
                size="default"
                @keyup.enter="handleQuery"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="default" @click="handleQuery"
                >搜索</el-button
              >
            </el-form-item>
          </el-form>
          <el-table
            ref="refTable"
            :data="userList"
            v-loading="loading"
            border
            height="350px"
          >
            <el-table-column
              label="操作"
              width="55"
              align="center"
              class-name="small-padding fixed-width"
            >
              <template #default="scope">
                <el-button
                  link
                  type="primary"
                  size="default"
                  icon="CirclePlus"
                  @click="handleSelectUser(scope.row)"
                />
              </template>
            </el-table-column>
            <el-table-column
              label="用户名称"
              prop="userName"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              label="邮箱"
              prop="email"
              :show-overflow-tooltip="true"
            />
            <el-table-column label="状态" align="center" prop="status">
              <template #default="scope">
                <el-tag size="default">
                  {{ scope.row.status === "0" ? "正常" : "停用" }}</el-tag
                >
              </template>
            </el-table-column>
          </el-table>
          <div style="margin: 10px 0">
            <el-pagination
              v-show="total > 0"
              size="default"
              background
              layout="prev, pager, next"
              :total="total"
            />
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <p class="tip">已选中列表</p>
        <el-table
          ref="selectedTable"
          :data="checkedUsersList"
          border
          height="350px"
        >
          <el-table-column
            label="操作"
            width="55"
            align="center"
            class-name="small-padding fixed-width"
          >
            <template #default="scope">
              <el-button
                link
                type="primary"
                size="default"
                icon="Delete"
                @click="handleRemove(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column
            label="用户名称"
            prop="userName"
            :show-overflow-tooltip="true"
          />
        </el-table>
      </el-col>
    </el-row>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" size="default" @click="saveDialog"
          >确 定</el-button
        >
        <el-button size="default" @click="closeDialog">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="selectUserDialog">
import { watch } from "vue";
//import { getEmployees } from '@/api/mockoutsideflow';
import { getDynamicsList } from "@/api/mock";
import { useStore } from "@/store/modules/outsideflow";
let store = useStore();
const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  data: {
    type: Array,
    default: [],
  },
});
const { proxy } = getCurrentInstance();
let emits = defineEmits(["update:visible", "change"]);
const loading = ref(false);
const userList = ref([]);
const total = ref(0);
let checkedUsersList = ref([]);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: undefined,
  phonenumber: undefined,
});

let visibleDialog = computed({
  get() {
    return props.visible;
  },
  set() {
    closeDialog();
  },
});
let list = computed(() => props.data);

watch(() => props.visible,
  (newVal) => {
    if (newVal) {
      getList();
    }
  },
  { deep: true, immediate: true }
);

watch(list, (newVal) => {
    checkedUsersList.value = newVal.map((item) => {
      return {
        userId: item.targetId,
        userName: item.name,
      };
    });
  },
  { deep: true, immediate: true }
); 
// 查询表数据
function getList() {
  if (!store.basideFormConfig || !store.basideFormConfig.userRequestUri) {
    return;
  }
  //console.log('store.basideFormConfig.userRequestUri=====',JSON.stringify(store.basideFormConfig.userRequestUri))
  loading.value = true;
  getDynamicsList(store.basideFormConfig.userRequestUri).then((res) => {
    //console.log('store.basideFormConfig.userRequestUri==res========',JSON.stringify(res))
    loading.value = false;
    userList.value = res.data.map((item) => {
      return {
        userId: item.id,
        userName: item.name,
        email: "574427343@qq.com",
        status: 1,
      };
    });
  }).catch((res) =>{
    proxy.$modal.msgError("[应用管理]配置的审批人模板URL不正确，未能成功获取数据");
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

/** 选择授权用户操作 */
function handleSelectUser(row) {
  if (!row || row.userId == "") {
    proxy.$modal.msgError("请选择用户");
    return;
  }
  if (checkedUsersList.value.some((c) => c.userId == row.userId)) {
    proxy.$modal.msgError("用户已被选中");
  } else {
    checkedUsersList.value.push(row);
  }
}
/** 移除选中用户操作 */
function handleRemove(row) {
  if (checkedUsersList.value.some((c) => c.userId == row.userId)) {
    checkedUsersList.value = checkedUsersList.value.filter(
      (item) => item.userId != row.userId
    );
  } else {
    proxy.$modal.msgError("用户已被移除");
  }
}
/**
 * 确认/保存
 */
let saveDialog = () => {
  let checkedList = [...checkedUsersList.value].map((item) => ({
    type: 1,
    targetId: item.userId,
    name: item.userName,
  }));
  handleClose();
  emits("change", checkedList);
};

getList();
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
</script>
<style lang="css" scoped>
.tip {
  padding: 8px 16px;
  background-color: rgb(197.7, 225.9, 255);
  border-left: 5px solid #409eff;
}
</style>
