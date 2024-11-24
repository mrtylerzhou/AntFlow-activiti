package org.openoa.engine.bpmnconf.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.Department;
import org.openoa.base.vo.DepartmentVo;
import org.openoa.engine.bpmnconf.mapper.DepartmentMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * department service
 * generic department service
 * @since 0.5
 */
@Slf4j
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> {

    public List<DepartmentVo> listSubDepartmentByEmployeeId(String userId) {
        List<Department> departments = this.getBaseMapper().ListSubDepartmentByEmployeeId(userId);
        if (!CollectionUtils.isEmpty(departments)) {
            String path = departments.get(0).getPath();
            if (StringUtils.hasText(path)) {
                String[] idsStr = path.split("/");
                List<Integer> ids = new ArrayList<>();
                for (String s : idsStr) {
                    ids.add(Integer.valueOf(s));
                }
                QueryWrapper<Department> wrapper = new QueryWrapper<>();
                wrapper.in("id", ids).eq("is_del", 0);
                List<Department> list = this.list(wrapper);
                //list.add(departments.get(0));
                List<DepartmentVo> result = new ArrayList<>(list.size());
                for (Department d : list) {
                    DepartmentVo vo = new DepartmentVo();
                    BeanUtils.copyProperties(d, vo);
                    result.add(vo);
                }
                return result;
            }
        }
        return Collections.EMPTY_LIST;
    }
    public Department getDepartmentByEmployeeId(String userId){
        return this.baseMapper.getDepartmentByEmployeeId(userId);
    }
}
