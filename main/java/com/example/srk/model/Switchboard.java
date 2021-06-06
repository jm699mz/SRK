package com.example.srk.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Switchboard")
public class Switchboard {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String label;

    @DatabaseField
    private int numberOfScans;

    public Switchboard(){

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumberOfScans() {
        return numberOfScans;
    }

    public void setNumberOfScans(int numberOfScans) {
        this.numberOfScans = numberOfScans;
    }

}
