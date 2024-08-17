package org.openoa.base.adp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SecurityAccountDeviceFilterDataAdp extends FilterDataAdaptor {


    @Override
    public List getFilterData(String params) {
        //todo
        return null;
    }

    @Override
    public Map<String, String> filterColumnMap() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "name");
        map.put("username", "username");
        map.put("mobile", "mobile");
        map.put("deptPath", "deptName");
        map.put("contractBody", "compName");
        map.put("deviceType", "deviceTypeName");
        map.put("deviceId", "deviceId");
        map.put("status", "statusName");
        return map;
    }

}
