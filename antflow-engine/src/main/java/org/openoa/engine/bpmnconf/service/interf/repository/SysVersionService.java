package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.SysVersion;
import org.openoa.engine.vo.AppVersionVo;
import org.openoa.engine.vo.SysVersionVo;

import java.util.List;

public interface SysVersionService extends IService<SysVersion> {
    AppVersionVo getAppVersion(String application, String appVersion);

    List<SysVersion> listVersion(Long id, String version, Integer index, Boolean isDel);

    List<SysVersion> listVersionByIndex(Integer minIndex, Integer maxIndex);

    SysVersion getInfoByVersion(String version);

    SysVersionVo getDownloadQRcode();
}
