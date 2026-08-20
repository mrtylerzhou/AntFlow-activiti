/*
 * 字典管理 相关接口
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
 * @param {*} query {dictType, keyword}
 */
export function getDictDataListPage(pageDto, query) {
  let data = {
    pageDto: pageDto,
    dictType: query?.dictType,
    keyword: query?.keyword,
  };
  return http.post(`${baseUrl}/dictData/listPage`, data, { headers });
}

/**
 * 新增
 * @param {*} data {dictLabel, dictValue, dictType, sort, remark}
 */
export function saveDictData(data) {
  return http.post(`${baseUrl}/dictData/save`, data, { headers });
}

/**
 * 编辑
 * @param {*} data {id, dictLabel, dictValue, dictType, sort, remark}
 */
export function updateDictData(data) {
  return http.post(`${baseUrl}/dictData/update`, data, { headers });
}

/**
 * 删除(后端校验 lowcodeflow 禁止删除)
 * @param {*} id
 */
export function deleteDictData(id) {
  return http.get(`${baseUrl}/dictData/delete/${id}`, { headers });
}
