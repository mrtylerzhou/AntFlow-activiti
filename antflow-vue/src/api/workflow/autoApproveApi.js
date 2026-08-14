/*
 * 用户自动审批设置 相关接口
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
 * @param {*} query {ownerUserName, formCode}
 */
export function getAutoApproveListPage(pageDto, query) {
  let data = {
    pageDto: pageDto,
    ownerUserName: query?.ownerUserName,
    formCode: query?.formCode,
  };
  return http.post(`${baseUrl}/userAutoApprove/listPage`, data, { headers });
}

/**
 * 活跃流程下拉(三类)
 */
export function getAutoApproveActiveConfList() {
  return http.get(`${baseUrl}/userAutoApprove/activeConfList`, { headers });
}

/**
 * 新增
 */
export function saveAutoApprove(data) {
  return http.post(`${baseUrl}/userAutoApprove/save`, data, { headers });
}

/**
 * 编辑
 */
export function updateAutoApprove(data) {
  return http.post(`${baseUrl}/userAutoApprove/update`, data, { headers });
}

/**
 * 启停
 */
export function toggleAutoApprove(id, enabled) {
  return http.post(`${baseUrl}/userAutoApprove/toggle/${id}/${enabled}`, {}, { headers });
}

/**
 * 删除
 */
export function deleteAutoApprove(id) {
  return http.get(`${baseUrl}/userAutoApprove/delete/${id}`, { headers });
}

/**
 * 复制到最新活跃版本
 */
export function copyAutoApprove(id) {
  return http.post(`${baseUrl}/userAutoApprove/copy/${id}`, {}, { headers });
}
