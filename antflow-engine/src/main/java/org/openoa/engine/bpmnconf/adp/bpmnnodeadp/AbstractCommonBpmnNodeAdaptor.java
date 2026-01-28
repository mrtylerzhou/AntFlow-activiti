package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Lists;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.NodeLabelConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

public abstract class AbstractCommonBpmnNodeAdaptor<TEntity> extends AbstractAdditionSignNodeAdaptor {

    @Autowired
    private IService<TEntity> service;

    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        super.formatToBpmnNodeVo(bpmnNodeVo);
        TEntity entity = queryEntity(bpmnNodeVo);
        setNodeProperty(bpmnNodeVo,entity);
    }


    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {
        checkParam(bpmnNodeVo);
        super.editBpmnNode(bpmnNodeVo);
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
