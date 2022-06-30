package cn.smallbun.screw.core.query.clickhouse.model;

import cn.smallbun.screw.core.mapping.MappingField;
import cn.smallbun.screw.core.metadata.Column;
import lombok.Data;

@Data
public class ClickhouseColumnModel implements Column {
    private static final long serialVersionUID = -7231934486902707912L;

    /**
     * 表名
     */
    @MappingField(value = "TABLE_NAME")
    private String tableName;
    /**
     * 默认值
     */
    @MappingField(value = "COLUMN_DEF")
    private String columnDef;
    /**
     * 列名
     */
    @MappingField(value = "COLUMN_NAME")
    private String columnName;
    /**
     * 能否为空
     */
    @MappingField(value = "NULLABLE")
    private String nullable;
    /**
     * 说明
     */
    @MappingField(value = "REMARKS")
    private String remarks;
    /**
     * 小数位
     */
    @MappingField(value = "DECIMAL_DIGITS")
    private String decimalDigits;
    /**
     * 序列号
     */
    @MappingField(value = "ORDINAL_POSITION")
    private String ordinalPosition;
    /**
     * 列类型
     */
    @MappingField(value = "TYPE_NAME")
    private String typeName;
    /**
     * 列表示给定列的指定列大小。
     * 对于数值数据，这是最大精度。
     * 对于字符数据，这是字符长度。
     * 对于日期时间数据类型，这是 String 表示形式的字符长度（假定允许的最大小数秒组件的精度）。
     * 对于二进制数据，这是字节长度。
     * 对于 ROWID 数据类型，这是字节长度。对于列大小不适用的数据类型，则返回 Null。
     */
    @MappingField(value = "COLUMN_SIZE")
    private String columnSize;
    /**
     * 是否主键
     */
    private String primaryKey;

    /**
     * 列类型（带长度）
     */
    @MappingField(value = "COLUMN_TYPE")
    private String columnType;
    /**
     * 列长度
     */
    @MappingField(value = "COLUMN_LENGTH")
    private String columnLength;
}
