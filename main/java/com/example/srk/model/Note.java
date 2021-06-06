package com.example.srk.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Note")
public class Note {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String content;

    @DatabaseField
    private String createdBy;

    @DatabaseField
    private String creationTime;

    @DatabaseField
    private String kksCodeLabel;

    @DatabaseField
    private String firebaseId;

    public Note(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }
    public String getKksCodeLabel() {
        return kksCodeLabel;
    }

    public void setKksCodeLabel(String kksCodeLabel) {
        this.kksCodeLabel = kksCodeLabel;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }


}
