package org.openoa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.LowCodeFlowBizService;
import org.openoa.entity.DictData;
import org.openoa.mapper.DicDataMapper;
import org.openoa.mapper.DictMainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
    public List<String> getLowCodeFlowFormCodes() {
        List<DictData> lowcodeflow = getDictItemsByType("lowcodeflow");
        return lowcodeflow.stream().map(DictData::getValue).collect(Collectors.toList());
    }
}
