package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.activiti.engine.impl.util.CollectionUtil;
import org.openoa.engine.bpmnconf.confentity.DefaultTemplate;
import org.openoa.engine.bpmnconf.mapper.DefaultTemplateMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.DefaultTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DefaultTemplateServiceImpl extends ServiceImpl<DefaultTemplateMapper, DefaultTemplate> implements DefaultTemplateService {

    public void insertOrUpdateAllColumnBatch(List<DefaultTemplate> list) {
        if(CollectionUtils.isEmpty(list)){
            return;
        }
       List<DefaultTemplate> updates=Lists.newArrayList();
        List<DefaultTemplate> inserts= Lists.newArrayList();
        for (DefaultTemplate defaultTemplate : list) {

            if(defaultTemplate.getId()==null){
                inserts.add(defaultTemplate);
            }else{
                updates.add(defaultTemplate);
            }
        }
        if(!CollectionUtils.isEmpty(updates)){
            this.updateBatchById(updates);
        }
        if(!CollectionUtils.isEmpty(inserts)){
            this.saveBatch(inserts);
        }
    }
}
