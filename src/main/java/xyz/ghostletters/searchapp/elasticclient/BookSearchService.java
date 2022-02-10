package xyz.ghostletters.searchapp.elasticclient;

import xyz.ghostletters.common.entity.Author;
import xyz.ghostletters.common.entity.Book;
import xyz.ghostletters.common.repository.AuthorRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class BookSearchService {

    @Inject
    AuthorRepository authorRepository;

    @Inject
    BookSearchViewRepository bookSearchViewRepository;

    public boolean addToIndex(Book book) {

        try {
            Optional<Author> byBook = authorRepository.findByBook(book);

            BookSearchView bookSearchView = byBook
                    .map(author -> new BookSearchView(author, book))
                    .orElseGet(BookSearchView::unknownBookSearchView);

            bookSearchViewRepository.save(bookSearchView);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
