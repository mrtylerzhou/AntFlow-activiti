package org.openoa.engine.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BaseVo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;
/**
 * sys version vo
 * @since 0.5
 */
@Data
@EqualsAndHashCode(callSuper=false)
@JsonSerialize(include=NON_NULL)
public class SysVersionVo extends BaseVo implements Serializable {

    private Long id;
    private Date createTime;
    private Date updateTime;
    /**
     * 0 for normal,1 for delete
    */
    private Integer isDel;
    /**
    * version
    */
    private String version;
    /**
     * version desc
     */
    private String description;
    /**
    * index
    */
    private Integer index;
    /**
     * is force update 0:no,1:yes
    */
    private Integer isForce;
    /**
     * is hide 0:no,1:yes
     */
    private Integer isHide;
    /**
     * android download url
     */
    private String androidUrl;
    /**
     * ios download url
     */
    private String iosUrl;
    /**
    * create user
    */
    private String createUser;
    /**
    * update user
    */
    private String updateUser;
    /**
     * APP download code
     */
    private String downloadCode;
    /**
     * related data
     */
    private List<BaseIdTranStruVo> data;
    /**
     * related app
     */
    private List<BaseIdTranStruVo> application;
    /**
     * related data ids
     */
    private List<Long> dataIds;
    /**
     * related app ids
     */
    private List<Long> appIds;
    /**
     * effective time
     */
    private String effectiveTime;

    /**

     * related quick entry ids
     */
    private List<Long> quickEntryIds;

    /**
     * related quick entry list
     */
    private List<BaseIdTranStruVo> quickEntryList;

}
