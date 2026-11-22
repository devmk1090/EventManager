package com.devproject.eventmanager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 내역 추가·수정 화면. 추가할 때는 보냄/받음을 고르고, 수정할 때는 방향을 바꿀 수 없다(테이블이 다르므로).
 * 저장 형식은 예전과 같다: 경조사·관계는 앞뒤 공백 포함(" 결혼식 "), 날짜는 "yyyy/M/d/요일", 금액은 숫자 문자열.
 */
public class EntryFormActivity extends AppCompatActivity {

    private static final String EXTRA_EDIT = "edit";
    private static final String EXTRA_DIRECTION = "direction";
    private static final String EXTRA_ID = "id";
    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_DATE = "date";
    private static final String EXTRA_CATEGORY = "category";
    private static final String EXTRA_RELATION = "relation";
    private static final String EXTRA_MONEY = "money";
    private static final String EXTRA_MEMO = "memo";

    private static final String[] CATEGORIES = {"결혼식", "돌잔치", "장례식", "환갑", "칠순", "생일", "기타"};
    private static final String[] RELATIONS = {"친구", "친척", "회사", "대학교", "가족", "지인", "기타"};
    private static final int MAX_DIGITS = 12;
    private static final long MAX_AMOUNT = 999_999_999_999L;

    private InOutDatabase database;
    private boolean editMode;
    private Entry.Direction direction;
    private int entryId;
    private String originalName = "";
    private String originalDate = "";
    private String originalCategory = "";
    private String originalRelation = "";

    private EntryDate selectedDate; // null = 날짜 없음 (예전 버전에서 날짜 없이 저장한 내역)
    private boolean dateChanged;

    private EditText moneyInput;
    private TextView moneyKorean;
    private EditText nameInput;
    private EditText memoInput;
    private TextView dateText;
    private ChipGroup categoryGroup;
    private ChipGroup relationGroup;
    private View saveButton;

    public static Intent newEntryIntent(Context context, Entry.Direction direction) {
        return new Intent(context, EntryFormActivity.class)
                .putExtra(EXTRA_DIRECTION, direction.name());
    }

