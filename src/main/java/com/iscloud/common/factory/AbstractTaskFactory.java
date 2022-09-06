package com.iscloud.common.factory;

import com.alibaba.fastjson2.JSON;
import com.iscloud.common.cst.BaseCst.TimeTypeCst;
import com.iscloud.common.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.Task;
import org.springframework.scheduling.support.CronTrigger;

import java.util.*;
import java.util.concurrent.Executors;

/**
 * @Desc: 抽象定时任务工厂类
 * @Author: HYbrid
 * @Date: 2022/8/27 13:09
 */
@Slf4j
@SuppressWarnings("unused")
public abstract class AbstractTaskFactory {

    private static ScheduledTaskRegistrar registrar;

    private static Set<Class<?>> taskClsSet;

    protected abstract Map<Object, String> initTaskCron();
    protected abstract Map<Object, Trigger> initTaskTrigger();
    protected abstract Map<Object, Long> initTaskFixedRate();
    protected abstract Map<Object, Long> initTaskFixedDelay();

    public void configRegistrar(@NotNull ScheduledTaskRegistrar taskRegistrar) {
        Map<Runnable, String> cronMap = filter(initTaskCron());
        Map<Runnable, Trigger> triggerMap = filter(initTaskTrigger());
        Map<Runnable, Long> rateMap = filter(initTaskFixedRate());
        Map<Runnable, Long> delayMap = filter(initTaskFixedDelay());
        taskClsSet = new HashSet<>(cronMap.size() + triggerMap.size() + rateMap.size() + delayMap.size());
        if (MapUtils.isNotEmpty(cronMap)) {
            cronMap.forEach((k, v) -> {
                log.error("add Scheduled Task [{}] -> cron [{}]", k.getClass().getSimpleName(), v);
                taskRegistrar.addTriggerTask(k, triggerContext -> new CronTrigger(v).nextExecutionTime(triggerContext));
                taskClsSet.add(k.getClass());
            });
        }
        if (MapUtils.isNotEmpty(triggerMap)) {
            triggerMap.forEach((k, v) -> {
                log.error("add Scheduled Task [{}] -> trigger [{}]", k.getClass().getSimpleName(), JSON.toJSONString(v));
                taskRegistrar.addTriggerTask(k, v);
                taskClsSet.add(k.getClass());
            });
        }
        if (MapUtils.isNotEmpty(rateMap)) {
            rateMap.forEach((k, v) -> {
                log.error("add Scheduled Task [{}] -> rate [{}]", k.getClass().getSimpleName(), v);
                taskRegistrar.addFixedRateTask(k, v);
                taskClsSet.add(k.getClass());
            });
        }
        if (MapUtils.isNotEmpty(delayMap)) {
            delayMap.forEach((k, v) -> {
                log.error("add Scheduled Task [{}] -> delay [{}]", k.getClass().getSimpleName(), v);
                taskRegistrar.addFixedDelayTask(k, v);
                taskClsSet.add(k.getClass());
            });
        }
        int size = Double.valueOf(Math.ceil(taskClsSet.size() * 1.2)).intValue();
        log.error("init Scheduled Task pool size -> {}", size);
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(size));
        registrar = taskRegistrar;
    }

    @SuppressWarnings("unchecked")
    private static <T> Map<Runnable, T> filter(Map<Object, T> map) {
        if (map == null) {
            return Collections.emptyMap();
        }
        Map<Runnable, T> res = new HashMap<>(map.size());
        map.forEach((k, v) -> {
            if (k != null && v != null
                    && ((v instanceof String s && StringUtils.isNotBlank(s)) || v instanceof Trigger || v instanceof Long)) {
                Runnable r = toRunnable(k);
                if (r != null) {
                    res.put(r, v instanceof Long ? (T) initLong(v) : v);
                }
            }
        });
        return res;
    }

    protected static Runnable toRunnable(Object o) {
        Runnable task = null;
        if (o instanceof String b) {
            Object obj = SpringContextUtils.getBean(b);
            if (obj instanceof Runnable r) {
                task = r;
            }
        } else if (o instanceof Runnable r) {
            task = r;
        } else if (o instanceof Task t) {
            task = t.getRunnable();
        }
        return task;
    }

    protected static Long initLong(Object o) {
        Long n = null;
        if (o instanceof Long l) {
            n = l;
        } else if (o instanceof String s && StringUtils.isNumeric(s)) {
            n = Long.parseLong(s);
        }
        if (n != null && n > 0) {
            return n;
        }
        return TimeTypeCst.MS_1_MIN;
    }

    /**
     * @Desc:   激活添加任务
     * @Params: []
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/8/27
     */
    public static void activateScheduledTask(Runnable task, Object v, boolean isDelay) {
        if (registrar != null && task != null) {
            // 添加之前优先移除
            removeTask(task);
            String clsName = task.getClass().getSimpleName();
            boolean activateAble = true;
            if (v instanceof Trigger t) {
                log.error("add Scheduled Task [{}] -> trigger [{}]", clsName, JSON.toJSONString(t));
                registrar.addTriggerTask(task, t);
            } else if (v instanceof String s) {
                log.error("add Scheduled Task [{}] -> cron [{}]", clsName, s);
                registrar.addCronTask(task, s);
            } else if (v instanceof Long l) {
                long d = initLong(v);
                if (isDelay) {
                    log.error("add Scheduled Task [{}] -> delay [{}]", clsName, d);
                    registrar.addFixedDelayTask(task, d);
                } else {
                    log.error("add Scheduled Task [{}] -> rate [{}]", clsName, d);
                    registrar.addFixedRateTask(task, d);
                }
            } else {
                activateAble = false;
                log.error("add Scheduled Task [{}] is illegal!", clsName);
            }
            if (activateAble) {
                registrar.afterPropertiesSet();
                taskClsSet.add(task.getClass());
                log.error("add Scheduled Task [{}] had effect!", clsName);
            }
        }
    }

    public static void activateScheduledTaskBean(Object bean, Object v, boolean isDelay) {
        if (registrar != null) {
            Runnable task = toRunnable(bean);
            activateScheduledTask(task, v, isDelay);
        }
    }

    /**
     * @Desc:   添加触发性任务
     * @Params: [task, trigger]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/8/27
     */
    public static void addTask(Runnable task, Trigger trigger) {
        activateScheduledTask(task, trigger, false);
    }

    /**
     * @Desc:   添加cron任务
     * @Params: [task, cron]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/8/27
     */
    public static void addTask(Runnable task, String cron) {
        activateScheduledTask(task, cron, false);
    }

    /**
     * @Desc:   添加周期性任务：当前任务启动时间与下次任务启动时间之差，单位ms
     * @Params: [task, interval]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/8/27
     */
    public static void addFixedRateTask(Runnable task, long interval) {
        activateScheduledTask(task, interval, false);
    }

    /**
     * @Desc:   添加周期性任务：当前任务结束时间与下次任务启动时间之差，单位ms
     * @Params: [task, delay]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/8/27
     */
    public static void addFixedDelayTask(Runnable task, long delay) {
        activateScheduledTask(task, delay, true);
    }

    /**
     * @Desc:   移除任务
     * @Params: [cls]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/8/27
     */
    public static void removeTask(Class<?> cls) {
        if (cls != null && registrar != null) {
            log.error("remove Scheduled Task [{}]", cls.getSimpleName());
            Set<ScheduledTask> set = registrar.getScheduledTasks();
            if (CollectionUtils.isNotEmpty(set)) {
                set.forEach(i -> {
                    if (i != null) {
                        Task t = i.getTask();
                        if (cls.equals(t.getRunnable().getClass())) {
                            i.cancel();
                            log.error("Scheduled Task [{}] had cancel!", cls.getSimpleName());
                        }
                    }
                });
            }
            if (CollectionUtils.isNotEmpty(taskClsSet)) {
                taskClsSet.remove(cls);
            }
        }
    }

    /**
     * @Desc:   移除任务
     * @Params: [bean]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/8/27
     */
    public static void removeTask(String bean) {
        try {
            Class<?> cls = Class.forName(bean);
            removeTask(cls);
        } catch (ClassNotFoundException e) {
            log.error("ClassNotFoundException->[{}]！", bean);
        }
    }

    /**
     * @Desc:   移除任务
     * @Params: [t]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/8/27
     */
    public static <T> void removeTask(T t) {
        if (t instanceof Runnable o) {
            removeTask(o.getClass());
        }
    }
}
