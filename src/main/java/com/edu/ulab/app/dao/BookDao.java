package com.edu.ulab.app.dao;

import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.mapper.BookRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class BookDao {

    private final JdbcTemplate jdbcTemplate;

    public BookDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Book save(Book book) {
        final String INSERT_SQL = "insert into book(title, author, page_count, user_id) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                        ps.setString(1, book.getTitle());
                        ps.setString(2, book.getAuthor());
                        ps.setLong(3, book.getPageCount());
                        ps.setLong(4, book.getUserId());
                        return ps;
                    }
                },
                keyHolder);
        book.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return book;
    }

    public Book update(Book book) {
        final String UPDATE_SQL = "update book set title=?, author=?, page_count=? where id=?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
                    ps.setString(1, book.getTitle());
                    ps.setString(2, book.getAuthor());
                    ps.setLong(3, book.getPageCount());
                    ps.setLong(4, book.getId());
                    return ps;
                });
        return book;
    }

    public Optional<Book> findById(Long id) {
        final String FIND_SQL = "select * from book where id=?";
        return jdbcTemplate.query(FIND_SQL, new BookRowMapper(), id)
                .stream()
                .findAny();
    }

    public void delete(Book book) {
        final String DELETE_SQL = "delete from book where id=?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(DELETE_SQL);
                    ps.setLong(1, book.getId());
                    return ps;
                });
    }

    public List<Book> findAllByUserId(Long id) {
        final String FIND_ALL_SQL = "select * from book where user_id=?";
        return jdbcTemplate.query(FIND_ALL_SQL, new BookRowMapper(), id);
    }

    public void setBookOwner(Long bookId, Long personId) {
        final String UPDATE_SQL = "update book set user_id=? where id=?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
                    ps.setLong(1, personId);
                    ps.setLong(2, bookId);
                    return ps;
                });
    }

    public void releaseBook(Long bookId) {
        final String UPDATE_SQL = "update book set user_id=null where id=?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
                    ps.setLong(1, bookId);
                    return ps;
                });
    }
}
