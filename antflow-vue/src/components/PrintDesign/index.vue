<template>
  <div class="print-design-container">
    <!-- 左侧字段标签列表 -->
    <div class="field-panel">
      <div class="field-panel-header">
        <h4>表单字段</h4>
      </div>
      <div class="field-list">
        <div 
          v-for="field in formFieldData" 
          :key="field.fieldName" 
          class="field-item"
          draggable="true"
          @dragstart="onDragStart($event, field)"
          @dragend="onDragEnd"
        >
          <div class="field-icon">
            <el-icon>
                <Setting />
            </el-icon>
          </div>
          <div class="field-info">
            <div class="field-label">{{ field.label || field.name }}</div>
            <div class="field-name">{{ field.name }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧编辑区域 -->
    <div class="editor-panel">
      <div class="editor-header">
        <h4>打印模板设计</h4>
        <div class="editor-tools">
          <button @click="clearTemplate" class="tool-btn">清空</button>
          <button @click="previewTemplate" v-if="!previewing" style="color:cornflowerblue;border: 1px solid lightblue;" class="tool-btn">预览</button>
          <button @click="cancelPreviewTemplate" style="color:red;border: 1px solid red;" v-else class="tool-btn">取消预览</button>
        </div>
      </div>
      
      <!-- 拖拽区域 -->
      <div 
        class="drop-zone"
        @dragover="onDragOver"
        @drop="onDrop"
        @dragenter="onDragEnter"
        @dragleave="onDragLeave"
        :class="{ 'drag-over': isDragOver }"
      >
        <!-- 提示区域 -->
        <div v-if="!hasContent" class="drop-placeholder">
          <i class="el-icon-upload"></i>
          <span>拖拽左侧字段到编辑器生成模板</span>
        </div>
        
        <!-- 富文本编辑器 -->
        <div class="editor-wrapper">
          <vue-ueditor-wrap 
            v-model="content"
            editor-id="editor"
            :config="editorConfig"
            :editorDependencies="['ueditor.config.js','ueditor.all.js']"
            class="ueditor-container"
            style="width: 100%; height: 100%;"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, computed, onMounted, watch, nextTick, onUnmounted} from 'vue';

const content = ref('');
const oriContent = ref('');
const lfFormDataJson = ref('');
const formJsonData = ref(null);
const formFieldData = ref(null);
const previewing = ref(false);

// 拖拽相关状态
const isDragOver = ref(false);
const draggedField = ref(null);
const hasContent = computed(() => content.value && content.value.trim().length > 0);

let props = defineProps({
    printData: {
        type: String,
        default: null,
    },
    lfFormData: {
        type: String,
        default: null,
    },
    formDesignRef: {
        type: Object,
        default: null,
    }
});

// 拖拽开始
const onDragStart = (event, field) => {
    draggedField.value = field;
    event.dataTransfer.effectAllowed = 'copy';
    event.dataTransfer.setData('text/plain', '${' + field.name + '}');
    // event.dataTransfer.setData('text/plain', JSON.stringify(field));
};

// 拖拽结束
const onDragEnd = () => {
    draggedField.value = null;
    console.log('拖拽结束');
};

// 拖拽悬停
const onDragOver = (event) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'copy';
};

// 拖拽进入
const onDragEnter = (event) => {
    event.preventDefault();
    isDragOver.value = true;
};

// 拖拽离开
const onDragLeave = (event) => {
    // 确保只有当真正离开拖拽区域时才设置状态
    if (!event.currentTarget.contains(event.relatedTarget)) {
        isDragOver.value = false;
    }
};

// 拖拽放置
const onDrop = (event) => {
    event.preventDefault();
    isDragOver.value = false;
    
    if (draggedField.value) {
        draggedField.value = null;
    }
};

// 生成预览数据
const generatePreviewData = () => {
    if (!formFieldData.value) return {};
    
    const previewData = {};
    formFieldData.value.forEach(field => {
        // 根据字段类型生成示例数据
        switch (field.fieldTypeName) {
            case 'input':
            case 'text':
                previewData[field.name] = `示例-${field.label || field.name}`;
                break;
            case 'select':
            case 'switch':
            case 'radio':
                previewData[field.name] = '示例-选项1';
                break;
            case 'textarea':
                previewData[field.name] = `示例-这是${field.label || field.name}的示例内容，可以包含多行文本。`;
                break;
            case 'time':
                previewData[field.name] = '示例-2024-01-01';
                break;
            case 'input-number':
                previewData[field.name] = '示例-123';
                break;
            default:
                previewData[field.name] = `示例-${field.label || field.name}`;
        }
    });
    
    return previewData;
};

// 预览模板
const previewTemplate = () => {
    const previewData = generatePreviewData();
    let previewContent = content.value;

    previewContent = previewContent.replace(/\${(.*?)}/g,(match, variableName)=>{
        return previewData[variableName];
    } );

    oriContent.value = content.value;
    content.value = previewContent;
    previewing.value = true;
};

const cancelPreviewTemplate = () => {
    content.value = oriContent.value;
    previewing.value = false;
}

