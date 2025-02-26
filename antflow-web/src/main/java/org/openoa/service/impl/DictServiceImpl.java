package org.openoa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.dto.PageDto;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseKeyValueStruVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmBusinessParty;
import org.openoa.engine.bpmnconf.service.biz.LowCodeFlowBizService;
import org.openoa.entity.DictData;
import org.openoa.mapper.DicDataMapper;
import org.openoa.mapper.DictMainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 字典表为demo表,一般用户系统都有自己的字典表,可以替换为自己的字典表
 * 目前用途:
 * 1.配置低代码流程
 */
@Service
public class DictServiceImpl implements LowCodeFlowBizService {
    @Autowired
    private DictMainMapper dictMainMapper;
    @Autowired
    private DicDataMapper dicDataMapper;

    public List<DictData> getDictItemsByType(String dictType){
        LambdaQueryWrapper<DictData> qryByDictType = Wrappers.<DictData>lambdaQuery()
                .eq(DictData::getDictType, dictType);
        List<DictData> dictData = dicDataMapper.selectList(qryByDictType);
        dictData.sort(Comparator.comparing(DictData::getSort));
        return dictData;
    }

    @Override
    public List<BaseKeyValueStruVo> getLowCodeFlowFormCodes() {
        List<DictData> lowcodeflow = getDictItemsByType("lowcodeflow");
        List<BaseKeyValueStruVo> results=new ArrayList<>();
        if(lowcodeflow != null ) {
            for (DictData item : lowcodeflow) {
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
        }
        return results;
    }

    @Override
    public ResultAndPage<BaseKeyValueStruVo> selectLFActiveFormCodePageList(PageDto pageDto, TaskMgmtVO taskMgmtVO) {
        Page<BaseKeyValueStruVo> page = PageUtils.getPageByPageDto(pageDto);
        List<DictData> dictDataList = dicDataMapper.selectLFActiveFormCodePageList(page,taskMgmtVO);
        if (dictDataList ==null) {
            return PageUtils.getResultAndPage(page);
        }
        List<BaseKeyValueStruVo> results=new ArrayList<>();
        for (DictData item : dictDataList) {
            results.add(
                    BaseKeyValueStruVo
                            .builder()
                            .key(item.getValue())
                            .value(item.getLabel())
                            .createTime(item.getCreateTime())
                            .type("LF")
                            .remark(item.getRemark())
                            .build()
            );
        }
        page.setRecords(results);
        return PageUtils.getResultAndPage(page);
    }
    @Override
    public List<BaseKeyValueStruVo> getLFActiveFormCodes() {
        List<DictData> dictDataList = dicDataMapper.selectLFActiveFormCodes();
        List<BaseKeyValueStruVo> results=new ArrayList<>();
        for (DictData item : dictDataList) {
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
}
