import { parseTime } from "@/utils/ruoyi";
import { isEmpty, isEmptyArray } from "@/utils/antflow/ObjectUtils";
import {
  formUserOptionSet,
  formPrevNodeApproverOptionSet,
} from "@/utils/antflow/const";
function All() {}
All.prototype = {
  arrToStr(arr) {
    if (arr) {
      return arr
        .map((item) => {
          return item.name;
        })
        .toString();
    }
  },
  toggleClass(arr, elem, key = "id") {
    //判断数组中是否包含某个元素
    if (isEmptyArray(arr)) return false;
    if (arr && arr.length > 0) {
      return arr.some((item) => {
        return !isEmpty(item) && item[key] == elem[key];
      });
    }
  },
  toChecked(arr, elem, key = "id") {
    var isIncludes = this.toggleClass(arr, elem, key);
    !isIncludes ? arr.push(elem) : this.removeEle(arr, elem, key);
  },
  removeEle(arr, elem, key = "id") {
    var includesIndex;
    arr.map((item, index) => {
      if (item[key] == elem[key]) {
        includesIndex = index;
      }
    });
    arr.splice(includesIndex, 1);
  },
  setApproverStr(nodeConfig) {
    if (!nodeConfig) return;
    let baseStr = this._buildApproverBaseStr(nodeConfig);
    return this._appendExtraSignStr(baseStr, nodeConfig);
  },
  _buildApproverBaseStr(nodeConfig) {
    if (nodeConfig.setType == 5) {
      if (nodeConfig.nodeApproveList.length == 1) {
        return nodeConfig.nodeApproveList[0].name;
      } else if (nodeConfig.nodeApproveList.length > 1) {
        if (nodeConfig.signType == 2) {
          return this.arrToStr(nodeConfig.nodeApproveList);
        } else if (nodeConfig.signType == 1) {
          return (
            nodeConfig.nodeApproveList.length +
            "人(" +
            this.arrToStr(nodeConfig.nodeApproveList) +
            ")会签"
          );
        } else if (nodeConfig.signType == 3) {
          return (
            nodeConfig.nodeApproveList.length +
            "人(" +
            this.arrToStr(nodeConfig.nodeApproveList) +
            ")顺序会签"
          );
        } else if (nodeConfig.signType == 4) {
          const ratio = nodeConfig.property?.arbitrationRatio ?? 100;
          return (
            nodeConfig.nodeApproveList.length +
            "人(" +
            this.arrToStr(nodeConfig.nodeApproveList) +
            ")仲裁签(" + ratio + "%)"
          );
        }
      }
    } else if (nodeConfig.setType == 3) {
      const levelMap = {
        1: "直接主管",
        2: "第二主管",
        3: "第三主管",
      };
      let level =
        levelMap[nodeConfig.directorLevel] ||
        `第${nodeConfig.directorLevel}级主管`;
      if (nodeConfig.signType == 2) {
        return level + "会签";
      } else {
        return level;
      }
    } else if (nodeConfig.setType == 4) {
      if (nodeConfig.nodeApproveList.length > 0) {
        return "指定 (" + this.arrToStr(nodeConfig.nodeApproveList) + ") 角色";
      }
      return "";
    } else if (nodeConfig.setType == 6) {
      if (nodeConfig.nodeApproveList.length > 0) {
        return "指定 (" + this.arrToStr(nodeConfig.nodeApproveList) + ")";
      }
      return "";
    } else if (nodeConfig.setType == 14) {
      return "指定部门";
    } else if (nodeConfig.setType == 12) {
      return "发起人自己";
    } else if (nodeConfig.setType == 13) {
      return "直属领导";
    } else if (nodeConfig.setType == 7) {
      return "由发起人自选审批人";
    } else if (nodeConfig.setType == 16) {
      const info = formUserOptionSet.find(
        (item) => item.value == nodeConfig.property?.formAssigneeProperty,
      );
      return "表单中的数据:" + info?.label;
    } else if (nodeConfig.setType == 17) {
      const name = nodeConfig.property?.udrAssigneeProperty?.name;
      return name ? "自定义：" + name : "自定义";
    } else if (nodeConfig.setType == 2) {
      return "层层审批";
    } else if (nodeConfig.setType == 18) {
      const info = formPrevNodeApproverOptionSet.find(
        (item) => item.value == nodeConfig.property?.formAssigneeProperty,
      );
      return "" + info?.label;
    } else {
      return "";
    }
  },
  _appendExtraSignStr(baseStr, nodeConfig) {
    const list = nodeConfig?.property?.additionalSignInfoList || [];
    if (list.length == 0) return baseStr;
    let result = baseStr;
    const addList = list.filter(a => a.propertyType == 1);
    const excludeList = list.filter(a => a.propertyType == 2);
    if (addList.length > 0) {
      const names = [];
      for (const item of addList) {
        const suffix = item.nodeProperty == 5 ? "（人员）" : "（角色）";
        for (const info of item.signInfos || []) {
          names.push(info.name + suffix);
        }
      }
      if (names.length > 0) {
        result += " + " + names.join(",");
      }
    }
    if (excludeList.length > 0) {
      const names = [];
      for (const item of excludeList) {
        const suffix = item.nodeProperty == 5 ? "（人员）" : "（角色）";
        for (const info of item.signInfos || []) {
          names.push(info.name + suffix);
        }
      }
      if (names.length > 0) {
        result += " - " + names.join(",");
      }
    }
    return result;
  },
  setCopyStrV2(nodeConfig) {
    if (!nodeConfig) return;
    if (nodeConfig.setType == 5) {
      if (nodeConfig.nodeApproveList.length == 1) {
        return nodeConfig.nodeApproveList[0].name;
      } else if (nodeConfig.nodeApproveList.length > 1) {
        if (nodeConfig.signType == 2) {
          return this.arrToStr(nodeConfig.nodeApproveList);
        } else if (nodeConfig.signType == 1) {
          return (
            nodeConfig.nodeApproveList.length +
            "人(" +
            this.arrToStr(nodeConfig.nodeApproveList) +
            ")"
          );
        } else if (nodeConfig.signType == 3) {
          return (
            nodeConfig.nodeApproveList.length +
            "人(" +
            this.arrToStr(nodeConfig.nodeApproveList) +
            ")"
          );
        }
      }
    } else if (nodeConfig.setType == 3) {
      const levelMap = {
        1: "直接主管",
        2: "第二主管",
        3: "第三主管",
      };
      let level =
        levelMap[nodeConfig.directorLevel] ||
        `第${nodeConfig.directorLevel}级主管`;
      if (nodeConfig.signType == 2) {
        return level + "";
      } else {
        return level;
      }
    } else if (nodeConfig.setType == 4) {
      if (nodeConfig.nodeApproveList.length > 0) {
        return "指定 (" + this.arrToStr(nodeConfig.nodeApproveList) + ") 角色";
      }
      return "";
    } else if (nodeConfig.setType == 6) {
      if (nodeConfig.nodeApproveList.length > 0) {
        return "指定 (" + this.arrToStr(nodeConfig.nodeApproveList) + ")";
      }
      return "";
    } else if (nodeConfig.setType == 14) {
      return "指定部门";
    } else if (nodeConfig.setType == 12) {
      return "发起人自己";
    } else if (nodeConfig.setType == 13) {
      return "直属领导";
    } else if (nodeConfig.setType == 7) {
      return "由发起人自选抄送人";
    } else {
      return "";
    }
  },
  getCheckboxStr(str, obj) {
    if (!obj) return;
    let arr = [];
    let list = str.split(",");
    for (var elem in obj) {
      list.map((item) => {
        if (item == obj[elem].key) {
          arr.push(obj[elem].value);
        }
      });
    }
    return arr.join("或");
  },
  // select 为单选
  getSelectStr(index, obj) {
    if (!obj) return;
    let ret = obj.filter((c) => c.key == index).map((x) => x.value);
    if (ret) {
      return ret;
    }
    return "";
  },
  // select 为多选
  getMultipleSelectStr(keys, obj) {
    if (!Array.isArray(keys) && keys.includes("[")) {
      keys = JSON.parse(keys);
    }
    if (!obj || isEmptyArray(keys)) return;
    let ret = obj.filter((c) => keys.includes(c.key)).map((x) => x.value);
    return ret;
  },

  conditionStr(nodeConfig, index) {
    var { conditionList, nodeApproveList, groupRelation } =
      nodeConfig.conditionNodes[index];

    const flatArray = conditionList
      .reduce((acc, val) => acc.concat(val), [])
      .filter((item) => item.columnId && item.columnId !== 0);
    if (flatArray.length == 0) {
      return index == nodeConfig.conditionNodes.length - 1 &&
        nodeConfig.conditionNodes[index].conditionList
          .reduce((acc, val) => acc.concat(val), [])
          .filter((item) => item.columnId && item.columnId !== 0).length == 0
        ? "其他条件进入此流程"
        : "请设置条件";
    }
    let str = "";
    for (let i = 0; i < conditionList.length; i++) {
      str = str + "条件组" + (i + 1) + "：【";
      str = str + this.getConditionStr(conditionList[i]);
      str = str + "】  ";
      if (i + 1 != conditionList.length) {
        str = str + (groupRelation == false ? " 且 " : " 或 ");
      }
    }
    return str;
  },

  autoNodeConditionStr(nodeConfig) {
    if (!nodeConfig) return "";
    let conditionList = nodeConfig.conditionList;
    let groupRelation = nodeConfig.groupRelation;
    if (!conditionList || isEmptyArray(conditionList)) {
      return "请设置自动条件";
    }
    const flatArray = conditionList
      .reduce((acc, val) => acc.concat(val), [])
      .filter((item) => item.columnId && item.columnId !== 0);
    if (flatArray.length == 0) {
      return "请设置自动条件";
    }
    let str = "";
    for (let i = 0; i < conditionList.length; i++) {
      str = str + "条件组" + (i + 1) + "：【";
      str = str + this.getConditionStr(conditionList[i]);
      str = str + "】  ";
      if (i + 1 != conditionList.length) {
        str = str + (groupRelation == false ? " 且 " : " 或 ");
      }
    }
    return str + this.autoNodeActionStr(nodeConfig);
  },

  /**自动节点动作摘要: 满足/不满足分支非默认时追加展示 */
  autoNodeActionStr(nodeConfig) {
    if (!nodeConfig) return "";
    const satMap = { 1: "跳转至固定节点", 2: "加批", 3: "转办", 4: "抄送" };
    const unsatMap = { 1: "结束流程", 2: "退回指定节点" };
    const parts = [];
    if (nodeConfig.satisfiedAction && satMap[nodeConfig.satisfiedAction]) {
      parts.push("满足→" + satMap[nodeConfig.satisfiedAction]);
    }
    if (nodeConfig.unsatisfiedAction && unsatMap[nodeConfig.unsatisfiedAction]) {
      parts.push("不满足→" + unsatMap[nodeConfig.unsatisfiedAction]);
    }
    return parts.length ? "  " + parts.join("; ") : "";
  },

  getConditionStr(conditionArray) {
    let str = "";
    for (let condition of conditionArray) {
      var {
        columnId,
        showName,
        optType,
        zdy1,
        opt1,
        zdy2,
        opt2,
        fieldTypeName,
        fixedDownBoxValue,
        condRelation, //条件关系
      } = condition;
      const relationTip = condRelation == false ? " 且 " : " 或 ";
      if (fieldTypeName == "input") {
        if (zdy1) {
          str += showName + "：" + zdy1 + relationTip;
        }
      } else if (fieldTypeName == "switch") {
        str += showName + "：" + zdy1 + relationTip;
      } else if (fieldTypeName == "radio") {
        // if (zdy1) {
        //     str += showName + '：' + zdy1 + " 并且 "
        // }
      } else if (fieldTypeName == "checkbox") {
        if (!fixedDownBoxValue) {
          str += nodeConfig.conditionNodes[index].nodeDisplayName + "     ";
        } else {
          if (zdy1) {
            str +=
              showName +
              "属于：" +
              this.getCheckboxStr(zdy1, JSON.parse(fixedDownBoxValue)) +
              relationTip;
          }
        }
      } else if (fieldTypeName == "select") {
        if (!fixedDownBoxValue) {
          str += nodeConfig.conditionNodes[index].nodeDisplayName + "     ";
        } else {
          if (zdy1) {
            if (!isNaN(Number(zdy1))) {
              str +=
                showName +
                "：" +
                this.getSelectStr(zdy1, JSON.parse(fixedDownBoxValue)) +
                relationTip;
            } else {
              str +=
                showName +
                "：" +
                this.getMultipleSelectStr(zdy1, JSON.parse(fixedDownBoxValue)) +
                relationTip;
            }
          }
        }
      } else if (fieldTypeName == "date") {
        if (zdy1) {
          var optTypeStr = ["", "≥", ">", "≤", "<", "="][optType];
          str += `${showName} ${optTypeStr} ${parseTime(
            zdy1,
            "{y}-{m}-{d}",
          )} ${relationTip} `;
        }
      } else if (fieldTypeName == "time") {
        if (zdy1) {
          var optTypeStr = ["", "≥", ">", "≤", "<", "="][optType];
          str += `${showName} ${optTypeStr} ${parseTime(
            zdy1,
            "{h}:{i}:{s}",
          )} ${relationTip} `;
        }
      } else if (fieldTypeName == "number") {
        if (optType < 6 && zdy1) {
          var optTypeStr = ["", "≥", ">", "≤", "<", "="][optType];
          str += `${showName} ${optTypeStr} ${zdy1} ${relationTip} `;
        } else if (optType >= 6 && zdy1 && zdy2) {
          str += `${zdy1} ${opt1} ${showName} ${opt2} ${zdy2} ${relationTip} `;
        }
      } else if (fieldTypeName == "expression") {
        const engineName = columnId == "20000" ? "JUEL" : "SpEL";
        str += `表达式(${engineName})${relationTip}`;
      } else {
        str += null;
      }
    }
    str = this.removeLastIndexOfAnd(str);
    str = this.removeLastIndexOfOR(str);
    return str && str.length > 0 ? str : "请设置条件";
  },
  copyerStr(nodeConfig) {
    if (nodeConfig.nodeApproveList.length != 0) {
      return this.arrToStr(nodeConfig.nodeApproveList);
    } else {
      // if (nodeConfig.ccSelfSelectFlag == 1) {
      //   return "发起人自选";
      // }
      return "";
    }
  },
  toggleStrClass(item, key) {
    let a = item.zdy1 ? item.zdy1.split(",") : [];
    return a.some((item) => {
      return item == key;
    });
  },

  removeLastIndexOfAnd(str) {
    let lastIndexOfAnd = str.lastIndexOf("且");
    if (lastIndexOfAnd !== -1) {
      str = str.slice(0, lastIndexOfAnd) + str.slice(lastIndexOfAnd + 1);
    }
    return str;
  },

  removeLastIndexOfOR(str) {
    let lastIndexOfOR = str.lastIndexOf("或");
    if (lastIndexOfOR !== -1) {
      str = str.slice(0, lastIndexOfOR) + str.slice(lastIndexOfOR + 1);
    }
    return str;
  },

  /**
   * 格式化条件控件值 (从 autoNodeDrawer 抽出, 供 autoNodeDrawer / approverDrawer / formatcommit_data 复用)
   * @param {Array} data - conditionList 二维数组
   * @param {Boolean} isPreview - true: 后端→前端显示; false: 前端显示→后端存储
   */
  convertConditionNodeValue(data, isPreview = true) {
    if (!data || isEmptyArray(data)) return;
    for (let itemArray of data) {
      let condRelationItem = itemArray[0]?.condRelation || false;
      for (let item of itemArray) {
        if (isEmpty(item.fieldTypeName)) {
          continue;
        }
        item.condRelation = condRelationItem;
        if (item.fieldTypeName == "radio") {
          item.zdy1 = parseInt(item.zdy1);
        }
        if (item.fieldTypeName == "select" && item.multiple) {
          if (!Array.isArray(item.zdy1) && item.zdy1.includes("[")) {
            if (isPreview) {
              item.zdy1 = JSON.parse(item.zdy1);
            }
          } else {
            if (!isPreview) {
              item.zdy1 = JSON.stringify(item.zdy1);
            }
          }
        }
        if (item.fieldTypeName == "select" && !item.multiple) {
          item.zdy1 = parseInt(item.zdy1);
        }
        if (item.fieldTypeName == "date") {
          item.zdy1 = parseTime(item.zdy1, "{y}-{m}-{d} {h}:{i}:{s}");
        }
        if (item.fieldTypeName == "time") {
          item.zdy1 = parseTime(item.zdy1, "{y}-{m}-{d} {h}:{i}:{s}");
        }
        if (item.optType == "6") {
          if (item.opt1 == "≤" && item.opt2 == "<") {
            item.optType = "7";
          } else if (item.opt1 == "<" && item.opt2 == "≤") {
            item.optType = "8";
          } else if (item.opt1 == "≤" && item.opt2 == "≤") {
            item.optType = "9";
          } else {
            item.optType = "6";
          }
        } else if (item.optType == "7" || item.optType == "8" || item.optType == "9") {
          item.optType = "6";
        }
      }
    }
  },

  /**
   * 构建条件显示文本 (从 autoNodeDrawer 抽出)
   * @param {Array} conditionList - 条件组数组
   * @param {Boolean} groupRelation - 组间关系 false:且 true:或
   * @returns {String}
   */
  buildConditionDisplayText(conditionList, groupRelation) {
    if (!conditionList || conditionList.length === 0) return "";
    let texts = [];
    for (let group of conditionList) {
      let groupTexts = [];
      for (let item of group) {
        if (item.showName && item.zdy1) {
          groupTexts.push(item.showName + ":" + item.zdy1);
        }
      }
      if (groupTexts.length > 0) {
        texts.push(groupTexts.join(" 且 "));
      }
    }
    return texts.length > 0 ? texts.join(groupRelation ? " 或 " : " 且 ") : "";
  },
};

export default new All();
