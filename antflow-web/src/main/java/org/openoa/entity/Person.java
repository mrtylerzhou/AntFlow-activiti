package org.openoa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-15 16:31
 * @Param
 * @return
 * @Version 1.0
 */
@Getter
@Setter
@TableName("person")
public class Person {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer age;
    private String year;
}
