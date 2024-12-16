/*
 * @Date:  2024-10-10 20:00:00
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2024-10-10 20:00:00
 * @FilePath: src\api\mockoutsideflow.js
 */
 
import http from '@/utils/axios' 
let baseUrl = import.meta.env.BASE_URL
 /**
 * 获取审批mock数据
 * @param {*} data 
 * @returns 
 */
export function getMockWorkFlowData(data) {
  return http.get(`${baseUrl}mockoutside/datashow.json`, { params: data })
}

/**
 * 获取职员
 * @param {*} data 
 * @returns 
 */
export function getEmployees(data) {
  return http.get(`${baseUrl}mockoutside/users.json`, { params: data })
}

/**
 * 获取条件字段
 * @param {*} data 
 * @returns 
 */
export function getConditions(data) {
  return http.get(`${baseUrl}mockoutside/conditions.json`, { params: data })
}