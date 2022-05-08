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
    AuthorRepository authorRepository;

    public void onStart(@Observes StartupEvent startupEvent) {
        authorRepository.save(buildAutherWithBooks());
    }

    private Author buildAutherWithBooks() {
        Book book = new Book("Tower of Light", 111);
        Book book2 = new Book("My Journey", 222);

        return new Author("Açaí Müller", Set.of(book, book2));
    }
}
