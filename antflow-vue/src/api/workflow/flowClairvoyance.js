import http from "@/utils/axios";
import Cookies from "js-cookie";
let baseUrl = import.meta.env.VITE_APP_BASE_API;
const headers = {
  Userid: Cookies.get("userId"),
  Username: Cookies.get("userName"),
};

/**
 * 流程千里眼 - 分批搜索
 * @param {Object} data { userIds, timeRange, nodeScope, offset }
 */
export function flowClairvoyanceSearch(data) {
  return http.post(`${baseUrl}/flowClairvoyance/search`, data, { headers });
}
