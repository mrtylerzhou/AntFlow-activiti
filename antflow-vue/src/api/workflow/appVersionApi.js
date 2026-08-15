/*
 * App 版本管理 相关接口
 */
import http from "@/utils/axios";
import Cookies from "js-cookie";
let baseUrl = import.meta.env.VITE_APP_BASE_API;
const headers = {
  Userid: Cookies.get("userId"),
  Username: Cookies.get("userName"),
};

/**
 * 版本分页列表
 * @param {*} pageDto {page,pageSize}
 * @param {*} query {version}
 */
export function getVersionListPage(pageDto, query) {
  return http.get(`${baseUrl}/appVersion/versionList`, {
    params: {
      page: pageDto?.page,
      pageSize: pageDto?.pageSize,
      version: query?.version || undefined,
    },
    headers,
  });
}

/**
 * 保存版本(新增/草稿编辑, 新增时传 inheritFromLast)
 * @param {*} data SysVersionVo
 */
export function saveVersion(data) {
  return http.post(`${baseUrl}/appVersion/save`, data, { headers });
}

/**
 * 更新版本基本信息(草稿全量/已发布仅运营参数, 由后端白名单控制)
 * @param {*} id
 * @param {*} data SysVersionVo
 */
export function updateVersion(id, data) {
  return http.post(`${baseUrl}/appVersion/${id}`, data, { headers });
}

/**
 * 发布草稿版本
 * @param {*} id
 */
export function publishVersion(id) {
  return http.post(`${baseUrl}/appVersion/publish/${id}`, {}, { headers });
}

/**
 * 删除草稿版本(级联清理关联数据)
 * @param {*} id
 */
export function deleteVersion(id) {
  return http.post(`${baseUrl}/appVersion/delete/${id}`, {}, { headers });
}

/**
 * 候选对象列表(type: 1图标应用 2上线流程 3快捷入口)
 * @param {*} type
 * @param {*} search 名称关键字(可选)
 */
export function getCandidates(type, search) {
  return http.get(`${baseUrl}/appVersion/candidates`, {
    params: { type, search: search || undefined },
    headers,
  });
}

/**
 * 查询版本已关联数据(按sort排序)
 * @param {*} versionId
 * @param {*} type 1图标应用 2上线流程 3快捷入口
 */
export function getAppDatas(versionId, type) {
  return http.get(`${baseUrl}/appVersion/appDatas`, {
    params: { versionId, type },
    headers,
  });
}

/**
 * 保存版本关联数据(全量替换, 仅草稿可用)
 * @param {*} data {versionId,type,items:[{id,name,sort}]}
 */
export function saveAppDatas(data) {
  return http.post(`${baseUrl}/appVersion/saveAppDatas`, data, { headers });
}

/**
 * App下载二维码
 */
export function getQrCode() {
  return http.get(`${baseUrl}/appVersion/getQrCode`, { headers });
}
