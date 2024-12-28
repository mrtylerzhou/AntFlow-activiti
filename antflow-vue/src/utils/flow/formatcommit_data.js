// import { FormatUtils } from '@/utils/flow/formatcommit_data'
//import { NodeUtils } from '@/utils/flow/nodeUtils' 
const isEmpty = data => data === null || data === undefined || data === ''
const isEmptyArray = data => Array.isArray(data) ? data.length === 0 : true

export class FormatUtils {
    /**
     * 对基础设置,高级设置等设置页内容进行格式化
     * @param params
     */
    static formatSettings(param) {

        let treeList = this.flattenMapTreeToList(param);
        let combinationList = this.getEndpointNodeId(treeList);
        let finalList = this.cleanNodeList(combinationList);
        let fomatList = this.adapterActivitiNodeList(finalList);
        return fomatList; 
    }
    /**
     * 展平树结构
     * @param {Object} treeData  - 节点数据 
     * @returns Array - 节点数组
     */
    static flattenMapTreeToList(treeData) {
        let nodeData = [];

        function traverse(node) {
            if (!node && !node.hasOwnProperty('nodeType')) {
                return nodeData;
            }
            if (node.nodeType == 2) {
                if (node.childNode) {
                    node.childNode.nodeFrom = node.nodeId;
                    traverse(node.childNode);
                }
                if (!isEmptyArray(node.conditionNodes)) {
                    for (const child of node.conditionNodes) {
                        child.nodeFrom = node.nodeId;
                        traverse(child);
                    }
                    node.nodeTo = node.conditionNodes.map(item => item.nodeId);
                    delete node.conditionNodes
                }
            } else if (node.childNode) {
                node.nodeTo = [node.childNode.nodeId];
                node.childNode.nodeFrom = node.nodeId;
                traverse(node.childNode);
            }
            delete node.childNode
            nodeData.push(node);
        }
        traverse(treeData);
        return nodeData;
    }
    /**
     * 递归处理网关节点下属子节点的nodeTo数据
     * @param { Array } parmData -节点关系数组 
     * @returns 
     */
    static getEndpointNodeId(parmData) {

        if (isEmptyArray(parmData)) return parmData;

        let getwayList = parmData.filter((c) => {
            return c.nodeType == 2;
        });

        if (isEmptyArray(getwayList)) return parmData;

        let nodesGroup = {};
        for (let t of parmData) {
            if (nodesGroup.hasOwnProperty(t.nodeFrom)) {
                nodesGroup[t.nodeFrom].push(t)
            } else {
                nodesGroup[t.nodeFrom] = [t]
            }
        }
        for (let getway of getwayList) {
            if (nodesGroup.hasOwnProperty(getway.nodeId)) {
                let itemNodes = nodesGroup[getway.nodeId];
                let comNode = itemNodes.find((c) => {
                    return c.nodeType != 3;
                });
                if (!comNode) continue;
                let conditionList = itemNodes.filter((c) => {
                    return c.nodeId != comNode.nodeId;
                });
                for (let itemNode of conditionList) {
                    function internalTraverse(info) {
                        if (info) {
                            if (!nodesGroup[info.nodeId]) {
                                info.nodeTo = [comNode.nodeId];
                            } else {
                                let tempNode = nodesGroup[info.nodeId];
                                if (Array.isArray(tempNode)) {
                                    for (let t_item of tempNode) {
                                        internalTraverse(t_item);
                                    }
                                } else {
                                    internalTraverse(tempNode);
                                }
                            }
                        }
                    }
                    internalTraverse(itemNode);
                }
            }
        }
        return parmData;
    }
    /**
     * 清理节点数据
     * @param { Array } arr -节点数组
     * @returns 
     */
    static cleanNodeList(arr) {
        let nodeIds = arr.map((c) => {
            return c.nodeId;
        });
        for (const node of arr) {
            node.nodeTo = Array.from(new Set(node.nodeTo));
            if (!isEmptyArray(node.nodeTo)) {
                node.nodeTo = node.nodeTo.filter((key) => {
                    return nodeIds.indexOf(key) > -1;
                });
            }
        }
        return arr;
    }

    /**
     * 格式化node数据，对接api接口
     * @param {Array} nodeList 
     * @returns 
     */
    static adapterActivitiNodeList(nodeList) {
        for (let node of nodeList) {
            if (node.hasOwnProperty('id')) {
                delete node.id;
            }
            if (node.nodeType == 3) {
                let conditionObj = {
                    conditionList: node.conditionList,
                    sort: node.priorityLevel,
                    isDefault: node.isDefault
                };
                Object.assign(node, {
                    property: {}
                });
                node.property = conditionObj;
                delete node.conditionList;
            }

            if (node.nodeType == 4 || node.nodeType == 6) {
                let approveObj = {
                    emplIds: [],
                    emplList: [],
                    roleIds: [],
                    roleList: [],
                    hrbpConfType: 0,
                    signType: node.signType,
                }

                if (node.nodeApproveList && !isEmptyArray(node.nodeApproveList)) {
                    if(node.setType==4){
                        for(let approve of node.nodeApproveList){
                            let role={};
                            role.id=parseInt(approve.targetId);
                            role.name=node.name;
                            approveObj.roleIds.push(parseInt(approve.targetId));
                            approveObj.roleList.push(role);
                        }
                    } else if(node.setType==5){
                        for (let approve of node.nodeApproveList) {
                            let emp={};
                            emp.id=parseInt(approve.targetId);
                            emp.name=approve.name;
                            approveObj.emplIds.push(parseInt(approve.targetId));
                            approveObj.emplList.push(emp);
                        }
                    } else if(node.setType== 6){
                        for (let approve of node.nodeApproveList) {                      
                            approveObj.hrbpConfType= parseInt(approve.targetId);
                        }
                    }
                }
                //node.nodeProperty = node.setType;

                Object.assign(approveObj, { afterSignUpWay : node.property.afterSignUpWay }); 
                node.nodeProperty=node.setType; 
                node.property = approveObj;
                delete node.nodeApproveList;
            }
        }
        return nodeList;
    } 

}