package org.openoa.engine.lowflow.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Strings;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.constant.enums.LFControlTypeEnum;
import org.openoa.base.constant.enums.LFFieldTypeEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.constant.enums.ProcessNodeEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.DateUtil;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.SnowFlake;
import org.openoa.base.vo.*;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.BpmnConfLfFormdata;
import org.openoa.base.entity.BpmnConfLfFormdataField;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.jsonconf.BpmnNodeApproverConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.BpmnNodeLowCodeConfJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.engine.bpmnconf.mapper.BpmnConfLfFormdataMapper;
import org.openoa.engine.bpmnconf.adp.processoperation.AbstractFormOperationAdaptor;
import org.openoa.engine.bpmnconf.service.interf.repository.*;
import org.openoa.engine.lowflow.entity.LFMain;
import org.openoa.engine.lowflow.entity.LFMainField;
import org.openoa.base.vo.UDLFApplyVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;
/**
 * desc = 拖拽表单低代码审批流
 * */
@ActivitiServiceAnno(svcName = StringConstants.LOWFLOW_FORM_CODE,desc = "")
public class LowFlowApprovalService extends AbstractFormOperationAdaptor<UDLFApplyVo> implements ActivitiService {
    private static final Logger log = LoggerFactory.getLogger(LowFlowApprovalService.class);
    //key is confid,value is a list of condition fields names which belongs to this conf
    private static Map<Long,List<String>> conditionFieldNameMap=new HashMap<>();
    // key is confid,value is a map of field's name and its self
    private static Map<Long,Map<String,BpmnConfLfFormdataField>> allFieldConfMap =new HashMap<>();
    // key is formdataId,value is a map of field's name and its self (external form mode)
    private static Map<Long,Map<String,BpmnConfLfFormdataField>> allFieldConfMapByFormdataId =new HashMap<>();
    @Autowired
    private BpmnConfLfFormdataFieldService lfFormdataFieldService;
    @Autowired
    private LFMainFieldService mainFieldService;
    @Autowired
    private LFMainService mainService;
    @Autowired
    private BpmnConfLfFormdataService lfFormdataService;
    @Autowired
    private BpmnNodeService bpmnNodeService;
    @Autowired
    private BpmnConfLfFormdataMapper lfFormdataMapper;
    @Autowired
    private BpmnConfService bpmnConfService;

    @Override
    public BpmnStartConditionsVo previewSetCondition(UDLFApplyVo vo) {
        flattenLfFieldsMultiIfNeeded(vo);
        String userId =  vo.getStartUserId();
        BpmnStartConditionsVo startConditionsVo = BpmnStartConditionsVo.builder()
                .isLowCodeFlow(true)
                .startUserId(userId)
                .build();
        if(!CollectionUtils.isEmpty(vo.getLfConditions())){
            startConditionsVo.setLfConditions(vo.getLfConditions());
        }else {
            startConditionsVo.setLfConditions(vo.getLfFields());
        }
        BpmnConfVo bpmnConfVo = vo.getBpmnConfVo();
        processFormRelatedUserConf(bpmnConfVo,vo);
        startConditionsVo.setBusinessDataVo(vo);
        return startConditionsVo;
    }

    @Override
    public void initData(UDLFApplyVo vo) {

    }

    @Override
    public BpmnStartConditionsVo launchParameters(UDLFApplyVo vo) {
        flattenLfFieldsMultiIfNeeded(vo);
        String userId =  vo.getStartUserId();
        BpmnStartConditionsVo startConditionsVo = BpmnStartConditionsVo.builder()
                .isLowCodeFlow(true)
                .startUserId(userId)
                .build();
        if(!CollectionUtils.isEmpty(vo.getLfConditions())){
            startConditionsVo.setLfConditions(vo.getLfConditions());
        }else {
            startConditionsVo.setLfConditions(vo.getLfFields());
        }
        BpmnConfVo bpmnConfVo = vo.getBpmnConfVo();
        processFormRelatedUserConf(bpmnConfVo,vo);
        return startConditionsVo;
    }

    @Override
    public Boolean autoCondition(UDLFApplyVo businessDataVo) {
        return null;
    }

    @Override
    public void automaticAction(UDLFApplyVo businessDataVo,Boolean conditionResult) {

    }

