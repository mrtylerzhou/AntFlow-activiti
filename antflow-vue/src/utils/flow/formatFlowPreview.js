// import { FormatUtils } from '@/utils/formatFlowPreview' 
const isEmptyArray = data => Array.isArray(data) ? data.length === 0 : true

export class FormatUtils {
    /**
     * 对基础设置,高级设置等设置页内容进行格式化
     * @param params
     */
    static formatSettings(param) {
        let nodeList = [];
        if(!param || isEmptyArray(param.bpmnNodeList))
            return nodeList;
        for (let node of param.bpmnNodeList) {
            nodeList.push(this.createNewNode(node));
        }
        return this.depthConverterToTree(nodeList);
    }

    /**
     * 展平树结构
     * @param {Object} treeData  - 节点数据 
     * @returns Array - 节点数组
     */
    static createNewNode(node) {
        let newNode = {
            nodeId: node.nodeId,
            nodeType: node.nodeType,
            nodeName: node.nodeName,
            nodeDisplayName: this.arrToStr(node?.params?.assigneeList),
            nodeTo: node.params?.nodeTo,
            assigneeList: node?.params?.assigneeList,
            isNodeDeduplication: node?.params?.isNodeDeduplication,
        };
        return newNode;
    }
    static arrToStr(arr) {
        if (arr) {
            return arr.map(item => { return item.assigneeName ?? item.elementName }).toString()
        }
    }
    static depthConverterToTree(parmData) {
        if (isEmptyArray(parmData)) return
        let nodesGroup = {}, startNode = {}
        for (let t of parmData) {
            if (!nodesGroup.hasOwnProperty(t.nodeId)) {
                nodesGroup[t.nodeId] = t
            }
        }
        for (let node of parmData) {
            if (1 == node.nodeType) {
                startNode = node;
            }
            let childNodeId = node.nodeTo;
            if (nodesGroup.hasOwnProperty(childNodeId)) {
                let itemNode = nodesGroup[childNodeId];
                Object.assign(node, { childNode: itemNode });
            }
        }
        return startNode
    }
}