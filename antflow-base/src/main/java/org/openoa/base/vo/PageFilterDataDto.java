package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageFilterDataDto {

    //页面筛选漏斗值
    private Serializable key;

    //页面筛选漏斗显示文本
    private Serializable value;

}
