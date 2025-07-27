package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * @Classname BizServiceImpl
 * @Description TODO
 * @Date 2021-10-31 15:58
 * @Created by AntOffice
 */
public class BizServiceImpl<T extends ServiceImpl> {

    @Autowired
    @Lazy
    protected T service;
}
