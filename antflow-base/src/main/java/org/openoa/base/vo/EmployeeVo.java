package org.openoa.base.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.entity.Department;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

/**
 * 员工信息
 * 此表为员工信息表的vo表,仅作demo使用,字段核心是id和名字,其它字段主要用于在审批流业务展示员工头像等个性信息,非核心业务
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_NULL)
public class EmployeeVo implements Serializable {

    private Integer page;
    private Integer pageSize;

    private Integer id;
    private String uuid;
    private Date createTime;
    private Date updateTime;
    /**
     * 用户名
     */
    private String username;
    /**
     * 原用户名
     */
    private String origUsername;
    /**
     * 直属领导
     */
    private Integer leaderId;
    /**
     * 姓名
     */
    private String name;

    /**
     * "left"左边%
     * "right"右边%
     * "default"两边%
     */
    private String nameLikeMode;
    /**
     * 工号
     */
    private String jobNum;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 电话
     */
    private String mobile;
    /**
     * 岗位
     */
    private String position;

    /**
     * 入职时间
     */
    private Date joinTime;
    private String joinTimeStr;
    /**
     * 离职时间
     */
    private Date leftTime;
    private String leftTimeStr;
    /**
     * 是否已删除
     */
    private Integer isDel;
    /**
     * 担保金额
     */
    private BigDecimal guaranteeAmount;
    /**
     * 已使用金额
     */
    private BigDecimal usedAmount;
    /**
     * 任职状态（0-待入职；1-未入职；2-待转正；3-在职；4-待离职；5-已离职）
     */
    private Integer status;
    private List<Integer> statusList;
    /**
     * 用工类型/用工状态（0-正式与员工；1-使用员工；2-实习生；3-外包人员；4-兼职）
     */
    private Integer recruitType;
    private List<Integer> recruitTypeList;
    /**
     * 试用期（单位：月）
     */

    private Integer probation;
    /**
     * HRBP类型员工的pk
     */
    private Integer hrbpId;

    private List<Integer> hrbpPermissionIds;
    /**
     * 职位表pk
     */
    private Long jobId;
    /**
     * 办公地点id
     */
    private Long officeLocationId;
    /**
     * 职级表pk
     */
    private Long jobLevelId;
    /**
     * 合同主体/录用公司
     */
    private Long contractBody;
    /**
     * 合同开始时间
     */
    private Date contractStartTime;
    private String contractStartTimeStr;
    /**
     * 合同结束时间
     */
    private Date contractEndTime;
    private String contractEndTimeStr;
    /**
     * 照片路径
     */
    private String photoPath;
    /**
     * 出生日期
     */
    private Date birthday;
    private String birthdayStr;
    /**
     * 性别（0-男；1-女）
     */
    private Integer sex;
    /**
     * 学历（0-初中；1-高中；2-中技；3-中专；4-大专；5-本科；6-硕士；7-MBA/EMBA；8-博士；9-博士后；10-其他）
     */
    private Integer education;
    /**
     * 婚姻状态（0-已婚；1-未婚；2-离异；3-保密；4-未知）
     */
    private Integer maritalStatus;
    /**
     * 政治面貌（0-党员；1-团员；2-群众；3-其他）
     */
    private Integer politicalStatus;
    /**
     * 民族（0-汉族）
     */
    private Integer nation;
    /**
     * 身份证号码
     */
    private String idNo;
    /**
     * 护照
     */
    private String passport;
    /**
     * 籍贯
     */
    private String placeOrigin;
    /**
     * 专业
     */
    private String specialities;
    /**
     * 毕业院校
     */
    private String graduationInstitutions;
    /**
     * 家庭住址
     */
    private String address;
    /**
     * 家庭电话
     */
    private String tel;
    /**
     * 工位编号
     */
    private String workplaceNo;
    /**
     * 部门描述（保存人员对应部门的描述；部门层级索引；多个部门以","隔开）
     */
    private String departmentName;
    /**
     * 描述信息
     */
    private String remark;
    /**
     * 创建人（邮箱前缀）
     */
    private String createUser;
    /**
     * 更新人（邮箱前缀）
     */
    private String updateUser;
    /**
     * qq号
     */
    private String qq;
    /**
     * 头像
     */
    private String headImg;
    /**
     * 是否显示手机号
     */
    private Boolean mobileIsShow;
    /**
     * 邮编
     */
    private String postcode;
    /**
     * 微信
     */
    private String weixin;
    /**
     * 外包主体
     */
    private Integer outsourcingBody;
    /**
     * 工作年限
     */
    private Integer workLife;
    /**
     * 工资卡号
     */
    private String wageCardNum;
    /**
     * 开户行
     */
    private String bankName;

    /*外部字段*/
    /**
     * 员工关联部门
     */
    private List<Integer> departmentIds;
    /**
     * 员工部门
     */
    private Integer departmentId;
    /**
     * 部门
     */
    private List<Department> departments;
    /*翻译字段*/
    /**
     * 职位名称
     */
    private String jobName;
    /**
     * 职级名称
     */
    private String jobLevelName;
    /**
     * 直属领导姓名
     */
    private String leaderName;
    /**
     * 婚姻状态名称
     */
    private String maritalStatusName;
    /**
     * 政治面貌名称
     */
    private String politicalStatusName;
    /**
     * 民族名称
     */
    private String nationName;
    /**
     * 用工状态名称
     */
    private String recruitTypeName;
    /**
     * 任职状态名称
     */
    private String positionStatusName;
    /**
     * HRBP姓名
     */
    private String hrbpName;
    /**
     * 证件类型名称
     */
    private String idTypeName;
    /**
     * 合同主体名称
     */
    private String contractBodyName;
    /**
     * 外包主体名称
     */
    private String outsourcingBodyName;
    /**
     * 学历名称
     */
    private String educationName;
    /**
     * 性别名称
     */
    private String sexName;
    /**
     * 用工状态名称
     */
    private String statusName;
    /**
     * 流程状态
     */
    private Integer procStatus;
    /**
     * 任职状态
     */
    private Integer positionStatus;
    /**
     * 申请离职时间
     */
    private Date applyLeftTime;
    /**
     * 入职日期起始区间
     */
    private String joinStartTime;
    /**
     * 入职日期结束区间
     */
    private String joinEndTime;
    /**
     * 离职日期起始区间
     */
    private String leftStartTime;
    /**
     * 离职日期结束区间
     */
    private String leftEndTime;
    /**
     * 生日起始区间
     */
    private String birthdayStart;
    /**
     * 生日结束区间
     */
    private String birthdayEnd;
    /**
     * 五险一金缴纳地
     */
    private String payPlace;
    /**
     * 薪资信息其他说明
     */
    private String salaryRemark;
    /**
     * 证件类型（0：身份证，1：护照）
     */
    private Integer idType;
    /**
     * 转正时间
     */
    private Date promotionTime;
    /**
     * 是否显示流程
     */
    private Boolean isDisplayProcess;
    /**
     * 工位号
     */
    private String seatNum;
    /**
     * 工址
     */
    private String seatAddressName;
    /**
     * 座机号
     */
    private String seatPhone;
    /**
     * 部门路径 中文
     */
    private String departmentPath;
    /**
     * 部门路径 主键
     */
    private String departmentPathId;
    /**
     * 预计转正日期
     */
    private Date expectPromotionTime;
    /**
     * 实际转正日期
     */
    private Date actualPromotionTime;
    /**
     * 生日是否为空
     */
    private Integer birthdayNotNull;
    /**
     * 紧急联系人姓名
     */
    private String emergencyContact;
    /**
     * 紧急联系人电话
     */
    private String emergencyContactPhone;

    //工作地 办公地点id
    //private Long officeLocationId;
    private String officeLocationName;
    private String officeLocationCity;
    private List<Long> officeLocationIds;

    //员工的部门负责人id
    private Integer curDepartmentLeaderId;

    //员工的部门负责人
    private String curDepartmentLeaderName;

    /**
     * 转正申请表中的主键，查看转正流程使用
     */
    private Long promotionBusinessId;
    /**
     * 是否发起转正流程
     * 0，未发起转正流程；1，已发起转正流程
     */
    private Integer isShowPromotion;

    /**
     * 转正流程标识
     * 0-旧数据；1-重构数据
     */
    private Integer promotionDataMark;

    /**
     * 转正流程编号
     * 转正流程前缀+_+promotionBusinessId
     */
    private String promotionProcessNum;

    /**
     * 是否显示离职链接
     * 0，不显示；1，显示
     */
    private Integer isShowLeftLink;
    /**
     * 是否已经发起部门调动，并在流程中（同时信息调整流程也要互斥，不能同时发起）
     * 0，不显示（已经在流程中）；1，显示（不在流程中）
     */
    private Integer isShowChangeDept;
    /**
     * 是否有异动流程的权限（由于职级判断权限）
     * 0，不显示（无权限）；1，显示（有权限）
     */
    private Integer isAudChangeDept;

    /**
     * 是否显示员工转编入口
     * 0，不显示（已经在流程中）；1，显示（不在流程中）
     */
    private Integer isShowReassignment;

    private Long leftBusinessId;
    //离职流程processNum
    private String leftJobProcessNum;
    //离职流程processKey
    private String leftJobProcessKey;

    private Integer loginUserId;
    /**
     * 招聘渠道（数据字典编号"DIC_ZPQD"）
     */
    private Integer recruitChannels;
    private String recruitChannelsStr;
    /**
     * 服务主体/供职主体
     */
    private Long serviceBody;
    /**
     * 服务主体/供职主体
     */
    private String serviceBodyName;
    /**
     * 开户行id（银行编码配置表主键）
     */
    private Long bankId;
    //开户行id对应开户行
    private String bankStr;
    //合同起止日期字符串
    private String contractBeginToEndStr;

    //员工信息编辑中有一个edit方法，多个地方使用，
    // 而只有在编辑员工调用时才进行
    // 监控（将变动修改放入record表），
    // 设置这个标志位1（不为null）才进行监控
    private Integer employeeChangeRecordType;

    /**
     * 是否已经发起信息调整，并在流程中（同时部门调整流程也要互斥，不能同时发起）
     * 0，不显示（已经在流程中）；1，显示（不在流程中）
     */
    private Integer isShowPersonnelAdjust;
    /**
     * 是否有信息调整的权限（由于职级判断权限）
     * 0，不显示（无权限）；1，显示（有权限）
     */
    private Integer isAudPersonnelAdjust;
    /**
     * 英文名
     */
    private String englishName;
    /**
     * 是否已经发起薪资调整，并在流程中
     * 0，不显示（已经在流程中）；1，显示（不在流程中）
     */
    private Integer isShowChangeSalary;
    /**
     * 是否有薪资调整的权限（由于职级判断权限）
     * 0，不显示（无权限）；1，显示（有权限）
     */
    private Integer isAudChangeSalary;

    /**
     * 是否已经发起员工离职，并在流程中
     * 0，不显示（已经在流程中）；1，显示（不在流程中）
     */
    private Integer isShowEmployeeLeft;
    /**
     * 发起员工离职流程key
     */
    private String employeeLeftProcessKey;

    /**
     * 发起员工离职流程编号
     */
    private String employeeLeftProcessNum;

    /**
     * 企业微信员工userid
     */
    private String qywxUserId;

    /**
     * 毕业时间
     */
    private Date graduationTime;

    /**
     * 户口所在地
     */
    private String registeredPlace;

    /**
     * 户口性质
     */
    private Integer registeredNature;

    /**
     * 户口性质名称翻译
     */
    private String registeredNatureName;

    /**
     * 员工年龄
     */
    private Integer age;

    /**
     * 司龄(入职天数)
     */
    private String joinDate;

    /**
     *离职原因
     */
    private String leftReason;

    /**
     * 离职原因名称翻译
     */
    private String leftReasonName;

    /**
     * 是否无固定期限：0：否、1：是
     */
    private Integer isNotFixedDeadline;


    //=====================================================
    private String path;
    //汇报链长度
    private Integer pathLength;

    private List<Integer> douStatus;

    private List<Integer> employeeIds;

    private List<Integer> notEmployeeIds;

    //=======================================

    private String oneDept;
    private String twoDept;
    private String threeDept;
    private String fourDept;
    private String fiveDept;
    private String sixDept;
    private String sevenDept;

    /**
     * 记录排序规则
     * 1,按工号递增排序，空的工号在最后，多个空的工号按照创建时间倒序排序
     */
    private Integer order;

    /**
     * 0,in条件不包括子部门；1，in包括子部门；
     */
    private Integer inChildDept;

    /*
    page条件
     */
    //是否分页，true分页，false不分页
    private Boolean isPage;
    //如果分页，总数据条数
    private Integer totalCount;

    private List<String> tests;

    private String code;

    /**
     * 公积金基数
     */
    private String accumulationFund;

    /**
     * 社保基数
     */
    private String socialSecurity;
    /**
     * 五险一金缴纳地
     */
    private String payTo;

    private List<Integer> ids;

    private Integer employeeId;
    // 是否可选，0，可选；1，不可选
    private Integer disabled;
    // 批量异动的标识 1，部门调动流程；2，信息调整流程；3，薪资变动流程
    private Integer adjustIndex;

    //人员的列表
    private List<EmployeeNameVo> employeeList;

    //列表选中标识(1-列表选中；2-全部数据反选)
    private Integer checkMarker;

    private DepartmentVo departmentInfo;

    /**
     * 是否高亮。0，不高亮；1，高亮（null默认）
     */
    private Integer isHighlight;
    /**
     * 高亮名字
     */
    private String highlightName;
    /**
     * 领导工号
     */
    private String leaderNum;

    /**
     * 实际工作地
     */
    private String realityOfficeSpace;

    //=======================================

    /**
     * 是否编辑员工异动信息
     */
    private Boolean isEditEmployeeChangeCompDetail = false;

    /**
     * 异动类型
     */
    private Integer type;

    /**
     * 调整原因
     */
    private Integer changeReason;

    /**
     * 流程编号
     */
    private String processNum;

    //=======================================

    /**
     * 其他补贴 补贴类型
     * 补贴类型选项：电脑补贴、话费补贴、外派补贴。
     */
    private Integer otherAllowanceType;

    /**
     * 其他补贴 补贴类型 名称翻译
     */
    private String otherAllowanceTypeName;

    /**
     * 其他补贴 补贴时间段 起止日期
     * 选择电脑补贴、话费补贴时，补贴时间段不需填写；选择外派补贴时，补贴时间段为必填项。
     * 补贴时间段：仅支持选择年月
     */
    private Date otherAllowancePeriodBegin;
    private Date otherAllowancePeriodEnd;
    /**
     * 职位序列
     */
    private Long serial;
    /**
     * 职位序列名称
     */
    private String serialName;
    /**
     * 职类名称
     */
    private String typeStr;
    /**
     * 职位编码
     */
    private String jobCode;
    /**
     * 业务类型 0或者空代表 报销常用联系人    1 会议室常用联系人
     */
    private String businessType;
    /**
     * 离职流程新老版本的标识 1：老流程，2：新流程
     */
    private Integer processVersion;
    /**
     * formCode
     */
    private String formCode;
}
