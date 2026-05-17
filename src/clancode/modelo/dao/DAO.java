package clancode.modelo.dao;
import java.util.List;

public interface DAO<T, K> {
    void insertar(T t) throws Exception;
    T obtener(K id) throws Exception;
    List<T> obtenerTodos() throws Exception;
    void eliminar(K id) throws Exception;
}