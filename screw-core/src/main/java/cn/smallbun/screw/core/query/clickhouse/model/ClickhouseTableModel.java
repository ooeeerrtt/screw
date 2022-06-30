package cn.smallbun.screw.core.query.clickhouse.model;

import cn.smallbun.screw.core.mapping.MappingField;
import cn.smallbun.screw.core.metadata.Table;
import lombok.Data;

@Data
public class ClickhouseTableModel implements Table {
    /**
     * 表名
     */
    @MappingField(value = "TABLE_NAME")
    private String tableName;

    /**
     * 备注
     */
    @MappingField(value = "REMARKS")
    private String remarks;
}
