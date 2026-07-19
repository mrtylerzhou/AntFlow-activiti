<!--
 * 条件组编辑器公共组件
 * 从 autoNodeDrawer 抽出, 供 autoNodeDrawer / approverDrawer(条件审批节点) 复用
 * 通过 v-model 双向绑定 conditionList / groupRelation / nodeApproveList
 * 加载时自动调 convertConditionNodeValue(true) 转为前端显示格式
-->
<template>
    <div class="condition_content">
        <p class="tip">
            <slot name="tip">当满足以下条件时，将执行相应动作</slot>
            <el-text class="ml10" type="warning"
                v-if="conditionList.length > 1">条件组关系：
                且<el-switch v-model="groupRelation" />或
            </el-text>
        </p>
        <div v-for="(conditionGroupArray, conditionGroupIdx) in conditionList" :key="conditionGroupIdx">
            <el-card class="mb10" style="max-width: 680px">
                <template #header>
                    <div class="card-header">
                        <div class="l">
                            <span>条件组{{ conditionGroupIdx + 1 }}</span>
                        </div>
                        <div class="l pl10" v-if="conditionGroupArray.length > 1">
                            <el-text class="ml10" type="warning">组内条件关系：
                                且<el-switch v-model="conditionGroupArray[0].condRelation" />或
                            </el-text>
                        </div>
                        <div @click="deleteConditionGroup(conditionGroupIdx)" class="r clickable">
                            <el-icon class="branch-delete-icon"><el-icon-delete /></el-icon>
                        </div>
                    </div>
                </template>
                <ul>
                    <li v-for="(item, index) in conditionGroupArray" :key="index" class="l">
                        <div v-if="item && item.fieldTypeName">
                            <span class="ellipsis">{{ item.type == 1 ? '发起人' : item.showName }}：</span>
                            <div v-if="item.type == 1">
                                <p :class="nodeApproveList.length > 0 ? 'selected_list' : ''"
                                    @click.self="addConditionRole" style="cursor:text">
                                    <span v-for="(item1, index1) in nodeApproveList" :key="index1">
                                        {{ item1.name }}<img
                                            src="@/assets/images/antflow/add-close1.png"
                                            @click="$func.removeEle(nodeApproveList, item1, 'targetId')">
                                    </span>
                                    <input type="text" placeholder="请选择具体人员/角色/部门"
                                        v-if="nodeApproveList.length == 0"
                                        @click="addConditionRole">
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
                                    <el-select
                                        :style="'width:' + (item.optType == 6 ? 350 : 105) + 'px'"
                                        @change="changeOptType(item)" v-model="item.optType">
                                        <el-option v-for="itemOpt in optTypes" :key="itemOpt.value"
                                            :label="itemOpt.label" :value="itemOpt.value" />
                                    </el-select>
                                    <el-date-picker v-if="item.optType != 6" v-model="item.zdy1"
                                        type="date" :placeholder="'请选择' + item.showName"
                                        format="YYYY-MM-DD" />
                                </p>
                            </div>
                            <div v-else-if="item.fieldTypeName == 'time'">
                                <p>
                                    <el-select
                                        :style="'width:' + (item.optType == 6 ? 350 : 105) + 'px'"
                                        @change="changeOptType(item)" v-model="item.optType">
                                        <el-option v-for="itemOpt in optTypes" :key="itemOpt.value"
                                            :label="itemOpt.label" :value="itemOpt.value" />
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
                                        v-for="(item1, index1) in JSON.parse(item.fixedDownBoxValue)"
                                        :key="index1">{{
                                            item1.value }}</a>
                                </p>
                            </div>
                            <div v-else-if="item.fieldTypeName == 'select' && item.multiple">
                                <p class="check_box" v-if="item.fixedDownBoxValue">
                                    <el-select :placeholder="'请选择' + item.showName" v-model="item.zdy1"
                                        multiple :multiple-limit="item.multipleLimit">
                                        <el-option v-for="itemOpt in JSON.parse(item.fixedDownBoxValue)"
                                            :key="itemOpt.key" :label="itemOpt.value"
                                            :value="itemOpt.key" />
                                    </el-select>
                                </p>
                            </div>
                            <div v-else-if="item.fieldTypeName == 'select' && !item.multiple">
                                <p class="check_box" v-if="item.fixedDownBoxValue">
                                    <el-select :placeholder="'请选择' + item.showName" v-model="item.zdy1">
                                        <el-option v-for="itemOpt in JSON.parse(item.fixedDownBoxValue)"
                                            :key="itemOpt.key" :label="itemOpt.value"
                                            :value="itemOpt.key" />
                                    </el-select>
                                </p>
                            </div>
                            <div v-else-if="item.fieldTypeName == 'number'">
                                <p>
                                    <el-select
                                        :style="'width:' + (item.optType == 6 ? 350 : 105) + 'px'"
                                        @change="changeOptType(item)" v-model="item.optType">
                                        <el-option v-for="itemOpt in optTypes" :key="itemOpt.value"
                                            :label="itemOpt.label" :value="itemOpt.value" />
                                    </el-select>
                                    <input v-if="item.optType != 6" style="width:220px;" type="text"
                                        :placeholder="'请输入' + item.showName" v-model="item.zdy1">
                                </p>
                                <p v-if="item.optType == 6">
                                    <input type="text" style="width:75px;" class="mr10" v-model="item.zdy1">
                                    <el-select style="width:60px;" v-model="item.opt1">
                                        <el-option v-for="itemOpt in opt1s" :key="itemOpt.value"
                                            :label="itemOpt.label" :value="itemOpt.value" />
                                    </el-select>
                                    <span class="ellipsis"
                                        style="display:inline-block;width:60px;vertical-align: text-bottom;">{{
                                            item.showName }}</span>
                                    <el-select style="width:60px;" class="ml10" v-model="item.opt2">
                                        <el-option v-for="itemOpt in opt1s" :key="itemOpt.value"
                                            :label="itemOpt.label" :value="itemOpt.value" />
                                    </el-select>
                                    <input type="text" style="width:75px;" v-model="item.zdy2">
                                </p>
                            </div>
                            <div v-else>
                                <p class="check_box">
                                    <input v-model="item.optType" hidden>
                                    <input type="text" :placeholder="'请输入' + item.showName"
                                        v-model="item.zdy1">
                                </p>
                            </div>
                            <a v-if="item.type == 1"
                                @click="nodeApproveList = []; $func.removeEle(conditionList[conditionGroupIdx], item, 'formId')">删除</a>
                            <a v-if="item.type == 2"
                                @click="$func.removeEle(conditionList[conditionGroupIdx], item, 'formId')">删除</a>
                        </div>
                    </li>
                </ul>
                <el-button type="primary" @click="addCondition(conditionGroupIdx)">添加条件</el-button>
            </el-card>
            <div v-if="conditionList.length != conditionGroupIdx + 1">
                <el-text class="ml10" type="warning" v-if="groupRelation == false">且满足</el-text>
                <el-text class="ml10" type="success" v-if="groupRelation == true">或满足</el-text>
            </div>
        </div>
        <el-button style="width: 100%" type="info" icon="el-icon-plus" text bg @click="addConditionGroup">
            添加条件组
        </el-button>
    </div>
    <ConditionDialog v-model:visible="conditionVisible" :activeGroupIdx="activeGroupIdx" :nodeConfig="editorNodeConfig" />
