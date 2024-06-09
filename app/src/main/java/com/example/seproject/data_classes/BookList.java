package com.example.seproject.data_classes;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.seproject.MainActivity;
import com.example.seproject.book_lists.BookDetailsFragment;
import com.example.seproject.book_lists.FetchBookFromFB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Exclude;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Class to store the information about a user's book list
 */
public class BookList {

    private String listID;
    private String name;
    private String description;
    private List<Book> orderedBooks;
    private Map<String, Boolean> books;

    /**
     * Initializes the book list instance
     * @param listID The list's id
     * @param name The list's name
     * @param description The list's description
     */
    public BookList(String listID, String name, String description){
        if (listID == null || name == null || description == null || listID.length() == 0 || name.length() == 0)
            throw new IllegalArgumentException("Illegal book list arguments");

        this.listID = listID;
        this.name = name;
        this.description = description;
        books = new LinkedHashMap<>();
    }

    /**
     * Get the shortened book list name
     * @return A string of the shortened name
     */
    private String getShortenedName(){
        return MainActivity.shortenString(name, 15);
    }

    /**
     * Returns short info about the book list, showing the list name and number of books
     * @return A string of the short info
     */
    @Exclude
    public String getListShortInfo(){
        return getShortenedName() + "\n" + getBookCount() + " books";
    }

    /**
     * Returns the number of books stored
     * @return The number of books
     */
    @Exclude
    public int getBookCount(){
        return getBooks().size();
    }

    /**
     * Adds the given book to the book list
     * @param bookToAdd The book to add
     * @param fragment The BookDetailsFragment who called this method
     * @param alreadyInFB A boolean value, describing if the book was already found to be in the database
     */
    public void addBook(final Book bookToAdd, BookDetailsFragment fragment, boolean alreadyInFB) {
        String bookID = bookToAdd.getBookID();
        Log.d("SEProject", "Adding book " + bookID + " to " + listID);

        if (getBooks().containsKey(bookID))
            throw new RuntimeException("List already contains book");

        if (alreadyInFB){
            addExistingBook(bookToAdd, fragment);
        }
        else{
            FetchBookFromFB fetchBook = new FetchBookFromFB(new FetchBookFromFB.OnGetBook() {
                @Override
                public void onSuccess(Book book) {
                    if (book == null) {
                        addNewBookToFB(bookToAdd, fragment);
                    }
                    else {
                        addExistingBook(book, fragment);
                    }
                }

                @Override
                public void onFailure() {
                    throw new FailedToFetchBookException();
                }
            });

            fetchBook.getBookFromFB(bookID);
        }

    }
    /**
     * Exception when failed to fetch book
     */
    public static class FailedToFetchBookException extends RuntimeException{}

