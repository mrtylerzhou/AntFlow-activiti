<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script setup>
const url = ref('https://gitee.com/ldhnet/FlowAdmin-vue/wikis');

function goto() {
  window.open(url.value)
}
</script>