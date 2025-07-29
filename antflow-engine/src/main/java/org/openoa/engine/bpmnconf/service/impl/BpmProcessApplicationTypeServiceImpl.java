package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.engine.bpmnconf.confentity.BpmProcessAppApplication;
import org.openoa.engine.bpmnconf.confentity.BpmProcessApplicationType;
import org.openoa.engine.bpmnconf.confentity.BpmProcessCategory;
import org.openoa.engine.bpmnconf.mapper.BpmProcessAppApplicationMapper;
import org.openoa.engine.bpmnconf.mapper.BpmProcessApplicationTypeMapper;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.openoa.engine.vo.BpmProcessApplicationTypeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



/**
 *  process application type curd service
 */
@Service
public class BpmProcessApplicationTypeServiceImpl extends ServiceImpl<BpmProcessApplicationTypeMapper, BpmProcessApplicationType> {

    @Autowired
    private BpmProcessApplicationTypeMapper bpmProcessApplicationTypeMapper;
    @Autowired
    private BpmProcessCategoryServiceImpl bpmProcessCategoryService;
    @Autowired
    @Lazy
    private BpmProcessAppApplicationServiceImpl processAppApplicationService;
    @Autowired
    private BpmProcessAppApplicationMapper bpmProcessAppApplicationMapper;

    public List<BpmProcessApplicationType> applicationTypes(BpmProcessApplicationTypeVo vo) {
        QueryWrapper<BpmProcessApplicationType> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del", 0);
        if (vo.getApplicationId()!=null) {
            wrapper.eq("application_id", vo.getApplicationId());
        }
        if (vo.getCategoryId()==null) {
            wrapper.eq("category_id", vo.getCategoryId());
        }
        return bpmProcessApplicationTypeMapper.selectList(wrapper);
    }

    /**
     * delete application that under a category
     */
    public boolean deletProcessApplicationType(BpmProcessApplicationTypeVo vo) {
        List<BpmProcessApplicationType> bpmProcessApplicationTypes = this.applicationTypes(vo);
        if (!CollectionUtils.isEmpty(bpmProcessApplicationTypes)) {
            for (BpmProcessApplicationType type : bpmProcessApplicationTypes) {
                BpmProcessApplicationType applicationType = this.getBaseMapper().selectById(type.getHistoryId());
                if (applicationType!=null) {
                    Integer sort = applicationType.getSort();
                    applicationType.setIsDel(0);
                    applicationType.setSort(0);
                    this.getBaseMapper().deleteById(applicationType);
                    this.sortProcessApplicationType(sort, applicationType.getCategoryId());
                }
                this.getBaseMapper().deleteById(type);
            }
        }
        return true;
    }

    /**
     * sorting process
     *
     * @param sort
     * @param processTypeId
     * @return
     */
    public boolean sortProcessApplicationType(Integer sort, Long processTypeId) {
        QueryWrapper<BpmProcessApplicationType> wrapper = new QueryWrapper<BpmProcessApplicationType>().eq("is_del", 0).eq("category_id", processTypeId);
        Long countTotal = bpmProcessApplicationTypeMapper.selectCount(wrapper);
        List<BpmProcessApplicationType> list = bpmProcessApplicationTypeMapper.selectList(wrapper.gt("sort", sort));
        //修改当前常用功能排序
        if (sort < countTotal) {
            int countChanged = 0;
            for (BpmProcessApplicationType o : list) {
                o.setSort(o.getSort() - 1);
                countChanged += bpmProcessApplicationTypeMapper.updateById(o);
            }
            return countChanged == countTotal - sort;
        }
        return true;
    }

    /**
     *
     * @param sort
     * @return
     */
    public boolean sortProcessApplicationType(Integer sort, Integer countTotal, List<BpmProcessApplicationType> list) {
        //修改当前常用功能排序
        if (sort < countTotal) {
            int countChanged = 0;
            for (BpmProcessApplicationType o : list) {
                o.setSort(o.getSort() - 1);
                countChanged += bpmProcessApplicationTypeMapper.updateById(o);
            }
            return countChanged == countTotal - sort;
        }
        return true;
    }