    public static Intent editIntent(Context context, Entry entry) {
        return new Intent(context, EntryFormActivity.class)
                .putExtra(EXTRA_EDIT, true)
                .putExtra(EXTRA_DIRECTION, entry.direction.name())
                .putExtra(EXTRA_ID, entry.id)
                .putExtra(EXTRA_NAME, entry.name)
                .putExtra(EXTRA_DATE, entry.date)
                .putExtra(EXTRA_CATEGORY, entry.category)
                .putExtra(EXTRA_RELATION, entry.relation)
                .putExtra(EXTRA_MONEY, entry.money)
                .putExtra(EXTRA_MEMO, entry.memo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_entry_form);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        // 위는 상태 표시줄, 아래는 내비게이션 바와 키보드 중 큰 쪽만큼 띄워 저장 버튼이 가려지지 않게 한다
        View root = findViewById(R.id.form_root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            view.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, ime.bottom));
            return WindowInsetsCompat.CONSUMED;
        });

        database = InOutDatabase.getInstance(this);
        database.open();

        Intent intent = getIntent();
        editMode = intent.getBooleanExtra(EXTRA_EDIT, false);
        direction = directionOf(intent.getStringExtra(EXTRA_DIRECTION));
        if (editMode) {
            entryId = intent.getIntExtra(EXTRA_ID, -1);
            originalName = orEmpty(intent.getStringExtra(EXTRA_NAME));
            originalDate = orEmpty(intent.getStringExtra(EXTRA_DATE));
            originalCategory = orEmpty(intent.getStringExtra(EXTRA_CATEGORY));
            originalRelation = orEmpty(intent.getStringExtra(EXTRA_RELATION));
        }

        moneyInput = findViewById(R.id.money_input);
        moneyKorean = findViewById(R.id.money_korean);
        nameInput = findViewById(R.id.name_input);
        memoInput = findViewById(R.id.memo_input);
        dateText = findViewById(R.id.date_text);
        categoryGroup = findViewById(R.id.category_group);
        relationGroup = findViewById(R.id.relation_group);
        saveButton = findViewById(R.id.btn_save);

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());
        setUpHeaderAndDirection();
        setUpMoney();
        setUpDate();

        addChips(categoryGroup, CATEGORIES, originalCategory.trim());
        addChips(relationGroup, RELATIONS, originalRelation.trim());
        scrollToChecked(findViewById(R.id.category_scroll), categoryGroup);
        scrollToChecked(findViewById(R.id.relation_scroll), relationGroup);

        if (editMode) {
            nameInput.setText(originalName);
            memoInput.setText(orEmpty(intent.getStringExtra(EXTRA_MEMO)));
            long amount = MoneyFormat.parse(intent.getStringExtra(EXTRA_MONEY));
            moneyInput.setText(amount > 0 ? MoneyFormat.digits(amount) : "");
        }

        saveButton.setOnClickListener(v -> save());
    }

    private void setUpHeaderAndDirection() {
        TextView title = findViewById(R.id.form_title);
        RadioGroup directionGroup = findViewById(R.id.direction_group);
        View deleteButton = findViewById(R.id.btn_delete);
        if (editMode) {
            title.setText(direction == Entry.Direction.RECEIVED
                    ? R.string.form_title_edit_received : R.string.form_title_edit_sent);
            directionGroup.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v -> confirmDelete());
        } else {
            title.setText(R.string.form_title_new);
            directionGroup.check(direction == Entry.Direction.RECEIVED ? R.id.direction_received : R.id.direction_sent);
            directionGroup.setOnCheckedChangeListener((group, checkedId) ->
                    direction = checkedId == R.id.direction_received ? Entry.Direction.RECEIVED : Entry.Direction.SENT);
        }
    }

    private void setUpMoney() {
        moneyInput.addTextChangedListener(new TextWatcher() {
            private boolean formatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (formatting) {
                    return;
                }
                formatting = true;
                String digits = s.toString().replaceAll("[^0-9]", "");
                if (digits.length() > MAX_DIGITS) {
                    digits = digits.substring(0, MAX_DIGITS);
                }
                long amount = MoneyFormat.parse(digits);
                String formatted = digits.isEmpty() ? "" : MoneyFormat.digits(amount);
                if (!formatted.equals(s.toString())) {
                    s.replace(0, s.length(), formatted);
                    moneyInput.setSelection(formatted.length());
                }
                showKoreanAmount(amount);
                formatting = false;
            }
        });
        findViewById(R.id.quick_10k).setOnClickListener(v -> addAmount(10_000));
        findViewById(R.id.quick_50k).setOnClickListener(v -> addAmount(50_000));
        findViewById(R.id.quick_100k).setOnClickListener(v -> addAmount(100_000));
        findViewById(R.id.quick_clear).setOnClickListener(v -> moneyInput.setText(""));
    }

    private void addAmount(long add) {
        long amount = Math.min(MAX_AMOUNT, MoneyFormat.parse(moneyInput.getText().toString()) + add);
        moneyInput.setText(MoneyFormat.digits(amount));
        moneyInput.setSelection(moneyInput.getText().length());
    }

    private void showKoreanAmount(long amount) {
        if (amount > 0) {
            moneyKorean.setText(MoneyFormat.korean(amount));
            moneyKorean.setVisibility(View.VISIBLE);
        } else {
            moneyKorean.setVisibility(View.INVISIBLE);
        }
    }

    private void setUpDate() {
        if (editMode) {
            selectedDate = EntryDate.parse(originalDate);
        } else {
            Calendar today = Calendar.getInstance();
            selectedDate = EntryDate.of(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1,
                    today.get(Calendar.DAY_OF_MONTH));
        }
        showDate();
        findViewById(R.id.date_row).setOnClickListener(v -> pickDate());
    }

    private void pickDate() {
        Calendar today = Calendar.getInstance();
        int year = selectedDate != null ? selectedDate.year : today.get(Calendar.YEAR);
        int month = selectedDate != null ? selectedDate.month - 1 : today.get(Calendar.MONTH);
        int day = selectedDate != null ? selectedDate.day : today.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, (view, pickedYear, pickedMonth, pickedDay) -> {
            selectedDate = EntryDate.of(pickedYear, pickedMonth + 1, pickedDay);
            dateChanged = true;
            showDate();
        }, year, month, day).show();
    }

    private void showDate() {
        dateText.setText(selectedDate == null ? getString(R.string.no_date) : selectedDate.longText());
    }

    /** 기본 항목으로 칩을 만든다. 예전 버전에서 저장한 다른 값이면 그 값도 칩으로 넣어 그대로 둘 수 있게 한다. */
    private void addChips(ChipGroup group, String[] defaults, String selectedLabel) {
        List<String> labels = new ArrayList<>(Arrays.asList(defaults));
        if (!selectedLabel.isEmpty() && !labels.contains(selectedLabel)) {
            labels.add(selectedLabel);
        }
        LayoutInflater inflater = getLayoutInflater();
        for (String label : labels) {
            Chip chip = (Chip) inflater.inflate(R.layout.item_choice_chip, group, false);
            chip.setId(View.generateViewId());
            chip.setText(label);
            chip.setTag(label);
            group.addView(chip);
            if (label.equals(selectedLabel)) {
                group.check(chip.getId());
            }
        }
    }

    /** 고른 칩이 가로 목록 밖(오른쪽)에 있으면 보이도록 스크롤한다 */
    private static void scrollToChecked(HorizontalScrollView scroll, ChipGroup group) {
        int id = group.getCheckedChipId();
        if (id == View.NO_ID) {
            return;
        }
        View chip = group.findViewById(id);
        scroll.post(() -> {
            int overflow = chip.getRight() + scroll.getPaddingLeft() + scroll.getPaddingRight() - scroll.getWidth();
            if (overflow > 0) {
                scroll.scrollTo(overflow, 0);
            }
        });
    }

    private static String checkedLabel(ChipGroup group) {
        int id = group.getCheckedChipId();
        if (id == View.NO_ID) {
            return "";
        }
        View chip = group.findViewById(id);
        return chip == null ? "" : (String) chip.getTag();
    }

    private void save() {
        String name = nameInput.getText().toString().trim();
        long amount = MoneyFormat.parse(moneyInput.getText().toString());
        String categoryLabel = checkedLabel(categoryGroup);
        String relationLabel = checkedLabel(relationGroup);
        String memo = memoInput.getText().toString().trim();

        if (name.isEmpty()) {
            showError(R.string.error_name);
            nameInput.requestFocus();
            return;
        }
        if (amount <= 0) {
            showError(R.string.error_amount);
            return;
        }
        if (categoryLabel.isEmpty()) {
            showError(R.string.error_category);
            return;
        }
        if (relationLabel.isEmpty()) {
            showError(R.string.error_relation);
            return;
        }

        // 바꾸지 않은 값은 저장된 원문을 그대로 쓴다(공백·요일 언어까지 유지)
        String category = editMode && categoryLabel.equals(originalCategory.trim()) ? originalCategory : " " + categoryLabel + " ";
        String relation = editMode && relationLabel.equals(originalRelation.trim()) ? originalRelation : " " + relationLabel + " ";
        String date;
        if (editMode && !dateChanged) {
            date = originalDate;
        } else {
            date = selectedDate == null ? "" : selectedDate.toStorage(Locale.getDefault());
        }
        String money = String.valueOf(amount);

        boolean duplicate;
        if (editMode) {
            // (이름, 날짜, 경조사, 관계)를 바꿀 때만 검사해서, 예전 버전에서 생긴 중복 항목도 금액·메모는 고칠 수 있게 한다
            boolean keyChanged = !name.equals(originalName) || !date.equals(originalDate)
                    || !category.equals(originalCategory) || !relation.equals(originalRelation);
            duplicate = keyChanged && database.hasOtherItem(direction, entryId, name, date, category, relation);
        } else {
            duplicate = database.exists(direction, name, date, category, relation);
        }
        if (duplicate) {
            showError(R.string.error_duplicate);
            return;
        }

        boolean saved = editMode
                ? database.update(direction, entryId, name, date, category, relation, money, memo)
                : database.insert(direction, name, date, category, relation, money, memo);
        if (!saved) {
            showError(R.string.error_save);
            return;
        }
        Toast.makeText(this, editMode ? R.string.saved_edit : R.string.saved_new, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm_message)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    if (database.delete(direction, entryId)) {
                        Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showError(R.string.error_delete);
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void showError(@StringRes int message) {
        Snackbar.make(saveButton, message, Snackbar.LENGTH_SHORT).setAnchorView(saveButton).show();
    }

    private static Entry.Direction directionOf(String name) {
        return Entry.Direction.RECEIVED.name().equals(name) ? Entry.Direction.RECEIVED : Entry.Direction.SENT;
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }
}
