package service;

import entity.BaseEntity;
import exception.EntityNotFoundException;
import jdbc.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractJdbcCrudService<T extends BaseEntity> implements CRUDService<T> {

    protected abstract String getSelectByIdSql();
    protected abstract String getSelectAllSql();
    protected abstract String getSelectAllSortedByNameSql();
    protected abstract String getInsertSql();
    protected abstract String getUpdateSql();
    protected abstract String getDeleteByIdSql();
    protected abstract String getCountSql();

    protected abstract T mapRow(ResultSet rs) throws SQLException;
    protected abstract void setInsertParameters(PreparedStatement pst, T entity) throws SQLException;
    protected abstract void setUpdateParameters(PreparedStatement pst, T entity) throws SQLException;

    @Override
    public T findById(long id) {
        try (Connection c = DBConnection.getInstance().getConnection();
             PreparedStatement pst = c.prepareStatement(getSelectByIdSql())) {

            pst.setLong(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка пошуку за id=" + id, e);
        }

        throw new EntityNotFoundException(id);
    }

    @Override
    public void save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Сутність не може бути null");
        }

        try (Connection c = DBConnection.getInstance().getConnection()) {

            if (entity.getId() > 0) {
                try (PreparedStatement pst = c.prepareStatement(getUpdateSql())) {
                    setUpdateParameters(pst, entity);

                    if (pst.executeUpdate() > 0) {
                        return;
                    }
                }
            }

            try (PreparedStatement pst = c.prepareStatement(getInsertSql())) {
                setInsertParameters(pst, entity);
                pst.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Помилка збереження", e);
        }
    }

    @Override
    public boolean delete(long id) {
        try (Connection c = DBConnection.getInstance().getConnection();
             PreparedStatement pst = c.prepareStatement(getDeleteByIdSql())) {

            pst.setLong(1, id);
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Помилка видалення", e);
        }
    }

    public List<T> findAll() {
        return executeQuery(getSelectAllSql());
    }

    @Override
    public List<T> findAllSortedByName() {
        return executeQuery(getSelectAllSortedByNameSql());
    }

    @Override
    public List<T> findAllSorted(Comparator<? super T> comparator) {
        List<T> list = findAll();
        list.sort(comparator);
        return list;
    }

    public long count() {
        try (Connection c = DBConnection.getInstance().getConnection();
             PreparedStatement pst = c.prepareStatement(getCountSql());
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Помилка підрахунку кількості", e);
        }

        return 0;
    }

    protected List<T> executeQuery(String sql) {
        List<T> list = new ArrayList<>();

        try (Connection c = DBConnection.getInstance().getConnection();
             PreparedStatement pst = c.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Помилка вибірки списку", e);
        }

        return list;
    }
}