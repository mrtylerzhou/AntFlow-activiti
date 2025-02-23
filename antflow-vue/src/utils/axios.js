/*
 * @Date:  2024-05-25 14:43:53
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2022-09-21 14:37:02
 * @FilePath: /ant-flow/src/plugins/axios.js
 */
"use strict";

import axios from "axios";   
import useUserStore from '@/store/modules/user'
import cache from '@/plugins/cache';

let config = {
  // baseURL: process.env.baseURL || process.env.apiUrl || ""
  timeout: 60 * 1000, // Timeout
  withCredentials: true, // Check cross-site Access-Control 
  headers: { 
    'X-Custom-Header': 'foobar',
    'Content-Type': 'application/json;charset=utf-8',
  }
};

const _axios = axios.create(config);

_axios.interceptors.request.use(
  function (config) {
    config.headers = config.headers || {};   
    return config;
  },
  function (error) { 
    return Promise.reject(error);
  }
);
 
_axios.interceptors.response.use(
  function (response) {  

    let Userid= cache.session.get('userId')
    let Username = cache.session.get('userName') 
    if (!Userid || !Username) {
      useUserStore().logOut().then(() => {
        location.href = '/admin/';//index
      })
    } 
    return response.data;
  },
  function (error) { 
    //console.log('res=======error=========',JSON.stringify(error))
    // let { message,status,code,name } = error;
    // if (status == 500) {
    //   
    // }
    
    // useUserStore().logOut().then(() => {
    //   location.href = '/low/';//index
    // })
    return Promise.reject(error);
  }
);

export default _axios;
