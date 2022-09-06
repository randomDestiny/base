package com.iscloud.common.vo.req;

import com.alibaba.fastjson.annotation.JSONField;
import com.iscloud.common.helper.ListHelper;
import com.iscloud.common.vo.res.ResCommonVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * @Desc: 查询数据库通用VO
 * @Author: HYbrid
 * @Date: 2022/6/14 17:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class QueryParamsVO implements ResCommonVO {

    protected Long id;
    @JSONField(serialize = false)
    protected Boolean queryStartEnd4Created;
    @JSONField(serialize = false)
    protected Boolean queryStartEnd4Updated;
    @JSONField(serialize = false)
    protected String startTime;
    @JSONField(serialize = false)
    protected String endTime;
    @JSONField(serialize = false)
    protected Long companyId;
    @JSONField(serialize = false)
    protected Collection<Long> ids;
    @JSONField(serialize = false)
    protected Integer pageNum;
    @JSONField(serialize = false)
    protected Integer pageSize;
    /**
     * @Desc: 图表类型，具体详见SystemConfig中chartType
     */
    @JSONField(serialize = false)
    protected String chartType;
    /**
     * @Desc: 统计时间类型：年=yyyy,月=MM,周=e,天=dd,小时=HH,分钟=mm,秒=ss,毫秒=long
     */
    @JSONField(serialize = false)
    protected String timeType;
    /**
     * @Desc: 关键字
     */
    @JSONField(serialize = false)
    protected String keyword;
    /**
     * @Desc: 是否开启全文检索
     */
    @JSONField(serialize = false)
    protected boolean fullTextSearch = false;

    @JSONField(serialize = false)
    public void queryStartEnd4Created() {
        this.queryStartEnd4Created = true;
    }

    @JSONField(serialize = false)
    public void queryStartEnd4Updated() {
        this.queryStartEnd4Updated = true;
    }

    /**
     * @Desc:   获取当前页
     * @Params: []
     * @Return: int
     * @Author: HYbrid
     * @Date:   2022/6/14
     */
    @JSONField(serialize = false)
    public int getNum() {
        return this.pageNum == null || this.pageNum <= 0 ? 1 : this.pageNum;
    }

    /**
     * @Desc:   获取分页数
     * @Params: []
     * @Return: int
     * @Author: HYbrid
     * @Date:   2022/6/14
     */
    @JSONField(serialize = false)
    public int getSize() {
        return this.pageSize == null || this.pageSize <= 0 ? Integer.MAX_VALUE : this.pageSize;
    }

    /**
     * @Desc:   设置ids
     * @Params: [ids]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/6/14
     */
    public void setIds(Long... ids) {
        this.ids = ListHelper.trim2Set(ids);
    }
}