// 清空模板
const clearTemplate = () => {
    content.value = '';
    console.log('模板已清空');
};

// 监听表单数据变化
watch(() => props.lfFormData, (val) => {
    if(val){
        lfFormDataJson.value = val;
        console.log('表单配置数据', lfFormDataJson.value);
        // 解析表单配置数据
        try {
            const formConfig = JSON.parse(val);
            formJsonData.value = formConfig;
            console.log('解析后的表单配置', formJsonData.value);
        } catch (error) {
            console.error('解析表单配置数据失败:', error);
        }
    }
}, {deep: true, immediate: true})

// 监听表单设计器引用变化，获取实时表单数据
watch(() => props.formDesignRef, async (val) => {
    if(val && val.getData) {
        try {
            // 获取表单JSON数据
            const formDataResult = await val.getData();
            formJsonData.value = formDataResult.formData;
            console.log('获取到的表单JSON数据', formJsonData.value);
            
            // 获取表单字段数据
            const fieldDataResult = await val.getFieldList();
            formFieldData.value = fieldDataResult.formData;
            console.log('获取到的表单字段数据', formFieldData.value);
        } catch (error) {
            console.error('获取表单数据失败:', error);
        }
    }
}, {deep: true, immediate: true})

// 监听打印数据变化
watch(() => props.printData,  (newVal, oldVal) => {
    content.value = props.printData;
});

// 获取表单字段列表的方法
const getFormFieldList = () => {
    return formFieldData.value || [];
}

// 获取表单JSON数据的方法
const getFormJsonData = () => {
    return formJsonData.value || null;
}

// 根据字段名获取字段信息
const getFieldInfo = (fieldName) => {
    if (!formFieldData.value) return null;
    return formFieldData.value.find(field => field.fieldName === fieldName);
}

// 更新表单数据的方法
const updateFormData = (newFormData) => {
    formJsonData.value = newFormData;
}

// 更新字段数据的方法
const updateFieldData = (newFieldData) => {
    formFieldData.value = newFieldData.formFields || newFieldData;
}

// 示例：获取特定字段的信息
const getFieldByType = (fieldType) => {
    if (!formFieldData.value) return [];
    return formFieldData.value.filter(field => field.fieldType === fieldType);
}

onMounted(() => {
    content.value = props.printData;

})

const getData = () => {
    return new Promise((resolve, reject) => {
        resolve({ 
            formData: content.value,
            formJson: formJsonData.value,
            formFields: formFieldData.value
        })
    })
}

const baseUrl = import.meta.env.VITE_APP_BASE_API;

const editorConfig = {
  // 后端服务地址，后端处理参考
  // https://open-doc.modstart.com/ueditor-plus/backend.html
  // serverUrl: baseUrl + '/upeditor',
  UEDITOR_HOME_URL: '/static/UEditorPlus/dist-min/',
  UEDITOR_CORS_URL: '/static/UEditorPlus/dist-min/',

  // 设置编辑器初始宽高
  initialFrameWidth: '100%',
  initialFrameHeight: 400,

  // 设置编辑器最小高度
  minFrameHeight: 400,

  // 设置编辑器最大高度
  maxFrameHeight: 800,
}

defineExpose({
    getData,
    getFormFieldList,
    getFormJsonData,
    getFieldInfo,
    updateFormData,
    updateFieldData,
    getFieldByType,
    generatePreviewData
})
</script>

<style lang="scss" scoped>
.print-design-container {
  display: flex;
  width: 100%;
  height: 100%;
  gap: 20px;
  background: #f5f5f7;
  box-sizing: border-box;
  overflow: hidden;
}

.field-panel {
  width: 280px;
  min-width: 280px;
    max-height: 750px !important;
    overflow: auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  
  .field-panel-header {
    padding: 16px;
    border-bottom: 1px solid #e8e8e8;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-shrink: 0;
    
    h4 {
      margin: 0;
      color: #333;
      font-size: 16px;
      font-weight: 600;
    }
    
    .field-count {
      font-size: 12px;
      color: #666;
      background: #f0f0f0;
      padding: 4px 8px;
      border-radius: 12px;
    }
  }
  
  .field-list {
    flex: 1;
    overflow-y: auto;
    padding: 12px;
    
    .field-item {
      display: flex;
      align-items: center;
      padding: 12px;
      margin-bottom: 8px;
      background: #f8f9fa;
      border: 1px solid #e9ecef;
      border-radius: 6px;
      cursor: grab;
      transition: all 0.2s ease;
      
      &:hover {
        background: #e9ecef;
        border-color: #007bff;
        transform: translateY(-1px);
        box-shadow: 0 2px 4px rgba(0, 123, 255, 0.2);
      }
      
      &:active {
        cursor: grabbing;
        transform: translateY(0);
      }
      
      .field-icon {
        width: 32px;
        height: 32px;
        background: #007bff;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        
        i {
          color: white;
          font-size: 14px;
        }
      }
      
      .field-info {
        flex: 1;
        
        .field-label {
          font-weight: 500;
          color: #333;
          font-size: 14px;
          margin-bottom: 2px;
        }
        
        .field-name {
          font-size: 12px;
          color: #666;
          font-family: 'Courier New', monospace;
        }
      }
    }
  }
}

