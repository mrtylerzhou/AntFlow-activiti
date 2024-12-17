<template>
  <div>
    <svg-icon icon-class="gitee" @click="goto" />
  </div>
</template>

<script setup>
const url = ref('https://gitee.com/tylerzhou/Antflow');

function goto() {
  window.open(url.value)
}
</script>
