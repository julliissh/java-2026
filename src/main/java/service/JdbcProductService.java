package service;

import entity.Category;
import jdbc.DBConnection;
import product.Product;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductService extends AbstractJdbcCrudService<Product> implements ProductService {

    @Override
    protected String getSelectByIdSql() {
        return "SELECT * FROM products WHERE id = ?";
    }

    @Override
    protected String getSelectAllSql() {
        return "SELECT * FROM products ORDER BY id";
    }

    @Override
    protected String getSelectAllSortedByNameSql() {
        return "SELECT * FROM products ORDER BY name";
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO products (id, name, category_id, price, quantity) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE products SET name = ?, category_id = ?, price = ?, quantity = ? WHERE id = ?";
    }

    @Override
    protected String getDeleteByIdSql() {
        return "DELETE FROM products WHERE id = ?";
    }

    @Override
    protected String getCountSql() {
        return "SELECT COUNT(*) FROM products";
    }

    @Override
    protected Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getBigDecimal("price").doubleValue(),
                rs.getInt("quantity"),
                rs.getLong("category_id")
        );
    }

    @Override
    protected void setInsertParameters(PreparedStatement pst, Product product) throws SQLException {
        pst.setLong(1, product.getId());
        pst.setString(2, product.getName());
        pst.setLong(3, product.getCategoryId());
        pst.setBigDecimal(4, BigDecimal.valueOf(product.getPrice()));
        pst.setInt(5, product.getQuantity());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pst, Product product) throws SQLException {
        pst.setString(1, product.getName());
        pst.setLong(2, product.getCategoryId());
        pst.setBigDecimal(3, BigDecimal.valueOf(product.getPrice()));
        pst.setInt(4, product.getQuantity());
        pst.setLong(5, product.getId());
    }

    @Override
    public List<Product> findAllSortedByNameAndByPrice() {
        return executeQuery("SELECT * FROM products ORDER BY name, price");
    }

    public List<Product> findByName(String name) {
        String sql = "SELECT * FROM products WHERE name = ?";
        List<Product> products = new ArrayList<>();

        try (Connection c = DBConnection.getInstance().getConnection();
             PreparedStatement pst = c.prepareStatement(sql)) {

            pst.setString(1, name);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Помилка пошуку товару за назвою", e);
        }

        return products;
    }

    public void createCategoryWithProducts(Category category, List<Product> products) {
        String categorySql = "INSERT INTO categories (id, name, description) VALUES (?, ?, ?)";

        try (Connection c = DBConnection.getInstance().getConnection()) {
            c.setAutoCommit(false);

            try {
                try (PreparedStatement pst = c.prepareStatement(categorySql)) {
                    pst.setLong(1, category.getId());
                    pst.setString(2, category.getName());
                    pst.setString(3, category.getDescription());
                    pst.executeUpdate();
                }

                try (PreparedStatement pst = c.prepareStatement(getInsertSql())) {
                    for (Product product : products) {
                        setInsertParameters(pst, product);
                        pst.executeUpdate();
                    }
                }

                c.commit();

            } catch (SQLException e) {
                c.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Помилка транзакції", e);
        }
    }

    public List<Product> findPage(int page, int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("Номер сторінки та розмір мають бути більшими за 0");
        }

        String sql = "SELECT * FROM products ORDER BY id ASC LIMIT ? OFFSET ?";
        List<Product> products = new ArrayList<>();

        try (Connection c = DBConnection.getInstance().getConnection();
             PreparedStatement pst = c.prepareStatement(sql)) {

            pst.setInt(1, size);
            pst.setInt(2, Math.toIntExact(((long) page - 1) * size));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Помилка посторінкового читання", e);
        }

        return products;
    }

    public void insertBatch(List<Product> products) {
        try (Connection c = DBConnection.getInstance().getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement pst = c.prepareStatement(getInsertSql())) {
                for (Product product : products) {
                    setInsertParameters(pst, product);
                    pst.addBatch();
                }

                pst.executeBatch();
                c.commit();

            } catch (SQLException e) {
                c.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Помилка пакетної вставки", e);
        }
    }
}