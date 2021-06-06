package com.example.srk.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Scheme")
public class Scheme {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String label;

    @DatabaseField
    private String url;

    public Scheme(){

    }

    public Scheme(String label, String url){
        this.label = label;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
