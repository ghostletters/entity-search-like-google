package xyz.ghostletters.common.entity;

import javax.persistence.Entity;

@Entity
public class Book extends AbstractEntity {
    private String title;

    private int isbn;

    Book(){} // needed for JPA

    public Book(String title, int isbn) {
        this.title = title;
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIsbn() {
        return isbn;
    }

    public void setIsbn(int isbn) {
        this.isbn = isbn;
    }
}
