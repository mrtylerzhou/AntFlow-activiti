'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var core = require('@vueuse/core');
require('../../../hooks/index.js');
require('../../form/index.js');
require('../../../utils/index.js');
require('../../../constants/index.js');
var segmented = require('./segmented.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var index$1 = require('../../../hooks/use-id/index.js');
var useFormCommonProps = require('../../form/src/hooks/use-form-common-props.js');
var useFormItem = require('../../form/src/hooks/use-form-item.js');
var event = require('../../../constants/event.js');
var shared = require('@vue/shared');
var error = require('../../../utils/error.js');

const _hoisted_1 = ["id", "aria-label", "aria-labelledby"];
const _hoisted_2 = ["name", "disabled", "checked", "onChange"];
const __default__ = vue.defineComponent({
  name: "ElSegmented"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: segmented.segmentedProps,
  emits: segmented.segmentedEmits,
  setup(__props, { emit }) {
    const props = __props;
    const ns = index.useNamespace("segmented");
    const segmentedId = index$1.useId();
    const segmentedSize = useFormCommonProps.useFormSize();
    const _disabled = useFormCommonProps.useFormDisabled();
    const { formItem } = useFormItem.useFormItem();
    const { inputId, isLabeledByFormItem } = useFormItem.useFormItemInputId(props, {
      formItemContext: formItem
    });
    const segmentedRef = vue.ref(null);
    const activeElement = core.useActiveElement();
    const state = vue.reactive({
      isInit: false,
      width: 0,
      translateX: 0,
      disabled: false,
      focusVisible: false
    });
    const handleChange = (item) => {
      const value = getValue(item);
      emit(event.UPDATE_MODEL_EVENT, value);
      emit(event.CHANGE_EVENT, value);
    };
    const getValue = (item) => {
      return shared.isObject(item) ? item.value : item;
    };
    const getLabel = (item) => {
      return shared.isObject(item) ? item.label : item;
    };
    const getDisabled = (item) => {
      return !!(_disabled.value || (shared.isObject(item) ? item.disabled : false));
    };
    const getSelected = (item) => {
      return props.modelValue === getValue(item);
    };
    const getOption = (value) => {
      return props.options.find((item) => getValue(item) === value);
    };
    const getItemCls = (item) => {
      return [
        ns.e("item"),
        ns.is("selected", getSelected(item)),
        ns.is("disabled", getDisabled(item))
      ];
    };
    const updateSelect = () => {
      if (!segmentedRef.value)
        return;
      const selectedItem = segmentedRef.value.querySelector(".is-selected");
      const selectedItemInput = segmentedRef.value.querySelector(".is-selected input");
      if (!selectedItem || !selectedItemInput) {
        state.width = 0;
        state.translateX = 0;
        state.disabled = false;
        state.focusVisible = false;
        return;
      }
      const rect = selectedItem.getBoundingClientRect();
      state.isInit = true;
      state.width = rect.width;
      state.translateX = selectedItem.offsetLeft;
      state.disabled = getDisabled(getOption(props.modelValue));
      try {
        state.focusVisible = selectedItemInput.matches(":focus-visible");
      } catch (e) {
      }
    };
    const segmentedCls = vue.computed(() => [
      ns.b(),
      ns.m(segmentedSize.value),
      ns.is("block", props.block)
    ]);
    const selectedStyle = vue.computed(() => ({
      width: `${state.width}px`,
      transform: `translateX(${state.translateX}px)`,
      display: state.isInit ? "block" : "none"
    }));
    const selectedCls = vue.computed(() => [
      ns.e("item-selected"),
      ns.is("disabled", state.disabled),
      ns.is("focus-visible", state.focusVisible)
    ]);
    const name = vue.computed(() => {
      return props.name || segmentedId.value;
    });
    core.useResizeObserver(segmentedRef, updateSelect);
    vue.watch(activeElement, updateSelect);
    vue.watch(() => props.modelValue, () => {
      var _a;
      updateSelect();
      if (props.validateEvent) {
        (_a = formItem == null ? void 0 : formItem.validate) == null ? void 0 : _a.call(formItem, "change").catch((err) => error.debugWarn(err));
      }
    }, {
      flush: "post"
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        id: vue.unref(inputId),
        ref_key: "segmentedRef",
        ref: segmentedRef,
        class: vue.normalizeClass(vue.unref(segmentedCls)),
        role: "radiogroup",
        "aria-label": !vue.unref(isLabeledByFormItem) ? _ctx.ariaLabel || "segmented" : void 0,
        "aria-labelledby": vue.unref(isLabeledByFormItem) ? vue.unref(formItem).labelId : void 0
      }, [
        vue.createElementVNode("div", {
          class: vue.normalizeClass(vue.unref(ns).e("group"))
        }, [
          vue.createElementVNode("div", {
            style: vue.normalizeStyle(vue.unref(selectedStyle)),
            class: vue.normalizeClass(vue.unref(selectedCls))
          }, null, 6),
          (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(_ctx.options, (item, index) => {
            return vue.openBlock(), vue.createElementBlock("label", {
              key: index,
              class: vue.normalizeClass(getItemCls(item))
            }, [
              vue.createElementVNode("input", {
                class: vue.normalizeClass(vue.unref(ns).e("item-input")),
                type: "radio",
                name: vue.unref(name),
                disabled: getDisabled(item),
                checked: getSelected(item),
                onChange: ($event) => handleChange(item)
              }, null, 42, _hoisted_2),
              vue.createElementVNode("div", {
                class: vue.normalizeClass(vue.unref(ns).e("item-label"))
              }, [
                vue.renderSlot(_ctx.$slots, "default", { item }, () => [
                  vue.createTextVNode(vue.toDisplayString(getLabel(item)), 1)
                ])
              ], 2)
            ], 2);
          }), 128))
        ], 2)
      ], 10, _hoisted_1);
    };
  }
});
var Segmented = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "segmented.vue"]]);

exports["default"] = Segmented;
//# sourceMappingURL=segmented2.js.map
