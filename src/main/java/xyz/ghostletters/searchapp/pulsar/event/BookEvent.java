package xyz.ghostletters.searchapp.pulsar.event;

import xyz.ghostletters.common.entity.Book;

public class BookEvent extends AbstractChangeEvent {

    private Book before;
    private Book after;

    public Book getBefore() {
        return before;
    }

    public void setBefore(Book before) {
        this.before = before;
    }

    public Book getAfter() {
        return after;
    }

    public void setAfter(Book after) {
        this.after = after;
    }
}
