/**
 * 加批/抄送规则子配置公共工具
 * 从 approverDrawer 抽出, 供 approverDrawer(条件自动加批) 与 autoNodeDrawer(自动节点加批/抄送) 复用
 */

/**加批规则子配置懒初始化(绑定嵌入的 ApproverStepPanel) */
export function initAutoSignUpConf(conf) {
    if (!conf || !conf.setType) {
        return { setType: 5, nodeApproveList: [], property: {} };
    }
    return conf;
}

/**加批规则校验: 按类型检查必要数据 */
export function validateAutoSignUpConf(conf) {
    const list = conf.nodeApproveList || [];
    if (conf.setType == 5 || conf.setType == 4 || conf.setType == 6) return list.length > 0;
    if (conf.setType == 3) return !!conf.directorLevel;
    if (conf.setType == 17) return !!(conf.property && conf.property.udrAssigneeProperty);
    return true; // 12 发起人自己 / 13 直属领导 无需额外数据
}

/**镜像 formatcommit_data 的映射, 构建运行期解析所需的 resolvedProperty */
export function buildAutoSignUpResolvedProperty(conf) {
    const p = { emplIds: [], emplList: [], roleIds: [], roleList: [] };
    const list = conf.nodeApproveList || [];
    if (conf.setType == 4) {
        list.forEach(a => { p.roleIds.push(a.targetId); p.roleList.push({ id: a.targetId, name: a.name }); });
    } else if (conf.setType == 5) {
        list.forEach(a => { p.emplIds.push(a.targetId); p.emplList.push({ id: a.targetId, name: a.name }); });
    } else if (conf.setType == 6) {
        p.hrbpConfType = list.length ? list[0].targetId : 0;
    } else if (conf.setType == 3) {
        p.assignLevelGrade = conf.directorLevel;
    } else if (conf.setType == 17) {
        p.udrAssigneeProperty = (conf.property && conf.property.udrAssigneeProperty) || null;
        p.udrValueJson = (conf.property && conf.property.udrValueJson) || null;
    }
    return p;
}

/**提交前完善规则配置: nodeProperty + resolvedProperty; 返回是否通过校验 */
export function finalizeAutoSignUpConf(conf) {
    if (!conf || !conf.setType) return false;
    if (!validateAutoSignUpConf(conf)) return false;
    conf.nodeProperty = conf.setType;
    conf.resolvedProperty = buildAutoSignUpResolvedProperty(conf);
    return true;
}
