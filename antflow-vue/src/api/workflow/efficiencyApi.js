/*
 * 流程效能统计 相关接口
 */
import http from "@/utils/axios";
import Cookies from "js-cookie";
let baseUrl = import.meta.env.VITE_APP_BASE_API;
const headers = {
  Userid: Cookies.get("userId"),
  Username: Cookies.get("userName"),
};

/**
 * 分页查询流程级效能数据
 * @param {*} data
 * @returns
 */
export function getEfficiencyPage(data) {
  return http.post(`${baseUrl}/processEfficiency/page`, data, { headers });
}

/**
 * 查询节点级效能数据
 * @param {string} procInstId
 * @returns
 */
export function getEfficiencyNodes(procInstId) {
  return http.get(`${baseUrl}/processEfficiency/nodes`, {
    params: { procInstId },
    headers,
  });
}

/**
 * 查询任务级效能数据
 * @param {string} procInstId
 * @param {string} taskDefKey
 * @returns
 */
export function getEfficiencyTasks(procInstId, taskDefKey) {
  return http.get(`${baseUrl}/processEfficiency/tasks`, {
    params: { procInstId, taskDefKey },
    headers,
  });
}
