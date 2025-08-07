package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.base.entity.DictData;

import java.util.List;

@Mapper
public interface DicDataMapper extends BaseMapper<DictData> {
    List<DictData> selectLFActiveFormCodes();
    List<DictData> selectLFFormCodePageList(Page page,@Param("vo") TaskMgmtVO taskMgmtVO);
    List<DictData> selectLFActiveFormCodePageList(Page page, @Param("vo") TaskMgmtVO taskMgmtVO);
}
