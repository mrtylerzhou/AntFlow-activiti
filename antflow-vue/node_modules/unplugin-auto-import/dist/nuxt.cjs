"use strict";Object.defineProperty(exports, "__esModule", {value: true});

var _chunkJ7PSH3RRcjs = require('./chunk-J7PSH3RR.cjs');
require('./chunk-6BSQ6ZKC.cjs');

// src/nuxt.ts
var _kit = require('@nuxt/kit');
var nuxt_default = _kit.defineNuxtModule.call(void 0, {
  setup(options) {
    options.exclude = options.exclude || [/[\\/]node_modules[\\/]/, /[\\/]\.git[\\/]/, /[\\/]\.nuxt[\\/]/];
    _kit.addWebpackPlugin.call(void 0, _chunkJ7PSH3RRcjs.unplugin_default.webpack(options));
    _kit.addVitePlugin.call(void 0, _chunkJ7PSH3RRcjs.unplugin_default.vite(options));
  }
});


exports.default = nuxt_default;
