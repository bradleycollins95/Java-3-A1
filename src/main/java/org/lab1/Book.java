package org.lab1;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a book from the titles table in the books database.
 * <p>
 * Each Book has an ISBN, title, edition number, copyright, and a list of authors.
 * </p>
 */
public class Book {
    private String isbn;
    private String title;
    private int editionNumber;
    private String copyright;
    private List<Author> authorList;

    /**
     * Constructs a Book object with the given details.
     *
     * @param isbn          the ISBN of the book
     * @param title         the title of the book
     * @param editionNumber the edition number of the book
     * @param copyright     the copyright information
     */
    public Book(String isbn, String title, int editionNumber, String copyright) {
        this.isbn = isbn;
        this.title = title;
        this.editionNumber = editionNumber;
        this.copyright = copyright;
        this.authorList = new ArrayList<>();
    }

    /**
     * Returns the ISBN of the book.
     *
     * @return the isbn
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the book.
     *
     * @param isbn the new ISBN
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Returns the title of the book.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the edition number of the book.
     *
     * @return the editionNumber
     */
    public int getEditionNumber() {
        return editionNumber;
    }

    /**
     * Sets the edition number of the book.
     *
     * @param editionNumber the new edition number
     */
    public void setEditionNumber(int editionNumber) {
        this.editionNumber = editionNumber;
    }

    /**
     * Returns the copyright information of the book.
     *
     * @return the copyright
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * Sets the copyright information of the book.
     *
     * @param copyright the new copyright info
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    /**
     * Returns the list of authors associated with the book.
     *
     * @return the authorList
     */
    public List<Author> getAuthorList() {
        return authorList;
    }

    /**
     * Adds an author to this book's list if it is not already present.
     * Also adds this book to the author's list to keep both sides of the relationship in sync.
     *
     * @param author the Author to add
     */
    public void addAuthor(Author author) {
        if (!authorList.contains(author)) {
            authorList.add(author);
            if (!author.getBookList().contains(this)) {
                author.addBook(this);
            }
        }
    }

    /**
     * Returns a string representation of the book, including the list of authors.
     *
     * @return a string containing book details and its authors
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ISBN: ").append(isbn)
                .append(", Title: ").append(title)
                .append(", Edition: ").append(editionNumber)
                .append(", Copyright: ").append(copyright);
        if (!authorList.isEmpty()) {
            sb.append("\nAuthors: ");
            for (Author author : authorList) {
                sb.append(author.getFirstName()).append(" ")
                        .append(author.getLastName()).append(", ");
            }
            // remove trailing comma and space
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
}


