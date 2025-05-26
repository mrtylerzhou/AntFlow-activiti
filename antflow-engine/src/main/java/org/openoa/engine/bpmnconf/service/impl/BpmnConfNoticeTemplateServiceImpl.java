package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.constant.enums.MsgNoticeTypeEnum;
import org.openoa.engine.bpmnconf.confentity.BpmnConfNoticeTemplate;
import org.openoa.engine.bpmnconf.confentity.BpmnConfNoticeTemplateDetail;
import org.openoa.engine.bpmnconf.mapper.BpmnConfNoticeTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname BpmnConfNoticeTemplateServiceImpl
 * @Description notice tempalte service
 * @Created by AntOffice
 * @since 0.5
 */
@Service
public class BpmnConfNoticeTemplateServiceImpl extends ServiceImpl<BpmnConfNoticeTemplateMapper, BpmnConfNoticeTemplate> {


    @Autowired
    private BpmnConfNoticeTemplateDetailServiceImpl bpmnConfNoticeTemplateDetailService;


    public BpmnConfNoticeTemplateDetail getDetailByCodeAndType(String bpmnCode, Integer noticeType) {
        List<BpmnConfNoticeTemplateDetail> bpmnConfNoticeTemplateDetail = bpmnConfNoticeTemplateDetailService.list(new QueryWrapper<BpmnConfNoticeTemplateDetail>()
                .eq("bpmn_code", bpmnCode)
                .eq("notice_template_type", noticeType)
                .eq("is_del", 0)
                .orderByDesc("id"));
        return bpmnConfNoticeTemplateDetail.isEmpty() ? null : bpmnConfNoticeTemplateDetail.get(0);
    }

    public Integer insert(String bpmnCode) {
        Integer id = this.getBaseMapper().insert(BpmnConfNoticeTemplate.builder()
                .bpmnCode(bpmnCode)
                .build());

        List<BpmnConfNoticeTemplateDetail> list = new ArrayList<>();
        for (MsgNoticeTypeEnum msgNoticeTypeEnum : MsgNoticeTypeEnum.values()) {
            list.add(BpmnConfNoticeTemplateDetail.builder()
                    .bpmnCode(bpmnCode)
                    .noticeTemplateType(msgNoticeTypeEnum.getCode())
                    .noticeTemplateDetail(msgNoticeTypeEnum.getDefaultValue())
                    .build());
        }
        bpmnConfNoticeTemplateDetailService.saveBatch(list);
        return id;
    }
}
