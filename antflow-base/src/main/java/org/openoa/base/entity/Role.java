package org.openoa.base.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author TylerZhou
 * @Date 2024/7/17 22:35
 * @Version 0.5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    Long id;
    String roleName;
}
