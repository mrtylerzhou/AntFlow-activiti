package org.openoa.base.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户可用性(办公状态)查询结果
 * 由 AfUserService.checkEmployeeEffective 返回,默认实现为骨架空实现(恒可用),真实数据由使用方对接
 * 员工表/工作日历表等自行实现。
 *
 * 时间判断规则(框架门禁侧使用):
 * 1. available = true(办公状态可用)→ 不转办
 * 2. available = false → 按不可用时间窗口判断:
 *    - 无开始无结束 → 永久不可用,直接生效
 *    - 只有开始时间,开始早于当前 → 生效
 *    - 只有结束时间,结束晚于当前 → 生效
 *    - 同时有开始和结束,当前在区间内 → 生效
 */
@Data
public class UserAvailableVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否可用(办公状态):true=办公状态可用,false=不在办公状态
     */
    private Boolean available;

    /**
     * 不可用开始时间
     */
    private Date unavailableBeginTime;

    /**
     * 不可用结束时间
     */
    private Date unavailableEndTime;

    /**
     * 转办目标人:不可用且需要转办时返回(id+name)
     */
    private BaseIdTranStruVo delegateUser;
}
