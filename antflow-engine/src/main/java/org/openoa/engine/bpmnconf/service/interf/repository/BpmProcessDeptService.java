package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessDept;

import java.util.List;

public interface BpmProcessDeptService extends IService<BpmProcessDept> {
    String maxProcessCode();

    String getProcessCode(String testStr);

    List<String> getAllProcess();
}
