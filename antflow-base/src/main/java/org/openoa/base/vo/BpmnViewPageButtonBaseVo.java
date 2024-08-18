package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname BpmnViewPageButtonBaseVo
 * @Description view page button
 * @Date 2021-10-31 10:04
 * @Created by AntOffice
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnViewPageButtonBaseVo {
    /**
     * view page start user
     */
    private List<Integer> viewPageStart;

    /**
     * 查view page other
     */
    private List<Integer> viewPageOther;
}
