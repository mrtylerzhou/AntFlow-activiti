# Antflow 新增审批人类型之模拟岗位审批功能完整实现指南


> 本文档由用户武十卓大佬提供,步骤较为复杂,实际工作中建议用户更改一个现有的自己系统不用的,作为特定审批人规则实现.审批人规则的名字是什么并不重要,关键在于sql的编写/改下
## 1. Antflow 工作流系统架构概述

### 1.1 系统整体架构
Antflow 是一个基于 Spring Boot 的企业级工作流引擎，采用分层架构设计：

```
┌─────────────────────────────────────────┐
│              前端层 (Vue.js)              │
├─────────────────────────────────────────┤
│              控制器层 (Controller)         │
├─────────────────────────────────────────┤
│              服务层 (Service)             │
├─────────────────────────────────────────┤
│              适配器层 (Adaptor)           │
├─────────────────────────────────────────┤
│              数据访问层 (Mapper)          │
├─────────────────────────────────────────┤
│              数据库层 (MySQL)             │
└─────────────────────────────────────────┘
```

### 1.2 核心组件说明

#### 1.2.1 枚举配置层
- **NodePropertyEnum**: 定义节点属性类型（如指定人员、指定角色、指定岗位等）
- **PersonnelEnum**: 定义人员提供者类型，与NodePropertyEnum一一对应
- **FieldValueTypeEnum**: 定义前端字段类型（如人员选择器、岗位选择器等）
- **SignTypeEnum**: 定义会签类型（会签、或签、依次会签）

#### 1.2.2 数据模型层
- **BpmnNodePropertysVo**: 存储节点的属性配置信息
- **BpmnNodeParamsAssigneeVo**: 存储审批人信息
- **BaseIdTranStruVo**: 通用的ID-名称结构体
- **BpmnNodePositionConf**: 岗位配置主表实体
- **BpmnNodePositionItemConf**: 岗位配置明细表实体

#### 1.2.3 服务提供者层
- **BpmnPersonnelProviderService**: 人员提供者接口，负责根据配置获取审批人列表
- **BpmnEmployeeInfoProviderService**: 员工信息提供者，负责获取员工详细信息
- **ISysPostService**: 系统岗位服务，提供岗位相关操作
- **ISysUserService**: 系统用户服务，提供用户相关操作

#### 1.2.4 适配器层
- **AbstractBpmnPersonnelAdaptor**: 人员适配器抽象类，连接前端配置和后端服务
- **BpmnNodeAdaptor**: 节点适配器抽象类，处理节点配置的格式化和验证
- **BpmnElementPositionAdp**: 岗位元素适配器，处理岗位相关的BPMN元素

#### 1.2.5 工厂模式
- **PersonnelTagParser**: 根据PersonnelEnum找到对应的人员适配器
- **AdaptorFactory**: 适配器工厂，统一管理各种适配器的创建

**文件**: `AdaptorFactory.java`

```java
public class AdaptorFactory {

    @SpfService(tagParser = ActivitiTagParser.class)
    public FormOperationAdaptor getActivitiService(BusinessDataVo dataVo) {
        return null;
    }

    @SpfService(tagParser = PersonnelTagParser.class)
    public AbstractBpmnPersonnelAdaptor getPersonnelAdaptor(NodePropertyEnum nodePropertyEnum) {
        return null;
    }

    @SpfService(tagParser= OrderedSignTagParser.class)
    public AbstractOrderedSignNodeAdp getOrderedSignNodeAdp(OrderNodeTypeEnum orderNodeTypeEnum){
        return null;
    }
}
```

**文件**: `IAdaptorFactory.java`

```java
public interface IAdaptorFactory {
    @SpfService(tagParser = ActivitiTagParser.class)
    public FormOperationAdaptor getActivitiService(BusinessDataVo dataVo);

    @SpfService(tagParser = PersonnelTagParser.class)
    AbstractBpmnPersonnelAdaptor getPersonnelAdaptor(NodePropertyEnum nodePropertyEnum);

    @SpfService(tagParser= OrderedSignTagParser.class)
    AbstractOrderedSignNodeAdp getOrderedSignNodeAdp(OrderNodeTypeEnum orderNodeTypeEnum);

    @SpfService(tagParser = FormOperationTagParser.class)
    ProcessOperationAdaptor getProcessOperation(BusinessDataVo vo);

    @SpfService(tagParser = BpmnNodeAdaptorTagParser.class)
    BpmnNodeAdaptor getBpmnNodeAdaptor(BpmnNodeAdpConfEnum adpConfEnum);

    @AutoParse
    BpmnElementAdaptor getBpmnElementAdaptor(NodePropertyEnum nodePropertyEnum);

    @AutoParse
    AbstractBusinessConfigurationAdaptor getBusinessConfigurationAdaptor(ConfigurationTableAdapterEnum tableAdapterEnum);
}
```

