package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openoa.base.constant.enums.AppApplicationType;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.SysVersion;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessAppDataBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.SysVersionBizService;
import org.openoa.engine.vo.SysVersionVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysVersionBizServiceImpl implements SysVersionBizService {
    @Autowired
    private BpmProcessAppDataBizService processAppDataBizService;

    /**
     * get a list of version info by version vo
     *
     * @param vo SysVersionVo
     * @return
     */
    @Override
    public ResultAndPage<SysVersionVo> list(SysVersionVo vo) {
        PageDto pageDto = PageUtils.getPageDtoByVo(vo);
        Page<SysVersionVo> page = PageUtils.getPageByPageDto(pageDto);
        Integer totalCount = getMapper().selectPageListCount(vo);
        page.setTotal(totalCount);
        List<SysVersionVo> dtoList = totalCount > 0 ? getMapper().selectPageList(vo, pageDto) : Collections.EMPTY_LIST;
        page.setRecords(dtoList);
        page.setRecords(dtoList
                .stream()
                .map(o -> {
                    List<BaseIdTranStruVo> appList = processAppDataBizService.findAppDataByVersionId(o.getId(), AppApplicationType.ONE_TYPE.getCode());
                    if (!CollectionUtils.isEmpty(appList)) {
                        o.setApplication(appList);
                    }
                    List<BaseIdTranStruVo> dataList = processAppDataBizService.findAppDataByVersionId(o.getId(), AppApplicationType.TWO_TYPE.getCode());
                    if (!CollectionUtils.isEmpty(dataList)) {
                        o.setData(dataList);
                    }
                    List<BaseIdTranStruVo> quickEntryList = processAppDataBizService.findAppDataByVersionId(o.getId(), AppApplicationType.THREE_TYPE.getCode());
                    if (!CollectionUtils.isEmpty(quickEntryList)) {
                        o.setQuickEntryList(quickEntryList);
                    }
                    return o;
                }).collect(Collectors.toList()));
        return new ResultAndPage<>(dtoList, PageUtils.getPageDto(page));
    }

    /**
     * edit version info
     *
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public Boolean edit(SysVersionVo vo) {
        if (vo==null) {
            throw new AFBizException("object can not be null");
        }

        if (vo.getId()!=null) {
            SysVersion sysVersion = new SysVersion();
            BeanUtils.copyProperties(vo, sysVersion);
            if (vo.getIsHide()!=null) {
                sysVersion.setEffectiveTime(new Date());
            }
            if (this.getService().updateById(sysVersion)) {
                if (!CollectionUtils.isEmpty(vo.getAppIds()) && !CollectionUtils.isEmpty(vo.getDataIds())) {
                    processAppDataBizService.addAppVersionData(vo.getAppIds(), sysVersion.getId(), AppApplicationType.ONE_TYPE.getCode());
                    processAppDataBizService.addAppVersionData(vo.getDataIds(), sysVersion.getId(), AppApplicationType.TWO_TYPE.getCode());
                    processAppDataBizService.addVersionData(vo.getQuickEntryIds(), sysVersion.getId(), AppApplicationType.THREE_TYPE.getCode());
                }
                return true;
            }
        } else {
            SysVersion sysVersion = new SysVersion();
            BeanUtils.copyProperties(vo, sysVersion);
            //set index incr 1
            Integer index = getMapper().maxIndex() + 1;
            //set is hide(unpublished)
            sysVersion.setIsHide(1);
            sysVersion.setIndex(index);
            if (this.getService().save(sysVersion)) {
                if (!CollectionUtils.isEmpty(vo.getAppIds()) && !CollectionUtils.isEmpty(vo.getDataIds())) {
                    processAppDataBizService.addAppVersionData(vo.getAppIds(), sysVersion.getId(), 1);
                    processAppDataBizService.addAppVersionData(vo.getDataIds(), sysVersion.getId(), 2);
                }
                return true;
            }
        }
        return false;
    }

}
