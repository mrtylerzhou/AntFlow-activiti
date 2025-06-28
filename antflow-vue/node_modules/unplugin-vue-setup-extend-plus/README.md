# unplugin-vue-setup-extend-plus

[![NPM version](https://img.shields.io/npm/v/unplugin-vue-setup-extend-plus?color=a1b858&label=)](https://www.npmjs.com/package/unplugin-vue-setup-extend-plus)

Make the vue script setup syntax support the name attribute

## CHANGELOG

[1.0.1]
- Fix: auto expose type

[1.0.0]
- Feat: support auto expose(by [@so11y](https://github.com/so11y))


## Feature 

- 🌟support auto expose
- support name
- support inheritAttrs
- precise breakpoints
- Expanded the function of automatic name generation
## Usage
### Basic example

```html
<template>
  <div>hello world {{ a }}</div>
</template>

<script lang="ts" setup name="App" inheritAttrs="false">
  const a = 1
</script>
```
## Install

```bash
npm i unplugin-vue-setup-extend-plus
```

## Options

```ts
vueSetupExtend({
  // Advanced mode for name, not necessary
  mode?: 'none' | 'relativeName' | Function
  // none: Cancel the setting of name.
  //       e.g.
  //       <script setup name="CustomName"> 'CustomName' 
  // support auto expose
  enableAutoExpose?: boolean
})

```

## enableAutoExpose

First of all thanks to [@so11y](https://github.com/so11y) for his contribution to this feature.

After using the script setup syntax, the export needs to be processed manually. When you need to export the full amount by default, just enable this property. The usage is as follows:

main.ts
```ts
import { createApp } from 'vue'
import autoExpose from 'unplugin-vue-setup-extend-plus/dist/client/index'
import App from './App.vue'

createApp(App).use(autoExpose).mount('#app')
```

Comp.vue
```vue
<script setup>
import { ref } from 'vue'
const numb = ref(50)
</script>
```
App.vue
```vue
<script setup>
import { onMounted, ref } from 'vue'
const el = ref()
onMounted(() => { console.log(el.value.num) }) // 50
</script>

<template>
  <Comp ref="el" />
</template>
```
## Script Tag Attributes

### name

`Set a name for the component, similar to the name attribute in the option API notation.`

[name docs](https://vuejs.org/api/options-misc.html#name)

```html
<template>
  <div>hello world {{ a }}</div>
</template>

<script lang="ts" setup name="App">
  const a = 1
</script>
```

### inheritAttrs

`If you do not want a component to automatically inherit attributes, you can set inheritAttrs: false in the component's options.`

[inheritAttrs docs](https://vuejs.org/api/options-misc.html#inheritattrs)

```html
<template>
  <div>hello world {{ a }}</div>
</template>

<script lang="ts" setup name="App" inheritAttrs="false">
  const a = 1
</script>
```

### extendIgnore

`Since the user may define the name attribute of the component in the script tag, this conflicts with the default name set by this plugin. So you need to add a switch attribute to the script setup.`

```html
<template>
  <div>hello world {{ a }}</div>
</template>

// name="App" will be invalid
<script lang="ts" setup name="App" inheritAttrs="false" extendIgnore>
  const a = 1
</script>
```


<details>
<summary>Vite</summary><br>

```ts
// vite.config.ts
import vueSetupExtend from 'unplugin-vue-setup-extend-plus/vite'

export default defineConfig({
  plugins: [
    vueSetupExtend({ /* options */ }),
  ],
})
```

<br></details>

<details>
<summary>Rollup</summary><br>

```ts
// rollup.config.js
import vueSetupExtend from 'unplugin-vue-setup-extend-plus/rollup'

export default {
  plugins: [
    vueSetupExtend({ /* options */ }),
  ],
}
```

<br></details>


<details>
<summary>Webpack</summary><br>

```ts
// webpack.config.js
module.exports = {
  /* ... */
  plugins: [
    require('unplugin-vue-setup-extend-plus/webpack').default({ /* options */ })
    // or
    // require('unplugin-vue-setup-extend-plus/webpack')({ /* options */ })
  ]
}
```

<br></details>

<details>
<summary>Nuxt</summary><br>

```ts
// nuxt.config.js
export default {
  buildModules: [
    ['unplugin-vue-setup-extend-plus/nuxt', { /* options */ }],
  ],
}
```

> This module works for both Nuxt 2 and [Nuxt Vite](https://github.com/nuxt/vite)

<br></details>

<details>
<summary>Vue CLI</summary><br>

```ts
// vue.config.js
module.exports = {
  configureWebpack: {
    plugins: [
      require('unplugin-vue-setup-extend-plus/webpack')({ /* options */ }),
    ],
  },
}
```

<br></details>

## Expansion based on [vite-plugin-vue-setup-extend](https://github.com/vbenjs/vite-plugin-vue-setup-extend)

## License

MIT