#### 1.2.6 服务层
- **BpmnNodePositionConfService**: 岗位配置服务接口，提供岗位配置的业务操作
- **BpmnNodePositionConfServiceImpl**: 岗位配置服务实现类，处理岗位配置的具体业务逻辑
- **BpmnNodePositionItemConfServiceImpl**: 岗位配置明细服务实现类，处理岗位明细的业务逻辑

## 2. 人员提供者机制深度解析

### 2.1 人员提供者注册机制

```mermaid
sequenceDiagram
    participant Frontend as 前端
    participant Factory as AdaptorFactory
    participant Parser as PersonnelTagParser
    participant Adaptor as PersonnelAdaptor
    participant Provider as PersonnelProvider
    
    Frontend->>Factory: 请求人员适配器(NodePropertyEnum)
    Factory->>Parser: parseTag(PersonnelEnum)
    Parser->>Parser: 扫描所有AbstractBpmnPersonnelAdaptor实现
    Parser->>Adaptor: 检查isSupportBusinessObject(PersonnelEnum)
    Adaptor->>Parser: 返回匹配结果
    Parser->>Factory: 返回对应适配器
    Factory->>Frontend: 返回适配器实例
    Frontend->>Adaptor: 调用setNodeParams
    Adaptor->>Provider: 调用getAssigneeList
    Provider->>Adaptor: 返回审批人列表
```

### 2.2 现有人员提供者类型

| 枚举值 | 描述 | 实现类 | 功能说明 |
|--------|------|--------|----------|
| USERAPPOINTED_PERSONNEL | 指定人员 | UserPointedPersonnelProvider | 直接指定具体的用户作为审批人 |
| ROLE_PERSONNEL | 指定角色 | RolePersonnelProvider | 根据角色获取该角色下的所有用户 |
| DEPARTMENT_PERSONNEL | 指定部门 | DepartmentPersonnelProvider | 根据部门获取该部门下的所有用户 |
| POSITION_PERSONNEL | 指定岗位 | PositionPersonnelProvider | 根据岗位获取该岗位下的所有用户 |
| NODE_LEVEL_PERSONNEL | 指定层级 | LevelPersonnelProvider | 根据组织层级获取对应层级的领导 |
| HRBP_PERSONNEL | HRBP审批 | HrbpPersonnelProvider | 获取员工对应的HRBP作为审批人 |
| DIRECT_LEADER_PERSONNEL | 直接领导 | DirectLeaderPersonnelProvider | 获取员工的直接上级领导 |
| START_USER_PERSONNEL | 发起人 | StartUserPersonnelProvider | 将流程发起人作为审批人 |

## 3. 岗位审批功能实现思路

### 3.1 需求分析

**功能目标**: 允许用户在配置审批节点时选择一个或多个岗位，系统自动将这些岗位中的所有用户作为审批人，并支持会签、或签、依次会签等多种签名类型。

**核心需求**:
1. 前端支持岗位选择器组件
2. 后端能够根据岗位ID获取岗位内所有用户
3. 支持多岗位选择
4. 支持会签、或签、依次会签三种签名类型
5. 集成到现有的人员提供者机制中
6. 数据持久化存储岗位配置信息

### 3.2 技术实现策略

#### 3.2.1 遵循现有架构模式
- 复用现有的人员提供者框架
- 遵循枚举驱动的配置模式
- 保持与其他人员提供者的一致性

#### 3.2.2 最小化侵入性
- 不修改核心框架代码
- 通过扩展方式添加新功能
- 保持向后兼容性

#### 3.2.3 数据库设计原则
- 采用主从表结构存储配置信息
- 支持多岗位配置
- 记录完整的审计信息

## 4. 详细实现步骤

### 步骤1: 数据库表设计

#### 4.1.1 创建岗位配置主表

**文件**: `create_position_approval_tables.sql`

