package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import org.openoa.base.entity.BpmBusiness;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.mapper.BpmBusinessMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmBusinessService;
import org.openoa.base.util.AFWrappers;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

@Repository
public class BpmBusinessServiceImpl extends ServiceImpl<BpmBusinessMapper, BpmBusiness> implements BpmBusinessService {


    /**
     * edit process business
     *
     * @param vo
     * @return
     */
    @Override
    public boolean editProcessBusiness(BusinessDataVo vo) {
        List<BpmBusiness> list = this.getBaseMapper().selectList(
                AFWrappers.<BpmBusiness>lambdaTenantQuery()
                .eq(BpmBusiness::getProcessCode,vo.getProcessNumber()));
        if (ObjectUtils.isEmpty(list)) {
            String processCode = vo.getProcessKey().split("_")[0];
            this.getBaseMapper().insert(BpmBusiness.builder()
                    .businessId(vo.getBusinessId())
                    .processKey(vo.getProcessKey())
                    .createTime(new Date())
                    .createUser( SecurityUtils.getLogInEmpIdSafe())
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
    @Override
    public boolean updateBusinessState(BusinessDataVo vo) {
        if (!Strings.isNullOrEmpty(vo.getProcessNumber())) {
            String businessId = vo.getProcessNumber().split("_")[1];
            List<BpmBusiness> list = this.getBaseMapper().selectList(
                    AFWrappers.<BpmBusiness>lambdaTenantQuery()
                            .eq(BpmBusiness::getBusinessId,businessId)
                            .eq(BpmBusiness::getProcessCode,vo.getProcessNumber())
            );
            list.forEach(o -> {
                o.setIsDel(1);
                this.getBaseMapper().updateById(o);
            });
        }
        return true;
    }

    /**
     * delete bpm business
     *
     * @param vo
     */
    @Override
    public void deleteBusiness(BusinessDataVo vo) {
        Long businessId = Long.parseLong(vo.getProcessNumber().split("_")[1]);
        //todo
        this.updateBusinessState(vo);
    }

    /**
     * get business count
     *
     * @return
     */
    @Override
    public Integer getBpmBusinessCount(Integer userId) {
        List<BpmBusiness> list = this.getBaseMapper().selectList(AFWrappers.<BpmBusiness>lambdaTenantQuery()
                .eq(BpmBusiness::getCreateUser,userId));
        return list.size();
    }
}
