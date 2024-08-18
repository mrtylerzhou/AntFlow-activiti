package org.openoa.base.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageDto implements Serializable {
    /**
     * current page
     */
    private Integer page = 1;
    /**
     * how many records in a page
     */
    private Integer pageSize = 10;
    /**
     * total records count
     */
    private Integer totalCount;
    /**
     * total page count
     */
    private Integer pageCount;
    /**
     * start index
     */
    private Integer startIndex = (page - 1) * pageSize;

    /**
     * columns for sorting
     */
    private String orderColumn;

    /**
     * sortType 1 ascending, 2 descending
     */
    private Integer orderType;

    public Integer getStartIndex() {
        return (page - 1) * pageSize;
    }

    public PageDto() {

    }

    public PageDto(Integer page, Integer pageSize, String orderColumn, Integer orderType) {
        this.page = page;
        this.pageSize = pageSize;
        this.orderColumn = orderColumn;
        this.orderType = orderType;
    }

    public static PageDto first() {
        PageDto pageDto = new PageDto();
        pageDto.setPage(1);
        pageDto.setPageSize(10);
        return pageDto;
    }

    public static PageDto buildCountedPage(PageDto pageDto, Integer totalCount) {
        PageDto pageDto1 = new PageDto();
        pageDto1.setPage(pageDto.getPage());
        pageDto1.setPageSize(pageDto.getPageSize());
        pageDto1.setTotalCount(totalCount);
        pageDto1.setPageCount((totalCount - 1) / pageDto1.getPageSize() + 1);
        return pageDto1;
    }
}