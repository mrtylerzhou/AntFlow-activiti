import { createApp } from "vue";
import Cookies from "js-cookie";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import locale from "element-plus/es/locale/lang/zh-cn";
import "@/assets/styles/index.scss"; // global css
import VForm3 from "@/./lib/vForm/designer.umd.js";
import "./lib/vForm/designer.style.css";

import App from "./App";
import store from "./store";
import router from "./router";
import directive from "./directive"; // directive

// 注册指令
import plugins from "./plugins"; // plugins
import { download } from "@/utils/request";

// svg图标
import "virtual:svg-icons-register";
import SvgIcon from "@/components/SvgIcon";
import elementIcons from "@/components/SvgIcon/svgicon";

import "./permission"; // permission control
import { isEmpty, isEmptyArray } from "@/utils/antflow/nodeUtils";
import {
  parseTime,
  substringHidden,
  resetForm,
  addDateRange,
  handleTree,
  selectDictLabel,
  selectDictLabels,
} from "@/utils/ruoyi";

// 分页组件
import Pagination from "@/components/Pagination";
// 自定义表格工具组件
import RightToolbar from "@/components/RightToolbar";
// 富文本组件
import Editor from "@/components/Editor";
// 文件上传组件
import FileUpload from "@/components/FileUpload";
// 图片上传组件
import ImageUpload from "@/components/ImageUpload";
// 图片预览组件
import ImagePreview from "@/components/ImagePreview";
// 自定义树选择组件
import TreeSelect from "@/components/TreeSelect";
// 字典标签组件
import DictTag from "@/components/DictTag";

import nodeWrap from "@/components/Workflow/nodeWrap.vue"; // 彷钉钉流程配置
import addNode from "@/components/Workflow/addNode.vue"; // 添加节点

const app = createApp(App);

// 全局方法挂载
app.config.globalProperties.download = download;
app.config.globalProperties.parseTime = parseTime;
app.config.globalProperties.isArrayEmpty = isEmptyArray;
app.config.globalProperties.isObjEmpty = isEmpty;
app.config.globalProperties.substringHidden = substringHidden;
app.config.globalProperties.resetForm = resetForm;
app.config.globalProperties.handleTree = handleTree;
app.config.globalProperties.addDateRange = addDateRange;
app.config.globalProperties.selectDictLabel = selectDictLabel;
app.config.globalProperties.selectDictLabels = selectDictLabels;

// 全局组件挂载
app.component("DictTag", DictTag);
app.component("Pagination", Pagination);
app.component("TreeSelect", TreeSelect);
app.component("FileUpload", FileUpload);
app.component("ImageUpload", ImageUpload);
app.component("ImagePreview", ImagePreview);
app.component("RightToolbar", RightToolbar);
app.component("Editor", Editor);
app.component("nodeWrap", nodeWrap);
app.component("addNode", addNode);

app.use(router);
app.use(store);
app.use(plugins);
app.use(elementIcons);
app.component("svg-icon", SvgIcon);
app.use(VForm3); //全局注册VForm3，同时注册了v-form-designer、v-form-render等组件
directive(app);

// 使用element-plus 并且设置全局的大小
app.use(ElementPlus, {
  locale: locale,
  // 支持 large、default、small
  size: Cookies.get("size") || "default",
});

app.mount("#app");
