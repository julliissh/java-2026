package service;

import jdbc.DBConnection;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void init() {
        String script = readScript();
        String[] queries = script.split(";");

        try (Connection c = DBConnection.getInstance().getConnection()) {
            for (String query : queries) {
                String sql = query.trim();
                if (!sql.isEmpty()) {
                    try (PreparedStatement pst = c.prepareStatement(sql)) {
                        pst.execute();
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Не вдалося виконати init.sql", e);
        }
    }

    private static String readScript() {
        try (InputStream in = DatabaseInitializer.class
                .getClassLoader()
                .getResourceAsStream("init.sql")) {

            if (in == null) {
                throw new IllegalStateException("Не знайдено init.sql");
            }

            return new String(in.readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new IllegalStateException("Помилка читання init.sql", e);
        }
    }
}
