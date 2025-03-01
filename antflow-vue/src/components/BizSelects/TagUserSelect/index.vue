<template>
    <div class="tag-select">
        <div class="tag-box">
            <el-tag 
            v-for="item in selectValues" 
            :key="item.id" 
            effect="dark" 
            style="margin-right: 5px" 
            type="primary" 
            :size="layoutSize" 
            closable 
            @close="onDeleteTag(item)">
                {{ item.name }}
            </el-tag>
        </div>
        <slot name="append" />
    </div>
</template>

<script setup>
import { ref } from 'vue';

{/* <TagUserSelect v-model:value="userSelectedList"  style="width: 220px">
    <template #append> 
        <el-button class="append-add" type="default" icon="Plus" @change="onSelectUser" />
    </template>
</TagUserSelect>    */}

// import TagUserSelect from "@/components/BizSelects/TagUserSelect/index.vue";
// import selectUser from '@/components/BizSelects/userListDialog2.vue';

// let userDialogVisible= ref(false);
// let userSelectedList = ref([{id:1,name:'张三'},{id:2,name:'李四'}]);
// const saveUserDialog = () => {
//     console.log('data====');
// }

// function onSelectUser() { 
//     userDialogVisible.value = true;
//     console.log('data====',userDialogVisible.value);
// }
 
const emits = defineEmits(['update:value']); 
const props = defineProps({ 
    value: {
        type: Array,
        default: []
    }
})
const layoutSize=ref('default');    
const selectValues = ref([]);
  
const onDeleteTag = (tag) => {
    let temp = selectValues.value.filter(item => {
        return item.id !== tag.id;
    });
    if (Array.isArray(props.value)) {
        emits('update:value', temp);
    } else {
        emits('update:value', temp.map(item => item.id).join(','));
    }
};

watch(() => props.value, newValue => {
    console.log('newValue====',JSON.stringify(newValue));
        if (newValue == null || newValue === '') {
            selectValues.value = [];
        } else {
            if (Array.isArray(newValue)) {
                selectValues.value = [...newValue].map(row => {
                    return {
                        id: row.id,
                        name: row.name,
                    };
                });
                console.log('selectValues====',JSON.stringify(selectValues.value));
            } else {
                selectValues.value = newValue.split(',').map(item => {
                    return {
                        id: item,
                        name: item,
                    };
                });
            }
        }
    },
    {immediate: true, deep: true, },
);
</script>

<style scoped lang="scss">
.tag-select {
    display: flex;
    width: 100%;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
}

.tag-box {
    flex-grow: 1;
    padding: 0 5px;
}
</style>