    @Override
    public void queryData(UDLFApplyVo vo) {
        LFMain lfMain = mainService.getById(vo.getBusinessId());
        if(lfMain==null){
            log.error("can not get lowcode from data by specified Id:{}",vo.getBusinessId());
            throw new AFBizException("can not get lowcode form data by specified id");
        }
        Long mainId = lfMain.getId();
        Long confId = lfMain.getConfId();
        String formCode = lfMain.getFormCode();

        // 外部表单模式: 按 lf_formdata_ids 加载多表单
        BpmnConf bpmnConf = bpmnConfService.getById(confId);
        if(bpmnConf != null && BpmnConfFlagsEnum.USE_EXTERNAL_FORM.flagsContainsCurrent(bpmnConf.getExtraFlags())){
            queryDataExternal(vo, bpmnConf, mainId, confId);
            return;
        }

        // 内联表单模式: 兼容旧逻辑,加载单个表单
        Map<String, BpmnConfLfFormdataField> lfFormdataFieldMap = allFieldConfMap.get(confId);
        if(CollectionUtils.isEmpty(lfFormdataFieldMap)){
            Map<String, BpmnConfLfFormdataField> Id2SelfMap = lfFormdataFieldService.qryFormDataFieldMap(confId);
            allFieldConfMap.put(confId,Id2SelfMap);
        }
        lfFormdataFieldMap=allFieldConfMap.get(confId);
        List<LFMainField> lfMainFields = mainFieldService.listByMainIdAndFormCode(mainId, formCode);
        if(CollectionUtils.isEmpty(lfMainFields)){
            throw  new AFBizException(Strings.lenientFormat("lowcode form with formcode:%s,confid:%s has no formdata",formCode,confId));
        }
        Map<String,Object> fieldVoMap = buildFieldVoMap(lfMainFields, lfFormdataFieldMap, formCode, confId);
        vo.setLfFields(fieldVoMap);

        List<BpmnConfLfFormdata> bpmnConfLfFormdataList = lfFormdataService.list(Wrappers.<BpmnConfLfFormdata>lambdaQuery().eq(BpmnConfLfFormdata::getBpmnConfId, confId));
        if(CollectionUtils.isEmpty(bpmnConfLfFormdataList)){
            throw  new AFBizException(Strings.lenientFormat("can not get lowcode flow formdata by confId:%s",confId));
        }
        BpmnConfLfFormdata lfFormdata = bpmnConfLfFormdataList.get(0);
        vo.setLfFormData(lfFormdata.getFormdata());
    }

