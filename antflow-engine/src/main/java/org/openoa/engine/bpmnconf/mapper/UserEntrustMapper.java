package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.bpmnconf.confentity.UserEntrust;
import org.openoa.base.vo.Entrust;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEntrustMapper extends BaseMapper<UserEntrust> {
    List<Entrust> getEntrustList(@Param("userId") Integer userId);

    List<Entrust> getEntrustListNew(@Param("userId") Integer userId);

    List<Entrust> getEntrustPageList(Page page,@Param("userId") Integer userId);
}
