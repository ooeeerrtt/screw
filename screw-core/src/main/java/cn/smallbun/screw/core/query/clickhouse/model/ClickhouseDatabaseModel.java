package cn.smallbun.screw.core.query.clickhouse.model;

import cn.smallbun.screw.core.mapping.MappingField;
import cn.smallbun.screw.core.metadata.Database;
import lombok.Data;

@Data
public class ClickhouseDatabaseModel implements Database {
    private static final long serialVersionUID = 931210775266917894L;
    /**
     * 数据库名称
     */
    private String database;

    /**
     * 备注
     */
    @MappingField(value = "REMARKS")
    private String remarks;
}
