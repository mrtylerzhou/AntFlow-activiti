package org.openoa.engine.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceEmployeeListDto implements Serializable {

    private Integer id;

    private String username;

    private String name;

    private String email;

    private String position;

    private String leaderName;

    private String mobile;

    private String joinTime;

    private String leftTime;

    private Integer isOnJob;

    /**
     * 头像
     */

    private String headImg;
    /**
     * 职级
     */
    private String jobLevel;
    /**
     * 企业微信员工userid
     */
    private String qywxUserId;
}
