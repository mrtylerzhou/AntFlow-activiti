package org.openoa.engine.bpmnconf.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.engine.bpmnconf.confentity.BpmProcessCategory;
import org.openoa.engine.bpmnconf.mapper.BpmProcessCategoryMapper;
import org.openoa.engine.vo.BpmProcessApplicationTypeVo;
import org.openoa.engine.vo.BpmProcessCategoryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * process category curd service
 */
@Service
public class BpmProcessCategoryServiceImpl extends ServiceImpl<BpmProcessCategoryMapper, BpmProcessCategory> {

    @Autowired
    private BpmProcessCategoryMapper bpmProcessCategoryMapper;
    @Autowired
    private BpmProcessApplicationTypeServiceImpl bpmProcessApplicationTypeService;

    /**
     * add category
     */
    public boolean editProcessCategory(BpmProcessCategoryVo vo) {
        String passFilList ="serialVersionUID";
        BpmProcessCategory forVo = new BpmProcessCategory();
        BeanUtils.copyProperties(vo,forVo,passFilList);

        if (vo.getId()!=null) {
            this.updateById(forVo);
        } else {

            //1.check whether the data is repeated
            QueryWrapper<BpmProcessCategory> wrapper = new QueryWrapper<BpmProcessCategory>()
                    .eq("process_type_name", vo.getProcessTypeName())
                    .eq("is_app", vo.getIsApp())
                    .eq("is_del", 0);
            if (this.count(wrapper) > 0) {
                throw new JiMuBizException("该选项名称已存在");
            }

            //2.get the maximum number of effective process categories
            QueryWrapper<BpmProcessCategory> categoryWrapper = new QueryWrapper<BpmProcessCategory>()
                    .eq("is_app", vo.getIsApp())
                    .eq("is_del", 0);
            Long countCode = bpmProcessCategoryMapper.selectCount(categoryWrapper);
            if (vo.getIsApp().equals(0)) {
                forVo.setEntrance("PC");
            } else if (vo.getIsApp().equals(1)) {
                forVo.setEntrance("APP");
            }
            forVo.setSort(countCode.intValue() + 1);
            this.save(forVo);
        }

        return true;
    }

    /**
     * operation
     *
     * @param type
     * @param id
     * @return
     */
    public boolean categoryOperation(Integer type, Long id) {
        switch (type) {

            case 2:
                this.moveUp(id);
                break;

            case 3:
                this.moveDown(id);
                break;

            case 4:
                this.delete(id);
                break;
        }

        return true;
    }


    public boolean moveUp(Long id) {
        BpmProcessCategory bpmProcessCategory = this.getBaseMapper().selectById(id);
        if (bpmProcessCategory==null) {
           throw  new JiMuBizException("无此条记录");
        }

        Integer sort = bpmProcessCategory.getSort();
        if (sort == 2) {
            throw new JiMuBizException("当前记录已到顶");
        }

        BpmProcessCategory processCategory = bpmProcessCategoryMapper.selectOne(new  QueryWrapper<BpmProcessCategory>()
        .eq("is_app",bpmProcessCategory.getIsApp())
                .eq("sort",sort-1)
        .eq("is_del",0)
        );
        processCategory.setSort(sort);
        this.updateById(processCategory);
        bpmProcessCategory.setSort(sort - 1);
        this.updateById(bpmProcessCategory);
        return true;
    }


    public boolean moveDown(Long id) {
        BpmProcessCategory bpmProcessCategory = this.getBaseMapper().selectById(id);
        if (bpmProcessCategory==null) {
            new JiMuBizException("无此条记录");
        }
        Integer sort = bpmProcessCategory.getSort();
        Long count = bpmProcessCategoryMapper.selectCount(new QueryWrapper<BpmProcessCategory>().eq("is_del", 0));
        if (sort >= count) {
            throw new JiMuBizException("当前记录已到底");
        }


        //get last modified record
        BpmProcessCategory processCategory = bpmProcessCategoryMapper.selectOne(
                new  QueryWrapper<BpmProcessCategory>()
                        .eq("is_app",bpmProcessCategory.getIsApp())
                        .eq("sort",sort+1)
                        .eq("is_del",0)
        );
        processCategory.setSort(sort);
        this.updateById(processCategory);
        bpmProcessCategory.setSort(sort + 1);
        this.updateById(bpmProcessCategory);
        return true;
    }


    public boolean delete(Long id) {
        BpmProcessCategory bpmProcessCategory = this.getBaseMapper().selectById(id);
        if (bpmProcessCategory==null) {
            new JiMuBizException("无此条记录");
        }
        Integer sort = bpmProcessCategory.getSort();
        QueryWrapper<BpmProcessCategory> wrapper = new QueryWrapper<BpmProcessCategory>().eq("is_del", 0).eq("is_app", bpmProcessCategory.getIsApp());
        Long countTotal = bpmProcessCategoryMapper.selectCount(wrapper);
        List<BpmProcessCategory> list = bpmProcessCategoryMapper.selectList(wrapper.gt("sort", sort));

        //delete application under a category
        bpmProcessApplicationTypeService.deletProcessApplicationType(BpmProcessApplicationTypeVo.builder().categoryId(id).build());
        bpmProcessCategory.setIsDel(1);
        bpmProcessCategory.setSort(0);
        this.updateById(bpmProcessCategory);
        //move left
        if (sort < countTotal) {
            int countChanged = 0;
            for (BpmProcessCategory o : list) {
                o.setSort(o.getSort() - 1);
                countChanged += bpmProcessCategoryMapper.updateById(o);
            }
            return countChanged == countTotal - sort;
        }
        return true;
    }

    /**
     * query a list of process category
     *
     * @param vo
     * @return
     */
    public List<BpmProcessCategory> processCategoryList(BpmProcessCategoryVo vo) {
        QueryWrapper<BpmProcessCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del", 0);
        if (vo.getIsApp()!=null) {
            wrapper.eq("is_app", vo.getIsApp());
        }
        List<BpmProcessCategory> bpmProcessCategories = bpmProcessCategoryMapper.selectList(wrapper);
        bpmProcessCategories.sort(Comparator.comparing(BpmProcessCategory::getSort));
        return bpmProcessCategories;
    }

    /**
     * get category vos
     *
     * @param vo
     * @return
     */
    public List<BpmProcessCategoryVo> processCategoryVos(BpmProcessCategoryVo vo) {
        return bpmProcessAppApplicationVoList(bpmProcessCategoryMapper.findProcessCategory(vo));
    }

    /**
     * mapping
     */
    public List<BpmProcessCategoryVo> bpmProcessAppApplicationVoList(List<BpmProcessCategoryVo> list) {
        return list.stream().peek(o -> o.setName(o.getProcessTypeName() + o.getEntrance())).collect(Collectors.toList());
    }

    /**
     * @param id
     * @return
     */
    public BpmProcessCategory getProcessCategory(Long id) {
        QueryWrapper<BpmProcessCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del", 0);
        wrapper.eq("id", id);
        List<BpmProcessCategory> bpmProcessCategoryList = bpmProcessCategoryMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(bpmProcessCategoryList)) {
            return bpmProcessCategoryList.get(0);
        }
        return null;
    }
}
