 
import http from '@/utils/axios' 
let baseUrl = import.meta.env.BASE_URL

/**
 * 获取版本信息
 */
export function getCurrentVersion() {
  const headers = {
    'Cache-Control': 'no-cache', 
  }
  return http.get(`${baseUrl}version.json`, { headers })
} 
 