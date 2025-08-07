package org.openoa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.service.AfUserService;
import org.openoa.entity.Person;
import org.openoa.entity.Student;
import org.openoa.base.entity.User;
import org.openoa.base.exception.AFBizException;
import org.openoa.mapper.PersonMapper;
import org.openoa.mapper.StudentMapper;
import org.openoa.base.util.ThreadLocalContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Map;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-15 16:50
 * @Param
 * @return
 * @Version 1.0
 */
@Slf4j
@Service
public class PersonServiceImpl extends ServiceImpl<PersonMapper, Person> {
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private DataSourceTransactionManager transactionManager;
    @Autowired
    private Map<String, DataSource> dataSourceMap;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private AfUserService userService;


    //@DS(DB_NAME_2)
    @Transactional
    public void  opTranstionally(){
        Person person=new Person();
        person.setName("china2");
        person.setAge(100);
        person.setYear("2022");
        personMapper.deleteById(11);
        //TransactionStatus transactionStatus = TransactionAspectSupport.currentTransactionStatus();
        if(3==3){
            throw new AFBizException("mock an exception throw out");
        }
        personMapper.insert(person);
    }

    @Transactional
    public void userOpTransactional(){
        User user=new User();
        user.setId(1L);
        user.setUserName("东西");

        if(3==3){
            throw new AFBizException("mock an exception throw out");
        }
    }
    @Transactional
    public void studentOPTrans(){
        Student student=new Student();
        student.setName("515测试");
        student.setAge(32);
        student.setBirthDay(new Date());
        studentMapper.deleteById(20);

        if(3==3){
            throw new AFBizException("制造产生异常情况");
        }
        studentMapper.insert(student);
    }

    @Async
    public void  asyncDemo(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        log.info("you request me from async context"+ ThreadLocalContainer.get("hello"));
        ThreadLocalContainer.set("hello","我很开心");
    }
}
