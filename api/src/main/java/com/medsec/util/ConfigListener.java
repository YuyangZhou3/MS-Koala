package com.medsec.util;

import com.medsec.base.Config;
import com.medsec.dao.*;
import com.medsec.entity.NotificationToken;
import com.medsec.entity.Pathology;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;

public class ConfigListener implements ServletContextListener{

    // Config file
    private static final String PROP_CONFIG = "/WEB-INF/classes/config.properties";

    // Database Profiles
    private static final String PROP_DBCP_DEPLOY = "/WEB-INF/classes/dbcp.properties";
    private static final String PROP_DBCP_DEV = "/WEB-INF/classes/dbcp_local.properties";

    public static DataSource dataSource;
    public static SqlSessionFactory sqlSessionFactory;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ServletContext app = sce.getServletContext();

            //init Log4j2
            System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");

            // Load configuration file
            System.out.println("Loading config file");
            Configurations configs = new Configurations();
            PropertiesConfiguration config = configs.properties(new File(PROP_CONFIG));
            // access configuration properties
            Config.USE_DEV_DATABASE_PROFILE = config.getBoolean("USE_DEV_DATABASE_PROFILE", Config.USE_DEV_DATABASE_PROFILE);
            System.out.println("[CONFIG] Use dev database profile: " + Config.USE_DEV_DATABASE_PROFILE);
            Config.TOKEN_SECRET_KEY = config.getString("TOKEN_SECRET_KEY", Config.TOKEN_SECRET_KEY);
            System.out.println("[CONFIG] Token secret key found: " + Config.TOKEN_SECRET_KEY);
            Config.TOKEN_TTL = config.getLong("TOKEN_TTL", Config.TOKEN_TTL);
            System.out.println("[CONFIG] Token TTL: " + Config.TOKEN_TTL);

            // Load database profile
            System.out.println("Init DB connection pool");
            String PROP_DBCP = Config.USE_DEV_DATABASE_PROFILE ? PROP_DBCP_DEV : PROP_DBCP_DEPLOY;
            Properties properties = new Properties();
            properties.load(app.getResourceAsStream(PROP_DBCP));

            // init Database
            dataSource = BasicDataSourceFactory.createDataSource(properties);
            app.setAttribute("dataSource", dataSource);

            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("development", transactionFactory, dataSource);
            Configuration configuration = new Configuration(environment);
            configuration.addMapper(UserMapper.class);
            configuration.addMapper(PatientMapper.class);
            configuration.addMapper(AppointmentMapper.class);
            configuration.addMapper(HospitalMapper.class);
            configuration.addMapper(DoctorMapper.class);
            configuration.addMapper(PathologyMapper.class);
            configuration.addMapper(RadiologyMapper.class);
            configuration.addMapper(ResourceMapper.class);
            configuration.addMapper(NotificationTokenMapper.class);
            configuration.addMapper(FileMapper.class);

            sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

            app.setAttribute("sqlSessionFactory", sqlSessionFactory);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
