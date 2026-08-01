/*
 * 流程实例效能(流程监控 → 更多 → 效能)相关接口
 */
import http from "@/utils/axios";
import Cookies from "js-cookie";
let baseUrl = import.meta.env.VITE_APP_BASE_API;
const headers = {
  Userid: Cookies.get("userId"),
  Username: Cookies.get("userName"),
};

/**
 * 顶部汇总(当时耗时、流程状态、发起时间)
 * @param {string} processNumber
 */
export function getInstanceEfficiencySummary(processNumber) {
  return http.get(`${baseUrl}/processInstanceEfficiency/summary`, {
    params: { processNumber },
    headers,
  });
}

/**
 * 节点列表(含耗时、退回标识、进行中标识、TOP3 排名)
 * @param {string} processNumber
 */
export function getInstanceEfficiencyNodes(processNumber) {
  return http.get(`${baseUrl}/processInstanceEfficiency/nodes`, {
    params: { processNumber },
    headers,
  });
}

/**
 * 节点详情(最后一轮人员明细 + 签署信息)
 * @param {string} processNumber
 * @param {string} taskDefKey
 */
export function getInstanceEfficiencyNodeDetail(processNumber, taskDefKey) {
  return http.get(`${baseUrl}/processInstanceEfficiency/nodeDetail`, {
    params: { processNumber, taskDefKey },
    headers,
  });
}
