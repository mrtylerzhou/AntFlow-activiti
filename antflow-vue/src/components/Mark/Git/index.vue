<template>
  <div>
    <svg-icon icon-class="gitee" @click="goto" />
  </div>
</template>

<script setup>
const url = ref('https://gitee.com/ldhnet/FlowAdmin-vue');

function goto() {
  window.open(url.value)
}
</script>
