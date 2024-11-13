package org.openoa.base.util;


import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.adp.FilterDataAdaptor;
import org.openoa.base.constant.enums.FilterDataEnum;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.constant.enums.SortTypeEnum;
import org.openoa.base.vo.PageSortVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.dto.PageDto;
import org.openoa.base.exception.JiMuBizException;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

//todo redesign
@Slf4j
public class PageUtils {

    public static PageDto getPageDto(Page page) {
        PageDto pageDto = new PageDto();
        pageDto.setPage((int) page.getCurrent());
        pageDto.setPageSize((int) page.getSize());
        pageDto.setTotalCount((int) page.getTotal());
        pageDto.setPageCount((int) page.getPages());
        return pageDto;
    }

    /**
     * build a paging object
     *
     * @param pageDto
     * @param <T>
     * @return
     */
    public static <T> Page<T> getPageByPageDto(PageDto pageDto) {
        return buildPage(pageDto);
    }

    /**
     *
     * @param pageDto
     * @param pageSortVos
     * @param <T>
     * @return
     */
    public static <T> Page<T> getPageByPageDto(PageDto pageDto, List<PageSortVo> pageSortVos) {
        Page<T> page = buildPage(pageDto);

        //if the sorting object is empty then return the paging object
        if (ObjectUtils.isEmpty(pageSortVos)) {
            return page;
        }
        //set the ordering field
        setOrderByField(page, pageDto, Optional.ofNullable(pageSortVos).orElse(Collections.EMPTY_LIST));
        return page;
    }

    /**
     * use a linked map to order the page object
     *
     * @param pageDto
     * @param orderFieldMap
     * @param <T>
     * @return
     */
    public static <T> Page<T> getPageByPageDto(PageDto pageDto, LinkedHashMap<String, SortTypeEnum> orderFieldMap) {
        Page<T> page = buildPage(pageDto);

        //转换链表为排序对象列表
        List<PageSortVo> pageSortVos = new ArrayList<>();
        if (!ObjectUtils.isEmpty(orderFieldMap)) {
            for (Map.Entry<String, SortTypeEnum> entry : orderFieldMap.entrySet()) {
                if (Strings.isNullOrEmpty(pageDto.getOrderColumn()) && pageDto.getOrderColumn().equals(entry.getKey())) {
                    continue;
                }
                pageSortVos.add(PageSortVo.builder().orderField(entry.getKey()).sortTypeEnum(entry.getValue()).build());
            }
        }
        //set ordeing field
        setOrderByField(page, pageDto, pageSortVos);
        return page;
    }

    /**
     * get the paging object
     *
     * @param page
     * @return
     */
    public static <T, R> ResultAndPage<R> getResultAndPage(Page<T> page, Function<T, R> mapper) {
        return new ResultAndPage<R>(page.getRecords().stream().map(mapper).collect(Collectors.toList()), getPageDto(page));
    }

    /**
     * get the paging object,another overload
     *
     * @param page
     * @return
     */
    public static <T> ResultAndPage<T> getResultAndPage(Page page) {
        return new ResultAndPage<>(page.getRecords(), getPageDto(page));
    }

    public static <T> ResultAndPage<T> getResultAndPage(List<T> data, PageDto pageDto) {
        return new ResultAndPage<>(data, pageDto);
    }

    /**
     * get the paging object,another overload
     *
     * @param page
     * @param statistics
     * @param <T>
     * @return
     */
    public static <T> ResultAndPage<T> getResultAndPage(Page page, Map<String, Object> statistics) {
        return new ResultAndPage<>(page.getRecords(), getPageDto(page), statistics);
    }

    /**
     * get the paging object,another overload
     *
     * @param page
     * @param statistics
     * @param sortColumnMap
     * @param <T>
     * @return
     */
    public static <T> ResultAndPage<T> getResultAndPage(Page page, Map<String, Object> statistics, Map<String, String> sortColumnMap) {
        return new ResultAndPage<>(page.getRecords(), getPageDto(page), statistics, sortColumnMap);
    }

