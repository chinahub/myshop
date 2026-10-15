package com.rocket.myshop.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
* @FullClassName com.rocket.myshop.common.utils.SqlScriptRunner
* @Description: sql脚本版本化管理
* @author  Liu Jie
* @date 2016年11月22日 上午12:42:51 
* @version V1.0.0
 */
public class SqlScriptRunner {
    private String driver;
    private String url;
    private String userid;
    private String password;
    private String sourceFile;

    public SqlScriptRunner(String sourceFile, String driver, String url,
            String userId, String password) throws Exception {
        
        if (sourceFile == null || sourceFile.length() == 0) {
            throw new Exception("SQL script file is required");
        }
        
        if (driver == null || driver.length() == 0) {
            throw new Exception("JDBC Driver is required");
        }
        
        if (url == null || url.length() == 0) {
            throw new Exception("JDBC URL is required");
        }
        
        this.sourceFile = sourceFile;
        this.driver = driver;
        this.url = url;
        this.userid = userId;
        this.password = password;
    }

    public void executeScript() throws Exception {

        Connection connection = null;

        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, userid, password);

            Statement statement = connection.createStatement();

            BufferedReader br = getScriptReader();

            String sql;

            while ((sql = readStatement(br)) != null) {
                statement.execute(sql);
            }

            closeStatement(statement);
            connection.commit();
            br.close();
        } finally {
            closeConnection(connection);
        }
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore
                ;
            }
        }
    }

    private void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                // ignore
                ;
            }
        }
    }

    private String readStatement(BufferedReader br) throws IOException {
        StringBuffer sb = new StringBuffer();

        String line;

        while ((line = br.readLine()) != null) {
            if (line.startsWith("--")) { //$NON-NLS-1$
                continue;
            }

            if (line == null || line.length() == 0) {
                continue;
            }

            if (line.endsWith(";")) { //$NON-NLS-1$
                sb.append(line.substring(0, line.length() - 1));
                break;
            } else {
                sb.append(' ');
                sb.append(line);
            }
        }

        String s = sb.toString().trim();

        return s.length() > 0 ? s : null;
    }
    
    private BufferedReader getScriptReader() throws Exception {
        BufferedReader answer;
        
        if (sourceFile.startsWith("classpath:")) {
            String resource = sourceFile.substring("classpath:".length());
            InputStream is = 
                Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            if (is == null) {
                throw new Exception("SQL script file does not exist: " + resource);
            }
            answer = new BufferedReader(new InputStreamReader(is));
        } else {
            File file = new File(sourceFile);
            if (!file.exists()) {
                throw new Exception("SQL script file does not exist");
            }
            answer = new BufferedReader(new FileReader(file));
        }
        
        return answer;
    }
}
