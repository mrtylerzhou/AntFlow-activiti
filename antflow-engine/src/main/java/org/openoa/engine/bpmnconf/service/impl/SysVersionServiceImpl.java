package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.APPTypeEnum;
import org.openoa.base.constant.enums.AppApplicationType;
import org.openoa.base.constant.enums.VersionIsForceEnums;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.BpmProcessAppApplication;
import org.openoa.base.entity.BpmProcessAppData;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.QuickEntry;
import org.openoa.base.entity.SysVersion;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.SysVersionMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessAppDataBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessAppApplicationService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessAppDataService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.openoa.engine.bpmnconf.service.interf.repository.QuickEntryService;
import org.openoa.engine.bpmnconf.service.interf.repository.SysVersionService;
import org.openoa.engine.vo.AppDataSaveVo;
import org.openoa.engine.vo.AppVersionVo;
import org.openoa.engine.vo.SysVersionVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
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

    @Autowired
    private BpmProcessAppDataService bpmProcessAppDataService;

    @Autowired
    private BpmProcessAppApplicationService bpmProcessAppApplicationService;

    @Autowired
    private QuickEntryService quickEntryService;

    @Autowired
    private BpmnConfService bpmnConfService;

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
        Integer curIndex = cur.getIndx();

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
            wrapper.eq("indx", index);
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
        wrapper.gt("indx", minIndex.intValue());
        wrapper.le("indx", maxIndex.intValue());
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
        //version number must be unique among valid records
        checkVersionUnique(vo);

        if (vo.getId()!=null) {
            SysVersion current = this.getById(vo.getId());
            if (current==null || Integer.valueOf(1).equals(current.getIsDel())) {
                throw new AFBizException("版本不存在");
            }
            SysVersion sysVersion = new SysVersion();
            if (SysVersion.HIDE_STATUS_1.equals(current.getIsHide())) {
                //draft version: full fields editable
                BeanUtils.copyProperties(vo, sysVersion);
                if ((vo.getIsHide()!=null)) {
                    sysVersion.setEffectiveTime(new Date());
                }
            } else {
                //published version: only operation fields(is_force/android_url/ios_url/download_code) editable
                sysVersion.setId(vo.getId());
                sysVersion.setIsForce(vo.getIsForce());
                sysVersion.setAndroidUrl(vo.getAndroidUrl());
                sysVersion.setIosUrl(vo.getIosUrl());
                sysVersion.setDownloadCode(vo.getDownloadCode());
            }
            if (this.updateById(sysVersion)) {
                if (SysVersion.HIDE_STATUS_1.equals(current.getIsHide())
                        && !CollectionUtils.isEmpty(vo.getAppIds()) && !CollectionUtils.isEmpty(vo.getDataIds())) {
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
            Integer maxIndex = getBaseMapper().maxIndex();
            sysVersion.setIndx(maxIndex==null ? 1 : maxIndex + 1);
            //置为未发布状态
            sysVersion.setIsHide(SysVersion.HIDE_STATUS_1);
            sysVersion.setIsDel(0);
            if (this.save(sysVersion)) {
                if (Boolean.TRUE.equals(vo.getInheritFromLast())) {
                    //inherit all related data from the previous max index version
                    copyAppDataFromPreviousVersion(sysVersion.getId());
                } else if (!CollectionUtils.isEmpty(vo.getAppIds()) && !CollectionUtils.isEmpty(vo.getDataIds())) {
                    bpmProcessAppDataBizService.addAppVersionData(vo.getAppIds(), sysVersion.getId(), 1);
                    bpmProcessAppDataBizService.addAppVersionData(vo.getDataIds(), sysVersion.getId(), 2);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * check version number uniqueness among valid records
     */
    private void checkVersionUnique(SysVersionVo vo) {
        if (ObjectUtils.isEmpty(vo.getVersion())) {
            throw new AFBizException("版本号不能为空");
        }
        Long count = this.count(new QueryWrapper<SysVersion>()
                .eq("version", vo.getVersion())
                .eq("is_del", 0)
                .ne(vo.getId()!=null, "id", vo.getId()));
        if (count!=null && count > 0) {
            throw new AFBizException("版本号已存在: " + vo.getVersion());
        }
    }

    /**
     * copy all types of related app data from the previous max index version to the new version
     *
     * @param newVersionId new version id
     */
    private void copyAppDataFromPreviousVersion(Long newVersionId) {
        List<SysVersion> previousList = this.list(new QueryWrapper<SysVersion>()
                .eq("is_del", 0)
                .ne("id", newVersionId)
                .orderByDesc("indx"));
        if (CollectionUtils.isEmpty(previousList)) {
            return;
        }
        SysVersion source = previousList.get(0);
        List<BpmProcessAppData> rows = new ArrayList<>();
        for (AppApplicationType type : AppApplicationType.values()) {
            List<BpmProcessAppData> sourceRows = bpmProcessAppDataService.getProcessAppData(source.getId(), 0, type.getCode());
            if (CollectionUtils.isEmpty(sourceRows)) {
                continue;
            }
            rows.addAll(sourceRows.stream().map(o -> BpmProcessAppData.builder()
                    .versionId(newVersionId)
                    .type(type.getCode())
                    .applicationId(o.getApplicationId())
                    .processKey(o.getProcessKey())
                    .processName(o.getProcessName())
                    .state(0)
                    .sort(o.getSort())
                    .build()).collect(Collectors.toList()));
        }
        if (!rows.isEmpty()) {
            bpmProcessAppDataService.saveBatch(rows);
        }
    }

    @Transactional
    @Override
    public Boolean publish(Long id) {
        SysVersion current = this.getById(id);
        if (current==null || Integer.valueOf(1).equals(current.getIsDel())) {
            throw new AFBizException("版本不存在");
        }
        if (!SysVersion.HIDE_STATUS_1.equals(current.getIsHide())) {
            throw new AFBizException("仅草稿版本可发布");
        }
        SysVersion update = new SysVersion();
        update.setId(id);
        update.setIsHide(SysVersion.HIDE_STATUS_0);
        update.setEffectiveTime(new Date());
        return this.updateById(update);
    }

    @Transactional
    @Override
    public Boolean deleteDraft(Long id) {
        SysVersion current = this.getById(id);
        if (current==null || Integer.valueOf(1).equals(current.getIsDel())) {
            throw new AFBizException("版本不存在");
        }
        if (!SysVersion.HIDE_STATUS_1.equals(current.getIsHide())) {
            throw new AFBizException("仅草稿版本可删除");
        }
        SysVersion update = new SysVersion();
        update.setId(id);
        update.setIsDel(1);
        boolean updated = this.updateById(update);
        //cascade remove related app data of the draft version
        bpmProcessAppDataService.remove(new QueryWrapper<BpmProcessAppData>().eq("version_id", id));
        return updated;
    }

    @Transactional
    @Override
    public Boolean saveAppDatas(AppDataSaveVo vo) {
        if (vo==null || vo.getVersionId()==null || vo.getType()==null) {
            throw new AFBizException(PARAM_ERROR.getCode(), PARAM_ERROR.getDesc());
        }
        boolean isQuickEntry = AppApplicationType.APP_QUICK_ENTRY.getCode().equals(vo.getType());
        boolean isAppData = AppApplicationType.APP_DATA.getCode().equals(vo.getType());
        if (!isQuickEntry
                && !isAppData
                && !AppApplicationType.APP.getCode().equals(vo.getType())) {
            throw new AFBizException("关联数据类型错误");
        }
        SysVersion current = this.getById(vo.getVersionId());
        if (current==null || Integer.valueOf(1).equals(current.getIsDel())) {
            throw new AFBizException("版本不存在");
        }
        if (!SysVersion.HIDE_STATUS_1.equals(current.getIsHide())) {
            throw new AFBizException("已发布版本的关联数据只读");
        }
        //full replacement: remove old rows first
        bpmProcessAppDataService.deleteAppVersionData(vo.getVersionId(), vo.getType());
        if (CollectionUtils.isEmpty(vo.getItems())) {
            return true;
        }
        List<BpmProcessAppData> rows = new ArrayList<>();
        int order = 1;
        for (AppDataSaveVo.AppDataItem item : vo.getItems()) {
            if (item==null || ObjectUtils.isEmpty(item.getId())) {
                continue;
            }
            BpmProcessAppData.BpmProcessAppDataBuilder builder = BpmProcessAppData.builder()
                    .versionId(vo.getVersionId())
                    .type(vo.getType())
                    .applicationId(item.getId())
                    .state(0)
                    .sort(item.getSort()!=null ? item.getSort() : order);
            if (isAppData) {
                //上线流程: 候选来自 bpmn_conf(effective_status=1), application_id/process_key 均存 formCode
                BpmnConf conf = bpmnConfService.getOne(new QueryWrapper<BpmnConf>()
                        .eq("form_code", item.getId())
                        .eq("effective_status", 1));
                if (conf==null) {
                    continue;
                }
                builder.processName(conf.getBpmnName());
                builder.processKey(conf.getFormCode());
            } else {
                Long objectId;
                try {
                    objectId = Long.parseLong(item.getId());
                } catch (NumberFormatException e) {
                    continue;
                }
                if (isQuickEntry) {
                    QuickEntry quickEntry = quickEntryService.getById(objectId);
                    if (quickEntry==null || Integer.valueOf(1).equals(quickEntry.getIsDel())) {
                        continue;
                    }
                    builder.processName(quickEntry.getTitle());
                } else {
                    BpmProcessAppApplication application = bpmProcessAppApplicationService.getById(objectId.intValue());
                    if (application==null || Integer.valueOf(1).equals(application.getIsDel())) {
                        continue;
                    }
                    builder.processName(application.getTitle());
                    builder.processKey(StringUtils.isEmpty(application.getBusinessCode())
                            ? application.getProcessKey()
                            : StringUtils.join(application.getBusinessCode(), "_", application.getProcessKey()));
                }
            }
            rows.add(builder.build());
            order++;
        }
        if (!rows.isEmpty()) {
            bpmProcessAppDataService.saveBatch(rows);
        }
        return true;
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
        SysVersion sysVersion = getOne(new QueryWrapper<SysVersion>().eq("indx", index));
        vo.setDownloadCode(Optional.ofNullable(sysVersion).map(SysVersion::getDownloadCode).orElse(null));
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
