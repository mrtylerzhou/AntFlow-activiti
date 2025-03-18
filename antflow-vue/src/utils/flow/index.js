import { parseTime } from '@/utils/ruoyi'
const isEmpty = (data) =>data === null || data === undefined || data == "" || data == "" || data == "{}" || data == "[]" || data == "null";
const isEmptyArray = data => Array.isArray(data) ? data.length === 0 : true
function All() {}
All.prototype = {
    timer: "",
    debounce(fn, delay = 500) {
        var _this = this;
        return function(arg) {
            //获取函数的作用域和变量
            let that = this;
            let args = arg;
            clearTimeout(_this.timer) // 清除定时器
            _this.timer = setTimeout(function() {
                fn.call(that, args)
            }, delay)
        }
    },
    setCookie(val) { //cookie设置[{key:value}]、获取key、清除['key1','key2']
        for (var i = 0, len = val.length; i < len; i++) {
            for (var key in val[i]) {
                document.cookie = key + '=' + encodeURIComponent(val[i][key]) + "; path=/";
            }
        }
    },
    getCookie(name) {
        var strCookie = document.cookie;
        var arrCookie = strCookie.split("; ");
        for (var i = 0, len = arrCookie.length; i < len; i++) {
            var arr = arrCookie[i].split("=");
            if (name == arr[0]) {
                return decodeURIComponent(arr[1]);
            }
        }
    },
    clearCookie(name) {
        var myDate = new Date();
        myDate.setTime(-1000); //设置时间    
        for (var i = 0, len = name.length; i < len; i++) {
            document.cookie = "" + name[i] + "=''; path=/; expires=" + myDate.toGMTString();
        }
    },
    arrToStr(arr) {
        if (arr) {
            return arr.map(item => { return item.name }).toString()
        }
    },
    toggleClass(arr, elem, key = 'id') {//判断数组中是否包含某个元素  
        if(isEmptyArray(arr)) return false;
        if(arr && arr.length > 0) { 
            return arr.some(item => { return  !isEmpty(item) && item[key] == elem[key] });
        }
    },
    toChecked(arr, elem, key = 'id') { 
        var isIncludes = this.toggleClass(arr, elem, key);
        !isIncludes ? arr.push(elem) : this.removeEle(arr, elem, key);
    },
    removeEle(arr, elem, key = 'id') {
        var includesIndex;
        arr.map((item, index) => {
            if (item[key] == elem[key]) {
                includesIndex = index
            }
        });
        arr.splice(includesIndex, 1);
    }, 
    setApproverStr(nodeConfig) {  
        if(!nodeConfig) return; 
        if (nodeConfig.setType == 5) {
            if (nodeConfig.nodeApproveList.length == 1) {
                return nodeConfig.nodeApproveList[0].name
            } else if (nodeConfig.nodeApproveList.length > 1) {
                if (nodeConfig.signType == 2) {
                    return this.arrToStr(nodeConfig.nodeApproveList)
                } else if (nodeConfig.signType == 1) {
                    return nodeConfig.nodeApproveList.length + "人(" + this.arrToStr(nodeConfig.nodeApproveList) + ")会签"
                } else if(nodeConfig.signType == 3){
                    return nodeConfig.nodeApproveList.length + "人(" + this.arrToStr(nodeConfig.nodeApproveList) + ")顺序会签"
                }
            }
        } else if (nodeConfig.setType == 3) {
            let level = nodeConfig.directorLevel == 1 ? '直接主管' : '第' + nodeConfig.directorLevel + '级主管' 
            if (nodeConfig.signType == 2) {
                return level + "会签"
            }else{
                return level
            }
        }else if (nodeConfig.setType == 4) {
            if (nodeConfig.nodeApproveList.length > 0) {
                return  "指定 (" + this.arrToStr(nodeConfig.nodeApproveList) + ") 角色"
            }
            return ""
        } else if (nodeConfig.setType == 6) {
            if (nodeConfig.nodeApproveList.length > 0) {
                return  "指定 (" + this.arrToStr(nodeConfig.nodeApproveList) + ")"
            }
            return ""
        } else if (nodeConfig.setType == 14) { 
            return "指定部门"
        } else if (nodeConfig.setType == 12) { 
            return "发起人自己"
        }  else if (nodeConfig.setType == 13) { 
            return "直属领导"
        }  else if (nodeConfig.setType == 7) { 
            return "由发起人自选审批人"
        }else {
            return ""
        }
    }, 
    getCheckboxStr(str, obj) {
        console.log(str, obj);
        if(!obj) return; 
        let arr = [];
        let list = str.split(","); 
        for (var elem in obj) { 
            list.map(item => {  
                if (item == obj[elem].key) { 
                    arr.push(obj[elem].value)
                }
            })
        }
        return arr.join("或")
    },  
    // index 为Number
    getSelectStr(index, obj) {   
        if(!obj) return;  
        let ret = obj.filter(c=>c.key == index).map(x => x.value); 
        if (ret) {
            return ret;
        }
        return '';
    },  
     // index 为string
     getOutSideConditionLabelStr(index, obj) {   
        if(!obj) return;   
        let ret = obj.filter(c=>c.key == index).map(x => x.value); 
        if (ret && ret.length > 0) {
            return ret;
        }
        return '';
    },   
    conditionStr(nodeConfig, index) { 
        var { conditionList, nodeApproveList } = nodeConfig.conditionNodes[index];   
        if (conditionList.length == 0) { 
            return (index == nodeConfig.conditionNodes.length - 1) && nodeConfig.conditionNodes[index].conditionList.length == 0 ? '其他条件进入此流程' : '请设置条件'
        } else {
            let str = ""
            for (var i = 0; i < conditionList.length; i++) {
                var { columnId, columnType, showType, showName, optType, zdy1, opt1, zdy2, opt2,fieldTypeName, fixedDownBoxValue } = conditionList[i]; 
                if (columnId == 0) {
                    if (nodeApproveList.length != 0) {
                        str += '发起人属于：'
                        str += nodeApproveList.map(item => { return item.name }).join("或") + " 并且 "
                    }
                } 
                else if (fieldTypeName == "input") {
                    if (zdy1) {
                        str += showName + '：' + zdy1 + " 并且 "
                    }              
                }
                else if (fieldTypeName == "switch") { 
                    str += showName + '：' + zdy1 + " 并且 "         
                }
                else if (fieldTypeName == "radio") {
                    // if (zdy1) {
                    //     str += showName + '：' + zdy1 + " 并且 "
                    // }              
                }
                else if (fieldTypeName == "checkbox") {
                    if (!fixedDownBoxValue) {
                        str += nodeConfig.conditionNodes[index].nodeDisplayName + "     "
                    }else {
                        if (zdy1) {
                            str += showName + '属于：' + this.getCheckboxStr(zdy1, JSON.parse(fixedDownBoxValue)) + " 并且 "
                        }
                    }             
                }
                else if (fieldTypeName == "select") {  
                    if (!fixedDownBoxValue) {
                        str += nodeConfig.conditionNodes[index].nodeDisplayName + "     "
                    }else {  
                        if (zdy1) { 
                            if(!isNaN(Number(zdy1))){
                                    str += showName + '：' + this.getSelectStr(zdy1, JSON.parse(fixedDownBoxValue)) + " 并且 "
                            }else {
                                str += showName + '：' + this.getOutSideConditionLabelStr(zdy1, JSON.parse(fixedDownBoxValue)) + " 并且 " 
                            } 
                        }
                    }        
                }
                else if (fieldTypeName == "date") {
                    if (zdy1) { 
                        var optTypeStr = ["", "≥", ">", "≤", "<", "="][optType]
                        str += `${showName} ${optTypeStr} ${parseTime(zdy1, '{y}-{m}-{d}') } 并且 `
                    }    
                }
                else if (fieldTypeName == "time") {
                    if (zdy1) {
                        var optTypeStr = ["", "≥", ">", "≤", "<", "="][optType]
                        str += `${showName} ${optTypeStr} ${parseTime(zdy1, '{h}:{i}:{s}') } 并且 `
                    }    
                }
                else if (fieldTypeName == "input-number"){
                    if (optType != 6 && zdy1) {
                        var optTypeStr = ["", "≥", ">", "≤", "<", "="][optType]
                        str += `${showName} ${optTypeStr} ${zdy1} 并且 `
                    } else if (optType == 6 && zdy1 && zdy2) {
                        str += `${zdy1} ${opt1} ${showName} ${opt2} ${zdy2} 并且 `
                    }
                } 
                else {
                    str += null
                }
            } 
            return str ? str.substring(0, str.length - 4) : '请设置条件'
        }
    },
    copyerStr(nodeConfig) {
        if (nodeConfig.nodeApproveList.length != 0) {
            return this.arrToStr(nodeConfig.nodeApproveList)
        } else {
            if (nodeConfig.ccSelfSelectFlag == 1) {
                return "发起人自选"
            }
        }
    }, 
    toggleStrClass(item, key) {
        let a = item.zdy1 ? item.zdy1.split(",") : []
        return a.some(item => { return item == key });
    },
}

export default new All();

