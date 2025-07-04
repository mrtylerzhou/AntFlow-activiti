/*
 * @Date: 2022-08-29 14:00:42
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2023-03-29 15:53:05
 * @FilePath: /ant-flow/src/components/dialog/common.js
 */
import { ref } from "vue";
import { getRoles, getDepartments, getEmployees } from "@/api/workflow/mock.js";
export let searchVal = ref("");
export let departments = ref({
  titleDepartments: [],
  childDepartments: [],
  employees: [],
});
export let roles = ref({});
export let getRoleList = async () => {
  let { data } = await getRoles();
  roles.value = data.map((c) => {
    return {
      roleId: c.roleId,
      roleName: c.roleName,
    };
  });
};
export let getDepartmentList = async (parentId = 0) => {
  let { data } = await getDepartments({ parentId });
  departments.value.childDepartments = data;
};
export let getEmployeeList = async () => {
  let res = await getEmployees();
  departments.value.employees = res.data;
};
