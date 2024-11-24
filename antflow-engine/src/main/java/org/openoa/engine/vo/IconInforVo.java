package org.openoa.engine.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include=NON_NULL)
public class IconInforVo implements Serializable {
    private ProcessTypeInfoVo commonFunction;
    private List<ProcessTypeInfoVo> applicationList;
    private ProcessTypeInfoVo sonApplicationList;
}
