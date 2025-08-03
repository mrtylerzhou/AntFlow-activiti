package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmProcessNodeOvertime;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNodeOvertimeMapper;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.openoa.base.vo.BpmProcessNodeOvertimeVo;

import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNodeOvertimeService;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Repository
public class BpmProcessNodeOvertimeServiceImpl extends ServiceImpl<BpmProcessNodeOvertimeMapper, BpmProcessNodeOvertime> implements BpmProcessNodeOvertimeService {




    /**
     * save overtime remind info
     *
     * @return
     */
    @Override
    public boolean saveNodeOvertime(BpmProcessDeptVo vo) {
        QueryWrapper<BpmProcessNodeOvertime> wrapper = new QueryWrapper<BpmProcessNodeOvertime>();
        wrapper.eq("process_key", vo.getProcessKey());
        getBaseMapper().delete(wrapper);
        if (!ObjectUtils.isEmpty(vo.getRemindTypeIds())) {
            vo.getRemindTypeIds().forEach(o -> {
                if (!ObjectUtils.isEmpty(vo.getNodeIds())) {
                    vo.getNodeIds().forEach(node -> {
                        getBaseMapper().insert(BpmProcessNodeOvertime.builder()
                                .noticeType(o)
                                .processKey(vo.getProcessKey())
                                .nodeKey(node)
                                //.nodeName(node.getNodeName())
                                .noticeTime(vo.getNoticeTime())
                                .build());
                    });
                }
            });
        }
        return true;
    }

    /**
     * get a list of node overtime info
     *
     * @param processKey
     * @return
     */
    @Override
    public List<BpmProcessNodeOvertime> nodeOvertimeList(String processKey) {
        QueryWrapper<BpmProcessNodeOvertime> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", processKey);
        List<BpmProcessNodeOvertime> list = getBaseMapper().selectList(wrapper);
        return list;
    }

    /**
     * get notice type
     *
     * @param processKey
     * @return
     */
    @Override
    public List<Integer> selectNoticeType(String processKey) {
        return getBaseMapper().selectNoticeType(processKey);
    }

    /***

     * query a list of node overtime info
     * @param processKey
     * @return
     */
    public List<BpmProcessNodeOvertimeVo> selectNoticeNodeName(String processKey) {
        return getBaseMapper().selectNoticeNodeName(processKey);
    }

}
