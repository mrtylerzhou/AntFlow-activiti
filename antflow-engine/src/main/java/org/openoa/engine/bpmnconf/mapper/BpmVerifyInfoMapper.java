package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.vo.BpmVerifyInfoVo;
import org.openoa.engine.bpmnconf.confentity.BpmVerifyInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BpmVerifyInfoMapper extends BaseMapper<BpmVerifyInfo> {
    /**

     * get verify info by a variety of conditions
     *
     * @return
     */
    public List<BpmVerifyInfoVo> getVerifyInfo(BpmVerifyInfoVo vo);

    /**
     * get verify info by a variety of conditions
     *
     * @return
     */
    public List<BpmVerifyInfoVo> verifyInfoList(BpmVerifyInfoVo vo);

    /**
     * get task to do list
     *
     * @return
     */
    public List<BpmVerifyInfoVo> findTaskInfor(@Param("procInstId") String procInstId);

    public List<BpmVerifyInfoVo> findVerifyInfo(@Param("business_id") String business_id);
}
