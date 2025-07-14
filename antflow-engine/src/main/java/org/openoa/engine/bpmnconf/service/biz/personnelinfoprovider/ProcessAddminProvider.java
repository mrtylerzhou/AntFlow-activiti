package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import org.openoa.base.interf.BpmnProcessAdminProvider;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//用户可以改写这个类，或者自己实现然后加上@primary注解
@Component
public class ProcessAddminProvider implements BpmnProcessAdminProvider {
    @Override
    public BaseIdTranStruVo provideProcessAdminInfo() {
        return BaseIdTranStruVo.builder().id("20").name("任盈盈").build();
    }
}
