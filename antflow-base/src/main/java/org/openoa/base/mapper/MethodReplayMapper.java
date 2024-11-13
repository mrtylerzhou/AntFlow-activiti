package org.openoa.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.openoa.base.entity.MethodReplayEntity;
import org.openoa.base.vo.MethodReplayDTO;

import java.util.List;

/**
 * @author duggle.du
 */
@Mapper
public interface MethodReplayMapper {

    int insert(MethodReplayEntity methodReplayEntity);

    List<MethodReplayEntity> select(MethodReplayDTO methodReplayDTO);

    int addAlreadyReplayTimes(MethodReplayEntity methodReplayEntity);

    int delete(MethodReplayEntity methodReplayEntity);

    MethodReplayEntity selectById(MethodReplayEntity methodReplayEntity);

    void deleteByIds(MethodReplayDTO methodReplayDTO);
}
