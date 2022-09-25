package com.edu.ulab.app.dao;

import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.PersonRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.Objects;
import java.util.Optional;

@Component
public class PersonDao {
    private final JdbcTemplate jdbcTemplate;

    public PersonDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Person save(Person person) {
        final String INSERT_SQL = "insert into person(full_name, title, age) values(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, person.getFullName());
                    ps.setString(2, person.getTitle());
                    ps.setLong(3, person.getAge());
                    return ps;
                }, keyHolder);
        person.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return person;
    }

    public Person update(Person person) {
        final String UPDATE_SQL = "update person set full_name=?, title=?, age=? where id=?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
                    ps.setString(1, person.getFullName());
                    ps.setString(2, person.getTitle());
                    ps.setLong(3, person.getAge());
                    ps.setLong(4, person.getId());
                    return ps;
                });
        return person;
    }

    public Optional<Person> findById(Long id) {
        final String FIND_SQL = "select * from person where id=?";
        return jdbcTemplate.query(FIND_SQL, new PersonRowMapper(), id)
                .stream()
                .findAny();
    }

    public void delete(Person person) {
        final String DELETE_SQL = "delete from person where id=?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(DELETE_SQL);
                    ps.setLong(1, person.getId());
                    return ps;
                });
    }
}
