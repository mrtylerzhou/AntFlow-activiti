package org.openoa.engine.bpmnconf.service.biz;

import org.openoa.base.constant.enums.ProcessJurisdictionEnum;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessNoticeServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessPermissionsServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessDeptBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BpmProcessDeptBizServiceImpl implements BpmProcessDeptBizService {
    @Autowired
    private BpmProcessNoticeServiceImpl processNoticeService;

    @Autowired
    @Lazy
    private BpmnConfBizServiceImpl confCommonService;

    @Autowired
    private BpmProcessNameServiceImpl bpmProcessNameService;
    @Autowired
    private BpmProcessPermissionsServiceImpl permissionsService;

    /**
     * 根据编号修改权限关联
     *
     * @param bpmnConf
     * @return
     */
    @Override
    public void editRelevance(BpmnConf bpmnConf) {
        bpmProcessNameService.editProcessName(bpmnConf);
    }
    @Override
    public List<String> findProcessKey() {
        List<String> processKeyList = Optional.ofNullable(permissionsService.getProcessKey(SecurityUtils.getLogInEmpIdSafe(), ProcessJurisdictionEnum.CREATE_TYPE.getCode())).orElse(Arrays.asList());
        List<BpmnConf> confList = Optional.ofNullable(confCommonService.getIsAllConfs()).orElse(Arrays.asList());
        List<String> collect = confList.stream().map(BpmnConf::getFormCode).collect(Collectors.toList());
        List<String> processList = Optional.ofNullable(this.getService().getAllProcess()).orElse(Arrays.asList());
        processList.addAll(processKeyList);
        processList.addAll(collect);
        return processList;
    }

    @Override
    public void editProcessConf(BpmProcessDeptVo vo) throws AFBizException {
        //todo save process's other info
        processNoticeService.saveProcessNotice(vo);
    }
}