    /**
     * 外部表单模式 queryData: 按 lf_formdata_ids 加载多表单定义及字段值
     */
    private void queryDataExternal(UDLFApplyVo vo, BpmnConf bpmnConf, Long mainId, Long confId) {
        String lfFormdataIds = bpmnConf.getLfFormdataIds();
        if(Strings.isNullOrEmpty(lfFormdataIds)){
            throw new AFBizException(Strings.lenientFormat("external form mode but lf_formdata_ids is empty, confId:%s", confId));
        }
        List<Long> ids = Arrays.stream(lfFormdataIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<BpmnConfLfFormdata> forms = lfFormdataMapper.listByIdsIgnoreDeleted(ids);
        if(CollectionUtils.isEmpty(forms)){
            throw new AFBizException(Strings.lenientFormat("can not get external forms by ids:%s", lfFormdataIds));
        }

        List<LFMainField> allMainFields = mainFieldService.listByMainId(mainId);
        Map<Long, List<LFMainField>> fieldsByFormdataId = allMainFields.stream()
                .collect(Collectors.groupingBy(f -> f.getFormdataId() != null ? f.getFormdataId() : -1L));

        Map<String, Map<String, Object>> lfFieldsMulti = new LinkedHashMap<>();
        Map<String, Object> flatFields = new HashMap<>();
        for (BpmnConfLfFormdata form : forms) {
            Long formdataId = form.getId();
            List<LFMainField> formFields = fieldsByFormdataId.get(formdataId);
            if(CollectionUtils.isEmpty(formFields)){
                // 该表单无数据(可能后加的表单),给空Map
                lfFieldsMulti.put(String.valueOf(formdataId), new HashMap<>());
                continue;
            }
            Map<String, BpmnConfLfFormdataField> fieldConfMap = allFieldConfMapByFormdataId.get(formdataId);
            if(CollectionUtils.isEmpty(fieldConfMap)){
                fieldConfMap = lfFormdataFieldService.qryFieldMapByFormdataId(formdataId);
                allFieldConfMapByFormdataId.put(formdataId, fieldConfMap);
            }
            Map<String, Object> fieldVoMap = buildFieldVoMap(formFields, fieldConfMap, form.getFormCode(), confId);
            lfFieldsMulti.put(String.valueOf(formdataId), fieldVoMap);
            flatFields.putAll(fieldVoMap);
        }
        vo.setLfFieldsMulti(lfFieldsMulti);
        vo.setLfFields(flatFields);
        vo.setLfFormdataList(forms);
    }

    @Override
    public void submitData(UDLFApplyVo vo) {
        BpmnConfVo bpmnConfVo = vo.getBpmnConfVo();
        // 外部表单模式
        if(BpmnConfFlagsEnum.USE_EXTERNAL_FORM.flagsContainsCurrent(bpmnConfVo.getExtraFlags())){
            submitDataExternal(vo, bpmnConfVo);
            return;
        }
        // 内联表单模式
        Map<String, Object> lfFields = vo.getLfFields();
        if(CollectionUtils.isEmpty(lfFields)){
            throw new AFBizException("form data does not contains any field");
        }
        //判断字段值是否超长，主要是判断vform表单中的富文本编辑器
        for (Map.Entry<String, Object> entry : lfFields.entrySet()) {
            Object value = entry.getValue();
            String valueStr = value == null ? "" : value.toString();
            if (valueStr.length() > 2000) {
                entry.setValue("该字段超出了表字段设计的最大长度，不做存储，防止antflow表字段长度溢出");
            }
        }
        Long confId =bpmnConfVo.getId();
        String formCode = vo.getFormCode();
        String currentTenantId = MultiTenantUtil.getCurrentTenantId();
        LFMain main = new LFMain();
        main.setTenantId(currentTenantId);
        main.setId(SnowFlake.nextId());
        main.setConfId(confId);
        main.setFormCode(formCode);
        main.setCreateUser(SecurityUtils.getLogInEmpName());
        mainService.save(main);
        Long mainId = main.getId();

        // 发起人节点字段权限校验: 过滤掉隐藏(H)字段,防止前端绕过
        List<LFFieldControlVO> startFieldControls = getFieldControlsFromJson(confId, ProcessNodeEnum.START_TASK_KEY.getDesc());
        if (!CollectionUtils.isEmpty(startFieldControls)) {
            lfFields.entrySet().removeIf(entry -> {
                LFFieldControlVO ctrl = startFieldControls.stream()
                        .filter(c -> c.getFieldId().equals(entry.getKey()))
                        .findFirst().orElse(null);
                return ctrl != null && StringConstants.HIDDEN_FIELD_PERMISSION.equals(ctrl.getPerm());
            });
        }

        Map<String, BpmnConfLfFormdataField> lfFormdataFieldMap = allFieldConfMap.get(confId);
        if(CollectionUtils.isEmpty(lfFormdataFieldMap)){
            Map<String, BpmnConfLfFormdataField> name2SelfMap = lfFormdataFieldService.qryFormDataFieldMap(confId);
            allFieldConfMap.put(confId,name2SelfMap);
        }
        Map<String, BpmnConfLfFormdataField> fieldConfMap = allFieldConfMap.get(confId);
        if(CollectionUtils.isEmpty(fieldConfMap)){
            throw  new AFBizException(Strings.lenientFormat("confId %s,formCode:%s does not has a field config",confId,vo.getFormCode()));
        }
        List<LFMainField> mainFields = LFMainField.parseFromMap(lfFields, fieldConfMap, mainId,formCode);
        mainFieldService.saveBatch(mainFields);
        vo.setBusinessId(mainId.toString());
        vo.setProcessDigest(vo.getRemark());
        vo.setEntityName(LowFlowApprovalService.class.getSimpleName());
    }

    /**
     * 外部表单模式 submitData: 按 formdataId 分组保存字段值
     */
    private void submitDataExternal(UDLFApplyVo vo, BpmnConfVo bpmnConfVo) {
        Map<String, Map<String, Object>> lfFieldsMulti = vo.getLfFieldsMulti();
        if(CollectionUtils.isEmpty(lfFieldsMulti)){
            throw new AFBizException("form data does not contains any field");
        }
        //判断字段值是否超长
        for (Map<String, Object> formFields : lfFieldsMulti.values()) {
            if(formFields == null) continue;
            for (Map.Entry<String, Object> entry : formFields.entrySet()) {
                Object value = entry.getValue();
                String valueStr = value == null ? "" : value.toString();
                if (valueStr.length() > 2000) {
                    entry.setValue("该字段超出了表字段设计的最大长度，不做存储，防止antflow表字段长度溢出");
                }
            }
        }
        Long confId = bpmnConfVo.getId();
        String formCode = vo.getFormCode();
        String currentTenantId = MultiTenantUtil.getCurrentTenantId();
        LFMain main = new LFMain();
        main.setTenantId(currentTenantId);
        main.setId(SnowFlake.nextId());
        main.setConfId(confId);
        main.setFormCode(formCode);
        main.setCreateUser(SecurityUtils.getLogInEmpName());
        mainService.save(main);
        Long mainId = main.getId();

        List<LFMainField> allMainFields = new ArrayList<>();

        // 发起人节点字段权限校验: 过滤隐藏表单和隐藏字段
        BpmnNodeLowCodeConfJson startLowCodeConf = getLowCodeConfJson(confId, ProcessNodeEnum.START_TASK_KEY.getDesc());
        Map<String, Boolean> startFormHidden = (startLowCodeConf != null) ? startLowCodeConf.getFormHidden() : null;
        List<BpmnNodeLowCodeConfJson.FieldControl> startFieldControls =
                (startLowCodeConf != null && startLowCodeConf.getFieldControls() != null) ? startLowCodeConf.getFieldControls() : Collections.emptyList();

        for (Map.Entry<String, Map<String, Object>> entry : lfFieldsMulti.entrySet()) {
            Long formdataId = Long.parseLong(entry.getKey());
            // 整表隐藏: 跳过该表单
            if (startFormHidden != null && Boolean.TRUE.equals(startFormHidden.get(String.valueOf(formdataId)))) {
                continue;
            }
            Map<String, Object> fields = entry.getValue();
            if(CollectionUtils.isEmpty(fields)){
                continue;
            }
            // 过滤隐藏字段
            if (!CollectionUtils.isEmpty(startFieldControls)) {
                fields.entrySet().removeIf(fieldEntry -> {
                    BpmnNodeLowCodeConfJson.FieldControl ctrl = startFieldControls.stream()
                            .filter(c -> Objects.equals(c.getFormdataId(), formdataId) && c.getFieldId().equals(fieldEntry.getKey()))
                            .findFirst().orElse(null);
                    return ctrl != null && StringConstants.HIDDEN_FIELD_PERMISSION.equals(ctrl.getPerm());
                });
            }
            Map<String, BpmnConfLfFormdataField> fieldConfMap = allFieldConfMapByFormdataId.get(formdataId);
            if(CollectionUtils.isEmpty(fieldConfMap)){
                fieldConfMap = lfFormdataFieldService.qryFieldMapByFormdataId(formdataId);
                allFieldConfMapByFormdataId.put(formdataId, fieldConfMap);
            }
            List<LFMainField> mainFields = LFMainField.parseFromMap(fields, fieldConfMap, mainId, formCode, formdataId);
            allMainFields.addAll(mainFields);
        }
        if(!CollectionUtils.isEmpty(allMainFields)){
            mainFieldService.saveBatch(allMainFields);
        }
        vo.setBusinessId(mainId.toString());
        vo.setProcessDigest(vo.getRemark());
        vo.setEntityName(LowFlowApprovalService.class.getSimpleName());
    }

    @Override
    public void consentData(UDLFApplyVo vo) {
        if (!vo.getOperationType().equals(ButtonTypeEnum.BUTTON_TYPE_RESUBMIT.getCode()) && !vo.getOperationType().equals(ButtonTypeEnum.BUTTON_TYPE_AGREE.getCode()) ){
            return ;
        }
        BpmnConfVo bpmnConfVo = vo.getBpmnConfVo();
        // 外部表单模式
        if(BpmnConfFlagsEnum.USE_EXTERNAL_FORM.flagsContainsCurrent(bpmnConfVo.getExtraFlags())){
            consentDataExternal(vo, bpmnConfVo);
            return;
        }
        // 内联表单模式
        Map<String, Object> lfFields = vo.getLfFields();
        if(CollectionUtils.isEmpty(lfFields)){
            throw new AFBizException("form data does not contains any field");
        }
        LFMain lfMain = mainService.getById(vo.getBusinessId());
        if(lfMain==null){
            log.error("can not get lowcode from data by specified Id:{}",vo.getBusinessId());
            throw new AFBizException("can not get lowcode form data by specified id");
        }
        Long mainId = lfMain.getId();
        String formCode = vo.getFormCode();
        Long confId = bpmnConfVo.getId();
        List<LFMainField> lfMainFields = mainFieldService.listByMainIdAndFormCode(mainId, formCode);
	    // 如果vo.getLfFields()里面有lfMainFields没有的元素，那么就将没有的元素save到LFMainField表中
	    Map<String, Object> submitLfFields = vo.getLfFields();
	    if (ObjectUtils.isNotEmpty(submitLfFields)) {
		    Map<String, BpmnConfLfFormdataField> lfFormdataFieldMap = allFieldConfMap.get(confId);
		    if (ObjectUtils.isEmpty(lfFormdataFieldMap)) {
			    Map<String, BpmnConfLfFormdataField> name2SelfMap = lfFormdataFieldService.qryFormDataFieldMap(confId);
			    allFieldConfMap.put(confId,name2SelfMap);
		    }
		    Map<String, BpmnConfLfFormdataField> fieldConfMap = allFieldConfMap.get(confId);
		    if (ObjectUtils.isEmpty(fieldConfMap)) {
			    throw new AFBizException(Strings.lenientFormat("confId %s,formCode:%s does not has a field config",confId,vo.getFormCode()));
		    }
		    List<LFMainField> mainFields = LFMainField.parseFromMap(submitLfFields, fieldConfMap, mainId, vo.getFormCode());
		    if (CollectionUtils.isNotEmpty(mainFields)) {
			    // 根据fieldId过滤掉已存在表里的数据lfMainFields
			    mainFields.removeIf(mainField -> lfMainFields.stream().anyMatch(ori -> ori.getFieldId().equals(mainField.getFieldId())));
			    mainFieldService.saveBatch(mainFields);
		    }
	    }
		if(CollectionUtils.isEmpty(lfMainFields)){
            throw  new AFBizException(Strings.lenientFormat("lowcode form with formcode:%s,confid:%s has no formdata",formCode,confId));
        }
        List<LFFieldControlVO> currentFieldControls = getFieldControlsFromJson(confId, vo.getTaskDefKey());

        for (LFMainField field : lfMainFields){
            if(!CollectionUtils.isEmpty(currentFieldControls)){
                LFFieldControlVO lfFieldControlVO = currentFieldControls.stream().filter(control -> control.getFieldId().equals(field.getFieldId())).findFirst().orElse(null);
                if(lfFieldControlVO!=null
                        &&(StringConstants.HIDDEN_FIELD_PERMISSION.equals(lfFieldControlVO.getPerm()) ||StringConstants.READ_ONLY_FIELD_PERMISSION.equals(lfFieldControlVO.getPerm())))
                {
                          continue;
                }
            }
            if (lfFields.containsKey(field.getFieldId()) && lfFields.get(field.getFieldId()) != null) {
                String f_value = lfFields.get(field.getFieldId()).toString();
                if (!Objects.equals(f_value, "******")){
                    field.setFieldValue(f_value);
                }
                mainFieldService.updateById(field);
            }
        }

    }

    /**
     * 外部表单模式 consentData: 尊重 formHidden(整表隐藏) + 按 formdataId 匹配字段权限
     */
    private void consentDataExternal(UDLFApplyVo vo, BpmnConfVo bpmnConfVo) {
        Map<String, Map<String, Object>> lfFieldsMulti = vo.getLfFieldsMulti();
        if(CollectionUtils.isEmpty(lfFieldsMulti)){
            throw new AFBizException("form data does not contains any field");
        }
        LFMain lfMain = mainService.getById(vo.getBusinessId());
        if(lfMain==null){
            log.error("can not get lowcode from data by specified Id:{}",vo.getBusinessId());
            throw new AFBizException("can not get lowcode form data by specified id");
        }
        Long mainId = lfMain.getId();
        String formCode = vo.getFormCode();
        Long confId = bpmnConfVo.getId();

        List<LFMainField> allMainFields = mainFieldService.listByMainId(mainId);
        if(CollectionUtils.isEmpty(allMainFields)){
            throw new AFBizException(Strings.lenientFormat("lowcode form with formcode:%s,confid:%s has no formdata",formCode,confId));
        }

        // 获取节点级配置: formHidden + fieldControls
        BpmnNodeLowCodeConfJson lowCodeConf = getLowCodeConfJson(confId, vo.getTaskDefKey());
        Map<String, Boolean> formHidden = (lowCodeConf != null) ? lowCodeConf.getFormHidden() : null;
        List<BpmnNodeLowCodeConfJson.FieldControl> fieldControls =
                (lowCodeConf != null && lowCodeConf.getFieldControls() != null) ? lowCodeConf.getFieldControls() : Collections.emptyList();

        // 保存新增字段(提交数据中有但DB中没有的),按 formdataId 分组
        Map<Long, List<LFMainField>> existingByFormdataId = allMainFields.stream()
                .collect(Collectors.groupingBy(f -> f.getFormdataId() != null ? f.getFormdataId() : -1L));
        List<LFMainField> newFields = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : lfFieldsMulti.entrySet()) {
            Long formdataId = Long.parseLong(entry.getKey());
            Map<String, Object> submitFields = entry.getValue();
            if(CollectionUtils.isEmpty(submitFields)){
                continue;
            }
            List<LFMainField> existingFields = existingByFormdataId.getOrDefault(formdataId, Collections.emptyList());
            Map<String, BpmnConfLfFormdataField> fieldConfMap = allFieldConfMapByFormdataId.get(formdataId);
            if(CollectionUtils.isEmpty(fieldConfMap)){
                fieldConfMap = lfFormdataFieldService.qryFieldMapByFormdataId(formdataId);
                allFieldConfMapByFormdataId.put(formdataId, fieldConfMap);
            }
            List<LFMainField> parsed = LFMainField.parseFromMap(submitFields, fieldConfMap, mainId, formCode, formdataId);
            // 过滤掉已存在的fieldId
            parsed.removeIf(nf -> existingFields.stream().anyMatch(ori -> ori.getFieldId().equals(nf.getFieldId())));
            newFields.addAll(parsed);
        }
        if(!CollectionUtils.isEmpty(newFields)){
            mainFieldService.saveBatch(newFields);
            allMainFields.addAll(newFields);
        }

        // 更新已有字段值,尊重 formHidden 和字段级权限
        for (LFMainField field : allMainFields){
            Long formdataId = field.getFormdataId();
            // 整表隐藏的表单不更新
            if(formHidden != null && Boolean.TRUE.equals(formHidden.get(formdataId))){
                continue;
            }
            // 字段级权限检查: 同时匹配 formdataId 和 fieldId
            if(!CollectionUtils.isEmpty(fieldControls)){
                BpmnNodeLowCodeConfJson.FieldControl ctrl = fieldControls.stream()
                        .filter(c -> Objects.equals(c.getFormdataId(), formdataId) && c.getFieldId().equals(field.getFieldId()))
                        .findFirst().orElse(null);
                if(ctrl != null
                        && (StringConstants.HIDDEN_FIELD_PERMISSION.equals(ctrl.getPerm())
                            || StringConstants.READ_ONLY_FIELD_PERMISSION.equals(ctrl.getPerm()))){
                    continue;
                }
            }
            Map<String, Object> formFields = lfFieldsMulti.get(String.valueOf(formdataId));
            if(formFields != null && formFields.containsKey(field.getFieldId()) && formFields.get(field.getFieldId()) != null){
                String f_value = formFields.get(field.getFieldId()).toString();
                if (!Objects.equals(f_value, "******")){
                    field.setFieldValue(f_value);
                }
                mainFieldService.updateById(field);
            }
        }
    }

