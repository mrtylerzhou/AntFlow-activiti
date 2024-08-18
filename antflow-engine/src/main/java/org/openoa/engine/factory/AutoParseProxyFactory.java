package org.openoa.engine.factory;


import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.*;
import org.apache.ibatis.javassist.bytecode.ClassFile;
import org.apache.ibatis.javassist.bytecode.ConstPool;
import org.apache.ibatis.javassist.bytecode.SignatureAttribute;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @className: SimpleProxyFactory
 * @author: Tyler Zhou
 * @since: 0.0.1
 **/
@Slf4j
public class AutoParseProxyFactory {
    private AutoParseProxyFactory(){}


    private static final String PROXYFREFIX = "$Proxy";

    private static final String PROXYSUFFIX = "Impl";
    private static Map<String,Object> loadedInstances=new HashMap<>();

    public static synchronized <T> T getProxyInstance(Class<T> objToProxy,String paramTypeName,String returnTypeName) {
        String simpleName = objToProxy.getSimpleName();
        if (loadedInstances.get(simpleName) == null) {
                try {
                    return (T)getProxyObj(simpleName,objToProxy,paramTypeName,returnTypeName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return (T)loadedInstances.get(simpleName);
    }
    //generated dynamic proxy object for passed class
    private static Object getProxyObj(final String objName,Class<?> objToProxy,String paramTypeName,String returnTypeName) throws Exception{
        if (loadedInstances.get(objName) != null) {
            return loadedInstances.get(objName);
        }
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(objToProxy));
        String packageName = objToProxy.getPackage().getName();
        String proxyClsName = packageName + "." + getProxyObjectName(objName);
        String sourceClsName = packageName + "." + objToProxy.getSimpleName();

        CtClass targetClass = pool.makeClass(proxyClsName);

        ClassFile classFile = targetClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();

        // Add public constructor
        CtConstructor constructor = new CtConstructor(new CtClass[]{}, targetClass);
        constructor.setModifiers(Modifier.PUBLIC);
        constructor.setBody("{}");
        targetClass.addConstructor(constructor);


        CtClass tagParserInterface = pool.get(TagParser.class.getName());

        targetClass.addInterface(tagParserInterface);
        String genericSignature = "L" + TagParser.class.getName().replace('.', '/') + "<L" + paramTypeName.replace('.', '/') + ";L"+returnTypeName+";>;";
        targetClass.setGenericSignature(new SignatureAttribute.TypeVariable(genericSignature).encode());
        for (CtMethod declaredMethod : tagParserInterface.getDeclaredMethods()){
            CtClass declaredMethodReturnType = declaredMethod.getReturnType();
            CtClass[] declaredMethodParameterTypes = declaredMethod.getParameterTypes();
            String declaredMethodName = declaredMethod.getName();
            // 创建方法
            CtMethod newMethod = new CtMethod(declaredMethodReturnType, declaredMethodName, declaredMethodParameterTypes, targetClass);
            String body = "{\n" +
                    "    if ($1 == null) {\n" +
                    "        throw new org.openoa.base.exception.JiMuBizException(\"provided data to find an adaptor method is null\");\n" +
                    "    }\n" +
                    "    java.util.Collection beans = org.openoa.base.util.SpringBeanUtils.getBeans(" + returnTypeName + ".class);\n" +
                    "    java.util.Iterator iterator = beans.iterator();\n" +
                    "    while (iterator.hasNext()) {\n" +
                    "        Object obj = iterator.next();\n" +
                    "        if (obj instanceof org.openoa.base.interf.AdaptorService) {\n" +
                    "            org.openoa.base.interf.AdaptorService bean = (org.openoa.base.interf.AdaptorService) obj;\n" +
                    "            if (bean.isSupportBusinessObject((Enum) $1)) {\n" +
                    "                return (" + returnTypeName + ") bean;\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }\n" +
                    "    System.out.println(\"nothing is return,for debugging use only\");\n" +
                    "    return null;\n" +
                    "}";
            newMethod.setBody(body);

            newMethod.setModifiers(Modifier.PUBLIC);
            targetClass.addMethod(newMethod);
        }

        Class<?> target = targetClass.toClass(IAdaptorFactory.class);
        Object newInstance = target.getDeclaredConstructor().newInstance();
        targetClass.detach();
        loadedInstances.put(objName,newInstance);
        return newInstance;
    }

    //获取代理对象的名称
    private static String getProxyObjectName(final String objName) {
        return PROXYFREFIX+"_" + objName + PROXYSUFFIX;
    }
}
