package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.vo.BaseNumIdStruVo;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnTemplateVo;
import org.openoa.engine.bpmnconf.confentity.BpmProcessNotice;
import org.openoa.engine.bpmnconf.confentity.BpmnTemplate;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BpmProcessNoticeServiceImpl extends ServiceImpl<BpmProcessNoticeMapper, BpmProcessNotice> {

    @Autowired
    private BpmnTemplateServiceImpl bpmnTemplateService;


    /**
     * save process notice
     *
     */
    public void saveProcessNotice(BpmProcessDeptVo vo) {
        String processKey=vo.getProcessKey();
        List<Integer> notifyTypeIds=vo.getNotifyTypeIds();
        QueryWrapper<BpmProcessNotice> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", processKey);

        List<BpmnTemplateVo> templateVos = vo.getTemplateVos();
        if(!CollectionUtils.isEmpty(templateVos)){
            List<BpmProcessNotice> bpmProcessNotices = this.getBaseMapper().selectList(wrapper);
            //如果设置了高级通知,但是没有设置普通通知类型,就高级的赋值给普通的
            if(CollectionUtils.isEmpty(bpmProcessNotices)){
                List<Integer> advancedNotifyIds = templateVos.stream().flatMap(x -> x.getMessageSendTypeList().stream()).map(a -> a.getId().intValue()).distinct().collect(Collectors.toList());
                notifyTypeIds = new ArrayList<>(advancedNotifyIds);
            }
            BpmnConfVo confVo=new BpmnConfVo();
            confVo.setFormCode(processKey);
            confVo.setTemplateVos(templateVos);
            LambdaQueryWrapper<BpmnTemplate> delWrapper = Wrappers.<BpmnTemplate>lambdaQuery()
                    .eq(BpmnTemplate::getFormCode, processKey)
                            .isNull(BpmnTemplate::getNodeId);

            bpmnTemplateService.remove(delWrapper);
            bpmnTemplateService.editBpmnTemplate(confVo,null);
        }
        if (!ObjectUtils.isEmpty(notifyTypeIds)) {

            this.getBaseMapper().delete(wrapper);
            notifyTypeIds.forEach(o -> {
                this.getBaseMapper().insert(BpmProcessNotice.builder()
                        .processKey(processKey)
                        .type(o)
                        .build());
            });
        }
    }

    public List<BpmProcessNotice> processNoticeList(String processKey) {
        QueryWrapper<BpmProcessNotice> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", processKey);
        return this.getBaseMapper().selectList(wrapper);
    }

    public Map<String,List<BpmProcessNotice>> processNoticeMap(List<String> processKeys) {
        LambdaQueryWrapper<BpmProcessNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BpmProcessNotice::getProcessKey,processKeys);
        List<BpmProcessNotice> bpmProcessNotices = this.baseMapper.selectList(wrapper);
       return bpmProcessNotices.stream()
           .collect(Collectors.groupingBy(BpmProcessNotice::getProcessKey));
    }
}
