package com.devproject.eventmanager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * 시작 화면. 청록 바탕에 봉투 아이콘과 앱 이름을 잠깐 보여 준 뒤 MainActivity로 넘어간다.
 *
 * <p>Android 12 이상에서는 같은 바탕색·같은 아이콘의 시스템 시작 화면이 먼저 뜬다.
 * 레이아웃이 아이콘을 시스템 화면과 같은 크기·위치에 두므로, 여기서는 앱 이름만 나타나게 해서
 * 두 화면이 하나로 이어져 보이게 한다.
 */
public class SplashScreen extends AppCompatActivity {

    private static final long SHOW_MILLIS = 2500;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable openMainTask = this::openMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        // 청록 바탕이라 상태 표시줄·내비게이션 바 아이콘을 흰색으로 둔다 (enableEdgeToEdge 뒤에 다시 지정)
        WindowInsetsControllerCompat bars = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        bars.setAppearanceLightStatusBars(false);
        bars.setAppearanceLightNavigationBars(false);
        setContentView(R.layout.activity_splashscreen);

        View title = findViewById(R.id.splash_title);
        title.setAlpha(0f);
        title.setTranslationY(12 * getResources().getDisplayMetrics().density);
        title.animate().alpha(1f).translationY(0f).setStartDelay(200).setDuration(600).start();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // 시스템 시작 화면이 없는 버전에서는 아이콘도 부드럽게 나타나게 한다
            View icon = findViewById(R.id.splash_icon);
            icon.setAlpha(0f);
            icon.animate().alpha(1f).setDuration(250).start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(openMainTask, SHOW_MILLIS);
    }

    @Override
    protected void onPause() {
        // 홈·뒤로가기로 시작 화면을 벗어나면 메인 화면을 띄우지 않는다. 돌아오면 처음부터 다시 기다린다.
        handler.removeCallbacks(openMainTask);
        super.onPause();
    }

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class),
                ActivityOptionsCompat.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
        finish();
    }
}
