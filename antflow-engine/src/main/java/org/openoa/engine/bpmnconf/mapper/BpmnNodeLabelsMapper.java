package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeLabel;

@Mapper
public interface BpmnNodeLabelsMapper extends BaseMapper<BpmnNodeLabel> {
    /**
     * <resultMap id="BpmnNodeLabelResultMap" type="com.example.BpmnNodeLabel">
     *     <id property="id" column="id" />
     *     <result property="nodeId" column="nodeid" />
     *     <result property="labelName" column="label_name" />
     *     <result property="labelValue" column="label_value" />
     *     <result property="remark" column="remark" />
     *     <result property="isDel" column="is_del" />
     *     <result property="createUser" column="create_user" />
     *     <result property="createTime" column="create_time" />
     *     <result property="updateUser" column="update_user" />
     *     <result property="updateTime" column="update_time" />
     * </resultMap>
     */
}
