package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmProcessApplicationType;
import org.openoa.engine.bpmnconf.mapper.BpmProcessApplicationTypeMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessApplicationTypeService;
import org.openoa.engine.vo.BpmProcessApplicationTypeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 *  process application type curd service
 */
@Repository
public class BpmProcessApplicationTypeServiceImpl extends ServiceImpl<BpmProcessApplicationTypeMapper, BpmProcessApplicationType> implements BpmProcessApplicationTypeService {

    @Override
    public List<BpmProcessApplicationType> applicationTypes(BpmProcessApplicationTypeVo vo) {
        QueryWrapper<BpmProcessApplicationType> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del", 0);
        if (vo.getApplicationId()!=null) {
            wrapper.eq("application_id", vo.getApplicationId());
        }
        if (vo.getCategoryId()==null) {
            wrapper.eq("category_id", vo.getCategoryId());
        }
        return getBaseMapper().selectList(wrapper);
    }

    /**
     * delete application that under a category
     */
    @Override
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
    @Override
    public boolean sortProcessApplicationType(Integer sort, Long processTypeId) {
        QueryWrapper<BpmProcessApplicationType> wrapper = new QueryWrapper<BpmProcessApplicationType>().eq("is_del", 0).eq("category_id", processTypeId);
        Long countTotal = getBaseMapper().selectCount(wrapper);
        List<BpmProcessApplicationType> list = getBaseMapper().selectList(wrapper.gt("sort", sort));
        //修改当前常用功能排序
        if (sort < countTotal) {
            int countChanged = 0;
            for (BpmProcessApplicationType o : list) {
                o.setSort(o.getSort() - 1);
                countChanged += getBaseMapper().updateById(o);
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
    @Override
    public boolean sortProcessApplicationType(Integer sort, Integer countTotal, List<BpmProcessApplicationType> list) {
        //修改当前常用功能排序
        if (sort < countTotal) {
            int countChanged = 0;
            for (BpmProcessApplicationType o : list) {
                o.setSort(o.getSort() - 1);
                countChanged += getBaseMapper().updateById(o);
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
    @Override
    public boolean editProcessApplicationType(BpmProcessApplicationTypeVo vo) {

            List<BpmProcessApplicationType> list = this.applicationTypes(vo);
            for (BpmProcessApplicationType type : list) {
                QueryWrapper<BpmProcessApplicationType> wrapper = new QueryWrapper<BpmProcessApplicationType>().eq("is_del", 0).eq("category_id", type.getCategoryId());
                Long countTotal = getBaseMapper().selectCount(wrapper);
                Integer sort = type.getSort();
                List<BpmProcessApplicationType> processApplicationTypes = getBaseMapper().selectList(wrapper.gt("sort", sort));
                this.getBaseMapper().deleteById(type);
                this.sortProcessApplicationType(sort, countTotal.intValue(), processApplicationTypes);
            }
            for (Long id : vo.getProcessTypes()) {
                QueryWrapper<BpmProcessApplicationType> wrapper = new QueryWrapper<>();
                wrapper.eq("is_del", 0);
                wrapper.eq("category_id", id);
                //List<BpmProcessApplicationType> bpmProcessApplicationTypes = bpmProcessApplicationTypeMapper.selectList(wrapper);
                Long countCode = getBaseMapper().selectCount(wrapper);
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
    @Override
    public Long addProcessApplicationType(BpmProcessApplicationTypeVo vo) {
        QueryWrapper<BpmProcessApplicationType> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del", 0);
        wrapper.eq("category_id", vo.getCategoryId());
        String passFilList ="serialVersionUID";
        BpmProcessApplicationType type = new BpmProcessApplicationType();
        BeanUtils.copyProperties(vo, type, passFilList);
        Long countCode = getBaseMapper().selectCount(wrapper);
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
     * show or hide
     *
     * @param id
     * @param isHidden
     * @return
     */
    @Override
    public boolean hiddenApplication(Long id, boolean isHidden, Long commonUseId) {
        BpmProcessApplicationType applicationType = getBaseMapper().selectById(id);
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

    @Override
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
        List<BpmProcessApplicationType> bpmProcessApplicationTypes = getBaseMapper().selectList(wrapper);
        return bpmProcessApplicationTypes;
    }



}
