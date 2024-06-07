package com.example.seproject.data_classes;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.seproject.MainActivity;
import com.example.seproject.book_lists.BookDetailsFragment;
import com.example.seproject.book_lists.BookListOverviewFragment;
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


public class BookList implements FetchBookFromFB.OnGetBook {

    private String listID;
    private String name;
    private String description;
    private List<Book> orderedBooks;
    private Book bookToAdd;
    private Map<String, Boolean> books;
    private OrderedBooksReceiver receiver;


    public BookList(String listID, String name, String description){
        if (listID == null || name == null || description == null || listID.length() == 0 || name.length() == 0)
            throw new IllegalArgumentException("Illegal book list arguments");

        this.listID = listID;
        this.name = name;
        this.description = description;
        books = new LinkedHashMap<>();
    }

    /**
     * Get the book list name shortened
     * @return A string of the shortened name
     */
    private String getShortenedName(){
        return MainActivity.shortenString(name, 15);
    }

    /**
     * Returns short info about the book list
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



    @Exclude
    public void setBookToAdd(Book book){
        if (book == null)
            throw new RuntimeException("Can't add book that is null");
        bookToAdd = book;
    }

    public void addBook(BookDetailsFragment fragment, boolean alreadyInFB) {
        String bookID = bookToAdd.getBookID();
        Log.d("SEProject", "Adding book " + bookID + " to " + listID);

        if (getBooks().containsKey(bookID))
            throw new RuntimeException("List already contains book");

        if (alreadyInFB){
            addExistingBook(fragment);
        }
        else{
            FetchBookFromFB fetchBook = new FetchBookFromFB(new FetchBookFromFB.OnGetBook() {
                @Override
                public void onSuccess(Book book) {
                    if (book == null) {
                        addNewBookToFB(fragment);
                    }
                    else {
                        addExistingBook(fragment);
                    }
                }

                @Override
                public void onFailure() {
                    bookToAdd = null;
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
     * Adds book to FB
     * @param fragment The fragment
     */
    private void addNewBookToFB(BookDetailsFragment fragment){
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

        addExistingBook(fragment);
    }

    /**
     * Adds book to client
     * @param fragment The fragment
     */
    private void addExistingBook(BookDetailsFragment fragment){
        Log.d("SEProject", "Adding book " + bookToAdd.getBookID() + " to client in list " + listID);
        String bookID = bookToAdd.getBookID();
        FBref.FBUsers.child(MainActivity.getCurrentUser().getUid()).child(FBref.USER_BOOK_LISTS).child(listID)
                .child(FBref.USER_BOOKS).child(bookID).setValue(true);

        getBooks().put(bookToAdd.getBookID(), true);

        if (orderedBooks != null)
            orderedBooks.add(bookToAdd);
        Log.d("SEProject", "Successfully added book " + bookToAdd.getBookID() + " to list " + listID);

        bookToAdd = null;

        fragment.getParentFragmentManager().popBackStack(BookListOverviewFragment.ADD_BOOK_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }
    /**
     * Deletes book
     * @param position Position of book in list to delete
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
        this.receiver = receiver;
        Log.d("SEProject", "Creating ordered books for list " + listID);
        orderedBooks = new ArrayList<>();
        List<String> booksIDs = new ArrayList<>(getBooks().keySet());

        if (booksIDs.size() == 0) {
            receiver.getOrderedBooks(orderedBooks);
            return;
        }

        for (int i = 0; i < booksIDs.size(); i++){
            String bookID = booksIDs.get(i);

            FetchBookFromFB fetchBook = new FetchBookFromFB(this);
            fetchBook.getBookFromFB(bookID);
        }

    }
    /**
     * An interface to call when the ordered books list creation is done
     */
    public interface OrderedBooksReceiver{
        /**
         * Method to call with the list of books
         * @param books The list of books
         */
        void getOrderedBooks(List<Book> books);
    }

    @Override
    public void onSuccess(Book book) {
        if (book == null)
            throw new FailedToFetchBookException();

        book.setImageLink(Book.IMAGE_IN_FIREBASE_STORAGE);

        orderedBooks.add(book);

        if (orderedBooks.size() == getBooks().size()) {
            Log.d("SEProject", "Finished creating ordered books");
            receiver.getOrderedBooks(orderedBooks);
            receiver = null;
        }
    }
    @Override
    public void onFailure() {
        throw new FailedToFetchBookException();
    }





    // FB required constructor and getters
    public BookList(){}

    public String getListID() {
        return listID;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    @NonNull
    public Map<String,Boolean> getBooks(){
        if (books == null){
            books = new LinkedHashMap<>();
        }
        return books;
    }

}
