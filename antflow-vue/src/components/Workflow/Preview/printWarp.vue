<template>
    <div class="print-warp" >
        <vue-ueditor-wrap ref="ueditor" v-model="content"
                          @ready="editor => editor.body.contentEditable = false"
                          editor-id="editor"
                          :config="editorConfig"
                          :editorDependencies="['ueditor.config.js','ueditor.all.js']"
                          style="height:600px;"/>
    </div>
</template>
<script setup>
import {getViewBusinessProcess} from "@/api/workflow/index.js";
import {loadDIYComponent, loadLFComponent} from "@/views/workflow/components/componentload.js";
import {useStore} from "@/store/modules/workflow.js";
let store = useStore()
let viewConfig = computed(() => store.instanceViewConfig1)

const { proxy } = getCurrentInstance();
let props = defineProps({
    lfFormData: {
        type: String,
        default: "{}",
    },
    printData: {
        type: String,
        default: null,
    },
});
const content = ref('');
const ueditor = ref(null);
onMounted(() => {
    content.value = props.printData;
    if(props.lfFormData && props.printData){
        const lfFormData = JSON.parse(props.lfFormData);
        content.value = content.value.replace(/\${(.*?)}/g,(match, variableName)=>{
            return lfFormData[variableName];
        });
    } else {
        const preview = async (param) => {
            let queryParams = ref({
                formCode: param.formCode,
                processNumber: param.processNumber,
                type: 2,
                isOutSideAccessProc: param.isOutSideAccess || false,
                isLowCodeFlow: param.isLowCodeFlow || false
            });
            await getViewBusinessProcess(queryParams.value).then(async (response) => {
                if (response.code == 200) {
                    const lfFormData = response.data.lfFields;
                    const printData = response.data.printData;
                    content.value = printData.replace(/\${(.*?)}/g,(match, variableName)=>{
                        return lfFormData[variableName];
                    });
                }
            });
        }
        preview(viewConfig.value);
    }
})

const baseUrl = import.meta.env.VITE_APP_BASE_API;
const editorConfig = {
    // 后端服务地址，后端处理参考
    // https://open-doc.modstart.com/ueditor-plus/backend.html
    // serverUrl: baseUrl + '/upeditor',
    UEDITOR_HOME_URL: '/static/UEditorPlus/dist-min/',
    UEDITOR_CORS_URL: '/static/UEditorPlus/dist-min/',
    toolbars: [
        [
            'fullscreen', 'print', 'preview', '|',
        ]
    ],
}
</script>
<style scoped>
</style>