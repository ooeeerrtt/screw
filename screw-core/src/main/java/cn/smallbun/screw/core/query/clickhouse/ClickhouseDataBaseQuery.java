package cn.smallbun.screw.core.query.clickhouse;

import cn.smallbun.screw.core.exception.QueryException;
import cn.smallbun.screw.core.mapping.Mapping;
import cn.smallbun.screw.core.metadata.Column;
import cn.smallbun.screw.core.metadata.Database;
import cn.smallbun.screw.core.metadata.PrimaryKey;
import cn.smallbun.screw.core.metadata.Table;
import cn.smallbun.screw.core.query.AbstractDatabaseQuery;
import cn.smallbun.screw.core.query.clickhouse.model.ClickhouseColumnModel;
import cn.smallbun.screw.core.query.clickhouse.model.ClickhouseDatabaseModel;
import cn.smallbun.screw.core.query.clickhouse.model.ClickhousePrimaryKeyModel;
import cn.smallbun.screw.core.query.mysql.model.MySqlColumnModel;
import cn.smallbun.screw.core.query.mysql.model.MySqlTableModel;
import cn.smallbun.screw.core.util.Assert;
import cn.smallbun.screw.core.util.ExceptionUtils;
import cn.smallbun.screw.core.util.JdbcUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.smallbun.screw.core.constant.DefaultConstants.*;

/**
 * Clickhouse 数据库查询
 *
 * @author <a href ='1346610162@qq.com'>Wangzhuoqun</a> 2022/6/30
 * @since JDK 1.8
 */
public class ClickhouseDataBaseQuery extends AbstractDatabaseQuery {

    private List<ClickhousePrimaryKeyModel> primaryKeys;

    /**
     * 构造函数
     *
     * @param dataSource {@link DataSource}
     */
    public ClickhouseDataBaseQuery(DataSource dataSource) {
        super(dataSource);
        primaryKeys = new ArrayList<>();
    }

    /**
     * 获取数据库
     *
     * @return {@link Database} 数据库信息
     */
    @Override
    public Database getDataBase() throws QueryException {
        ClickhouseDatabaseModel model = new ClickhouseDatabaseModel();
        model.setDatabase(getCatalog());
        return model;
    }

    /**
     * 获取表信息
     *
     * @return {@link List} 所有表信息
     */
    @Override
    public List<? extends Table> getTables() throws QueryException {
        ResultSet resultSet = null;
        try {
            //查询
            resultSet = getMetaData().getTables(getCatalog(), getSchema(), PERCENT_SIGN,
                    new String[]{"TABLE"});

            List<MySqlTableModel> models = Mapping.convertList(resultSet, MySqlTableModel.class);
            // 由于clickhouse的metadata中没有表注释，因此通过系统表重新获取
            reSetTable(models);
            System.out.println(models);
            //映射
            return models;
        } catch (SQLException e) {
            throw ExceptionUtils.mpe(e);
        } finally {
            JdbcUtils.close(resultSet);
        }
    }

    /**
     * 获取列信息
     *
     * @param table {@link String} 表名
     * @return {@link List} 表字段信息
     */
    @Override
    public List<? extends Column> getTableColumns(String table) throws QueryException {
        Assert.notEmpty(table, "Table name can not be empty!");
        ResultSet resultSet = null;
        try {
            //查询
            resultSet = getMetaData().getColumns(getCatalog(), getSchema(), table, PERCENT_SIGN);
            //映射
            List<MySqlColumnModel> models = Mapping.convertList(resultSet, MySqlColumnModel.class);
            List<String> tableNames = models.stream().map(MySqlColumnModel::getTableName)
                    .collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());

            return reSetColumn(tableNames);
        } catch (SQLException e) {
            throw ExceptionUtils.mpe(e);
        } finally {
            JdbcUtils.close(resultSet);
        }
    }

    /**
     * 获取所有列信息
     *
     * @return {@link List} 表字段信息
     * @throws QueryException QueryException
     */
    @Override
    public List<? extends Column> getTableColumns() throws QueryException {
        return getTableColumns(PERCENT_SIGN);
    }

    /**
     * 根据表名获取主键信息
     *
     * @return {@link List}
     * @throws QueryException QueryException
     */
    @Override
    public List<? extends PrimaryKey> getPrimaryKeys(String table) throws QueryException {
        return primaryKeys;
    }

    @Override
    public List<ClickhousePrimaryKeyModel> getPrimaryKeys() throws QueryException {
        return primaryKeys;
    }

    private void reSetTable(List<MySqlTableModel> models) throws SQLException {
        String sql = "select comment from system.tables where database = ? and name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        String databaseName = getDataBase().getDatabase();

        for (MySqlTableModel model : models) {
            String tableName = model.getTableName();
            preparedStatement.setString(1, databaseName);
            preparedStatement.setString(2, tableName);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            model.setRemarks(resultSet.getString(1));
        }
    }

    private List<ClickhouseColumnModel> reSetColumn(List<String> tableNames) throws SQLException {
        String sql = "select name,type,position,default_kind,default_expression,comment,is_in_partition_key from system.columns where database = ? and table = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        String databaseName = getDataBase().getDatabase();
        ArrayList<ClickhouseColumnModel> models = new ArrayList<>();

        for (String tableName : tableNames) {
            preparedStatement.setString(1, databaseName);
            preparedStatement.setString(2, tableName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ClickhouseColumnModel model = new ClickhouseColumnModel();
                model.setTableName(tableName);
                model.setColumnName(resultSet.getString(1));
                model.setColumnType(resultSet.getString(2));
                model.setOrdinalPosition(resultSet.getString(3));
                model.setColumnDef(resultSet.getString(4).equals("DEFAULT") ? resultSet.getString(5) : null);
                model.setNullable(resultSet.getString(2).startsWith("Nullable") ? "1" : "0");
                model.setRemarks(resultSet.getString(6));
                models.add(model);

                if (resultSet.getString(7).equals("1")) {
                    ClickhousePrimaryKeyModel pk = new ClickhousePrimaryKeyModel();
                    pk.setTableName(tableName);
                    pk.setColumnName(resultSet.getString(1));
                    pk.setKeySeq(resultSet.getString(3));
                    primaryKeys.add(pk);
                }
            }
        }

        return models;
    }
}