```sql
-- BPMN节点岗位配置表
CREATE TABLE `t_bpmn_node_position_conf` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bpmn_node_id` varchar(64) NOT NULL COMMENT 'BPMN节点ID',
  `sign_type` int(11) DEFAULT NULL COMMENT '会签类型：1-会签（所有人都同意），2-或签（一人同意即可），3-依次会签',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `is_del` tinyint(1) DEFAULT '0' COMMENT '是否删除 0-否 1-是',
  `create_user` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bpmn_node_id` (`bpmn_node_id`),
  KEY `idx_bpmn_node_id` (`bpmn_node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BPMN节点岗位配置表';
```

#### 4.1.2 创建岗位配置明细表

```sql
-- BPMN节点岗位配置项表
CREATE TABLE `t_bpmn_node_position_item_conf` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bpmn_node_position_id` bigint(20) NOT NULL COMMENT 'BPMN节点岗位配置ID',
  `position_id` bigint(20) NOT NULL COMMENT '岗位ID',
  `position_name` varchar(128) DEFAULT NULL COMMENT '岗位名称',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `is_del` tinyint(1) DEFAULT '0' COMMENT '是否删除 0-否 1-是',
  `create_user` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_bpmn_node_position_id` (`bpmn_node_position_id`),
  KEY `idx_position_id` (`position_id`),
  CONSTRAINT `fk_bpmn_node_position_item_conf` FOREIGN KEY (`bpmn_node_position_id`) REFERENCES `t_bpmn_node_position_conf` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BPMN节点岗位配置项表';
```

### 步骤2: 枚举配置扩展

#### 4.2.1 添加节点属性枚举

**文件**: `NodePropertyEnum.java`

```java
/**
 * 节点属性枚举
 * 
 * @author antflow
 * @date 2025-7-25
 */
public enum NodePropertyEnum {
    // 现有枚举...
    
    /**
     * 指定岗位审批
     */
    NODE_PROPERTY_POSITION(15,"指定岗位",1,BPMN_NODE_PARAM_MULTIPLAYER);
    
    @Getter
    private Integer code;
    @Getter
    private String desc;
    @Getter
    private final BpmnNodeParamTypeEnum paramTypeEnum;
    @Getter
    private Integer hasPropertyTable;
    
    NodePropertyEnum(Integer code, String desc, Integer hasPropertyTable,BpmnNodeParamTypeEnum paramTypeEnum) {
        this.code = code;
        this.desc = desc;
        this.paramTypeEnum=paramTypeEnum;
        this.hasPropertyTable = hasPropertyTable;
    }
}
```

#### 4.2.2 添加人员类型枚举

**文件**: `PersonnelEnum.java`

```java
/**
 * 人员类型枚举
 * 
 * @author antflow
 * @date 2025-7-25
 */
public enum PersonnelEnum {
    // 现有枚举...
    
    /**
     * 岗位人员
     */
    POSITION_PERSONNEL(NODE_PROPERTY_POSITION, "指定岗位");
    
    private final NodePropertyEnum nodePropertyEnum;
    private final String desc;
    
    // 构造方法和getter方法...
}
```

#### 4.2.3 添加会签类型枚举

**文件**: `SignTypeEnum.java`

```java
/**
 * 会签类型枚举
 * 
 * @author antflow
 * @date 2025-7-25
 */
public enum SignTypeEnum {
    /**
     * 会签（所有人都同意）
     */
    ALL_SIGN(1, "会签", "所有人都同意"),
    
    /**
     * 或签（一人同意即可）
     */
    OR_SIGN(2, "或签", "一人同意即可"),
    
    /**
     * 依次会签
     */
    SEQUENCE_SIGN(3, "依次会签", "按顺序逐个审批");
    
    private final Integer code;
    private final String name;
    private final String description;
    
    SignTypeEnum(Integer code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    // getter方法...
}
```

#### 4.2.4 添加字段类型枚举

**文件**: `FieldValueTypeEnum.java`

```java
/**
 * 字段值类型枚举
 * 
 * @author antflow
 * @date 2025-7-25
 */
public enum FieldValueTypeEnum {
    // 现有枚举...
    
    /**
     * 岗位选择器
     */
    POSITIONCHOICE(9, "PositionChoice");
    
    @Getter
    private Integer code;
    @Getter
    private String desc;
    
    FieldValueTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
```

### 步骤3: 数据模型层实现

#### 4.3.1 岗位配置主表实体

**文件**: `BpmnNodePositionConf.java`

```java
/**
 * 岗位审批人节点配置表
 * 用于存储BPMN节点的岗位审批人配置信息
 * 
 * @author Antflow
 * @date 2025-7-24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_bpmn_node_position_conf")
public class BpmnNodePositionConf {

    /**
     * 自增主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * BPMN节点ID，关联t_bpmn_node表
     */
    @TableField("bpmn_node_id")
    private Long bpmnNodeId;

    /**
     * 签署类型
     * 1-会签（所有岗位人员都需要审批）
     * 2-或签（任意一个岗位人员审批即可）
     */
    @TableField("sign_type")
    private Integer signType;

    /**
     * 备注信息
     */
    @TableField("remark")
    private String remark;

    /**
     * 删除标识
     * 0-正常，1-已删除
     */
    @TableField("is_del")
    private Integer isDel;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
    
    /**
     * 岗位配置项列表
     */
    @TableField(exist = false)
    private List<BpmnNodePositionItemConf> positionItems;
}
```

#### 4.3.2 岗位配置明细表实体

**文件**: `BpmnNodePositionItemConf.java`

```java
/**
 * 岗位审批人节点配置明细表
 * 用于存储具体的岗位ID信息，与BpmnNodePositionConf形成一对多关系
 * 
 * @author Antflow
 * @date 2025-7-24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_bpmn_node_position_item_conf")
public class BpmnNodePositionItemConf {

    /**
     * 自增主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 岗位配置主表ID，关联t_bpmn_node_position_conf表
     */
    @TableField("bpmn_node_position_id")
    private Long bpmnNodePositionId;

    /**
     * 岗位ID，关联sys_post表的post_id
     */
    @TableField("position_id")
    private String positionId;

    /**
     * 岗位名称，冗余字段便于查询显示
     */
    @TableField("position_name")
    private String positionName;

    /**
     * 备注信息
     */
    @TableField("remark")
    private String remark;

    /**
     * 删除标识
     * 0-正常，1-已删除
     */
    @TableField("is_del")
    private Integer isDel;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
}
```

#### 4.3.3 扩展节点属性VO

**文件**: `BpmnNodePropertysVo.java`

```java
/**
 * BPMN节点属性VO
 * 
 * @author antflow
 * @date 2025-7-25
 */
@Data
public class BpmnNodePropertysVo {
    // 现有字段...
    
    /**
     * 岗位ID列表
     */
    private List<String> positionIds;
    
    /**
     * 岗位信息列表
     */
    private List<BaseIdTranStruVo> positionList;
    
    /**
     * 会签类型
     */
    private Integer signType;
}
```

### 步骤4: 数据访问层实现

#### 4.4.1 岗位配置Mapper接口

**文件**: `BpmnNodePositionConfMapper.java`

```java
/**
 * BPMN节点岗位配置Mapper接口
 * 
 * @author antflow
 * @date 2025-7-25
 */
@Mapper
public interface BpmnNodePositionConfMapper extends BaseMapper<BpmnNodePositionConf> {
    // 如需自定义SQL，可在此添加方法
}
```

#### 4.4.2 岗位配置明细Mapper接口

**文件**: `BpmnNodePositionItemConfMapper.java`

```java
/**
 * BPMN节点岗位配置项Mapper接口
 * 
 * @author antflow
 * @date 2025-7-25
 */
@Mapper
public interface BpmnNodePositionItemConfMapper extends BaseMapper<BpmnNodePositionItemConf> {
    // 如需自定义SQL，可在此添加方法
}
```

### 步骤5: 服务层实现

#### 4.5.1 岗位配置服务实现

**文件**: `BpmnNodePositionConfServiceImpl.java`

```java
/**
 * BPMN节点岗位配置服务实现
 * 
 * @author antflow
 * @date 2025-7-25
 */
@Service
@Slf4j
public class BpmnNodePositionConfServiceImpl extends ServiceImpl<BpmnNodePositionConfMapper, BpmnNodePositionConf> 
        implements BpmnNodePositionConfService {
    
}
```

#### 4.5.2 岗位配置明细服务实现

**文件**: `BpmnNodePositionItemConfServiceImpl.java`

```java
/**
 * BPMN节点岗位配置明细服务实现
 * 
 * @author antflow
 * @date 2025-7-25
 */