    @Override
    public void backToModifyData(UDLFApplyVo vo) {

    }

    @Override
    public void cancellationData(UDLFApplyVo vo) {

    }

    @Override
    public void onProcessRecover(BusinessDataVo businessData) {

    }

    @Override
    public void finishData(BusinessDataVo vo) {

    }

    /**
     * 外部表单模式: 将 lfFieldsMulti 展平到 lfFields, 使既有的条件求值/表单取人逻辑无需改动
     */
    private void flattenLfFieldsMultiIfNeeded(UDLFApplyVo vo) {
        Map<String, Map<String, Object>> multi = vo.getLfFieldsMulti();
        if (multi != null && !multi.isEmpty()) {
            Map<String, Object> flat = new HashMap<>();
            for (Map<String, Object> formFields : multi.values()) {
                if (formFields != null) {
                    flat.putAll(formFields);
                }
            }
            vo.setLfFields(flat);
        }
    }

    /**
     * 从节点配置JSON中读取低代码表单配置(formHidden + fieldControls)
     */
    private BpmnNodeLowCodeConfJson getLowCodeConfJson(Long confId, String elementId) {
        if (confId == null || StringUtils.isEmpty(elementId)) {
            return null;
        }
        BpmnNode node = bpmnNodeService.getOne(Wrappers.<BpmnNode>lambdaQuery()
                .eq(BpmnNode::getConfId, confId)
                .eq(BpmnNode::getNodeId, elementId)
                .eq(BpmnNode::getIsDel, 0)
                .last("LIMIT 1"));
        if (node == null) {
            return null;
        }
        String nodeConfigJson = node.getNodeConfigJson();
        if (StringUtils.isEmpty(nodeConfigJson)) {
            return null;
        }
        BpmnNodeConfigJson nodeConfig = JsonConfUtil.parseNodeConfig(nodeConfigJson);
        if (nodeConfig == null) {
            return null;
        }
        return nodeConfig.getLowCodeConf();
    }

