package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterDataVo {

    //表头对应字段
    private String titleColumn;

    //筛选对应字段
    private String filterColum;

    //页面筛选
    private List<PageFilterDataDto> pageFilterDatas;

}
