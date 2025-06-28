import {
  unplugin_default
} from "./chunk-PQZ6WYW7.js";
import "./chunk-6F4PWJZI.js";

// src/nuxt.ts
function nuxt_default(options) {
  this.extendBuild((config) => {
    config.plugins = config.plugins || [];
    config.plugins.unshift(unplugin_default.webpack(options));
  });
  this.nuxt.hook("vite:extend", async (vite) => {
    vite.config.plugins = vite.config.plugins || [];
    vite.config.plugins.push(unplugin_default.vite(options));
  });
}
export {
  nuxt_default as default
};
