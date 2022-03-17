package xyz.ghostletters.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.ghostletters.common.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
