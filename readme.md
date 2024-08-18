### English | [简体中文](./README.zh_CN.md) |

### Basic Introduction:

AntFlow is an open-source OA (Office Automation) approval workflow system inspired by Dingtalk's workflow design. Its interface is very simple,you only need some clicks to configure a workflow. It aims to solve the high usage threshold and complexity of traditional BPMN.js process designers, making it easy for non-technical users to get started and quickly configure daily approval processes.

> Use Case 1: In a large enterprise with thousands of employees, heavily relying on workflows (even getting a notebook or a pen requires a workflow), a single process operator can solve most problems without needing backend development intervention.



### Core Technology Stack:

* **Java 8-21:** the main branch is java 8 version,if you use a newer java version,please check out the `java17_support` branch
* **Activiti 5.23:**
* **Spring Boot 2.7.17:**
* **MybatisPlus 3.5.1:** MybatisPlus is a famous orm tool on top of another famous orm framework:mybatis.
* **MySQL 5.7+:**

### core features

1. **easy to config** it has a very simple an intuitive workflow configuration interface.It simplifies the approach to design a flow diagram.You only need some clicks and drags to configure an approval flow.You do not need to write expressions,scripts snippets and variables.
2. **super easy to develop new workflow process** AntFlow decoupled the process engine and business logic.If you choose to use it,you only need to implement a single predefined interface.
3. **Process Observability:** AntFlow offers an admin preview interface for debugging, allowing administrators to quickly identify issues by previewing the approval path based on user-initiated conditions.

### a glimpse of its features

![1.png](./doc/images/1.png)

add approver or conditions node
![](./doc/images/4.png)
conditions attr panel
![](./doc/images/2.png)
approver node attr panel
![](./doc/images/搜狗截图20240818082058.png)
validation
![](./doc/images/3.png)
admin pages
![](./doc/images/QQ20240818-082212.png)