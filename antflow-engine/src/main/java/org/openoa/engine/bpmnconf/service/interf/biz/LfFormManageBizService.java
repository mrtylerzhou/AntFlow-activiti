package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.dto.PageDto;
import org.openoa.base.vo.LfFormManageVo;
import org.openoa.base.vo.ResultAndPage;

import java.util.List;

/**
 * 独立表单管理业务接口
 */
public interface LfFormManageBizService {

    /**
     * 分页查询独立表单（家族分组，每族一行生效版本）
     */
    ResultAndPage<LfFormManageVo> listPage(PageDto pageDto, LfFormManageVo vo);

    /**
     * 按 id 查询表单版本（编辑回显 / 审批按 id 取 formdata）
     */
    LfFormManageVo getById(Long id);

    /**
     * 保存表单：无 formCode => 新建家族+首版本(默认生效)；有 formCode => 新建版本(默认不生效)
     *
     * @return 新版本的 id
     */
    Long save(LfFormManageVo vo);

    /**
     * 生效指定版本：同 formCode 的其他生效版本自动置为非生效（互斥）。
     */
    void effective(Long id);

    /**
     * 软删除单个版本。被生效流程引用时拒绝。
     * 删生效版本后族进入"无生效版本"状态（不自动提升）。
     */
    void delete(Long id);

    /**
     * 查询某家族所有版本（历史版本查看）
     */
    List<LfFormManageVo> listHistory(String formCode);

    /**
     * 列出所有生效独立表单（流程设计多选下拉框）
     */
    List<LfFormManageVo> listEffectiveForSelect();
}
