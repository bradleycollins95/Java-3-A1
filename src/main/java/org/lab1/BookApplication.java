package org.lab1;

import java.util.List;
import java.util.Scanner;

/**
 * The main application class that provides a text-based menu for interacting with the book database.
 * <p>
 * The user can:
 * <ul>
 *   <li>Print all books (with authors)</li>
 *   <li>Print all authors (with books)</li>
 *   <li>Edit a book's attributes</li>
 *   <li>Edit an author's attributes</li>
 *   <li>Add a book (with existing or new authors)</li>
 *   <li>Quit the application</li>
 * </ul>
 * </p>
 */
public class BookApplication {
    /**
     * The main method that starts the application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        BookDatabaseManager dbManager = new BookDatabaseManager();
        dbManager.loadData();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("\nPlease select an option:");
            System.out.println("1. Print all books (with authors)");
            System.out.println("2. Print all authors (with books)");
            System.out.println("3. Edit a book's attributes");
            System.out.println("4. Edit an author's attributes");
            System.out.println("5. Add a book");
            System.out.println("6. Quit");
            System.out.print("Your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    printAllBooks(dbManager);
                    break;
                case "2":
                    printAllAuthors(dbManager);
                    break;
                case "3":
                    editBook(dbManager, scanner);
                    break;
                case "4":
                    editAuthor(dbManager, scanner);
                    break;
                case "5":
                    addBook(dbManager, scanner);
                    break;
                case "6":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
        System.out.println("Exiting application.");
    }

    /**
     * Prints all books along with their associated authors.
     *
     * @param dbManager the BookDatabaseManager managing the data
     */
    private static void printAllBooks(BookDatabaseManager dbManager) {
        List<Book> books = dbManager.getBooks();
        if (books.isEmpty()) {
            System.out.println("No books found.");
        } else {
            for (Book book : books) {
                System.out.println(book);
                System.out.println("------------------------------");
            }
        }
    }

    /**
     * Prints all authors along with the books they have written.
     *
     * @param dbManager the BookDatabaseManager managing the data
     */
    private static void printAllAuthors(BookDatabaseManager dbManager) {
        List<Author> authors = dbManager.getAuthors();
        if (authors.isEmpty()) {
            System.out.println("No authors found.");
        } else {
            for (Author author : authors) {
                System.out.println(author);
                System.out.println("------------------------------");
            }
        }
    }

    /**
     * Provides a prompt to edit a book's attributes and updates it in the database.
     *
     * @param dbManager the BookDatabaseManager managing the data
     * @param scanner   the Scanner for user input
     */
    private static void editBook(BookDatabaseManager dbManager, Scanner scanner) {
        System.out.print("Enter the ISBN of the book to edit: ");
        String isbn = scanner.nextLine();
        Book book = dbManager.getBookByISBN(isbn);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        System.out.println("Editing book: " + book.getTitle());
        System.out.print("Enter new title (or press Enter to keep [" + book.getTitle() + "]): ");
        String newTitle = scanner.nextLine();
        if (!newTitle.trim().isEmpty()) {
            book.setTitle(newTitle);
        }
        System.out.print("Enter new edition number (or press Enter to keep [" + book.getEditionNumber() + "]): ");
        String editionInput = scanner.nextLine();
        if (!editionInput.trim().isEmpty()) {
            try {
                int newEdition = Integer.parseInt(editionInput);
                book.setEditionNumber(newEdition);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Keeping current edition number.");
            }
        }
        System.out.print("Enter new copyright (or press Enter to keep [" + book.getCopyright() + "]): ");
        String newCopyright = scanner.nextLine();
        if (!newCopyright.trim().isEmpty()) {
            book.setCopyright(newCopyright);
        }
        if (dbManager.updateBook(book)) {
            System.out.println("Book updated successfully.");
        } else {
            System.out.println("Error updating book.");
        }
    }

    /**
     * Provides a prompt to edit an author's attributes and updates it in the database.
     *
     * @param dbManager the BookDatabaseManager managing the data
     * @param scanner   the Scanner for user input
     */
    private static void editAuthor(BookDatabaseManager dbManager, Scanner scanner) {
        System.out.print("Enter the author ID to edit: ");
        String idInput = scanner.nextLine();
        int authorID;
        try {
            authorID = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid author ID.");
            return;
        }
        Author author = dbManager.getAuthorByID(authorID);
        if (author == null) {
            System.out.println("Author not found.");
            return;
        }
        System.out.println("Editing author: " + author.getFirstName() + " " + author.getLastName());
        System.out.print("Enter new first name (or press Enter to keep [" + author.getFirstName() + "]): ");
        String newFirstName = scanner.nextLine();
        if (!newFirstName.trim().isEmpty()) {
            author.setFirstName(newFirstName);
        }
        System.out.print("Enter new last name (or press Enter to keep [" + author.getLastName() + "]): ");
        String newLastName = scanner.nextLine();
        if (!newLastName.trim().isEmpty()) {
            author.setLastName(newLastName);
        }
        if (dbManager.updateAuthor(author)) {
            System.out.println("Author updated successfully.");
        } else {
            System.out.println("Error updating author.");
        }
    }

    /**
     * Provides a prompt to add a new book (with existing or new authors) to the database.
     *
     * @param dbManager the BookDatabaseManager managing the data
     * @param scanner   the Scanner for user input
     */
    private static void addBook(BookDatabaseManager dbManager, Scanner scanner) {
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        if (dbManager.getBookByISBN(isbn) != null) {
            System.out.println("A book with this ISBN already exists.");
            return;
        }
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter edition number: ");
        int editionNumber = 0;
        try {
            editionNumber = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid edition number.");
            return;
        }
        System.out.print("Enter copyright: ");
        String copyright = scanner.nextLine();

        Book newBook = new Book(isbn, title, editionNumber, copyright);

        // add authors
        boolean addingAuthors = true;
        while (addingAuthors) {
            System.out.println("\nSelect an option to add an author for this book:");
            System.out.println("1. Existing author");
            System.out.println("2. New author");
            System.out.println("3. Done adding authors");
            System.out.print("Your choice: ");
            String authChoice = scanner.nextLine();
            switch (authChoice) {
                case "1":
                    System.out.print("Enter author ID: ");
                    try {
                        int authorID = Integer.parseInt(scanner.nextLine());
                        Author existingAuthor = dbManager.getAuthorByID(authorID);
                        if (existingAuthor != null) {
                            newBook.addAuthor(existingAuthor);
                            System.out.println("Author added.");
                        } else {
                            System.out.println("Author not found.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid author ID.");
                    }
                    break;
                case "2":
                    System.out.print("Enter first name: ");
                    String firstName = scanner.nextLine();
                    System.out.print("Enter last name: ");
                    String lastName = scanner.nextLine();
                    Author newAuthor = new Author(0, firstName, lastName); // temporary ID; will be set after insert
                    if (dbManager.addAuthor(newAuthor)) {
                        newBook.addAuthor(newAuthor);
                        System.out.println("New author created and added.");
                    } else {
                        System.out.println("Error creating new author.");
                    }
                    break;
                case "3":
                    addingAuthors = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
        if (dbManager.addBook(newBook)) {
            System.out.println("Book added successfully.");
        } else {
            System.out.println("Error adding book.");
        }
    }
}

