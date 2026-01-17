package per.example.dersnotlar_apporn;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

import per.example.dersnotlar_apporn.databinding.ActivityNotDetayBinding;

public class NotDetayActivity extends AppCompatActivity {
    DBConnection dbConnection;
    Notlar not;
    ActivityNotDetayBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityNotDetayBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        setSupportActionBar(bind.detayToolbar);
        bind.detayToolbar.setTitleTextColor(Color.WHITE);

        dbConnection = new DBConnection(this);

        //* Main Activity'den sayfa geçişiyle gönderilen verileri "nesne" key ismiyle karşılama.
        not = (Notlar) getIntent().getSerializableExtra("nesne");
        bind.editTxtDetayDersAdi.setText(not.getDersAdi());
        bind.editTxtDetayNot1.setText(String.valueOf(not.getNot1()));
        bind.editTxtDetayNot2.setText(String.valueOf(not.getNot2()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /* VOLLEY kullanarak web servis ile ders notu SİLME işlemi yapılıyor.

         *Response methodu, web servisten veya sunucudan gelen cevabı alır ve "POST" işlemi gerçekleştikten sonra yapılacak işlemler yazılır.
         *ErrorResponse methodu, "POST" işlemi gerçekleşirken veya gerçekleştikten sonra web servisten hatalı cevap geldiği zaman yapılacak işlemler yazılır.
         *getParams methodu, web serviste bulunan koşullar için gerekli sorgulamalar ve işlemler yazılır.
         */

        String url = "https://restfuldb.onatsomer.com/notlar/";

        if (item.getItemId() == R.id.action_Delete) {

            Snackbar.make(bind.detayToolbar, "Seçilen ders notu silinsin mi?", Snackbar.LENGTH_LONG)
                    .setAction("Evet", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            StringRequest req = new StringRequest(Request.Method.POST, url + "delete_not.php", new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.e("GELEN CEVAP - SİLME", response);
                                    startActivity(new Intent(NotDetayActivity.this, MainActivity.class));
                                    finish();
                                    Toast.makeText(NotDetayActivity.this, "Seçili ders notu başarıyla silindi.", Toast.LENGTH_LONG).show();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Log.e("SİLME İŞLEMİNDE HATA", error.getMessage().toString());
                                }
                            }) {
                                @Nullable
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {

                                    Map<String, String> params = new HashMap<>();
                                    params.put("not_id", String.valueOf(not.getNotID()));

                                    return params;
                                }
                            };

                            Volley.newRequestQueue(NotDetayActivity.this).add(req);
                        }
                    }).show();

        } else if (item.getItemId() == R.id.action_Edit) {
            /* VOLLEY kullanarak web servis ile ders notu güncelleme işlemi yapılıyor.

             *Response methodu, web servisten veya sunucudan gelen cevabı alır ve "POST" işlemi gerçekleştikten sonra yapılacak işlemler yazılır.
             *ErrorResponse methodu, "POST" işlemi gerçekleşirken veya gerçekleştikten sonra web servisten hatalı cevap geldiği zaman yapılacak işlemler yazılır.
             *getParams methodu, web serviste bulunan koşullar için gerekli sorgulamalar ve işlemler yazılır.
             */

            String dersAd = bind.editTxtDetayDersAdi.getText().toString();
            String dersNot1 = bind.editTxtDetayNot1.getText().toString();
            String dersNot2 = bind.editTxtDetayNot2.getText().toString();

            StringRequest req = new StringRequest(Request.Method.POST, url + "update_not.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.e("GELEN CEVAP - GÜNCELLEME", response);
                    startActivity(new Intent(NotDetayActivity.this, MainActivity.class));
                    finish();
                    Toast.makeText(NotDetayActivity.this, "Seçili ders notu başarıyla güncellendi.", Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e("GÜNCELLEME İŞLEMİNDE HATA", error.getMessage().toString());
                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("not_id", String.valueOf(not.getNotID()));
                    params.put("ders_adi", dersAd);
                    params.put("not1", dersNot1);
                    params.put("not2", dersNot2);

                    return params;
                }
            };

            Volley.newRequestQueue(NotDetayActivity.this).add(req);
        }
        return true;
    }
}