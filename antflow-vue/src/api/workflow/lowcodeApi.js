/*
 * @Date:  2024-05-25 14:06:59
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2023-03-29 15:52:57
 * @FilePath: src\api\lowcodeApi.js
 * 低代码表单模块 相关接口
 */
import http from "@/utils/axios";
import Cookies from "js-cookie";
let baseUrl = import.meta.env.VITE_APP_BASE_API;
const headers = {
  Userid: Cookies.get("userId"),
  Username: Cookies.get("userName"),
};

/**
 * Obsoleted
 * 获取全部 LF FormCodes 在流程设计时选择使用
 * @param { String } formCode
 * @returns
 */
export function getLowCodeFlowFormCodes() {
  return http.get(`${baseUrl}/lowcode/getLowCodeFlowFormCodes`, { headers });
}
/**
 * 获取LF FormCode Page List 模板列表使用
 * @returns
 */
export function getLFFormCodePageList(pageDto, taskMgmtVO) {
  let paramDto = {
    pageDto: pageDto,
    taskMgmtVO: taskMgmtVO,
  };
  return http.post(`${baseUrl}/lowcode/getLFFormCodePageList`, paramDto, {
    headers,
  });
}
/**
 * 获取 已设计流程并且启用的 LF FormCode Page List 发起页面使用
 * @returns
 */
export function getLFActiveFormCodePageList(pageDto, taskMgmtVO) {
  let paramDto = {
    pageDto: pageDto,
    taskMgmtVO: taskMgmtVO,
  };
  return http.post(`${baseUrl}/lowcode/getLFActiveFormCodePageList`, paramDto, {
    headers,
  });
}

/**
 * 获取低代码表单根据FromCode
 * @param { String } formCode
 * @returns
 */
export function getLowCodeFromCodeData(formCode) {
  return http.get(
    `${baseUrl}/lowcode/getformDataByFormCode?formCode=${formCode}`,
    { headers }
  );
}

/**
 * 发起流程页: 根据 formCode 获取表单数据(兼容内联/外部表单模式)
 * @param { String } formCode 流程表单代码
 * @returns { useExternalForm, lfFormData, lfFormdataList }
 */
export function getStartFormData(formCode) {
  return http.get(
    `${baseUrl}/lowcode/getStartFormData?formCode=${formCode}`,
    { headers }
  );
}

/**
 * 新增低代码表单FormCode
 * @param {*} data
 * @returns
 */
export function createLFFormCode(data) {
  return http.post(`${baseUrl}/lowcode/createLowCodeFormCode`, data, {
    headers,
  });
}

/**
 * 新增 page-added DIY FormCode(LF 后端 + 自定义 Vue 前端)
 * @param {*} data
 * @returns
 */
export function createDIYFormCode(data) {
  return http.post(`${baseUrl}/lowcode/createDIYFormCode`, data, { headers });
}

/**
 * 获取 page-added DIY FormCode Page List 模板列表使用
 * @returns
 */
export function getDIYFormCodePageList(pageDto, taskMgmtVO) {
  let paramDto = { pageDto: pageDto, taskMgmtVO: taskMgmtVO };
  return http.post(`${baseUrl}/lowcode/getDIYFormCodePageList`, paramDto, { headers });
}

// ===================== 独立表单管理 =====================

/**
 * 分页查询独立表单（家族分组，每族一行生效版本）
 * @param {Object} pageDto - { page, pageSize }
 * @param {Object} vo - { search }
 * @returns
 */
export function listFormPage(pageDto, vo) {
  let paramDto = {
    pageDto: pageDto,
    taskMgmtVO: { search: vo?.search },
  };
  return http.post(`${baseUrl}/lowcode/form/listPage`, paramDto, { headers });
}

/**
 * 按 id 查询表单版本（编辑回显 / 查看版本）
 * @param {Number} id
 * @returns
 */
export function getFormById(id) {
  return http.get(`${baseUrl}/lowcode/form/${id}`, { headers });
}

/**
 * 保存表单：无 formCode => 新建家族+首版本；有 formCode => 新建版本
 * @param {Object} data - { formCode, formName, formdata }
 * @returns
 */
export function saveForm(data) {
  return http.post(`${baseUrl}/lowcode/form/save`, data, { headers });
}

/**
 * 软删除单个版本（被生效流程引用时拒绝）
 * @param {Number} id
 * @returns
 */
export function deleteForm(id) {
  return http.delete(`${baseUrl}/lowcode/form/${id}`, { headers });
}

/**
 * 查询某家族所有版本（历史版本查看）
 * @param {String} formCode
 * @returns
 */
export function listFormHistory(formCode) {
  return http.get(`${baseUrl}/lowcode/form/history?formCode=${formCode}`, { headers });
}

/**
 * 列出所有生效独立表单（流程设计多选下拉框）
 * @returns
 */
export function listEffectiveForSelect() {
  return http.get(`${baseUrl}/lowcode/form/listEffectiveForSelect`, { headers });
}

/**
 * 生效指定表单版本（同族其他版本自动置为非生效）
 * @param {Number} id
 * @returns
 */
export function effectiveForm(id) {
  return http.put(`${baseUrl}/lowcode/form/effective/${id}`, { headers });
}

/**
 * 查询引用了指定表单版本的流程配置列表（查看引用）
 * @param {Number} formdataId
 * @returns
 */
export function listFormReferences(formdataId) {
  return http.get(`${baseUrl}/lowcode/form/references/${formdataId}`, { headers });
}
