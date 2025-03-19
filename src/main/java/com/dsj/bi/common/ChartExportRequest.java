package com.dsj.bi.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 导出Excel
 */
@Data
public class ChartExportRequest implements Serializable {

    /**
     * id
     */
    private Long chartId;

    private static final long serialVersionUID = 1L;
}