</template>
<script setup>
import { ref, computed, watch } from 'vue'
import ConditionDialog from "../../dialog/selectConditionDialog.vue";
import { optTypes, opt1s } from '@/utils/antflow/const'
import $func from '@/utils/antflow/index'

const { proxy } = getCurrentInstance()

const props = defineProps({
    conditionList: {
        type: Array,
        required: true,
    },
    groupRelation: {
        type: Boolean,
        default: false,
    },
    nodeApproveList: {
        type: Array,
        default: () => [],
    },
})

const emit = defineEmits(['update:groupRelation', 'update:nodeApproveList'])

// 双向绑定: conditionList 是数组, 直接 mutate 即可触发父组件更新(引用同源)
// groupRelation / nodeApproveList 需通过 emit update
const groupRelation = computed({
    get() { return props.groupRelation },
    set(val) { emit('update:groupRelation', val) }
})
const nodeApproveList = computed({
    get() { return props.nodeApproveList },
    set(val) { emit('update:nodeApproveList', val) }
})

let conditionVisible = ref(false)
let conditionRoleVisible = ref(false)
let activeGroupIdx = ref(0)

// ConditionDialog 期望接收 nodeConfig (含 conditionList/nodeApproveList 等), 这里组装
const editorNodeConfig = computed(() => ({
    conditionList: props.conditionList,
    groupRelation: props.groupRelation,
    nodeApproveList: props.nodeApproveList,
}))

// 加载时把后端存储格式转为前端显示格式
watch(() => props.conditionList, (val) => {
    if (val && val.length > 0) {
        $func.convertConditionNodeValue(val, true)
    }
}, { immediate: true })

/**添加条件 */
const addCondition = (index) => {
    activeGroupIdx.value = index
    conditionVisible.value = true
}
/**添加条件组 */
const addConditionGroup = () => {
    props.conditionList.push([])
}
/**删除条件组 */
const deleteConditionGroup = (index) => {
    props.conditionList.splice(index, 1)
}
/**值类型条件改变 */
const changeOptType = (item) => {
    if (item.optType == 1) {
        item.zdy1 = null
    } else {
        item.zdy1 = null
        item.zdy2 = null
    }
}
/**checkbox控件选中效果 */
const toStrChecked = (item, key) => {
    let a = item.zdy1 ? item.zdy1.split(",") : []
    var isIncludes = $func.toggleStrClass(item, key)
    if (!isIncludes) {
        a.push(key)
        item.zdy1 = a.toString()
    } else {
        removeStrEle(item, key)
    }
}
/**删除数组元素 */
const removeStrEle = (item, key) => {
    let a = item.zdy1 ? item.zdy1.split(",") : []
    var includesIndex
    a.map((item, index) => {
        if (item == key) {
            includesIndex = index
        }
    })
    a.splice(includesIndex, 1)
    item.zdy1 = a.toString()
}
/**添加条件角色 */
const addConditionRole = () => {
    conditionRoleVisible.value = true
}
</script>
<style scoped lang="scss">
@use "@/assets/styles/antflow/dialog.scss";

.condition_content {
    padding: 5px 5px 0;

    p.tip {
        text-indent: 17px;
        line-height: 30px;
        background: rgba(241, 249, 255, 1);
        border: 1px solid rgba(64, 163, 247, 1);
        color: #46a6fe;
        font-size: 14px;
    }

    ul {
        max-height: 178px;
        overflow-y: scroll;
        margin-bottom: 20px;

        li {
            border-bottom: 1px solid #F2F2F2;

            div {
                &>span {
                    float: left;
                    margin-right: 5px;
                    width: 100px;
                    line-height: 65px;
                    text-align: right;
                    color: #0857a1;
                    font-size: 14px;
                }

                &>div {
                    display: inline-block;
                    width: 350px;

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
    }

    .el-button {
        margin-bottom: 5px;
    }
}
</style>
