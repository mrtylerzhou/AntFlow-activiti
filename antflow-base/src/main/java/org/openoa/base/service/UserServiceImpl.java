package org.openoa.base.service;

import com.google.common.collect.Lists;
import org.openoa.base.mapper.UserMapper;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    public List<BaseIdTranStruVo> queryUserByIds(Collection<String> userIds){

        List<BaseIdTranStruVo> users = userMapper.queryByIds(userIds);
        return users;
    }
    public BaseIdTranStruVo getById(String id){
        List<BaseIdTranStruVo> users = userMapper.queryByIds(Lists.newArrayList(id));
        if(CollectionUtils.isEmpty(users)){
            return new BaseIdTranStruVo();
        }
        return users.get(0);
    }
    public  List<BaseIdTranStruVo> queryLeadersByEmployeeIdAndTier(String employeeId,Integer tier){
        List<BaseIdTranStruVo> users = userMapper.getLevelLeadersByEmployeeIdAndTier(employeeId,tier);
        return users;
    }

    /**
     * dummy sql to be implement
     * @param employeeId
     * @param grade
     * @return
     */
    public  List<BaseIdTranStruVo> queryLeadersByEmployeeIdAndGrade(String employeeId,Integer grade){
        List<BaseIdTranStruVo> users = userMapper.getLevelLeadersByEmployeeIdAndTier(employeeId,grade);
        return users;
    }
    /**
     * dummy sql to be implement
     * @param employeeId
     * @param level setting
     * @return
     */
    public BaseIdTranStruVo queryLeaderByEmployeeIdAndLevel(String employeeId,Integer level){

        return null;
    }
    public BaseIdTranStruVo queryEmployeeHrpbByEmployeeId(String employeeId){
        BaseIdTranStruVo baseIdTranStruVo = userMapper.getHrpbByEmployeeId(employeeId);
        return baseIdTranStruVo;
    }
    public BaseIdTranStruVo queryEmployeeDirectLeaderById(String employeeId){
        BaseIdTranStruVo baseIdTranStruVo = userMapper.getDirectLeaderByEmployeeId(employeeId);
        return baseIdTranStruVo;
    }
}
