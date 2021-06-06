package com.example.srk.model.Config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.srk.model.History;
import com.example.srk.model.KKSCode;
import com.example.srk.model.Note;
import com.example.srk.model.Scheme;
import com.example.srk.model.Switchboard;
import com.example.srk.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "switchboards.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Switchboard, Long> switchboardsDao;
    private Dao<KKSCode, Long> kksCodesDao;
    private Dao<Scheme, Long> schemeDao;
    private Dao<Note, Long> noteDao;
    private Dao<History, Long> historyDao;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Switchboard.class);
            TableUtils.createTable(connectionSource, KKSCode.class);
            TableUtils.createTable(connectionSource, Scheme.class);
            TableUtils.createTable(connectionSource, Note.class);
            TableUtils.createTable(connectionSource, History.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, KKSCode.class, true);
            TableUtils.dropTable(connectionSource, Switchboard.class, true);
            TableUtils.dropTable(connectionSource, Scheme.class, true);
            TableUtils.dropTable(connectionSource, Note.class, true);
            TableUtils.dropTable(connectionSource, History.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Switchboard, Long> switchboardDao() throws SQLException {
        if (switchboardsDao == null) {
            switchboardsDao = getDao(Switchboard.class);
        }
        return switchboardsDao;
    }

    public Dao<KKSCode, Long> kksCodeDao() throws SQLException{
        if(kksCodesDao == null){
            kksCodesDao = getDao(KKSCode.class);
        }
        return kksCodesDao;
    }

    public Dao<Scheme, Long> schemeDao() throws SQLException{
        if(schemeDao == null){
            schemeDao = getDao(Scheme.class);
        }
        return schemeDao;
    }

    public Dao<Note, Long> noteDao() throws SQLException{
        if(noteDao == null){
            noteDao = getDao(Note.class);
        }

        return noteDao;
    }

    public Dao<History, Long> historyDao() throws SQLException{
        if(historyDao == null){
            historyDao = getDao(History.class);
        }

        return historyDao;
    }

    public void createSwitchboard(Switchboard switchboard) throws SQLException {
        if (switchboardDao().queryForEq("label", switchboard.getLabel()).isEmpty()) {
            switchboardDao().create(switchboard);
        }
    }

    public void createKKSCode(KKSCode kksCode) throws SQLException {
        if (kksCodeDao().queryForEq("label", kksCode.getLabel()).isEmpty()) {
            kksCodeDao().create(kksCode);
        }
    }

    public void createScheme(Scheme scheme) throws SQLException {
        if(schemeDao().queryForEq("label", scheme.getLabel()).isEmpty()){
            schemeDao().create(scheme);
        }
    }

    public void createNote(Note note) throws SQLException{
        noteDao().create(note);
    }

    public void createHistory(History history) throws SQLException {
        historyDao().create(history);
    }

    public void deleteHistory() throws SQLException{
        historyDao().deleteBuilder().delete();
    }

    public List<Note> getNotes() throws SQLException{
        return noteDao().queryForAll();
    }

    public Note getNoteByContent(String content) throws SQLException{
        return noteDao().queryForEq("content", content).get(0);
    }

    public boolean noteExists(Note note) throws SQLException{
        return noteDao().queryForEq("createdBy", note.getCreatedBy()).size() > 0 &&
                noteDao().queryForEq("creationTime", note.getCreationTime()).size() > 0;
    }

    public boolean kksCodeExists(KKSCode kksCode) throws SQLException{
        return kksCodeDao().queryForEq("label", kksCode.getLabel()).size() > 0;
    }

    public boolean schemeExists(Scheme scheme) throws SQLException{
        return schemeDao().queryForEq("label", scheme.getLabel()).size() > 0;
    }

    public boolean switchboardExists(String switchboardLabel) throws SQLException{
        return switchboardDao().queryForEq("label", switchboardLabel).size() > 0;
    }

    public void deleteNoteById(Long id) throws SQLException {
        noteDao().deleteById(id);
    }

    public void updateNote(Long id, String content) throws SQLException{
        UpdateBuilder<Note, Long> updateBuilder = noteDao().updateBuilder();
        updateBuilder.updateColumnValue("content", content);
        updateBuilder.where().idEq(id);
        updateBuilder.update();
    }

    public void updateSwitchboardScans(String label) throws SQLException {
        Switchboard switchboard = getSwitchboardByLabel(label);
        UpdateBuilder<Switchboard, Long> updateBuilder = switchboardDao().updateBuilder();
        updateBuilder.updateColumnValue("numberOfScans", switchboard.getNumberOfScans() + 1);
        updateBuilder.where().eq("label", label);
        updateBuilder.update();
    }

    public void resetSwitchboardScans(String label) throws SQLException {
        UpdateBuilder<Switchboard, Long> updateBuilder = switchboardDao().updateBuilder();
        updateBuilder.updateColumnValue("numberOfScans", 0);
        updateBuilder.where().eq("label", label);
        updateBuilder.update();
    }

    public void resetKKSCodeScans(String label) throws SQLException{
        UpdateBuilder<KKSCode, Long> updateBuilder = kksCodeDao().updateBuilder();
        updateBuilder.updateColumnValue("numberOfScans", 0);
        updateBuilder.where().eq("label", label);
        updateBuilder.update();
    }

    public void updateKKSCodeScans(String label) throws SQLException {
        KKSCode kksCode = getKKSCodeByLabel(label);
        UpdateBuilder<KKSCode, Long> updateBuilder = kksCodeDao().updateBuilder();
        updateBuilder.updateColumnValue("numberOfScans", kksCode.getNumberOfScans() + 1);
        updateBuilder.where().eq("label", label);
        updateBuilder.update();
    }

    public void deleteCodeByLabel(String label) throws SQLException{
        DeleteBuilder<KKSCode, Long> deleteBuilder = kksCodeDao().deleteBuilder();
        deleteBuilder.where().eq("label", label);
        deleteBuilder.delete();
    }

    public void deleteSchemeByLabel(String label) throws SQLException{
        DeleteBuilder<Scheme, Long> deleteBuilder = schemeDao().deleteBuilder();
        deleteBuilder.where().eq("label", label);
        deleteBuilder.delete();
    }


    public Scheme getSchemeByLabel(String label) throws SQLException {
        return schemeDao().queryForEq("label", label).get(0);
    }


    public Long getSwitchBoardId(Switchboard switchboard) throws SQLException {
        List<Switchboard> switchboards = switchboardDao().queryForEq("label", switchboard.getLabel());
        //List<Switchboard> switchboards = switchboardDao().queryBuilder().where().eq("label", switchboard.label).query();

        if(!switchboards.isEmpty()) {
            return switchboards.get(0).getId();
        }

        return null;
    }

    public KKSCode getKKSCodeByLabel(String label) throws SQLException{
        return kksCodeDao().queryForEq("label", label).get(0);
    }

    public Switchboard getSwitchboardByLabel(String label) throws SQLException {
        return switchboardDao().queryForEq("label", label).get(0);
    }

    public String getCodeDescriptionByLabel(String label) throws SQLException {
        return kksCodeDao().queryForEq("label", label).get(0).getDescription();
    }

    public Long getSwitchboardIdByLabel(String label) throws SQLException {
        return switchboardDao().queryForEq("label", label).get(0).getId();
    }

    public Switchboard getSwitchboardById(Long id) throws SQLException{
        return switchboardDao().queryForEq("id", id).get(0);
    }

    public List<Scheme> getSchemes() throws SQLException {
        return  schemeDao().queryForAll();
    }

    public List<KKSCode> getKKSCodes() throws SQLException{
        return kksCodeDao().queryForAll();
    }

    public List<History> getHistory() throws SQLException{
        return historyDao().queryForAll();
    }
}