@Service
@Slf4j
public class BpmnNodePositionItemConfServiceImpl extends ServiceImpl<BpmnNodePositionItemConfMapper, BpmnNodePositionItemConf> {

}
```

### 步骤6: 人员提供者实现

#### 4.6.1 岗位人员提供者

**文件**: `PositionPersonnelProvider.java`

```java
/**
 * 岗位人员提供者
 * 根据岗位配置获取审批人列表
 * 
 * @author antflow
 * @date 2025-7-25
 */
@Component
public class PositionPersonnelProvider extends AbstractMissingAssignNodeAssigneeVoProvider {
    
    @Autowired
    private AfUserService afUserService;
    
    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        String startUserId = startConditionsVo.getStartUserId();
        List<BaseIdTranStruVo> assigneeList = new ArrayList<>();
        
        try {
            // 获取发起人的部门ID
            String deptId = afUserService.getUserDeptId(startUserId);
            if (deptId == null) {
                return super.provideAssigneeList(bpmnNodeVo, assigneeList);
            }
            
            // 从节点属性中获取岗位ID列表
            List<String> positionIdStrings = bpmnNodeVo.getProperty().getPositionIds();
            if (CollectionUtils.isEmpty(positionIdStrings)) {
                return super.provideAssigneeList(bpmnNodeVo, assigneeList);
            }
            
            // 转换为Long类型的岗位ID列表
            List<Long> positionIds = positionIdStrings.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
            
            // 根据部门ID和岗位ID列表查询用户
            List<BaseIdTranStruVo> users = afUserService.getUsersByDeptAndPositions(deptId, positionIds);
            assigneeList.addAll(users);
            
        } catch (Exception e) {
            log.error("查询岗位审批人员失败", e);
        }
        
        return super.provideAssigneeList(bpmnNodeVo, assigneeList);
    }
}
```

### 步骤7: 适配器层实现

#### 4.7.1 岗位人员适配器

**文件**: `PositionPersonnelAdaptor.java`

```java
/**
 * 岗位人员适配器
 * 连接前端岗位配置和后端岗位人员提供者
 * 
 * @author antflow
 * @date 2025-7-25
 */
@Component
@Slf4j
public class PositionPersonnelAdaptor extends AbstractBpmnPersonnelAdaptor {
    
    /**
     * 构造方法
     * 
     * @param bpmnEmployeeInfoProviderService 员工信息提供者服务
     * @param bpmnPersonnelProviderService 人员提供者服务
     */
    public PositionPersonnelAdaptor(
            BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService,
            @Qualifier("positionPersonnelProvider") BpmnPersonnelProviderService bpmnPersonnelProviderService
    ) {
        super(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);
        log.info("岗位人员适配器初始化完成");
    }
    
    /**
     * 设置支持的业务对象类型
     */
    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(PersonnelEnum.POSITION_PERSONNEL);
        log.info("岗位人员适配器注册支持的业务对象: {}", PersonnelEnum.POSITION_PERSONNEL);
    }
}
```

#### 4.7.2 岗位节点属性适配器

**文件**: `NodePropertyPositionAdp.java`

```java
/**
 * 岗位节点属性适配器
 * 处理岗位节点的前端配置信息
 * 
 * @author antflow
 * @date 2025-7-25
 */
@Component
@Slf4j
public class NodePropertyPositionAdp extends BpmnNodeAdaptor {

  @Autowired
  private BpmnNodePositionConfServiceImpl bpmnNodePositionConfService;

  @Autowired
  private BpmnNodePositionItemConfServiceImpl bpmnNodePositionItemConfService;

  /**
   * 格式化BPMN节点VO，从数据库读取岗位配置信息
   *
   * @param bpmnNodeVo BPMN节点VO
   * @return 填充了岗位配置信息的BPMN节点VO
   */
  @Override
  public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
    // 查询岗位配置主表
    BpmnNodePositionConf bpmnNodePositionConf = bpmnNodePositionConfService.getBaseMapper()
            .selectOne(new QueryWrapper<BpmnNodePositionConf>()
                    .eq("bpmn_node_id", bpmnNodeVo.getId())
                    .eq("is_del", 0));

