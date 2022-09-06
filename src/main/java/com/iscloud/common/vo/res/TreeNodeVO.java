package com.iscloud.common.vo.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * @Desc:   树节点VO
 * @Author: HYbrid
 * @Date:   2022/5/24
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuppressWarnings("unused")
public class TreeNodeVO {

    private String title;
    private String label;
    private Long id;
    // 是否展开
    private boolean spread = false;
    // 是否展开
    private boolean checked = false;
    // 是否选中
    private boolean open = false;
    private Collection<?> children;
    private Collection<?> infoList;
    private Object info;
    private String code;
    private String fullName;

    public TreeNodeVO(Long id, String title, String fullName, boolean spread) {
        this.id = id;
        this.title = title;
        this.label = title;
        this.spread = spread;
        this.checked = spread;
        this.fullName = fullName;
    }

    public TreeNodeVO(Long id, String title, boolean spread) {
        this.id = id;
        this.title = title;
        this.label = title;
        this.spread = spread;
        this.checked = spread;
    }

    public TreeNodeVO(Long id, String title) {
        this.id = id;
        this.title = title;
        this.label = title;
    }
}
