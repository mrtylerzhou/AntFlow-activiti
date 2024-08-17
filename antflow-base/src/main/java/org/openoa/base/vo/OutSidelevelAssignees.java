package org.openoa.base.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * approver list
 * @Author tylerzhou
 * @since 0.5
 */
@Getter
@Setter
public class OutSidelevelAssignees implements Serializable {
    /**
     * assignee id list
     */
    private List<String> assigneeIds;

    /**
     * assignee id name map
     */
    private Map<Integer, String> assigneeNameMap;
}
