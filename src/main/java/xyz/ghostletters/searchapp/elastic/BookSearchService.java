package xyz.ghostletters.searchapp.elastic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import xyz.ghostletters.common.entity.Author;
import xyz.ghostletters.common.entity.Book;
import xyz.ghostletters.common.repository.AuthorRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import java.io.IOException;

import static xyz.ghostletters.searchapp.elastic.BookIndexConfig.BOOK;

@ApplicationScoped
@Transactional
public class BookSearchService {

    @Inject
    RestClient restClient;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    AuthorRepository authorRepository;

    public void addToIndex(Book book) {
        Author author = authorRepository.findByBook(book).orElseThrow();
        BookView bookView = new BookView(author, book);
        Request post = new Request("POST", BOOK + "/_doc/" + book.getIsbn());
        try {
            post.setJsonEntity(objectMapper.writeValueAsString(bookView));
            restClient.performRequest(post);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