    /**
     *  add process and application relationship
     *
     * @return
     */
    public boolean editProcessApplicationType(BpmProcessApplicationTypeVo vo) {

            List<BpmProcessApplicationType> list = this.applicationTypes(vo);
            for (BpmProcessApplicationType type : list) {
                QueryWrapper<BpmProcessApplicationType> wrapper = new QueryWrapper<BpmProcessApplicationType>().eq("is_del", 0).eq("category_id", type.getCategoryId());
                Long countTotal = bpmProcessApplicationTypeMapper.selectCount(wrapper);
                Integer sort = type.getSort();
                List<BpmProcessApplicationType> processApplicationTypes = bpmProcessApplicationTypeMapper.selectList(wrapper.gt("sort", sort));
                this.getBaseMapper().deleteById(type);
                this.sortProcessApplicationType(sort, countTotal.intValue(), processApplicationTypes);
            }
            for (Long id : vo.getProcessTypes()) {
                QueryWrapper<BpmProcessApplicationType> wrapper = new QueryWrapper<>();
                wrapper.eq("is_del", 0);
                wrapper.eq("category_id", id);
                //List<BpmProcessApplicationType> bpmProcessApplicationTypes = bpmProcessApplicationTypeMapper.selectList(wrapper);
                Long countCode = bpmProcessApplicationTypeMapper.selectCount(wrapper);
                BpmProcessApplicationType type = BpmProcessApplicationType
                        .builder()
                        .applicationId(vo.getApplicationId())
                        .categoryId(id)
                        .createTime(new Date())
                        .sort(countCode.intValue() + 1)
                        .build();
                if (vo.getVisbleState()!=null) {
                    type.setVisbleState(vo.getVisbleState());
                }
                this.save(type);
            }
        return true;
    }

    /**
     * add process and application relationship
     *
     * @return
     */
    public Long addProcessApplicationType(BpmProcessApplicationTypeVo vo) {
        QueryWrapper<BpmProcessApplicationType> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del", 0);
        wrapper.eq("category_id", vo.getCategoryId());
        String passFilList ="serialVersionUID";
        BpmProcessApplicationType type = new BpmProcessApplicationType();
        BeanUtils.copyProperties(vo, type, passFilList);
        Long countCode = bpmProcessApplicationTypeMapper.selectCount(wrapper);
        type.setSort(countCode.intValue() + 1);
        type.setCreateTime(new Date());
        this.getBaseMapper().insert(type);
        //获得需要返回的Id，如果Id为空则返回0
        Serializable id = Optional.ofNullable(Optional.ofNullable(type).orElseGet(() -> {
            return new BpmProcessApplicationType();
        }).getId()).orElse(0L);
        return Long.parseLong(id.toString());
    }

    /**
     * icon operation
     *
     * @return
     */
    public boolean iconOperation(BpmProcessApplicationTypeVo vo) {
        switch (vo.getType()) {
            //move left
            case 5:
                this.moveUp(vo.getId());
                break;
            //hide
            case 2:
                this.hiddenApplication(vo.getId(), true, vo.getCommonUseId());
                break;
            //visible
            case 1:
                this.hiddenApplication(vo.getId(), false, vo.getCommonUseId());
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

    /**
     * move left
     *
     * @return
     */
    public boolean moveUp(Long id) {


        BpmProcessApplicationType applicationType = this.getBaseMapper().selectById(id);
        if (applicationType==null) {
            throw new JiMuBizException("无此条记录");
        }

        BpmProcessAppApplication application = processAppApplicationService.getBaseMapper().selectById(applicationType.getApplicationId());
        Integer sort = applicationType.getSort();
        if (sort == 1) {
            throw new JiMuBizException("当前记录已到顶");
        }


        if (applicationType.getCategoryId() == 1 || applicationType.getCategoryId() == 2) {
            BpmProcessApplicationType type = null;
            BpmProcessApplicationType bpmProcessApplicationType = bpmProcessApplicationTypeMapper.selectOne(
                    new QueryWrapper<BpmProcessApplicationType>()
                            .eq("sort", sort - 1)
                            .eq("is_del",0)
                            .eq("category_id", applicationType.getCategoryId()));
            if(bpmProcessApplicationType==null){
                throw new JiMuBizException("没有此记录");
            }
            type=bpmProcessApplicationType;


            applicationType.setSort(sort - 1);
            bpmProcessApplicationTypeMapper.updateById(applicationType);
            type.setSort(sort);
            bpmProcessApplicationTypeMapper.updateById(type);
            return true;
        } else {

            if (application.getIsSon() == 0) {

                List<BpmProcessAppApplicationVo> vos = bpmProcessAppApplicationMapper
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
                    throw new JiMuBizException("当前记录已到顶");
                }
                //交换sort
                BpmProcessAppApplicationVo vo = vos.get(vos.size() - 1);
                applicationType.setSort(vo.getSort());
                bpmProcessApplicationTypeMapper.updateById(applicationType);
                bpmProcessApplicationTypeMapper.updateById(BpmProcessApplicationType
                        .builder()
                        .id(vo.getId().longValue())
                        .sort(sort)
                        .build());
                return true;
            }

            if (application.getIsSon() == 1) {

                List<BpmProcessAppApplicationVo> vos = bpmProcessAppApplicationMapper
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
                    throw new JiMuBizException("当前记录已到顶");
                }

                BpmProcessAppApplicationVo vo = vos.get(vos.size() - 1);
                applicationType.setSort(vo.getSort());
                bpmProcessApplicationTypeMapper.updateById(applicationType);
                bpmProcessApplicationTypeMapper.updateById(BpmProcessApplicationType
                        .builder()
                        .id(vo.getId().longValue())
                        .sort(sort)
                        .build());
                return true;
            }
        }
        throw new JiMuBizException("无满足条件的排序方式！");

    }

