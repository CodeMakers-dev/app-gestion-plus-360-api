package com.gestion.plus.api.config;

import com.zaxxer.hikari.HikariConfig;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.gestion.plus.commons.repositories"}, entityManagerFactoryRef = "ptgrEntityManagerFactory", transactionManagerRef = "ptgrTransactionManager")
public class PostgresConfig {
  private static final String APPLICATION_NAME = "spring.application.name";
  
  @Autowired
  private Environment env;
  
  @Bean(name = {"ptgrEntityManagerFactory"})
  @Primary
  public LocalContainerEntityManagerFactoryBean ptgrEntityManagerFactory() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(ptgrDataSource());
    em.setPackagesToScan(new String[] { "com.gestion.plus.commons.entities" });
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter((JpaVendorAdapter)vendorAdapter);
    HashMap<String, Object> properties = new HashMap<>();
    properties.put("hibernate.hbm2ddl.auto", "update");
    properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    properties.put("hibernate.show_sql", "true");
    properties.put("hibernate.format_sql", "true");
    properties.put("hibernate.jdbc.time_zone", "UTC");
    em.setJpaPropertyMap(properties);
    em.setPersistenceUnitName("postgres");
    return em;
  }
  
  @Bean(name = {"ptgrDataSource"})
  @Primary
  public DataSource ptgrDataSource() {
      HikariConfig config = new HikariConfig();
      String applicationName = "?ApplicationName=" + this.env.getProperty("spring.application.name");
      config.setJdbcUrl(this.env.getProperty("spring.gestion.datasource.url") + applicationName);
      config.setDriverClassName(this.env.getProperty("spring.gestion.datasource.driver-class-name"));
      config.setUsername(this.env.getProperty("spring.gestion.datasource.username"));
      config.setPassword(this.env.getProperty("spring.gestion.datasource.password"));

      return new HikariDataSource(config);
  }
  
  @Bean(name = {"ptgrTransactionManager"})
  @Primary
  public PlatformTransactionManager ptgrTransactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(ptgrEntityManagerFactory().getObject());
    return (PlatformTransactionManager)transactionManager;
  }
}