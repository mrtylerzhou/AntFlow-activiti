package org.openoa.engine.bpmnconf.constant.enus;

import lombok.Getter;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.constant.enums.NodeTypeEnum;

import java.util.ArrayList;
import java.util.List;

import static org.openoa.base.constant.enums.NodePropertyEnum.NODE_PROPERTY_PERSONNEL;
import static org.openoa.base.constant.enums.NodePropertyEnum.NODE_PROPERTY_ROLE;

public enum BpmnNodeAdpConfEnum {
    ADP_CONF_NODE_PROPERTY_LOOP(NodePropertyEnum.NODE_PROPERTY_LOOP),
    ADP_CONF_NODE_PROPERTY_LEVEL(NodePropertyEnum.NODE_PROPERTY_LEVEL),
    ADP_CONF_NODE_PROPERTY_ROLE(NodePropertyEnum.NODE_PROPERTY_ROLE),
    ADP_CONF_NODE_PROPERTY_PERSONNEL(NodePropertyEnum.NODE_PROPERTY_PERSONNEL),
    ADP_CONF_NODE_TYPE_CONDITIONS(NodeTypeEnum.NODE_TYPE_CONDITIONS),
    ADP_CONF_NODE_TYPE_COPY(NodeTypeEnum.NODE_TYPE_COPY),
    ADP_CONF_NODE_TYPE_OUT_SIDE_CONDITIONS(NodeTypeEnum.NODE_TYPE_OUT_SIDE_CONDITIONS),
    ADP_CONF_NODE_PROPERTY_OUT_SIDE_ACCESS(NodePropertyEnum.NODE_PROPERTY_OUT_SIDE_ACCESS),
    ADP_CONF_NODE_PROPERTY_START_USER(NodePropertyEnum.NODE_PROPERTY_START_USER),
    ADP_CONF_NODE_PROPERTY_HRBP(NodePropertyEnum.NODE_PROPERTY_HRBP),
    ADP_CONF_NODE_PROPERTY_BUSINESSTABLE(NodePropertyEnum.NODE_PROPERTY_BUSINESSTABLE),
    ADP_CONF_NODE_PROPERTY_DIRECT_LEADER(NodePropertyEnum.NODE_PROPERTY_DIRECT_LEADER),
    ADP_CONF_NODE_PROPERTY_CUSTOMIZE(NodePropertyEnum.NODE_PROPERTY_CUSTOMIZE),
    ADP_CONF_NODE_PROPERTY_DEPARTMENT_LEADER(NodePropertyEnum.NODE_PROPERTY_DEPARTMENT_LEADER),
    ADP_CONF_NODE_PROPERTY_APPROVED_USERS(NodePropertyEnum.NODE_PROPERTY_APPROVED_USERS),
    ;

    @Getter
    private Enum<?> anEnum;


    BpmnNodeAdpConfEnum(Enum<?> anEnum) {
        this.anEnum = anEnum;
    }

    /**
     * get node adaptor conf by an enum
     *
     * @param anEnum
     * @return
     */
    public static BpmnNodeAdpConfEnum getBpmnNodeAdpConfEnumByEnum(Enum<?> anEnum) {
        for (BpmnNodeAdpConfEnum bpmnNodeAdpConfEnum : BpmnNodeAdpConfEnum.values()) {
            if (bpmnNodeAdpConfEnum.getAnEnum().equals(anEnum)) {
                return bpmnNodeAdpConfEnum;
            }
        }
        return null;
    }
    public static List<BpmnNodeAdpConfEnum> getBpmnNodeAdpConfWithPersonnels(){
        List<BpmnNodeAdpConfEnum> results=new ArrayList<>();
        for (BpmnNodeAdpConfEnum value : BpmnNodeAdpConfEnum.values()) {
            if(value.anEnum instanceof NodePropertyEnum){
                if(((NodePropertyEnum) value.anEnum).getHasPropertyTable()==1){
                    results.add(value);
                }
            }
        }
        return results;
    }
}