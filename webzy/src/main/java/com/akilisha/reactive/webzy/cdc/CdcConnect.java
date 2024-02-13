package com.akilisha.reactive.webzy.cdc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

public class CdcConnect {

    public static CdcConnect instance;

    static {
        try {
            instance = new CdcConnect("/jdbc-session.properties");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private final Properties props = new Properties();

    private CdcConnect(String file) throws IOException, InterruptedException {
        props.load(CdcConnect.class.getResourceAsStream(file));
        PgSqlEngine.instance.listen();
    }

    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("jdbc.url"));
        config.setUsername(props.getProperty("jdbc.user"));
        config.setPassword(props.getProperty("jdbc.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }
}