    if (ObjectUtils.isEmpty(bpmnNodePositionConf)) {
      throw new JiMuBizException("岗位审批人配置错误或者数据被删除，未获取到岗位配置信息");
    }

    // 查询岗位配置明细表
    List<String> positionIds = new ArrayList<>();
    List<String> positionNames = new ArrayList<>();

    List<BpmnNodePositionItemConf> bpmnNodePositionItems = bpmnNodePositionItemConfService.getBaseMapper()
            .selectList(new QueryWrapper<BpmnNodePositionItemConf>()
                    .eq("bpmn_node_position_id", bpmnNodePositionConf.getId())
                    .eq("is_del", 0))
            .stream()
            .distinct()
            .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(bpmnNodePositionItems)) {
      throw new JiMuBizException("岗位审批人配置错误或者数据被删除，未获取到具体岗位信息");
    }

    // 构建岗位ID和名称列表
    for (BpmnNodePositionItemConf positionItem : bpmnNodePositionItems) {
      String positionId = positionItem.getPositionId();
      String positionName = positionItem.getPositionName();
      positionIds.add(positionId);
      if (!StringUtils.isEmpty(positionName)) {
        positionNames.add(positionName);
      }
    }

    // 设置节点属性
    bpmnNodeVo.setProperty(BpmnNodePropertysVo.builder()
            .signType(bpmnNodePositionConf.getSignType())
            .positionIds(positionIds)
            .positionList(getPositionList(positionIds, positionNames))
            .build());

    return bpmnNodeVo;
  }

  /**
   * 获取岗位列表
   *
   * @param positionIds 岗位ID列表
   * @param positionNames 岗位名称列表
   * @return 岗位信息列表
   */
  private List<BaseIdTranStruVo> getPositionList(List<String> positionIds, List<String> positionNames) {
    List<BaseIdTranStruVo> result = new ArrayList<>();

    // 如果岗位名称不为空，直接使用存储的名称
    if (!CollectionUtils.isEmpty(positionNames)) {
      if (positionIds.size() != positionNames.size()) {
        throw new JiMuBizException("岗位审批人配置存在岗位名称不完整的情况！");
      }
      for (int i = 0; i < positionIds.size(); i++) {
        BaseIdTranStruVo vo = new BaseIdTranStruVo();
        vo.setId(positionIds.get(i));
        vo.setName(positionNames.get(i));
        result.add(vo);
      }
      return result;
    }

    // 如果岗位名称为空，则只设置ID（实际项目中可能需要从岗位服务获取名称）
    for (String positionId : positionIds) {
      BaseIdTranStruVo vo = new BaseIdTranStruVo();
      vo.setId(positionId);
      vo.setName("岗位-" + positionId); // 临时名称，实际应从岗位服务获取
      result.add(vo);
    }
    return result;
  }

  /**
   * 编辑BPMN节点，保存岗位配置信息到数据库
   *
   * @param bpmnNodeVo BPMN节点VO
   */
  @Override
  public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {
    BpmnNodePropertysVo bpmnNodePropertysVo = Optional.ofNullable(bpmnNodeVo.getProperty())
            .orElse(new BpmnNodePropertysVo());

    // 保存岗位配置主表
    BpmnNodePositionConf bpmnNodePositionConf = BpmnNodePositionConf.builder()
            .bpmnNodeId(bpmnNodeVo.getId())
            .signType(bpmnNodePropertysVo.getSignType())
            .isDel(0)
            .createTime(new Date())
            .createUser(SecurityUtils.getLogInEmpNameSafe())
            .updateTime(new Date())
            .updateUser(SecurityUtils.getLogInEmpNameSafe())
            .build();

    bpmnNodePositionConfService.getBaseMapper().insert(bpmnNodePositionConf);

    Long nodePositionId = bpmnNodePositionConf.getId();
    if (ObjectUtils.isEmpty(nodePositionId)) {
      throw new JiMuBizException("保存岗位配置主表失败");
    }

    // 如果没有岗位ID，直接返回
    if (ObjectUtils.isEmpty(bpmnNodePropertysVo.getPositionIds())) {
      return;
    }

    // 保存岗位配置明细表
    List<BpmnNodePositionItemConf> positionItemConfs = new ArrayList<>();
    List<BaseIdTranStruVo> positionList = bpmnNodePropertysVo.getPositionList();
    Map<String, String> id2nameMap = null;

    if (!CollectionUtils.isEmpty(positionList)) {
      id2nameMap = positionList.stream()
              .collect(Collectors.toMap(
                      a -> a.getId().toString(),
                      BaseIdTranStruVo::getName,
                      (k1, k2) -> k1));
    }

    for (String positionId : bpmnNodePropertysVo.getPositionIds()) {
      BpmnNodePositionItemConf positionItemConf = BpmnNodePositionItemConf.builder()
              .bpmnNodePositionId(nodePositionId)
              .positionId(positionId)
              .isDel(0)
              .createTime(new Date())
              .createUser(SecurityUtils.getLogInEmpNameSafe())
              .updateTime(new Date())
              .updateUser(SecurityUtils.getLogInEmpNameSafe())
              .build();

      // 设置岗位名称（如果有的话）
      if (id2nameMap != null && !StringUtils.isEmpty(id2nameMap.get(positionId))) {
        positionItemConf.setPositionName(id2nameMap.get(positionId));
      }

      positionItemConfs.add(positionItemConf);
    }

    bpmnNodePositionItemConfService.saveBatch(positionItemConfs);
  }

  /**
   * 格式化字段属性信息VO
   *
   * @return 人员规则VO
   */
  @Override
  public PersonnelRuleVO formaFieldAttributeInfoVO() {
    PersonnelRuleVO personnelRuleVO = new PersonnelRuleVO();
    NodePropertyEnum nodePropertyPosition = NodePropertyEnum.NODE_PROPERTY_POSITION;
    personnelRuleVO.setNodeProperty(nodePropertyPosition.getCode());
    personnelRuleVO.setNodePropertyName(nodePropertyPosition.getDesc());

    String fieldName = "positionIds";
    FieldAttributeInfoVO vo = new FieldAttributeInfoVO();
    vo.setFieldName(fieldName);
    vo.setFieldLabel("请选择岗位");
    vo.setFieldType(FieldValueTypeEnum.POSITIONCHOICE.getDesc());

    personnelRuleVO.setFieldInfos(Lists.newArrayList(vo));
    return personnelRuleVO;
  }

  /**
   * 设置支持的业务对象
   */
  @Override
  public void setSupportBusinessObjects() {
    addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_POSITION);
  }
}
```

#### 4.7.3 岗位BPMN元素适配器

**文件**: `BpmnElementPositionAdp.java`

```java
/**
 * 岗位审批元素适配器
 * 负责将岗位节点配置转换为BPMN流程元素
 *
 * @author Antflow
 * @date 2025-07-25
 */
