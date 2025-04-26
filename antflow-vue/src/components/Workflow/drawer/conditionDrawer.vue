<!--
 * @Date: 2023-03-15 14:44:17
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2023-05-24 15:20:48
 * @FilePath: /ant-flow/src/components/drawer/conditionDrawer.vue
-->

<template>
    <el-drawer :append-to-body="true" title="条件设置" v-model="visible" class="set_condition" :with-header="false"
        :size="680">
        <span class="drawer-title">条件设置</span>
        <template #header="{ titleId, titleClass }">
            <h3 :id="titleId" :class="titleClass">条件设置</h3>
            <select v-model="originalConfigData.priorityLevel" class="priority_level">
                <option v-for="item in conditionsConfig.conditionNodes.length" :value="item" :key="item">优先级{{ item }}
                </option>
            </select>
        </template>
        <div class="demo-drawer__content">
            <div class="condition_content drawer_content">
                <p class="tip">当审批单同时满足以下条件时进入此流程</p>
                <ul>
                    <li v-for="(item, index) in originalConfigData.conditionList" :key="index" style="float: left;">
                        <span class="ellipsis">{{ item.type == 1 ? '发起人' : item.showName }}：</span>
                        <div v-if="item.type == 1">
                            <p :class="originalConfigData.nodeApproveList.length > 0 ? 'selected_list' : ''"
                                @click.self="addConditionRole" style="cursor:text">
                                <span v-for="(item1, index1) in originalConfigData.nodeApproveList" :key="index1">
                                    {{ item1.name }}<img src="@/assets/images/add-close1.png"
                                        @click="$func.removeEle(originalConfigData.nodeApproveList, item1, 'targetId')">
                                </span>
                                <input type="text" placeholder="请选择具体人员/角色/部门"
                                    v-if="originalConfigData.nodeApproveList.length == 0" @click="addConditionRole">
                            </p>
                        </div>
                        <div v-else-if="item.fieldTypeName == 'input'">
                            <p class="check_box">
                                <input v-model="item.optType" hidden>
                                <input type="text" :placeholder="'请输入' + item.showName" v-model="item.zdy1">
                            </p>
                        </div>
                        <div v-else-if="item.fieldTypeName == 'date'">
                            <p>
                                <el-select :style="'width:' + (item.optType == 6 ? 370 : 120) + 'px'"
                                    @change="changeOptType(item)"
                                    v-model="item.optType">
                                    <el-option v-for="itemOpt in optTypes" :key="itemOpt.value" :label="itemOpt.label"
                                        :value="itemOpt.value" />
                                </el-select>
                                <el-date-picker v-if="item.optType != 6" v-model="item.zdy1" type="date"
                                    :placeholder="'请选择' + item.showName" format="YYYY-MM-DD" />
                            </p>
                        </div>
                        <div v-else-if="item.fieldTypeName == 'time'">
                            <p>
                                <el-select :style="'width:' + (item.optType == 6 ? 370 : 120) + 'px'"
                                    @change="changeOptType(item)"
                                    v-model="item.optType">
                                    <el-option v-for="itemOpt in optTypes" :key="itemOpt.value" :label="itemOpt.label"
                                        :value="itemOpt.value" />
                                </el-select>
                                <el-time-picker v-if="item.optType != 6" v-model="item.zdy1"
                                    :placeholder="'请选择' + item.showName" />
                            </p>
                        </div>
                        <div v-else-if="item.fieldTypeName == 'switch'">
                            <p class="check_box">
                                <el-switch v-model="item.zdy1" />
                            </p>
                        </div>
                        <div v-else-if="item.fieldTypeName == 'radio'">
                            <p class="check_box">
                                {{ item.fieldTypeName }}
                            </p>
                        </div>
                        <div v-else-if="item.fieldTypeName == 'checkbox'">
                            <p class="check_box">
                                <a :class="$func.toggleStrClass(item, item1.key) && 'active'"
                                    @click="toStrChecked(item, item1.key)"
                                    v-for="(item1, index1) in JSON.parse(item.fixedDownBoxValue)" :key="index1">{{
                                        item1.value }}</a>
                            </p>
                        </div>
                        <div v-else-if="item.fieldTypeName == 'select' && item.multiple">
                            <p class="check_box" v-if="item.fixedDownBoxValue">
                                <el-select :placeholder="'请选择' + item.showName" v-model="item.zdy1" multiple
                                    :multiple-limit="item.multipleLimit">
                                    <el-option v-for="itemOpt in JSON.parse(item.fixedDownBoxValue)" :key="itemOpt.key"
                                        :label="itemOpt.value" :value="itemOpt.key" />
                                </el-select>
                            </p>
                        </div>
                        <div v-else-if="item.fieldTypeName == 'select' && !item.multiple">
                            <p class="check_box" v-if="item.fixedDownBoxValue">
                                <el-select :placeholder="'请选择' + item.showName" v-model="item.zdy1">
                                    <el-option v-for="itemOpt in JSON.parse(item.fixedDownBoxValue)" :key="itemOpt.key"
                                        :label="itemOpt.value" :value="itemOpt.key" />
                                </el-select>
                            </p>
                        </div>
                        <div v-else-if="item.fieldTypeName == 'input-number'">
                            <p>
                                <el-select :style="'width:' + (item.optType == 6 ? 370 : 120) + 'px'"
                                    @change="changeOptType(item)"
                                    v-model="item.optType">
                                    <el-option v-for="itemOpt in optTypes" :key="itemOpt.value" :label="itemOpt.label"
                                        :value="itemOpt.value" />
                                </el-select>
                                <input v-if="item.optType != 6" style="width:250px;" type="text"
                                    :placeholder="'请输入' + item.showName" v-model="item.zdy1">
                            </p>
                            <p v-if="item.optType == 6">
                                <input type="text" style="width:75px;" class="mr_10" v-model="item.zdy1">
                                <el-select style="width:60px;" v-model="item.opt1">
                                    <el-option v-for="itemOpt in opt1s" :key="itemOpt.value" :label="itemOpt.label"
                                        :value="itemOpt.value" />
                                </el-select>
                                <span class="ellipsis"
                                    style="display:inline-block;width:60px;vertical-align: text-bottom;">{{
                                    item.showName }}</span>
                                <el-select style="width:60px;" class="ml_10" v-model="item.opt2">
                                    <el-option v-for="itemOpt in opt1s" :key="itemOpt.value" :label="itemOpt.label"
                                        :value="itemOpt.value" />
                                </el-select>
                                <input type="text" style="width:75px;" v-model="item.zdy2">
                            </p>
                        </div>
                        <div v-else>
                            <p class="check_box">
                                <input v-model="item.optType" hidden>
                                <input type="text" :placeholder="'请输入' + item.showName" v-model="item.zdy1">
                            </p>
                        </div>
                        <a v-if="item.type == 1"
                            @click="originalConfigData.nodeApproveList = []; $func.removeEle(originalConfigData.conditionList, item, 'formId')">删除</a>
                        <a v-if="item.type == 2"
                            @click="$func.removeEle(originalConfigData.conditionList, item, 'formId')">删除</a>
                    </li>
                </ul>
                <el-button type="primary" @click="addCondition">添加条件</el-button>
                <el-dialog title="选择条件" v-model="conditionVisible" :width="480" append-to-body class="condition_list">
                    <p>请选择用来区分审批流程的条件字段</p>
                    <p class="check_box">
                        <!-- <a :class="$func.toggleClass(conditionList,{formId:0},'formId')&&'active'" @click="$func.toChecked(conditionList,{formId:0},'formId')">发起人</a> -->
                        <template v-for="(item, index) in conditions" :key="index">
                            <a :class="$func.toggleClass(conditionList, item, 'formId') && 'active'"
                                @click="$func.toChecked(conditionList, item, 'formId')">{{ item?.showName }}</a>
                            <br v-if="(index + 1) % 3 == 0" />
                        </template>
                    </p>
                    <template #footer>
                        <el-button @click="conditionVisible = false">取 消</el-button>
                        <el-button type="primary" @click="sureCondition">确 定</el-button>
                    </template>
                </el-dialog>
            </div>
            <div class="demo-drawer__footer clear">
                <el-button type="primary" @click="saveCondition">确 定</el-button>
                <el-button @click="closeDrawer">取 消</el-button>
            </div>
        </div>
    </el-drawer>
