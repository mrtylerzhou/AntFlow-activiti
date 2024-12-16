package org.openoa.controller;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.util.DateUtil;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;

public abstract class BaseController {

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        // String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text == null ? null : StringEscapeUtils.escapeHtml4(text.trim()));
            }

            @Override
            public String getAsText() {
                Object value = getValue();
                return value != null ? value.toString() : "";
            }
        });

        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                try {
                    if (StringUtils.isNotEmpty(text)) {
                        if (text.contains(" ")) {
                            setValue(DateUtil.SDF_DATETIME_PATTERN.parse(text));
                        } else {
                            setValue(DateUtil.SDF_DATE_PATTERN.parse(text));
                        }
                    }
                } catch (ParseException e) {
                    throw new HttpMessageConversionException(e.getMessage());
                }
            }
        });

    }


}
