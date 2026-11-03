package jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static DBConnection instance;
    private final HikariDataSource dataSource;

    private DBConnection() {
        Properties props = new Properties();

        try (InputStream input = DBConnection.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "Файл application.properties не знайдено");
            }

            props.load(input);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Помилка читання application.properties", e);
        }

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.user"));
        config.setPassword(props.getProperty("db.password"));

        int maxSize = Integer.parseInt(
                props.getProperty("db.pool.max-size")
        );

        config.setMaximumPoolSize(maxSize);

        dataSource = new HikariDataSource(config);
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }

        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (!dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
