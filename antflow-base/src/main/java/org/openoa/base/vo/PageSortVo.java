package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.constant.enums.SortTypeEnum;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageSortVo {

    //sort field
    private String orderField;

    //sort type
    private SortTypeEnum sortTypeEnum;
}
