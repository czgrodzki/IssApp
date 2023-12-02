package org.example.model;

import java.util.List;

public class Crew {

    private List<String> names;


    public Crew(final List<String> names) {
        this.names = names;
    }

    public List<String> getNames() {
        return names;
    }

    @Override
    public String toString() {
        return "Crew{" +
                "names=" + names +
                '}';
    }
}
