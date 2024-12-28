export default {
    "code": "200",
    "msg": "success",
    "data": {
        "tableId": 1,//审批id
        "workFlowDef": {
            "name": "合同审批",//审批名称
        },
        "directorMaxLevel": 4,//审批主管最大层级
        "flowPermission": [],//发起人
        "nodeConfig": {
            "nodeName": "发起人",//节点名称
            "type": 0,// 0 发起人 1审批 2抄送 3条件 4路由
            "priorityLevel": "",// 条件优先级
            "setType": "",// 审批人设置 1指定成员 2主管 4发起人自选 5发起人自己 7连续多级主管
             //审批人数 1选一个人 2选多个人
             //选择范围 1.全公司 2指定成员 2指定角色
            "directorLevel": "", //审批终点   第n层主管
            "signType": "", //多人审批时采用的审批方式 1依次审批 2会签
            "noHeaderAction": "",//审批人为空时 1自动审批通过/不允许发起 2转交给审核管理员
 
            "ccSelfSelectFlag": "", //允许发起人自选抄送人
            "conditionList": [], //当审批单同时满足以下条件时进入此流程
            "nodeApproveList": [], //操作人
            "childNode": {
                "nodeName": "审核人",
                "type": 1,
                "setType": 2,
                "directorLevel": 1,
                "signType": 1,
                "noHeaderAction": 2, 
                "childNode": {
                    "nodeName": "路由",
                    "type": 4,
                    "priorityLevel": 1,
                    "setType": 1,
                    "directorLevel": 1,
                    "signType": 1,
                    "noHeaderAction": 2,                    
                    "ccSelfSelectFlag": 1,
                    "conditionList": [],
                    "nodeApproveList": [],
                    "childNode": {
                        "nodeName": "抄送人",
                        "type": 2,
                        "ccSelfSelectFlag": 1,
                        "childNode": null,
                        "nodeApproveList": [],                        
                    },
                    "conditionNodes": [{ //条件节点
                        "nodeName": "条件1",
                        "type": 3,
                        "priorityLevel": 1, 
                        "setType": 1,         
                        "directorLevel": 1,
                        "signType": 1,
                        "noHeaderAction": 2,                        
                        "ccSelfSelectFlag": 1,
                        "isDefault": 0,
                        "conditionList": [{ //当前条件
                            "formId": 0, //条件表单的唯一Id
                            "columnId": 0, //用于条件判断的字段Id，与后端对应
                            "type": 1, //1 发起人 2其他
                            "optType": "", //["", "<", ">", "≤", "=", "≥","介于两数之间"][optType]
                            "zdy1": "",//左侧自定义内容
                            "zdy2": "",//右侧自定义内容
                            "opt1": "",//左侧符号 < ≤
                            "opt2": "",//右侧符号 < ≤
                            "columnDbname": "",//条件字段名称
                            "columnType": "",//条件字段类型
                            "showType": "",//3多选 其他
                            "showName": "",//展示名
                            "fixedDownBoxValue": ""//多选数组
                        }],
                        "nodeApproveList": [{
                            "targetId": 85,
                            "type": 1,
                            "name": "天旭"
                        }],
                        "childNode": {
                            "nodeName": "审核人",
                            "type": 1,
                            "priorityLevel": 1,
                            "setType": 5,   //指定审批人类型，5-指定人员，4-指定角色，3-指定层级审批 ，13-直属领导，12-发起人自己，6-HRBP
                            "directorLevel": 1, //当 setType= 3时，代表指定第几级领导审批
                            "signType": 1,
                            "noHeaderAction": 2,
                            "ccSelfSelectFlag": 1,
                            "conditionList": [],
                            "nodeApproveList": [{
                                "type": 5, //指定审批人类型，5-指定人员，4-指定角色，3-指定层级审批 ，13-直属领导，12-发起人自己，6-HRBP
                                "targetId": 2515744, 
                                "name": "哈哈哈哈"
                            }],
                            "childNode": null,
                            "conditionNodes": [],
                            
                        },
                        "conditionNodes": [],
                        
                    }, {
                        "nodeName": "条件2",
                        "type": 3,
                        "priorityLevel": 2,
                        "setType": 1,
                        "directorLevel": 1,
                        "signType": 1,
                        "noHeaderAction": 2,
                        "ccSelfSelectFlag": 1,
                        "conditionList": [],
                        "nodeApproveList": [],
                        "childNode": null,
                        "conditionNodes": [],
                        "isDefault": 1,
                    }]
                },
                "nodeApproveList": []
            },
            "conditionNodes": []
        }
    }
}