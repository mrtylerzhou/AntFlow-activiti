package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.dto.PageDto;
import org.openoa.base.dto.StartFlowListPageReq;
import org.openoa.base.entity.BpmProcessCategory;
import org.openoa.base.entity.BpmProcessPermissions;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.BpmnProcessAdminProvider;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.interf.LFFormOperationAdaptor;
import org.openoa.base.service.AfUserService;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.BpmnConfMapper;
import org.openoa.engine.bpmnconf.mapper.BpmProcessPermissionsMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.StartFlowListBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessCategoryService;
import org.openoa.engine.vo.StartFlowCategoryVo;
import org.openoa.engine.vo.StartFlowListRowVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 发起流程页聚合实现
 * 流程范围:DIY + LF + outside 全部可用流程(effective_status=1);未设计的 DIY 适配器并入「未分类」
 * 权限:is_all=1 放行;is_all=0 按创建权限(permissions_type=2)过滤,从用户主体出发(用户/部门及子部门/角色)
 * 布局:每栏 8 卡片位,标题占 1 位,超长分类块(>7 个流程)整栏内滚;一页最多 3 栏,分类块不跨栏不跨页
 */
@Slf4j
@Service
public class StartFlowListBizServiceImpl implements StartFlowListBizService {

    /**
     * 每栏卡片位容量(含分类标题 1 位)
     */
    private static final int COLUMN_CAPACITY = 8;
    /**
     * 每页最多栏数
     */
    private static final int MAX_COLUMN = 3;
    /**
     * 未分类占位 key
     */
    private static final long UNCATEGORIZED_KEY = -1L;

    @Autowired
    private Map<String, FormOperationAdaptor> formOperationAdaptorMap;
    @Autowired
    private BpmnConfMapper bpmnConfMapper;
    @Autowired
    private BpmProcessCategoryService bpmProcessCategoryService;
    @Autowired
    private BpmProcessPermissionsMapper bpmProcessPermissionsMapper;
    @Autowired
    private AfUserService afUserService;
    /**
     * 流程管理员提供者(可选):是管理员则展示全部流程(最高权限)
     */
    @Autowired(required = false)
    private BpmnProcessAdminProvider bpmnProcessAdminProvider;

    @Override
    public ResultAndPage<StartFlowCategoryVo> page(StartFlowListPageReq req) {
        if (req == null) {
            req = new StartFlowListPageReq();
        }
        int pageNo = (req.getPage() == null || req.getPage() < 1) ? 1 : req.getPage();

        // 1. 聚合可用流程
        List<StartFlowListRowVo> rows = bpmnConfMapper.selectStartFlowList();
        if (rows == null) {
            rows = new ArrayList<>();
        }
        fillDerivedType(rows);
        rows.addAll(collectUndesignedDiy(rows));

        // 2. 权限过滤(管理员跳过,展示全部)
        List<StartFlowListRowVo> allowed = filterByPermission(rows);
        if (CollectionUtils.isEmpty(allowed)) {
            return emptyResult(pageNo);
        }

        // 3. 条件过滤(流程名称 > formCode > 流程类型)
        allowed = filterByQuery(allowed, req);
        if (CollectionUtils.isEmpty(allowed)) {
            return emptyResult(pageNo);
        }

        // 4. 分类分组(分类按 sort asc,未分类最后;分类内按创建时间 asc)
        List<StartFlowCategoryVo> blocks = groupByCategory(allowed);

        // 5. 栏切分
        List<List<StartFlowCategoryVo>> pages = splitColumns(blocks);
        int pageCount = Math.max(pages.size(), 1);
        int idx = pageNo - 1;
        List<StartFlowCategoryVo> current = (idx >= 0 && idx < pages.size()) ? pages.get(idx) : Collections.emptyList();

        PageDto out = PageDto.buildCountedPage(pageDtoOf(pageNo), blocks.size());
        out.setPageCount(pageCount);
        out.setPage(pageNo);
        return new ResultAndPage<>(current, out);
    }

    private ResultAndPage<StartFlowCategoryVo> emptyResult(int pageNo) {
        PageDto out = PageDto.buildCountedPage(pageDtoOf(pageNo), 0);
        out.setPageCount(1);
        out.setPage(1);
        return new ResultAndPage<>(Collections.emptyList(), out);
    }

    private PageDto pageDtoOf(int pageNo) {
        PageDto dto = new PageDto();
        dto.setPage(pageNo);
        dto.setPageSize(10);
        return dto;
    }

