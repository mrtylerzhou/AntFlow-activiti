package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.entity.OutSideBpmCallbackUrlConf;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmCallbackUrlConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.OutSideBpmCallbackUrlConfService;
import org.openoa.engine.utils.AFWrappers;
import org.openoa.engine.vo.OutSideBpmCallbackUrlConfVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * third party process service callback url conf
 * @since 0.5
 */
@Service
public class OutSideBpmCallbackUrlConfServiceImpl extends ServiceImpl<OutSideBpmCallbackUrlConfMapper, OutSideBpmCallbackUrlConf> implements OutSideBpmCallbackUrlConfService {



    @Override
    public List<OutSideBpmCallbackUrlConf> selectListByFormCode(String formCode) {
        List<OutSideBpmCallbackUrlConf> confList = this.getBaseMapper().selectList(new QueryWrapper<OutSideBpmCallbackUrlConf>()
                .eq("form_code", formCode)
                .eq("status", 1));
        return confList;
    }

    /**
     * edit
     *
     * @param vo
     */
    @Override
    public void edit(OutSideBpmCallbackUrlConfVo vo) {
        //to check whether the business party can add data
        if (vo.getId()==null) {
            Long count = this.getBaseMapper().selectCount(new QueryWrapper<OutSideBpmCallbackUrlConf>()
                    .eq("business_party_id", vo.getBusinessPartyId())
                    .eq("application_id", vo.getApplicationId())
            );
            if (count > 0) {
                throw new AFBizException("一个应用只能配置一条回调数据");
            }
        }

        OutSideBpmCallbackUrlConf outSideBpmCallbackUrlConf = this.getBaseMapper().selectById(vo.getId());
        if (outSideBpmCallbackUrlConf!=null) {
            BeanUtils.copyProperties(vo, outSideBpmCallbackUrlConf);
            outSideBpmCallbackUrlConf.setUpdateTime(new Date());
            outSideBpmCallbackUrlConf.setUpdateUser(SecurityUtils.getLogInEmpName());
            this.updateById(outSideBpmCallbackUrlConf);
        } else {
            outSideBpmCallbackUrlConf = new OutSideBpmCallbackUrlConf();
            BeanUtils.copyProperties(vo, outSideBpmCallbackUrlConf);
            //GenericEmployee loginedEmployee = new GenericEmployee();
            //todo
            outSideBpmCallbackUrlConf.setStatus(1);
            outSideBpmCallbackUrlConf.setCreateTime(new Date());
            outSideBpmCallbackUrlConf.setCreateUser(SecurityUtils.getLogInEmpName());
            outSideBpmCallbackUrlConf.setUpdateTime(new Date());
            outSideBpmCallbackUrlConf.setUpdateUser(SecurityUtils.getLogInEmpName());
            this.save(outSideBpmCallbackUrlConf);
        }
    }

    /**
     * query call back conf by business party id
     *
     * @param bpmnConfId
     * @param businessPartyId
     * @return
     */
    @Override
    public OutSideBpmCallbackUrlConf getOutSideBpmCallbackUrlConf(Long bpmnConfId, Long businessPartyId) {

        OutSideBpmCallbackUrlConf outSideBpmCallbackUrlConf = this.getBaseMapper()
                .selectList(AFWrappers.<OutSideBpmCallbackUrlConf>lambdaTenantQuery()
                        .eq(OutSideBpmCallbackUrlConf::getBusinessPartyId,businessPartyId)
                        .eq(OutSideBpmCallbackUrlConf::getStatus,1))
                .stream()
                .findFirst()
                .orElse(null);


//        if (outSideBpmCallbackUrlConf==null) {
//            throw new JiMuBizException("流程回调URL未配置，方法执行失败");
//        }

        return outSideBpmCallbackUrlConf;
    }

}
