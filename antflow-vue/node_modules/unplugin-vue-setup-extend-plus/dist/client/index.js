// src/client/index.ts
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
export {
  client_default as default
};