    /**
     * get the paging object,another overload
     *
     * @param page
     * @param filterDataEnum
     * @param <T>
     * @return
     */
    public static <T> ResultAndPage<T> getResultAndPage(Page page, FilterDataEnum filterDataEnum) {

        FilterDataAdaptor filterDataAdaptor = SpringBeanUtils.getBean(filterDataEnum.getFilterDataService());

        if (!ObjectUtils.isEmpty(filterDataAdaptor)) {
            Map<String, String> sortColumnMap = filterDataAdaptor.filterColumnMap();

            return new ResultAndPage<>(page.getRecords(), getPageDto(page), null, sortColumnMap);
        }

        return new ResultAndPage<>(page.getRecords(), getPageDto(page), null);
    }

    /**
     * get the paging object,another overload
     *
     * @param page
     * @param statistics
     * @param filterDataEnum
     * @param <T>
     * @return
     */
    public static <T> ResultAndPage<T> getResultAndPage(Page page, Map<String, Object> statistics, FilterDataEnum filterDataEnum) {

        FilterDataAdaptor filterDataAdaptor = SpringBeanUtils.getBean(filterDataEnum.getFilterDataService());

        if (!ObjectUtils.isEmpty(filterDataAdaptor)) {
            Map<String, String> sortColumnMap = filterDataAdaptor.filterColumnMap();

            return new ResultAndPage<>(page.getRecords(), getPageDto(page), statistics, sortColumnMap);
        }

        return new ResultAndPage<>(page.getRecords(), getPageDto(page), statistics);
    }

    /**
     * get paging vo by a passing in object
     *
     * @param obj
     * @return
     */
    public static PageDto getPageDtoByVo(Object obj) {

        String objJson = JSON.toJSONString(obj);

        return JSON.parseObject(objJson, PageDto.class);
    }

    /**
     * build a page object
     *
     * @param pageDto
     * @param <T>
     * @return
     */
    private static <T> Page<T> buildPage(PageDto pageDto) {
        Page<T> page = new Page<>(pageDto.getPage() < 1 ? 1 : pageDto.getPage(), (pageDto.getPageSize() > 200 ? 200 : pageDto.getPageSize()) < 1 ? 1 : pageDto.getPageSize());
        if (pageDto.getPageSize() == null) {
            pageDto.setPageSize(10);
        }
        if (pageDto.getPage() == null) {
            pageDto.setPage(1);
        }
        return page;
    }

    /**
     * set ordering field
     *
     * @param page
     * @param pageSortVos
     */
    private static void setOrderByField(Page page, PageDto pageDto, List<PageSortVo> pageSortVos) {

        List<PageSortVo> pageSortVoList = new ArrayList<>();
        pageSortVoList.addAll(pageSortVos);
        if (!ObjectUtils.isEmpty(pageDto.getOrderColumn()) && !ObjectUtils.isEmpty(pageDto.getOrderType())) {
            pageSortVoList.add(PageSortVo
                    .builder()
                    .orderField(pageDto.getOrderColumn())
                    .sortTypeEnum(SortTypeEnum.getSortTypeEnumByCode(pageDto.getOrderType()))
                    .build());
        }


        if (ObjectUtils.isEmpty(pageSortVoList)) {
            return;
        }

        //build the ordering fields
        List<String> orderFields = new ArrayList<>();
        for (int i = 0; i < pageSortVoList.size(); i++) {
            PageSortVo vo = pageSortVoList.get(i);
            if (i < pageSortVoList.size() - 1) {
                orderFields.add(StringUtils.join(vo.getOrderField(), vo.getSortTypeEnum().getMark()));
            } else {
                orderFields.add(vo.getOrderField());
                //todo assending or desending order?
            }
        }


        //todo
    }


    public static Integer getPageCount(Integer total, Integer size) {
        return total % size == 0 ? total / size : total / size + 1;
    }

    /**
     * 根据分页信息获得子列表
     *
     * @param page
     * @param pageSize
     * @param pageTotalCount
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T getPageList(Integer page, Integer pageSize, Integer pageTotalCount, List<?> list) throws JiMuBizException {
        if (page > pageTotalCount) {
            throw new JiMuBizException("999", "wrong page count info");
        }
        if (!page.equals(pageTotalCount)) {
            return (T) list.subList((page - 1) * pageSize, page * pageSize);
        } else {
            return (T) list.subList((page - 1) * pageSize, list.size());
        }
    }

}
