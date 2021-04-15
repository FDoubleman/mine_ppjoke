package cn.xdf.mine_ppjoke;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import cn.xdf.libnetwork.GetRequest;
import cn.xdf.mine_ppjoke.utils.NavGraphBuilder;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);


        navController = NavHostFragment.findNavController(fragment);
        NavGraphBuilder.build(navController, this, fragment.getId());

        navView.setOnNavigationItemSelectedListener(this);

        GetRequest getRequest = new GetRequest("www.baidu.com");
        getRequest.execute();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navController.navigate(item.getItemId());
        return !TextUtils.isEmpty(item.getTitle());
    }
}