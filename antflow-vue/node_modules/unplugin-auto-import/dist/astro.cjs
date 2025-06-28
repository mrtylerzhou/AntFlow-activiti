"use strict";Object.defineProperty(exports, "__esModule", {value: true});

var _chunkJ7PSH3RRcjs = require('./chunk-J7PSH3RR.cjs');
require('./chunk-6BSQ6ZKC.cjs');

// src/astro.ts
function astro_default(options) {
  return {
    name: "unplugin-auto-import",
    hooks: {
      "astro:config:setup": async (astro) => {
        var _a;
        (_a = astro.config.vite).plugins || (_a.plugins = []);
        astro.config.vite.plugins.push(_chunkJ7PSH3RRcjs.unplugin_default.vite(options));
      }
    }
  };
}


exports.default = astro_default;