    /**
     * show or hide
     *
     * @param id
     * @param isHidden
     * @return
     */
    public boolean hiddenApplication(Long id, boolean isHidden, Long commonUseId) {
        BpmProcessApplicationType applicationType = bpmProcessApplicationTypeMapper.selectById(id);
        Integer visbleState = 0;
        if (!isHidden) {
            visbleState = 1;
        }
        if (applicationType.getHistoryId()!=null) {
            BpmProcessApplicationType type = this.getBaseMapper().selectById((applicationType.getHistoryId()));
            type.setVisbleState(visbleState);
            this.updateById(type);
        } else {
            List<BpmProcessApplicationType> applicationTypeList = this.getProcessApplicationType(BpmProcessApplicationTypeVo.builder()
                    .categoryId(commonUseId)
                    .applicationId(applicationType.getApplicationId())
                    .build());
            if (!CollectionUtils.isEmpty(applicationTypeList)) {
                BpmProcessApplicationType processApplicationType = applicationTypeList.get(0);
                processApplicationType.setVisbleState(visbleState);
                this.updateById(processApplicationType);
            }
        }
        applicationType.setVisbleState(visbleState);
        this.updateById(applicationType);
        return true;
    }

    public List<BpmProcessApplicationType> getProcessApplicationType(BpmProcessApplicationTypeVo vo) {
        QueryWrapper<BpmProcessApplicationType> wrapper = new QueryWrapper<>();
        if (vo.getCategoryId()!=null) {
            wrapper.eq("category_id", vo.getCategoryId());
        }
        if (vo.getApplicationId()!=null) {
            wrapper.eq("application_id", vo.getApplicationId());
        }
        if (vo.getVisbleState()!=null) {
            wrapper.eq("visble_state", vo.getVisbleState());
        }
        List<BpmProcessApplicationType> bpmProcessApplicationTypes = bpmProcessApplicationTypeMapper.selectList(wrapper);
        return bpmProcessApplicationTypes;
    }


    public boolean asCommonlyUsed(Long processTypeId, Integer id, boolean isCancel, Integer type) {
        //BpmProcessApplicationType applicationType = this.selectById(processTypeId);
        BpmProcessCategory processCategory = bpmProcessCategoryService.getBaseMapper().selectById(processTypeId);
        BpmProcessApplicationType processApplicationType = this.getBaseMapper().selectById(id);
        if (processCategory==null) {
           throw  new JiMuBizException("无此条记录");
        }
        if (isCancel) {
            Long aLong = this.addProcessApplicationType(BpmProcessApplicationTypeVo.builder()
                    .categoryId(processTypeId)
                    .state(1)
                    .historyId(id.longValue())
                    .commonUseState(1)
                    .visbleState(processApplicationType.getVisbleState())
                    .applicationId(processApplicationType.getApplicationId())
                    .build());
            processApplicationType.setState(1);
            processApplicationType.setHistoryId(aLong);
            this.updateById(processApplicationType);
        } else {
            Integer sort = 0;
            Long countTotal = 0L;
            List<BpmProcessApplicationType> list = null;
            if (processApplicationType.getHistoryId()!=null && processApplicationType.getCommonUseState()!=null) {
                QueryWrapper<BpmProcessApplicationType> wrapper = new QueryWrapper<BpmProcessApplicationType>().eq("is_del", 0).eq("category_id", processTypeId);
                countTotal = bpmProcessApplicationTypeMapper.selectCount(wrapper);
                processApplicationType.setIsDel(1);
                sort = processApplicationType.getSort();
                processApplicationType.setSort(0);

                this.updateById(processApplicationType);
                BpmProcessApplicationType applicationType = this.getBaseMapper().selectById(processApplicationType.getHistoryId());
                applicationType.setState(0);
                list = bpmProcessApplicationTypeMapper.selectList(wrapper.gt("sort", sort));

                this.updateById(applicationType);
            } else {
                processApplicationType.setState(0);
                this.updateById(processApplicationType);
                List<BpmProcessApplicationType> applicationTypeList = this.getProcessApplicationType(BpmProcessApplicationTypeVo.builder()
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
                    countTotal = bpmProcessApplicationTypeMapper.selectCount(wrapper);
                    list = bpmProcessApplicationTypeMapper.selectList(wrapper.gt("sort", sort));
                    this.updateById(applicationType);
                }
            }
            this.sortProcessApplicationType(sort, countTotal.intValue(), list);
        }
        return true;
    }
}