</template>
<script setup>
import { ref, watch, computed } from 'vue'
import { useStore } from '@/store/modules/workflow'
import { optTypes, opt1s, condition_filedTypeMap, condition_filedValueTypeMap, condition_columnTypeMap } from '@/utils/flow/const'
import $func from '@/utils/flow/index'
import { NodeUtils } from '@/utils/flow/nodeUtils'
import { getConditions } from '@/api/mock'
const { proxy } = getCurrentInstance()
const route = useRoute()
const routePath = route.path || ''
let store = useStore()
let { setCondition, setConditionsConfig } = store
let conditionVisible = ref(false)
let conditionRoleVisible = ref(false)
let conditionsConfig = ref(null)
let originalConfigData = ref({})
let priorityLevel = ref('')
let conditions = ref([])//添加条件弹框显示
let conditionList = ref([])//添加条件弹框显示>是否选中
let checkedList = ref([])
let tableId = computed(() => store.tableId)
let conditionsConfig1 = computed(() => store.conditionsConfig1)
let conditionDrawer = computed(() => store.conditionDrawer)
let lowCodeFormFields = {}
let visible = computed({
    get() {
        lowCodeFormFields = store.lowCodeFormField;
        return conditionDrawer.value
    },
    set() {
        closeDrawer()
    }
})
watch(conditionsConfig1, (val) => {
    conditionsConfig.value = val.value;
    priorityLevel.value = val.priorityLevel
    originalConfigData.value = val.priorityLevel ? val.value.conditionNodes[val?.priorityLevel - 1] : { nodeApproveList: [], conditionList: [] }
    convertConditionNodeValue(originalConfigData.value.conditionList);
});

