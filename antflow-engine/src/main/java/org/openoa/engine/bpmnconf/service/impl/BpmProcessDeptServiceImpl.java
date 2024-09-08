package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.activiti.engine.RepositoryService;
import org.openoa.base.constant.enums.ProcessJurisdictionEnum;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.openoa.engine.bpmnconf.confentity.BpmProcessDept;
import org.openoa.engine.bpmnconf.confentity.BpmnConf;
import org.openoa.engine.bpmnconf.mapper.BpmProcessDeptMapper;
import org.openoa.engine.bpmnconf.mapper.EmployeeMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmProcessNameServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmnConfCommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * //todo process and department connection service,the module is deprecated and will be redesigned
 */
@Service
public class BpmProcessDeptServiceImpl extends ServiceImpl<BpmProcessDeptMapper, BpmProcessDept> {

    private static Map<String, BpmnConf> bpmnConfMap = new ConcurrentHashMap<>();


    @Autowired
    private BpmProcessDeptMapper bpmProcessDeptMapper;
    @Autowired
    private BpmProcessNodeOvertimeServiceImpl processNodeOvertimeService;
    @Autowired
    private BpmProcessNoticeServiceImpl processNoticeService;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private BpmnConfCommonServiceImpl confCommonService;
    @Autowired
    private BpmnConfServiceImpl bpmnConfService;
    @Autowired
    private BpmProcessNameServiceImpl bpmProcessNameService;
    @Autowired
    private BpmProcessPermissionsServiceImpl permissionsService;

    /**
     * 查找最大流程编号
     *
     * @return
     */
    public String maxProcessCode() {
        return bpmProcessDeptMapper.maxProcessCode();
    }

    /**
     * 生成流程编号
     *
     * @param testStr
     * @return
     */
    public String getProcessCode(String testStr) {
        String[] strs = testStr.split("[^0-9]");//根据不是数字的字符拆分字符串
        String numStr = strs[strs.length - 1];//取出最后一组数字
        if (numStr != null && numStr.length() > 0) {//如果最后一组没有数字(也就是不以数字结尾)，抛NumberFormatException异常
            int n = numStr.length();//取出字符串的长度
            int num = Integer.parseInt(numStr) + 1;//将该数字加一
            String added = String.valueOf(num);
            n = Math.min(n, added.length());
            //拼接字符串
            return testStr.subSequence(0, testStr.length() - n) + added;
        } else {
            throw new NumberFormatException();
        }
    }
    /**
     * 查询全员创建权限
     */
    public List<String> getAllProcess() {
        QueryWrapper<BpmProcessDept> wrapper = new QueryWrapper<>();
        wrapper.eq("is_all", 1);
        List<BpmProcessDept> deptList = bpmProcessDeptMapper.selectList(wrapper);
        return Optional.ofNullable(deptList)
                .map(o ->
                        deptList.stream().map(depProcess -> depProcess.getProcessKey()).collect(Collectors.toList())
                )
                .orElse(Arrays.asList());
    }

    /**
     * 根据编号修改权限关联
     *
     * @param bpmnConf
     * @return
     */
    public void editRelevance(BpmnConf bpmnConf) {
        bpmProcessNameService.editProcessName(bpmnConf);
    }
    public List<String> findProcessKey() {
        List<String> processKeyList = Optional.ofNullable(permissionsService.getProcessKey(SecurityUtils.getLogInEmpIdSafe().intValue(), ProcessJurisdictionEnum.CREATE_TYPE.getCode())).orElse(Arrays.asList());
        List<BpmnConf> confList = Optional.ofNullable(confCommonService.getIsAllConfs()).orElse(Arrays.asList());
        List<String> collect = confList.stream().map(BpmnConf::getFormCode).collect(Collectors.toList());
        List<String> processList = Optional.ofNullable(this.getAllProcess()).orElse(Arrays.asList());
        processList.addAll(processKeyList);
        processList.addAll(collect);
        return processList;
    }

    public void editProcessConf(BpmProcessDeptVo vo) throws JiMuBizException {
        //todo save process's other info
        processNoticeService.saveProcessNotice(vo.getProcessCode(), vo.getNotifyTypeIds());
    }
}
