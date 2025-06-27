import { defineComponent, computed, openBlock, createElementBlock, normalizeClass, unref, renderSlot, createVNode, Transition, withCtx, withDirectives, createElementVNode, normalizeStyle, toDisplayString, vShow } from 'vue';
import '../../../hooks/index.mjs';
import '../../../utils/index.mjs';
import { badgeProps } from './badge.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { isNumber } from '../../../utils/types.mjs';
import { addUnit } from '../../../utils/dom/style.mjs';
import { useDeprecated } from '../../../hooks/use-deprecated/index.mjs';

const _hoisted_1 = ["textContent"];
const __default__ = defineComponent({
  name: "ElBadge"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: badgeProps,
  setup(__props, { expose }) {
    const props = __props;
    const ns = useNamespace("badge");
    const content = computed(() => {
      if (props.isDot)
        return "";
      if (isNumber(props.value) && isNumber(props.max)) {
        if (props.max < props.value) {
          return `${props.max}+`;
        }
        return props.value === 0 && !props.showZero ? "" : `${props.value}`;
      }
      return `${props.value}`;
    });
    const style = computed(() => {
      var _a, _b, _c, _d, _e, _f;
      return [
        {
          backgroundColor: props.color,
          marginRight: addUnit(-((_b = (_a = props.offset) == null ? void 0 : _a[0]) != null ? _b : 0)),
          marginTop: addUnit((_d = (_c = props.offset) == null ? void 0 : _c[1]) != null ? _d : 0)
        },
        (_e = props.dotStyle) != null ? _e : {},
        (_f = props.badgeStyle) != null ? _f : {}
      ];
    });
    useDeprecated({
      from: "dot-style",
      replacement: "badge-style",
      version: "2.8.0",
      scope: "el-badge",
      ref: "https://element-plus.org/en-US/component/badge.html"
    }, computed(() => !!props.dotStyle));
    useDeprecated({
      from: "dot-class",
      replacement: "badge-class",
      version: "2.8.0",
      scope: "el-badge",
      ref: "https://element-plus.org/en-US/component/badge.html"
    }, computed(() => !!props.dotClass));
    expose({
      content
    });
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock("div", {
        class: normalizeClass(unref(ns).b())
      }, [
        renderSlot(_ctx.$slots, "default"),
        createVNode(Transition, {
          name: `${unref(ns).namespace.value}-zoom-in-center`,
          persisted: ""
        }, {
          default: withCtx(() => [
            withDirectives(createElementVNode("sup", {
              class: normalizeClass([
                unref(ns).e("content"),
                unref(ns).em("content", _ctx.type),
                unref(ns).is("fixed", !!_ctx.$slots.default),
                unref(ns).is("dot", _ctx.isDot),
                _ctx.dotClass,
                _ctx.badgeClass
              ]),
              style: normalizeStyle(unref(style)),
              textContent: toDisplayString(unref(content))
            }, null, 14, _hoisted_1), [
              [vShow, !_ctx.hidden && (unref(content) || _ctx.isDot)]
            ])
          ]),
          _: 1
        }, 8, ["name"])
      ], 2);
    };
  }
});
var Badge = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "badge.vue"]]);

export { Badge as default };
//# sourceMappingURL=badge2.mjs.map
