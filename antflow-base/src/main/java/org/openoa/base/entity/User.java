package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * this table is for demo use only,you can change it to your own employee info
 */
@TableName("t_user")
@Getter
@Setter
public class User {
    private Integer id;
    private String userName;
}