    /**
     * 将 LFMainField 列表转换为前端展示用的字段值Map
     * 从 queryData 中抽取,供内联模式和外部模式共用
     */
    private Map<String, Object> buildFieldVoMap(List<LFMainField> lfMainFields,
                                                Map<String, BpmnConfLfFormdataField> lfFormdataFieldMap,
                                                String formCode, Long confId) {
        Map<String, Object> fieldVoMap = new HashMap<>(lfMainFields.size());
        Map<String, List<LFMainField>> fieldName2SelfMap = lfMainFields.stream()
                .collect(Collectors.groupingBy(LFMainField::getFieldId));
        for (Map.Entry<String, List<LFMainField>> Id2SelfEntry : fieldName2SelfMap.entrySet()) {
            String fieldName = Id2SelfEntry.getKey();
            BpmnConfLfFormdataField currentFieldProp = lfFormdataFieldMap.get(fieldName);
            if (currentFieldProp == null) {
                throw new AFBizException(Strings.lenientFormat("field with name:%s has no property", fieldName));
            }
            List<LFMainField> fields = Id2SelfEntry.getValue();
            int valueLen = fields.size();
            List<Object> actualMultiValue = valueLen == 1 ? null : new ArrayList<>(valueLen);
            for (LFMainField field : fields) {
                Integer fieldType = currentFieldProp.getFieldType();
                LFFieldTypeEnum fieldTypeEnum = LFFieldTypeEnum.getByType(fieldType);
                if (fieldTypeEnum == null) {
                    throw new AFBizException(Strings.lenientFormat("unrecognized field type,name:%s,formcode:%s,confId:%d", fieldName, formCode, confId));
                }
                Object actualValue = null;
                switch (fieldTypeEnum) {
                    case STRING:
                        actualValue = field.getFieldValue();
                        if (actualValue != null) {
                            String actualValueString = actualValue.toString();
                            if (actualValueString.startsWith("{")) {
                                actualValue = JSON.parseObject(actualValueString);
                            } else if (actualValueString.startsWith("[")) {
                                actualValue = JSON.parseArray(actualValueString);
                            }
                        }
                        break;
                    case NUMBER:
                        if (LFControlTypeEnum.SELECT.getName().equals(currentFieldProp.getFieldName())) {
                            try {
                                Object parse = JSON.parse(field.getFieldValue());
                                if (parse == null) {
                                    actualValue = "";
                                } else if (parse instanceof JSONArray) {
                                    actualValue = JSON.parseArray(field.getFieldValue());
                                } else {
                                    actualValue = parse;
                                }
                            } catch (Exception e) {
                                log.warn("field value can not be parsed to number,fieldName:{},formCode:{},confId:{}", fieldName, formCode, confId);
                                actualValue = field.getFieldValue();
                            }
                        } else {
                            actualValue = field.getFieldValueNumber();
                        }
                        break;
                    case DATE_TIME:
                        actualValue = DateUtil.SDF_DATETIME_PATTERN.format(field.getFieldValueDt());
                        break;
                    case DATE:
                        actualValue = DateUtil.SDF_DATE_PATTERN.format(field.getFieldValueDt());
                        break;
                    case TEXT:
                        actualValue = field.getFieldValueText();
                        break;
                    case BOOLEAN:
                        actualValue = Boolean.parseBoolean(field.getFieldValue());
                        break;
                }
                if (valueLen == 1) {
                    fieldVoMap.put(fieldName, actualValue);
                    break;
                }
                actualMultiValue.add(actualValue);
            }
            if (!CollectionUtils.isEmpty(actualMultiValue)) {
                fieldVoMap.put(fieldName, actualMultiValue);
            }
        }
        return fieldVoMap;
    }

