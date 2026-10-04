package vn.edu.vhu.ltdd.a6controls;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A6_231A290126";

    public static final String EXTRA_TOM_TAT = "extra_tom_tat";

    private EditText edtHoTen, edtMssv;
    private Spinner spMonHoc;
    private RadioGroup rgHeDaoTao;
    private CheckBox cbSang, cbChieu, cbToi;
    private MaterialSwitch swThongBao, swCheDoToi;
    private ToggleButton tgUuTien;
    private TextView tvSoBuoi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Chừa chỗ cho thanh hệ thống, phần khuyết màn hình và bàn phím.
        View root = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets safe = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
                            | WindowInsetsCompat.Type.ime()
            );

            v.setPadding(
                    safe.left,
                    safe.top,
                    safe.right,
                    safe.bottom
            );

            return insets;
        });

        ViewCompat.requestApplyInsets(root);

        // 1. Nối biến Java với các control trong XML.
        edtHoTen = findViewById(R.id.edtHoTen);
        edtMssv = findViewById(R.id.edtMssv);

        spMonHoc = findViewById(R.id.spMonHoc);
        rgHeDaoTao = findViewById(R.id.rgHeDaoTao);

        cbSang = findViewById(R.id.cbSang);
        cbChieu = findViewById(R.id.cbChieu);
        cbToi = findViewById(R.id.cbToi);

        swThongBao = findViewById(R.id.swThongBao);
        swCheDoToi = findViewById(R.id.swCheDoToi);

        tgUuTien = findViewById(R.id.tgUuTien);
        tvSoBuoi = findViewById(R.id.tvSoBuoi);

        Button btnXacNhan = findViewById(R.id.btnXacNhan);
        Button btnLamLai = findViewById(R.id.btnLamLai);

        // 2. Đọc danh sách học phần và đưa vào Spinner.
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.mon_hoc,
                        android.R.layout.simple_spinner_item
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spMonHoc.setAdapter(adapter);

        // 3. Bắt sự kiện chọn học phần.
        spMonHoc.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        Log.d(
                                TAG,
                                "Học phần: "
                                        + parent.getItemAtPosition(position)
                        );
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Interface yêu cầu phương thức này.
                    }
                }
        );

        // 4. NC2: dùng chung listener cho ba CheckBox.
        CompoundButton.OnCheckedChangeListener buoiListener =
                (button, checked) -> {
                    capNhatSoBuoi();

                    Log.d(
                            TAG,
                            "Thay đổi buổi học: "
                                    + button.getText()
                                    + " = "
                                    + checked
                    );
                };

        cbSang.setOnCheckedChangeListener(buoiListener);
        cbChieu.setOnCheckedChangeListener(buoiListener);
        cbToi.setOnCheckedChangeListener(buoiListener);

        capNhatSoBuoi();

        // 5. Bắt sự kiện chọn hệ đào tạo.
        rgHeDaoTao.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == -1) {
                Log.d(TAG, "Chưa chọn hệ đào tạo");
            } else {
                String he = checkedId == R.id.rbChinhQuy
                        ? getString(R.string.he_chinh_quy)
                        : getString(R.string.he_vlvh);

                Log.d(TAG, "Hệ đào tạo: " + he);
            }
        });

        // 6. Bắt sự kiện công tắc thông báo và nút ưu tiên.
        swThongBao.setOnCheckedChangeListener((button, checked) ->
                Log.d(TAG, "Nhận thông báo: " + checked)
        );

        tgUuTien.setOnCheckedChangeListener((button, checked) ->
                Log.d(TAG, "Ưu tiên: " + checked)
        );

        // 7. Bắt sự kiện hai nút.
        btnXacNhan.setOnClickListener(v -> xacNhan());
        btnLamLai.setOnClickListener(v -> lamLai());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Hệ thống đã khôi phục trạng thái View sau xoay/đổi theme.
        capNhatSoBuoi();

        boolean dangToi =
                (getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK)
                        == Configuration.UI_MODE_NIGHT_YES;

        swCheDoToi.setChecked(dangToi);

        // NC3: gắn listener sau khi khôi phục trạng thái.
        swCheDoToi.setOnCheckedChangeListener((button, checked) ->
                AppCompatDelegate.setDefaultNightMode(
                        checked
                                ? AppCompatDelegate.MODE_NIGHT_YES
                                : AppCompatDelegate.MODE_NIGHT_NO
                )
        );
    }

    // Đếm số buổi đang được chọn.
    private void capNhatSoBuoi() {
        int soBuoi = 0;

        if (cbSang.isChecked()) soBuoi++;
        if (cbChieu.isChecked()) soBuoi++;
        if (cbToi.isChecked()) soBuoi++;

        tvSoBuoi.setText(
                getString(R.string.so_buoi_format, soBuoi)
        );
    }

    // Kiểm tra dữ liệu và mở màn hình xác nhận.
    private void xacNhan() {
        String hoTen = edtHoTen.getText()
                .toString()
                .trim();

        String mssv = edtMssv.getText()
                .toString()
                .trim()
                .toUpperCase(Locale.ROOT);

        edtHoTen.setError(null);
        edtMssv.setError(null);

        // Lỗi 1: họ tên trống.
        if (hoTen.isEmpty()) {
            edtHoTen.setError(getString(R.string.err_empty));
            edtHoTen.requestFocus();
            return;
        }

        // Lỗi 2: MSSV không đúng 10 ký tự chữ hoặc số.
        // Điều chỉnh để nhận MSSV thật 231A290126.
        if (!mssv.matches("[A-Z0-9]{10}")) {
            edtMssv.setError(getString(R.string.err_mssv));
            edtMssv.requestFocus();
            return;
        }

        // Lỗi 3: chưa chọn hệ đào tạo.
        if (rgHeDaoTao.getCheckedRadioButtonId() == -1) {
            Toast.makeText(
                    this,
                    R.string.err_he,
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Gom các buổi được chọn.
        List<String> buoiHoc = new ArrayList<>();

        if (cbSang.isChecked()) {
            buoiHoc.add(getString(R.string.buoi_sang));
        }

        if (cbChieu.isChecked()) {
            buoiHoc.add(getString(R.string.buoi_chieu));
        }

        if (cbToi.isChecked()) {
            buoiHoc.add(getString(R.string.buoi_toi));
        }

        // Lỗi 4: chưa chọn buổi học.
        if (buoiHoc.isEmpty()) {
            Toast.makeText(
                    this,
                    R.string.err_buoi,
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String he =
                rgHeDaoTao.getCheckedRadioButtonId() == R.id.rbChinhQuy
                        ? getString(R.string.he_chinh_quy)
                        : getString(R.string.he_vlvh);

        // Lập bản tóm tắt gồm bảy mục.
        String tomTat = getString(
                R.string.tom_tat_format,
                hoTen,
                mssv,
                spMonHoc.getSelectedItem().toString(),
                he,
                TextUtils.join(", ", buoiHoc),
                swThongBao.isChecked()
                        ? getString(R.string.co)
                        : getString(R.string.khong),
                tgUuTien.isChecked()
                        ? getString(R.string.bat)
                        : getString(R.string.tat)
        );

        // Gửi dữ liệu và chuyển màn hình.
        Intent intent = new Intent(this, ConfirmActivity.class);

        intent.putExtra(EXTRA_TOM_TAT, tomTat);

        startActivity(intent);
    }

    // Đặt lại form.
    private void lamLai() {
        edtHoTen.setText("");
        edtMssv.setText("");

        edtHoTen.setError(null);
        edtMssv.setError(null);

        spMonHoc.setSelection(0);
        rgHeDaoTao.clearCheck();

        cbSang.setChecked(false);
        cbChieu.setChecked(false);
        cbToi.setChecked(false);

        swThongBao.setChecked(true);
        tgUuTien.setChecked(false);

        capNhatSoBuoi();
        edtHoTen.requestFocus();

        swCheDoToi.setChecked(false);
    }
}