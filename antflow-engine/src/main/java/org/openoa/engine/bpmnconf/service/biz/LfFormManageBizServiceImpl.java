package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.BpmnConfLfFormdata;
import org.openoa.base.entity.BpmnConfLfFormdataField;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.LfFormManageVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.engine.bpmnconf.mapper.BpmnConfLfFormdataMapper;
import org.openoa.engine.bpmnconf.mapper.BpmnConfMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfLfFormdataFieldServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfLfFormdataServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.LfFormManageBizService;
import org.openoa.engine.lowflow.service.LfFormWidgetParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LfFormManageBizServiceImpl implements LfFormManageBizService {

    private static final String FORM_CODE_PREFIX = "LFFM";
    private static final int FORM_CODE_SEQ_LEN = 5;
    private static final String FORM_CODE_FORMAT = "%0" + FORM_CODE_SEQ_LEN + "d";
    private static final Pattern FORM_CODE_PATTERN = Pattern.compile(".*-([0-9]{" + FORM_CODE_SEQ_LEN + "})");

    @Autowired
    private BpmnConfLfFormdataServiceImpl lfFormdataService;
    @Autowired
    private BpmnConfLfFormdataFieldServiceImpl lfFormdataFieldService;
    @Autowired
    private BpmnConfLfFormdataMapper lfFormdataMapper;
    @Autowired
    private BpmnConfMapper bpmnConfMapper;

    @Override
    public ResultAndPage<LfFormManageVo> listPage(PageDto pageDto, LfFormManageVo vo) {
        Page<LfFormManageVo> page = new Page<>(pageDto.getPage(), pageDto.getPageSize());
        List<LfFormManageVo> records = lfFormdataMapper.listEffectiveFormPage(page, vo);
        pageDto.setTotalCount((int) page.getTotal());
        pageDto.setPageCount((int) page.getPages());
        return new ResultAndPage<>(records, pageDto);
    }

    @Override
    public LfFormManageVo getById(Long id) {
        if (id == null) {
            throw new AFBizException("id不能为空");
        }
        // 管理界面查询走逻辑删除过滤
        BpmnConfLfFormdata formdata = lfFormdataService.getById(id);
        if (formdata == null) {
            throw new AFBizException("表单不存在或已删除");
        }
        LfFormManageVo vo = new LfFormManageVo();
        vo.setId(formdata.getId());
        vo.setFormCode(formdata.getFormCode());
        vo.setFormName(formdata.getFormName());
        vo.setFormdata(formdata.getFormdata());
        vo.setEffectiveStatus(formdata.getEffectiveStatus());
        vo.setCreateUser(formdata.getCreateUser());
        vo.setCreateTime(formdata.getCreateTime());
        vo.setUpdateUser(formdata.getUpdateUser());
        vo.setUpdateTime(formdata.getUpdateTime());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(LfFormManageVo vo) {
        if (Strings.isNullOrEmpty(vo.getFormName())) {
            throw new AFBizException("表单名称不能为空");
        }
        if (Strings.isNullOrEmpty(vo.getFormdata())) {
            throw new AFBizException("表单内容不能为空");
        }
        String currentUser = SecurityUtils.getLogInEmpName();
        String formCode = vo.getFormCode();

        boolean isNewFamily = Strings.isNullOrEmpty(formCode);
        if (isNewFamily) {
            // 新建家族
            formCode = generateNewFormCode();
        }
        // 新建家族首版本默认生效; 编辑产生的新版本默认不生效,由用户在历史版本中手动点击生效

        BpmnConfLfFormdata formdata = new BpmnConfLfFormdata();
        formdata.setBpmnConfId(null);
        formdata.setFormCode(formCode);
        formdata.setFormName(vo.getFormName());
        formdata.setFormdata(vo.getFormdata());
        formdata.setEffectiveStatus(isNewFamily ? 1 : 0);
        formdata.setCreateUser(currentUser);
        lfFormdataService.save(formdata);

        // 同步字段元数据
        List<BpmnConfLfFormdataField> fields = LfFormWidgetParser.parseFields(vo.getFormdata(), null, formdata.getId());
        lfFormdataFieldService.saveBatch(fields);

        return formdata.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null) {
            throw new AFBizException("id不能为空");
        }
        BpmnConfLfFormdata formdata = lfFormdataService.getById(id);
        if (formdata == null) {
            throw new AFBizException("表单不存在或已删除");
        }
        // 删除保护：当前生效版本不可删除
        if (formdata.getEffectiveStatus() != null && formdata.getEffectiveStatus() == 1) {
            throw new AFBizException("当前生效的版本不可删除");
        }
        // 删除保护：被生效流程引用时拒绝
        int refCount = bpmnConfMapper.countEffectiveConfReferencingFormdata(id);
        if (refCount > 0) {
            throw new AFBizException(String.format("该表单版本已被%d个生效流程引用，请先解除引用后再删除", refCount));
        }
        lfFormdataService.removeById(id);
    }

    @Override
    public List<LfFormManageVo> listHistory(String formCode) {
        if (Strings.isNullOrEmpty(formCode)) {
            throw new AFBizException("formCode不能为空");
        }
        return lfFormdataMapper.listVersionsByFormCode(formCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void effective(Long id) {
        if (id == null) {
            throw new AFBizException("id不能为空");
        }
        BpmnConfLfFormdata formdata = lfFormdataService.getById(id);
        if (formdata == null) {
            throw new AFBizException("表单不存在或已删除");
        }
        String formCode = formdata.getFormCode();
        // 同 formCode 的其他生效版本置为非生效
        lfFormdataService.update(Wrappers.<BpmnConfLfFormdata>lambdaUpdate()
                .eq(BpmnConfLfFormdata::getFormCode, formCode)
                .eq(BpmnConfLfFormdata::getEffectiveStatus, 1)
                .set(BpmnConfLfFormdata::getEffectiveStatus, 0));
        // 当前版本置为生效
        lfFormdataService.update(Wrappers.<BpmnConfLfFormdata>lambdaUpdate()
                .eq(BpmnConfLfFormdata::getId, id)
                .set(BpmnConfLfFormdata::getEffectiveStatus, 1));
    }

    @Override
    public List<LfFormManageVo> listEffectiveForSelect() {
        return lfFormdataMapper.listAllEffectiveForms();
    }

    @Override
    public List<BpmnConfVo> listReferencingConfs(Long formdataId) {
        if (formdataId == null) {
            throw new AFBizException("formdataId不能为空");
        }
        return bpmnConfMapper.listConfsReferencingFormdata(formdataId);
    }

    /**
     * 生成新的家族 formCode：LFFM-00001, LFFM-00002, ...
     */
    private String generateNewFormCode() {
        String prefix = FORM_CODE_PREFIX + "-";
        String maxFormCode = lfFormdataMapper.getMaxFormCode(prefix);
        int nextSeq = 1;
        if (!Strings.isNullOrEmpty(maxFormCode)) {
            Matcher matcher = FORM_CODE_PATTERN.matcher(maxFormCode);
            if (matcher.matches()) {
                nextSeq = Integer.parseInt(matcher.group(1)) + 1;
            }
        }
        return prefix + String.format(FORM_CODE_FORMAT, nextSeq);
    }
}
