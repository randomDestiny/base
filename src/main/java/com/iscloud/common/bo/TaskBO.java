package com.iscloud.common.bo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iscloud.common.cst.CronJobRunStepEnum;
import com.iscloud.common.utils.DateFormatUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Desc: TaskBO
 * @Author: HYbrid
 * @Date: 2022/8/17 13:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskBO {

    protected String cron;
    protected String lastDateString;
    protected final Date currentDate = new Date();
    protected final String currentDateString = DateFormatUtils.formatDefault(new Date());
    protected Object data;
    protected Long expendTimestamp;
    protected Long expendFetch;
    protected Long expendHandle;
    protected Date startFetchDate;
    protected Date startHandleDate;
    protected CronJobRunStepEnum currStep = CronJobRunStepEnum.NOT_START;
    protected boolean runAble = true;

    public void runDisable() {
        this.runAble = false;
    }

    @JSONField(deserialize = false, serialize = false)
    @JsonIgnore
    public boolean isRunDisAble() {
        return !this.runAble;
    }

    public void endFetch() {
        if (this.startFetchDate != null) {
            this.expendFetch = System.currentTimeMillis() - this.startFetchDate.getTime();
        }
        this.currStep = CronJobRunStepEnum.FETCH;
    }

    public void endHandle() {
        long curr = System.currentTimeMillis();
        if (this.startHandleDate != null) {
            this.expendHandle = curr - this.startHandleDate.getTime();
        }
        this.expendTimestamp = curr - this.currentDate.getTime();
        this.currStep = CronJobRunStepEnum.FINISH;
    }

}
