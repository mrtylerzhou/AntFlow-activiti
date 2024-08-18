package org.openoa.engine.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include=NON_NULL)
public class AppVersionVo implements Serializable{

    //latest version id
    private Long id;

    //current version
    private String curVersion;

    //latest version
    private String version;

    //is latest 0 for no and 1 for yes
    private Integer isLatest;

    //is force update 0 for no and 1 for yes
    private Integer isForce;

    //latest version download url
    private String downloadUrl;

    //latest version description
    private String description;
}