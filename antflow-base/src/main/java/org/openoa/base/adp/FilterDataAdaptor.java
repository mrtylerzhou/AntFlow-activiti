package org.openoa.base.adp;

import org.openoa.base.vo.BaseVo;

import java.util.List;
import java.util.Map;

public abstract class FilterDataAdaptor<obj extends BaseVo> {

    /**
     * query and return funnel data
     *
     * @param params
     * @return
     */
    public abstract List<obj> getFilterData(String params);

    /**
     * filterColumnMap
     *
     * @return
     */
    public abstract Map<String, String> filterColumnMap();

}
