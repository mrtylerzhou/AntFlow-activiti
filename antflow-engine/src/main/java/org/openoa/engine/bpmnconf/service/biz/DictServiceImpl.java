package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.constant.enums.ProcessNoticeEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.DictData;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.entity.jsonconf.BpmnConfConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.mapper.DicDataMapper;

import org.openoa.engine.bpmnconf.service.interf.biz.BpmnConfBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.LowCodeFlowBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 字典表为demo表,一般用户系统都有自己的字典表,可以替换为自己的字典表
 * 目前用途:
 * 1.配置低代码流程
 * 2.配置 page-added DIY 流程(dict_type='diylowcodeflow': LF 后端 + 自定义 Vue 前端)
 */
@Service
public class DictServiceImpl implements LowCodeFlowBizService {
    /** page-added DIY FormCode 的 dict_type */
    public static final String DIY_LOW_CODE_DICT_TYPE = "diylowcodeflow";
    @Autowired
    private DicDataMapper dicDataMapper;
    @Autowired
    private BpmnConfBizService bpmnConfBizService;
    /**
     * 获取全部 LF FormCodes 在流程设计时选择使用
     * @return
     */
    @Override
    public List<BaseKeyValueStruVo> getLowCodeFlowFormCodes() {
        List<DictData> lowcodeList = getDictItemsByType("lowcodeflow");
        List<BaseKeyValueStruVo> results=new ArrayList<>();
        for (DictData item : lowcodeList) {
            results.add(
                    BaseKeyValueStruVo
                            .builder()
                            .key(item.getValue())
                            .value(item.getLabel())
                            .type("LF")
                            .remark(item.getRemark())
                            .build()
            );
        }
        return results;
    }

    /**
     * 获取LF FormCode Page List 模板列表使用
     * @param pageDto
     * @param taskMgmtVO
     * @return
     */
    @Override
    public ResultAndPage<BaseKeyValueStruVo> selectLFFormCodePageList(PageDto pageDto, TaskMgmtVO taskMgmtVO) {
        Page<BaseKeyValueStruVo> page = PageUtils.getPageByPageDto(pageDto);
        List<DictData> dictDataList = dicDataMapper.selectLFFormCodePageList(page,taskMgmtVO);
        return handleFormCodePageList(page,dictDataList,"LF");
    }
    /**
     * 获取 已设计流程并且启用的 LF FormCode Page List 发起页面使用
     * @param pageDto
     * @param taskMgmtVO
     * @return
     */
    @Override
    public ResultAndPage<BaseKeyValueStruVo> selectLFActiveFormCodePageList(PageDto pageDto, TaskMgmtVO taskMgmtVO) {
        Page<BaseKeyValueStruVo> page = PageUtils.getPageByPageDto(pageDto);
        List<DictData> dictDataList = dicDataMapper.selectLFActiveFormCodePageList(page,taskMgmtVO);
        return handleFormCodePageList(page,dictDataList,"LF");
    }

    /**
     * 获取 page-added DIY FormCode Page List 模板列表使用
     * @param pageDto
     * @param taskMgmtVO
     * @return
     */
    @Override
    public ResultAndPage<BaseKeyValueStruVo> selectDIYFormCodePageList(PageDto pageDto, TaskMgmtVO taskMgmtVO) {
        Page<BaseKeyValueStruVo> page = PageUtils.getPageByPageDto(pageDto);
        List<DictData> dictDataList = dicDataMapper.selectDIYFormCodePageList(page,taskMgmtVO);
        return handleFormCodePageList(page,dictDataList,"DIY");
    }

    /**
     * 新增LF FormCode
     * @param vo
     * @return
     */
    @Override
    public Integer addFormCode(BaseKeyValueStruVo vo) {
        Integer result = 0;
        LambdaQueryWrapper<DictData> qryByValue =  Wrappers.<DictData>lambdaQuery()
                .eq(DictData::getValue, vo.getKey());
        List<DictData> dictData = dicDataMapper.selectList(qryByValue);
        if (dictData.isEmpty()){
            DictData  entity = new DictData();
            entity.setDictType("lowcodeflow");
            entity.setValue(vo.getKey());
            entity.setLabel(vo.getValue());
            entity.setRemark(vo.getRemark());
            entity.setIsDefault("N");
            entity.setIsDel(0);
            entity.setCreateUser(SecurityUtils.getLogInEmpName());
            entity.setCreateTime(new Date());
            result = dicDataMapper.insert(entity);
        }
        return  result;
    }

    /**
     * 新增 page-added DIY FormCode(dict_type='diylowcodeflow')
     * 该类流程后端走 LowFlowApprovalService(is_lowcode_flow=1),前端渲染自定义 Vue 组件(bizFormMaps)。
     * @param vo
     * @return
     */
    @Override
    public Integer addDIYFormCode(BaseKeyValueStruVo vo) {
        Integer result = 0;
        // formCode 全局唯一(路由键),按 value 校验,与 LF/coded DIY 不冲突
        LambdaQueryWrapper<DictData> qryByValue = Wrappers.<DictData>lambdaQuery()
                .eq(DictData::getValue, vo.getKey());
        List<DictData> dictData = dicDataMapper.selectList(qryByValue);
        if (dictData.isEmpty()) {
            DictData entity = new DictData();
            entity.setDictType(DIY_LOW_CODE_DICT_TYPE);
            entity.setValue(vo.getKey());
            entity.setLabel(vo.getValue());
            entity.setRemark(vo.getRemark());
            entity.setIsDefault("N");
            entity.setIsDel(0);
            entity.setCreateUser(SecurityUtils.getLogInEmpName());
            entity.setCreateTime(new Date());
            result = dicDataMapper.insert(entity);
        }
        return result;
    }

