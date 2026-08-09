package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.constant.enums.APPTypeEnum;
import org.openoa.base.constant.enums.AppApplicationType;
import org.openoa.base.constant.enums.VersionIsForceEnums;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.SysVersion;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.SysVersionMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessAppDataBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.SysVersionService;
import org.openoa.engine.vo.AppVersionVo;
import org.openoa.engine.vo.SysVersionVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.openoa.base.vo.OperationResp.PARAM_ERROR;


/**
 * sys version service
 * @since0.5
 */
@Repository
public class SysVersionServiceImpl extends ServiceImpl<SysVersionMapper, SysVersion> implements SysVersionService {

    @Autowired
    private BpmProcessAppDataBizService bpmProcessAppDataBizService;

    @Value("${app.ios.skip_force_version:}")
    private String iosSkipForceVersion;
    @Value("${app.android.skip_force_version:}")
    private String androidSkipForceVersion;
    @Value("${app.harmony.skip_force_version:}")
    private String harmonySkipForceVersion;

    /**
     * get app version info
     *
     * @param application app type ios,android,open harmony
     * @param appVersion appversion
     * @return AppVersionVo
     */
    @Override
    public AppVersionVo getAppVersion(String application, String appVersion) {
        if (ObjectUtils.isEmpty(application) || ObjectUtils.isEmpty(appVersion)) {
            return null;
        }
        List<SysVersion> list;
        AppVersionVo appVersionVo = new AppVersionVo();

        //get latest version
        SysVersion cur;
        list = listVersion(null, appVersion, null, true);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        cur = list.get(0);
        if (SysVersion.HIDE_STATUS_1.equals(cur.getIsHide())) {
            appVersionVo.setIsLatest(1);
            return appVersionVo;
        }


        //current version index
        Integer curIndex = cur.getIndex();

        //get max index
        Integer maxIndex = getBaseMapper().maxIndex();
        if (maxIndex==null) {
            //if max index is null then return null
            return null;
        }


        if (curIndex==null) {

            return null;
        }


        //to check whether current version is latest
        if (curIndex.intValue() == maxIndex.intValue()) {
            appVersionVo.setIsLatest(1);
            return appVersionVo;
        }
        SysVersion sysVersion = listVersion(null, null, maxIndex, true).stream().findFirst().orElseGet(SysVersion::new);
        boolean androidSkipFlag = application.equalsIgnoreCase(APPTypeEnum.ANDROID.getName()) && androidSkipForceVersion.equals(sysVersion.getVersion());
        boolean iosSkipFlag = application.equalsIgnoreCase(APPTypeEnum.IOS.getName()) && iosSkipForceVersion.equals(sysVersion.getVersion());
        boolean harmonySkipFlag = application.equalsIgnoreCase(APPTypeEnum.HARMONY_OS.getName()) && harmonySkipForceVersion.equals(sysVersion.getVersion());
        if (androidSkipFlag || iosSkipFlag||harmonySkipFlag) {
            appVersionVo.setIsLatest(1);
            return appVersionVo;
        }


        List<SysVersion> maxList = listVersion(null, null, maxIndex, true);
        SysVersion maxVersion;
        if (!CollectionUtils.isEmpty(maxList)) {
            maxVersion = maxList.get(0);
        } else {
            //something wrong
            return null;
        }

        list = listVersionByIndex(curIndex, maxIndex);
        boolean force = false;
        if (!CollectionUtils.isEmpty(list)) {
            for (SysVersion s : list) {
                if (VersionIsForceEnums.RECRUIT_TYPE_SYYG.getCode() == s.getIsForce().intValue()) {
                    force = true;
                }
            }
        } else {

            return null;
        }

        if (maxVersion!=null) {
            if (application.equalsIgnoreCase(APPTypeEnum.ANDROID.getName())) {
                appVersionVo.setDownloadUrl(maxVersion.getAndroidUrl());
            } else if (application.equalsIgnoreCase(APPTypeEnum.IOS.getName())) {
                appVersionVo.setDownloadUrl(maxVersion.getIosUrl());
            }
            appVersionVo.setDescription(maxVersion.getDescription());
            appVersionVo.setId(maxVersion.getId());
            appVersionVo.setVersion(maxVersion.getVersion());
            appVersionVo.setCurVersion(appVersion);
        } else {

            return null;
        }

        if (!force) {

            appVersionVo.setIsLatest(0);
            appVersionVo.setIsForce(0);
            return appVersionVo;
        } else {

            appVersionVo.setIsLatest(0);
            appVersionVo.setIsForce(1);
            return appVersionVo;
        }
    }




    /**
     * get version info
     *
     * @param id
     * @param version
     * @param index
     * @param isDel   true:filtering deleted only keep valid data
     * @return
     */
    @Override
    public List<SysVersion> listVersion(Long id, String version, Integer index, Boolean isDel) {
        if ((id==null) && (ObjectUtils.isEmpty(version)) && (index==null) && (!isDel)) {
            //listVersion params can not be all null
            return Collections.EMPTY_LIST;
        }
        QueryWrapper<SysVersion> wrapper = new QueryWrapper<>();
        if (id!=null) {
            wrapper.eq("id", id);
        }
        if (!ObjectUtils.isEmpty(version)) {
            wrapper.eq("version", version);
        }
        if (index!=null) {
            wrapper.eq("`index`", index);
        }
        if (isDel!=null) {
            wrapper.eq("is_del", 0);
        }
        return getBaseMapper().selectList(wrapper);
    }

