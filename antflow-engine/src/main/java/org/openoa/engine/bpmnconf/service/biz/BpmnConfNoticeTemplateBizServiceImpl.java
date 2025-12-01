package org.openoa.engine.bpmnconf.service.biz;

import org.openoa.base.constant.enums.MsgNoticeTypeEnum;
import org.openoa.base.entity.BpmnConfNoticeTemplate;
import org.openoa.base.entity.BpmnConfNoticeTemplateDetail;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnConfNoticeTemplateBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfNoticeTemplateDetailService;
import org.openoa.base.util.AFWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BpmnConfNoticeTemplateBizServiceImpl implements BpmnConfNoticeTemplateBizService {
    @Autowired
    private BpmnConfNoticeTemplateDetailService bpmnConfNoticeTemplateDetailService;

    @Override
    public BpmnConfNoticeTemplateDetail getDetailByCodeAndType(String bpmnCode, Integer noticeType) {
        List<BpmnConfNoticeTemplateDetail> bpmnConfNoticeTemplateDetail = bpmnConfNoticeTemplateDetailService.list(
                AFWrappers.<BpmnConfNoticeTemplateDetail>lambdaTenantQuery()
                        .eq(BpmnConfNoticeTemplateDetail::getBpmnCode,bpmnCode)
                        .eq(BpmnConfNoticeTemplateDetail::getNoticeTemplateType,noticeType)
                        .orderByDesc(BpmnConfNoticeTemplateDetail::getId));
        return bpmnConfNoticeTemplateDetail.isEmpty() ? null : bpmnConfNoticeTemplateDetail.get(0);
    }

    @Override
    public Integer insert(String bpmnCode) {
        Integer id = this.getMapper().insert(BpmnConfNoticeTemplate.builder()
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
