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

// src/client/index.ts
var client_exports = {};
__export(client_exports, {
  default: () => client_default
});
module.exports = __toCommonJS(client_exports);
function proxyDefine(context, proxyContext, proxyKey) {
  const proxyKeys = Object.keys(context[proxyKey]);
  proxyKeys.forEach((key) => {
    Object.defineProperty(proxyContext, key, {
      get() {
        return context[proxyKey][key];
      },
      set(newValue) {
        context[proxyKey][key] = newValue;
      }
    });
  });
}
function defineDev(context) {
  const proxy = {};
  proxyDefine(context, proxy, "exposed");
  proxyDefine(context, proxy, "setupState");
  return proxy;
}
var hasOwn = (val, key) => Object.prototype.hasOwnProperty.call(val, key);
function helpProxy(context, key, update, value) {
  const get = (proxyContext) => {
    return proxyContext[key];
  };
  const set = (proxyContext, value2) => {
    proxyContext[key] = value2;
    return true;
  };
  if (context.exposed && hasOwn(context.exposed, key))
    return update ? set(context.exposed, value) : get(context.exposed);
  if (hasOwn(context.setupState, key))
    return update ? set(context.setupState, value) : get(context.setupState);
  return context[key];
}
var onVnodeBeforeMountRef_ = (VNode) => {
  const { component } = VNode;
  if (component) {
    const proxyContext = process.env.NODE_ENV !== "production" ? defineDev(component) : {};
    component.exposeProxy = new Proxy(proxyContext, {
      get(_, key) {
        return helpProxy(component, key, false);
      },
      set(_, key, value) {
        return helpProxy(component, key, true, value);
      }
    });
  }
};
var client_default = (app) => {
  app.config.globalProperties.onVnodeBeforeMountRef_ = onVnodeBeforeMountRef_;
};
// Annotate the CommonJS export names for ESM import in node:
0 && (module.exports = {});