    private Map<String,Object> filterConditionFields(UDLFApplyVo vo){
        Long confId = vo.getBpmnConfVo().getId();
        List<String> conditionFieldNames = conditionFieldNameMap.get(confId);
        Map<String,Object>conditionFieldMap=null;
        //put values into cache
        if(CollectionUtils.isEmpty(conditionFieldNames)){
            Map<String, Object> lfConditions = vo.getLfConditions();
            if(!CollectionUtils.isEmpty(lfConditions)){
                conditionFieldMap=lfConditions;
            }
            List<BpmnConfLfFormdataField> allFields = lfFormdataFieldService.list(Wrappers.<BpmnConfLfFormdataField>lambdaQuery()
                    .eq(BpmnConfLfFormdataField::getBpmnConfId, confId));
            if(CollectionUtils.isEmpty(allFields)){
               throw new AFBizException("lowcode form data has no fields");
            }

            List<String> condFieldNames=new ArrayList<>();
            Map<String,BpmnConfLfFormdataField> Id2SelfMap=new HashMap<>();
            for (BpmnConfLfFormdataField field : allFields) {
                String fieldId = field.getFieldId();
                Id2SelfMap.put(fieldId,field);
                if(field.getIsConditionField()!=null&&field.getIsConditionField()==1){
                    condFieldNames.add(fieldId);
                }
            }
            conditionFieldNameMap.put(confId,condFieldNames);
           if(!allFieldConfMap.containsKey(confId)){
               allFieldConfMap.put(confId,Id2SelfMap);
           }
        }
        conditionFieldNames=conditionFieldNameMap.get(confId);


        //if it is still empty here,it indicates that this approval has no condition fields
        if(!CollectionUtils.isEmpty(conditionFieldNames)){
                conditionFieldMap=new HashMap<>();
                Map<String, Object> lfFields = vo.getLfFields();
                for (Map.Entry<String, Object> stringObjectEntry : lfFields.entrySet()) {
                    String key = stringObjectEntry.getKey();
                    if (conditionFieldNames.contains(key)) {
                        conditionFieldMap.put(key,stringObjectEntry.getValue());
                    }
            }
        }
        //condition fields can not be greater than 1 at the moment
        if(!CollectionUtils.isEmpty(conditionFieldMap) &&conditionFieldMap.size()>1){
            throw new AFBizException("conditionFields size can not greater than 1");
        }
        return conditionFieldMap;
    }