/**值类型条件改变 */
const changeOptType = (item) => {
    if (item.optType == 1) {
        item.zdy1 = null;
    } else {
        item.zdy1 = null;
        item.zdy2 = null;
    }
}
/**checkbox控件选中效果 */
const toStrChecked = (item, key) => {
    let a = item.zdy1 ? item.zdy1.split(",") : []
    var isIncludes = $func.toggleStrClass(item, key);
    if (!isIncludes) {
        a.push(key)
        item.zdy1 = a.toString()
    } else {
        removeStrEle(item, key);
    }
}
/**删除数组元素 */
const removeStrEle = (item, key) => {
    let a = item.zdy1 ? item.zdy1.split(",") : []
    var includesIndex;
    a.map((item, index) => {
        if (item == key) {
            includesIndex = index
        }
    });
    a.splice(includesIndex, 1);
    item.zdy1 = a.toString()
}

/**过滤空值 */
const nullableFilter = (elm) => {
    return (elm != null && elm !== false && elm !== "");
}
/**自定义表单条件加载 */
const loadDIYFormCondition = () => {
    return new Promise(async (resolve, reject) => {
        let { data } = await getConditions({ tableId: tableId.value });
        resolve(data);
        reject([]);
    });
}
/**低代码表单条件加载 */
const loadLFFormCondition = () => {
    return new Promise((resolve, reject) => {
        let conditionArr = [];
        if (!lowCodeFormFields.hasOwnProperty("formFields")) {
            resolve(conditionArr);
        }
        conditionArr = lowCodeFormFields.formFields.filter(item => { return item.fieldTypeName; }).map((item, index) => {
            if (item.fieldTypeName && condition_filedTypeMap.has(item.fieldTypeName)) {
                let optionGroup = [];
                if (item.optionItems) {
                    optionGroup = item.optionItems.map(c => {
                        let convertValue = parseInt(c.value);
                        if (!isNaN(convertValue)) {
                            return { key: convertValue, value: c.label }
                        }
                    });
                    optionGroup = optionGroup.filter(c => c);
                } 
                return {
                    formId: index + 1,
                    columnId: condition_columnTypeMap.get(item.fieldTypeName),
                    showType: condition_filedTypeMap.get(item.fieldTypeName),
                    showName: item.label,
                    columnName: item.name,
                    columnType: condition_filedValueTypeMap.get(item.fieldTypeName),
                    fieldTypeName: item.fieldTypeName,
                    multiple: item.multiple,
                    multipleLimit: item.multipleLimit,
                    fixedDownBoxValue: JSON.stringify(optionGroup)
                }
            }
        })
        conditionArr = conditionArr.filter(nullableFilter);
        resolve(conditionArr);
        reject([]);
    });
};
/**添加条件 */
const addCondition = async () => {
    conditionList.value = [];
    conditionVisible.value = true;
    conditions.value = routePath.indexOf('diy-design') > 0 ? await loadDIYFormCondition() : await loadLFFormCondition();
    if (originalConfigData.value.conditionList) {
        for (var i = 0; i < originalConfigData.value.conditionList.length; i++) {
            var { formId, columnId } = originalConfigData.value.conditionList[i];
            if (columnId == 0) {
                conditionList.value.push({ formId: formId, columnId: 0 })
            } else {
                conditionList.value.push(conditions.value.filter(item => { return item.formId == formId; })[0])
            }
        }
    }
}
/**选择条件后确认 */
const sureCondition = () => {
    for (var i = 0; i < conditionList.value.length; i++) {
        var { formId, columnId, showName, columnName, showType, columnType, fieldTypeName, multiple, multipleLimit, fixedDownBoxValue } = conditionList.value[i];
        if ($func.toggleClass(originalConfigData.value.conditionList, conditionList.value[i], "formId")) {
            continue;
        }
        const judgeObj = NodeUtils.createJudgeNode(formId, columnId, 2, showName, showType, columnName, columnType, fieldTypeName, multiple, multipleLimit, fixedDownBoxValue);
        if (columnId == 0) {
            originalConfigData.value.nodeApproveList = [];
            originalConfigData.value.conditionList.push({ formId: formId, columnId: columnId, type: 1, showName: '发起人' });
        } else {
            originalConfigData.value.conditionList.push(judgeObj)
        }
    }
    for (let i = originalConfigData.value.conditionList.length - 1; i >= 0; i--) {
        if (!$func.toggleClass(conditionList.value, originalConfigData.value.conditionList[i], "formId")) {
            originalConfigData.value.conditionList.splice(i, 1);
        }
    }
    originalConfigData.value.conditionList.sort(function (a, b) { return a.columnId - b.columnId; });
    conditionVisible.value = false;
}
/**条件抽屉的确认 */
const saveCondition = () => {
    //console.log("conditionsConfig.value.conditionNodes=====",JSON.stringify(conditionsConfig.value.conditionNodes));
    closeDrawer()
    var a = conditionsConfig.value.conditionNodes.splice(priorityLevel.value - 1, 1)//截取旧下标
    conditionsConfig.value.conditionNodes.splice(originalConfigData.value.priorityLevel - 1, 0, a[0])//填充新下标
    conditionsConfig.value.conditionNodes.map((item, index) => {
        item.priorityLevel = index + 1,
            convertConditionNodeValue(item.conditionList,false)
    });
    for (var i = 0; i < conditionsConfig.value.conditionNodes.length; i++) {
        conditionsConfig.value.conditionNodes[i].error = $func.conditionStr(conditionsConfig.value, i) == "请设置条件" && i != conditionsConfig.value.conditionNodes.length - 1
        conditionsConfig.value.conditionNodes[i].nodeDisplayName = $func.conditionStr(conditionsConfig.value, i);
    }
    setConditionsConfig({
        value: conditionsConfig.value,
        flag: true,
        id: conditionsConfig1.value.id
    })
}
/**添加条件角色 */
const addConditionRole = () => {
    conditionRoleVisible.value = true;
    checkedList.value = originalConfigData.value.nodeApproveList
}
/**关闭抽屉 */
const closeDrawer = (val) => {
    setCondition(false)
}
/**格式化控件值 */
const convertConditionNodeValue = (data,isPreview=true) => {
    if (!data || proxy.isArrayEmpty(data)) return;
    for (let item of data) { 
        if (item.fieldTypeName == 'radio') {//单选radio
            item.zdy1 = parseInt(item.zdy1)
        }
        if (item.fieldTypeName == 'select' && item.multiple) {//select多选 
            if (!Array.isArray(item.zdy1) && item.zdy1.includes('[')) {
                if (isPreview) {
                    item.zdy1 = JSON.parse(item.zdy1)
                }
            } else {
                if (!isPreview) {
                    item.zdy1 = JSON.stringify(item.zdy1)
                }
            } 
        }
        if (item.fieldTypeName == 'select' && !item.multiple) {//select单选
            item.zdy1 = parseInt(item.zdy1)
        }
        if (item.fieldTypeName == 'date') {//日期控件
            item.zdy1 = proxy.parseTime(item.zdy1, '{y}-{m}-{d} {h}:{i}:{s}')
        }
        if (item.fieldTypeName == 'time') {//时间控件
            item.zdy1 = proxy.parseTime(item.zdy1, '{y}-{m}-{d} {h}:{i}:{s}')
        }
        /**适配后端设计 */
        if (item.optType == '6') {//数字控件
            //提交转换
            if(item.opt1 == '≤'&& item.opt2 == '<'){
                item.optType='7' 
            }else if(item.opt1 == '<'&& item.opt2 == '≤'){
                item.optType='8'
            }else if(item.opt1 == '≤'&& item.opt2 == '≤'){
                item.optType='9' 
            }else{
                item.optType='6' 
            }
        }else if(item.optType == '7' || item.optType == '8' || item.optType == '9'){
            item.optType='6'//显示转换
        } 
    }
}
</script>
<style scoped lang="scss">
@import "@/assets/styles/flow/dialog.scss";

