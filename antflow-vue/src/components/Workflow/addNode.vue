<template>
    <div class="add-node-btn-box">
        <div class="add-node-btn">
            <el-popover placement="right-start" v-model="visible" width="auto">
                <div class="add-node-popover-body">
                    <a class="add-node-popover-item approver" @click="addType(4)">
                        <div class="item-wrapper">
                            <span class="iconfont"></span>
                        </div>
                        <p>审批人</p>
                    </a>
                    <a class="add-node-popover-item approver" @click="addType(7)">
                        <div class="item-wrapper">
                            <span style="padding-top: 20px; color:#c45656;">
                                <svg width="50" height="50"  xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
                                    <path fill="currentColor" d="m679.872 348.8-301.76 188.608a127.808 127.808 0 0 1 5.12 52.16l279.936 104.96a128 128 0 1 1-22.464 59.904l-279.872-104.96a128 128 0 1 1-16.64-166.272l301.696-188.608a128 128 0 1 1 33.92 54.272z"></path></svg>
                            </span>
                        </div>
                        <p>并行审批</p>
                    </a>
                    <a class="add-node-popover-item notifier" @click="addType(6)">
                        <div class="item-wrapper">
                            <span class="iconfont"></span>
                        </div>
                        <p>抄送人</p>
                    </a>
                    <a class="add-node-popover-item condition" @click="addType(2)">
                        <div class="item-wrapper">
                            <span class="iconfont"></span>
                        </div>
                        <p>条件分支</p>
                    </a>
                </div>
                <template #reference>
                    <button class="btn" type="button">
                        <span class="iconfont"></span>
                    </button>
                </template>
            </el-popover>
        </div>
    </div>
</template>
<script setup>
import { ref } from 'vue'
import { NodeUtils } from '@/utils/flow/nodeUtils'
let props = defineProps({
    childNodeP: {
        type: Object,
        default: ()=> (null)
    }
})
let emits = defineEmits(['update:childNodeP'])
let visible = ref(false)
const addType = (type)=> {
    visible.value = false;
    if (type != 2 && type != 3) { 
        var _dataNode;
        if (type == 4) { 
            _dataNode = NodeUtils.createApproveNode();  
            _dataNode.childNode = props.childNodeP; 
        } else if (type == 6) {
            _dataNode = NodeUtils.createCopyNode();  
            _dataNode.childNode = props.childNodeP;  
        }
        else if (type == 7) {
            _dataNode = NodeUtils.createParallelWayNode(props.childNodeP);   
        }
        emits("update:childNodeP", _dataNode)
    } else { 
        let gatewayNode= NodeUtils.createGatewayNode(props.childNodeP); 
        emits("update:childNodeP", gatewayNode)
    }
}
</script>
<style scoped lang="scss">  
@import "@/assets/styles/flow/workflow.scss";
.el-icon {
    --color: inherit;
    align-items: center;
    display: inline-flex;
    height: 1em;
    justify-content: center;
    line-height: 1em;
    position: relative;
    width: 1em;
    fill: currentColor;
    color: var(--color);
    font-size: inherit;
}
.add-node-btn-box {
    width: 240px;
    display: -webkit-inline-box;
    display: -ms-inline-flexbox;
    display: inline-flex;
    -ms-flex-negative: 0;
    flex-shrink: 0;
    -webkit-box-flex: 1;
    -ms-flex-positive: 1;
    position: relative;
    &:before {
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: -1;
        margin: auto;
        width: 2px;
        height: 100%;
        background-color: #cacaca
    }
    .add-node-btn {
        user-select: none;
        width: 240px;
        padding: 20px 0 32px;
        display: flex;
        -webkit-box-pack: center;
        justify-content: center;
        flex-shrink: 0;
        -webkit-box-flex: 1;
        flex-grow: 1;
        .btn {
            outline: none;
            box-shadow: 0 2px 4px 0 rgba(0, 0, 0, .1);
            width: 30px;
            height: 30px;
            background: #3296fa;
            border-radius: 50%;
            position: relative;
            border: none;
            line-height: 30px;
            -webkit-transition: all .3s cubic-bezier(.645, .045, .355, 1);
            transition: all .3s cubic-bezier(.645, .045, .355, 1);
            .iconfont {
                color: #fff;
                font-size: 16px
            }
            &:hover {
                transform: scale(1.3);
                box-shadow: 0 13px 27px 0 rgba(0, 0, 0, .1)
            }
            &:active {
                transform: none;
                background: #1e83e9;
                box-shadow: 0 2px 4px 0 rgba(0, 0, 0, .1)
            }
        }
    }
}
.add-node-popover-body {
    display: flex;
    .add-node-popover-item {
        margin-right: 10px;
        cursor: pointer;
        text-align: center;
        flex: 1;
        color: #191f25!important;
        .item-wrapper {
            user-select: none;
            display: inline-block;
            width: 80px;
            height: 80px;
            margin-bottom: 5px;
            background: #fff;
            border: 1px solid #e2e2e2;
            border-radius: 50%;
            transition: all .3s cubic-bezier(.645, .045, .355, 1);
            .iconfont {
                font-size: 35px;
                line-height: 80px
            }
        }
        &.approver{
            .item-wrapper {
                color: #ff943e
            }
        }
        &.notifier{
            .item-wrapper {
                color: #3296fa
            }
        }
        &.condition{
            .item-wrapper {
                color: #15bc83
            }
        }
        &:hover{
            .item-wrapper {
                background: #3296fa;
                box-shadow: 0 10px 20px 0 rgba(50, 150, 250, .4)
            }
            .iconfont {
                color: #fff
            }
        }
        &:active{
            .item-wrapper {
                box-shadow: none;
                background: #eaeaea
            }
            .iconfont {
                color: inherit
            }
        }
    }
}
</style>