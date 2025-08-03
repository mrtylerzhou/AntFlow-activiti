package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.AppApplicationType;
import org.openoa.base.entity.BpmProcessAppApplication;
import org.openoa.base.entity.BpmProcessAppData;
import org.openoa.base.entity.QuickEntry;
import org.openoa.base.entity.SysVersion;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessAppApplicationServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.QuickEntryServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.SysVersionServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessAppDataBizService;
import org.openoa.engine.vo.SysVersionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BpmProcessAppDataBizServiceImpl implements BpmProcessAppDataBizService {
    @Autowired
    @Lazy
    private SysVersionServiceImpl sysVersionService;

    @Autowired
    private BpmProcessAppApplicationServiceImpl bpmProcessAppApplicationService;

    @Autowired
    @Lazy
    private QuickEntryServiceImpl quickEntryService;

    @Override
    public SysVersionVo findMaxAppData() {
        List<SysVersion> sysVersions = sysVersionService.list(new QueryWrapper<SysVersion>().eq("is_del", 0).orderByAsc("id"));
        SysVersionVo vo = new SysVersionVo();
        if (!CollectionUtils.isEmpty(sysVersions)) {
            SysVersion sysVersion = sysVersions.get(0);
            List<BpmProcessAppData> processAppData = this.getService().getProcessAppData(sysVersion.getId(), 0, AppApplicationType.TWO_TYPE.getCode());
            List<BaseIdTranStruVo> voList = new ArrayList<>();
            voList.addAll(
                    processAppData.stream().map(o ->
                            BaseIdTranStruVo.builder()
                                    .id(o.getApplicationId().toString())
                                    .name(o.getProcessName())
                                    .build()
                    ).collect(Collectors.toList()));
            List<BpmProcessAppData> appDataList = this.getService().getProcessAppData(sysVersion.getId(), 0, AppApplicationType.ONE_TYPE.getCode());
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
    @Override
    public List<BaseIdTranStruVo> findAppDataByVersionId(Long versionId, Integer type) {
        if (versionId!=null) {
            List<BpmProcessAppData> processAppData = this.getService().getProcessAppData(versionId, 0, type);
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
    @Override
    public void addAppVersionData(List<Long> applicationIds, Long versionId, Integer type) {
        this.getService().deleteAppVersionData(versionId, type);
        this.getService().saveBatch(
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
    @Override
    public void addVersionData(List<Long> applicationIds, Long versionId, Integer type) {
        this.getService().deleteAppVersionData(versionId, type);
        this.getService().saveBatch(
                applicationIds.stream().map(o ->
                        BpmProcessAppData.builder()
                                .versionId(versionId)
                                .type(type)
                                .applicationId(o)
                                .processName(Optional.ofNullable(quickEntryService.getById(o)).orElse(new QuickEntry()).getTitle())
                                .build()
                ).collect(Collectors.toList()));
    }

}
