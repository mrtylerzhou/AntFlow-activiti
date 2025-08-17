<template>
    <div class="app-container">
        <el-descriptions title="菜单信息" :column="3" border>
            <el-descriptions-item label="菜单类型">
                <el-tag type="success">目录</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="菜单名称">
                驾驶舱
            </el-descriptions-item>
            <el-descriptions-item label="上级菜单">
            </el-descriptions-item>
            <el-descriptions-item label="排序">
                1
            </el-descriptions-item>
            <el-descriptions-item label="图标">
                ant-design:appstore-outlined
            </el-descriptions-item>
            <el-descriptions-item label="路由地址">
                /dashboard
            </el-descriptions-item>
            <el-descriptions-item label="菜单状态" :span="2">
                <el-tag type="success">显示</el-tag>
            </el-descriptions-item>
        </el-descriptions>
    </div>
</template>

<script setup>

</script>