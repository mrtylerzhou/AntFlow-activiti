/*
 * 发起流程页(任务中心) 相关接口
 */
import http from "@/utils/axios";
import Cookies from "js-cookie";
let baseUrl = import.meta.env.VITE_APP_BASE_API;
const headers = {
  Userid: Cookies.get("userId"),
  Username: Cookies.get("userName"),
};

/**
 * 发起流程分页(页 = 最多 3 栏,栏内按分类块)
 * @param {*} page 第几页
 * @param {*} query {bpmnName, formCode, categoryId} 过滤,优先级:流程名称 > formCode > 流程类型
 */
export function getStartFlowListPage(page, query = {}) {
  return http.post(`${baseUrl}/startFlowList/page`, {
    page,
    bpmnName: query?.bpmnName,
    formCode: query?.formCode,
    categoryId: query?.categoryId,
  }, { headers });
}
