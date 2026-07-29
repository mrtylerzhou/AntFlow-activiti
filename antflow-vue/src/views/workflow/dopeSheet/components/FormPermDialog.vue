<template>
  <el-dialog title="表单权限设置" v-model="dialogVisible" width="700px" append-to-body destroy-on-close>
    <form-perm-conf v-if="dialogVisible" default-perm="R" v-model:formItems="formItems"
      :formHidden="formHiddenMap" @changePermVal="changePermVal" @changeFormHidden="changeFormHidden" />
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import formPermConf from '@/components/Workflow/drawer/permConfig/FormPermConf.vue';

const props = defineProps({
  visible: Boolean,
  node: Object
});

const emit = defineEmits(['update:visible', 'confirm']);

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

const formItems = ref([]);
const formHiddenMap = ref({});
const currentPermData = ref([]);

watch(() => props.visible, (val) => {
  if (val && props.node) {
    formItems.value = props.node.lfFieldControlVOs || [];
    formHiddenMap.value = props.node.formHidden || {};
    currentPermData.value = props.node.lfFieldControlVOs || [];
  }
});

const changePermVal = (data) => {
  currentPermData.value = data;
};

const changeFormHidden = (data) => {
  if (props.node) {
    props.node.formHidden = data;
  }
};

const handleConfirm = () => {
  emit('confirm', currentPermData.value);
  dialogVisible.value = false;
};
</script>
