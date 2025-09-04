package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.BpmBusinessDraft;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessDraftBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.openoa.engine.factory.FormFactory;
import org.openoa.engine.utils.AFWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
@Slf4j
@Service
public class BpmProcessDraftBizServiceImpl implements BpmProcessDraftBizService {

    @Autowired
    private BpmnConfService bpmnConfService;
    @Resource
    private FormFactory formFactory;

    @Override
    public void saveBusinessDraft(BusinessDataVo businessDataVo){
        String formCode = businessDataVo.getFormCode();
        List<BpmnConf> bpmnConfs = bpmnConfService.list(AFWrappers.<BpmnConf>lambdaTenantQuery()
                .eq(BpmnConf::getFormCode, formCode)
                .eq(BpmnConf::getEffectiveStatus, 1));
        if(CollectionUtils.isEmpty(bpmnConfs)){
            String errMsg= Strings.lenientFormat("未能根据流程formCode:%s查找到有效的模板配置!",formCode);
            log.error(errMsg);
            throw new AFBizException(errMsg);
        }
        BpmnConf bpmnConf = bpmnConfs.get(0);
        String bpmnCode = bpmnConf.getBpmnCode();
        //同一个流程只保留最新版本的一个草稿,历史草稿是没有意义的
        this.getMapper().delete(AFWrappers.<BpmBusinessDraft>lambdaTenantQuery()
                .eq(BpmBusinessDraft::getProcessKey,formCode)
                .eq(BpmBusinessDraft::getCreateUser, SecurityUtils.getLogInEmpIdSafe()));
        BpmBusinessDraft draft=new BpmBusinessDraft();
        draft.setBpmnCode(bpmnCode);
        draft.setProcessKey(formCode);
        draft.setCreateUser(SecurityUtils.getLogInEmpIdStr());
        draft.setCreateUserName(SecurityUtils.getLogInEmpNameSafe());
        draft.setTenantId(MultiTenantUtil.getCurrentTenantId());
        draft.setDraftJson(JSON.toJSONString(businessDataVo));
        this.getMapper().insert(draft);
    }
    @Override
    public BusinessDataVo loadDraft(String formCode, String userId){
        List<BpmBusinessDraft> bpmBusinessDrafts = this.getMapper().selectList(AFWrappers.<BpmBusinessDraft>lambdaTenantQuery()
                .eq(BpmBusinessDraft::getProcessKey, formCode)
                .eq(BpmBusinessDraft::getCreateUser, userId));
        if(CollectionUtils.isEmpty(bpmBusinessDrafts)){
            return null;
        }
        BpmBusinessDraft draft = bpmBusinessDrafts.get(0);
        String draftJson = draft.getDraftJson();
        BusinessDataVo businessDataVo = formFactory.dataFormConversion(draftJson, formCode);
        String oldBpmnCode = businessDataVo.getBpmnConfVo().getBpmnCode();

        List<BpmnConf> bpmnConfs = bpmnConfService.list(AFWrappers.<BpmnConf>lambdaTenantQuery()
                .eq(BpmnConf::getFormCode, formCode)
                .eq(BpmnConf::getEffectiveStatus, 1));
        if(CollectionUtils.isEmpty(bpmnConfs)){
            String errMsg= Strings.lenientFormat("未能根据流程formCode:%s查找到有效的模板配置!",formCode);
            log.error(errMsg);
            throw new AFBizException(errMsg);
        }
        BpmnConf bpmnConf = bpmnConfs.get(0);
        String bpmnCode = bpmnConf.getBpmnCode();
        //流程引擎无法感知版本变化时表单是否也发生变化,默认如果版本变化则草稿失效
        if(!oldBpmnCode.equals(bpmnCode)){
            this.getMapper().delete(AFWrappers.<BpmBusinessDraft>lambdaTenantQuery()
                    .eq(BpmBusinessDraft::getProcessKey,formCode)
                    .eq(BpmBusinessDraft::getCreateUser, userId));
            return null;
        }
        return businessDataVo;
    }
}
