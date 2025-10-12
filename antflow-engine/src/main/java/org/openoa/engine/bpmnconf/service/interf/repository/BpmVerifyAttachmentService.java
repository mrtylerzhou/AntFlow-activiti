package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmVerifyAttachment;
import org.openoa.base.vo.BpmVerifyAttachmentVo;
import java.util.List;
import java.util.Map;

public interface BpmVerifyAttachmentService extends IService<BpmVerifyAttachment> {

    void addVerifyAttachment(BpmVerifyAttachmentVo vo,Long verifyInfoId);

    void addVerifyAttachmentBatch(List<BpmVerifyAttachmentVo> list, Long verifyInfoId);

    List<BpmVerifyAttachmentVo> getBpmVerifyAttachment(Long verifyInfoId);

}
