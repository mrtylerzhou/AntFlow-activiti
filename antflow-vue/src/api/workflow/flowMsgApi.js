/*
 * @Date:  2025-07-03 17:06:59
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2025-07-03 17:06:59
 * @FilePath: src\api\flowMsgApi.js
 * 流程通知 相关接口
 */
import http from "@/utils/axios";
import Cookies from "js-cookie";
let baseUrl = import.meta.env.VITE_APP_BASE_API;
const headers = {
  Userid: Cookies.get("userId"),
  Username: Cookies.get("userName"),
};
/**
 * 获取消息模板列表
 * @returns
 */
export function getFlowMsgTempleteList(pageDto, taskMgmtVO) {
  let paramDto = {
    pageDto: pageDto,
    entity: taskMgmtVO,
  };
  return http.post(`${baseUrl}/informationTemplates/listPage`, paramDto, {
    headers,
  });
}
