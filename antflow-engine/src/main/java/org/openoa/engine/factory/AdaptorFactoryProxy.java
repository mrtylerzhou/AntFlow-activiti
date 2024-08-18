package org.openoa.engine.factory;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.*;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.anno.AutoParse;
import org.openoa.base.util.SpringBeanUtils;

/**
 * please be noticed that is class is designed for specific business,not for general use,please do not use it for other purposes
 * if you want to generate a proxy object,you should know what you are doing,SimpleProxyFactory maybe  be suitable for your needs
 */
@Slf4j
public class AdaptorFactoryProxy {

    private AdaptorFactoryProxy() {
    }

    private static final String SIMPLECLSNAME = "IAdaptorFactory";
    private static final String PACKAGENAME = "org.openoa.engine.factory";
    /**
     * prefix for generated proxy object
     */
    private static final String PROXYFREFIX = "$Proxy";
    /**
     * suffix for generated proxy object
     */
    private static final String PROXYSUFFIX = "Impl";

    private volatile static Object loadedInstance = null;

    public static Object getProxyInstance() {
        if (loadedInstance == null) {
            synchronized (SIMPLECLSNAME) {
                try {
                    return getProxyObj();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return loadedInstance;
    }


    // generate proxy object for IAdaptorFactory dynamically
    private static Object getProxyObj() throws Exception {
        if (loadedInstance != null) {
            return loadedInstance;
        }
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(AdaptorFactoryProxy.class));
        String proxyClsName = PACKAGENAME + "." + getProxyObjectName();
        String sourceClsName = PACKAGENAME + "." + SIMPLECLSNAME;

        CtClass targetClass = pool.makeClass(proxyClsName);

        CtClass interf = pool.getCtClass(sourceClsName);
        CtClass[] interfs = new CtClass[]{interf};
        targetClass.setInterfaces(interfs);


        for (CtMethod declaredMethod : interf.getDeclaredMethods()) {

            CtClass declaredMethodReturnType = declaredMethod.getReturnType();
            CtClass[] declaredMethodParameterTypes = declaredMethod.getParameterTypes();
            String declaredMethodName = declaredMethod.getName();
            // 创建方法
            CtMethod newMethod = new CtMethod(declaredMethodReturnType, declaredMethodName, declaredMethodParameterTypes, targetClass);

            SpfService annotation = (SpfService) declaredMethod.getAnnotation(SpfService.class);
            Class<? extends TagParser<?,?>> tagParserCls=null;
            if(annotation!=null){
                 tagParserCls = annotation.tagParser();
            }else {
                AutoParse autoParse = (AutoParse) declaredMethod.getAnnotation(AutoParse.class);
                if(autoParse==null){
                    throw new JiMuBizException("adaptor method has neither tagparser nor AutoParse Annotation");
                }
                String paramTypeName=declaredMethodParameterTypes[0].getName();
                String returnTypeName=declaredMethodReturnType.getName();
                TagParser proxyInstance = AutoParseProxyFactory.getProxyInstance(TagParser.class, paramTypeName, returnTypeName);
                tagParserCls= (Class<? extends TagParser<?, ?>>) proxyInstance.getClass();
            }

            String tagParserClassName=tagParserCls.getName();
            TagParser<?, ?> tagParser = tagParserCls.getDeclaredConstructor().newInstance();
            SpringBeanUtils.putObject(tagParserClassName,tagParser);
            String declaredMethodReturnTypeName = declaredMethodReturnType.getName();
            String methodBody = "     {\n" +
                    " org.openoa.engine.factory.TagParser parser = (org.openoa.engine.factory.TagParser)org.openoa.base.util.SpringBeanUtils.getObject(\"" +tagParserClassName +"\");\n"+
                    "        Object beanOrName = parser.parseTag($1);\n" +
                    " if (!(beanOrName instanceof String)) {\n" +
                    "return ("+declaredMethodReturnTypeName+")beanOrName;"+
                    "        } else {"+
                    " java.util.Map/*<String, ?>*/ beansOfType = org.openoa.base.util.SpringBeanUtils.getBeansOfType(" + declaredMethodReturnTypeName + ".class);\n" +
                    " Object bean = beansOfType.get((String)beanOrName);" +
                    "     return (" + declaredMethodReturnTypeName + ") bean;}}";
            newMethod.setBody(methodBody);

            newMethod.setModifiers(Modifier.PUBLIC);
            targetClass.addMethod(newMethod);
        }


        Class<?> target = targetClass.toClass(IAdaptorFactory.class);
        Object newInstance = target.newInstance();
        targetClass.detach();
        loadedInstance = newInstance;
        return newInstance;
    }


    //get generated proxy object name
    private static String getProxyObjectName() {
        return PROXYFREFIX + SIMPLECLSNAME + PROXYSUFFIX;
    }

}
