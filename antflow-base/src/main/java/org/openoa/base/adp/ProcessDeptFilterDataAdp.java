package org.openoa.base.adp;

import org.openoa.base.service.BpmProcessDeptService;
import org.openoa.base.util.FilterDataUtils;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProcessDeptFilterDataAdp extends FilterDataAdaptor<BpmProcessDeptVo> {
    @Autowired
    private BpmProcessDeptService processDeptService;
    @Override
    public List<BpmProcessDeptVo> getFilterData(String params) {
        BpmProcessDeptVo vo= FilterDataUtils.formatParams(params,BpmProcessDeptVo.class);
        //查询并返回漏斗数据
        return processDeptService.allConfigure(vo);
    }
    @Override
    public Map<String, String> filterColumnMap() {
        Map<String, String> map = new HashMap<>();
        map.put("deptId","deptName");
        map.put("processCode","processCode");
        map.put("processName","processName");
        map.put("processType","processTypeName");
        return map;
    }
}
