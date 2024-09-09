package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.AppApplicationType;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.confentity.BpmProcessAppApplication;
import org.openoa.engine.bpmnconf.confentity.BpmProcessAppData;
import org.openoa.engine.bpmnconf.confentity.QuickEntry;
import org.openoa.engine.bpmnconf.confentity.SysVersion;
import org.openoa.engine.bpmnconf.mapper.BpmProcessAppDataMapper;
import org.openoa.engine.vo.SysVersionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 *  app process go online service
 */
@Service
public class BpmProcessAppDataServiceImpl extends ServiceImpl<BpmProcessAppDataMapper, BpmProcessAppData> {

    @Autowired
    private BpmProcessAppDataMapper bpmProcessAppDataMapper;

    @Autowired
    private SysVersionServiceImpl sysVersionService;

    @Autowired
    private BpmProcessAppApplicationServiceImpl bpmProcessAppApplicationService;

    @Autowired
    private QuickEntryServiceImpl quickEntryService;

    /**
     *  get all entrance application by version and process key
     *
     * @param versionId
     * @return
     */
    public List<String> getBpmProcessAppDataVo(Long versionId, List<String> processKey) {
        QueryWrapper<BpmProcessAppData> bpmProcessAppDataWrapper = new QueryWrapper<>();
        bpmProcessAppDataWrapper.eq("version_id", versionId);
        bpmProcessAppDataWrapper.eq("type", 1);
        bpmProcessAppDataWrapper.in("process_key", processKey);
        List<BpmProcessAppData> list = bpmProcessAppDataMapper.selectList(bpmProcessAppDataWrapper);
        if (!CollectionUtils.isEmpty(list)) {
            return list.stream().map(BpmProcessAppData::getProcessKey).collect(Collectors.toList());
        } else {
            return Arrays.asList();
        }
    }

    /**
     * query all app process for all users
     */
    public List<BpmProcessAppData> getProcessAppData(Long versionId, Integer isAll, Integer type) {
        QueryWrapper<BpmProcessAppData> bpmProcessAppDataWrapper = new QueryWrapper<>();
        bpmProcessAppDataWrapper.eq("state", isAll);
        bpmProcessAppDataWrapper.eq("version_id", versionId);
        bpmProcessAppDataWrapper.eq("type", type);
        return bpmProcessAppDataMapper.selectList(bpmProcessAppDataWrapper);
    }



    public SysVersionVo findMaxAppData() {
        List<SysVersion> sysVersions = sysVersionService.list(new QueryWrapper<SysVersion>().eq("is_del", 0).orderByAsc("id"));
        SysVersionVo vo = new SysVersionVo();
        if (!CollectionUtils.isEmpty(sysVersions)) {
            SysVersion sysVersion = sysVersions.get(0);
            List<BpmProcessAppData> processAppData = this.getProcessAppData(sysVersion.getId(), 0, AppApplicationType.TWO_TYPE.getCode());
            List<BaseIdTranStruVo> voList = new ArrayList<>();
            voList.addAll(
                    processAppData.stream().map(o ->
                            BaseIdTranStruVo.builder()
                                    .id(o.getApplicationId().toString())
                                    .name(o.getProcessName())
                                    .build()
                    ).collect(Collectors.toList()));
            List<BpmProcessAppData> appDataList = this.getProcessAppData(sysVersion.getId(), 0, AppApplicationType.ONE_TYPE.getCode());
            List<BaseIdTranStruVo> list = new ArrayList<>();
            list.addAll(
                    appDataList.stream().map(o ->
                            BaseIdTranStruVo.builder()
                                    .id(o.getApplicationId().toString())
                                    .name(o.getProcessName())
                                    .build()
                    ).collect(Collectors.toList()));
            vo.setApplication(list);
            vo.setData(voList);
            vo.setDownloadCode(sysVersion.getDownloadCode());
            return vo;
        }
        return vo;
    }

    /**
     * get current version's process
     * @param versionId version id
     * @param type      (1:current version's app 2:current version's app data)
     * @return
     */
    public List<BaseIdTranStruVo> findAppDataByVersionId(Long versionId, Integer type) {
        if (versionId!=null) {
            List<BpmProcessAppData> processAppData = this.getProcessAppData(versionId, 0, type);
            List<BaseIdTranStruVo> voList = new ArrayList<>();
            voList.addAll(
                    processAppData.stream().map(o ->
                            BaseIdTranStruVo.builder()
                                    .id(o.getApplicationId().toString())
                                    .name(o.getProcessName())
                                    .build()
                    ).collect(Collectors.toList()));
            return voList;
        }
        return Arrays.asList();
    }

    /**
     * add data to app version
     *
     * @param applicationIds application id
     * @param versionId     version id
     * @param type           (1:app 2:app data)
     */
    public void addAppVersionData(List<Long> applicationIds, Long versionId, Integer type) {
        this.deleteAppVersionData(versionId, type);
        this.saveBatch(
                applicationIds.stream().map(o ->
                        {
                            BpmProcessAppApplication bpmProcessAppApplication = bpmProcessAppApplicationService.getById(o);

                            if(bpmProcessAppApplication!=null){
                                return BpmProcessAppData.builder()
                                        .processName(bpmProcessAppApplication.getTitle())
                                        .processKey(getProcessKey(o))
                                        .versionId(versionId)
                                        .type(type)
                                        .applicationId(o)
                                        .build();
                            }else{
                                return null;
                            }
                        }
                ).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    /**
     * get process key
     *
     * @param id
     * @return
     */
    private String getProcessKey(Long id) {
        BpmProcessAppApplication bpmProcessAppApplication = bpmProcessAppApplicationService.getById(id);

        if (bpmProcessAppApplication==null) {
            return StringUtils.EMPTY;
        }

        if (!StringUtil.isEmpty(bpmProcessAppApplication.getBusinessCode())) {
            return StringUtils.join(bpmProcessAppApplication.getBusinessCode(), "_", bpmProcessAppApplication.getProcessKey());
        }

        return bpmProcessAppApplication.getProcessKey();
    }

    /**
     * add version data
     *
     * @param applicationIds app ids
     * @param versionId      version id
     * @param type           (1:app 2:app data)
     */
    public void addVersionData(List<Long> applicationIds, Long versionId, Integer type) {
        this.deleteAppVersionData(versionId, type);
        this.saveBatch(
                applicationIds.stream().map(o ->
                        BpmProcessAppData.builder()
                                .versionId(versionId)
                                .type(type)
                                .applicationId(o)
                                .processName(Optional.ofNullable(quickEntryService.getById(o)).orElse(new QuickEntry()).getTitle())
                                .build()
                ).collect(Collectors.toList()));
    }

    /**
     * remove data from a specified version
     */
    public void deleteAppVersionData(Long versionId, Integer type) {
        this.remove(new QueryWrapper<BpmProcessAppData>().eq("version_id", versionId).eq("type", type));
    }

}
