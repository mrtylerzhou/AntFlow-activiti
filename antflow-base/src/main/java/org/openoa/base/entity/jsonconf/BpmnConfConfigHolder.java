package org.openoa.base.entity.jsonconf;

import org.openoa.base.constant.enums.MsgNoticeTypeEnum;
import org.openoa.base.vo.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper for building BpmnConfConfigJson from BpmnConfVo data during edit flow.
 */
public class BpmnConfConfigHolder {

    private BpmnConfConfigHolder() {
    }

    /**
     * Build complete conf config JSON from BpmnConfVo
     */
    public static BpmnConfConfigJson buildConfConfig(BpmnConfVo confVo) {
        BpmnConfConfigJson config = new BpmnConfConfigJson();

        // View page buttons
        config.setViewPageButtons(buildViewPageButtons(confVo.getViewPageButtons()));

        // Notice template config (from MsgNoticeTypeEnum defaults)
        config.setNoticeTemplateConfig(buildNoticeTemplateConfig());

        // Conf-level templates (where nodeId is null)
        config.setConfTemplates(buildConfTemplates(confVo.getTemplateVos(), confVo.getFormCode()));

        // Low-code form config
        if (confVo.getIsLowCodeFlow() != null && confVo.getIsLowCodeFlow() == 1) {
            config.setLowCodeFormConfig(buildLowCodeFormConfig(confVo));
        }

        // Notice channel types
        if (!CollectionUtils.isEmpty(confVo.getNoticeChannelTypes())) {
            config.setNoticeChannelTypes(confVo.getNoticeChannelTypes());
        }

        return config;
    }

    private static List<BpmnConfConfigJson.ViewPageButton> buildViewPageButtons(BpmnViewPageButtonBaseVo viewPageButtons) {
        if (ObjectUtils.isEmpty(viewPageButtons)) {
            return null;
        }
        List<BpmnConfConfigJson.ViewPageButton> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(viewPageButtons.getViewPageStart())) {
            for (Integer btnType : viewPageButtons.getViewPageStart()) {
                result.add(BpmnConfConfigJson.ViewPageButton.builder()
                        .viewType(1)
                        .buttonType(btnType)
                        .build());
            }
        }
        if (!CollectionUtils.isEmpty(viewPageButtons.getViewPageOther())) {
            for (Integer btnType : viewPageButtons.getViewPageOther()) {
                result.add(BpmnConfConfigJson.ViewPageButton.builder()
                        .viewType(2)
                        .buttonType(btnType)
                        .build());
            }
        }
        return result;
    }

    private static BpmnConfConfigJson.NoticeTemplateConfig buildNoticeTemplateConfig() {
        List<BpmnConfConfigJson.NoticeTemplateDetail> details = new ArrayList<>();
        for (MsgNoticeTypeEnum msgType : MsgNoticeTypeEnum.values()) {
            details.add(BpmnConfConfigJson.NoticeTemplateDetail.builder()
                    .noticeTemplateType(msgType.getCode())
                    .noticeTemplateDetail(msgType.getDefaultValue())
                    .build());
        }
        return BpmnConfConfigJson.NoticeTemplateConfig.builder()
                .details(details)
                .build();
    }

    private static List<BpmnConfConfigJson.ConfTemplateConf> buildConfTemplates(List<BpmnTemplateVo> templateVos, String formCode) {
        if (CollectionUtils.isEmpty(templateVos)) {
            return null;
        }
        return templateVos.stream()
                .map(t -> BpmnConfConfigJson.ConfTemplateConf.builder()
                        .event(t.getEvent())
                        .informs(joinList(t.getInformIdList()))
                        .emps(joinList(t.getEmpIdList()))
                        .roles(joinList(t.getRoleIdList()))
                        .funcs(joinList(t.getFuncIdList()))
                        .templateId(t.getTemplateId())
                        .messageSendType(convertMessageSendTypeList(t.getMessageSendTypeList()))
                        .formCode(formCode)
                        .build())
                .collect(Collectors.toList());
    }

    private static BpmnConfConfigJson.LowCodeFormConfig buildLowCodeFormConfig(BpmnConfVo confVo) {
        BpmnConfConfigJson.LowCodeFormConfig lcConfig = new BpmnConfConfigJson.LowCodeFormConfig();
        lcConfig.setFormdata(confVo.getLfFormData());
        // Note: form fields are built by LFFormDataPreProcessor, handled separately
        return lcConfig;
    }

    private static String joinList(List<?> list) {
        if (CollectionUtils.isEmpty(list)) return null;
        return list.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    private static String convertMessageSendTypeList(List<BaseNumIdStruVo> list) {
        if (CollectionUtils.isEmpty(list)) return null;
        return list.stream().map(Object::toString).collect(Collectors.joining(","));
    }
}
