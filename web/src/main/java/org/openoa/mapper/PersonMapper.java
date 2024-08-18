package org.openoa.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openoa.entity.Person;

import static org.openoa.base.constant.StringConstants.DB_NAME_2;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-15 16:34
 * @Param
 * @return
 * @Version 1.0
 */
@DS(DB_NAME_2)
public interface PersonMapper extends BaseMapper<Person> {
}
