package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrevEmployeeInfo implements Serializable {

    private static final long serialVersionUID = 4309495676966582583L;

    private Integer employeeId;

    private String employeeName;

    private Long jobLevelId;

    private String jobLevel;

    private String departName;

    private Integer departId;
}
