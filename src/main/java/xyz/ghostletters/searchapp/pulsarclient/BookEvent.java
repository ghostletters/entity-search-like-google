package xyz.ghostletters.searchapp.pulsarclient;

import xyz.ghostletters.common.entity.Book;

public record BookEvent(Book after) {
}
