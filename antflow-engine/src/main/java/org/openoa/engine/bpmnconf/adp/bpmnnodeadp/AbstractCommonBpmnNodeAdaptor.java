package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.vo.BpmnNodeVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;

public abstract class AbstractCommonBpmnNodeAdaptor<TEntity> implements BpmnNodeAdaptor {

    @Autowired
    private IService<TEntity> service;

    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        TEntity entity = queryEntity(bpmnNodeVo);
        setNodeProperty(bpmnNodeVo,entity);

    }


    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {
        checkParam(bpmnNodeVo);
        List<TEntity> tEntities = buildEntity(bpmnNodeVo);
        if(!CollectionUtils.isEmpty(tEntities)){
            service.saveBatch(tEntities);
        }
    }

    protected abstract void  setNodeProperty(BpmnNodeVo bpmnNodeVo,TEntity entity);
    protected abstract List<TEntity> buildEntity(BpmnNodeVo nodeVo);
    protected abstract void checkParam(BpmnNodeVo nodeVo);
    private TEntity queryEntity(BpmnNodeVo nodeVo){
        TEntity entity=service.getOne(new QueryWrapper<TEntity>()
                .eq("bpmn_node_id", nodeVo.getId()));
        return entity;
    }

}
