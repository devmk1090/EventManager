package com.devproject.eventmanager;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

/**
 * 앱 종료 확인 창. 가운데에 300x250 배너 광고가 들어간다 (M2 앱과 같은 방식).
 * 창을 열자마자 광고가 보이도록 앱 시작 3초 뒤에 미리 불러 두고, 창을 열 때마다 같은 광고 뷰를 다시 붙인다.
 * 3초 안에 종료하면 그때 불러온다. 광고를 못 불러왔으면(오프라인 등) 광고 자리 없이 창을 띄운다.
 */
final class ExitAdDialog {

    private static final long PRELOAD_DELAY_MS = 3_000L;

    private final Activity activity;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private AdView adView;
    private boolean adFailed;
    private Dialog dialog;

    ExitAdDialog(Activity activity) {
        this.activity = activity;
    }

    /** 앱 시작 직후 화면이 버벅이지 않도록 조금 뒤에 광고를 미리 불러 둔다 */
    void preload() {
        handler.postDelayed(this::ensureAdView, PRELOAD_DELAY_MS);
    }

    void show() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        ensureAdView();

        View content = LayoutInflater.from(activity).inflate(R.layout.dialog_exit, null);
        FrameLayout adContainer = content.findViewById(R.id.exit_ad_container);
        if (adFailed) {
            // 지난번에 못 불러왔으면 광고 자리를 비워 두지 않고, 다음 번을 위해 다시 불러 둔다
            adContainer.setVisibility(View.GONE);
            adFailed = false;
            adView.loadAd(new AdRequest.Builder().build());
        } else {
            detachAdView();
            adContainer.addView(adView);
        }

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(content);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        content.findViewById(R.id.btn_exit_cancel).setOnClickListener(v -> dialog.dismiss());
        content.findViewById(R.id.btn_exit_confirm).setOnClickListener(v -> {
            dialog.dismiss();
            activity.finishAffinity();
        });
        // 다음에 다시 붙일 수 있도록 창이 닫히면 광고 뷰를 떼어 둔다
        dialog.setOnDismissListener(d -> detachAdView());
        dialog.show();
    }

    void destroy() {
        handler.removeCallbacksAndMessages(null);
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (adView != null) {
            detachAdView();
            adView.destroy();
            adView = null;
        }
    }

    private void ensureAdView() {
        if (adView != null) {
            return;
        }
        adView = new AdView(activity);
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        adView.setAdUnitId(activity.getString(R.string.ad_unit_id_rev));
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adFailed = false;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError error) {
                adFailed = true;
                // 창이 열려 있는 동안 실패하면 빈 자리를 접는다
                if (adView != null && adView.getParent() instanceof View) {
                    ((View) adView.getParent()).setVisibility(View.GONE);
                }
            }
        });
        adView.loadAd(new AdRequest.Builder().build());
    }

    private void detachAdView() {
        if (adView != null && adView.getParent() instanceof ViewGroup) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }
    }
}
