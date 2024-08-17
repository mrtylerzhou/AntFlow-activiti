package org.openoa.base.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * outside level node
 * @Author tylerzhou
 * @since 0.5
 */
@Getter
@Setter
public class OutSideLevelNodeVo implements Serializable {
    /**
     * node mark
     */
    private String nodeMark;

    /**
     * node name
     */
    private String nodeName;
    List<OutSidelevelAssignees>assignees=new ArrayList<>();

}
