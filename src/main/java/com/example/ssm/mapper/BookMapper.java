package com.example.ssm.mapper;

import com.example.ssm.domain.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MyBatis data access interface.
 *
 * <p>Each method name here is matched with a SQL statement id in
 * src/main/resources/mappers/BookMapper.xml.</p>
 */
public interface BookMapper {
    List<Book> findAll();

    Book findById(@Param("id") Long id);

    int insert(Book book);

    int update(Book book);

    int deleteById(@Param("id") Long id);
}
