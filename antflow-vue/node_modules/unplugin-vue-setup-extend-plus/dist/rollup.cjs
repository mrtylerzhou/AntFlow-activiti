"use strict";
var __defProp = Object.defineProperty;
var __getOwnPropDesc = Object.getOwnPropertyDescriptor;
var __getOwnPropNames = Object.getOwnPropertyNames;
var __hasOwnProp = Object.prototype.hasOwnProperty;
var __export = (target, all) => {
  for (var name in all)
    __defProp(target, name, { get: all[name], enumerable: true });
};
var __copyProps = (to, from, except, desc) => {
  if (from && typeof from === "object" || typeof from === "function") {
    for (let key of __getOwnPropNames(from))
      if (!__hasOwnProp.call(to, key) && key !== except)
        __defProp(to, key, { get: () => from[key], enumerable: !(desc = __getOwnPropDesc(from, key)) || desc.enumerable });
  }
  return to;
};
var __toCommonJS = (mod) => __copyProps(__defProp({}, "__esModule", { value: true }), mod);

// src/rollup.ts
var rollup_exports = {};
__export(rollup_exports, {
  default: () => rollup_default
});
module.exports = __toCommonJS(rollup_exports);

// src/core/unplugin.ts
var import_unplugin = require("unplugin");

// src/core/transform.ts
var import_path = require("path");
var import_compiler_sfc = require("@vue/compiler-sfc");
var supportScriptName = (_SFCParseResult, magicString, options) => {
  var _a;
  const { mode } = options.options;
  const { descriptor } = _SFCParseResult;
  if (!descriptor.script && descriptor.scriptSetup && !((_a = descriptor.scriptSetup.attrs) == null ? void 0 : _a.extendIgnore)) {
    const result = (0, import_compiler_sfc.compileScript)(descriptor, { id: options.id });
    const name = typeof result.attrs.name === "string" ? result.attrs.name : nameProcess(options.id, mode);
    const lang = result.attrs.lang;
    const inheritAttrs = result.attrs.inheritAttrs;
    if (name || inheritAttrs) {
      magicString.appendLeft(
        0,
        `<script${lang ? ` lang="${lang}"` : ""}>
import { defineComponent } from 'vue'
export default defineComponent({
  ${name ? `name: "${name}",` : ""}
  ${inheritAttrs ? `inheritAttrs: ${inheritAttrs !== "false"},` : ""}
})
<\/script>
`
      );
    }
  }
};
function nameProcess(id, mode) {
  const commonId = id.replace(/\\/g, "/").split("?")[0];
  if (typeof mode === "string") {
    const parseUrl = (0, import_path.parse)(commonId);
    const fileName = parseUrl.name;
    const relativeName = parseUrl.dir.split("/").at(-1);
    if (mode === "relativeName")
      return camelize(`${relativeName}-${fileName}`);
  }
  if (typeof mode === "function")
    return mode(commonId);
  return "";
}
function camelize(str) {
  return str.replace(/-(\w)/g, (_, c) => c ? c.toUpperCase() : "").replace(/(\w)/, (_, c) => c.toUpperCase());
}

// src/core/compose.ts
var import_path2 = require("path");
var import_compiler_sfc2 = require("@vue/compiler-sfc");
var compose = (pluginsOption, plugins) => {
  const magicString = new import_compiler_sfc2.MagicString(pluginsOption.code);
  const _SFCParseResult = (0, import_compiler_sfc2.parse)(pluginsOption.code);
  const registerCompile = {
    templates: []
  };
  plugins.forEach(
    (plugin) => plugin(_SFCParseResult, magicString, pluginsOption, registerCompile)
  );
  if (registerCompile.templates.length && _SFCParseResult.descriptor) {
    const { descriptor } = _SFCParseResult;
    if (descriptor.template) {
      (0, import_compiler_sfc2.compileTemplate)({
        filename: pluginsOption.id,
        source: descriptor.template.content,
        id: pluginsOption.id,
        compilerOptions: {
          nodeTransforms: registerCompile.templates
        }
      });
    }
  }
  const map = magicString.generateMap({ hires: true });
  const filename = (0, import_path2.basename)(pluginsOption.id);
  map.file = filename;
  map.sources = [filename];
  return {
    map,
    code: magicString.toString()
  };
};

// node_modules/.pnpm/@vue+shared@3.2.37/node_modules/@vue/shared/dist/shared.esm-bundler.js
function makeMap(str, expectsLowerCase) {
  const map = /* @__PURE__ */ Object.create(null);
  const list = str.split(",");
  for (let i = 0; i < list.length; i++) {
    map[list[i]] = true;
  }
  return expectsLowerCase ? (val) => !!map[val.toLowerCase()] : (val) => !!map[val];
}
var PatchFlagNames = {
  [1]: `TEXT`,
  [2]: `CLASS`,
  [4]: `STYLE`,
  [8]: `PROPS`,
  [16]: `FULL_PROPS`,
  [32]: `HYDRATE_EVENTS`,
  [64]: `STABLE_FRAGMENT`,
  [128]: `KEYED_FRAGMENT`,
  [256]: `UNKEYED_FRAGMENT`,
  [512]: `NEED_PATCH`,
  [1024]: `DYNAMIC_SLOTS`,
  [2048]: `DEV_ROOT_FRAGMENT`,
  [-1]: `HOISTED`,
  [-2]: `BAIL`
};
var specialBooleanAttrs = `itemscope,allowfullscreen,formnovalidate,ismap,nomodule,novalidate,readonly`;
var isBooleanAttr = /* @__PURE__ */ makeMap(specialBooleanAttrs + `,async,autofocus,autoplay,controls,default,defer,disabled,hidden,loop,open,required,reversed,scoped,seamless,checked,muted,multiple,selected`);
var EMPTY_OBJ = process.env.NODE_ENV !== "production" ? Object.freeze({}) : {};
var EMPTY_ARR = process.env.NODE_ENV !== "production" ? Object.freeze([]) : [];
var NO = () => false;
var extend = Object.assign;
var isArray = Array.isArray;
var isString = (val) => typeof val === "string";
var cacheStringFunction = (fn) => {
  const cache = /* @__PURE__ */ Object.create(null);
  return (str) => {
    const hit = cache[str];
    return hit || (cache[str] = fn(str));
  };
};
var camelizeRE = /-(\w)/g;
var camelize2 = cacheStringFunction((str) => {
  return str.replace(camelizeRE, (_, c) => c ? c.toUpperCase() : "");
});
var hyphenateRE = /\B([A-Z])/g;
var hyphenate = cacheStringFunction((str) => str.replace(hyphenateRE, "-$1").toLowerCase());
var capitalize = cacheStringFunction((str) => str.charAt(0).toUpperCase() + str.slice(1));
var toHandlerKey = cacheStringFunction((str) => str ? `on${capitalize(str)}` : ``);

