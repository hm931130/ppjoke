package com.beantechs.ppjoke;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.beantechs.libnetwork.ApiResponse;
import com.beantechs.libnetwork.ApiService;
import com.beantechs.libnetwork.JsonCallback;
import com.beantechs.ppjoke.test.TestBean;
import com.beantechs.ppjoke.utils.NavGraphBuilder;
import com.beantechs.ppjoke.view.AppBottomBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController navController;
    private AppBottomBar appBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appBottomBar = findViewById(R.id.bottomBar);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = NavHostFragment.findNavController(fragment);
        appBottomBar.setOnNavigationItemSelectedListener(this);

        NavGraphBuilder.build(navController, this, fragment.getId());

        ApiService.get("shapes").execute(new JsonCallback<List<TestBean>>() {
            @Override
            public void onSuccess(ApiResponse<List<TestBean>> response) {
                super.onSuccess(response);
                List<TestBean> list = response.body;

                Log.e("TAG", list.size() + "");
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navController.navigate(item.getItemId());
        return TextUtils.isEmpty(item.getTitle());
    }
}
