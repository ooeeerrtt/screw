package cn.smallbun.screw.core.query.clickhouse.model;

import cn.smallbun.screw.core.mapping.MappingField;
import cn.smallbun.screw.core.metadata.PrimaryKey;
import lombok.Data;

@Data
public class ClickhousePrimaryKeyModel implements PrimaryKey {
    private static final long serialVersionUID = -4908250184995248600L;

    /**
     * 表名
     */
    @MappingField(value = "TABLE_NAME")
    private String tableName;
    /**
     * 主键名称
     */
    @MappingField(value = "PK_NAME")
    private String pkName;
    /**
     * 列名
     */
    @MappingField(value = "COLUMN_NAME")
    private String columnName;
    /**
     *
     */
    @MappingField(value = "KEY_SEQ")
    private String keySeq;
}
