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
 * 此类为流程引擎中用户服务,AntFlow完全接管了activiti中的用户系统
 * 此类中绝大部分依赖t_user demo表,用户早期为快速测试可以使用这个表,但是和生产上强烈建议替换为自己的用户表
 * 替换很简单,每个系统都有自己的用户表,绝大部分场景只需要查出用户的id和name即可(如果您的系统不是叫id和name,sql中需要使用as转成id和name)
 * 此类为默认实现,用户可以将sql替换为自己系统的sql,也可以自己实现AfUserService(方法较多,但是非必须都实现,前四个必须要实现的,后面的等运行时报错了,再实现也不迟到,不然一下子会很懵逼,不知道要干什么)
 * 前四个实现以后,基本的流程流转就ok了,后面的可以用到指定的demo时报错了再看修改,这些都是相对高级一些的功能,等你用到这部分的,也大概有一些基本的概念了,这时候再改些也相对较快了
 * 用户也可以不重写sql,而是自己实现AfUserService,然后将自己的实现标为@Primary即可
 * 这里可能有一个误区,用户信息不是必须查sql(但是一般情况下是),只需要实现指定方法将用户的id和名称拿到即可,至于你是怎么拿的antflow不关心.比如你的用户信息是调用三方Restful接口返回的,返回结果只要封装成BaseIdTranStruVo即可
 */
@Service("afUserService")
public class UserServiceImpl implements AfUserService{
    @Autowired
    UserMapper userMapper;
    @Override
    public List<BaseIdTranStruVo> queryByNameFuzzy(String userName) {
        List<BaseIdTranStruVo> users = userMapper.queryByNameFuzzy(userName);
        return users;
    }

    @Override
    public List<BaseIdTranStruVo> queryCompanyByNameFuzzy(String companyName) {
        List<BaseIdTranStruVo> baseIdTranStruVos = userMapper.queryCompanyByNameFuzzy(companyName);
        return baseIdTranStruVos;
    }

    @Override
    public List<BaseIdTranStruVo> queryUserByIds(Collection<String> userIds){

        List<BaseIdTranStruVo> users = userMapper.queryByIds(userIds);
        return users;
    }
    @Override
    public BaseIdTranStruVo getById(String id){
        List<BaseIdTranStruVo> users = userMapper.queryByIds(Lists.newArrayList(id));
        if(CollectionUtils.isEmpty(users)){
            return new BaseIdTranStruVo();
        }
        return users.get(0);
    }
    @Override
    public  List<BaseIdTranStruVo> queryLeadersByEmployeeIdAndTier(String employeeId, Integer tier){
        List<BaseIdTranStruVo> users = userMapper.getLevelLeadersByEmployeeIdAndTier(employeeId,tier);
        return users;
    }

    /**
     * dummy sql to be implement
     * @param employeeId
     * @param grade
     * @return
     */
    @Override
    public  List<BaseIdTranStruVo> queryLeadersByEmployeeIdAndGrade(String employeeId, Integer grade){
        List<BaseIdTranStruVo> users = userMapper.getLevelLeadersByEmployeeIdAndEndGrade(employeeId,grade);
        return users;
    }
    /**
     * dummy sql to be implement
     * @param employeeId
     * @param level setting
     * @return
     */
    @Override
    public BaseIdTranStruVo queryLeaderByEmployeeIdAndLevel(String employeeId, Integer level){

        return userMapper.getLeaderByLeventDepartment(employeeId,level);
    }
    @Override
    public BaseIdTranStruVo queryEmployeeHrpbByEmployeeId(String employeeId){
        BaseIdTranStruVo baseIdTranStruVo = userMapper.getHrpbByEmployeeId(employeeId);
        return baseIdTranStruVo;
    }
    @Override
    public BaseIdTranStruVo queryEmployeeDirectLeaderById(String employeeId){
        BaseIdTranStruVo baseIdTranStruVo = userMapper.getDirectLeaderByEmployeeId(employeeId);
        return baseIdTranStruVo;
    }
}
