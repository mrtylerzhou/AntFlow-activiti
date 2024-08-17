package org.openoa.base.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic Employee,we try to make it as simple as possible,but each company has its own business logic,it may not fit all,you can customize it
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    private Long id;
    private String username;
    /**
     * direct leader id
     */
    private Long leaderId;
    /**
     * emaill,for notification
     */
    private String email;
    /**
     * moible
     */
    private String mobile;
    private Integer isDel;
    /**
     * hrbp id
     */
    private Integer hrbpId;
    /**
     * avatar
     */
    private String headImg;
    /**
     * is show mobile,you should respect user's privacy,if he or she do not want to show his or her mobile,you should set it false
     */
    private Boolean mobileIsShow;
    private String path;
    private Integer departmentId;

}
