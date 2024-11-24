package org.openoa.base.util;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.vo.FilterDataVo;
import org.openoa.base.vo.PageFilterDataDto;
import org.springframework.util.CollectionUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class FilterDataUtils {

    /**
     * 格式化参数
     *
     * @param params
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T formatParams(String params, Class<T> cls) {
        return JSON.parseObject(params, cls);
    }

    /**
     * 构建漏斗Map
     *
     * @param filterDataList
     * @param filterColumnMap
     * @return
     */
    public static List<FilterDataVo> buildFilterMap(List<?> filterDataList, Map<String, String> filterColumnMap) {

        if (CollectionUtils.isEmpty(filterDataList) && CollectionUtils.isEmpty(filterColumnMap)) {
            return Collections.EMPTY_LIST;
        }

        List<FilterDataVo> filterDataVos = new ArrayList<>();

        filterColumnMap.forEach((key, val) -> {
            filterDataVos.add(FilterDataVo
                    .builder()
                    .filterColum(key)
                    .titleColumn(val)
                    .pageFilterDatas(getPageFilterDataDtos(key, val, filterDataList))
                    .build());
        });

        return filterDataVos;

    }

    /**
     * 反射组装对象列表
     *
     * @param keyColumn
     * @param valueColumn
     * @param filterDataList
     * @return
     */
    public static List<PageFilterDataDto> getPageFilterDataDtos(String keyColumn, String valueColumn, List<?> filterDataList) {

        List<PageFilterDataDto> pageFilterDataDtos = new ArrayList<>();

        //遍历结果集反射组装对象列表
        filterDataList.forEach(fdo -> {
            try {

                String key = Optional.ofNullable(PropertyUtils.getProperty(fdo, keyColumn)).orElse(StringUtils.EMPTY).toString();
                String value = Optional.ofNullable(PropertyUtils.getProperty(fdo, valueColumn)).orElse(StringUtils.EMPTY).toString();
                pageFilterDataDtos.add(PageFilterDataDto
                        .builder()
                        .key(key)
                        .value(value)
                        .build());
            } catch (IllegalAccessException e) {
                log.error("FilterDataUtils-构建漏斗Map失败：", e);
            } catch (InvocationTargetException e) {
                log.error("FilterDataUtils-构建漏斗Map失败：", e);
            } catch (NoSuchMethodException e) {
                log.error("FilterDataUtils-构建漏斗Map失败：", e);
            }

        });

        //去除重复并返回
        return pageFilterDataDtos.stream().distinct().collect(Collectors.toList());
    }

}
