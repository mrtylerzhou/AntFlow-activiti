## antflow快速集成到已有系统之审批条件定制

条件是antflow动态决策流程分支走向的关键模块。涉及到条件参数管理、条件判断器、条件分支决策器、条件节点适配器、前后端数据转换条件数据存储等。

需要完全了解全部代码需要花些时间,好在antflow有强大的策略扩展机制。因此不需要完全弄懂全部的代码，只需要实现关键步骤即可快速扩展定制想要的功能。

参考下面配置


```java
       CONDITION_TYPE_DEPARTMENT_NAME(39 // 注意这个必须和前端一样
                                      , "部门名称", "departmentName"  //前端传入的字段名
                                      , 2, String.class,// 字段类型
           BpmnNodeConditionsEmptyAdp.class  // 使用默认就行
           ,BpmnStartConditionsVo.class, // 需要往这个里面加入需要判断字段（例如：departmentName）
            "departmentName", //存储数据库里面的字段名 
            DepartmentNameJudge.class //需要自己创建的类  里面进行判断
                                     ),
   
```

1. 第一项是code，不能和已有的重复
2. 第二项是描述，简要描述这个条件做什么的，方便日后维护
3. 字段名，这个是页面请求时传入的字段名，页面传入进来的时候是通过BusinessDataVo,最终会拷贝到BpmnStartConditionsVo对象里面（需要自己拷贝）,需要在DIY工作流的service里面的org.openoa.base.interf.FormOperationAdaptor#previewSetCondition和org.openoa.base.interf.FormOperationAdaptor#launchParameters都拷贝，分别对应就和预览和流程发起
4. fieldType 1.集合，2.对象。集合即为数组类型，包含一组元素。对象即为单个值对象，String，Integer甚至复杂class类都是对象
5. fieldCls,字段的类。上面是字段的类型。只是粗略区分为集合和对象，定性。这里要给出他的具体类型，反射获取值和设置值需要使用。
6. 条件字段扩展适配Class，使用默认的BpmnNodeConditionsEmptyAdp即可。
7. 对比对象，一般是BpmnStartConditionsVo
8. 存储在数据库里的字段名，存储在数据库里的字段名
9. 条件比较器，参照ThirdAccountJudge和AskLeaveJudge，其中AskLeaveJudge高度抽象只需要提供前端传入参数名称和数据库实体字段名称即可。

> 上面3和8配置的字段是需要在BpmnStartConditionsVo和BpmnNodeConditionsConfBaseVo里面加的。并不是只需要在枚举里指定一下就行了。原理就是动态从实体中获取或者设置指定名字的字段值。
>
> 需要说明的是，以上说到前端传入的名称和数据库的名字。这里数据库里的名字并非指存入数据库的字段名。存入数据库字段名对用户来说是封装起来的，用户也不需要添加数据库字段。只需要定一个参数名字即可。
>
> 前端传入参数名是指BpmnStartConditionsVo实体里的字段名。上面说过，这里有一个拷贝的过程，前端传入的参数是在BusinessDataVo里面。字段名在这两个实体里并不一定要一样，但是尽量保持一样是一个好的做法。
>
> 数据库字段名是批BpmnNodeConditionsConfBaseVo里面定义的字段，从这就可以看出来他其实和数据库里字段的名称没有关系。它只是个Vo，名字可以任意命。存储在数据库里的模板条件值会赋值给它。
