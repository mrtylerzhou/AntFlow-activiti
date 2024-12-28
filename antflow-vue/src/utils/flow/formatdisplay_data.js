// import { FormatDisplayUtils } from '@/utils/formatdisplay_data'
import { approveList,hrbpOptions } from '@/utils/flow/const'
const isEmptyArray = data => Array.isArray(data) ? data.length === 0 : false

export class FormatDisplayUtils {
    /**
     * 格式化显示数据
     * @param {Array} parmData 
     * @returns Object
     */
    static getToTree(parmData) {        
        if (isEmptyArray(parmData)) return;
        let node = this.createNodeDisplay(parmData); 
        if (!node) return;
        let formatList = this.formatDisplayStructNodeList(parmData?.nodes);     
        node.nodeConfig = this.depthConverterToTree(formatList);//parmData.nodes
        return node;
    }

    /**
     * 创建Node Data 数据
     * @param { Object } nodeData - 源节点数据
     * @returns Object
     */
    static createNodeDisplay(nodeData) { 
       if (!nodeData) return;
       if (isEmptyArray(nodeData)) return;
       let displayObj = {
            tableId: nodeData?.id,
            bpmnCode: nodeData.bpmnCode,
            bpmnName: nodeData.bpmnName, //name 改成 bpmnName 其他的都是添加的
            bpmnType: nodeData.bpmnType,
            formCode:nodeData.formCode,
            appId: nodeData.appId,
            isOutSideProcess: nodeData.isOutSideProcess,
            businessPartyId: nodeData.businessPartyId,
            deduplicationType: nodeData.deduplicationType,//2去重,1不去重
            effectiveStatus:  nodeData.effectiveStatus == 1?true:false,
            isLowCodeFlow:  nodeData.isLowCodeFlow,
            lfFormData:  nodeData.lfFormData,
            lfFormDataId:  nodeData.lfFormDataId,
            remark: nodeData.remark,
            isDel: 0,
            directorMaxLevel: 3, 
            nodeConfig: {},
        } 
        return displayObj
    }
    /**
     * List 转成tree结构
     * @param {Array} parmData 
     * @returns 
     */
    static depthConverterToTree(parmData) {
        if(!parmData) return;
        if (isEmptyArray(parmData)) return;
        let nodesGroup = {}, startNode = {}
        for (let t of parmData) {
            if (nodesGroup.hasOwnProperty(t.nodeFrom)) {
                nodesGroup[t.nodeFrom].push(t)
            } else {
                nodesGroup[t.nodeFrom] = [t]
            }
        }
        for (let node of parmData) {
            if (1 == node.nodeType) {
                startNode = node;
            }
            Object.assign(node, { conditionNodes: [] });
            let currNodeId = node.nodeId;
            if (nodesGroup.hasOwnProperty(currNodeId)) {
                let itemNodes = nodesGroup[currNodeId];
                for (let itemNode of itemNodes) {
                    if (3 == itemNode.nodeType) {
                        node.conditionNodes.push(itemNode);
                    } else {
                        node.childNode = itemNode;
                    }
                }
            }
        }
        return startNode
    }

    static formatDisplayStructNodeList(nodeList) {
        if (!nodeList) return;
        if (isEmptyArray(nodeList)) return nodeList;
        for (let node of nodeList) {    
            if (node.nodeType == 3) {
                node.priorityLevel = node.property.sort;
                node.isDefault = node.property.isDefault;
                Object.assign(node, { conditionList: [] });
                node.conditionList  =  node.property.conditionList ? node.property.conditionList : [];  
                delete node.property;
            }

            if (node.nodeType == 4 || node.nodeType == 6) {
                let empList = [];
                if (node.nodeProperty == 6) {
                    let approveObj = {
                        type: 5,
                        targetId: parseInt(node.property.hrbpConfType||0),
                        name: hrbpOptions.find(item => item.value == node.property.hrbpConfType)?.label
                    };
                    empList.push(approveObj);
                }
                else if (node.nodeProperty == 4 && !isEmptyArray(node.property.roleList)) {
                    for (let role of node.property.roleList) {
                        let r = {
                            type: 3,
                            targetId: parseInt(role.id),
                            name: role.name
                        };
                        empList.push(r);
                    }
                } else if (node.nodeProperty == 5 && !isEmptyArray(node.property.emplList)) {
                        for (let emp of node.property.emplList) {
                            let approveObj = {
                                type: 5,
                                targetId: parseInt(emp.id),
                                name: emp.name
                            };
                            empList.push(approveObj);
                        }
                } 
                else if (node.nodeProperty == 3){
                    node.directorLevel = node.property.assignLevelGrade;
                }               
                Object.assign(node, { signType: node.property?.signType });  
                node.setType = node.nodeProperty;
                Object.assign(node, { nodeApproveList: [] });
                
                node.nodeApproveList = empList;
                //delete node.property;
            }
        }
        return nodeList;
    }
}