    private List<LFFieldControlVO> getFieldControlsFromJson(Long confId, String elementId) {
        if (confId == null || StringUtils.isEmpty(elementId)) {
            return Collections.emptyList();
        }
        BpmnNode node = bpmnNodeService.getOne(Wrappers.<BpmnNode>lambdaQuery()
                .eq(BpmnNode::getConfId, confId)
                .eq(BpmnNode::getNodeId, elementId)
                .eq(BpmnNode::getIsDel, 0)
                .last("LIMIT 1"));
        if (node == null) {
            return Collections.emptyList();
        }
        String nodeConfigJson = node.getNodeConfigJson();
        if (StringUtils.isEmpty(nodeConfigJson)) {
            return Collections.emptyList();
        }
        BpmnNodeConfigJson nodeConfig = JsonConfUtil.parseNodeConfig(nodeConfigJson);
        if (nodeConfig == null || nodeConfig.getLowCodeConf() == null
                || CollectionUtils.isEmpty(nodeConfig.getLowCodeConf().getFieldControls())) {
            return Collections.emptyList();
        }
        List<LFFieldControlVO> result = new ArrayList<>();
        for (BpmnNodeLowCodeConfJson.FieldControl fc : nodeConfig.getLowCodeConf().getFieldControls()) {
            LFFieldControlVO vo = new LFFieldControlVO();
            vo.setNodeId(node.getId());
            vo.setFormdataId(fc.getFormdataId());
            vo.setFieldId(fc.getFieldId());
            vo.setFieldName(fc.getFieldName());
            vo.setPerm(fc.getPerm());
            result.add(vo);
        }
        return result;
    }

