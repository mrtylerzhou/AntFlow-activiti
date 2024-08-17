package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.confentity.BpmBusiness;
import org.openoa.engine.bpmnconf.mapper.BpmBusinessMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

@Service
public class BpmBusinessServiceImpl extends ServiceImpl<BpmBusinessMapper, BpmBusiness> {

    @Autowired
    private BpmBusinessMapper bpmProcessBusinessMapper;


    /**
     * edit process business
     *
     * @param vo
     * @return
     */
    public boolean editProcessBusiness(BusinessDataVo vo) {
        QueryWrapper<BpmBusiness> wrapper = new QueryWrapper<>();
        wrapper.eq("process_code", vo.getProcessNumber());
        List<BpmBusiness> list = bpmProcessBusinessMapper.selectList(wrapper);
        if (ObjectUtils.isEmpty(list)) {
            String processCode = vo.getProcessKey().split("\\_")[0].toString();
            bpmProcessBusinessMapper.insert(BpmBusiness.builder()
                    .businessId(vo.getBusinessId())
                    .processKey(vo.getProcessKey())
                    .createTime(new Date())
                    .createUser( SecurityUtils.getLogInEmpIdSafe().longValue())
                    .createUserName(SecurityUtils.getLogInEmpNameSafe())
                    .processCode(processCode + "_" + vo.getBusinessId())
                    .build());
        }
        return true;
    }

    /**
     * 修改草稿数据
     *
     * @param vo
     * @return
     */
    public boolean updateBusinessState(BusinessDataVo vo) {
        if (!Strings.isNullOrEmpty(vo.getProcessNumber())) {
            String businessId = vo.getProcessNumber().split("\\_")[1];
            QueryWrapper<BpmBusiness> wrapper = new QueryWrapper<>();
            wrapper.eq("business_id", businessId);
            wrapper.eq("process_code", vo.getProcessNumber());
            List<BpmBusiness> list = bpmProcessBusinessMapper.selectList(wrapper);
            list.forEach(o -> {
                o.setIsDel(1);
                bpmProcessBusinessMapper.updateById(o);
            });
        }
        return true;
    }

    /**
     * delete bpm business
     *
     * @param vo
     */
    public void deleteBusiness(BusinessDataVo vo) {
        Long businessId = Long.parseLong(vo.getProcessNumber().split("\\_")[1].toString());
        //todo
        this.updateBusinessState(vo);
    }

    /**
     * get business count
     *
     * @return
     */
    public Integer getBpmBusinessCount(Integer userId) {
        QueryWrapper<BpmBusiness> wrapper = new QueryWrapper<>();
        wrapper.eq("create_user", userId);
        wrapper.eq("is_del", 0);
        List<BpmBusiness> list = bpmProcessBusinessMapper.selectList(wrapper);
        return list.size();
    }
}
