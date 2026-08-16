/*
 * 演示数据-业务数据 相关接口
 */
import http from "@/utils/axios";
import Cookies from "js-cookie";
let baseUrl = import.meta.env.VITE_APP_BASE_API;
const headers = {
  Userid: Cookies.get("userId"),
  Username: Cookies.get("userName"),
};

/**
 * 业务数据动态列表(columns + rows + total)
 * @param {*} pageDto {page, pageSize}
 * @param {*} query {formCode, processNumber}
 */
export function getBusinessDataListPage(pageDto, query) {
  let data = {
    pageDto: pageDto,
    formCode: query?.formCode,
    processNumber: query?.processNumber,
  };
  return http.post(`${baseUrl}/demoData/businessData/listPage`, data, { headers });
}

/**
 * 流程详情查看权限校验
 * @param {*} processNumber 流程编号
 */
export function checkBusinessDataPermission(processNumber) {
  return http.post(`${baseUrl}/demoData/businessData/checkPermission`, null, {
    params: { processNumber },
    headers,
  });
}

/**
 * 人员管理分页列表(姓名/手机号模糊搜索)
 * @param {*} pageDto {page, pageSize}
 * @param {*} query {userName, mobile}
 */
export function getUserListPage(pageDto, query) {
  return http.post(`${baseUrl}/demoData/businessData/userListPage`, {
    pageDto: pageDto,
    userName: query?.userName,
    mobile: query?.mobile,
  }, { headers });
}

/**
 * 部门管理分页列表(名称模糊搜索)
 * @param {*} pageDto {page, pageSize}
 * @param {*} query {deptName}
 */
export function getDepartmentListPage(pageDto, query) {
  return http.post(`${baseUrl}/demoData/businessData/departmentListPage`, {
    pageDto: pageDto,
    deptName: query?.deptName,
  }, { headers });
}

/**
 * 角色管理分页列表(名称模糊搜索,含关联人数)
 * @param {*} pageDto {page, pageSize}
 * @param {*} query {roleName}
 */
export function getRoleListPage(pageDto, query) {
  return http.post(`${baseUrl}/demoData/businessData/roleListPage`, {
    pageDto: pageDto,
    roleName: query?.roleName,
  }, { headers });
}

/**
 * 角色详情:角色下人员分页列表
 * @param {*} pageDto {page, pageSize}
 * @param {*} roleId 角色ID
 */
export function getRoleUsersPage(pageDto, roleId) {
  return http.post(`${baseUrl}/demoData/businessData/roleUsers`, {
    pageDto: pageDto,
    roleId: roleId,
  }, { headers });
}
