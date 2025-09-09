package org.openoa.engine.factory;


import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.*;
import org.apache.ibatis.javassist.bytecode.AnnotationsAttribute;
import org.apache.ibatis.javassist.bytecode.ClassFile;
import org.apache.ibatis.javassist.bytecode.ConstPool;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @className: SimpleProxyFactory
 * @author: Tyler Zhou
 * @since: 0.0.1
 **/
@Slf4j
public class SimpleProxyFactory {
    private SimpleProxyFactory(){}


    private static final String PROXYFREFIX = "$Proxy";

    private static final String PROXYSUFFIX = "Impl";
    private static Map<String,Object> loadedInstances=new HashMap<>();

    public static synchronized <T> T getProxyInstance(Class<T> objToProxy) {
        String simpleName = objToProxy.getSimpleName();
        if (loadedInstances.get(simpleName) == null) {
                try {
                    return (T)getProxyObj(simpleName,objToProxy);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return (T)loadedInstances.get(simpleName);
    }
    //generated dynamic proxy object for passed class
    private static Object getProxyObj(final String objName,Class<?> objToProxy) throws Exception{
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


        CtClass interf = pool.getCtClass(sourceClsName);
        CtClass[] interfs = new CtClass[]{interf};
        targetClass.setInterfaces(interfs);
        for (CtMethod declaredMethod : interf.getDeclaredMethods()){
            CtClass declaredMethodReturnType = declaredMethod.getReturnType();
            CtClass[] declaredMethodParameterTypes = declaredMethod.getParameterTypes();
            String declaredMethodName = declaredMethod.getName();
            // 创建方法
            CtMethod newMethod = new CtMethod(declaredMethodReturnType, declaredMethodName, declaredMethodParameterTypes, targetClass);
            AnnotationsAttribute attr=new AnnotationsAttribute(constPool,AnnotationsAttribute.visibleTag);

            newMethod.getMethodInfo().addAttribute(attr);
            String methodBody = " {\n" +
                    " System.out.println(\"Hello,you called a dynamically generated method,this message is for debugging use only\");\n" +
                "return null;"+
                    "    }";
            newMethod.setBody(methodBody);

            newMethod.setModifiers(Modifier.PUBLIC);
            targetClass.addMethod(newMethod);
        }

        Class<?> target = targetClass.toClass();
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
