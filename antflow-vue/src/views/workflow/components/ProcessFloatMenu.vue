<template>
    <div class="process-float-menu" :class="{ 'is-fixed': fixed, 'is-absolute': !fixed }">
        <div class="menu">
            <!-- 收起态: 只显示图标 -->
            <div class="ball-icon">
                <el-icon><MoreFilled /></el-icon>
            </div>
            <!-- 展开态: 上下展开两个选项(图标 + 文字) -->
            <div class="options">
                <div class="option" @click="openAudit">
                    <el-icon><Clock /></el-icon>
                    <span class="opt-text">查看表单字段变更记录</span>
                </div>
                <div class="divider"></div>
                <div class="option" @click="openComment">
                    <el-icon><ChatDotRound /></el-icon>
                    <span class="opt-text">流程沟通</span>
                </div>
            </div>
        </div>

        <auditDrawer v-model="auditVisible" :processNumber="processNumber" />
        <commentDrawer v-model="commentVisible" :processNumber="processNumber" />
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { Clock, ChatDotRound, MoreFilled } from '@element-plus/icons-vue';
import auditDrawer from '@/views/workflow/components/auditDrawer.vue';
import commentDrawer from '@/views/workflow/components/commentDrawer.vue';

defineProps({
    processNumber: { type: String, default: '' },
    // true: 固定贴视口右缘(审批页); false: 绝对定位贴父容器右缘(预览 drawer)
    fixed: { type: Boolean, default: true },
});

const auditVisible = ref(false);
const commentVisible = ref(false);

function openAudit() {
    auditVisible.value = true;
}
function openComment() {
    commentVisible.value = true;
}
</script>

<style lang="scss" scoped>
.process-float-menu {
    // 注意: 不用 transform 垂直居中(子级 el-drawer 的 fixed overlay 会被 transform 困住),
    // 通过 margin-top = -高度/2 居中; hover 时高度变化, margin-top 同步变化实现"上下展开".
    transition: margin-top 0.25s ease;

    &.is-fixed {
        position: fixed;
        right: 12px;
        top: 50%;
        margin-top: -22px;
        z-index: 1000;
    }

    &.is-absolute {
        position: absolute;
        right: -16px;
        top: 50%;
        margin-top: -22px;
        z-index: 10;
    }

    &.is-fixed:hover,
    &.is-absolute:hover {
        margin-top: -42px;
    }
}

.menu {
    position: relative;
    width: 44px;
    height: 44px;
    overflow: hidden;
    background: #fff;
    border-radius: 22px;
    box-shadow: -2px 0 8px rgba(0, 0, 0, 0.12);
    cursor: pointer;
    transition: width 0.25s ease, height 0.25s ease, border-radius 0.25s ease;
}

.process-float-menu:hover .menu {
    width: 176px;
    height: 84px;
    border-radius: 12px;
}

.ball-icon {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    color: #409eff;
    transition: opacity 0.2s ease;
}

.options {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.2s ease;

    .option {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 0 14px;
        height: 34px;
        font-size: 12px;
        color: #303133;
        white-space: nowrap;
        user-select: none;

        &:hover {
            color: #409eff;
            background-color: #ecf5ff;
        }
    }

    .divider {
        height: 1px;
        margin: 2px 14px;
        background: #ebeef5;
    }
}

.process-float-menu:hover .ball-icon {
    opacity: 0;
}

.process-float-menu:hover .options {
    opacity: 1;
}
</style>