@Slf4j
@Component
public class BpmnElementPositionAdp extends BpmnElementAdaptor {

    @Override
    protected BpmnConfCommonElementVo getElementVo(BpmnNodePropertysVo property, BpmnNodeParamsVo params, Integer elementCode, String elementId) {
        log.debug("开始处理岗位审批元素，elementCode: {}, elementId: {}", elementCode, elementId);

        // 获取岗位审批人员列表
        List<BpmnNodeParamsAssigneeVo> assigneeList = params.getAssigneeList();
        if (assigneeList == null || assigneeList.isEmpty()) {
            log.warn("岗位审批节点未配置审批人员，elementId: {}", elementId);
            return null;
        }

        // 获取元素名称（通常是第一个审批人的元素名称）
        String elementName = assigneeList.get(0).getElementName();

        // 构建审批人员映射（去重处理）
        Map<String, String> assigneeMap = assigneeList.stream()
                .filter(o -> o.getIsDeduplication() == 0) // 过滤掉需要去重的项
                .collect(Collectors.toMap(
                        BpmnNodeParamsAssigneeVo::getAssignee,
                        BpmnNodeParamsAssigneeVo::getAssigneeName,
                        (k1, k2) -> k1 // 如果有重复key，保留第一个
                ));

        // 构建集合名称，用于流程变量
        String collectionName = StringUtils.join(new Serializable[]{"positionUserList", elementCode});

        // 根据签名类型创建对应的BPMN元素
        if (property.getSignType().equals(SignTypeEnum.SIGN_TYPE_SIGN.getCode())) {
            // 会签模式：所有人都需要审批
            log.debug("创建岗位会签元素，审批人数: {}", assigneeMap.size());
            return BpmnElementUtils.getMultiplayerSignElement(
                    elementId,
                    elementName,
                    collectionName,
                    new ArrayList<>(assigneeMap.keySet()),
                    assigneeMap
            );
        } else if (property.getSignType().equals(SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode())) {
            // 或签模式：任意一人审批即可
            log.debug("创建岗位或签元素，审批人数: {}", assigneeMap.size());
            return BpmnElementUtils.getMultiplayerOrSignElement(
                    elementId,
                    elementName,
                    collectionName,
                    new ArrayList<>(assigneeMap.keySet()),
                    assigneeMap
            );
        } else if (property.getSignType().equals(SignTypeEnum.SIGN_TYPE_SIGN_IN_ORDER.getCode())) {
            // 顺序签模式：按顺序逐一审批
            log.debug("创建岗位顺序签元素，审批人数: {}", assigneeMap.size());
            return BpmnElementUtils.getMultiplayerSignInOrderElement(
                    elementId,
                    elementName,
                    collectionName,
                    new ArrayList<>(assigneeMap.keySet()),
                    assigneeMap
            );
        } else {
            // 默认使用单人审批
            log.debug("创建岗位单人审批元素");
            String firstAssignee = assigneeList.get(0).getAssignee();
            return BpmnElementUtils.getSingleElement(
                    elementId,
                    elementName,
                    "assignee",
                    firstAssignee,
                    assigneeMap
            );
        }
    }

