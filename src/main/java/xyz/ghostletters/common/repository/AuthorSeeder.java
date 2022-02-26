package xyz.ghostletters.common.repository;

import io.quarkus.runtime.StartupEvent;
import xyz.ghostletters.common.entity.Author;
import xyz.ghostletters.common.entity.Book;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Set;

@ApplicationScoped
public class AuthorSeeder {

    @Inject
    AuthorRepository autherRepository;

    public void onStart(@Observes StartupEvent startupEvent) {
        autherRepository.save(buildAutherWithBooks());
    }

    private Author buildAutherWithBooks() {
        Book book = new Book("Cien años de soledad", 1);
        Book book2 = new Book("Crónica de una muerte anunciada", 2);

        return new Author("Gabriel García Márquez", Set.of(book, book2));
    }
}
