package com.devproject.eventmanager;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/** 설정 탭: 백업 파일 저장·보내기·불러오기와 안내, 엑셀 내보내기, 앱 버전. */
public class SettingsFragment extends Fragment {

    private static final String PREFS = "backup";
    /** 백업 안내를 본 적이 있는지. 처음 백업할 때는 안내부터 보여 준다. */
    private static final String KEY_GUIDE_SHOWN = "guide_shown";
    /** 마지막으로 백업 파일을 저장한 시각(ms) */
    private static final String KEY_LAST_BACKUP = "last_backup";

    private static final int[] GUIDE_STEPS = {R.string.guide_step1, R.string.guide_step2, R.string.guide_step3};

    // 저장하거나 불러올 위치는 시스템 파일 창에서 사용자가 고른다 (Google 드라이브, 내 파일 등)
    private final ActivityResultLauncher<String> createBackup = registerForActivityResult(
            new ActivityResultContracts.CreateDocument(DatabaseBackup.MIME_TYPE), this::saveBackup);
    private final ActivityResultLauncher<String[]> openBackup = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(), this::inspectBackup);
    private final ActivityResultLauncher<String> createExcel = registerForActivityResult(
            new ActivityResultContracts.CreateDocument(ExcelExporter.MIME_TYPE), this::saveExcel);

    private AlertDialog progress;
    private TextView backupDesc;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        backupDesc = view.findViewById(R.id.backup_desc);
        showLastBackup();
        view.findViewById(R.id.row_backup).setOnClickListener(v -> onBackupClicked());
        view.findViewById(R.id.row_share).setOnClickListener(v -> shareBackup());
        // 이름이나 확장자를 바꾼 백업 파일도 고를 수 있게 모든 파일을 보여 주고, 내용으로 검사한다
        view.findViewById(R.id.row_restore).setOnClickListener(v -> openBackup.launch(new String[]{"*/*"}));
        view.findViewById(R.id.row_guide).setOnClickListener(v -> showGuide());
        view.findViewById(R.id.row_export).setOnClickListener(v ->
                createExcel.launch(ExcelExporter.fileName(Calendar.getInstance())));
        ((TextView) view.findViewById(R.id.version_text)).setText(versionName());
        return view;
    }

    @Override
    public void onDestroyView() {
        hideProgress();
        backupDesc = null;
        super.onDestroyView();
    }

    // ---- 백업 ----

    private void onBackupClicked() {
        if (prefs().getBoolean(KEY_GUIDE_SHOWN, false)) {
            launchBackup();
        } else {
            showGuide(); // 처음에는 안내부터 보여 주고, 안내의 버튼으로 이어서 저장한다
        }
    }

    private void launchBackup() {
        createBackup.launch(DatabaseBackup.fileName(Calendar.getInstance()));
    }

    private void saveBackup(Uri target) {
        if (target == null) {
            return; // 파일 창에서 취소
        }
        showProgress(R.string.backup_working);
        DatabaseBackup.save(requireContext(), target, saved -> {
            hideProgress();
            if (!isAdded()) {
                return;
            }
            if (saved) {
                prefs().edit().putLong(KEY_LAST_BACKUP, System.currentTimeMillis()).apply();
                showLastBackup();
            }
            Toast.makeText(requireContext(), saved ? R.string.backup_done : R.string.backup_failed,
                    Toast.LENGTH_SHORT).show();
        });
    }

    /** 백업 항목 설명: 백업한 적이 있으면 마지막 백업 날짜 */
    private void showLastBackup() {
        if (backupDesc == null) {
            return;
        }
        long last = prefs().getLong(KEY_LAST_BACKUP, 0);
        if (last == 0) {
            backupDesc.setText(R.string.settings_backup_desc);
            return;
        }
        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(last);
        EntryDate date = EntryDate.of(day.get(Calendar.YEAR), day.get(Calendar.MONTH) + 1, day.get(Calendar.DAY_OF_MONTH));
        backupDesc.setText(getString(R.string.settings_backup_last, date.longText()));
    }

    // ---- 백업 안내 ----

    @SuppressLint("InflateParams") // 시트 내용에는 부모 뷰가 없다
    private void showGuide() {
        prefs().edit().putBoolean(KEY_GUIDE_SHOWN, true).apply();
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.sheet_backup_guide, null);
        LinearLayout steps = view.findViewById(R.id.guide_steps);
        for (int i = 0; i < GUIDE_STEPS.length; i++) {
            View row = getLayoutInflater().inflate(R.layout.item_guide_step, steps, false);
            ((TextView) row.findViewById(R.id.step_number)).setText(String.valueOf(i + 1));
            ((TextView) row.findViewById(R.id.step_text)).setText(GUIDE_STEPS[i]);
            steps.addView(row);
        }
        // 드라이브 앱이 없거나 꺼져 있으면 저장 창에 드라이브가 안 나오므로 켜는 방법을 알려 준다
        boolean driveAvailable = DatabaseBackup.isDriveAvailable(requireContext());
        view.findViewById(R.id.guide_drive_off).setVisibility(driveAvailable ? View.GONE : View.VISIBLE);
        view.findViewById(R.id.guide_enable_drive).setOnClickListener(v -> {
            sheet.dismiss();
            openDriveInStore();
        });
        view.findViewById(R.id.guide_backup_now).setOnClickListener(v -> {
            sheet.dismiss();
            launchBackup();
        });
        view.findViewById(R.id.guide_share).setOnClickListener(v -> {
            sheet.dismiss();
            shareBackup();
        });
        sheet.setContentView(view);
        sheet.getBehavior().setSkipCollapsed(true);
        sheet.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        sheet.show();
    }

    /** Play 스토어의 Google 드라이브 페이지. 꺼져 있으면 ‘사용’, 없으면 ‘설치’ 버튼이 나온다. */
    private void openDriveInStore() {
        String id = DatabaseBackup.DRIVE_PACKAGE;
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + id)));
        } catch (ActivityNotFoundException noStore) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + id)));
            } catch (ActivityNotFoundException ignored) {
                // Play 스토어도 브라우저도 없는 기기: 안내 문구만으로 둔다
            }
        }
    }

    // ---- 다른 앱으로 보내기 ----

    /** 백업 파일을 공유 창으로 보낸다 (카카오톡 나에게 보내기, 메일, 드라이브 등). 마지막 백업 날짜는 바꾸지 않는다. */
    private void shareBackup() {
        File file;
        try {
            file = DatabaseBackup.createShareFile(requireContext());
        } catch (IOException e) {
            Toast.makeText(requireContext(), R.string.share_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = FileProvider.getUriForFile(requireContext(),
                requireContext().getPackageName() + DatabaseBackup.SHARE_AUTHORITY_SUFFIX, file);
        Intent send = new Intent(Intent.ACTION_SEND)
                .setType(DatabaseBackup.MIME_TYPE)
                .putExtra(Intent.EXTRA_STREAM, uri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        send.setClipData(ClipData.newRawUri(file.getName(), uri));
        startActivity(Intent.createChooser(send, getString(R.string.share_backup_chooser)));
    }

    // ---- 엑셀 ----

    private void saveExcel(Uri target) {
        if (target == null) {
            return; // 파일 창에서 취소
        }
        showProgress(R.string.export_working);
        ExcelExporter.export(requireContext(), target, saved -> {
            hideProgress();
            if (isAdded()) {
                Toast.makeText(requireContext(), saved ? R.string.export_done : R.string.export_failed,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ---- 불러오기 ----

    private void inspectBackup(Uri source) {
        if (source == null) {
            return; // 파일 창에서 취소
        }
        showProgress(R.string.restore_checking);
        DatabaseBackup.inspect(requireContext(), source, inspection -> {
            hideProgress();
            if (!isAdded()) {
                inspection.discard();
            } else if (inspection.problem != null) {
                showProblem(inspection.problem);
            } else {
                confirmRestore(inspection);
            }
        });
    }

    private void confirmRestore(DatabaseBackup.Inspection inspection) {
        InOutDatabase database = InOutDatabase.getInstance(requireContext());
        database.open();
        DatabaseBackup.Summary backup = inspection.summary;
        String latest = backup.latest == null ? getString(R.string.restore_no_date) : backup.latest.longText();
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.restore_confirm_title)
                .setMessage(getString(R.string.restore_confirm_message, backup.received, backup.sent, latest,
                        database.count(Entry.Direction.RECEIVED), database.count(Entry.Direction.SENT)))
                .setPositiveButton(R.string.action_restore_confirm, (dialog, which) -> applyRestore(inspection))
                .setNegativeButton(R.string.action_cancel, (dialog, which) -> inspection.discard())
                .setOnCancelListener(dialog -> inspection.discard())
                .show();
    }

    private void applyRestore(DatabaseBackup.Inspection inspection) {
        DatabaseBackup.Summary backup = inspection.summary;
        if (!DatabaseBackup.apply(requireContext(), inspection)) {
            Toast.makeText(requireContext(), R.string.restore_failed, Toast.LENGTH_LONG).show();
            return;
        }
        // 내역·통계 탭은 다시 보일 때(onHiddenChanged) DB를 새로 읽는다
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.restore_done_title)
                .setMessage(getString(R.string.restore_done_message, backup.received, backup.sent))
                .setPositiveButton(R.string.action_ok, null)
                .setNegativeButton(R.string.action_undo_restore, (dialog, which) -> undoRestore())
                .show();
    }

    private void undoRestore() {
        boolean undone = DatabaseBackup.undo(requireContext());
        Toast.makeText(requireContext(), undone ? R.string.restore_undone : R.string.restore_undo_failed,
                Toast.LENGTH_SHORT).show();
    }

    private void showProblem(DatabaseBackup.Problem problem) {
        int message;
        switch (problem) {
            case NEWER_VERSION:
                message = R.string.restore_error_newer;
                break;
            case DAMAGED:
                message = R.string.restore_error_damaged;
                break;
            case READ_FAILED:
                message = R.string.restore_error_read;
                break;
            default:
                message = R.string.restore_error_not_backup;
                break;
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.restore_error_title)
                .setMessage(message)
                .setPositiveButton(R.string.action_ok, null)
                .show();
    }

    // ---- 진행 표시 ----

    @SuppressLint("InflateParams") // 대화상자 내용에는 부모 뷰가 없다
    private void showProgress(@StringRes int message) {
        hideProgress();
        View view = getLayoutInflater().inflate(R.layout.dialog_progress, null);
        ((TextView) view.findViewById(R.id.progress_text)).setText(message);
        progress = new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setCancelable(false)
                .show();
    }

    private void hideProgress() {
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }

    private SharedPreferences prefs() {
        return requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private String versionName() {
        try {
            String name = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0).versionName;
            return name == null ? "" : name;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }
}
