package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    private Sql2o sql2o = new Sql2o("jdbc:h2:file:./testdb;DB_CLOSE_DELAY=-1", "", "");

//    @BeforeAll
    @BeforeEach
    public void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

//    @BeforeEach
//    public void cleanUp() {
//        try (Connection con = sql2o.open()) {
//            con.createQuery("TRUNCATE TABLE users").executeUpdate();
//        }
//    }

    @BeforeEach
    public void cleanUp() {
        try (Connection con = sql2o.open()) {
            con.createQuery("TRUNCATE TABLE users").executeUpdate();
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var user = sql2oUserRepository.save(new User(0, "456@mail.ru", "Boris", "pass123wd"));
        var savedUser = sql2oUserRepository.findByEmailAndPassword(user.get().getEmail(), user.get().getPassword());
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenSaveShouldReturnEmptyOptionalWhenEmailAlreadyExists() {
        User user1 = new User();
        user1.setEmail("test@example.com");
        user1.setName("Test User");
        user1.setPassword("password");

        Optional<User> savedUser1 = sql2oUserRepository.save(user1);
        assertTrue(savedUser1.isPresent(), "Первый пользователь должен быть успешно сохранен");

        User user2 = new User();
        user2.setEmail("test@example.com");
        user2.setName("Another User");
        user2.setPassword("password123");

        Optional<User> savedUser2 = sql2oUserRepository.save(user2);

        assertTrue(savedUser2.isEmpty(), "Ожидалось, что метод вернет пустой Optional, если почта уже существует");
    }

    @Test
    void whenFindByIncorrectEmailAndPasswordThenReturnsEmpty() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password");
        sql2oUserRepository.save(user);
        Optional<User> result = sql2oUserRepository.findByEmailAndPassword("test@mail.com", "password");
        assertTrue(result.isEmpty());
    }
}