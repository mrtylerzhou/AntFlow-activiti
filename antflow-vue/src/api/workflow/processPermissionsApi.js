/*
 * 流程权限管理 相关接口
 */
import http from "@/utils/axios";
import Cookies from "js-cookie";
let baseUrl = import.meta.env.VITE_APP_BASE_API;
const headers = {
  Userid: Cookies.get("userId"),
  Username: Cookies.get("userName"),
};

/**
 * 分页列表
 * @param {*} pageDto
 * @param {*} query {formCode, permissionsType, objectName}
 */
export function getProcessPermissionsListPage(pageDto, query) {
  let data = {
    pageDto: pageDto,
    formCode: query?.formCode,
    permissionsType: query?.permissionsType,
    objectName: query?.objectName,
  };
  return http.post(`${baseUrl}/processPermissions/listPage`, data, { headers });
}

/**
 * 批量保存(流程×授权对象×权限类型 笛卡尔积, 已存在跳过)
 * @param {*} data {processKeys, permissionsTypes, objectType, objectIds}
 */
export function saveProcessPermissions(data) {
  return http.post(`${baseUrl}/processPermissions/save`, data, { headers });
}

/**
 * 删除(物理)
 * @param {*} id
 */
export function deleteProcessPermission(id) {
  return http.get(`${baseUrl}/processPermissions/delete/${id}`, { headers });
}

/**
 * 部门树懒加载(按父级查子部门,parentId为空返回根节点)
 * @param {*} parentId
 */
export function getDepartmentsByParentId(parentId) {
  return http.get(`${baseUrl}/department/getDepartmentsByParentId`, {
    params: { parentId: parentId ?? undefined },
    headers,
  });
}

/**
 * 部门名称模糊查询(树形弹窗搜索用)
 * @param {*} name
 */
export function queryDepartmentsByName(name) {
  return http.get(`${baseUrl}/department/queryByNameFuzzy`, {
    params: { name },
    headers,
  });
}

/**
 * 查询全部部门(含path/level, 前端按path组装部门树)
 */
export function getAllDepartments() {
  return http.get(`${baseUrl}/department/getAllDepartments`, { headers });
}

/**
 * 角色列表(授权对象选择用,全量)
 */
export function getRoleInfo() {
  return http.get(`${baseUrl}/user/getRoleInfo`, { headers });
}
