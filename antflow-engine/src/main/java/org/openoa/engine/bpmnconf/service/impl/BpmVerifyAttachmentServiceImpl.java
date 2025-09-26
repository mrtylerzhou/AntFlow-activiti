package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmVerifyAttachment;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.vo.BpmVerifyAttachmentVo;
import org.openoa.engine.bpmnconf.mapper.BpmVerifyAttachmentMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVerifyAttachmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * //todo 审批附件 服务层实现
 */
@Service
public class BpmVerifyAttachmentServiceImpl extends ServiceImpl<BpmVerifyAttachmentMapper, BpmVerifyAttachment> implements BpmVerifyAttachmentService {

    @Override
    public void addVerifyAttachment(BpmVerifyAttachmentVo vo,Long verifyInfoId) {
        BpmVerifyAttachment entity = new BpmVerifyAttachment();
        BeanUtils.copyProperties(vo, entity);
        entity.setVerifyInfoId(verifyInfoId);
        entity.setCreateTime(new Date());
        entity.setTenantId(MultiTenantUtil.getCurrentTenantId());
        this.getBaseMapper().insert(entity);
    }

    @Override
    public void addVerifyAttachmentBatch(List<BpmVerifyAttachmentVo> list,Long verifyInfoId) {
        if (CollectionUtils.isEmpty(list) || verifyInfoId == null) {
            return;
        }
        List<BpmVerifyAttachment> entityList = new ArrayList<>();
        for (BpmVerifyAttachmentVo vo : list) {
            BpmVerifyAttachment entity = new BpmVerifyAttachment();
            BeanUtils.copyProperties(vo, entity);
            entity.setVerifyInfoId(verifyInfoId);
            entity.setCreateTime(new Date());
            entity.setTenantId(MultiTenantUtil.getCurrentTenantId());
            entityList.add(entity);
        }
        this.saveBatch(entityList);
    }
    @Override
    public List<BpmVerifyAttachmentVo> getBpmVerifyAttachment(Long verifyInfoId) {
        List<BpmVerifyAttachmentVo> vos = this.getBaseMapper().getVerifyAttachmentList(verifyInfoId);
        if (!CollectionUtils.isEmpty(vos)) {
            return vos;
        }
        return new ArrayList<>();
    }
}
