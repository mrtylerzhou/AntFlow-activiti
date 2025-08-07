package org.openoa.engine.utils;

import org.openoa.base.constant.enums.WildcardCharacterEnum;
import org.openoa.base.vo.InformationTemplateVo;
import org.openoa.base.entity.InformationTemplate;
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
     * translate message template
     *
     * @param informationTemplateVo i
     * @return newly build information template with jump url type
     */
    public InformationTemplateVo translateInformationTemplate(InformationTemplateVo informationTemplateVo) {
        InformationTemplate informationTemplate = Optional
                .ofNullable(informationTemplateService.getBaseMapper().selectById(informationTemplateVo.getId()))
                .orElse(new InformationTemplate());
        return InformationTemplateVo
                .builder()
                .systemTitle(translate(informationTemplate.getSystemTitle(), informationTemplateVo.getWildcardCharacterMap()))
                .systemContent(translate(informationTemplate.getSystemContent(), informationTemplateVo.getWildcardCharacterMap()))
                .mailTitle(translate(informationTemplate.getSystemTitle(), informationTemplateVo.getWildcardCharacterMap()))
                .mailContent(translate(informationTemplate.getSystemContent(), informationTemplateVo.getWildcardCharacterMap()))
                .noteContent(translate(informationTemplate.getNoteContent(), informationTemplateVo.getWildcardCharacterMap()))
                .jumpUrl(informationTemplate.getJumpUrl())
                .build();
    }

    private String translate(String info, Map<Integer, String> map) {
        if (ObjectUtils.isEmpty(info)) {
            return "";
        }
        for (WildcardCharacterEnum o : WildcardCharacterEnum.values()) {
            String pattern= o.getTransfDesc() /*+ "\\(" + o.getCode() + "\\)"*/;
            String replacement=!ObjectUtils.isEmpty(map.get(o.getCode())) ? map.get(o.getCode()) : "";
            info = info.replaceAll(pattern, replacement);
        }
        return info;
    }

}
