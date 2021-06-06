package com.example.srk.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "History")
public class History {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String creationTime;

    @DatabaseField
    private String codeLabel;

    @DatabaseField
    private String scannedBy;

    public History(){

    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getCodeLabel() {
        return codeLabel;
    }

    public void setCodeLabel(String codeLabel) {
        this.codeLabel = codeLabel;
    }

    public String getScannedBy() {
        return scannedBy;
    }

    public void setScannedBy(String scannedBy) {
        this.scannedBy = scannedBy;
    }
}
