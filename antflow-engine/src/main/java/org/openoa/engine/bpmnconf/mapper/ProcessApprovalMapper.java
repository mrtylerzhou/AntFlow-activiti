package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.vo.TaskMgmtVO;

import java.util.List;

/**
 *@Author JimuOffice
 * @Description task mgmt mapper
 * @Param
 * @return
 * @Version 0.5
 */
@Mapper
public interface ProcessApprovalMapper extends BaseMapper<TaskMgmtVO> {
    /**
     * view my newly create process
     *
     * @param page
     * @param taskMgmtVO
     * @return
     */
    List<TaskMgmtVO> viewPcpNewlyBuildList(Page page, @Param("taskMgmtVO") TaskMgmtVO taskMgmtVO);

    /**
     * query already done tasks on the pc
     *
     * @param page
     * @param taskMgmtVO
     * @return
     */
    List<TaskMgmtVO> viewPcAlreadyDoneList(Page page, @Param("taskMgmtVO") TaskMgmtVO taskMgmtVO);

    /**
     * query to do tasks on the pc
     *
     * @param page
     * @param taskMgmtVO
     * @return
     */
    List<TaskMgmtVO> viewPcToDoList(Page page, @Param("taskMgmtVO") TaskMgmtVO taskMgmtVO);

    /**
     * get all process
     *
     * @param page
     * @param taskMgmtVO
     * @return
     */
    List<TaskMgmtVO> allProcessList(Page page, @Param("taskMgmtVO") TaskMgmtVO taskMgmtVO);

    /**
     * query whether current node is operable
     *
     * @param taskMgmtVO
     * @return
     */
    Integer isOperational(@Param("taskMgmtVO") TaskMgmtVO taskMgmtVO);

    /**
     * get today's alread done count
     *
     * @param createUserId
     * @return
     */
    Integer doneTodayProcess(@Param("createUserId") String createUserId);

    /**
     * query specified users newly created process
     *
     * @param createUserId
     * @return
     */
    Integer doneCreateProcess(@Param("createUserId") String createUserId);

    /**
     * query forwarded tasks
     * @param page
     * @param vo
     * @return
     */
    List<TaskMgmtVO> viewPcForwardList(Page<TaskMgmtVO> page,@Param("taskMgmtVO") TaskMgmtVO vo);

    List<TaskMgmtVO> viewPcProcessList(Page page, TaskMgmtVO taskMgmtVO);
}
