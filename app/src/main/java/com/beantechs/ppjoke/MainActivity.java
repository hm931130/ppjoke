package com.beantechs.ppjoke;

import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.beantechs.libnetwork.ApiResponse;
import com.beantechs.libnetwork.ApiService;
import com.beantechs.libnetwork.JsonCallback;
import com.beantechs.libnetwork.PostRequest;
import com.beantechs.libnetwork.Request;
import com.beantechs.libnetwork.log.MyTestInterceptor;
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

import org.json.JSONObject;

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

        ApiService.post("shapes")
                .addHeader("hello", "world")
                .addParams("name", "张飞")
                .addParams("age", 10)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        super.onSuccess(response);
                        Log.e("TAG", "onSuccess-" + response.toString());
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        super.onError(response);
                        Log.e("TAG", "onError-" + response.toString());
                    }

                    @Override
                    public void onCacheSuccess(ApiResponse<JSONObject> response) {
                        super.onCacheSuccess(response);
                        Log.e("TAG", "onCacheSuccess-" + response.toString());
                    }
                });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navController.navigate(item.getItemId());
        return !TextUtils.isEmpty(item.getTitle());
    }
}