    /**
     * 条件过滤,优先级:流程名称 > formCode > 流程类型(命中前者忽略后者)
     */
    private List<StartFlowListRowVo> filterByQuery(List<StartFlowListRowVo> rows, StartFlowListPageReq req) {
        if (StringUtils.hasText(req.getBpmnName())) {
            String kw = req.getBpmnName().trim();
            return rows.stream()
                    .filter(r -> r.getBpmnName() != null && r.getBpmnName().contains(kw))
                    .collect(Collectors.toList());
        }
        if (StringUtils.hasText(req.getFormCode())) {
            String kw = req.getFormCode().trim();
            return rows.stream()
                    .filter(r -> r.getFormCode() != null && r.getFormCode().contains(kw))
                    .collect(Collectors.toList());
        }
        if (req.getCategoryId() != null) {
            if (req.getCategoryId() == -1L) {
                // 未分类: bpmn_type IS NULL
                return rows.stream()
                        .filter(r -> r.getBpmnType() == null)
                        .collect(Collectors.toList());
            }
            return rows.stream()
                    .filter(r -> r.getBpmnType() != null && r.getBpmnType().longValue() == req.getCategoryId())
                    .collect(Collectors.toList());
        }
        return rows;
    }

    /**
     * 派生 type:is_out_side_process=1 → OUTSIDE;is_lowcode_flow=1 → LF;否则 DIY
     */
    private void fillDerivedType(List<StartFlowListRowVo> rows) {
        for (StartFlowListRowVo r : rows) {
            if (Integer.valueOf(1).equals(r.getIsOutSideProcess())) {
                r.setType("OUTSIDE");
            } else if (Integer.valueOf(1).equals(r.getIsLowCodeFlow())) {
                r.setType("LF");
            } else {
                r.setType("DIY");
            }
        }
    }

    /**
     * 收集未在 t_bpmn_conf 中出现的 DIY 适配器 Bean(未设计流程),并入未分类
     */
    private List<StartFlowListRowVo> collectUndesignedDiy(List<StartFlowListRowVo> rows) {
        Set<String> existing = rows.stream().map(StartFlowListRowVo::getFormCode).collect(Collectors.toSet());
        List<StartFlowListRowVo> result = new ArrayList<>();
        if (formOperationAdaptorMap == null) {
            return result;
        }
        for (Map.Entry<String, FormOperationAdaptor> entry : formOperationAdaptorMap.entrySet()) {
            FormOperationAdaptor adaptor = entry.getValue();
            if (adaptor instanceof LFFormOperationAdaptor) {
                continue;
            }
            ActivitiServiceAnno anno = ClassUtils.getUserClass(adaptor).getAnnotation(ActivitiServiceAnno.class);
            if (anno == null || StringUtils.isEmpty(anno.desc())) {
                continue;
            }
            if (existing.contains(entry.getKey())) {
                continue;
            }
            result.add(StartFlowListRowVo.builder()
                    .formCode(entry.getKey())
                    .bpmnName(anno.desc())
                    .type("DIY")
                    .createTime(null)
                    .build());
        }
        return result;
    }

