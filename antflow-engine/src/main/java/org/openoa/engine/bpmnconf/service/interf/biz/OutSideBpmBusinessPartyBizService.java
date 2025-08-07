package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.BpmProcessAppApplication;
import org.openoa.base.entity.OutSideBpmBusinessParty;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmBusinessPartyMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.OutSideBpmBusinessPartyService;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.openoa.engine.vo.NodeRolePersonVo;
import org.openoa.engine.vo.OutSideBpmApplicationVo;
import org.openoa.engine.vo.OutSideBpmBusinessPartyVo;

import java.util.List;

public interface OutSideBpmBusinessPartyBizService extends BizService<OutSideBpmBusinessPartyMapper, OutSideBpmBusinessPartyService, OutSideBpmBusinessParty>{
    ResultAndPage<OutSideBpmBusinessPartyVo> listPage(PageDto pageDto, OutSideBpmBusinessPartyVo vo);

    OutSideBpmBusinessPartyVo detail(Integer id);

    String getBusinessPartyMarkById(Long id);

    void edit(OutSideBpmBusinessPartyVo vo);

    Long editApplication(OutSideBpmApplicationVo vo);

    ResultAndPage<BpmProcessAppApplicationVo> applicationsPageList(PageDto page, BpmProcessAppApplicationVo vo);

    BpmProcessAppApplication getApplicationDetailById(Integer id);

    List<BpmnConfVo> getBpmConf(String businessPartyMark);

    void syncRolePersonnel(String businessPartyMark, NodeRolePersonVo userList);
}