.editor-panel {
  flex: 1;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  height: 100%;
  min-width: 0; // 防止flex子元素溢出
  
  .editor-header {
    padding: 16px;
    border-bottom: 1px solid #e8e8e8;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-shrink: 0;
    
    h4 {
      margin: 0;
      color: #333;
      font-size: 16px;
      font-weight: 600;
    }
    
    .editor-tools {
      display: flex;
      gap: 8px;
      
      .tool-btn {
        padding: 6px 12px;
        border: 1px solid #ddd;
        background: white;
        border-radius: 4px;
        cursor: pointer;
        font-size: 12px;
        transition: all 0.2s ease;
        
        &:hover {
          background: #f8f9fa;
          border-color: #007bff;
          color: #007bff;
        }
      }
    }
  }
  
  .drop-zone {
    flex: 1;
    position: relative;
    border-radius: 0 0 8px 8px;
    transition: all 0.2s ease;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    
    &.drag-over {
      background: rgba(0, 123, 255, 0.05);
      border: 2px dashed #007bff;
    }
    
    .drop-placeholder {
      flex-shrink: 0;
      padding: 12px 16px;
      background: #f8f9fa;
      border-bottom: 1px solid #e8e8e8;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #666;
      font-size: 14px;
      
      i {
        font-size: 16px;
        margin-right: 8px;
        color: #007bff;
      }
      
      span {
        margin: 0;
        font-size: 14px;
        color: #666;
      }
    }
    
    .editor-wrapper {
      flex: 1;
      display: flex;
      flex-direction: column;
      min-height: 0;
      width: 100%;
      height: auto;
      max-height: 100%;
      overflow: hidden;
      
      .ueditor-container {
        flex: 1;
        min-height: 0;
        width: 100%;
        height: auto;
        max-height: 100%;
        display: flex;
        flex-direction: column;
        
        :deep(.edui-editor) {
          height: auto !important;
          min-height: 400px !important;
          width: 100% !important;
          display: flex !important;
          flex-direction: column !important;
        }
        
        :deep(.edui-editor-toolbarbox) {
          flex-shrink: 0 !important;
          width: 100% !important;
        }
        
        :deep(.edui-editor-iframeholder) {
          height: auto !important;
          min-height: 400px !important;
          width: 100% !important;
          flex: 1 !important;
          display: flex !important;
          flex-direction: column !important;
        }
        
        :deep(.edui-editor-iframeholder iframe) {
          height: auto !important;
          min-height: 500px !important;
          width: 100% !important;
          flex: 1 !important;
          border: none !important;
        }
        
        :deep(.edui-editor-iframeholder .edui-editor-iframe) {
          height: auto !important;
          min-height: 400px !important;
          width: 100% !important;
          flex: 1 !important;
        }
      }
    }
  }
}

// 编辑器内字段样式
:deep(.field-item) {
  display: inline-block;
  margin: 4px;
  padding: 8px 12px;
  background: #e3f2fd;
  border: 1px solid #2196f3;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  
  .field-label {
    font-weight: 500;
    color: #1976d2;
    margin-right: 8px;
  }
  
  .field-value {
    color: #2196f3;
    background: rgba(33, 150, 243, 0.1);
    padding: 2px 6px;
    border-radius: 3px;
  }
  
  // 不同类型字段的样式
  &.select-field {
    background: #fff3e0;
    border-color: #ff9800;
    
    .field-label {
      color: #e65100;
    }
    
    .field-value {
      background: rgba(255, 152, 0, 0.1);
      color: #ff9800;
    }
  }
  
  &.textarea-field {
    background: #f3e5f5;
    border-color: #9c27b0;
    
    .field-label {
      color: #4a148c;
    }
    
    .field-value {
      background: rgba(156, 39, 176, 0.1);
      color: #9c27b0;
    }
  }
  
  &.date-field {
    background: #e8f5e8;
    border-color: #4caf50;
    
    .field-label {
      color: #1b5e20;
    }
    
    .field-value {
      background: rgba(76, 175, 80, 0.1);
      color: #4caf50;
    }
  }
  
  &.number-field {
    background: #fff8e1;
    border-color: #ffc107;
    
    .field-label {
      color: #e65100;
    }
    
    .field-value {
      background: rgba(255, 193, 7, 0.1);
      color: #ffc107;
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .print-design-container {
    flex-direction: column;
    height: auto;
    min-height: 100vh;
    
    .field-panel {
      width: 100%;
      height: 200px;
      min-width: auto;
      
      .field-list {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        
        .field-item {
          width: calc(50% - 4px);
          margin-bottom: 0;
        }
      }
    }
    
    .editor-panel {
      height: 500px;
      min-height: 500px;
    }
  }
}

// 确保编辑器在容器中正确显示
:deep(.edui-editor) {
  border: none !important;
  border-radius: 0 0 8px 8px !important;
}

:deep(.edui-editor-toolbarbox) {
  border-bottom: 1px solid #e8e8e8 !important;
}

:deep(.edui-editor-iframeholder) {
  border: none !important;
}
</style>