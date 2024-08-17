package org.openoa.engine.bpmnconf.mapper;

import org.apache.ibatis.annotations.Param;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.io.Serializable;

public interface BsMapper {

    /**
     * it contains some common fields
     *
     * @param id
     * @param filName
     * @param tableName
     * @return
     */
    public BaseIdTranStruVo baseIdTran(@Param("id") Serializable id, @Param("filName") String filName, @Param("tableName") String tableName);


}
