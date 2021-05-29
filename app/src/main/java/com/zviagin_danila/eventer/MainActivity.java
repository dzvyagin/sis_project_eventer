package com.zviagin_danila.eventer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.zviagin_danila.eventer.ui.authentication.AuthenticationActivity;
import com.zviagin_danila.eventer.ui.main_content.add_event.AddEventTitleFragment;
import com.zviagin_danila.eventer.ui.main_content.favorites.FavoritesFragment;
import com.zviagin_danila.eventer.ui.main_content.home.HomeFragment;
import com.zviagin_danila.eventer.ui.main_content.messages.MessagesFragment;
import com.zviagin_danila.eventer.ui.main_content.responses.ResponsesFragment;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new HomeFragment());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        loadFragment(new HomeFragment());
                        return true;
                    case R.id.navigation_add_event:
                        loadFragment(new AddEventTitleFragment());
                        return true;
                    case R.id.navigation_favorites:
                        loadFragment(new FavoritesFragment());
                        return true;
                    case R.id.navigation_messages:
                        loadFragment(new MessagesFragment());
                        return true;
                    case R.id.navigation_responses:
                        loadFragment(new ResponsesFragment());
                        return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content_container, fragment)
                .commit();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}