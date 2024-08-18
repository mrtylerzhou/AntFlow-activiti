package org.openoa.base.service;

import com.google.common.collect.Lists;
import org.openoa.base.mapper.UserMapper;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * this service should be redesigned
 */
@Service
public class UserServiceImpl{
    @Autowired
    UserMapper userMapper;
    public List<BaseIdTranStruVo> queryByNameFuzzy(String userName) {
        List<BaseIdTranStruVo> users = userMapper.queryByNameFuzzy(userName);
        return users;
    }

    public List<BaseIdTranStruVo> queryCompanyByNameFuzzy(String companyName) {
        List<BaseIdTranStruVo> baseIdTranStruVos = userMapper.queryCompanyByNameFuzzy(companyName);
        return baseIdTranStruVos;
    }

    public List<BaseIdTranStruVo> queryUserByIds(Collection<Long> userIds){

        List<BaseIdTranStruVo> users = userMapper.queryByIds(userIds);
        return users;
    }
    public BaseIdTranStruVo getById(Long id){
        List<BaseIdTranStruVo> users = userMapper.queryByIds(Lists.newArrayList(id));
        return users.get(0);
    }
    public  List<BaseIdTranStruVo> queryLeadersByEmployeeIdAndTier(Long employeeId,Integer tier){
        List<BaseIdTranStruVo> users = userMapper.getLevelLeadersByEmployeeIdAndTier(employeeId,tier);
        return users;
    }

    /**
     * dummy sql to be implement
     * @param employeeId
     * @param grade
     * @return
     */
    public  List<BaseIdTranStruVo> queryLeadersByEmployeeIdAndGrade(Long employeeId,Integer grade){
        List<BaseIdTranStruVo> users = userMapper.getLevelLeadersByEmployeeIdAndTier(employeeId,grade);
        return users;
    }
    /**
     * dummy sql to be implement
     * @param employeeId
     * @param level setting
     * @return
     */
    public BaseIdTranStruVo queryLeaderByEmployeeIdAndLevel(Long employeeId,Integer level){

        return null;
    }
    public BaseIdTranStruVo queryEmployeeHrpbByEmployeeId(Long employeeId){
        BaseIdTranStruVo baseIdTranStruVo = userMapper.getHrpbByEmployeeId(employeeId);
        return baseIdTranStruVo;
    }
    public BaseIdTranStruVo queryEmployeeDirectLeaderById(Long employeeId){
        BaseIdTranStruVo baseIdTranStruVo = userMapper.getDirectLeaderByEmployeeId(employeeId);
        return baseIdTranStruVo;
    }
}
