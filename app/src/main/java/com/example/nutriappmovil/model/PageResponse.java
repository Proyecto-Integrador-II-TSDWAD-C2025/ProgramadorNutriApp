package com.example.nutriappmovil.model;

import java.util.List;

/**
 * Respuesta paginada de Django REST (PageNumberPagination, 10 por página):
 * { "count": 25, "next": "...?page=2", "previous": null, "results": [...] }
 * Sirve para cualquier listado: PageResponse<Plan>, PageResponse<Usuario>, etc.
 */
public class PageResponse<T> {

    private int count;
    private String next;
    private String previous;
    private List<T> results;

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<T> getResults() {
        return results;
    }

    public boolean hasNext() {
        return next != null;
    }
}