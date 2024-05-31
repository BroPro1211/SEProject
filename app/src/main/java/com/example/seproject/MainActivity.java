package com.example.seproject;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.seproject.book_lists.BookListsFragment;
import com.example.seproject.tools.ToolsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, FragmentManager.OnBackStackChangedListener{
    public static final int HOME_SCREEN_ID = R.id.booksMenu;
    public static final String HOME_SCREEN_TAG = "home";
    private FragmentManager fragmentManager;
    private BottomNavigationView navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User.checkCurrentUser();

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
}