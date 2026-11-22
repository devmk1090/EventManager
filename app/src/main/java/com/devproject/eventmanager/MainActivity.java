package com.devproject.eventmanager;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** 하단 메뉴(내역·통계·설정)와 광고 배너를 가진 메인 화면. 세 화면은 숨김/보임으로 전환한다. */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_HISTORY = "history";
    private static final String TAG_STATISTICS = "statistics";
    private static final String TAG_SETTINGS = "settings";
    private static final String[] TAGS = {TAG_HISTORY, TAG_STATISTICS, TAG_SETTINGS};

    private BottomNavigationView bottomNav;
    private ExitAdDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        // 상태 표시줄 높이만큼 위를 띄운다. 아래(내비게이션 바)는 BottomNavigationView가 스스로 띄운다.
        View root = findViewById(R.id.main_root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, 0);
            return insets;
        });

        MobileAds.initialize(this, status -> { });
        AdView adView = findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());
        exitDialog = new ExitAdDialog(this);
        exitDialog.preload();

        InOutDatabase.getInstance(this).open();

        if (savedInstanceState == null) {
            Fragment statistics = new StatisticsFragment();
            Fragment settings = new SettingsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new HistoryFragment(), TAG_HISTORY)
                    .add(R.id.fragment_container, statistics, TAG_STATISTICS)
                    .add(R.id.fragment_container, settings, TAG_SETTINGS)
                    .hide(statistics)
                    .hide(settings)
                    .commit();
        }

        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            showOnly(tagFor(item.getItemId()));
            return true;
        });
        bottomNav.setOnItemReselectedListener(item -> { });

        // targetSdk 36 + Android 16에서는 onBackPressed()가 호출되지 않으므로 콜백으로 처리한다
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBack();
            }
        });
    }

    /** 뒤로가기: 다른 탭이면 내역으로, 검색 중이면 검색 닫기, 그 밖에는 광고가 든 종료 확인 창 */
    private void onBack() {
        if (bottomNav.getSelectedItemId() != R.id.nav_history) {
            bottomNav.setSelectedItemId(R.id.nav_history);
            return;
        }
        Fragment history = getSupportFragmentManager().findFragmentByTag(TAG_HISTORY);
        if (history instanceof HistoryFragment && ((HistoryFragment) history).closeSearch()) {
            return;
        }
        exitDialog.show();
    }

    @Override
    protected void onDestroy() {
        exitDialog.destroy();
        super.onDestroy();
    }

    private void showOnly(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (String each : TAGS) {
            Fragment fragment = fragmentManager.findFragmentByTag(each);
            if (fragment == null) {
                continue;
            }
            if (each.equals(tag)) {
                transaction.show(fragment);
            } else {
                transaction.hide(fragment);
            }
        }
        transaction.commit();
    }

    private static String tagFor(int itemId) {
        if (itemId == R.id.nav_statistics) {
            return TAG_STATISTICS;
        }
        if (itemId == R.id.nav_settings) {
            return TAG_SETTINGS;
        }
        return TAG_HISTORY;
    }
}
