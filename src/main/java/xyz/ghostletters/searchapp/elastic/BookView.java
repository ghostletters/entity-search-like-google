package xyz.ghostletters.searchapp.elastic;

import xyz.ghostletters.common.entity.Author;
import xyz.ghostletters.common.entity.Book;

public record BookView(
        String author,
        String title,
        String all
) {

    public BookView(Author author, Book book) {
        this(author.getName(), book.getTitle(), author.getName() + " " + book.getTitle());
    }
}
