package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmProcessDept;
import org.openoa.base.entity.BpmnConf;
import org.openoa.engine.bpmnconf.mapper.BpmProcessDeptMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessDeptService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * //todo process and department connection service,the module is deprecated and will be redesigned
 */
@Service
public class BpmProcessDeptServiceImpl extends ServiceImpl<BpmProcessDeptMapper, BpmProcessDept> implements BpmProcessDeptService {

    private static Map<String, BpmnConf> bpmnConfMap = new ConcurrentHashMap<>();



    /**
     * 查找最大流程编号
     *
     * @return
     */
    @Override
    public String maxProcessCode() {
        return getBaseMapper().maxProcessCode();
    }

    /**
     * 生成流程编号
     *
     * @param testStr
     * @return
     */
    @Override
    public String getProcessCode(String testStr) {
        String[] strs = testStr.split("[^0-9]");//根据不是数字的字符拆分字符串
        String numStr = strs[strs.length - 1];//取出最后一组数字
        if (numStr != null && numStr.length() > 0) {//如果最后一组没有数字(也就是不以数字结尾)，抛NumberFormatException异常
            int n = numStr.length();//取出字符串的长度
            int num = Integer.parseInt(numStr) + 1;//将该数字加一
            String added = String.valueOf(num);
            n = Math.min(n, added.length());
            //拼接字符串
            return testStr.subSequence(0, testStr.length() - n) + added;
        } else {
            throw new NumberFormatException();
        }
    }
    /**
     * 查询全员创建权限
     */
    @Override
    public List<String> getAllProcess() {
        QueryWrapper<BpmProcessDept> wrapper = new QueryWrapper<>();
        wrapper.eq("is_all", 1);
        List<BpmProcessDept> deptList = getBaseMapper().selectList(wrapper);
        return Optional.ofNullable(deptList)
                .map(o ->
                        deptList.stream().map(depProcess -> depProcess.getProcessKey()).collect(Collectors.toList())
                )
                .orElse(Arrays.asList());
    }


}
