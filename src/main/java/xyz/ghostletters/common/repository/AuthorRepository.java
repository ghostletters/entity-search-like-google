package xyz.ghostletters.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xyz.ghostletters.common.entity.Author;
import xyz.ghostletters.common.entity.Book;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("select a from Author a join a.books b where b = ?1")
    Optional<Author> findByBook(Book book);
}
