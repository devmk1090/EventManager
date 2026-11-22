package com.devproject.eventmanager;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/** 설정 탭: 엑셀 내보내기와 앱 버전. */
public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        view.findViewById(R.id.row_export).setOnClickListener(v -> ExcelExporter.confirmAndExport(requireContext()));
        ((TextView) view.findViewById(R.id.version_text)).setText(versionName());
        return view;
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
