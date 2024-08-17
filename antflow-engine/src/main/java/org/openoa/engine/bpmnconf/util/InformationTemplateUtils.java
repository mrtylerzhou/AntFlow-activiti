package org.openoa.engine.bpmnconf.util;

import org.openoa.base.constant.enums.WildcardCharacterEnum;
import org.openoa.base.vo.InformationTemplateVo;
import org.openoa.engine.bpmnconf.confentity.InformationTemplate;
import org.openoa.engine.bpmnconf.service.impl.InformationTemplateServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

@Component
public class InformationTemplateUtils {

    @Resource
    private InformationTemplateServiceImpl informationTemplateService;

    /**
     * 翻译消息模板方法
     *
     * @param informationTemplateVo id和通配符map
     * @return 翻译后的消息以及跳转类型
     */
    public InformationTemplateVo translateInformationTemplate(InformationTemplateVo informationTemplateVo) {
        InformationTemplate informationTemplate = Optional
                .ofNullable(informationTemplateService.getBaseMapper().selectById(informationTemplateVo.getId()))
                .orElse(new InformationTemplate());
        return InformationTemplateVo
                .builder()
                .systemTitle(translate(informationTemplate.getSystemTitle(), informationTemplateVo.getWildcardCharacterMap()))
                .systemContent(translate(informationTemplate.getSystemContent(), informationTemplateVo.getWildcardCharacterMap()))
                .mailTitle(translate(informationTemplate.getMailTitle(), informationTemplateVo.getWildcardCharacterMap()))
                .mailContent(translate(informationTemplate.getMailContent(), informationTemplateVo.getWildcardCharacterMap()))
                .noteContent(translate(informationTemplate.getNoteContent(), informationTemplateVo.getWildcardCharacterMap()))
                .jumpUrl(informationTemplate.getJumpUrl())
                .build();
    }

    private String translate(String info, Map<Integer, String> map) {
        if (!ObjectUtils.isEmpty(info)) {
            for (WildcardCharacterEnum o : WildcardCharacterEnum.values()) {
                info = info.replaceAll("@\\[" + o.getTransfDesc() + "\\]\\(" + o.getCode() + "\\)",
                        !ObjectUtils.isEmpty(map.get(o.getCode()))
                                ? map.get(o.getCode())
                                : "");
            }
            return info;
        }
        return "";
    }

}
