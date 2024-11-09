package org.openoa.engine.lowflow.entity;

import lombok.Data;

import java.util.List;

public class FormConfigWrapper {

    private List<LFWidget> LFWidgetList;
    private FormConfig formConfig;

    @Data
    // Nested static class for Widget
    public static class LFWidget {
        private int key;
        private String type;
        private String icon;
        private boolean formItemFlag;
        private LFOption options;
        private String id;


        @Data
        // Nested static class for Options
        public static class LFOption {
            private String name;
            private String label;
            private String labelAlign;
            private String type;
            private String defaultValue;
            private String startPlaceholder;
            private String endPlaceholder;
            private Double columnWidth;
            private String size;
            private Boolean autoFullWidth;
            private Double labelWidth;
            private Boolean labelHidden;
            private Boolean readonly;
            private Boolean disabled;
            private Boolean hidden;
            private Boolean clearable;
            private Boolean editable;
            private String format;
            private String valueFormat;
            private Boolean required;
            private String requiredHint;
            private String validation;
            private String validationHint;
            private String customClass;
            private Object labelIconClass;
            private String labelIconPosition;
            private String labelTooltip;
            private String onCreated;
            private String onMounted;
            private String onChange;
            private String onFocus;
            private String onBlur;
            private String onValidate;
        }
    }

    @Data
    public class FormConfig {
        private String modelName;
        private String refName;
        private String rulesName;
        private int labelWidth; // 假设labelWidth是整数类型，如果不是请根据实际情况修改
        private String labelPosition;
        private String size;
        private String labelAlign;
        private String cssCode;
        private String[] customClass; // 假设customClass是字符串数组，如果不是请根据实际情况修改
        private String functions;
        private String layoutType;
        private int jsonVersion; // 假设jsonVersion是整数类型，如果不是请根据实际情况修改
        private String onFormCreated;
        private String onFormMounted;
        private String onFormDataChange;

    }
}