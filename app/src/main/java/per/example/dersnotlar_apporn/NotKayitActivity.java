package per.example.dersnotlar_apporn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class NotKayitActivity extends AppCompatActivity {
    TextInputLayout inputLayoutDersAdi, inputLayoutNot1, inputLayoutNot2;
    TextInputEditText editTxtDersAd, editTxtNot1, editTxtNot2;
    Button btnNotKayit;
    DBConnection dbConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_kayit);
        editTxtDersAd = findViewById(R.id.editTxt_DetayDersAdi);
        editTxtNot1 = findViewById(R.id.editTxt_DetayNot1);
        editTxtNot2 = findViewById(R.id.editTxt_DetayNot2);
        btnNotKayit = findViewById(R.id.btnNotKayit);
        inputLayoutDersAdi = findViewById(R.id.input_DetayDersAdi);
        inputLayoutNot1 = findViewById(R.id.input_DetayNot1);
        inputLayoutNot2 = findViewById(R.id.input_DetayNot2);


        dbConnection = new DBConnection(this);

        btnNotKayit.setOnClickListener(view -> {

            String dersAdi = editTxtDersAd.getText().toString().trim();
            String not1 = editTxtNot1.getText().toString().trim();
            String not2 = editTxtNot2.getText().toString().trim();

            if (TextUtils.isEmpty(dersAdi)) {

                inputLayoutDersAdi.setError("Bu alan boş bırakılamaz!");
            } else if (TextUtils.isEmpty(not1)) {

                inputLayoutNot1.setError("Bu alan boş bırakılamaz!");
            } else if (TextUtils.isEmpty(not2)) {

                inputLayoutNot2.setError("Bu alan boş bırakılamaz!");
            } else {
                new NotlarDao().notEkle(dbConnection, dersAdi, Integer.parseInt(not1), Integer.parseInt(not2));
                startActivity(new Intent(NotKayitActivity.this, MainActivity.class));
                finish();
            }

        });
    }
}