    /**
     * 权限过滤:仅展示当前用户有创建权限(permissions_type=2)的流程
     * 管理员(provideProcessAdminInfo 命中)跳过过滤,展示全部(最高权限)
     * 匹配方式(从主体出发):全员(object_type=4)∪ 用户(object_type=1)∪ 部门及子部门(object_type=2)∪ 角色(object_type=3)
     */
    private List<StartFlowListRowVo> filterByPermission(List<StartFlowListRowVo> rows) {
        String userId = SecurityUtils.getLogInEmpIdSafe();
        if (isProcessAdmin(userId)) {
            return rows;
        }
        Set<String> allowedKeys = getAllowedCreateProcessKeys(userId);
        List<StartFlowListRowVo> result = new ArrayList<>();
        for (StartFlowListRowVo r : rows) {
            if (allowedKeys.contains(r.getFormCode())) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * 判断当前用户是否为流程管理员
     */
    private boolean isProcessAdmin(String userId) {
        if (bpmnProcessAdminProvider == null || StringUtils.isEmpty(userId)) {
            return false;
        }
        BaseIdTranStruVo admin = bpmnProcessAdminProvider.provideProcessAdminInfo();
        return admin != null && StringUtils.hasText(admin.getId()) && userId.equals(admin.getId());
    }

    /**
     * 从用户主体出发,查询有创建权限(permissions_type=2)的 processKey 集合
     */
    private Set<String> getAllowedCreateProcessKeys(String userId) {
        List<BaseIdTranStruVo> depts = afUserService.getUserDepartmentsById(userId);
        List<BaseIdTranStruVo> roles = afUserService.getUserRolesById(userId);

        QueryWrapper<BpmProcessPermissions> wrapper = new QueryWrapper<>();
        wrapper.eq("permissions_type", 2);
        wrapper.eq("is_del", 0);
        wrapper.and(q -> {
            // 全员(object_type=4)对所有用户生效
            q.eq("object_type", 4)
                    .or(o -> o.eq("object_type", 1).eq("object_id", userId));
            if (!CollectionUtils.isEmpty(depts)) {
                List<String> deptIds = depts.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
                q.or(o -> o.eq("object_type", 2).in("object_id", deptIds));
            }
            if (!CollectionUtils.isEmpty(roles)) {
                List<String> roleIds = roles.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
                q.or(o -> o.eq("object_type", 3).in("object_id", roleIds));
            }
        });
        return bpmProcessPermissionsMapper.selectList(wrapper).stream()
                .map(BpmProcessPermissions::getProcessKey)
                .collect(Collectors.toSet());
    }

    /**
     * 按分类分组:分类按 sort asc,未分类最后;分类内按创建时间 asc(null 最后)
     */
    private List<StartFlowCategoryVo> groupByCategory(List<StartFlowListRowVo> rows) {
        List<BpmProcessCategory> categories = bpmProcessCategoryService
                .processCategoryList(new org.openoa.engine.vo.BpmProcessCategoryVo());

        Map<Long, List<StartFlowListRowVo>> group = new HashMap<>();
        for (StartFlowListRowVo r : rows) {
            Long key = r.getBpmnType() == null ? UNCATEGORIZED_KEY : r.getBpmnType().longValue();
            group.computeIfAbsent(key, k -> new ArrayList<>()).add(r);
        }

        List<StartFlowCategoryVo> blocks = new ArrayList<>();
        for (BpmProcessCategory c : categories) {
            List<StartFlowListRowVo> fs = group.remove(c.getId());
            if (CollectionUtils.isEmpty(fs)) {
                continue;
            }
            blocks.add(buildBlock(c.getId(), c.getProcessTypeName(), fs));
        }
        List<StartFlowListRowVo> uncategorized = group.get(UNCATEGORIZED_KEY);
        if (!CollectionUtils.isEmpty(uncategorized)) {
            blocks.add(buildBlock(null, "未分类", uncategorized));
        }
        return blocks;
    }

    private StartFlowCategoryVo buildBlock(Long categoryId, String categoryName, List<StartFlowListRowVo> fs) {
        fs.sort(Comparator.comparing(StartFlowListRowVo::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())));
        List<StartFlowCategoryVo.StartFlowVo> flows = fs.stream().map(r ->
                StartFlowCategoryVo.StartFlowVo.builder()
                        .formCode(r.getFormCode())
                        .bpmnName(r.getBpmnName())
                        .type(r.getType())
                        .applicationId(r.getApplicationId())
                        .createTime(r.getCreateTime())
                        .build()
        ).collect(Collectors.toList());
        return StartFlowCategoryVo.builder()
                .categoryId(categoryId)
                .categoryName(categoryName)
                .flows(flows)
                .build();
    }

    /**
     * 栏切分:按栏 1→2→3 顺序装,装不下换栏,三栏都装不下进下一页;分类块不跨栏不跨页
     */
    private List<List<StartFlowCategoryVo>> splitColumns(List<StartFlowCategoryVo> blocks) {
        List<List<StartFlowCategoryVo>> pages = new ArrayList<>();
        List<StartFlowCategoryVo> currentPage = new ArrayList<>();
        int[] used = new int[MAX_COLUMN];
        for (StartFlowCategoryVo block : blocks) {
            int size = blockSize(block.getFlows().size());
            int col = -1;
            for (int i = 0; i < MAX_COLUMN; i++) {
                if (used[i] + size <= COLUMN_CAPACITY) {
                    col = i;
                    break;
                }
            }
            if (col == -1) {
                pages.add(currentPage);
                currentPage = new ArrayList<>();
                used = new int[MAX_COLUMN];
                col = 0;
            }
            used[col] += size;
            block.setColumn(col);
            currentPage.add(block);
        }
        if (!currentPage.isEmpty()) {
            pages.add(currentPage);
        }
        return pages;
    }

    /**
     * 分类块占位:标题 1 位 + 流程卡片;超过栏容量则整栏内滚(占满一栏)
     */
    private int blockSize(int flowCount) {
        return Math.min(flowCount + 1, COLUMN_CAPACITY);
    }
}
