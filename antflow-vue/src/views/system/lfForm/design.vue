<template>
  <div class="lf-form-design-container">
    <div class="fd-nav">
      <div class="fd-nav-left">
        <div class="fd-nav-title">
          <el-icon><HomeFilled /></el-icon>
          {{ title }}
        </div>
      </div>
      <div class="fd-nav-center">
        <el-form :inline="true" class="form-name-form">
          <el-form-item label="表单名称">
            <el-input
              v-model="formName"
              placeholder="请输入表单名称"
              style="width: 220px"
              :disabled="readonly"
            />
          </el-form-item>
        </el-form>
      </div>
      <div class="fd-nav-right">
        <button type="button" class="fd-btn button-publish" @click="goBack">
          <span>返回</span>
        </button>
        <button
          v-if="!readonly"
          type="button"
          class="fd-btn button-publish"
          @click="handleSave"
        >
          <span>保 存</span>
        </button>
      </div>
    </div>
    <div class="my-nav-content">
      <div id="form-designer-container" class="lf-form-container">
        <v-form-designer ref="formDesign"></v-form-designer>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getFormById, saveForm } from "@/api/workflow/lowcodeApi";

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();

const formDesign = ref(null);
const formName = ref("");
const readonly = ref(false);
const formCode = ref(null);
const existingId = ref(null);
const title = ref("新建表单");

onMounted(async () => {
  readonly.value = route.query.readonly === "1";
  formCode.value = route.query.formCode || null;
  existingId.value = route.query.id || null;

  if (route.query.formName) {
    formName.value = decodeURIComponent(route.query.formName);
  }

  if (formCode.value) {
    title.value = readonly.value ? "查看表单版本" : "编辑表单(新版本)";
  }

  if (existingId.value) {
    proxy.$modal.loading();
    try {
      const res = await getFormById(existingId.value);
      if (res.code === 200 && res.data) {
        const formDataStr = res.data.formdata;
        if (formDataStr) {
          formDesign.value.clearDesigner();
          formDesign.value.designer.loadFormJson(JSON.parse(formDataStr));
        }
        if (!formName.value && res.data.formName) {
          formName.value = res.data.formName;
        }
      }
    } catch (e) {
      proxy.$modal.msgError("加载表单数据失败");
    } finally {
      proxy.$modal.closeLoading();
    }
  }
});

const goBack = () => {
  router.push({ path: "/system/lfForm" });
};

const handleSave = async () => {
  if (!formName.value || !formName.value.trim()) {
    proxy.$modal.msgError("请输入表单名称");
    return;
  }
  let formJson;
  try {
    formJson = formDesign.value.getFormJson();
  } catch (e) {
    proxy.$modal.msgError("获取表单数据失败");
    return;
  }
  proxy.$modal.loading();
  try {
    const data = {
      formCode: formCode.value,
      formName: formName.value.trim(),
      formdata: JSON.stringify(formJson),
    };
    const res = await saveForm(data);
    if (res.code === 200) {
      proxy.$modal.msgSuccess("保存成功");
      router.push({ path: "/system/lfForm" });
    } else {
      proxy.$modal.msgError(res.msg || "保存失败");
    }
  } catch (e) {
    proxy.$modal.msgError(e?.data?.msg || "保存失败");
  } finally {
    proxy.$modal.closeLoading();
  }
};
</script>

<style lang="scss" scoped>
@use "@/assets/styles/antflow/workflow.scss";

.lf-form-design-container {
  position: relative;
  background-color: #f5f5f7;
  min-height: calc(100vh - 85px);
  padding-top: 15px;
  overflow: auto;
}

.form-name-form {
  margin-bottom: 0;
}

.form-name-form :deep(.el-form-item__label) {
  color: #fff;
}

.my-nav-content {
  text-align: center;
}

.lf-form-container {
  background: white !important;
  padding: 0px;
  width: 95%;
  left: 0;
  bottom: 0;
  right: 0;
  margin: auto;
}
</style>
