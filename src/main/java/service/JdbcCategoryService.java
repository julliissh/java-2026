
package service;

import entity.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcCategoryService extends AbstractJdbcCrudService<Category> {

    @Override
    protected String getSelectByIdSql() {
        return "SELECT id, name, description FROM categories WHERE id = ?";
    }

    @Override
    protected String getSelectAllSql() {
        return "SELECT id, name, description FROM categories ORDER BY id ASC";
    }

    @Override
    protected String getSelectAllSortedByNameSql() {
        return "SELECT id, name, description FROM categories ORDER BY name ASC";
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO categories (id, name, description) VALUES (?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE categories SET name = ?, description = ? WHERE id = ?";
    }

    @Override
    protected String getDeleteByIdSql() {
        return "DELETE FROM categories WHERE id = ?";
    }

    @Override
    protected String getCountSql() {
        return "SELECT COUNT(*) FROM categories";
    }

    @Override
    protected Category mapRow(ResultSet rs) throws SQLException {
        return new Category(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description")
        );
    }

    @Override
    protected void setInsertParameters(PreparedStatement pst, Category category)throws SQLException {
        pst.setLong(1, category.getId());
        pst.setString(2, category.getName());
        pst.setString(3, category.getDescription());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pst, Category category) throws SQLException {
        pst.setString(1, category.getName());
        pst.setString(2, category.getDescription());
        pst.setLong(3, category.getId());
    }
}
