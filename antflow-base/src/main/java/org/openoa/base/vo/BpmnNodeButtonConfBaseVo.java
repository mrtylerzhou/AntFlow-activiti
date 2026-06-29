package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname BpmnNodeButtonConfBaseVo
 * @Description button conf base
 * @Date 2021-10-31 10:13
 * @Created by AntOffice
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnNodeButtonConfBaseVo implements Serializable {
    /**
     * start page button list
     */
    private List<Integer> startPage=new ArrayList<>();

    /**
     * approval page button list
     */
    private List<Integer> approvalPage = new ArrayList<>();

    /**
     * view page button list
     */
    private List<Integer> viewPage = new ArrayList<>();
}
