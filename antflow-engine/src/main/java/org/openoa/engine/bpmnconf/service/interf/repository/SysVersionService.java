package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.SysVersion;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.vo.AppDataSaveVo;
import org.openoa.engine.vo.AppVersionVo;
import org.openoa.engine.vo.SysVersionVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SysVersionService extends IService<SysVersion> {
    AppVersionVo getAppVersion(String application, String appVersion);

    List<SysVersion> listVersion(Long id, String version, Integer index, Boolean isDel);

    List<SysVersion> listVersionByIndex(Integer minIndex, Integer maxIndex);

    SysVersion getInfoByVersion(String version);

    ResultAndPage<SysVersionVo> listSysVersion(SysVersionVo vo);

    @Transactional
    Boolean edit(SysVersionVo vo);

    SysVersionVo getDownloadQRcode();

    /**
     * publish a draft version,only draft(is_hide=1) can be published
     *
     * @param id version id
     * @return true for success
     */
    @Transactional
    Boolean publish(Long id);

    /**
     * logically delete a draft version and its related app data,only draft(is_hide=1) can be deleted
     *
     * @param id version id
     * @return true for success
     */
    @Transactional
    Boolean deleteDraft(Long id);

    /**
     * full replacement save of a version's related data(ordered items with sort),only draft(is_hide=1) is allowed
     *
     * @param vo {versionId,type,items}
     * @return true for success
     */
    @Transactional
    Boolean saveAppDatas(AppDataSaveVo vo);
}
