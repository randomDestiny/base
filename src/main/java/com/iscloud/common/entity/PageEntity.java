package com.iscloud.common.entity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @Desc: 分页VO
 * @Author ：HYbrid
 * @Date ：2021/7/2 17:58
 */
@SuppressWarnings("unused")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageEntity<T> implements IPage<T> {
    protected long total;
    protected long size;
    protected long current;
    protected Collection<T> records;

    public PageEntity(int current, int size) {
        this(current, size, 0);
    }
    public PageEntity(int current, int size, long total) {
        this.current = current;
        this.size = size;
        this.total = total;
    }

    public PageEntity(PageEntity<?> p, Collection<T> records) {
        this.records = records;
        if (p != null) {
            this.current = p.getCurrent();
            this.size = p.getSize();
            this.total = p.getTotal();
        }
    }

    @Override
    public List<T> getRecords() {
        return CollectionUtils.isNotEmpty(this.records) ? new ArrayList<>(this.records) : Collections.emptyList();
    }

    @Override
    public PageEntity<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    @Override
    public PageEntity<T> setSize(long size) {
        this.size = size;
        return this;
    }

    @Override
    public PageEntity<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    @Override
    public PageEntity<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    @Override
    public List<OrderItem> orders() {
        return Collections.emptyList();
    }

}
