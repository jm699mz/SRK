package com.example.srk.model.Config;

import com.example.srk.model.History;
import com.example.srk.model.KKSCode;
import com.example.srk.model.Note;
import com.example.srk.model.Scheme;
import com.example.srk.model.Switchboard;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class OrmliteDatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[] {Switchboard.class, KKSCode.class, Scheme.class, Note.class, History.class};

    public static void main(String[] args) throws IOException, SQLException {

        String currDirectory = "user.dir";

        String configPath = "/app/src/main/res/raw/ormlite_config.txt";

        /**
         * Gets the project root directory
         */
        String projectRoot = System.getProperty(currDirectory);

        /**
         * Full configuration path includes the project root path, and the location
         * of the ormlite_config.txt file appended to it
         */
        String fullConfigPath = projectRoot + configPath;

        File configFile = new File(fullConfigPath);

        /**
         * In the a scenario where we run this program serveral times, it will recreate the
         * configuration file each time with the updated configurations.
         */
        if(configFile.exists()) {
            configFile.delete();
            configFile = new File(fullConfigPath);
        }

        /**
         * writeConfigFile is a util method used to write the necessary configurations
         * to the ormlite_config.txt file.
         */
        writeConfigFile(configFile, classes);
    }

}
