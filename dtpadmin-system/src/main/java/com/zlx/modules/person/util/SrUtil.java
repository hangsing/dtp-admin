package com.zlx.modules.person.util;


import java.io.StringReader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SrUtil {
    //local
    //private static final String JDBC_URL = "jdbc:mysql://localhost:9030/default_catalog.assets?characterEncoding=utf-8&rewriteBatchedStatements=true";
    //private static final String JDBC_USER = "root";
    //private static final String JDBC_PASSWORD = "";

    //pro
    private static final String JDBC_URL = "jdbc:mysql://192.168.249.68:8088/default_catalog.assets?characterEncoding=utf-8&rewriteBatchedStatements=true";
    private static final String JDBC_USER = "xingzi";
    private static final String JDBC_PASSWORD = "Zlx666";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static <T> List<T> executeDynamicQuery(String sql, Class<T> entityType, String flag, Object... params) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<T> resultList = new ArrayList<>();

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);

            //模糊查询
            if("fix".equals(flag)){
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, "%" + params[i] + "%");
                }
            }else {
                //精确查询
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1,  params[i] );
                }
            }

            System.out.println(preparedStatement);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                T data = entityType.getDeclaredConstructor().newInstance();

                Field[] fields = entityType.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object value = resultSet.getObject(toSnakeCase(field.getName()));
                    field.set(data, value);
                }

                resultList.add(data);
            }
        } catch (SQLException | ReflectiveOperationException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }

        return resultList;
    }

    public static <T> int executeDynamicInsert(String sql, T entity) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int rowsInserted = 0;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);

            Field[] fields = entity.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                Object value = field.get(entity);
                preparedStatement.setObject(i + 1, value);
            }

            System.out.println(preparedStatement);
            rowsInserted = preparedStatement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }

        return rowsInserted;
    }

    // Other utility methods (getConnection, closeConnection)...

    // Convert camelCase to snake_case
    private static String toSnakeCase(String input) {
        return input.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
    }
}
