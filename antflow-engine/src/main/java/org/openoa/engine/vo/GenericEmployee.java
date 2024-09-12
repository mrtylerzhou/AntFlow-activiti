package org.openoa.engine.vo;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * may be your system do not have all these concepts,but feel free
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericEmployee implements Serializable {

    // userId
    private String userId;


    // username
    @Builder.Default
    private String username="";

    // real name
    @Builder.Default
    private String givenName="";

    //job num
    private String jobNum;

    //job num name
    private String jobName;

    //job level
    private String jobLevelName;

    //photopath
    private String photoPath;

    //head image path
    private String headImg;

    // email
    private String mail;

    // mobile
    private String mobile;

    // is sys admin
    private Boolean isMaster;


    //the company he or she serves
    private Long companyId;



    // direct leader
    private GenericEmployee directLeader;

    // his or her permissions
    @Builder.Default
    private Set<String> permissions= Sets.newHashSet("3060101");

    //reporting path
    private String path;


}
