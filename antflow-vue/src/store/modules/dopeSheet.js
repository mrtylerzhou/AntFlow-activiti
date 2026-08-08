import { defineStore } from 'pinia';

/**
 * Dope Sheet 数据共享 Store
 * 用于 Dope Sheet、完整编辑(设计器)、完整预览 三者之间共享流程配置数据
 */
export const useDopeSheetStore = defineStore('dopeSheet', {
  state: () => ({
    // 是否处于 Dope Sheet 模式（设计器/预览从 store 读数据而非请求后端）
    active: false,
    // 完整编辑模式：设计器"发布"替换为"返回"
    fullEditMode: false,
    // 流程类型标识
    formCode: '',
    // 流程类型名称
    formCodeName: '',
    // 流程分类: LF / DIY
    flowType: '',
    // 当前选中的版本 id
    currentVersionId: null,
    // 完整的 processConfig 数据（FormatDisplayUtils.getToTree 后的结构）
    processConfig: null,
    // 标记是否有未保存修改
    dirty: false,
    // Zen 模式工作副本（进入时深拷贝 processConfig，返回时覆盖回 processConfig）
    zenConfig: null,
  }),
  actions: {
    /**
     * 初始化 Dope Sheet 模式
     */
    init({ formCode, formCodeName, flowType }) {
      this.active = true;
      this.fullEditMode = false;
      this.formCode = formCode;
      this.formCodeName = formCodeName || '';
      this.flowType = flowType;
      this.currentVersionId = null;
      this.processConfig = null;
      this.dirty = false;
    },
    /**
     * 设置流程配置数据
     */
    setProcessConfig(data) {
      this.processConfig = data;
    },
    /**
     * 设置当前版本 id
     */
    setCurrentVersionId(id) {
      this.currentVersionId = id;
    },
    /**
     * 标记数据已修改
     */
    markDirty() {
      this.dirty = true;
    },
    /**
     * 重置 dirty 标记
     */
    resetDirty() {
      this.dirty = false;
    },
    /**
     * 进入完整编辑模式
     */
    enterFullEdit() {
      this.fullEditMode = true;
    },
    /**
     * 退出完整编辑模式（返回 Dope Sheet）
     */
    exitFullEdit() {
      this.fullEditMode = false;
    },
    /**
     * 进入 Zen 模式：深拷贝当前 processConfig 作为工作副本
     */
    enterZen() {
      this.zenConfig = JSON.parse(JSON.stringify(this.processConfig));
    },
    /**
     * 退出 Zen 模式（返回 Dope Sheet）
     */
    exitZen() {
      this.zenConfig = null;
    },
    /**
     * Zen 模式返回：校验通过后，用 Zen 工作副本覆盖 processConfig 并标记 dirty
     */
    commitZen() {
      if (this.zenConfig) {
        this.processConfig = this.zenConfig;
        this.dirty = true;
      }
      this.zenConfig = null;
    },
    /**
     * 重置整个 store
     */
    reset() {
      this.active = false;
      this.fullEditMode = false;
      this.formCode = '';
      this.formCodeName = '';
      this.flowType = '';
      this.currentVersionId = null;
      this.processConfig = null;
      this.dirty = false;
      this.zenConfig = null;
    },
  },
});