    /**
     * Adds the given book to the database (in the books subtree)
     * @param bookToAdd The book to add
     * @param fragment The fragment
     */
    private void addNewBookToFB(Book bookToAdd, BookDetailsFragment fragment){
        String bookID = bookToAdd.getBookID();
        Log.d("SEProject", "Adding book " + bookID + " to FB in list " + listID);

        // save data to firebase realtime database
        FBref.FBBooks.child(bookID).setValue(bookToAdd);

        // save image to firebase storage
        bookToAdd.getImage(fragment.getContext(), new Book.BookImageReceiver() {
            @Override
            public void receiveBookImage(Book book, Bitmap image) {

                String fileName = book.getBookID() + FBref.IMAGE_FILE_EXTENSION;

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] data = byteArrayOutputStream.toByteArray();

                StorageReference bookStorageReference = FBref.FBBookImages.child(fileName);

                UploadTask uploadTask = bookStorageReference.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("SEProject", "Successfully saved book " + book.getBookID() + " image to FB");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("SEProject", "Failed to save book " + book.getBookID() + " image to FB", exception);
                    }
                });
            }
        });

        addExistingBook(bookToAdd, fragment);
    }

    /**
     * Adds the given book to the user
     * @param bookToAdd The book to add
     * @param fragment The fragment
     */
    private void addExistingBook(Book bookToAdd, BookDetailsFragment fragment){
        Log.d("SEProject", "Adding book " + bookToAdd.getBookID() + " to client in list " + listID);
        String bookID = bookToAdd.getBookID();
        FBref.FBUsers.child(MainActivity.getCurrentUser().getUid()).child(FBref.USER_BOOK_LISTS).child(listID)
                .child(FBref.USER_BOOKS).child(bookID).setValue(true);

        getBooks().put(bookToAdd.getBookID(), true);

        if (orderedBooks != null)
            orderedBooks.add(bookToAdd);
        Log.d("SEProject", "Successfully added book " + bookToAdd.getBookID() + " to list " + listID);


        fragment.onAddedBook();

    }
    /**
     * Deletes book from the book list
     * @param position Position of book in the ordered books list to delete
     */
    public void deleteBook(int position){
        String bookID = orderedBooks.get(position).getBookID();
        Log.d("SEProject", "Deleting book " + bookID + " from list " + listID);

        books.remove(bookID);
        orderedBooks.remove(position); // also removes book from User.getCurrentlyViewedListOfBooks

        Log.d("SEProject", "Deleting book " + bookID + " from list " + listID + " in FB");
        FBref.FBUsers.child(MainActivity.getCurrentUser().getUid()).child(FBref.USER_BOOK_LISTS).child(listID)
                .child(FBref.USER_BOOKS).child(bookID).removeValue();

        Log.d("SEProject", "Successfully deleted book " + bookID + " from list " + listID);
    }


    /**
     * Returns this book list's ordered books to the receiver
     * @param receiver Receiver to call with the list
     */
    @Exclude
    public void getOrderedBooks(OrderedBooksReceiver receiver){
        if (orderedBooks == null)
            createOrderedBooks(receiver);
        else {
            receiver.getOrderedBooks(orderedBooks);
        }
    }
    /**
     * A method to download the books from the book list and create their list
     * @param receiver A receiver that gets the list
     */
    private void createOrderedBooks(OrderedBooksReceiver receiver){
        Log.d("SEProject", "Creating ordered books for list " + listID);

        OrderedBooksCreator orderedBooksCreator = new OrderedBooksCreator(this, receiver);
        orderedBooksCreator.createOrderedBooks();
    }

    /**
     * An interface to call when the ordered books list creation is done
     */
    public interface OrderedBooksReceiver{
        /**
         * Method to call with the list of books
         * @param books The list of books
         */
        void getOrderedBooks(@NonNull List<Book> books);
    }

    /**
     * Sets the ordered books list
     * @param orderedBooks The list of books to store
     */
    @Exclude
    public void setOrderedBooks(List<Book> orderedBooks){
        this.orderedBooks = orderedBooks;
    }

    /**
     * A class that creates the ordered books list
     */
    private static class OrderedBooksCreator implements FetchBookFromFB.OnGetBook{
        private final BookList bookList;
        private final OrderedBooksReceiver orderedBooksReceiver;
        private final List<Book> orderedBooks;

        /**
         * Initializes the OrderedBooksCreator instance
         * @param bookList The book list to create the ordered books for
         * @param receiver The receiver to call when done
         */
        public OrderedBooksCreator(BookList bookList, OrderedBooksReceiver receiver) {
            this.bookList = bookList;
            orderedBooksReceiver = receiver;
            orderedBooks = new ArrayList<>();
        }

        /**
         * Creates the ordered books list, saves it to the book list and calls the receiver when done
         */
        public void createOrderedBooks(){
            List<String> booksIDs = new ArrayList<>(bookList.getBooks().keySet());

            if (booksIDs.size() == 0) {
                sendOrderedBooks();
                return;
            }

            for (int i = 0; i < booksIDs.size(); i++){
                String bookID = booksIDs.get(i);

                FetchBookFromFB fetchBook = new FetchBookFromFB(this);
                fetchBook.getBookFromFB(bookID);
            }
        }

        /**
         * Saves the ordered books to the book list and calls the receiver
         */
        private void sendOrderedBooks(){
            bookList.setOrderedBooks(orderedBooks);
            orderedBooksReceiver.getOrderedBooks(orderedBooks);
        }

        @Override
        public void onSuccess(Book book) {
            if (book == null)
                throw new FailedToFetchBookException();

            book.setImageLink(Book.IMAGE_IN_FIREBASE_STORAGE);

            orderedBooks.add(book);

            if (orderedBooks.size() == bookList.getBooks().size()) {
                Log.d("SEProject", "Finished creating ordered books");
                sendOrderedBooks();
            }
        }
        @Override
        public void onFailure() {
            throw new FailedToFetchBookException();
        }


    }





    // FB required constructor and getters

    /**
     * Empty constructor
     */
    public BookList(){}

    /**
     * Returns the list ID
     * @return The list ID
     */
    public String getListID() {
        return listID;
    }

    /**
     * Returns the list name
     * @return The list name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list description
     * @return The list description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the list map of books
     * @return The map of book ID's
     */
    @NonNull
    public Map<String,Boolean> getBooks(){
        if (books == null){
            books = new LinkedHashMap<>();
        }
        return books;
    }

}
