import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'AntFlow',
  description: 'AntFlow 企业级低代码工作流引擎 · 完整功能文档',
  lang: 'zh-CN',
  lastUpdated: true,
  cleanUrls: true,

  head: [
    ['meta', { name: 'theme-color', content: '#3aa675' }],
    ['meta', { property: 'og:title', content: 'AntFlow 文档' }],
    ['meta', { property: 'og:description', content: '开源企业级低代码工作流引擎' }]
  ],

  themeConfig: {
    siteTitle: 'AntFlow 文档',

    logo: '/logo.png',

    nav: [
      { text: '首页', link: '/' },
      { text: '入门', link: '/guide/introduction' },
      {
        text: '用户手册',
        items: [
          { text: '流程设计', link: '/workflow-design/flow-category' },
          { text: '流程运行', link: '/workflow-run/start-flow' }
        ]
      },
      {
        text: '开发指南',
        items: [
          { text: '架构总览', link: '/dev-guide/architecture' },
          { text: 'Adaptor 模式', link: '/dev-guide/adaptor-pattern' },
          { text: 'REST API', link: '/dev-guide/rest-api' },
          { text: '数据库设计', link: '/dev-guide/db-design' }
        ]
      },
      {
        text: '更多',
        items: [
          { text: '低代码专题', link: '/lowcode/lowcode-overview' },
          { text: '运维部署', link: '/ops/deploy' },
          { text: '术语表', link: '/reference/glossary' },
          { text: 'FAQ', link: '/reference/faq' }
        ]
      },
      {
        text: '仓库',
        items: [
          { text: 'Gitee', link: 'https://gitee.com/tylerzhou/Antflow' },
          { text: 'GitHub', link: 'https://github.com/mrtylerzhou/AntFlow' }
        ]
      }
    ],

    sidebar: {
      '/guide/': [
        {
          text: '入门指南',
          collapsible: true,
          collapsed: false,
          items: [
            { text: '什么是 AntFlow', link: '/guide/introduction' },
            { text: '快速开始', link: '/guide/quick-start' },
            { text: '登录与用户切换', link: '/guide/login' },
            { text: '系统总览', link: '/guide/overview' }
          ]
        }
      ],

      '/workflow-design/': [
        {
          text: '流程设计',
          collapsible: true,
          collapsed: false,
          items: [
            { text: '流程分类管理', link: '/workflow-design/flow-category' },
            { text: '流程设计器', link: '/workflow-design/flow-designer' },
            { text: '低代码表单设计', link: '/workflow-design/form-design' },
            { text: '节点类型详解', link: '/workflow-design/node-types' },
            { text: '审批人规则', link: '/workflow-design/approver-rules' },
            { text: '条件规则', link: '/workflow-design/condition-rules' },
            { text: '版本管理与启动', link: '/workflow-design/version-management' }
          ]
        }
      ],

      '/workflow-run/': [
        {
          text: '流程运行',
          collapsible: true,
          collapsed: false,
          items: [
            { text: '发起流程', link: '/workflow-run/start-flow' },
            { text: '我的待办', link: '/workflow-run/my-tasks' },
            { text: '审批操作', link: '/workflow-run/approve' },
            { text: '流程预览', link: '/workflow-run/flow-preview' },
            { text: '流程消息', link: '/workflow-run/flow-msg' }
          ]
        }
      ],

      '/dev-guide/': [
        {
          text: '开发指南',
          collapsible: true,
          collapsed: false,
          items: [
            { text: '架构总览', link: '/dev-guide/architecture' },
            { text: 'Adaptor 适配器模式', link: '/dev-guide/adaptor-pattern' },
            { text: '虚拟节点系统', link: '/dev-guide/vnode-system' },
            { text: '流程流转控制', link: '/dev-guide/flow-control' },
            { text: 'REST API 参考', link: '/dev-guide/rest-api' },
            { text: '数据库设计', link: '/dev-guide/db-design' },
            { text: '扩展审批人来源', link: '/dev-guide/extend-approver' },
            { text: '扩展条件规则', link: '/dev-guide/extend-condition' },
            { text: '扩展通知渠道', link: '/dev-guide/extend-notice' },
            { text: '集成现有系统', link: '/dev-guide/integrate-existing' }
          ]
        }
      ],

      '/lowcode/': [
        {
          text: '低代码专题',
          collapsible: true,
          collapsed: false,
          items: [
            { text: '低代码流程总览', link: '/lowcode/lowcode-overview' },
            { text: '低代码表单引擎', link: '/lowcode/lowcode-form' },
            { text: '低代码 vs 自定义表单', link: '/lowcode/lowcode-vs-diy' }
          ]
        }
      ],

      '/ops/': [
        {
          text: '运维与部署',
          collapsible: true,
          collapsed: false,
          items: [
            { text: '生产部署', link: '/ops/deploy' },
            { text: '多数据库支持', link: '/ops/db-multi' },
            { text: '常见问题排查', link: '/ops/troubleshooting' },
            { text: '性能优化', link: '/ops/performance' }
          ]
        }
      ],

      '/reference/': [
        {
          text: '参考',
          collapsible: true,
          collapsed: false,
          items: [
            { text: '术语表', link: '/reference/glossary' },
            { text: 'FAQ', link: '/reference/faq' },
            { text: '版本变更', link: '/reference/changelog' }
          ]
        }
      ]
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/mrtylerzhou/AntFlow' }
    ],

    search: {
      provider: 'local',
      options: {
        translations: {
          button: {
            buttonText: '搜索文档',
            buttonAriaLabel: '搜索文档'
          },
          modal: {
            noResultsText: '无法找到相关结果',
            resetButtonTitle: '清除查询条件',
            footer: {
              selectText: '选择',
              navigateText: '切换'
            }
          }
        }
      }
    },

    outline: {
      level: [2, 3],
      label: '本页目录'
    },

    docFooter: {
      prev: '上一页',
      next: '下一页'
    },

    lastUpdatedText: '最后更新于',

    returnToTopLabel: '回到顶部',
    sidebarMenuLabel: '菜单',

    darkModeSwitchLabel: '主题',
    lightModeSwitchTitle: '切换到浅色模式',
    darkModeSwitchTitle: '切换到深色模式'
  }
})