// node_modules/.pnpm/@vue+compiler-core@3.2.37/node_modules/@vue/compiler-core/dist/compiler-core.esm-bundler.js
function defaultOnError(error) {
  throw error;
}
function defaultOnWarn(msg) {
  process.env.NODE_ENV !== "production" && console.warn(`[Vue warn] ${msg.message}`);
}
function createCompilerError(code, loc, messages, additionalMessage) {
  const msg = process.env.NODE_ENV !== "production" || false ? (messages || errorMessages)[code] + (additionalMessage || ``) : code;
  const error = new SyntaxError(String(msg));
  error.code = code;
  error.loc = loc;
  return error;
}
var errorMessages = {
  [0]: "Illegal comment.",
  [1]: "CDATA section is allowed only in XML context.",
  [2]: "Duplicate attribute.",
  [3]: "End tag cannot have attributes.",
  [4]: "Illegal '/' in tags.",
  [5]: "Unexpected EOF in tag.",
  [6]: "Unexpected EOF in CDATA section.",
  [7]: "Unexpected EOF in comment.",
  [8]: "Unexpected EOF in script.",
  [9]: "Unexpected EOF in tag.",
  [10]: "Incorrectly closed comment.",
  [11]: "Incorrectly opened comment.",
  [12]: "Illegal tag name. Use '&lt;' to print '<'.",
  [13]: "Attribute value was expected.",
  [14]: "End tag name was expected.",
  [15]: "Whitespace was expected.",
  [16]: "Unexpected '<!--' in comment.",
  [17]: `Attribute name cannot contain U+0022 ("), U+0027 ('), and U+003C (<).`,
  [18]: "Unquoted attribute value cannot contain U+0022 (\"), U+0027 ('), U+003C (<), U+003D (=), and U+0060 (`).",
  [19]: "Attribute name cannot start with '='.",
  [21]: "'<?' is allowed only in XML context.",
  [20]: `Unexpected null character.`,
  [22]: "Illegal '/' in tags.",
  [23]: "Invalid end tag.",
  [24]: "Element is missing end tag.",
  [25]: "Interpolation end sign was not found.",
  [27]: "End bracket for dynamic directive argument was not found. Note that dynamic directive argument cannot contain spaces.",
  [26]: "Legal directive name was expected.",
  [28]: `v-if/v-else-if is missing expression.`,
  [29]: `v-if/else branches must use unique keys.`,
  [30]: `v-else/v-else-if has no adjacent v-if or v-else-if.`,
  [31]: `v-for is missing expression.`,
  [32]: `v-for has invalid expression.`,
  [33]: `<template v-for> key should be placed on the <template> tag.`,
  [34]: `v-bind is missing expression.`,
  [35]: `v-on is missing expression.`,
  [36]: `Unexpected custom directive on <slot> outlet.`,
  [37]: `Mixed v-slot usage on both the component and nested <template>.When there are multiple named slots, all slots should use <template> syntax to avoid scope ambiguity.`,
  [38]: `Duplicate slot names found. `,
  [39]: `Extraneous children found when component already has explicitly named default slot. These children will be ignored.`,
  [40]: `v-slot can only be used on components or <template> tags.`,
  [41]: `v-model is missing expression.`,
  [42]: `v-model value must be a valid JavaScript member expression.`,
  [43]: `v-model cannot be used on v-for or v-slot scope variables because they are not writable.`,
  [44]: `Error parsing JavaScript expression: `,
  [45]: `<KeepAlive> expects exactly one child component.`,
  [46]: `"prefixIdentifiers" option is not supported in this build of compiler.`,
  [47]: `ES module mode is not supported in this build of compiler.`,
  [48]: `"cacheHandlers" option is only supported when the "prefixIdentifiers" option is enabled.`,
  [49]: `"scopeId" option is only supported in module mode.`,
  [50]: ``
};
var FRAGMENT = Symbol(process.env.NODE_ENV !== "production" ? `Fragment` : ``);
var TELEPORT = Symbol(process.env.NODE_ENV !== "production" ? `Teleport` : ``);
var SUSPENSE = Symbol(process.env.NODE_ENV !== "production" ? `Suspense` : ``);
var KEEP_ALIVE = Symbol(process.env.NODE_ENV !== "production" ? `KeepAlive` : ``);
var BASE_TRANSITION = Symbol(process.env.NODE_ENV !== "production" ? `BaseTransition` : ``);
var OPEN_BLOCK = Symbol(process.env.NODE_ENV !== "production" ? `openBlock` : ``);
var CREATE_BLOCK = Symbol(process.env.NODE_ENV !== "production" ? `createBlock` : ``);
var CREATE_ELEMENT_BLOCK = Symbol(process.env.NODE_ENV !== "production" ? `createElementBlock` : ``);
var CREATE_VNODE = Symbol(process.env.NODE_ENV !== "production" ? `createVNode` : ``);
var CREATE_ELEMENT_VNODE = Symbol(process.env.NODE_ENV !== "production" ? `createElementVNode` : ``);
var CREATE_COMMENT = Symbol(process.env.NODE_ENV !== "production" ? `createCommentVNode` : ``);
var CREATE_TEXT = Symbol(process.env.NODE_ENV !== "production" ? `createTextVNode` : ``);
var CREATE_STATIC = Symbol(process.env.NODE_ENV !== "production" ? `createStaticVNode` : ``);
var RESOLVE_COMPONENT = Symbol(process.env.NODE_ENV !== "production" ? `resolveComponent` : ``);
var RESOLVE_DYNAMIC_COMPONENT = Symbol(process.env.NODE_ENV !== "production" ? `resolveDynamicComponent` : ``);
var RESOLVE_DIRECTIVE = Symbol(process.env.NODE_ENV !== "production" ? `resolveDirective` : ``);
var RESOLVE_FILTER = Symbol(process.env.NODE_ENV !== "production" ? `resolveFilter` : ``);
var WITH_DIRECTIVES = Symbol(process.env.NODE_ENV !== "production" ? `withDirectives` : ``);
var RENDER_LIST = Symbol(process.env.NODE_ENV !== "production" ? `renderList` : ``);
var RENDER_SLOT = Symbol(process.env.NODE_ENV !== "production" ? `renderSlot` : ``);
var CREATE_SLOTS = Symbol(process.env.NODE_ENV !== "production" ? `createSlots` : ``);
var TO_DISPLAY_STRING = Symbol(process.env.NODE_ENV !== "production" ? `toDisplayString` : ``);
var MERGE_PROPS = Symbol(process.env.NODE_ENV !== "production" ? `mergeProps` : ``);
var NORMALIZE_CLASS = Symbol(process.env.NODE_ENV !== "production" ? `normalizeClass` : ``);
var NORMALIZE_STYLE = Symbol(process.env.NODE_ENV !== "production" ? `normalizeStyle` : ``);
var NORMALIZE_PROPS = Symbol(process.env.NODE_ENV !== "production" ? `normalizeProps` : ``);
var GUARD_REACTIVE_PROPS = Symbol(process.env.NODE_ENV !== "production" ? `guardReactiveProps` : ``);
var TO_HANDLERS = Symbol(process.env.NODE_ENV !== "production" ? `toHandlers` : ``);
var CAMELIZE = Symbol(process.env.NODE_ENV !== "production" ? `camelize` : ``);
var CAPITALIZE = Symbol(process.env.NODE_ENV !== "production" ? `capitalize` : ``);
var TO_HANDLER_KEY = Symbol(process.env.NODE_ENV !== "production" ? `toHandlerKey` : ``);
var SET_BLOCK_TRACKING = Symbol(process.env.NODE_ENV !== "production" ? `setBlockTracking` : ``);
var PUSH_SCOPE_ID = Symbol(process.env.NODE_ENV !== "production" ? `pushScopeId` : ``);
var POP_SCOPE_ID = Symbol(process.env.NODE_ENV !== "production" ? `popScopeId` : ``);
var WITH_CTX = Symbol(process.env.NODE_ENV !== "production" ? `withCtx` : ``);
var UNREF = Symbol(process.env.NODE_ENV !== "production" ? `unref` : ``);
var IS_REF = Symbol(process.env.NODE_ENV !== "production" ? `isRef` : ``);
var WITH_MEMO = Symbol(process.env.NODE_ENV !== "production" ? `withMemo` : ``);
var IS_MEMO_SAME = Symbol(process.env.NODE_ENV !== "production" ? `isMemoSame` : ``);
var helperNameMap = {
  [FRAGMENT]: `Fragment`,
  [TELEPORT]: `Teleport`,
  [SUSPENSE]: `Suspense`,
  [KEEP_ALIVE]: `KeepAlive`,
  [BASE_TRANSITION]: `BaseTransition`,
  [OPEN_BLOCK]: `openBlock`,
  [CREATE_BLOCK]: `createBlock`,
  [CREATE_ELEMENT_BLOCK]: `createElementBlock`,
  [CREATE_VNODE]: `createVNode`,
  [CREATE_ELEMENT_VNODE]: `createElementVNode`,
  [CREATE_COMMENT]: `createCommentVNode`,
  [CREATE_TEXT]: `createTextVNode`,
  [CREATE_STATIC]: `createStaticVNode`,
  [RESOLVE_COMPONENT]: `resolveComponent`,
  [RESOLVE_DYNAMIC_COMPONENT]: `resolveDynamicComponent`,
  [RESOLVE_DIRECTIVE]: `resolveDirective`,
  [RESOLVE_FILTER]: `resolveFilter`,
  [WITH_DIRECTIVES]: `withDirectives`,
  [RENDER_LIST]: `renderList`,
  [RENDER_SLOT]: `renderSlot`,
  [CREATE_SLOTS]: `createSlots`,
  [TO_DISPLAY_STRING]: `toDisplayString`,
  [MERGE_PROPS]: `mergeProps`,
  [NORMALIZE_CLASS]: `normalizeClass`,
  [NORMALIZE_STYLE]: `normalizeStyle`,
  [NORMALIZE_PROPS]: `normalizeProps`,
  [GUARD_REACTIVE_PROPS]: `guardReactiveProps`,
  [TO_HANDLERS]: `toHandlers`,
  [CAMELIZE]: `camelize`,
  [CAPITALIZE]: `capitalize`,
  [TO_HANDLER_KEY]: `toHandlerKey`,
  [SET_BLOCK_TRACKING]: `setBlockTracking`,
  [PUSH_SCOPE_ID]: `pushScopeId`,
  [POP_SCOPE_ID]: `popScopeId`,
  [WITH_CTX]: `withCtx`,
  [UNREF]: `unref`,
  [IS_REF]: `isRef`,
  [WITH_MEMO]: `withMemo`,
  [IS_MEMO_SAME]: `isMemoSame`
};
var locStub = {
  source: "",
  start: { line: 1, column: 1, offset: 0 },
  end: { line: 1, column: 1, offset: 0 }
};
function createVNodeCall(context, tag, props, children, patchFlag, dynamicProps, directives, isBlock = false, disableTracking = false, isComponent = false, loc = locStub) {
  if (context) {
    if (isBlock) {
      context.helper(OPEN_BLOCK);
      context.helper(getVNodeBlockHelper(context.inSSR, isComponent));
    } else {
      context.helper(getVNodeHelper(context.inSSR, isComponent));
    }
    if (directives) {
      context.helper(WITH_DIRECTIVES);
    }
  }
  return {
    type: 13,
    tag,
    props,
    children,
    patchFlag,
    dynamicProps,
    directives,
    isBlock,
    disableTracking,
    isComponent,
    loc
  };
}
function createObjectExpression(properties, loc = locStub) {
  return {
    type: 15,
    loc,
    properties
  };
}
function createObjectProperty(key, value) {
  return {
    type: 16,
    loc: locStub,
    key: isString(key) ? createSimpleExpression(key, true) : key,
    value
  };
}
function createSimpleExpression(content, isStatic = false, loc = locStub, constType = 0) {
  return {
    type: 4,
    loc,
    content,
    isStatic,
    constType: isStatic ? 3 : constType
  };
}
function createCompoundExpression(children, loc = locStub) {
  return {
    type: 8,
    loc,
    children
  };
}
function createCallExpression(callee, args = [], loc = locStub) {
  return {
    type: 14,
    loc,
    callee,
    arguments: args
  };
}
function createFunctionExpression(params, returns = void 0, newline = false, isSlot = false, loc = locStub) {
  return {
    type: 18,
    params,
    returns,
    newline,
    isSlot,
    loc
  };
}
function createConditionalExpression(test, consequent, alternate, newline = true) {
  return {
    type: 19,
    test,
    consequent,
    alternate,
    newline,
    loc: locStub
  };
}
function createBlockStatement(body) {
  return {
    type: 21,
    body,
    loc: locStub
  };
}
var isStaticExp = (p) => p.type === 4 && p.isStatic;
var isBuiltInType = (tag, expected) => tag === expected || tag === hyphenate(expected);
function getInnerRange(loc, offset, length) {
  const source = loc.source.slice(offset, offset + length);
  const newLoc = {
    source,
    start: advancePositionWithClone(loc.start, loc.source, offset),
    end: loc.end
  };
  if (length != null) {
    newLoc.end = advancePositionWithClone(loc.start, loc.source, offset + length);
  }
  return newLoc;
}
function advancePositionWithClone(pos, source, numberOfCharacters = source.length) {
  return advancePositionWithMutation(extend({}, pos), source, numberOfCharacters);
}
function advancePositionWithMutation(pos, source, numberOfCharacters = source.length) {
  let linesCount = 0;
  let lastNewLinePos = -1;
  for (let i = 0; i < numberOfCharacters; i++) {
    if (source.charCodeAt(i) === 10) {
      linesCount++;
      lastNewLinePos = i;
    }
  }
  pos.offset += numberOfCharacters;
  pos.line += linesCount;
  pos.column = lastNewLinePos === -1 ? pos.column + numberOfCharacters : numberOfCharacters - lastNewLinePos;
  return pos;
}
function findDir(node, name, allowEmpty = false) {
  for (let i = 0; i < node.props.length; i++) {
    const p = node.props[i];
    if (p.type === 7 && (allowEmpty || p.exp) && (isString(name) ? p.name === name : name.test(p.name))) {
      return p;
    }
  }
}
function findProp(node, name, dynamicOnly = false, allowEmpty = false) {
  for (let i = 0; i < node.props.length; i++) {
    const p = node.props[i];
    if (p.type === 6) {
      if (dynamicOnly)
        continue;
      if (p.name === name && (p.value || allowEmpty)) {
        return p;
      }
    } else if (p.name === "bind" && (p.exp || allowEmpty) && isStaticArgOf(p.arg, name)) {
      return p;
    }
  }
}
function isStaticArgOf(arg, name) {
  return !!(arg && isStaticExp(arg) && arg.content === name);
}
function isVSlot(p) {
  return p.type === 7 && p.name === "slot";
}
function isTemplateNode(node) {
  return node.type === 1 && node.tagType === 3;
}
function isSlotOutlet(node) {
  return node.type === 1 && node.tagType === 2;
}
function getVNodeHelper(ssr, isComponent) {
  return ssr || isComponent ? CREATE_VNODE : CREATE_ELEMENT_VNODE;
}
function getVNodeBlockHelper(ssr, isComponent) {
  return ssr || isComponent ? CREATE_BLOCK : CREATE_ELEMENT_BLOCK;
}
var propsHelperSet = /* @__PURE__ */ new Set([NORMALIZE_PROPS, GUARD_REACTIVE_PROPS]);
function getUnnormalizedProps(props, callPath = []) {
  if (props && !isString(props) && props.type === 14) {
    const callee = props.callee;
    if (!isString(callee) && propsHelperSet.has(callee)) {
      return getUnnormalizedProps(props.arguments[0], callPath.concat(props));
    }
  }
  return [props, callPath];
}
function injectProp(node, prop, context) {
  let propsWithInjection;
  let props = node.type === 13 ? node.props : node.arguments[2];
  let callPath = [];
  let parentCall;
  if (props && !isString(props) && props.type === 14) {
    const ret = getUnnormalizedProps(props);
    props = ret[0];
    callPath = ret[1];
    parentCall = callPath[callPath.length - 1];
  }
  if (props == null || isString(props)) {
    propsWithInjection = createObjectExpression([prop]);
  } else if (props.type === 14) {
    const first = props.arguments[0];
    if (!isString(first) && first.type === 15) {
      first.properties.unshift(prop);
    } else {
      if (props.callee === TO_HANDLERS) {
        propsWithInjection = createCallExpression(context.helper(MERGE_PROPS), [
          createObjectExpression([prop]),
          props
        ]);
      } else {
        props.arguments.unshift(createObjectExpression([prop]));
      }
    }
    !propsWithInjection && (propsWithInjection = props);
  } else if (props.type === 15) {
    let alreadyExists = false;
    if (prop.key.type === 4) {
      const propKeyName = prop.key.content;
      alreadyExists = props.properties.some((p) => p.key.type === 4 && p.key.content === propKeyName);
    }
    if (!alreadyExists) {
      props.properties.unshift(prop);
    }
    propsWithInjection = props;
  } else {
    propsWithInjection = createCallExpression(context.helper(MERGE_PROPS), [
      createObjectExpression([prop]),
      props
    ]);
    if (parentCall && parentCall.callee === GUARD_REACTIVE_PROPS) {
      parentCall = callPath[callPath.length - 2];
    }
  }
  if (node.type === 13) {
    if (parentCall) {
      parentCall.arguments[0] = propsWithInjection;
    } else {
      node.props = propsWithInjection;
    }
  } else {
    if (parentCall) {
      parentCall.arguments[0] = propsWithInjection;
    } else {
      node.arguments[2] = propsWithInjection;
    }
  }
}
function getMemoedVNodeCall(node) {
  if (node.type === 14 && node.callee === WITH_MEMO) {
    return node.arguments[1].returns;
  } else {
    return node;
  }
}
function makeBlock(node, { helper, removeHelper, inSSR }) {
  if (!node.isBlock) {
    node.isBlock = true;
    removeHelper(getVNodeHelper(inSSR, node.isComponent));
    helper(OPEN_BLOCK);
    helper(getVNodeBlockHelper(inSSR, node.isComponent));
  }
}
var decodeRE = /&(gt|lt|amp|apos|quot);/g;
var decodeMap = {
  gt: ">",
  lt: "<",
  amp: "&",
  apos: "'",
  quot: '"'
};
var defaultParserOptions = {
  delimiters: [`{{`, `}}`],
  getNamespace: () => 0,
  getTextMode: () => 0,
  isVoidTag: NO,
  isPreTag: NO,
  isCustomElement: NO,
  decodeEntities: (rawText) => rawText.replace(decodeRE, (_, p1) => decodeMap[p1]),
  onError: defaultOnError,
  onWarn: defaultOnWarn,
  comments: process.env.NODE_ENV !== "production"
};
function traverseChildren(parent, context) {
  let i = 0;
  const nodeRemoved = () => {
    i--;
  };
  for (; i < parent.children.length; i++) {
    const child = parent.children[i];
    if (isString(child))
      continue;
    context.parent = parent;
    context.childIndex = i;
    context.onNodeRemoved = nodeRemoved;
    traverseNode(child, context);
  }
}
function traverseNode(node, context) {
  context.currentNode = node;
  const { nodeTransforms } = context;
  const exitFns = [];
  for (let i2 = 0; i2 < nodeTransforms.length; i2++) {
    const onExit = nodeTransforms[i2](node, context);
    if (onExit) {
      if (isArray(onExit)) {
        exitFns.push(...onExit);
      } else {
        exitFns.push(onExit);
      }
    }
    if (!context.currentNode) {
      return;
    } else {
      node = context.currentNode;
    }
  }
  switch (node.type) {
    case 3:
      if (!context.ssr) {
        context.helper(CREATE_COMMENT);
      }
      break;
    case 5:
      if (!context.ssr) {
        context.helper(TO_DISPLAY_STRING);
      }
      break;
    case 9:
      for (let i2 = 0; i2 < node.branches.length; i2++) {
        traverseNode(node.branches[i2], context);
      }
      break;
    case 10:
    case 11:
    case 1:
    case 0:
      traverseChildren(node, context);
      break;
  }
  context.currentNode = node;
  let i = exitFns.length;
  while (i--) {
    exitFns[i]();
  }
}
function createStructuralDirectiveTransform(name, fn) {
  const matches = isString(name) ? (n) => n === name : (n) => name.test(n);
  return (node, context) => {
    if (node.type === 1) {
      const { props } = node;
      if (node.tagType === 3 && props.some(isVSlot)) {
        return;
      }
      const exitFns = [];
      for (let i = 0; i < props.length; i++) {
        const prop = props[i];
        if (prop.type === 7 && matches(prop.name)) {
          props.splice(i, 1);
          i--;
          const onExit = fn(node, prop, context);
          if (onExit)
            exitFns.push(onExit);
        }
      }
      return exitFns;
    }
  };
}
var prohibitedKeywordRE = new RegExp("\\b" + "do,if,for,let,new,try,var,case,else,with,await,break,catch,class,const,super,throw,while,yield,delete,export,import,return,switch,default,extends,finally,continue,debugger,function,arguments,typeof,void".split(",").join("\\b|\\b") + "\\b");
var stripStringRE = /'(?:[^'\\]|\\.)*'|"(?:[^"\\]|\\.)*"|`(?:[^`\\]|\\.)*\$\{|\}(?:[^`\\]|\\.)*`|`(?:[^`\\]|\\.)*`/g;
function validateBrowserExpression(node, context, asParams = false, asRawStatements = false) {
  const exp = node.content;
  if (!exp.trim()) {
    return;
  }
  try {
    new Function(asRawStatements ? ` ${exp} ` : `return ${asParams ? `(${exp}) => {}` : `(${exp})`}`);
  } catch (e) {
    let message = e.message;
    const keywordMatch = exp.replace(stripStringRE, "").match(prohibitedKeywordRE);
    if (keywordMatch) {
      message = `avoid using JavaScript keyword as property name: "${keywordMatch[0]}"`;
    }
    context.onError(createCompilerError(44, node.loc, void 0, message));
  }
}
var transformIf = createStructuralDirectiveTransform(/^(if|else|else-if)$/, (node, dir, context) => {
  return processIf(node, dir, context, (ifNode, branch, isRoot) => {
    const siblings = context.parent.children;
    let i = siblings.indexOf(ifNode);
    let key = 0;
    while (i-- >= 0) {
      const sibling = siblings[i];
      if (sibling && sibling.type === 9) {
        key += sibling.branches.length;
      }
    }
    return () => {
      if (isRoot) {
        ifNode.codegenNode = createCodegenNodeForBranch(branch, key, context);
      } else {
        const parentCondition = getParentCondition(ifNode.codegenNode);
        parentCondition.alternate = createCodegenNodeForBranch(branch, key + ifNode.branches.length - 1, context);
      }
    };
  });
});
function processIf(node, dir, context, processCodegen) {
  if (dir.name !== "else" && (!dir.exp || !dir.exp.content.trim())) {
    const loc = dir.exp ? dir.exp.loc : node.loc;
    context.onError(createCompilerError(28, dir.loc));
    dir.exp = createSimpleExpression(`true`, false, loc);
  }
  if (process.env.NODE_ENV !== "production" && true && dir.exp) {
    validateBrowserExpression(dir.exp, context);
  }
  if (dir.name === "if") {
    const branch = createIfBranch(node, dir);
    const ifNode = {
      type: 9,
      loc: node.loc,
      branches: [branch]
    };
    context.replaceNode(ifNode);
    if (processCodegen) {
      return processCodegen(ifNode, branch, true);
    }
  } else {
    const siblings = context.parent.children;
    const comments = [];
    let i = siblings.indexOf(node);
    while (i-- >= -1) {
      const sibling = siblings[i];
      if (process.env.NODE_ENV !== "production" && sibling && sibling.type === 3) {
        context.removeNode(sibling);
        comments.unshift(sibling);
        continue;
      }
      if (sibling && sibling.type === 2 && !sibling.content.trim().length) {
        context.removeNode(sibling);
        continue;
      }
      if (sibling && sibling.type === 9) {
        if (dir.name === "else-if" && sibling.branches[sibling.branches.length - 1].condition === void 0) {
          context.onError(createCompilerError(30, node.loc));
        }
        context.removeNode();
        const branch = createIfBranch(node, dir);
        if (process.env.NODE_ENV !== "production" && comments.length && !(context.parent && context.parent.type === 1 && isBuiltInType(context.parent.tag, "transition"))) {
          branch.children = [...comments, ...branch.children];
        }
        if (process.env.NODE_ENV !== "production" || false) {
          const key = branch.userKey;
          if (key) {
            sibling.branches.forEach(({ userKey }) => {
              if (isSameKey(userKey, key)) {
                context.onError(createCompilerError(29, branch.userKey.loc));
              }
            });
          }
        }
        sibling.branches.push(branch);
        const onExit = processCodegen && processCodegen(sibling, branch, false);
        traverseNode(branch, context);
        if (onExit)
          onExit();
        context.currentNode = null;
      } else {
        context.onError(createCompilerError(30, node.loc));
      }
      break;
    }
  }
}
function createIfBranch(node, dir) {
  const isTemplateIf = node.tagType === 3;
  return {
    type: 10,
    loc: node.loc,
    condition: dir.name === "else" ? void 0 : dir.exp,
    children: isTemplateIf && !findDir(node, "for") ? node.children : [node],
    userKey: findProp(node, `key`),
    isTemplateIf
  };
}
function createCodegenNodeForBranch(branch, keyIndex, context) {
  if (branch.condition) {
    return createConditionalExpression(
      branch.condition,
      createChildrenCodegenNode(branch, keyIndex, context),
      createCallExpression(context.helper(CREATE_COMMENT), [
        process.env.NODE_ENV !== "production" ? '"v-if"' : '""',
        "true"
      ])
    );
  } else {
    return createChildrenCodegenNode(branch, keyIndex, context);
  }
}
function createChildrenCodegenNode(branch, keyIndex, context) {
  const { helper } = context;
  const keyProperty = createObjectProperty(`key`, createSimpleExpression(`${keyIndex}`, false, locStub, 2));
  const { children } = branch;
  const firstChild = children[0];
  const needFragmentWrapper = children.length !== 1 || firstChild.type !== 1;
  if (needFragmentWrapper) {
    if (children.length === 1 && firstChild.type === 11) {
      const vnodeCall = firstChild.codegenNode;
      injectProp(vnodeCall, keyProperty, context);
      return vnodeCall;
    } else {
      let patchFlag = 64;
      let patchFlagText = PatchFlagNames[64];
      if (process.env.NODE_ENV !== "production" && !branch.isTemplateIf && children.filter((c) => c.type !== 3).length === 1) {
        patchFlag |= 2048;
        patchFlagText += `, ${PatchFlagNames[2048]}`;
      }
      return createVNodeCall(context, helper(FRAGMENT), createObjectExpression([keyProperty]), children, patchFlag + (process.env.NODE_ENV !== "production" ? ` /* ${patchFlagText} */` : ``), void 0, void 0, true, false, false, branch.loc);
    }
  } else {
    const ret = firstChild.codegenNode;
    const vnodeCall = getMemoedVNodeCall(ret);
    if (vnodeCall.type === 13) {
      makeBlock(vnodeCall, context);
    }
    injectProp(vnodeCall, keyProperty, context);
    return ret;
  }
}
function isSameKey(a, b) {
  if (!a || a.type !== b.type) {
    return false;
  }
  if (a.type === 6) {
    if (a.value.content !== b.value.content) {
      return false;
    }
  } else {
    const exp = a.exp;
    const branchExp = b.exp;
    if (exp.type !== branchExp.type) {
      return false;
    }
    if (exp.type !== 4 || exp.isStatic !== branchExp.isStatic || exp.content !== branchExp.content) {
      return false;
    }
  }
  return true;
}
function getParentCondition(node) {
  while (true) {
    if (node.type === 19) {
      if (node.alternate.type === 19) {
        node = node.alternate;
      } else {
        return node;
      }
    } else if (node.type === 20) {
      node = node.value;
    }
  }
}
var transformFor = createStructuralDirectiveTransform("for", (node, dir, context) => {
  const { helper, removeHelper } = context;
  return processFor(node, dir, context, (forNode) => {
    const renderExp = createCallExpression(helper(RENDER_LIST), [
      forNode.source
    ]);
    const isTemplate = isTemplateNode(node);
    const memo = findDir(node, "memo");
    const keyProp = findProp(node, `key`);
    const keyExp = keyProp && (keyProp.type === 6 ? createSimpleExpression(keyProp.value.content, true) : keyProp.exp);
    const keyProperty = keyProp ? createObjectProperty(`key`, keyExp) : null;
    const isStableFragment = forNode.source.type === 4 && forNode.source.constType > 0;
    const fragmentFlag = isStableFragment ? 64 : keyProp ? 128 : 256;
    forNode.codegenNode = createVNodeCall(context, helper(FRAGMENT), void 0, renderExp, fragmentFlag + (process.env.NODE_ENV !== "production" ? ` /* ${PatchFlagNames[fragmentFlag]} */` : ``), void 0, void 0, true, !isStableFragment, false, node.loc);
    return () => {
      let childBlock;
      const { children } = forNode;
      if ((process.env.NODE_ENV !== "production" || false) && isTemplate) {
        node.children.some((c) => {
          if (c.type === 1) {
            const key = findProp(c, "key");
            if (key) {
              context.onError(createCompilerError(33, key.loc));
              return true;
            }
          }
        });
      }
      const needFragmentWrapper = children.length !== 1 || children[0].type !== 1;
      const slotOutlet = isSlotOutlet(node) ? node : isTemplate && node.children.length === 1 && isSlotOutlet(node.children[0]) ? node.children[0] : null;
      if (slotOutlet) {
        childBlock = slotOutlet.codegenNode;
        if (isTemplate && keyProperty) {
          injectProp(childBlock, keyProperty, context);
        }
      } else if (needFragmentWrapper) {
        childBlock = createVNodeCall(context, helper(FRAGMENT), keyProperty ? createObjectExpression([keyProperty]) : void 0, node.children, 64 + (process.env.NODE_ENV !== "production" ? ` /* ${PatchFlagNames[64]} */` : ``), void 0, void 0, true, void 0, false);
      } else {
        childBlock = children[0].codegenNode;
        if (isTemplate && keyProperty) {
          injectProp(childBlock, keyProperty, context);
        }
        if (childBlock.isBlock !== !isStableFragment) {
          if (childBlock.isBlock) {
            removeHelper(OPEN_BLOCK);
            removeHelper(getVNodeBlockHelper(context.inSSR, childBlock.isComponent));
          } else {
            removeHelper(getVNodeHelper(context.inSSR, childBlock.isComponent));
          }
        }
        childBlock.isBlock = !isStableFragment;
        if (childBlock.isBlock) {
          helper(OPEN_BLOCK);
          helper(getVNodeBlockHelper(context.inSSR, childBlock.isComponent));
        } else {
          helper(getVNodeHelper(context.inSSR, childBlock.isComponent));
        }
      }
      if (memo) {
        const loop = createFunctionExpression(createForLoopParams(forNode.parseResult, [
          createSimpleExpression(`_cached`)
        ]));
        loop.body = createBlockStatement([
          createCompoundExpression([`const _memo = (`, memo.exp, `)`]),
          createCompoundExpression([
            `if (_cached`,
            ...keyExp ? [` && _cached.key === `, keyExp] : [],
            ` && ${context.helperString(IS_MEMO_SAME)}(_cached, _memo)) return _cached`
          ]),
          createCompoundExpression([`const _item = `, childBlock]),
          createSimpleExpression(`_item.memo = _memo`),
          createSimpleExpression(`return _item`)
        ]);
        renderExp.arguments.push(loop, createSimpleExpression(`_cache`), createSimpleExpression(String(context.cached++)));
      } else {
        renderExp.arguments.push(createFunctionExpression(createForLoopParams(forNode.parseResult), childBlock, true));
      }
    };
  });
});
function processFor(node, dir, context, processCodegen) {
  if (!dir.exp) {
    context.onError(createCompilerError(31, dir.loc));
    return;
  }
  const parseResult = parseForExpression(
    dir.exp,
    context
  );
  if (!parseResult) {
    context.onError(createCompilerError(32, dir.loc));
    return;
  }
  const { addIdentifiers, removeIdentifiers, scopes } = context;
  const { source, value, key, index } = parseResult;
  const forNode = {
    type: 11,
    loc: dir.loc,
    source,
    valueAlias: value,
    keyAlias: key,
    objectIndexAlias: index,
    parseResult,
    children: isTemplateNode(node) ? node.children : [node]
  };
  context.replaceNode(forNode);
  scopes.vFor++;
  const onExit = processCodegen && processCodegen(forNode);
  return () => {
    scopes.vFor--;
    if (onExit)
      onExit();
  };
}
var forAliasRE = /([\s\S]*?)\s+(?:in|of)\s+([\s\S]*)/;
var forIteratorRE = /,([^,\}\]]*)(?:,([^,\}\]]*))?$/;
var stripParensRE = /^\(|\)$/g;
function parseForExpression(input, context) {
  const loc = input.loc;
  const exp = input.content;
  const inMatch = exp.match(forAliasRE);
  if (!inMatch)
    return;
  const [, LHS, RHS] = inMatch;
  const result = {
    source: createAliasExpression(loc, RHS.trim(), exp.indexOf(RHS, LHS.length)),
    value: void 0,
    key: void 0,
    index: void 0
  };
  if (process.env.NODE_ENV !== "production" && true) {
    validateBrowserExpression(result.source, context);
  }
  let valueContent = LHS.trim().replace(stripParensRE, "").trim();
  const trimmedOffset = LHS.indexOf(valueContent);
  const iteratorMatch = valueContent.match(forIteratorRE);
  if (iteratorMatch) {
    valueContent = valueContent.replace(forIteratorRE, "").trim();
    const keyContent = iteratorMatch[1].trim();
    let keyOffset;
    if (keyContent) {
      keyOffset = exp.indexOf(keyContent, trimmedOffset + valueContent.length);
      result.key = createAliasExpression(loc, keyContent, keyOffset);
      if (process.env.NODE_ENV !== "production" && true) {
        validateBrowserExpression(result.key, context, true);
      }
    }
    if (iteratorMatch[2]) {
      const indexContent = iteratorMatch[2].trim();
      if (indexContent) {
        result.index = createAliasExpression(loc, indexContent, exp.indexOf(indexContent, result.key ? keyOffset + keyContent.length : trimmedOffset + valueContent.length));
        if (process.env.NODE_ENV !== "production" && true) {
          validateBrowserExpression(result.index, context, true);
        }
      }
    }
  }
  if (valueContent) {
    result.value = createAliasExpression(loc, valueContent, trimmedOffset);
    if (process.env.NODE_ENV !== "production" && true) {
      validateBrowserExpression(result.value, context, true);
    }
  }
  return result;
}
function createAliasExpression(range, content, offset) {
  return createSimpleExpression(content, false, getInnerRange(range, offset, content.length));
}
function createForLoopParams({ value, key, index }, memoArgs = []) {
  return createParamsList([value, key, index, ...memoArgs]);
}
function createParamsList(args) {
  let i = args.length;
  while (i--) {
    if (args[i])
      break;
  }
  return args.slice(0, i + 1).map((arg, i2) => arg || createSimpleExpression(`_`.repeat(i2 + 1), false));
}
var defaultFallback = createSimpleExpression(`undefined`, false);
process.env.NODE_ENV !== "production" ? Object.freeze({}) : {};
process.env.NODE_ENV !== "production" ? Object.freeze([]) : [];
var cacheStringFunction2 = (fn) => {
  const cache = /* @__PURE__ */ Object.create(null);
  return (str) => {
    const hit = cache[str];
    return hit || (cache[str] = fn(str));
  };
};
var camelizeRE2 = /-(\w)/g;
var camelize3 = cacheStringFunction2((str) => {
  return str.replace(camelizeRE2, (_, c) => c ? c.toUpperCase() : "");
});

