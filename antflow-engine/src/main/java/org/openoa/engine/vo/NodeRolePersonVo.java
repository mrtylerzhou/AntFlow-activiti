package org.openoa.engine.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.List;

/**
 * @Author: jwz
 * @Date: 2024/9/24 15:58
 * @Description:
 * @Version: 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeRolePersonVo {

    private String roleId;

    private String roleName;

    private List<BaseIdTranStruVo> userList;
}