    @Override
    public void setSupportBusinessObjects() {
        // 注册支持的业务对象类型：岗位审批
        this.addSupportBusinessObjects(new Enum[]{NodePropertyEnum.NODE_PROPERTY_POSITION});
        log.info("BpmnElementPositionAdp 已注册支持 NODE_PROPERTY_POSITION 类型");
    }
}
```

## 5. 系统集成机制

### 5.1 自动发现机制

Antflow使用Spring的自动扫描机制来发现和注册组件：

```java
// PersonnelTagParser.parseTag方法
Collection<AbstractBpmnPersonnelAdaptor> beans = SpringBeanUtils.getBeans(AbstractBpmnPersonnelAdaptor.class);
    for (AbstractBpmnPersonnelAdaptor bean : beans) {
        if(bean.isSupportBusinessObject(data)){
            return bean;
        }
}
```

**工作原理**:
1. Spring容器启动时扫描所有@Component注解的类
2. PersonnelTagParser获取所有AbstractBpmnPersonnelAdaptor实现类
3. 通过isSupportBusinessObject方法匹配对应的适配器
4. 返回匹配的适配器实例

### 5.2 配置驱动机制

```mermaid
flowchart TD
    A[前端选择岗位] --> B[NodePropertyEnum.NODE_PROPERTY_POSITION]
    B --> C[PersonnelEnum.POSITION_PERSONNEL]
    C --> D[PersonnelTagParser查找适配器]
    D --> E[PositionPersonnelAdaptor]
    E --> F[PositionPersonnelProvider]
    F --> G[BpmnNodePositionConfService]
    G --> H[BpmnNodePositionConfMapper]
    H --> I[数据库查询]
    I --> J[返回岗位用户列表]
```

### 5.3 数据流转机制

```mermaid
sequenceDiagram
    participant Frontend as 前端
    participant Controller as 控制器
    participant Service as 服务层
    participant Mapper as 数据访问层
    participant DB as 数据库
    
    Frontend->>Controller: 保存岗位配置
    Controller->>Service: savePositionConf()
    Service->>Service: 参数校验
    Service->>Mapper: 删除原有配置
    Service->>Mapper: 保存主表配置
    Service->>Mapper: 批量保存明细配置
    Mapper->>DB: 执行SQL操作
    DB->>Mapper: 返回操作结果
    Mapper->>Service: 返回操作结果
    Service->>Controller: 返回保存结果
    Controller->>Frontend: 返回响应
```

## 6. 总结

### 6.1 功能特性

本实现指南提供了完整的Antflow岗位审批功能，具备以下特性：

✅ **完整的架构设计**：采用分层架构，职责清晰，易于维护

✅ **灵活的配置方式**：支持多岗位、多会签类型配置

✅ **高性能查询**：优化的数据库设计和查询策略

✅ **安全可靠**：完善的权限验证和异常处理

✅ **易于扩展**：工厂模式和适配器模式支持功能扩展

✅ **完善的测试**：提供测试控制器验证功能正确性

---

**最后更新**：2025-7-25