    /**
     * get all versions from min to max index
     * @param minIndex
     * @param maxIndex
     * @return
     */
    @Override
    public List<SysVersion> listVersionByIndex(Integer minIndex, Integer maxIndex) {
        if ((minIndex==null) || (maxIndex==null)) {
            //入参都不能为空
            return Collections.EMPTY_LIST;
        }
        QueryWrapper<SysVersion> wrapper = new QueryWrapper<>();
        wrapper.gt("`index`", minIndex.intValue());
        wrapper.le("`index`", maxIndex.intValue());
        return getBaseMapper().selectList(wrapper);
    }

    /**
     * get version info by version number
     *
     * @param version
     * @return
     */
    @Override
    public SysVersion getInfoByVersion(String version) {
        if (ObjectUtils.isEmpty(version)) {
            throw new AFBizException( "版本号错误!");
        }
        return getOne(new QueryWrapper<SysVersion>().eq("version", version));
    }


    @Override
    public ResultAndPage<SysVersionVo> listSysVersion(SysVersionVo vo) {
        PageDto pageDto = PageUtils.getPageDtoByVo(vo);
        Page<SysVersionVo> page = PageUtils.getPageByPageDto(pageDto);
        Integer totalCount = getBaseMapper().selectPageListCount(vo);
        page.setTotal(totalCount);
        pageDto.setStartIndex((int)page.offset());
        List<SysVersionVo> dtoList = totalCount > 0 ? getBaseMapper().selectPageList(vo, pageDto) : Collections.EMPTY_LIST;
        page.setRecords(dtoList);
        page.setRecords(dtoList
                .stream()
                .map(o -> {

                    List<BaseIdTranStruVo> appList = bpmProcessAppDataBizService.findAppDataByVersionId(o.getId(), AppApplicationType.APP.getCode());
                    if (!CollectionUtils.isEmpty(appList)) {
                        o.setApplication(appList);
                    }
                    List<BaseIdTranStruVo> dataList = bpmProcessAppDataBizService.findAppDataByVersionId(o.getId(), AppApplicationType.APP_DATA.getCode());
                    if (!CollectionUtils.isEmpty(dataList)) {
                        o.setData(dataList);
                    }
                    List<BaseIdTranStruVo> quickEntryList = bpmProcessAppDataBizService.findAppDataByVersionId(o.getId(), AppApplicationType.APP_QUICK_ENTRY.getCode());
                    if (!CollectionUtils.isEmpty(quickEntryList)) {
                        o.setQuickEntryList(quickEntryList);
                    }
                    return o;
                }).collect(Collectors.toList()));
        return new ResultAndPage<>(dtoList, PageUtils.getPageDto(page));
    }



    @Transactional
    @Override
    public Boolean edit(SysVersionVo vo) {
        if (vo==null) {
            throw new AFBizException(PARAM_ERROR.getCode(), PARAM_ERROR.getDesc());
        }

        if (vo.getId()!=null) {
            SysVersion sysVersion = new SysVersion();
            BeanUtils.copyProperties(vo, sysVersion);
            if ((vo.getIsHide()!=null)) {
                sysVersion.setEffectiveTime(new Date());
            }
            if (this.updateById(sysVersion)) {
                if (!CollectionUtils.isEmpty(vo.getAppIds()) && !CollectionUtils.isEmpty(vo.getDataIds())) {
                    bpmProcessAppDataBizService.addAppVersionData(vo.getAppIds(), sysVersion.getId(), AppApplicationType.APP.getCode());
                    bpmProcessAppDataBizService.addAppVersionData(vo.getDataIds(), sysVersion.getId(), AppApplicationType.APP_DATA.getCode());
                    bpmProcessAppDataBizService.addVersionData(vo.getQuickEntryIds(), sysVersion.getId(), AppApplicationType.APP_QUICK_ENTRY.getCode());
                }
                return true;
            }
        } else {
            SysVersion sysVersion = new SysVersion();
            BeanUtils.copyProperties(vo, sysVersion);
            //索引值加1
            Integer index = getBaseMapper().maxIndex() + 1;
            //置为未发布状态
            sysVersion.setIsHide(1);
            sysVersion.setIndex(index);
            if (this.save(sysVersion)) {
                if (!CollectionUtils.isEmpty(vo.getAppIds()) && !CollectionUtils.isEmpty(vo.getDataIds())) {
                    bpmProcessAppDataBizService.addAppVersionData(vo.getAppIds(), sysVersion.getId(), 1);
                    bpmProcessAppDataBizService.addAppVersionData(vo.getDataIds(), sysVersion.getId(), 2);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * get download code
     *
     * @return
     */
    @Override
    public SysVersionVo getDownloadQRcode() {
        SysVersionVo vo = new SysVersionVo();
        Integer index = getBaseMapper().maxIndex();
        SysVersion sysVersion = getOne(new QueryWrapper<SysVersion>().eq("`index`", index));
        vo.setDownloadCode(sysVersion.getDownloadCode());
        return vo;
    }
    private List<Integer> getIgnoreIndexs() {
        //todo
      /*  String ignoreIndexs = ConfigService.getConfig("service").getProperty("sys.version.ignore.indexs", "23");
        if (!StringUtil.isEmpty(ignoreIndexs)) {
            return Lists.newArrayList(ignoreIndexs)
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }*/
        return Collections.EMPTY_LIST;
    }
}
