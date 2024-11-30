package org.openoa.base.vo;

import lombok.Data;

import java.util.List;

@Data
public class FormConfigWrapper {

    private List<LFWidget> widgetList;
    private FormConfig formConfig;

    @Data
    // Nested static class for Widget
    public static class LFWidget {
        private int key;
        private String type;
        private String category;
        private String icon;
        private boolean formItemFlag;
        private LFOption options;
        private String id;
        private List<LFWidget> tabs;
        private List<TableRow> rows;
        private List<LFWidget> cols;
        private List<LFWidget> widgetList;
        @Data
        // Nested static class for Options
        public static class LFOption {
            private String name;
            private String label;
            private String labelAlign;
            private String type;
            /**
             * {@link org.openoa.base.constant.enums.LFFieldTypeEnum}
             */
            private Integer fieldType;
            private String defaultValue;
            private String startPlaceholder;
            private String endPlaceholder;
            private String columnWidth;
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
    public static class TableRow {
       private List<LFWidget> cols;
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