// src/core/transformRef.ts
var onVnodeBeforeMountRef = ' :onVnodeBeforeMount="onVnodeBeforeMountRef_" ';
var supportTransformRef = (_SFCParseResult, magicString, options, registerCompile) => {
  const { descriptor } = _SFCParseResult;
  if (descriptor.template) {
    const { loc: templateLocStart } = descriptor.template;
    const onRefNode = (node) => {
      if (node.type === 1) {
        const ref = findProp(node, "ref");
        if (ref && ref.type === 6) {
          const { loc } = ref;
          magicString.appendLeft(
            loc.end.offset + templateLocStart.start.offset,
            onVnodeBeforeMountRef
          );
        }
      }
    };
    registerCompile.templates.push(onRefNode);
  }
};

// src/core/unplugin.ts
var unplugin_default = (0, import_unplugin.createUnplugin)((options = {}, meta) => {
  return {
    name: "unplugin-vue-setup-extend-plus",
    enforce: "pre",
    transformInclude(id) {
      return /\.vue$/.test(id) || /\.vue\?vue/.test(id);
    },
    async transform(code, id) {
      try {
        if (options.mode && options.mode === "none")
          return null;
        if (/\.vue$/.test(id) || /\.vue\?.*type=script.*/.test(id)) {
          return compose({
            code,
            id,
            options
          }, options.enableAutoExpose ? [supportTransformRef, supportScriptName] : [supportScriptName]);
        }
        return null;
      } catch (e) {
        if (meta.framework === "webpack") {
          console.error(e);
          return null;
        }
      }
    }
  };
});

// src/rollup.ts
var rollup_default = unplugin_default.rollup;
// Annotate the CommonJS export names for ESM import in node:
0 && (module.exports = {});
exports.default = module.exports;