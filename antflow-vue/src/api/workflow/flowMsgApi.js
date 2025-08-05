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
 * 获取所有通知类型
 * @returns
 */
export function getAllNoticeTypes() {
  return http.get(`${baseUrl}/informationTemplates/getAllNoticeTypes`, {
    headers,
  });
}
/**
 * 新增消息模板
 * @param {*} data
 * @returns
 */
export function saveInformationTemp(data) {
  return http.post(`${baseUrl}/informationTemplates/save`, data, {
    headers,
  });
}
/**
 * 获取消息模板详情
 * @returns object
 */
export function getInformationTemplateById(id) {
  return http.get(
    `${baseUrl}/informationTemplates/getInformationTemplateById?templateId=${id}`,
    {
      headers,
    }
  );
}
/**
 * 编辑消息模板
 * @param {*} data
 * @returns
 */
export function editInformationTemp(data) {
  return http.post(`${baseUrl}/informationTemplates/updateById`, data, {
    headers,
  });
}

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
/**
 * 获取通配符
 * @returns
 */
export function getWildcardCharacter() {
  return http.get(`${baseUrl}/informationTemplates/getWildcardCharacter`, {
    headers,
  });
}
/**
 * 获取事件列表接口
 * @returns
 */
export function getProcessEvents() {
  return http.get(`${baseUrl}/informationTemplates/getProcessEvents`, {
    headers,
  });
}

/**
 * 根据FormCode获取通知类型
 * @returns
 */
export function getNoticeTypeByFormCode(formCode) {
  return http.get(
    `${baseUrl}/informationTemplates/getNoticeTypeByFormCode?formCode=${formCode}`,
    {
      headers,
    }
  );
}
/**
 * 保存消息通知
 * @param {*} data
 * @returns
 */
export function saveTaskMgmt(data) {
  return http.post(`${baseUrl}/taskMgmt/taskMgmt`, data, {
    headers,
  });
}
