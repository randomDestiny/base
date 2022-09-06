package com.iscloud.common.utils;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.iscloud.common.cst.BaseCst;
import com.iscloud.common.helper.StringHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.*;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @Desc: spring上下文对象工具类
 * @Author: Yu.Hua
 * @Date: 2019/12/5 0005 15:22
 */
@Slf4j
@SuppressWarnings({"unused", "unchecked", "UnusedReturnValue"})
@Component(SpringContextUtils.BEAN)
public class SpringContextUtils implements BeanFactoryPostProcessor, ApplicationContextAware, BaseCst.SymbolCst {

    public static final String BEAN = "SpringContextUtils",
            SERVICE = "Service", GET = "get", IS = "is", WRITE_REPLACE = "writeReplace";

    private static ApplicationContext applicationContext;
    /**
     * "@PostConstruct"注解标记的类中，由于ApplicationContext还未加载，导致空指针<br>
     * 因此实现BeanFactoryPostProcessor注入ConfigurableListableBeanFactory实现bean的操作
     */
    private static ConfigurableListableBeanFactory beanFactory;

    private static final Map<Class<?>, String> CLASS_LAMBDA_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Field> CLASS_FIELD_CACHE = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringContextUtils.beanFactory = beanFactory;
    }

    public static ApplicationContext getContext() {
        return applicationContext;
    }

    public static ListableBeanFactory getBeanFactory() {
        return null == beanFactory ? applicationContext : beanFactory;
    }

    /**
     * 获取{@link ConfigurableListableBeanFactory}
     * @return {@link ConfigurableListableBeanFactory}
     */
    public static ConfigurableListableBeanFactory getConfigurableBeanFactory() {
        final ConfigurableListableBeanFactory factory;
        if (null != beanFactory) {
            factory = beanFactory;
        } else if (applicationContext instanceof ConfigurableApplicationContext) {
            factory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        } else {
            throw new RuntimeException("No ConfigurableListableBeanFactory from context!");
        }
        return factory;
    }

    public static <T, R> R getValue(Function<T, R> f, T t) {
        return f == null || t == null ? null : f.apply(t);
    }

    public static <T extends Annotation> Collection<?> listByAnno(Class<T> annoCls) {
        return annoCls == null ? Collections.emptyList() : applicationContext.getBeansWithAnnotation(annoCls).values();
    }

    public static <T extends Annotation, S> List<S> listByAnno(Class<T> annoCls, Class<S> parentCls) {
        if (annoCls == null || parentCls == null) {
            return Collections.emptyList();
        }
        List<S> res = new LinkedList<>();
        applicationContext.getBeansWithAnnotation(annoCls).forEach((k, v) -> {
            if (parentCls.isAssignableFrom(v.getClass())) {
                res.add((S) v);
            }
        });
        return res;
    }

    /**
     * @Desc:   根据beanName获取bean对象
     * @Params: [name]
     * @Return: T
     * @Author: HYbrid
     * @Date:   2022/5/24
     */
    public static <T> T getBean(String name) {
        return (T) getBeanFactory().getBean(name);
    }

    @SneakyThrows
    public static <T> T getBeanObj(String name) {
        Class<T> c = (Class<T>) Class.forName(name);
        return getBeanFactory().getBean(name, c);
    }

    public static <T> T getBean(String name, Class<T> c) {
        return StringUtils.isNotBlank(name) && c != null ? getBeanFactory().getBean(name, c) : null;
    }

    /**
     * @Desc: 根据class获取bean对象
     * @Author:      Yu.Hua
     * @Date:        2020/4/24 0024 16:45
     * @Params:      [clazz]
     * @Return:      T
     */
    public static <T> T getBean(Class<T> clazz) {
        return clazz == null ? null : (T) getBeanFactory().getBean(clazz.getSimpleName());
    }

    /**
     * @Desc:   通过类型参考（类型参考，用于持有转换后的泛型类型）返回带泛型参数的Bean<br />返回：带泛型参数的Bean
     * @Params: [reference]
     * @Return: T
     * @Author: HYbrid
     * @Date:   2022/1/27
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(TypeReference<T> reference) {
        if (reference == null) {
            return null;
        }
        final ParameterizedType parameterizedType = (ParameterizedType) reference.getType();
        final Class<T> rawType = (Class<T>) parameterizedType.getRawType();
        final Class<?>[] genericTypes = Arrays.stream(parameterizedType.getActualTypeArguments()).map(type -> (Class<?>) type).toArray(Class[]::new);
        final String[] beanNames = getBeanFactory().getBeanNamesForType(ResolvableType.forClassWithGenerics(rawType, genericTypes));
        return getBean(beanNames[0], rawType);
    }

    /**
     * @Desc:   获取指定类型（类、接口，null表示获取所有bean）对应的所有Bean，包括子类<br />返回：类型对应的bean，key是bean注册的name，value是Bean
     * @Params: [type]
     * @Return: java.util.Map<java.lang.String,T>
     * @Author: HYbrid
     * @Date:   2022/1/27
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return type == null ? Collections.emptyMap() : getBeanFactory().getBeansOfType(type);
    }

    /**
     * @Desc:   获取指定类型（类、接口，null表示获取所有bean名称）对应的Bean名称，包括子类
     * @Params: [type]
     * @Return: java.lang.String[]
     * @Author: HYbrid
     * @Date:   2022/1/27
     */
    public static String[] getBeanNamesForType(Class<?> type) {
        return type == null ? null : getBeanFactory().getBeanNamesForType(type);
    }

    /**
     * @Desc:   获取配置文件配置项(配置项key)的值
     * @Params: [key]
     * @Return: java.lang.String
     * @Author: HYbrid
     * @Date:   2022/1/27
     */
    public static String getProperty(String key) {
        return null == applicationContext ? null : applicationContext.getEnvironment().getProperty(key);
    }

    /**
     * @Desc:   获取应用程序名称
     * @Params: []
     * @Return: java.lang.String
     * @Author: HYbrid
     * @Date:   2022/1/27
     */
    public static String getApplicationName() {
        return getProperty("spring.application.name");
    }

    /**
     * @Desc:   获取当前的环境配置，无配置返回null
     * @Params: []
     * @Return: java.lang.String[]
     * @Author: HYbrid
     * @Date:   2022/1/27
     */
    public static String[] getActiveProfiles() {
        return null == applicationContext ? null : applicationContext.getEnvironment().getActiveProfiles();
    }

    /**
     * @Desc:   获取当前的环境配置，当有多个环境配置时，只获取第一个
     * @Params: []
     * @Return: java.lang.String
     * @Author: HYbrid
     * @Date:   2022/1/27
     */
    public static String getActiveProfile() {
        final String[] activeProfiles = getActiveProfiles();
        return ArrayUtil.isNotEmpty(activeProfiles) ? activeProfiles[0] : null;
    }

    /**
     * @Desc:   动态向Spring注册Bean
     * <p>
     * 由{@link org.springframework.beans.factory.BeanFactory} 实现，通过工具开放API
     * <p>
     * 更新: 增加自动注入，修复注册bean无法反向注入的问题
     * @Params: [beanName, bean]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/1/27
     */
    public static <T> void registerBean(String beanName, T bean) {
        if (StringUtils.isNotBlank(beanName) && bean != null) {
            final ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
            factory.autowireBean(bean);
            factory.registerSingleton(beanName, bean);
        }
    }

    /**
     * @Desc: 注销bean
     * <p>
     * 将Spring中的bean注销，请谨慎使用
     * @Params: [beanName]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/1/27
     */
    public static void unregisterBean(String beanName) {
        if (StringUtils.isBlank(beanName)) {
            return;
        }
        final ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        if (factory instanceof DefaultSingletonBeanRegistry registry) {
            registry.destroySingleton(beanName);
        } else {
            throw new RuntimeException("Can not unregister bean, the factory is not a DefaultSingletonBeanRegistry!");
        }
    }

    /**
     * @Desc:   发布事件
     * @Params: [event]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/1/27
     */
    public static void publishEvent(ApplicationEvent event) {
        if (applicationContext != null && event != null) {
            applicationContext.publishEvent(event);
        }
    }

    /**
     * @Desc: 获取为空属性名
     * @Author: Yu.Hua
     * @Date: 2020/2/3 18:21
     * @Params: [source]
     * @Return: java.lang.String[]
     */
    public static String[] getNullPropertyNames(Object source) {
        if (source == null) {
            return null;
        }
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>(pds.length);
        for (PropertyDescriptor pd : pds) {
            if (src.getPropertyValue(pd.getName()) == null) {
                emptyNames.add(pd.getName());
            }
        }
        return emptyNames.toArray(new String[] {});
    }

    public static Map<String, Object> getNotNullProperties(Object source) {
        if (source == null) {
            return Collections.emptyMap();
        }
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Map<String, Object> args = new HashMap<>(pds.length);
        Object o; String n;
        for (PropertyDescriptor pd : pds) {
            if ((o = src.getPropertyValue((n = pd.getName()))) != null && !"class".equalsIgnoreCase(n)) {
                args.put(n, o);
            }
        }
        return args;
    }

    public static Set<String> getNotNullPropertyNames(Object source) {
        if (source == null) {
            return Collections.emptySet();
        }
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> args = new HashSet<>(pds.length);
        for (PropertyDescriptor pd : pds) {
            if (src.getPropertyValue(pd.getName()) != null && !"class".equalsIgnoreCase(pd.getName())) {
                args.add(pd.getName());
            }
        }
        return args;
    }

    @SneakyThrows
    public static <T> T transfer(T s, T t) {
        if (s == null) {
            return null;
        }
        if (t == null) {
            t = (T) s.getClass().getDeclaredConstructor().newInstance();
        }
        BeanUtils.copyProperties(s, t, getNullPropertyNames(s));
        return t;
    }

    public static <T> T transfer(Object o, Class<T> t) {
        Assert.notNull(t, "无效的转换对象类型！");
        try {
            T res = t.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(o, res, getNullPropertyNames(o));
            return res;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Desc:   根据表名获取Service实例对象
     * @Params: [tableName]
     * @Return: java.lang.Object
     * @Author: HYbrid
     * @Date:   2022/5/24
     */
    public static Object getServiceBeanByTableName(String tableName) {
        if (ResultUtils.check(tableName, "缺失的表名称！", 101)) {
            String serviceName = StringHelper.camel1stUpper(tableName.trim()) + SERVICE;
            return getBean(serviceName);
        }
        return null;
    }

    @SneakyThrows
    public static Field convertToField(Serializable f) {
        if (f != null) {
            Class<? extends Serializable> clazz = f.getClass();
            Field field = CLASS_FIELD_CACHE.get(clazz);
            if (field == null) {
                convertToFieldName(f);
                field = CLASS_FIELD_CACHE.get(clazz);
            }
            return field;
        }
        return null;
    }

    @SneakyThrows
    public static String convertToFieldName(Serializable f) {
        if (f != null) {
            Class<? extends Serializable> clazz = f.getClass();
            String name = CLASS_LAMBDA_CACHE.get(clazz);
            if (name == null) {
                Method method = clazz.getDeclaredMethod(WRITE_REPLACE);
                method.setAccessible(Boolean.TRUE);
                name = getFieldName(clazz, (SerializedLambda) method.invoke(f));
            }
            return name;
        }
        return StringUtils.EMPTY;
    }

    @SneakyThrows
    public static String getFieldName(Class<? extends Serializable> clazz, SerializedLambda lambda) {
        String name = null;
        if (lambda != null && clazz != null) {
            String methodName = lambda.getImplMethodName();
            if (methodName.startsWith(GET)) {
                name = Introspector.decapitalize(methodName.replaceFirst(GET, StringUtils.EMPTY));
                CLASS_LAMBDA_CACHE.put(clazz, name);
                CLASS_FIELD_CACHE.put(clazz, Class.forName(lambda.getImplClass().replace(OBLIQUE_LINE, POINT)).getDeclaredField(name));
            } else if (methodName.startsWith(IS)) {
                name = Introspector.decapitalize(methodName.replaceFirst(IS, StringUtils.EMPTY));
                CLASS_LAMBDA_CACHE.put(clazz, name);
                CLASS_FIELD_CACHE.put(clazz, Class.forName(lambda.getImplClass().replace(OBLIQUE_LINE, POINT)).getDeclaredField(name));
            } else {
                log.error("无效的getter方法: " + methodName);
            }
        }
        return name;
    }

    /**
     * @Desc:   获取声明的属性，包括父类的声明
     * @Params: [clazz, fieldName]
     * @Return: java.lang.reflect.Field
     * @Author: HYbrid
     * @Date:   2022/5/24
     */
    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        if (clazz == null || StringUtils.isBlank(fieldName)) {
            return null;
        }
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return getDeclaredField(clazz.getSuperclass(), fieldName);
        }
        return field;
    }

    /**
     * @Desc:   生成bean的定义
     * @Params: [k, v]
     * @Return: org.springframework.beans.factory.support.RootBeanDefinition
     * @Author: HYbrid
     * @Date:   2022/3/25
     */
    @SneakyThrows
    public static RootBeanDefinition beanDefine(String k, JSONObject v) {
        if (StringUtils.isNotBlank(k)) {
            RootBeanDefinition def = new RootBeanDefinition();
            def.setBeanClass(ClassUtils.getClass(k));
            if (v != null && !v.isEmpty()) {
                MutablePropertyValues p = new MutablePropertyValues();
                p.addPropertyValues(v.getInnerMap());
                def.setPropertyValues(p);
            }
            return def;
        }
        return null;
    }

    /**
     * @Desc:   注册bean
     * @Params: [k, v, reg]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/3/25
     */
    public static void beanRegistry(String k, JSONObject v, BeanDefinitionRegistry reg) {
        RootBeanDefinition def = beanDefine(k, v);
        if (def != null) {
            reg.registerBeanDefinition(k, def);
        }
    }

    /**
     * @Desc:   从环境获取ConfigurationProperties的绑定信息
     * @Params: [cls, env]
     * @Return: T
     * @Author: HYbrid
     * @Date:   2022/3/25
     */
    public static <T> T boundGet(Class<T> cls, Environment env) {
        if (env != null && cls != null) {
            String pre = Objects.requireNonNull(AnnotationUtils.getAnnotation(cls, ConfigurationProperties.class)).prefix();
            BindResult<T> b = Binder.get(env).bind(pre, cls);
            if (b != null && b.isBound()) {
                return b.get();
            }
        }
        return null;
    }

    /**
     * @Desc:   获取某个类中的泛型类
     * @Params: [cls]
     * @Return: java.lang.Class<T>
     * @Author: HYbrid
     * @Date:   2022/8/18
     */
    public static List<Class<?>> getGenericClass(Class<?> cls) {
        if (cls != null) {
            ParameterizedType type = (ParameterizedType) cls.getGenericSuperclass();
            Type[] types = type.getActualTypeArguments();
            if (ArrayUtils.isNotEmpty(types)) {
                List<Class<?>> res = new ArrayList<>(types.length);
                for (Type t : types) {
                    res.add(t.getClass());
                }
                return res;
            }
        }
        return Collections.emptyList();
    }

    /**
     * @Desc:   迭代获取第一个泛型类class
     * @Params: [cls]
     * @Return: java.lang.Class<T>
     * @Author: HYbrid
     * @Date:   2022/8/31
     */
    public static <T> Class<T> getParentGenericClassFirst(Class<?> cls) {
        if (cls != null) {
            Type type = cls.getGenericSuperclass();
            if (type instanceof ParameterizedType p) {
                Type[] types = p.getActualTypeArguments();
                if (ArrayUtils.isNotEmpty(types)) {
                    Type t = types[0];
                    if (t != null) {
                        return (Class<T>) t;
                    }
                }
            } else if (type instanceof Class<?> c) {
                return getParentGenericClassFirst(c);
            }
        }
        return null;
    }

    /**
     * @Desc:   迭代获取第一个泛型类bean
     * @Params: [cls]
     * @Return: T
     * @Author: HYbrid
     * @Date:   2022/8/31
     */
    @SneakyThrows
    public static <T> T getBeanByParentGenericClassFirst(Class<?> cls) {
        Class<T> clazz = getParentGenericClassFirst(cls);
        if (clazz != null) {
            return clazz.getConstructor().newInstance();
        }
        return null;
    }

    /**
     * @Desc:   获取第一个泛型类class
     * @Params: [cls]
     * @Return: java.lang.Class<T>
     * @Author: HYbrid
     * @Date:   2022/8/31
     */
    public static <T> Class<T> getGenericClassFirst(Class<?> cls) {
        if (cls != null) {
            ParameterizedType type = (ParameterizedType) cls.getGenericSuperclass();
            Type[] types = type.getActualTypeArguments();
            if (ArrayUtils.isNotEmpty(types)) {
                Type t = types[0];
                if (t != null) {
                    return (Class<T>) t;
                }
            }
        }
        return null;
    }

    /**
     * @Desc:   获取第一个泛型类bean
     * @Params: [cls]
     * @Return: T
     * @Author: HYbrid
     * @Date:   2022/8/31
     */
    @SneakyThrows
    public static <T> T getBeanByGenericClassFirst(Class<?> cls) {
        Class<T> clazz = getGenericClassFirst(cls);
        if (clazz != null) {
            return clazz.getConstructor().newInstance();
        }
        return null;
    }

}
