package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openoa.base.constant.enums.MsgNoticeTypeEnum;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.BpmnConfNoticeTemplate;
import org.openoa.base.entity.BpmnConfNoticeTemplateDetail;
import org.openoa.base.entity.jsonconf.BpmnConfConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnConfNoticeTemplateBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfNoticeTemplateDetailService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.openoa.base.util.AFWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class BpmnConfNoticeTemplateBizServiceImpl implements BpmnConfNoticeTemplateBizService {
    @Autowired
    private BpmnConfNoticeTemplateDetailService bpmnConfNoticeTemplateDetailService;
    @Autowired
    private BpmnConfService bpmnConfService;

    @Override
    public BpmnConfNoticeTemplateDetail getDetailByCodeAndType(String bpmnCode, Integer noticeType) {
        BpmnConfNoticeTemplateDetail jsonResult = getDetailFromConfJson(bpmnCode, noticeType);
        if (jsonResult != null) {
            return jsonResult;
        }
        List<BpmnConfNoticeTemplateDetail> bpmnConfNoticeTemplateDetail = bpmnConfNoticeTemplateDetailService.list(
                AFWrappers.<BpmnConfNoticeTemplateDetail>lambdaTenantQuery()
                        .eq(BpmnConfNoticeTemplateDetail::getBpmnCode,bpmnCode)
                        .eq(BpmnConfNoticeTemplateDetail::getNoticeTemplateType,noticeType)
                        .orderByDesc(BpmnConfNoticeTemplateDetail::getId));
        return bpmnConfNoticeTemplateDetail.isEmpty() ? null : bpmnConfNoticeTemplateDetail.get(0);
    }

    private BpmnConfNoticeTemplateDetail getDetailFromConfJson(String bpmnCode, Integer noticeType) {
        BpmnConf conf = bpmnConfService.getOne(new QueryWrapper<BpmnConf>()
                .eq("bpmn_code", bpmnCode)
                .eq("effective_status", 1)
                .isNotNull("conf_config_json")
                .last("LIMIT 1"));
        if (conf == null || conf.getConfConfigJson() == null) {
            return null;
        }
        BpmnConfConfigJson confConfig = JsonConfUtil.parseConfConfig(conf.getConfConfigJson());
        if (confConfig == null || confConfig.getNoticeTemplateConfig() == null
                || CollectionUtils.isEmpty(confConfig.getNoticeTemplateConfig().getDetails())) {
            return null;
        }
        for (BpmnConfConfigJson.NoticeTemplateDetail detail : confConfig.getNoticeTemplateConfig().getDetails()) {
            if (noticeType.equals(detail.getNoticeTemplateType())) {
                BpmnConfNoticeTemplateDetail result = new BpmnConfNoticeTemplateDetail();
                result.setBpmnCode(bpmnCode);
                result.setNoticeTemplateType(detail.getNoticeTemplateType());
                result.setNoticeTemplateDetail(detail.getNoticeTemplateDetail());
                return result;
            }
        }
        return null;
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
