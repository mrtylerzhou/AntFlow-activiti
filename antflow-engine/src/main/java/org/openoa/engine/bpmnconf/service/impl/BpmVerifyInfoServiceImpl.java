package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmVerifyInfoVo;
import org.openoa.engine.bpmnconf.mapper.BpmVerifyInfoMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVerifyInfoService;
import org.openoa.base.util.AFWrappers;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * //todo 员工信息
 */
@Service
public class BpmVerifyInfoServiceImpl extends ServiceImpl<BpmVerifyInfoMapper, BpmVerifyInfo> implements BpmVerifyInfoService {


    @Override
    public void addVerifyInfo(String businessId, String remark, Integer businessType, String taskName, Integer verifyStatuss) {
        BpmVerifyInfo verifyInfo = new BpmVerifyInfo();
        verifyInfo.setBusinessId(businessId);
        verifyInfo.setId(null);
        verifyInfo.setBusinessType(businessType);
        verifyInfo.setVerifyDate(new Date());
        verifyInfo.setVerifyUserName(SecurityUtils.getLogInEmpName());
        verifyInfo.setTaskName(taskName);
        verifyInfo.setVerifyUserId(SecurityUtils.getLogInEmpIdStr());
        verifyInfo.setVerifyDesc(Strings.isNullOrEmpty(remark) ? "同意" : remark);
        verifyInfo.setVerifyStatus(verifyStatuss);
        verifyInfo.setTenantId(MultiTenantUtil.getCurrentTenantId());
        this.getBaseMapper().insert(verifyInfo);
    }

    @Override
    public Map<String,BpmVerifyInfo> getByProcInstIdAndTaskDefKey(String processNumber,String taskDefKey){
        if(StringUtils.isBlank(processNumber)){
            throw  new AFBizException("流程编号不存在!");
        }
       if(StringUtils.isEmpty(taskDefKey)){
           return null;
       }

        List<BpmVerifyInfo> verifyInfos = this.list(AFWrappers.<BpmVerifyInfo>lambdaTenantQuery()
                .eq(BpmVerifyInfo::getProcessCode, processNumber)
                .eq(BpmVerifyInfo::getTaskDefKey, taskDefKey));
        Map<String, BpmVerifyInfo> verifyInfoMap = verifyInfos.stream().collect(Collectors.toMap(a -> a.getTaskDefKey() + a.getVerifyUserId(), b -> b, (v1, v2) -> v1));
        return verifyInfoMap;
    }


    /**
     * get verify info by a list of process codes
     *
     * @param processCodes
     * @return
     */
    @Override
    public Map<String, List<BpmVerifyInfoVo>> getBpmVerifyInfoBatch(List<String> processCodes) {
        List<BpmVerifyInfoVo> vos = this.getBaseMapper().verifyInfoList(BpmVerifyInfoVo.builder().processCodeList(processCodes)
                .build());
        if (!ObjectUtils.isEmpty(vos)) {
            return vos.stream().collect(Collectors.groupingBy(BpmVerifyInfoVo::getProcessCode));
        }
        return new HashMap<>();
    }

    /**
     * list verify info by a process code
     *
     * @param processCodes
     * @return
     */
    @Override
    public Map<String, String> listBpmVerifyInfoVo(List<String> processCodes) {
        Map<String, String> map = Maps.newHashMap();
        Map<String, List<BpmVerifyInfoVo>> listMap = this.getBpmVerifyInfoBatch(processCodes);
        if (!ObjectUtils.isEmpty(listMap)) {
            listMap.forEach((key, value) -> {
                List<BpmVerifyInfoVo> list = listMap.get(key);
                StringBuffer buf = new StringBuffer();
                for (BpmVerifyInfoVo vo : list) {
                    String join = StringUtils.join(vo.getTaskName(), ":", vo.getVerifyUserName(), "--");
                    //buf.append("\r\n");
                    buf.append(join);
                }
                String verify = buf.toString().substring(0, buf.toString().length() - 2);
                map.put(key, verify);
            });
        }
        return map;
    }
}
