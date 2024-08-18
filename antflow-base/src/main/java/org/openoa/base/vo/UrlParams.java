package org.openoa.base.vo;

import lombok.Data;

import java.io.Serializable;

@Data

public class UrlParams implements Serializable {

    private String businessId;

    private String code;

    private String nodeType;

    private String taskId;

    private String newsId;


}
