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
