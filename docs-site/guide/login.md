# 登录与用户切换

## 登录页

AntFlow 演示环境使用 mock 登录机制,登录接口读取 `public/mock/login.json` 静态文件,返回固定的 token。因此**任何用户名和密码都能登录成功**。

![登录页](/images/login.png)

登录页主要元素:
- **系统标题**:流程后台管理系统
- **用户名输入框**:支持模糊搜索选择(下拉来自 `t_user` 表)
- **密码输入框**:任意输入即可
- **登录按钮**:提交登录

## 登录流程

### 前端流程

```
用户输入 → userStore.login() → GET /mock/login.json → 获取 token
         → setToken(token) 写入 Cookie("Admin-Token")
         → 跳转首页 /index
         → userStore.getInfo() → GET /mock/userinfo.json → 获取用户信息
```

源码位置:[antflow-vue/src/store/modules/user.js](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/store/modules/user.js)

### Token 存储

Token 存储在 Cookie 中,key 为 `Admin-Token`:

```javascript
// antflow-vue/src/utils/auth.js
const TokenKey = 'Admin-Token'
export function setToken(token) {
  return Cookies.set(TokenKey, token)
}
```

### 用户信息

登录后,前端调用 `GET /mock/userinfo.json` 获取用户信息,包括:
- `user`:用户基本信息(userId、userName、nickName、avatar)
- `roles`:角色列表
- `permissions`:权限列表

源码位置:[antflow-vue/public/mock/userinfo.json](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/public/mock/userinfo.json)

## 测试账号

执行 `script/t_demo_init.db.sql` 后,系统内置 10 个测试用户:

| ID | 用户名 | 说明 |
|---|---|---|
| 1 | 张三 | 常用测试用户 |
| 2 | 李四 | 常用测试用户 |
| 3 | 王五 | 测试用户 |
| 4 | 菜六 | 测试用户 |
| 5 | 赵七 | 测试用户 |
| 6 | 孙八 | 测试用户 |
| 7 | 周九 | 测试用户 |
| 8 | 吴十 | 测试用户 |
| 9 | 郑十一 | 测试用户 |
| 10 | 王十二 | 测试用户 |

每个用户都有对应的:
- `leader_id`:直属领导(层层审批用)
- `hrbp_id`:HRBP(HRBP 审批用)
- `department_id`:部门(部门负责人审批用)
- `path`:组织线路径(层层审批用)

## 切换用户

### 方式一:退出重新登录

1. 点击右上角用户头像
2. 选择"退出登录"
3. 重新用其他用户名登录

### 方式二:直接修改 Cookie(开发调试用)

在浏览器开发者工具(F12)中,修改 Cookie:
- `Admin-Token`:保持不变(mock 模式下 token 固定)
- `Userid`:修改为目标用户 ID
- `Username`:修改为目标用户名

然后刷新页面即可切换用户。

::: warning 生产环境
生产环境应实现真实的登录接口,替换 mock。后端通过 `SecurityUtils.getLogInEmpId()` 获取当前登录用户 ID,该值来自 `ThreadLocalContainer.get("currentuser")`,需要通过过滤器或拦截器设置。
:::

## 后端用户体系

AntFlow 完全接管了 Activiti 的用户系统,通过 `AfUserService` 接口对接外部用户系统:

```java
// antflow-base/src/main/java/org/openoa/base/service/AfUserService.java
public interface AfUserService {
    List<BaseIdTranStruVo> queryByNameFuzzy(String userName);
    List<BaseIdTranStruVo> queryUserByIds(Collection<String> userIds);
    BaseIdTranStruVo getById(String id);
    List<BaseIdTranStruVo> queryLeadersByEmployeeIdAndTier(String employeeId, Integer tier);
    // ... 更多方法
}
```

默认实现 `UserServiceImpl` 查询 `t_user` 表。接入自有系统时,实现此接口并标注 `@Primary` 即可。

详见 [扩展审批人来源](/dev-guide/extend-approver)。
