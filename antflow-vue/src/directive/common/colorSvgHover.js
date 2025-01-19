// color Svg hover 的指令
//引入方式 <div v-colorSvgHover="'red'">......</div>
export default {
  beforeMount(el, binding) {
    // binding可以获取到指令的一些列数据，valu属性是绑定的参数 
    const wrapDom = el;
    const svg = el.querySelector("svg");
    // 储存svg原本的颜色
    const originColor = svg.style.fill;
    wrapDom.onmouseenter = function () {
      svg.style.fill = binding.value??"#fff";
    };
    wrapDom.onmouseleave = function () {
      svg.style.fill = originColor;
    };
  },
};
 