package org.openoa.engine.bpmnconf.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Strings;
import org.openoa.base.constant.enums.LFFieldTypeEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmnConfLfFormdataField;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.util.DateUtil;
import org.openoa.base.vo.LowFlowBusinessDataVO;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfLfFormdataFieldService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.openoa.engine.bpmnconf.service.interf.repository.LFMainFieldService;
import org.openoa.engine.bpmnconf.service.interf.repository.LFMainService;
import org.openoa.engine.lowflow.entity.LFMain;
import org.openoa.engine.lowflow.entity.LFMainField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 低代码流程业务数据查询接口
 */
@RestController
@RequestMapping("lowFlowBusiness")
public class LowFlowBusinessController {

    private static final Logger log = LoggerFactory.getLogger(LowFlowBusinessController.class);

    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;

    @Autowired
    private BpmnConfService bpmnConfService;

    @Autowired
    private LFMainService lfMainService;

    @Autowired
    private LFMainFieldService lfMainFieldService;

    @Autowired
    private BpmnConfLfFormdataFieldService lfFormdataFieldService;

    /**
     * 根据流程编号查询低代码流程的业务数据
     *
     * @param processNumber 流程编号
     * @return 低代码流程业务数据
     */
    @GetMapping("/getBusinessData")
    public Result<LowFlowBusinessDataVO> getBusinessData(@RequestParam String processNumber) {

        // 1. 根据流程编号查询BpmBusinessProcess表,获取VERSION(bpmnCode)
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if (bpmBusinessProcess == null) {
            throw new AFBizException(Strings.lenientFormat("未找到流程编号为 %s 的流程数据", processNumber));
        }

        String bpmnCode = bpmBusinessProcess.getVersion();
        if (Strings.isNullOrEmpty(bpmnCode)) {
            throw new AFBizException(Strings.lenientFormat("流程编号 %s 的VERSION字段为空", processNumber));
        }

        // 3. 根据confId查询LFMain表
        LFMain lfMain = lfMainService.getOne(
                Wrappers.<LFMain>lambdaQuery()
                        .eq(LFMain::getId, bpmBusinessProcess.getBusinessId())

        );

        if (lfMain == null) {
            throw new AFBizException(Strings.lenientFormat("未找到流程编号 %s 的低代码流程数据", processNumber));
        }

        Long mainId = lfMain.getId();
        String formCode = lfMain.getFormCode();
        Long confId = lfMain.getConfId();

        // 4. 查询字段配置信息
        Map<String, BpmnConfLfFormdataField> fieldConfigMap = lfFormdataFieldService.qryFormDataFieldMap(confId);
        if (CollectionUtils.isEmpty(fieldConfigMap)) {
            throw new AFBizException(Strings.lenientFormat("流程配置ID %s 没有字段配置", confId));
        }

        // 5. 查询字段值数据
        List<LFMainField> lfMainFields = lfMainFieldService.list(
                Wrappers.<LFMainField>lambdaQuery().eq(LFMainField::getMainId, mainId)
        );

        if (CollectionUtils.isEmpty(lfMainFields)) {
            throw new AFBizException(Strings.lenientFormat("低代码流程 formCode:%s, confId:%s 没有表单数据", formCode, confId));
        }

        // 6. 按fieldId分组处理多值字段
        Map<String, List<LFMainField>> fieldId2FieldsMap = lfMainFields.stream()
                .collect(Collectors.groupingBy(LFMainField::getFieldId));

        // 7. 构建返回数据
        List<LowFlowBusinessDataVO.FieldInfo> fieldInfoList = new ArrayList<>();

        for (Map.Entry<String, List<LFMainField>> entry : fieldId2FieldsMap.entrySet()) {
            String fieldId = entry.getKey();
            List<LFMainField> fields = entry.getValue();

            BpmnConfLfFormdataField fieldConfig = fieldConfigMap.get(fieldId);
            if (fieldConfig == null) {
                log.warn("字段 {} 没有配置信息，跳过", fieldId);
                continue;
            }

            // 获取字段值
            Object fieldValue = parseFieldValue(fields, fieldConfig, formCode, confId);

            LowFlowBusinessDataVO.FieldInfo fieldInfo = LowFlowBusinessDataVO.FieldInfo.builder()
                    .fieldId(fieldId)
                    .fieldName(fieldConfig.getFieldName())
                    .fieldLabel(fieldConfig.getFieldName()) // 大部分时候fieldName就是中文注释
                    .fieldType(fieldConfig.getFieldType())
                    .fieldValue(fieldValue)
                    .build();

            fieldInfoList.add(fieldInfo);
        }

        // 8. 构建最终返回对象
        LowFlowBusinessDataVO result = LowFlowBusinessDataVO.builder()
                .mainId(mainId)
                .confId(confId)
                .formCode(formCode)
                .createUser(lfMain.getCreateUser())
                .fields(fieldInfoList)
                .build();

        return Result.newSuccessResult(result);
    }

    /**
     * 解析字段值
     *
     * @param fields     字段数据列表(可能有多条)
     * @param fieldConfig 字段配置
     * @param formCode   表单编码
     * @param confId     配置ID
     * @return 解析后的字段值
     */
    private Object parseFieldValue(List<LFMainField> fields, BpmnConfLfFormdataField fieldConfig,
                                   String formCode, Long confId) {
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }

        Integer fieldType = fieldConfig.getFieldType();
        LFFieldTypeEnum fieldTypeEnum = LFFieldTypeEnum.getByType(fieldType);

        if (fieldTypeEnum == null) {
            throw new AFBizException(Strings.lenientFormat("未识别的字段类型,fieldName:%s, formCode:%s, confId:%d",
                    fieldConfig.getFieldName(), formCode, confId));
        }

        // 如果只有一个值,直接返回
        if (fields.size() == 1) {
            return parseSingleFieldValue(fields.get(0), fieldTypeEnum);
        }

        // 如果有多个值,返回数组
        List<Object> multiValues = new ArrayList<>(fields.size());
        for (LFMainField field : fields) {
            multiValues.add(parseSingleFieldValue(field, fieldTypeEnum));
        }
        return multiValues;
    }

    /**
     * 解析单个字段的值
     *
     * @param field       字段数据
     * @param fieldTypeEnum 字段类型枚举
     * @return 字段值
     */
    private Object parseSingleFieldValue(LFMainField field, LFFieldTypeEnum fieldTypeEnum) {
        switch (fieldTypeEnum) {
            case STRING:
                String value = field.getFieldValue();
                if (value != null) {
                    // 尝试解析JSON对象或数组
                    if (value.startsWith("{")) {
                        try {
                            return com.alibaba.fastjson2.JSON.parseObject(value);
                        } catch (Exception e) {
                            // 解析失败,返回原字符串
                        }
                    } else if (value.startsWith("[")) {
                        try {
                            return com.alibaba.fastjson2.JSON.parseArray(value);
                        } catch (Exception e) {
                            // 解析失败,返回原字符串
                        }
                    }
                }
                return value;

            case NUMBER:
                return field.getFieldValueNumber();

            case DATE_TIME:
                if (field.getFieldValueDt() != null) {
                    return DateUtil.SDF_DATETIME_PATTERN.format(field.getFieldValueDt());
                }
                return null;

            case DATE:
                if (field.getFieldValueDt() != null) {
                    return DateUtil.SDF_DATE_PATTERN.format(field.getFieldValueDt());
                }
                return null;

            case TEXT:
                return field.getFieldValueText();

            case BOOLEAN:
                return Boolean.parseBoolean(field.getFieldValue());

            default:
                return field.getFieldValue();
        }
    }
}
