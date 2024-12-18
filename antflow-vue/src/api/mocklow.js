/*
 * @Date:  2024-05-25 14:06:59
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2023-03-29 15:52:57
 * @FilePath: src\api\mockflow.js
 */
import http from '@/utils/axios';
import cache from '@/plugins/cache';
let baseUrl = import.meta.env.VITE_APP_BASE_API
const headers = {
  "Userid": cache.session.get('userId'),
  "Username": cache.session.get('userName')
}  
/**
 * 获取低代码表单根据FromCode
 * @param { String } formCode 
 * @returns 
 */
export function getLowCodeFromCodeData(formCode) {
  return http.get(`${baseUrl}/lowcode/getformDataByFormCode?formCode=${formCode}`, { headers })
} 

/**
 * 获取所有低代码的FromCode
 * @param { String } formCode 
 * @returns 
 */
export function getLowCodeFlowFormCodes() {
  return http.get(`${baseUrl}/lowcode/getLowCodeFlowFormCodes`, { headers })
} 
/**
 * 新增低代码表单FormCode
 * @param {*} data 
 * @returns 
 */
export function createLFFormCode(data){
  return http.post(`${baseUrl}/lowcode/createLowCodeFormCode`, data, { headers })
}
