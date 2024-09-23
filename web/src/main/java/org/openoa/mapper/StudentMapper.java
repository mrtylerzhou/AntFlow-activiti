package org.openoa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.entity.Student;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    //for testing only
    Student selectOneStudent();
    List<Student>getStudentList(Page<Student> page);
    List<BaseIdTranStruVo> selectAllStudent();
}
