
package com.example.nutriappmovil.model;

import java.util.List;

public class UsuariosResponse {

    private int count;
    private String next;
    private String previous;
    private List<Usuario> results;

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<Usuario> getResults() {
        return results;
    }
}
