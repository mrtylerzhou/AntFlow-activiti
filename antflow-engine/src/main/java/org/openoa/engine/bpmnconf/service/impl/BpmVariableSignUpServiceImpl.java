package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.openoa.base.entity.BpmVariableSignUp;

import org.openoa.engine.bpmnconf.mapper.BpmVariableSignUpMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVariableSignUpService;
import org.springframework.stereotype.Service;

@Service
public class BpmVariableSignUpServiceImpl extends ServiceImpl<BpmVariableSignUpMapper, BpmVariableSignUp> implements BpmVariableSignUpService {

}