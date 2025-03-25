// svg hover的指令
// 引入方式 <div v-svgHover="[home,hoverHome]"></div>
export default {
  beforeMount(el, binding) {
    const svg = binding.value[0];
    const hoverSvg = binding.value[1];
    el.onmouseenter = function () {
      el.innerHTML = hoverSvg;
    };
    el.onmouseleave = function () {
      el.innerHTML = svg;
    };
  },
};
 