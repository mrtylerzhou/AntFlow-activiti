package org.openoa.engine.bpmnconf.service.interf;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessAppApplication;
import org.openoa.engine.vo.BaseApplicationVo;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;

import java.util.List;

public interface ApplicationService extends  IService<BpmProcessAppApplication> {

    void del(Integer id);

    BpmProcessAppApplicationVo getApplicationUrl(String businessCode, String processKey);

    List<BaseApplicationVo> getApplicationKeyList(BpmProcessAppApplicationVo applicationVo);
}
