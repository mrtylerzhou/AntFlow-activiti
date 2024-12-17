<template>
  <div class="app-container home"> 
    <el-row :gutter="20">
      <el-col :sm="24" :lg="24"><shortcuts /></el-col> 
    </el-row>
    <el-divider />
    <el-row :gutter="20"> 
      <el-col :sm="24" :lg="12" style="padding-left: 20px">
        <el-button
            type="success"
            icon="Cloudy"
            plain
            @click="goTarget('https://gitee.com/tylerzhou/Antflow/blob/master/antflow-vue/public/docs/%E7%B3%BB%E7%BB%9F%E6%93%8D%E4%BD%9C%E6%89%8B%E5%86%8C-%E4%BD%8E%E4%BB%A3%E7%A0%81%E7%89%88%E6%9C%AC.md')"
            >操作手册【低代码表单】</el-button
          >     
          <el-button
            type="success"
            icon="Cloudy"
            plain
            @click="goTarget('https://gitee.com/tylerzhou/Antflow/blob/master/antflow-vue/public/docs/%E7%B3%BB%E7%BB%9F%E6%93%8D%E4%BD%9C%E6%89%8B%E5%86%8C-%E8%87%AA%E5%AE%9A%E4%B9%89%E8%A1%A8%E5%8D%95.md')"
            >操作手册【自定义表单】</el-button
          >     
        <h2>流程后台管理框架</h2>
        <p>
          一直想做一款后台管理系统，看了很多优秀的开源项目但是发现没有合适自己的。于是利用空闲休息时间开始自己写一套后台系统。如此有了流程管理系统，她可以用于所有的Web应用程序，如网站管理后台，网站会员中心，CMS，CRM，OA等等，当然，您也可以对她进行深度定制，以做出更强系统。所有前端后台代码封装过后十分精简易上手，出错概率低。同时支持移动客户端访问。系统会陆续更新一些实用功能。
        </p>
        <p>
          <b>当前版本:</b> <span>v{{ version }}</span>
        </p>
        <p>
          <el-tag type="danger">&yen;免费开源</el-tag>
        </p>
        <p>
          <el-button
            type="primary"
            icon="Cloudy"
            plain
            @click="goTarget('https://gitee.com/ldhnet')"
            >访问码云</el-button
          >
          <el-button
            icon="HomeFilled"
            plain
            @click="goTarget('https://gitee.com/ldhnet')"
            >访问主页</el-button
          >
         
        </p> 
      </el-col>

      <el-col :sm="24" :lg="12" style="padding-left: 50px">
        <el-row>
          <el-col :span="12">
            <h2>技术选型</h2>
          </el-col>
        </el-row>
        <el-row> 
          <el-col :span="6">
            <h4>前端技术</h4>
            <ul>
              <li>Vue</li>
              <li>Vuex</li>
              <li>Element-ui</li>
              <li>Axios</li>
              <li>Sass</li>
              <li>Quill</li>
              <li>借鉴RuoYi</li>
              <li>...</li>
            </ul>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    <el-divider />
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <template v-slot:header>
            <div class="clearfix">
              <span>联系信息</span>
            </div>
          </template>
          <div class="body">
            <p>
              <i class="el-icon-s-promotion"></i> 官网：<el-link
                href="http://117.72.70.166/admin/"
                target="_blank"
                >http://117.72.70.166/admin/</el-link
              >
            </p>
            <p>
              <i class="el-icon-user-solid"></i> QQ群：
               <a>972107977</a>
            </p> 
            <p>
              <i class="el-icon-user-solid"></i> 邮箱：
               <a>574427343@qq.com</a>
            </p> 
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <template v-slot:header>
            <div class="clearfix">
              <span>更新日志</span>
            </div>
          </template>
          <el-collapse accordion>
            <el-collapse-item title="v4.0.0 - 2024-12-14">
              <ol> 
                <li>低代码表单接入</li> 
              </ol>
            </el-collapse-item>
            <el-collapse-item title="v3.1.0 - 2024-10-17">
              <ol> 
                <li>三方业务表单接入</li>
                <li>三方业务方注册，应用注册</li>
                <li>三方业务流程设计</li>
              </ol>
            </el-collapse-item>
            <el-collapse-item title="v3.0.0 - 2024-08-10">
              <ol> 
                <li>其他细节优化</li>
              </ol>
            </el-collapse-item>
            <el-collapse-item title="v2.0.0 - 2024-08-08">
              <ol>
                <li>Mock Data</li> 
                <li>其他细节优化</li>
              </ol>
            </el-collapse-item> 
            <el-collapse-item title="v1.0.0 - 2024-08-01">
              <ol>
                <li>防RuoYi-Vue版前端正式发布</li>
              </ol>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col> 
    </el-row>
  </div>
</template>

<script setup name="Index">
import shortcuts from "@/components/Dashboard/Shortcuts";  
import cache from '@/plugins/cache'; 
import { approveList } from '@/utils/flow/const';
const version = ref('3.5.0')  
  
console.log("VITE_APP_BASE_API=======",import.meta.env.BASE_URL);

onMounted(() => {
    let userId = cache.session.get('userId'); 
    let name = approveList[userId];  
})
function goTarget(url) {
  window.open(url, '__blank')
}
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }
  .item {
    margin-top: 10px;
    margin-right: 10px;
  }
  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>

