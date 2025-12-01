package org.openoa.engine.bpmnconf.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openoa.base.entity.BpmProcessAppApplication;
import org.openoa.base.exception.AFBizException;
import org.openoa.engine.bpmnconf.mapper.BpmProcessAppApplicationMapper;
import org.openoa.engine.bpmnconf.service.interf.ApplicationService;
import org.openoa.base.util.AFWrappers;
import org.openoa.engine.vo.BaseApplicationVo;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;


/**
 * process application service
 *
 * @author tylerZhou
 * @since 0.5
 */
@Repository
public class ApplicationServiceImpl extends ServiceImpl<BpmProcessAppApplicationMapper, BpmProcessAppApplication> implements ApplicationService {





    /**
     * remove application
     */
    @Override
    public void del(Integer id) {
        BpmProcessAppApplication application = this.getById(id);

        List<BpmProcessAppApplication> list = this.list(
                AFWrappers.<BpmProcessAppApplication>lambdaTenantQuery()
                        .eq(BpmProcessAppApplication::getParentId,id));
        //如果父级应用且关联子级应用 则不可删除
        if (application.getApplyType().equals(3)
                && !CollectionUtils.isEmpty(list)) {
            throw new AFBizException("父级应用内联了子应用，请先删除子应用！");
        }
        application.setIsDel(1);
        this.getBaseMapper().updateById(application);
    }



    /**
     * get application url
     *
     * @param businessCode business code
     * @param processKey   process key
     * @return 应用url
     */
    @Override
    public BpmProcessAppApplicationVo getApplicationUrl(String businessCode, String processKey) {
        if (!ObjectUtils.isEmpty(businessCode) &&!ObjectUtils.isEmpty(processKey)) {

            List<BpmProcessAppApplication> list = this.list(
                    AFWrappers.<BpmProcessAppApplication>lambdaTenantQuery()
                            .eq(BpmProcessAppApplication::getBusinessCode,businessCode)
                            .eq(BpmProcessAppApplication::getProcessKey,processKey));

            if (!CollectionUtils.isEmpty(list)) {
                BpmProcessAppApplication application = list.get(0);
                BpmProcessAppApplicationVo vo = new BpmProcessAppApplicationVo();
               BeanUtils.copyProperties(application, vo);
                if (!ObjectUtils.isEmpty(vo.getLookUrl())) {
                    vo.setLookUrl(StringEscapeUtils.unescapeHtml4(vo.getLookUrl()));
                }
                if (!ObjectUtils.isEmpty(vo.getSubmitUrl())) {
                    vo.setSubmitUrl(StringEscapeUtils.unescapeHtml4(vo.getSubmitUrl()));
                }
                if (!ObjectUtils.isEmpty(vo.getConditionUrl())) {
                    vo.setConditionUrl(StringEscapeUtils.unescapeHtml4(vo.getConditionUrl()));
                }
                return vo;
            }
        }
        return null;
    }


    /**
     * get application key list
     */
    @Override
    public List<BaseApplicationVo> getApplicationKeyList(BpmProcessAppApplicationVo applicationVo) {
        if (applicationVo==null || ObjectUtils.isEmpty(applicationVo.getBusinessCode())) {
            return Lists.newArrayList();
        }
        List<BpmProcessAppApplication> list = this.list(
                AFWrappers.<BpmProcessAppApplication>lambdaTenantQuery()
                        .eq(BpmProcessAppApplication::getBusinessCode,applicationVo.getBusinessCode()));
        return list.stream()
                .filter(o -> !ObjectUtils.isEmpty(o.getProcessKey()))
                .map(o -> BaseApplicationVo
                        .builder()
                        .id(o.getProcessKey())
                        .name(o.getTitle())
                        .lookUrl(o.getLookUrl())
                        .submitUrl(o.getSubmitUrl())
                        .conditionUrl(o.getConditionUrl())
                        .pkId(o.getId())
                        .build())
                .distinct()
                .collect(Collectors.toList());
    }

}
