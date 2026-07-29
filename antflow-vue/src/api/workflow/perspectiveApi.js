/*
 * 流程透视 相关接口
 */
import http from "@/utils/axios";
import Cookies from "js-cookie";
let baseUrl = import.meta.env.VITE_APP_BASE_API;
const headers = {
  Userid: Cookies.get("userId"),
  Username: Cookies.get("userName"),
};

/**
 * 获取全部流程列表(DIY/LF/SaaS合并)
 * @param {string} desc 搜索关键字
 */
export function getAllFormCodeList(desc) {
  return http.post(`${baseUrl}/bpmnBusiness/getAllFormCodeList`, null, {
    params: { desc },
    headers,
  });
}

/**
 * 分批搜索流程配置
 * @param {object} data 搜索参数
 */
export function perspectiveSearch(data) {
  return http.post(`${baseUrl}/processPerspective/search`, data, { headers });
}
