package com.globant.db.mybatis;

import com.globant.db.mybatis.mapper.SimpleTestMapper;
import com.globant.db.entity.SimpleTest;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.TypeAliasRegistry;

import javax.sql.DataSource;


public class MyBatisConfig {

    public  SqlSessionFactory getSqlSessionFactory() {
        // Get DataSource object.
        DataSource dataSource = getDataSource();

        // Creates a transaction factory.
        TransactionFactory trxFactory = new JdbcTransactionFactory();

        // Creates an environment object with the specified name, transaction
        // factory and a data source.
        Environment env = new Environment("dev", trxFactory, dataSource);

        // Creates a Configuration object base on the Environment object.
        // We can also add type aliases and mappers.
        Configuration config = new Configuration(env);
        TypeAliasRegistry aliases = config.getTypeAliasRegistry();
        aliases.registerAlias("record", SimpleTest.class);

        config.addMapper(SimpleTestMapper.class);

        // Build the SqlSessionFactory based on the created Configuration object.
        // Open a session and query a record using the RecordMapper.
        return new SqlSessionFactoryBuilder().build(config);
    }

    /**
     * Returns a DataSource object.
     *
     * @return a DataSource.
     */
    public  DataSource getDataSource() {
        final HikariConfig hikariConfig = new  HikariConfig();
        hikariConfig.setMaximumPoolSize(30);
        hikariConfig.setPoolName("Simple");
        hikariConfig.setConnectionTimeout(1000);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setAutoCommit(true);
        hikariConfig.setLeakDetectionThreshold(100);
        hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
        hikariConfig.setJdbcUrl("jdbc:mariadb://localhost:3306/compare-database");
        hikariConfig.setUsername("compare-user");
        hikariConfig.setPassword("userpassword123!");
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        return dataSource;
    }

    public static DataSource getMyBatisDataSource(){
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver("org.mariadb.jdbc.Driver");
        dataSource.setUrl("jdbc:mariadb://localhost:3306/compare-database");
        dataSource.setUsername("compare-user");
        dataSource.setPassword("userpassword123!");
        return dataSource;
    }
    public static DataSource getUnpooledDataSource(){
        UnpooledDataSource dataSource = new UnpooledDataSource();
        dataSource.setDriver("org.mariadb.jdbc.Driver");
        dataSource.setUrl("jdbc:mariadb://localhost:3306/compare-database");
        dataSource.setUsername("compare-user");
        dataSource.setPassword("userpassword123!");
        return dataSource;
    }
}
