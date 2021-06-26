package xyz.ghostletters.searchapp.event;

import xyz.ghostletters.common.entity.Book;

public class BookChangeEvent {

    private Book after;

    public Book getAfter() {
        return after;
    }
}
