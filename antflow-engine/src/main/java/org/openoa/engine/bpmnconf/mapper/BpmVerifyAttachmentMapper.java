package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.BpmVerifyAttachment;
import org.openoa.base.vo.BpmVerifyAttachmentVo;

import java.util.List;

@Mapper
public interface BpmVerifyAttachmentMapper extends BaseMapper<BpmVerifyAttachment> {

    public List<BpmVerifyAttachmentVo> getVerifyAttachment(BpmVerifyAttachmentVo vo);

    public List<BpmVerifyAttachmentVo> getVerifyAttachmentList(@Param("verifyInfoId") Long verifyInfoId);
}
