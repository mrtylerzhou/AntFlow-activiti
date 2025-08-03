package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.apache.commons.lang3.StringEscapeUtils;
import org.openoa.base.entity.*;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.StrUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessAppApplicationService;
import org.openoa.engine.vo.*;
import org.openoa.engine.bpmnconf.mapper.BpmProcessAppApplicationMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;



/**
 *  process application curd service
 * @since 0.5
 */
@Repository
public class BpmProcessAppApplicationServiceImpl extends ServiceImpl<BpmProcessAppApplicationMapper, BpmProcessAppApplication> implements BpmProcessAppApplicationService {




    /**
     * get application list
     *
     * @param list
     * @return
     */
    @Deprecated
    public List<BpmProcessAppApplication> getBpmProcessAppApplication(List<String> list) {
        List<BpmProcessAppApplication> appApplications = new ArrayList<>();
        QueryWrapper<BpmProcessAppApplication> applicationWrapper = new QueryWrapper<>();
        applicationWrapper.in("process_key", list);
        applicationWrapper.eq("is_del", 0);
        getBaseMapper().selectList(applicationWrapper).forEach(o -> {
            appApplications.add(BpmProcessAppApplication.builder()
                    .id(o.getId())
                    .source(o.getEffectiveSource())
                    .state(o.getState())
                    .sort(o.getSort())
                    .route(o.getRoute())
                    .processKey(o.getProcessKey())
                    .title(o.getTitle())
                    .build());
        });
        return appApplications;
    }

    /**
     * query applications that for all users
     *
     * @param list
     * @return
     */
    @Deprecated
    public List<BpmProcessAppApplication> allProcessAppApplication(List<BpmProcessAppData> list) {
        List<BpmProcessAppApplication> appApplications = new ArrayList<>();
        for (BpmProcessAppData processAppData : list) {
            BpmProcessAppApplication appApplication = getBaseMapper().selectById(processAppData.getApplicationId());
            if (processAppData.getState().equals(1)) {
                appApplication.setRoute("");
            } else {
                appApplication.setSource(appApplication.getEffectiveSource());
            }
            if (appApplication.getIsDel().equals("0")) {
                appApplications.add(appApplication);
            }
        }
        return appApplications;
    }



    /**
     * data mapping
     */
    @Deprecated
    public List<BpmProcessAppApplication> processAppApplicationList(List<BpmProcessAppApplication> list) {
        return list.stream().map(o -> {
            if (o.getRoute()!=null) {
                String route = StringEscapeUtils.unescapeJson(o.getRoute());
                try {
                    o.setRoute(URLEncoder.encode(route, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return o;
        }).collect(Collectors.toList());
    }



    /**
     * add or modify applications that are icon applications
     */
    @Override
    public boolean addBpmProcessAppApplication(BpmProcessAppApplicationVo vo) {
        String[] passFilList = new String[]{"serialVersionUID", "isAll"};
        BpmProcessAppApplication forVo = new BpmProcessAppApplication();
        String route = StringEscapeUtils.unescapeHtml3(vo.getRoute());
        vo.setRoute(route);
        BeanUtils.copyProperties(vo, forVo, passFilList);
        if (vo.getId() != null) {
            forVo.setEffectiveSource(vo.getEffectiveSource());
            this.updateById(forVo);
        } else {
            forVo.setCreateTime(new Date());
            forVo.setEffectiveSource(vo.getEffectiveSource());
            forVo.setProcessKey(vo.getBusinessCode() + "_" + StrUtils.getFirstLetters(vo.getTitle()));
            // to check whether there is duplicate data
            QueryWrapper<BpmProcessAppApplication> wrapper = new QueryWrapper<BpmProcessAppApplication>().eq("process_name", vo.getTitle()).eq("is_del", 0);
            if (this.count(wrapper) > 0) {
                throw new AFBizException("该选项名称已存在");
            }
            this.save(forVo);
        }
//            Serializable id = Optional.ofNullable(Optional.ofNullable(forVo).orElseGet(() -> {
//                return new BpmProcessAppApplication();
//            }).getId()).orElse((int) 0L);
//            bpmProcessApplicationTypeService.editProcessApplicationType(BpmProcessApplicationTypeVo.builder()
//                    .applicationId(((Integer) id).longValue())
//                    .processTypes(vo.getProcessTypes())
//                    .build());

        return true;
    }

    /**
     * delete app
     *
     * @param id
     * @return
     */
    @Override
    public boolean deleteAppIcon(Long id) {
        BpmProcessAppApplication processApplicationType = getBaseMapper().selectById(id);
        processApplicationType.setIsDel(1);
        getBaseMapper().updateById(processApplicationType);
        return true;
    }


    /**
     * aapp process list;
     */
    @Override
    public List<ProcessTypeInforVo> list(String version) {

       //todo to be redesigned
        return null;
    }




    /**
     * querying all application list,including all versions
     *
     * @return
     */
    @Override
    public List<BpmProcessAppApplicationVo> listProcessApplication() {
        List<BpmProcessAppApplicationVo> result = new ArrayList<>();
        QueryWrapper<BpmProcessAppApplication> wrapper = new QueryWrapper<>();
        List<BpmProcessAppApplication> list = this.getBaseMapper().selectList(wrapper);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (!CollectionUtils.isEmpty(list)) {
            for (BpmProcessAppApplication b : list) {
                BpmProcessAppApplicationVo vo = new BpmProcessAppApplicationVo();
                vo.setId(b.getId());
                vo.setProcessName(b.getTitle());
                vo.setCreateTime(b.getCreateTime());
                vo.setCreateTimeStr(df.format(b.getCreateTime()));
                vo.setName(b.getTitle() + "(" + df.format(b.getCreateTime()) + ")");
                result.add(vo);
            }
        }
        return result;
    }

    /**
     * get all applications
     */
    @Override
    public List<BaseIdTranStruVo> listProcessAppApplication(String search, Integer limitSize) {
        if (limitSize==null) {
            limitSize = 10;
        }
        List<BpmProcessAppApplicationVo> vos = getBaseMapper().searchIcon(BpmProcessAppApplicationVo.builder()
                .search(search)
                .limitSize(limitSize)
                .build());
        List<BaseIdTranStruVo> list = new ArrayList<>();
        list.addAll(vos.stream().map(o ->
                BaseIdTranStruVo.builder()
                        .id(o.getId().toString())
                        .name(o.getProcessName())
                        .build()
        ).collect(Collectors.toList()));
        return list;
    }



    @Override
    public List<BpmProcessAppApplicationVo> selectThirdPartyApplications(String businessPartyMark) {
        return getBaseMapper().selectAllByBusinessPart(businessPartyMark);
    }

    @Override
    public List<BpmProcessAppApplicationVo> selectAllByPartMarkId(Integer partyMarkId) {
        return getBaseMapper().selectAllByPartMarkId(partyMarkId);
    }
    @Override
    public List<BpmProcessAppApplication> selectApplicationList() {
       return getBaseMapper().selectList(null);
    }
    /**
     * query online process app
     *
     * @return
     */
    private List<BpmProcessAppApplication> applicationsList() {
        QueryWrapper<BpmProcessAppApplication> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del", 0);
        return getBaseMapper().selectList(wrapper);
    }

}
