package com.iscloud.common.entity;

import com.iscloud.common.bo.TaskBO;
import com.iscloud.common.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Desc: AbstractTask
 * @Author: HYbrid
 * @Date: 2022/8/17 13:12
 */
@Slf4j
@SuppressWarnings("unused")
@Component
public abstract class AbstractTask<T extends TaskBO> implements Runnable {

    @Value("${job.logging.able:false}")
    protected boolean loggingAble;

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        Class<?> cls = this.getClass();
        String clsName = cls.getSimpleName();
        log.info(String.format("Task【%s】start", clsName));
        // init
        TaskBO bo = SpringContextUtils.getBeanByParentGenericClassFirst(cls);
        if (bo == null) {
            bo = new TaskBO();
        }
        // start
        bo = this.start(bo);
        if (bo.isRunDisAble()) {
            log.warn("Task disabled ...");
            return;
        }
        // fetch
        bo.setStartFetchDate(new Date());
        bo.setData(fetch());
        bo.endFetch();
        if (bo.getData() != null && loggingAble) {
            this.saveStepHistory((T) bo);
        }
        // handle
        bo.setStartHandleDate(new Date());
        this.handle((T) bo);
        bo.endHandle();
        if (loggingAble) {
            this.saveStepHistory((T) bo);
        }
        log.info(String.format("Task【%s】end", clsName));
    }

    /**
     * @Desc:   开始执行，做一些准备工作
     * @Params: [ctx]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/8/18
     */
    protected TaskBO start(TaskBO ctx) {
        return ctx;
    }

    /**
     * @Desc:   获取数据
     * @Params: []
     * @Return: java.lang.Object
     * @Author: HYbrid
     * @Date:   2022/8/17
     */
    protected Object fetch() {
        return null;
    }

    /**
     * @Desc:   处理数据
     * @Params: [ctx]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/8/17
     */
    protected abstract void handle(T ctx);

    /**
     * @Desc:   保存每步信息
     * @Params: [ctx]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/8/18
     */
    protected void saveStepHistory(T ctx) {
    }

}
