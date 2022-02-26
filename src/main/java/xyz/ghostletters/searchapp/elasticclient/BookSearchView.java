package xyz.ghostletters.searchapp.elasticclient;

import xyz.ghostletters.common.entity.Author;
import xyz.ghostletters.common.entity.Book;

public record BookSearchView(
        String author,
        String title,
        int isbn
) {

    public BookSearchView(Author author, Book book) {
        this(
                author.getName(),
                book.getTitle(),
                book.getIsbn()
        );
    }

    public static BookSearchView unknown() {
        return new BookSearchView("UNKNOWN", "UNKNOWN", -1);
    }
}
