package org.openoa.engine.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@Data
@JsonSerialize(include = NON_NULL)
public class DataAccessVo {

    private String columns;

    private List<Integer> departmentIds;

    private String joinColumns;
}
