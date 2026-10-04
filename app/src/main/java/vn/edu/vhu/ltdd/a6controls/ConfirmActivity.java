package vn.edu.vhu.ltdd.a6controls;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm);

        View root = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets safe = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
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

        TextView tvTomTat = findViewById(R.id.tvTomTat);
        Button btnQuayLai = findViewById(R.id.btnQuayLai);

        // Nhận chuỗi được gửi từ MainActivity.
        String tomTat = getIntent().getStringExtra(
                MainActivity.EXTRA_TOM_TAT
        );

        // Hiển thị dữ liệu hoặc thông báo nếu không có.
        tvTomTat.setText(
                tomTat != null
                        ? tomTat
                        : getString(R.string.no_data)
        );

        // Đóng màn hình xác nhận để về form cũ.
        btnQuayLai.setOnClickListener(v -> finish());
    }
}