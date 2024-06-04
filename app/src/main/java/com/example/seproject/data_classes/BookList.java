package com.example.seproject.data_classes;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

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


public class BookList  {

    private String listID;
    private String name;
    private String description;
    private List<Book> orderedBooks;
    private Book bookToAdd;
    private Map<String, Boolean> books;


    public BookList(String listID, String name, String description){
        if (listID == null || name == null || description == null || listID.length() == 0 || name.length() == 0)
            throw new IllegalArgumentException("Illegal book list arguments");

        this.listID = listID;
        this.name = name;
        this.description = description;
        books = new LinkedHashMap<>();
    }

    private String getShortenedName(){
        if (name.length() <= 15)
            return name;
        return name.substring(0, 15) + "...";
    }

    @Exclude
    public String getListShortInfo(){
        return getShortenedName() + "\n" + getBookCount() + " books";
    }

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
            addBookInClient(fragment);
        }
        else{
            FetchBookFromFB fetchBook = new FetchBookFromFB(new FetchBookFromFB.OnGetBook() {
                @Override
                public void onSuccess(Book book) {
                    if (book == null) {
                        addBookToFB(fragment);
                    }
                    else {
                        addBookInClient(fragment);
                    }
                }

                @Override
                public void onFailure() {
                    bookToAdd = null;
                    throw new FetchBookFromFB.FailedToFetchBookException();
                }
            });

            fetchBook.getBookFromFB(bookID);
        }

    }


    private void addBookToFB(BookDetailsFragment fragment){
        String bookID = bookToAdd.getBookID();
        Log.d("SEProject", "Adding book " + bookID + " to FB in list " + listID);

        // save data to firebase realtime database
        FBref.FBBooks.child(bookID).setValue(bookToAdd);
        FBref.FBUsers.child(User.getCurrentUser().getUid()).child(FBref.USER_BOOK_LISTS).child(listID)
                .child(FBref.USER_BOOKS).child(bookID).setValue(true);

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

        addBookInClient(fragment);
    }


    private void addBookInClient(BookDetailsFragment fragment){
        Log.d("SEProject", "Adding book " + bookToAdd.getBookID() + " to client in list " + listID);

        getBooks().put(bookToAdd.getBookID(), true);

        if (orderedBooks != null)
            orderedBooks.add(bookToAdd);
        Log.d("SEProject", "Successfully added book " + bookToAdd.getBookID() + " to list " + listID);

        bookToAdd = null;

        fragment.getParentFragmentManager().popBackStack(BookListOverviewFragment.ADD_BOOK_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

    public void deleteBook(int position){
        String bookID = orderedBooks.get(position).getBookID();
        Log.d("SEProject", "Deleting book " + bookID + " from list " + listID);

        books.remove(bookID);
        orderedBooks.remove(position); // also removes book from User.getCurrentlyViewedListOfBooks

        Log.d("SEProject", "Deleting book " + bookID + " from list " + listID + " in FB");
        FBref.FBUsers.child(User.getCurrentUser().getUid()).child(FBref.USER_BOOK_LISTS).child(listID)
                .child(FBref.USER_BOOKS).child(bookID).removeValue();

        Log.d("SEProject", "Successfully deleted book " + bookID + " from list " + listID);
    }

    private void createOrderedBooks(OrderedBooksReceiver receiver){
        Log.d("SEProject", "Creating ordered books for list " + listID);
        orderedBooks = new ArrayList<>();
        List<String> booksIDs = new ArrayList<>(getBooks().keySet());

        if (booksIDs.size() == 0) {
            receiver.getOrderedBooks(orderedBooks);
            return;
        }

        for (int i = 0; i < booksIDs.size(); i++){
            String bookID = booksIDs.get(i);

            FetchBookFromFB fetchBook = new FetchBookFromFB(new CreateOrderedBooks(orderedBooks, i==booksIDs.size()-1, receiver));
            fetchBook.getBookFromFB(bookID);
        }

    }
    private static class CreateOrderedBooks implements FetchBookFromFB.OnGetBook {
        private final List<Book> orderedBooks;
        private final boolean end;
        private final OrderedBooksReceiver receiver;
        public CreateOrderedBooks(List<Book> orderedBooks, boolean end, OrderedBooksReceiver receiver){
            this.orderedBooks = orderedBooks;
            this.end = end;
            this.receiver = receiver;
        }

        @Override
        public void onSuccess(Book book) {
            if (book == null)
                throw new FetchBookFromFB.FailedToFetchBookException();

            book.setImageLink(Book.IMAGE_IN_FIREBASE_STORAGE);

            orderedBooks.add(book);

            if (end) {
                Log.d("SEProject", "Finished creating ordered books");
                receiver.getOrderedBooks(orderedBooks);
            }
        }

        @Override
        public void onFailure() {
            throw new FetchBookFromFB.FailedToFetchBookException();
        }
    }

    @Exclude
    public void getOrderedBooks(OrderedBooksReceiver receiver){
        if (orderedBooks == null)
            createOrderedBooks(receiver);
        else {
            receiver.getOrderedBooks(orderedBooks);
        }
    }
    public interface OrderedBooksReceiver{
        void getOrderedBooks(List<Book> books);
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
    public Map<String,Boolean> getBooks(){
        if (books == null){
            books = new LinkedHashMap<>();
        }
        return books;
    }

}
