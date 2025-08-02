package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.util.StringUtil;
import org.openoa.base.constant.enums.AppApplicationType;
import org.openoa.base.constant.enums.VersionIsForceEnums;
import org.openoa.base.dto.PageDto;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.entity.SysVersion;
import org.openoa.engine.bpmnconf.mapper.SysVersionMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.SysVersionService;
import org.openoa.engine.vo.AppVersionVo;
import org.openoa.engine.vo.SysVersionVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * sys version service
 * @since0.5
 */
@Service
public class SysVersionServiceImpl extends ServiceImpl<SysVersionMapper, SysVersion> implements SysVersionService {

    public static final String APP_TYPE_ANDROID = "android";
    public static final String APP_TYPE_IOS = "ios";
    @Autowired
    private SysVersionMapper sysVersionMapper;

    @Autowired
    private BpmProcessAppDataServiceImpl bpmProcessAppDataService;
    @Value("${app.ios.skip_force_version:}")
    private String iosSkipForceVersion;
    @Value("${app.android.skip_force_version:}")
    private String androidSkipForceVersion;

    /**
     * get app version info
     *
     * @param application app type ios,android,open harmony
     * @param appVersion appversion
     * @return AppVersionVo
     */
    public AppVersionVo getAppVersion(String application, String appVersion) {
        if (StringUtil.isEmpty(application) || StringUtil.isEmpty(appVersion)) {
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
        Integer maxIndex = sysVersionMapper.maxIndex();
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
        boolean androidSkipFlag = application.equalsIgnoreCase(APP_TYPE_ANDROID) && androidSkipForceVersion.equals(sysVersion.getVersion());
        boolean iosSkipFlag = application.equalsIgnoreCase(APP_TYPE_IOS) && iosSkipForceVersion.equals(sysVersion.getVersion());
        if (androidSkipFlag || iosSkipFlag) {
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
            if (application.equalsIgnoreCase(APP_TYPE_ANDROID)) {
                appVersionVo.setDownloadUrl(maxVersion.getAndroidUrl());
            } else if (application.equalsIgnoreCase(APP_TYPE_IOS)) {
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

    /**
     * get version info
     *
     * @param id
     * @param version
     * @param index
     * @param isDel   true:filtering deleted only keep valid data
     * @return
     */
    public List<SysVersion> listVersion(Long id, String version, Integer index, Boolean isDel) {
        if ((id==null) && (StringUtil.isEmpty(version)) && (index==null) && (!isDel)) {
            //listVersion params can not be all null
            return Collections.EMPTY_LIST;
        }
        QueryWrapper<SysVersion> wrapper = new QueryWrapper<>();
        if (id!=null) {
            wrapper.eq("id", id);
        }
        if (!StringUtil.isEmpty(version)) {
            wrapper.eq("version", version);
        }
        if (index!=null) {
            wrapper.eq("`index`", index);
        }
        if (!isDel) {
            if (isDel) {
                wrapper.eq("is_del", 0);
            }
        }
        return sysVersionMapper.selectList(wrapper);
    }

    /**
     * get all versions from min to max index
     * @param minIndex
     * @param maxIndex
     * @return
     */
    public List<SysVersion> listVersionByIndex(Integer minIndex, Integer maxIndex) {
        if ((minIndex==null) || (maxIndex==null)) {
            //入参都不能为空
            return Collections.EMPTY_LIST;
        }
        QueryWrapper<SysVersion> wrapper = new QueryWrapper<>();
        wrapper.gt("`index`", minIndex.intValue());
        wrapper.le("`index`", maxIndex.intValue());
        return sysVersionMapper.selectList(wrapper);
    }

    /**
     * get version info by version number
     *
     * @param version
     * @return
     */
    public SysVersion getInfoByVersion(String version) {
        if (StringUtil.isEmpty(version)) {
            throw new AFBizException( "版本号错误!");
        }
        return getOne(new QueryWrapper<SysVersion>().eq("version", version));
    }

    /**
     * get a list of version info by version vo
     *
     * @param vo SysVersionVo
     * @return
     */
    public ResultAndPage<SysVersionVo> list(SysVersionVo vo) {
        PageDto pageDto = PageUtils.getPageDtoByVo(vo);
        Page<SysVersionVo> page = PageUtils.getPageByPageDto(pageDto);
        Integer totalCount = sysVersionMapper.selectPageListCount(vo);
        page.setTotal(totalCount);
        List<SysVersionVo> dtoList = totalCount > 0 ? sysVersionMapper.selectPageList(vo, pageDto) : Collections.EMPTY_LIST;
        page.setRecords(dtoList);
        page.setRecords(dtoList
                .stream()
                .map(o -> {
                    List<BaseIdTranStruVo> appList = bpmProcessAppDataService.findAppDataByVersionId(o.getId(), AppApplicationType.ONE_TYPE.getCode());
                    if (!CollectionUtils.isEmpty(appList)) {
                        o.setApplication(appList);
                    }
                    List<BaseIdTranStruVo> dataList = bpmProcessAppDataService.findAppDataByVersionId(o.getId(), AppApplicationType.TWO_TYPE.getCode());
                    if (!CollectionUtils.isEmpty(dataList)) {
                        o.setData(dataList);
                    }
                    List<BaseIdTranStruVo> quickEntryList = bpmProcessAppDataService.findAppDataByVersionId(o.getId(), AppApplicationType.THREE_TYPE.getCode());
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
            if (this.updateById(sysVersion)) {
                if (!CollectionUtils.isEmpty(vo.getAppIds()) && !CollectionUtils.isEmpty(vo.getDataIds())) {
                    bpmProcessAppDataService.addAppVersionData(vo.getAppIds(), sysVersion.getId(), AppApplicationType.ONE_TYPE.getCode());
                    bpmProcessAppDataService.addAppVersionData(vo.getDataIds(), sysVersion.getId(), AppApplicationType.TWO_TYPE.getCode());
                    bpmProcessAppDataService.addVersionData(vo.getQuickEntryIds(), sysVersion.getId(), AppApplicationType.THREE_TYPE.getCode());
                }
                return true;
            }
        } else {
            SysVersion sysVersion = new SysVersion();
            BeanUtils.copyProperties(vo, sysVersion);
            //set index incr 1
            Integer index = sysVersionMapper.maxIndex() + 1;
            //set is hide(unpublished)
            sysVersion.setIsHide(1);
            sysVersion.setIndex(index);
            if (this.save(sysVersion)) {
                if (!CollectionUtils.isEmpty(vo.getAppIds()) && !CollectionUtils.isEmpty(vo.getDataIds())) {
                    bpmProcessAppDataService.addAppVersionData(vo.getAppIds(), sysVersion.getId(), 1);
                    bpmProcessAppDataService.addAppVersionData(vo.getDataIds(), sysVersion.getId(), 2);
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
    public SysVersionVo getDownloadQRcode() {
        SysVersionVo vo = new SysVersionVo();
        Integer index = sysVersionMapper.maxIndex();
        SysVersion sysVersion = getOne(new QueryWrapper<SysVersion>().eq("`index`", index));
        vo.setDownloadCode(sysVersion.getDownloadCode());
        return vo;
    }
}
