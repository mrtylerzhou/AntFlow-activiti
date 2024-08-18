package org.openoa.base.vo;

import lombok.Data;
import org.openoa.base.dto.PageDto;

import java.util.List;

/**
 * @since 0.0.1
 */
@Data
public class BaseVo extends PageDto {

    // a list of filter conditions
    private List<PageFilterDto> pageFilters;

}