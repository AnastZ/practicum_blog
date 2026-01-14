package ru.yandex.practicum.repository;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource; // Для чтения application.properties
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.hibernate.HibernateTransactionManager;
import org.springframework.orm.jpa.hibernate.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)

public class HibernateConfig {
    @Autowired
    private Environment env;

    /** Настройка DataSource (источника данных).
     *
     * @return
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }

    /** Настройка SessionFactory (фабрики сессий Hibernate).
     *
     * @return
     */
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("ru.yandex.practicum.model.entity");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    /** Свойства Hibernate
     *
     * @return
     */
    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));
        properties.setProperty("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
        properties.setProperty("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql"));
        properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.setProperty("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
        properties.setProperty("hibernate.current_session_context_class",
                "org.springframework.orm.jpa.hibernate.SpringSessionContext");
        properties.setProperty("hibernate.connection.handling_mode", "DELAYED_ACQUISITION_AND_HOLD");

        properties.setProperty("hibernate.transaction.coordinator_class",
                "jdbc");
        properties.setProperty("hibernate.cache.use_second_level_cache", env.getProperty("spring.jpa.properties.hibernate.cache.use_second_level_cache"));
        properties.setProperty("hibernate.cache.use_query_cache", env.getProperty("spring.jpa.properties.hibernate.cache.use_query_cache"));
        return properties;
    }

    /**
     * Менеджер транзакций. Для того, чтобы Spring сам создавал транзакцию, если метод отмечен аннотацией org.springframework.transaction.annotation.Transactional.
     * @param sessionFactory
     * @return
     */
    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        transactionManager.setValidateExistingTransaction(true);
        transactionManager.setNestedTransactionAllowed(true);
        return transactionManager;
    }
}
