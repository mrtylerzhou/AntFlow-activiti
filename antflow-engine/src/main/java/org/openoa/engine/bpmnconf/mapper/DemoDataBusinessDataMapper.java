package org.openoa.engine.bpmnconf.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.lowflow.entity.LFMainField;

import java.util.Collection;
import java.util.List;

/**
 * 业务数据(演示数据)列表查询
 */
@Mapper
public interface DemoDataBusinessDataMapper {

    /**
     * 按 mainId 集合 + formCode 批量查询低代码字段值
     * <p>注意:参数名必须为 formCode,分表路由拦截器依赖该参数名做一致性哈希路由</p>
     *
     * @param mainIds  lf_main 主键集合(bpm_business_process.business_id)
     * @param formCode 低代码流程 form_code
     * @return 字段值列表
     */
    List<LFMainField> selectLfMainFieldsByMainIds(@Param("mainIds") Collection<Long> mainIds,
                                                  @Param("formCode") String formCode);
}
