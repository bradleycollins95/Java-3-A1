package org.lab1;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an author in the books database.
 * <p>
 * Each Author has an ID, first name, last name, and a list of books that the author has written.
 * </p>
 */
public class Author {
    private int authorID;
    private String firstName;
    private String lastName;
    private List<Book> bookList;

    /**
     * Constructs an Author object with the given details.
     *
     * @param authorID  the unique identifier for the author
     * @param firstName the author's first name
     * @param lastName  the author's last name
     */
    public Author(int authorID, String firstName, String lastName) {
        this.authorID = authorID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bookList = new ArrayList<>();
    }
    
    /**
     * Returns the author's ID.
     *
     * @return the authorID
     */
    public int getAuthorID() {
        return authorID;
    }

    /**
     * Sets the author's ID.
     *
     * @param authorID the new authorID
     */
    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    /**
     * Returns the author's first name.
     *
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the author's first name.
     *
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the author's last name.
     *
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the author's last name.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the list of books associated with the author.
     *
     * @return the bookList
     */
    public List<Book> getBookList() {
        return bookList;
    }

    /**
     * Adds a book to the author's list if it is not already present.
     * Also adds this author to the book's list to keep both sides of the relationship in sync.
     *
     * @param book the Book to add
     */
    public void addBook(Book book) {
        if (!bookList.contains(book)) {
            bookList.add(book);
            if (!book.getAuthorList().contains(this)) {
                book.addAuthor(this);
            }
        }
    }

    /**
     * Returns a string representation of the author, including the list of books.
     *
     * @return a string containing author details and their books
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Author ID: ").append(authorID)
                .append(", Name: ").append(firstName).append(" ").append(lastName);
        if (!bookList.isEmpty()) {
            sb.append("\nBooks: ");
            for (Book book : bookList) {
                sb.append(book.getTitle()).append(" (").append(book.getIsbn()).append("), ");
            }
            // remove trailing comma and space.
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
}
