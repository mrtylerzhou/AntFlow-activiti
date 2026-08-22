package org.openoa.engine.lowflow.service;

import com.alibaba.fastjson2.JSON;
import com.google.common.base.Strings;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.LFFieldTypeEnum;
import org.openoa.base.constant.enums.VariantFormContainerTypeEnum;
import org.openoa.base.entity.BpmnConfLfFormdataField;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.FormConfigWrapper;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析低代码表单 widgetList，提取字段元数据。
 * 供流程内联表单保存({@link LFFormDataPreProcessor})和独立表单管理模块共用。
 */
public final class LfFormWidgetParser {

    private LfFormWidgetParser() {
    }

    /**
     * 解析 formdata JSON，返回字段元数据列表。
     *
     * @param formdataJson 表单 JSON
     * @param confId       所属流程配置ID（独立表单可传 null）
     * @param formDataId   表单版本ID(t_bpmn_conf_lf_formdata.id)
     */
    public static List<BpmnConfLfFormdataField> parseFields(String formdataJson, Long confId, Long formDataId) {
        if (Strings.isNullOrEmpty(formdataJson)) {
            throw new AFBizException("lowcode formdata is empty");
        }
        FormConfigWrapper formConfigWrapper = JSON.parseObject(formdataJson, FormConfigWrapper.class);
        List<FormConfigWrapper.LFWidget> lfWidgetList = formConfigWrapper.getWidgetList();
        if (CollectionUtils.isEmpty(lfWidgetList)) {
            throw new AFBizException(Strings.lenientFormat("lowcode form has no widget,confId:%s,formDataId:%s", confId, formDataId));
        }
        List<BpmnConfLfFormdataField> formdataFields = new ArrayList<>();
        parseWidgetListRecursively(lfWidgetList, confId, formDataId, formdataFields);
        if (CollectionUtils.isEmpty(formdataFields)) {
            throw new AFBizException(Strings.lenientFormat("lowcode form fields can not be empty,confId:%s,formDataId:%s", confId, formDataId));
        }
        return formdataFields;
    }

    private static void parseWidgetListRecursively(List<FormConfigWrapper.LFWidget> widgetList, Long confId, Long formDataId, List<BpmnConfLfFormdataField> result) {
        for (FormConfigWrapper.LFWidget lfWidget : widgetList) {
            if (!StringConstants.LOWFLOW_FORM_CONTAINER_TYPE.equals(lfWidget.getCategory())) {
                FormConfigWrapper.LFWidget.LFOption lfOption = lfWidget.getOptions();
                BpmnConfLfFormdataField formdataField = new BpmnConfLfFormdataField();
                formdataField.setBpmnConfId(confId);
                formdataField.setFormDataId(formDataId);
                formdataField.setFieldType(getFieldTypeByTypeString(lfWidget.getType()));
                formdataField.setFieldId(lfOption.getName());
                formdataField.setFieldName(lfOption.getLabel());
                result.add(formdataField);
            } else {
                String containerType = lfWidget.getType();
                VariantFormContainerTypeEnum containerTypeEnum = VariantFormContainerTypeEnum.getByTypeName(containerType);
                if (containerTypeEnum == null) {
                    continue;
                }
                // widgetList 结构: CARD / SUB_FORM / GRID_SUB_FORM / TABLE_SUB_FORM
                if (VariantFormContainerTypeEnum.CARD.equals(containerTypeEnum)
                        || VariantFormContainerTypeEnum.SUB_FORM.equals(containerTypeEnum)
                        || VariantFormContainerTypeEnum.GRID_SUB_FORM.equals(containerTypeEnum)
                        || VariantFormContainerTypeEnum.TABLE_SUB_FORM.equals(containerTypeEnum)) {
                    List<FormConfigWrapper.LFWidget> subWidgetList = lfWidget.getWidgetList();
                    if (!CollectionUtils.isEmpty(subWidgetList)) {
                        parseWidgetListRecursively(subWidgetList, confId, formDataId, result);
                    }
                } else if (VariantFormContainerTypeEnum.TAB.equals(containerTypeEnum)) {
                    List<FormConfigWrapper.LFWidget> tabs = lfWidget.getTabs();
                    for (FormConfigWrapper.LFWidget tab : tabs) {
                        parseWidgetListRecursively(tab.getWidgetList(), confId, formDataId, result);
                    }
                } else {
                    // rows/cols 结构: TABLE / GRID
                    List<FormConfigWrapper.TableRow> rows = lfWidget.getRows();
                    if (!CollectionUtils.isEmpty(rows)) {
                        for (FormConfigWrapper.TableRow row : lfWidget.getRows()) {
                            List<FormConfigWrapper.LFWidget> cols = row.getCols();
                            for (FormConfigWrapper.LFWidget col : cols) {
                                List<FormConfigWrapper.LFWidget> subWidgetList = col.getWidgetList();
                                if (CollectionUtils.isEmpty(subWidgetList)) {
                                    continue;
                                }
                                parseWidgetListRecursively(subWidgetList, confId, formDataId, result);
                            }
                        }
                    } else {
                        List<FormConfigWrapper.LFWidget> cols = lfWidget.getCols();
                        if (!CollectionUtils.isEmpty(cols)) {
                            for (FormConfigWrapper.LFWidget col : cols) {
                                List<FormConfigWrapper.LFWidget> subWidgetList = col.getWidgetList();
                                if (CollectionUtils.isEmpty(subWidgetList)) {
                                    continue;
                                }
                                parseWidgetListRecursively(subWidgetList, confId, formDataId, result);
                            }
                        }
                    }
                }
            }
        }
    }

    private static int getFieldTypeByTypeString(String typeString) {
        switch (typeString) {
            // NUMBER
            case "number":
            case "slider":
                return LFFieldTypeEnum.NUMBER.getType();
            // DATE
            case "date":
                return LFFieldTypeEnum.DATE.getType();
            // DATE_TIME
            case "date-range":
            case "time":
            case "time-range":
                return LFFieldTypeEnum.DATE_TIME.getType();
            // BOOLEAN
            case "switch":
                return LFFieldTypeEnum.BOOLEAN.getType();
            // TEXT (long text)
            case "textarea":
            case "richtext-editor":
                return LFFieldTypeEnum.TEXT.getType();
            // STRING (short text) - default for most form fields
            case "select":
            case "radio":
            case "checkbox":
            case "cascader":
            case "tree-select":
            case "color-picker":
            case "rate":
            case "input":
            case "number-range":
            case "picture-upload":
            case "file-upload":
            case "icon-picker":
            case "transfer":
                return LFFieldTypeEnum.STRING.getType();
            default:
                return LFFieldTypeEnum.STRING.getType();
        }
    }
}
