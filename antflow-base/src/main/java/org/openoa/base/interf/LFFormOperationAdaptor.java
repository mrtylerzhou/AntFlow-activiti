package org.openoa.base.interf;


import org.openoa.base.vo.UDLFApplyVo;

/**
 * 此类主要用于用特定低代码表流程的一些自定义行为
 * 如果你熟悉FormOperationAdaptor和FormOperationAdaptor不同的是,这里实现类使用普通的@Service而不是使用定制的@ActivitiServiceAnno
 * 实现类的@Service需要指定名称,需要和低代码表单的formCode一样,这个类似DIY流程@ActivitiServiceAnno里的svcName
 * 每个方法的含义查看antflow事件系统与接入模式介绍.md
 * @param <T>
 */
public interface LFFormOperationAdaptor<T extends UDLFApplyVo>  extends FormOperationAdaptor<T> {



}
