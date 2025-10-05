package database.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, T> {
    Optional<T> findById(K id);
    List<T> findAll();
    void save(T entity);
    void delete(K id);
}
