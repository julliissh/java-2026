package service;

import entity.BaseEntity;
import exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryCrudService<T extends BaseEntity> implements CRUDService<T> {
    private final Map<Long, T> storage = new LinkedHashMap<>();

    @Override
    public T findById(long id) {
        T entity = storage.get(id);
        if (entity == null) {
            throw new EntityNotFoundException(id);
        }
        return entity;
    }

    @Override
    public void save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Сутність не може бути null");
        }
        storage.put(entity.getId(), entity);
    }

    @Override
    public boolean delete(long id) {
        return storage.remove(id) != null;
    }

    @Override
    public List<T> findAllSorted(Comparator<? super T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Компаратор не може бути null");
        }
        List<T> sorted = findAll();
        sorted.sort(comparator);
        return sorted;
    }

    public int count() {
        return storage.size();
    }

    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }
}
