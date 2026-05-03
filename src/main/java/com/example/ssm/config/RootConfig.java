package com.example.ssm.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
// Enables Spring's @Transactional support in service methods.
@EnableTransactionManagement
// Find @Service classes, such as BookService, and register them as Spring beans.
@ComponentScan(basePackages = "com.example.ssm.service")
// Find MyBatis mapper interfaces and create mapper proxy objects for them.
@MapperScan("com.example.ssm.mapper")
public class RootConfig {
    @Bean
    public DataSource dataSource() {
        // H2 lives in memory. schema.sql creates tables, data.sql inserts demo rows at startup.
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:data.sql")
                .build();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        // MyBatis uses the same database connection pool/DataSource managed by Spring.
        factoryBean.setDataSource(dataSource);
        // Allows mapper XML to write type="Book" instead of the full package name.
        factoryBean.setTypeAliasesPackage("com.example.ssm.domain");
        // Load SQL mapper XML files from src/main/resources/mappers.
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:mappers/*.xml"));
        return factoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        // This transaction manager makes @Transactional work with JDBC/MyBatis.
        return new DataSourceTransactionManager(dataSource);
    }
}
