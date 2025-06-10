<!--
 * @Date: 2022-08-26 17:18:14
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2022-09-21 14:36:25
 * @FilePath: /ant-flow/src/components/selectBox.vue
-->
<template>
  <ul class="select-box">
    <template v-for="(elem, i) in list" :key="i">
      <template v-if="elem.type === 'role'">
        <li v-for="item in elem.data" :key="item.roleId" 
          class="check_box"
          :class="{active: elem.isActive && elem.isActive(item), not: elem.not}" 
          @click="elem.change(item)">
          <a :title="item.description" :class="{active: elem.isActiveItem && elem.isActiveItem(item)}">
            <img src="@/assets/images/icon_role.png">{{item.roleName}}
          </a>
        </li>
      </template> 
      <template v-if="elem.type === 'employee'">
        <li v-for="item in elem.data" :key="item.id" class="check_box">
            <a :class="elem.isActive(item) && 'active'" 
              @click="elem.change(item)" 
              :title="item.departmentNames">
              <img src="@/assets/images/icon_people.png">{{item.employeeName}}
            </a>
        </li>
      </template>
    </template>
  </ul>
</template>
<script setup>
const props = defineProps({
  list: {
    type: Array,
    default: () => []
  }
}) 
</script>
<style scoped lang="scss">  
@import "@/assets/styles/antflow/dialog.scss"; 
.select-box {
  height: 440px;
  overflow-y: auto; 
  margin-left: -30px;
  li {
    padding: 5px 0;
    list-style-type: none;
    i {
      float: right;
      padding-left: 24px;
      padding-right: 10px;
      color: #3195f8;
      font-size: 12px;
      cursor: pointer;
      background: url(../../assets/images/next_level_active.png) no-repeat 10px center;
      border-left: 1px solid rgb(238, 238, 238);
    }

    a.active+i {
      color: rgb(197, 197, 197);
      background-image: url(../../assets/images/next_level.png);
      pointer-events: none;
    }

    img {
      width: 14px;
      vertical-align: middle;
      margin-right: 5px;
    }
  }
}
</style>