package com.example.srk.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "KKSCode")
public class KKSCode {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String label;

    @DatabaseField
    private String description;

    @DatabaseField
    private String devicePower;

    @DatabaseField
    private long switchboardId;

    @DatabaseField
    private int numberOfScans;

    private String switchboardLabel;

    public KKSCode(){

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDevicePower() {
        return devicePower;
    }

    public void setDevicePower(String devicePower) {
        this.devicePower = devicePower;
    }

    public long getSwitchboardId() {
        return switchboardId;
    }

    public void setSwitchboardId(long switchboardId) {
        this.switchboardId = switchboardId;
    }

    public Long getId() {
        return id;
    }

    public String getSwitchboardLabel() {
        return switchboardLabel;
    }

    public void setSwitchboardLabel(String switchboardLabel) {
        this.switchboardLabel = switchboardLabel;
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
