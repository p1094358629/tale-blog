package com.tale.model.entity;

import com.blade.jdbc.annotation.Table;
import com.blade.jdbc.core.ActiveRecord;

import lombok.Data;
/**
 * 时间轴
 *
 * @author pete
 */
@Data
@Table(value = "t_talks", pk = "tid")
public class Talks extends ActiveRecord{
    private Integer tid;
    private String contents;
    private Integer created;
    private String status;
}