    private List<BpmnNodeApproverConfJson.FormRelatedUserConf> getFormRelatedConfsFromNode(BpmnNode node) {
        String nodeConfigJson = node.getNodeConfigJson();
        if (StringUtils.isEmpty(nodeConfigJson)) {
            return Collections.emptyList();
        }
        BpmnNodeConfigJson nodeConfig = JsonConfUtil.parseNodeConfig(nodeConfigJson);
        if (nodeConfig == null || nodeConfig.getApproverConf() == null
                || CollectionUtils.isEmpty(nodeConfig.getApproverConf().getFormRelatedUserConfList())) {
            return Collections.emptyList();
        }
        return nodeConfig.getApproverConf().getFormRelatedUserConfList();
    }

    private void  processFormRelatedUserConf(BpmnConfVo bpmnConfVo,UDLFApplyVo vo) {
        Long confId =bpmnConfVo.getId();
        Map<String, Object> lfFields = vo.getLfFields();
        Integer extraFlags = bpmnConfVo.getExtraFlags();
        if (extraFlags != null && BpmnConfFlagsEnum.HAS_FORM_RELATED_ASSIGNEES.flagsContainsCurrent(extraFlags)) {
            List<BpmnNode> formRelatedNodes = bpmnNodeService.list(Wrappers.<BpmnNode>lambdaQuery()
                    .eq(BpmnNode::getConfId, confId)
                    .eq(BpmnNode::getNodeProperty, NodePropertyEnum.NODE_PROPERTY_FORM_RELATED.getCode()));
            Map<String, List<String>> node2formRelatedAssignees = new HashMap<>();
            if (!CollectionUtils.isEmpty(formRelatedNodes)) {
                for (BpmnNode node : formRelatedNodes) {
                    List<BpmnNodeApproverConfJson.FormRelatedUserConf> formRelatedConfs = getFormRelatedConfsFromNode(node);
                    for (BpmnNodeApproverConfJson.FormRelatedUserConf formRelatedConf : formRelatedConfs) {
                        String valueJson = formRelatedConf.getValueJson();
                        if (StringUtils.isEmpty(valueJson)) {
                            throw new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL);
                        }
                        List<BaseIdTranStruVo> formInfos = JSON.parseArray(valueJson, BaseIdTranStruVo.class);
                        List<String> formValues = new ArrayList<>();
                        for (BaseIdTranStruVo formInfo : formInfos) {
                            String formName = formInfo.getId();
                            Object formVal = lfFields.get(formName);
                            if (formVal instanceof Iterable) {
                                Iterable iterablef = (Iterable) formVal;
                                Iterator iteratorf = iterablef.iterator();
                                while (iteratorf.hasNext()) {
                                    Object bValue = iteratorf.next();
                                    formValues.add(bValue.toString());
                                }
                            } else {
                                formValues.add(formVal.toString());
                            }
                        }
                        node2formRelatedAssignees.put(node.getId().toString(), formValues);
                    }
                }
            }
            if (node2formRelatedAssignees.isEmpty()) {
                throw new AFBizException("migration error,please contact the author");
            }
            vo.setNode2formRelatedAssignees(node2formRelatedAssignees);
        }

    }
}
