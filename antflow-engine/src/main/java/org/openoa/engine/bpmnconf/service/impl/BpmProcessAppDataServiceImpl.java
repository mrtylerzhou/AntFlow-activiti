package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmProcessAppData;
import org.openoa.engine.bpmnconf.mapper.BpmProcessAppDataMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessAppDataService;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 *  app process go online service
 */
@Repository
public class BpmProcessAppDataServiceImpl extends ServiceImpl<BpmProcessAppDataMapper, BpmProcessAppData> implements BpmProcessAppDataService {



    /**
     *  get all entrance application by version and process key
     *
     * @param versionId
     * @return
     */
    @Override
    public List<String> getBpmProcessAppDataVo(Long versionId, List<String> processKey) {
        QueryWrapper<BpmProcessAppData> bpmProcessAppDataWrapper = new QueryWrapper<>();
        bpmProcessAppDataWrapper.eq("version_id", versionId);
        bpmProcessAppDataWrapper.eq("type", 1);
        bpmProcessAppDataWrapper.in("process_key", processKey);
        List<BpmProcessAppData> list = getBaseMapper().selectList(bpmProcessAppDataWrapper);
        if (!CollectionUtils.isEmpty(list)) {
            return list.stream().map(BpmProcessAppData::getProcessKey).collect(Collectors.toList());
        } else {
            return Arrays.asList();
        }
    }

    /**
     * query all app process for all users
     */
    @Override
    public List<BpmProcessAppData> getProcessAppData(Long versionId, Integer isAll, Integer type) {
        QueryWrapper<BpmProcessAppData> bpmProcessAppDataWrapper = new QueryWrapper<>();
        bpmProcessAppDataWrapper.eq("state", isAll);
        bpmProcessAppDataWrapper.eq("version_id", versionId);
        bpmProcessAppDataWrapper.eq("type", type);
        return getBaseMapper().selectList(bpmProcessAppDataWrapper);
    }




    /**
     * remove data from a specified version
     */
    @Override
    public void deleteAppVersionData(Long versionId, Integer type) {
        this.remove(new QueryWrapper<BpmProcessAppData>().eq("version_id", versionId).eq("type", type));
    }

}
