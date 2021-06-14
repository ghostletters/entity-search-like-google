package xyz.ghostletters.common.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Author extends AbstractEntity {
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "author_id")
    private Set<Book> books = new HashSet<>();

    Author(){} // needed for JPA

    public Author(String name, Set<Book> books) {
        this.name = name;
        this.books = books;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public Set<Book> addBook(Book book) {
        books.add(book);
        return books;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
