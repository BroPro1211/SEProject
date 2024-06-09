package com.example.seproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;

import com.example.seproject.book_lists.BookListsFragment;
import com.example.seproject.data_classes.Book;
import com.example.seproject.data_classes.BookList;
import com.example.seproject.data_classes.User;
import com.example.seproject.tools.ToolsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * The main activity, displaying the app
 */
public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, FragmentManager.OnBackStackChangedListener{

    public static final int HOME_SCREEN_ID = R.id.booksMenu;
    public static final String HOME_SCREEN_TAG = "home";
    private FragmentManager fragmentManager;
    private BottomNavigationView navBar;
    private static User loggedInUser;
    private static List<BookList> orderedBookLists;
    private static List<Book> currentlyViewedListOfBooks; // a list of the BookList currently being viewed


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkCurrentUser();

        navBar = findViewById(R.id.bottomNavigationView);
        navBar.getMenu().findItem(R.id.booksMenu).setChecked(true);

        fragmentManager = getSupportFragmentManager();

        navBar.setOnItemSelectedListener(this);

        fragmentManager.addOnBackStackChangedListener(this);
    }

    /*
    The fragmentContainerView is automatically set to BookListsFragment (the home screen). Each transaction
    from the home screen has the HOME_SCREEN_TAG, such that when the user selects a different tab,
    the fragment manager backstack is popped until this tag. That is, at any one time the backstack
    consists of the root transaction from the home screen, followed by the fragments of the current tab.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Class<? extends androidx.fragment.app.Fragment> fragmentClass;
        int id = item.getItemId();
        Log.d("SEProject", "Navigating to fragment " + id);

        if (id == R.id.toolsMenu)
            fragmentClass = ToolsFragment.class;
        else if (id == R.id.booksMenu)
            fragmentClass = BookListsFragment.class;
        else
            fragmentClass = ProfileFragment.class;

        // pop the current backstack so that we get to the home screen
        fragmentManager.popBackStack(HOME_SCREEN_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // begin a transaction if we are navigating to a screen that is not the home screen
        if (id != HOME_SCREEN_ID)
            fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragmentClass, null)
                .setReorderingAllowed(true)
                .addToBackStack(HOME_SCREEN_TAG)
                .commit();

        return true;
    }

    /*
    If the user presses the back button, the backstack topmost transaction is popped, but the bottom
    navigation view isn't updated. Hence the shown fragment and the selected tab may not match.
    Due to the implemented onNavigationItemSelected behavior, the only way for this to occur is if we pressed
    the back button until the entire backstack popped and we are left at the home screen. This method
    fixes this.
     */
    @Override
    public void onBackStackChanged() {
        int selectedItemID = navBar.getSelectedItemId();

        if (selectedItemID != HOME_SCREEN_ID && fragmentManager.getBackStackEntryCount() == 0) {
            Log.d("SEProject", "Navigating to fragment " + HOME_SCREEN_ID);
            navBar.getMenu().findItem(HOME_SCREEN_ID).setChecked(true);
        }
    }

    /**
     * Sets the currently logged in user
     * @param user User instance of the logged in user.
     */
    public static void setCurrentUser(User user){
        loggedInUser = user;
    }

    /**
     * Returns the user instance of the logged in user
     * @return The logged in user
     */
    public static User getCurrentUser(){
        return loggedInUser;
    }

    /**
     * Checks if there is a currently logged in user. If not, an exception is thrown.
     */
    public static void checkCurrentUser(){
        if (loggedInUser == null)
            throw new RuntimeException("No logged in user found");
    }

    /**
     * Signs out the currently logged in user
     */
    public static void signOut(){
        FirebaseAuth.getInstance().signOut();
        setCurrentUser(null);
        setCurrentlyViewedListOfBooks(null);
        orderedBookLists = null;
    }


    /**
     * Returns the book lists of the signed in user, ordered by their creation time
     * @return The list of book lists
     */
    @NonNull
    public static List<BookList> getOrderedBookLists(){
        if (orderedBookLists == null) {
            // the book list id's created by firebase are ordered by creation time
            if (getCurrentUser() == null)
                orderedBookLists = new ArrayList<>();
            else
                orderedBookLists = getCurrentUser().getBookLists().entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList());
        }
        return orderedBookLists;
    }

    /**
     * Sets the current list of books being displayed
     * @param listOfBooks The list of books being displayed
     */
    public static void setCurrentlyViewedListOfBooks(List<Book> listOfBooks){
        currentlyViewedListOfBooks = listOfBooks;
    }

    /**
     * Returns the current list of books being displayed
     * @return The list of books being displayed
     */
    @NonNull
    public static List<Book> getCurrentlyViewedListOfBooks(){
        if (currentlyViewedListOfBooks == null)
            currentlyViewedListOfBooks = new ArrayList<>();
        return currentlyViewedListOfBooks;
    }

    /**
     * Finds the index of the book with the given id in the current list of books being displayed
     * @param bookID The id of the book to search for
     * @return Returns the index of the book in the list if found, and -1 if not found
     */
    public static int findBookInCurrentlyViewed(String bookID){
        for (int i = 0; i < getCurrentlyViewedListOfBooks().size(); i++){
            if (getCurrentlyViewedListOfBooks().get(i).getBookID().equals(bookID)){
                return i;
            }
        }
        return -1;
    }

    /**
     * Shortens the string to the given length
     * @param s The string to shorten
     * @param length The length to shorten to
     * @return Returns s if s is shorter than the length, and else returns s shortened to the given length
     */
    public static String shortenString(String s, int length){
        if (s.length() <= length)
            return s;
        return s.substring(0, length) + "...";
    }

    /**
     * Returns a bitmap of the given drawable resource
     * @param context The context
     * @param drawableId The id of the drawable
     * @return Returns a bitmap of the drawable
     */
    public static Bitmap getBitmapFromDrawable(Context context, int drawableId){
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}