.set_condition {
    .priority_level {
        position: absolute;
        top: 11px;
        right: 30px;
        width: 100px;
        height: 32px;
        background: rgba(255, 255, 255, 1);
        border-radius: 4px;
        border: 1px solid rgba(217, 217, 217, 1);
        font-size: 12px;
    }

    .condition_content {
        padding: 20px 20px 0;

        p.tip {
            margin: 20px 0;
            width: 610px;
            text-indent: 17px;
            line-height: 50px;
            background: rgba(241, 249, 255, 1);
            border: 1px solid rgba(64, 163, 247, 1);
            color: #46a6fe;
            font-size: 14px;
        }

        ul {
            max-height:calc(68vh);
            overflow-y: scroll;
            margin-bottom: 20px;

            li {
                border-bottom: 1px solid #F2F2F2;

                &>span {
                    float: left;
                    margin-right: 5px;
                    width: 110px;
                    line-height: 65px;
                    text-align: right;
                    color: #0857a1;
                    font-size: 14px;
                }

                &>div {
                    display: inline-block;
                    width: 370px;

                    &>p:not(:last-child) {
                        margin-bottom: 10px;
                    }
                }

                &:not(:last-child)>div>p {
                    margin-bottom: 20px;
                }

                &>a {
                    margin-left: 10px;
                    margin-top: 20px;
                    color: #46a6fe;
                    font-size: 14px;
                }

                select,
                input {
                    width: 100%;
                    height: 32px;
                    background: rgba(255, 255, 255, 1);
                    border-radius: 4px;
                    border: 1px solid rgba(217, 217, 217, 1);
                }

                select+input {
                    width: 260px;
                }

                select {
                    margin-right: 10px;
                    width: 100px;
                }

                p.selected_list {
                    padding-left: 10px;
                    border-radius: 4px;
                    min-height: 32px;
                    border: 1px solid rgba(217, 217, 217, 1);
                    word-break: break-word;
                }

                p.check_box {
                    line-height: 32px;
                }
            }
        }

        .el-button {
            margin-bottom: 20px;
        }
    }
}

.condition_list {
    .el-dialog__body {
        padding: 16px 26px;
    }

    p {
        color: #666666;
        margin-bottom: 10px;

        &>.check_box {
            margin-bottom: 0;
            line-height: 36px;
        }
    }
}
</style>