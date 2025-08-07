package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.OutSideBpmAdminPersonnel;

import java.util.List;

@Mapper
public interface OutSideBpmAdminPersonnelMapper extends BaseMapper<OutSideBpmAdminPersonnel> {

    /**
     * get business party id list by employee id and filtering out the type
     *
     * @param employeeId
     * @return
     */
    List<Integer> getBusinessPartyIdByEmployeeId(@Param("employeeId") String employeeId, @Param("types") List<Integer> types);

}
