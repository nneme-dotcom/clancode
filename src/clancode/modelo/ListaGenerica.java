package clancode.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase genérica que encapsula una lista y proporciona
 * operaciones comunes de CRUD.
 * @param <T> Tipo de elementos que contendrá la lista
 */
public class ListaGenerica<T> {
    private List<T> lista;

    public ListaGenerica() {
        this.lista = new ArrayList<>();
    }

    public void añadir(T elemento) {
        lista.add(elemento);
    }

    public void eliminar(T elemento) {
        lista.remove(elemento);
    }

    public T obtener(int indice) {
        return lista.get(indice);
    }

    public int tamaño() {
        return lista.size();
    }

    public boolean estaVacia() {
        return lista.isEmpty();
    }

    public List<T> getLista() {
        return lista;
    }
}