package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openoa.base.entity.BpmProcessAppApplication;
import org.openoa.base.entity.BpmProcessApplicationType;
import org.openoa.base.entity.BpmProcessCategory;
import org.openoa.base.exception.AFBizException;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessAppApplicationServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessCategoryServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessApplicationTypeBizService;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.openoa.engine.vo.BpmProcessApplicationTypeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BpmProcessApplicationTypeBizServiceImpl implements BpmProcessApplicationTypeBizService {
    @Autowired
    private BpmProcessCategoryServiceImpl bpmProcessCategoryService;
    @Autowired
    @Lazy
    private BpmProcessAppApplicationServiceImpl processAppApplicationService;

    /**
     * move left
     *
     * @return
     */
    @Override
    public boolean moveUp(Long id) {


        BpmProcessApplicationType applicationType = this.getMapper().selectById(id);
        if (applicationType==null) {
            throw new AFBizException("无此条记录");
        }

        BpmProcessAppApplication application = processAppApplicationService.getBaseMapper().selectById(applicationType.getApplicationId());
        Integer sort = applicationType.getSort();
        if (sort == 1) {
            throw new AFBizException("当前记录已到顶");
        }


        if (applicationType.getCategoryId() == 1 || applicationType.getCategoryId() == 2) {
            BpmProcessApplicationType type = null;
            BpmProcessApplicationType bpmProcessApplicationType = getMapper().selectOne(
                    new QueryWrapper<BpmProcessApplicationType>()
                            .eq("sort", sort - 1)
                            .eq("is_del",0)
                            .eq("category_id", applicationType.getCategoryId()));
            if(bpmProcessApplicationType==null){
                throw new AFBizException("没有此记录");
            }
            type=bpmProcessApplicationType;


            applicationType.setSort(sort - 1);
            getMapper().updateById(applicationType);
            type.setSort(sort);
            getMapper().updateById(type);
            return true;
        } else {

            if (application.getIsSon() == 0) {

                List<BpmProcessAppApplicationVo> vos = processAppApplicationService.getBaseMapper()
                        .listIcon(BpmProcessAppApplicationVo
                                .builder()
                                .id(applicationType.getCategoryId().intValue())
                                .isSon(0)
                                .build())
                        .stream()
                        .filter(o -> o.getSort().compareTo(sort) < 0)
                        .sorted((a, b) -> a.getSort().compareTo(b.getSort()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(vos)) {
                    throw new AFBizException("当前记录已到顶");
                }
                //交换sort
                BpmProcessAppApplicationVo vo = vos.get(vos.size() - 1);
                applicationType.setSort(vo.getSort());
                getMapper().updateById(applicationType);
                getMapper().updateById(BpmProcessApplicationType
                        .builder()
                        .id(vo.getId().longValue())
                        .sort(sort)
                        .build());
                return true;
            }

            if (application.getIsSon() == 1) {

                List<BpmProcessAppApplicationVo> vos = processAppApplicationService.getBaseMapper()
                        .listIcon(BpmProcessAppApplicationVo
                                .builder()
                                .id(applicationType.getCategoryId().intValue())
                                .parentId(application.getParentId())
                                .isSon(1)
                                .build())
                        .stream()
                        .filter(o -> o.getSort().compareTo(sort) < 0)
                        .sorted((a, b) -> a.getSort().compareTo(b.getSort()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(vos)) {
                    throw new AFBizException("当前记录已到顶");
                }

                BpmProcessAppApplicationVo vo = vos.get(vos.size() - 1);
                applicationType.setSort(vo.getSort());
                getMapper().updateById(applicationType);
                getMapper().updateById(BpmProcessApplicationType
                        .builder()
                        .id(vo.getId().longValue())
                        .sort(sort)
                        .build());
                return true;
            }
        }
        throw new AFBizException("无满足条件的排序方式！");

    }
    @Override
    public boolean asCommonlyUsed(Long processTypeId, Integer id, boolean isCancel, Integer type) {
        //BpmProcessApplicationType applicationType = this.selectById(processTypeId);
        BpmProcessCategory processCategory = bpmProcessCategoryService.getBaseMapper().selectById(processTypeId);
        BpmProcessApplicationType processApplicationType = this.getMapper().selectById(id);
        if (processCategory==null) {
            throw  new AFBizException("无此条记录");
        }
        if (isCancel) {
            Long aLong = this.getService().addProcessApplicationType(BpmProcessApplicationTypeVo.builder()
                    .categoryId(processTypeId)
                    .state(1)
                    .historyId(id.longValue())
                    .commonUseState(1)
                    .visbleState(processApplicationType.getVisbleState())
                    .applicationId(processApplicationType.getApplicationId())
                    .build());
            processApplicationType.setState(1);
            processApplicationType.setHistoryId(aLong);
            this.getService().updateById(processApplicationType);
        } else {
            Integer sort = 0;
            Long countTotal = 0L;
            List<BpmProcessApplicationType> list = null;
            if (processApplicationType.getHistoryId()!=null && processApplicationType.getCommonUseState()!=null) {
                QueryWrapper<BpmProcessApplicationType> wrapper = new QueryWrapper<BpmProcessApplicationType>().eq("is_del", 0).eq("category_id", processTypeId);
                countTotal = getMapper().selectCount(wrapper);
                processApplicationType.setIsDel(1);
                sort = processApplicationType.getSort();
                processApplicationType.setSort(0);

                this.getService().updateById(processApplicationType);
                BpmProcessApplicationType applicationType = this.getMapper().selectById(processApplicationType.getHistoryId());
                applicationType.setState(0);
                list = getMapper().selectList(wrapper.gt("sort", sort));

                this.getService().updateById(applicationType);
            } else {
                processApplicationType.setState(0);
                this.getService().updateById(processApplicationType);
                List<BpmProcessApplicationType> applicationTypeList = this.getService().getProcessApplicationType(BpmProcessApplicationTypeVo.builder()
                        .applicationId(processApplicationType.getApplicationId())
                        .categoryId(processTypeId)
                        .build());
                if (!CollectionUtils.isEmpty(applicationTypeList)) {
                    BpmProcessApplicationType applicationType = applicationTypeList.get(0);
                    sort = applicationType.getSort();
                    applicationType.setState(0);
                    applicationType.setSort(0);
                    applicationType.setIsDel(1);
                    QueryWrapper<BpmProcessApplicationType> wrapper = new QueryWrapper<BpmProcessApplicationType>().eq("is_del", 0).eq("category_id", processTypeId);
                    countTotal = getMapper().selectCount(wrapper);
                    list = getMapper().selectList(wrapper.gt("sort", sort));
                    this.getService().updateById(applicationType);
                }
            }
            this.getService().sortProcessApplicationType(sort, countTotal.intValue(), list);
        }
        return true;
    }
    /**
     * icon operation
     *
     * @return
     */
    @Override
    public boolean iconOperation(BpmProcessApplicationTypeVo vo) {
        switch (vo.getType()) {
            //move left
            case 5:
                this.moveUp(vo.getId());
                break;
            //hide
            case 2:
                this.getService().hiddenApplication(vo.getId(), true, vo.getCommonUseId());
                break;
            //visible
            case 1:
                this.getService().hiddenApplication(vo.getId(), false, vo.getCommonUseId());
                break;
            //set frequently used
            case 3:
                this.asCommonlyUsed(vo.getCommonUseId(), vo.getId().intValue(), true, vo.getType());
                break;
            //cancel frequently used
            case 4:
                this.asCommonlyUsed(vo.getCommonUseId(), vo.getId().intValue(), false, vo.getType());
                break;
        }
        return true;
    }
}