    /**
     * page-added DIY(有效版本): 供"流程中心-可用流程(DIY)"合并展示。
     * 返回 dict_type='diylowcodeflow' 且有有效 BpmnConf(effective_status=1) 的 FormCode。
     */
    @Override
    public List<DIYProcessInfoDTO> getDIYActiveFormCodes() {
        List<DictData> list = dicDataMapper.selectDIYActiveFormCodeList(new TaskMgmtVO());
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<DIYProcessInfoDTO> results = new ArrayList<>();
        for (DictData item : list) {
            results.add(DIYProcessInfoDTO.builder()
                    .key(item.getValue())
                    .value(item.getLabel())
                    .type("DIY")
                    .remark(item.getRemark())
                    .createTime(item.getCreateTime())
                    .build());
        }
        return results;
    }
    /** 私有方法 */
    private List<DictData> getDictItemsByType(String dictType){
        LambdaQueryWrapper<DictData> qryByDictType = Wrappers.<DictData>lambdaQuery()
                .eq(DictData::getDictType, dictType);
        List<DictData> dictData = dicDataMapper.selectList(qryByDictType);
        dictData.sort(Comparator.comparing(DictData::getCreateTime).reversed());
        return dictData;
    }
    /** 私有方法: type 传入 "LF" 或 "DIY" */
    private ResultAndPage<BaseKeyValueStruVo> handleFormCodePageList(Page page, List<DictData> dictlist, String type) {
        if (dictlist ==null) {
            return PageUtils.getResultAndPage(page);
        }
        List<BaseKeyValueStruVo> results=new ArrayList<>();
        for (DictData item : dictlist) {
            results.add(
                    BaseKeyValueStruVo
                            .builder()
                            .key(item.getValue())
                            .value(item.getLabel())
                            .createTime(item.getCreateTime())
                            .type(type)
                            .remark(item.getRemark())
                            .build()
            );
        }
        List<String> formCodes = results.stream().map(BaseKeyValueStruVo::getKey).collect(Collectors.toList());
        //20250326:修改增加formCodes为空判断
        if(!formCodes.isEmpty()){
            LambdaQueryWrapper<BpmnConf> queryWrapper = Wrappers.<BpmnConf>lambdaQuery()
                    .select(BpmnConf::getFormCode, BpmnConf::getExtraFlags, BpmnConf::getConfConfigJson)
                    .in(BpmnConf::getFormCode, formCodes)
                    .eq(BpmnConf::getEffectiveStatus, 1);
            List<BpmnConf> bpmnConfs = bpmnConfBizService.getService().list(queryWrapper);
            if(!CollectionUtils.isEmpty(bpmnConfs)){
                Map<String, Integer> formCode2Flags = bpmnConfs
                        .stream()
                        .filter(a->a.getExtraFlags()!=null)
                        .collect(Collectors.toMap(BpmnConf::getFormCode, BpmnConf::getExtraFlags, (v1, v2) -> v1));
                Map<String, List<Integer>> formCode2NoticeTypes = new HashMap<>();
                for (BpmnConf conf : bpmnConfs) {
                    BpmnConfConfigJson confConfig = JsonConfUtil.parseConfConfig(conf.getConfConfigJson());
                    List<Integer> types = confConfig != null ? confConfig.getNoticeChannelTypes() : null;
                    if (!CollectionUtils.isEmpty(types)) {
                        formCode2NoticeTypes.put(conf.getFormCode(), types);
                    }
                }
                for (BaseKeyValueStruVo lfDto : results) {

                    Integer flags = formCode2Flags.get(lfDto.getKey());
                    if(flags!=null){
                        boolean hasStartUserChooseModules = BpmnConfFlagsEnum.HAS_STARTUSER_CHOOSE_MODULES.flagsContainsCurrent(flags);
                        lfDto.setHasStarUserChooseModule(hasStartUserChooseModules);
                    }
                    String formCode = lfDto.getKey();
                    List<Integer> noticeChannelTypes = formCode2NoticeTypes.get(formCode);
                    if(!CollectionUtils.isEmpty(noticeChannelTypes)){
                        List<BaseNumIdStruVo> processNotices=new ArrayList<>();
                        for (ProcessNoticeEnum value : ProcessNoticeEnum.values()) {
                            BaseNumIdStruVo struVo=new BaseNumIdStruVo();
                            struVo.setId(value.getCode().longValue());
                            struVo.setName(value.getDesc());
                            if(noticeChannelTypes.contains(value.getCode())){
                                struVo.setActive(true);
                            }
                            processNotices.add(struVo);
                        }
                        lfDto.setProcessNotices(processNotices);
                    }
                    BpmnConfVo confVo=new BpmnConfVo();
                    confVo.setFormCode(formCode);
                    bpmnConfBizService.setBpmnTemplateVos(confVo);
                    lfDto.setTemplateVos(confVo.getTemplateVos());
                }
            }
        }
        page.setRecords(results);
        return PageUtils.getResultAndPage(page);
    }
}
