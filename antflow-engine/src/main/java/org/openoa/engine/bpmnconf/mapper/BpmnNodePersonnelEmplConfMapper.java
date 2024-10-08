package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.engine.bpmnconf.confentity.BpmnNodePersonnelEmplConf;
import org.springframework.stereotype.Repository;

/**
 *@Author JimuOffice
 * @Description personnel users
 * @Param
 * @return
 * @Version 0.5
 */
@Mapper
public interface BpmnNodePersonnelEmplConfMapper extends BaseMapper<BpmnNodePersonnelEmplConf> {
}
