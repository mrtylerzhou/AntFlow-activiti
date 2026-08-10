package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openoa.base.constant.enums.MsgNoticeTypeEnum;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.BpmnConfNoticeTemplateDetail;
import org.openoa.base.entity.jsonconf.BpmnConfConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnConfNoticeTemplateBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class BpmnConfNoticeTemplateBizServiceImpl implements BpmnConfNoticeTemplateBizService {
    @Autowired
    private BpmnConfService bpmnConfService;

    /**
     * Get notice template detail by bpmnCode and noticeType.
     * Reads exclusively from conf_config_json — no DB table fallback.
     * If JSON is missing or does not contain the requested type, returns a
     * synthetic detail populated with the enum default value, so callers
     * (e.g. ActivitiBpmMsgTemplateServiceImpl#getContent) never get null.
     */
    @Override
    public BpmnConfNoticeTemplateDetail getDetailByCodeAndType(String bpmnCode, Integer noticeType) {
        BpmnConfNoticeTemplateDetail jsonResult = getDetailFromConfJson(bpmnCode, noticeType);
        if (jsonResult != null) {
            return jsonResult;
        }
        // JSON not available or type not found — return default value
        return BpmnConfNoticeTemplateDetail.builder()
                .bpmnCode(bpmnCode)
                .noticeTemplateType(noticeType)
                .noticeTemplateDetail(MsgNoticeTypeEnum.getDefaultValueByCode(noticeType))
                .build();
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

}
