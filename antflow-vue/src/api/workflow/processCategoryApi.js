/*
 * 流程分类管理 相关接口
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
 * @param {*} query {processTypeName}
 */
export function getProcessCategoryListPage(pageDto, query) {
  let data = {
    pageDto: pageDto,
    processTypeName: query?.processTypeName,
  };
  return http.post(`${baseUrl}/processCategory/listPage`, data, { headers });
}

/**
 * 新增/编辑分类
 * @param {*} data {id?, processTypeName, isApp}
 */
export function saveProcessCategory(data) {
  return http.post(`${baseUrl}/processCategory/save`, data, { headers });
}

/**
 * 分类操作:2 上移 / 3 下移 / 4 删除
 * @param {*} type
 * @param {*} id
 */
export function operateProcessCategory(type, id) {
  return http.get(`${baseUrl}/processCategory/operation/${type}/${id}`, { headers });
}

/**
 * 下拉选项(流程设计器-基础设置-流程类型)
 */
export function getProcessCategoryOptions() {
  return http.get(`${baseUrl}/processCategory/options`, { headers });
}
