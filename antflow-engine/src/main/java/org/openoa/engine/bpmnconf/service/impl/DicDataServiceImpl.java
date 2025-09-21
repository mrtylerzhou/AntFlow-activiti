package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.DictData;
import org.openoa.engine.bpmnconf.mapper.DicDataMapper;
import org.openoa.engine.bpmnconf.service.interf.DicDataService;
import org.openoa.base.util.AFWrappers;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DicDataServiceImpl extends ServiceImpl<DicDataMapper, DictData> implements DicDataService {

    @Override
    public List<DictData> queryDicDataByCategory(String category){
        List<DictData> dictData = this.baseMapper.selectList(AFWrappers.<DictData>lambdaTenantQuery()
                .eq(DictData::getDictType, category));
        return dictData;
    }
}
