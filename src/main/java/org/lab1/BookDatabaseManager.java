package org.lab1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the connection to the database and performs CRUD operations on the books and authors.
 * <p>
 * This class loads books and authors from the database, manages their relationships,
 * and provides methods to add, update, or delete books and authors.
 * </p>
 */
public class BookDatabaseManager {
    // Update these values as necessary.
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/books";
    private static final String USER = "root";
    private static final String PASSWORD = "1qaz2w"; // or your DB password

    private Connection conn;
    private List<Book> books;
    private List<Author> authors;

    /**
     * Constructs a BookDatabaseManager object and initializes the connection.
     */
    public BookDatabaseManager() {
        
        books = new ArrayList<>();
        authors = new ArrayList<>();
        
        try {
            connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Establishes a connection to the database.
     *
     * @throws SQLException if a database access error occurs
     */
    public void connect() throws SQLException {
        conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    /**
     * Loads all books, authors, and their relationships from the database.
     */
    public void loadData() {
        loadBooks();
        loadAuthors();
        loadRelationships();
    }

    /**
     * Loads all books from the 'titles' table.
     */
    private void loadBooks() {
        
        String sql = "SELECT * FROM titles";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                int editionNumber = rs.getInt("editionNumber");
                String copyright = rs.getString("copyright");
                Book book = new Book(isbn, title, editionNumber, copyright);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all authors from the 'authors' table.
     */
    private void loadAuthors() {
        
        String sql = "SELECT * FROM authors";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int authorID = rs.getInt("authorID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                Author author = new Author(authorID, firstName, lastName);
                authors.add(author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the relationships from the 'authorISBN' table.
     * <p>
     * For each row, finds the corresponding Book and Author and adds the relationship.
     * </p>
     */
    private void loadRelationships() {

        String sql = "SELECT * FROM authorISBN";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int authorID = rs.getInt("authorID");
                String isbn = rs.getString("isbn");
                Book book = getBookByISBN(isbn);
                Author author = getAuthorByID(authorID);
                if (book != null && author != null) {
                    book.addAuthor(author);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds and returns a Book by its ISBN.
     *
     * @param isbn the ISBN to search for
     * @return the Book if found; otherwise, null
     */
    public Book getBookByISBN(String isbn) {

        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Finds and returns an Author by its authorID.
     *
     * @param authorID the authorID to search for
     * @return the Author if found; otherwise, null
     */
    public Author getAuthorByID(int authorID) {

        for (Author a : authors) {
            if (a.getAuthorID() == authorID) {
                return a;
            }
        }
        return null;
    }

    ////////// CRUD Methods //////////

    /**
     * Inserts a new Book into the database (and its relationships) and adds it to the books list.
     *
     * @param book the Book to add
     * @return true if the insertion was successful; false otherwise
     */
    public boolean addBook(Book book) {

        String sql = "INSERT INTO titles (isbn, title, editionNumber, copyright) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setInt(3, book.getEditionNumber());
            ps.setString(4, book.getCopyright());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                books.add(book);
                // Insert relationships for each author
                for (Author author : book.getAuthorList()) {
                    addAuthorISBNRelation(author, book);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Inserts a row in the authorISBN table to create a relationship between an author and a book.
     *
     * @param author the Author
     * @param book   the Book
     * @return true if the insertion was successful; false otherwise
     */
    private boolean addAuthorISBNRelation(Author author, Book book) {

        String sql = "INSERT INTO authorISBN (authorID, isbn) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, author.getAuthorID());
            ps.setString(2, book.getIsbn());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Inserts a new Author into the database and adds it to the authors list.
     *
     * @param author the Author to add
     * @return true if the insertion was successful; false otherwise
     */
    public boolean addAuthor(Author author) {

        String sql = "INSERT INTO authors (firstName, lastName) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, author.getFirstName());
            ps.setString(2, author.getLastName());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        author.setAuthorID(generatedKeys.getInt(1));
                    }
                }
                authors.add(author);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an existing Book’s attributes in the database.
     *
     * @param book the Book to update
     * @return true if the update was successful; false otherwise
     */
    public boolean updateBook(Book book) {

        String sql = "UPDATE titles SET title = ?, editionNumber = ?, copyright = ? WHERE isbn = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setInt(2, book.getEditionNumber());
            ps.setString(3, book.getCopyright());
            ps.setString(4, book.getIsbn());
            int rowsAffected = ps.executeUpdate();
            // relationships are assumed to be maintained by the application
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an existing Author’s attributes in the database.
     *
     * @param author the Author to update
     * @return true if the update was successful; false otherwise
     */
    public boolean updateAuthor(Author author) {

        String sql = "UPDATE authors SET firstName = ?, lastName = ? WHERE authorID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, author.getFirstName());
            ps.setString(2, author.getLastName());
            ps.setInt(3, author.getAuthorID());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the list of all books loaded from the database.
     *
     * @return the list of books
     */
    public List<Book> getBooks() {
        return books;
    }

    /**
     * Returns the list of all authors loaded from the database.
     *
     * @return the list of authors
     */
    public List<Author> getAuthors() {
        return authors;
    }
}
