package per.example.dersnotlar_apporn;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import per.example.dersnotlar_apporn.databinding.ActivityNotDetayBinding;
import per.example.dersnotlar_apporn.retrofitDb.ApiUtils;
import per.example.dersnotlar_apporn.retrofitDb.NotlarInterface;

public class NotDetayActivity extends AppCompatActivity {
    DBConnection dbConnection;
    Notlar not;
    ActivityNotDetayBinding bind;
    private NotlarInterface notlarInterface;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityNotDetayBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        setSupportActionBar(bind.detayToolbar);
        bind.detayToolbar.setTitleTextColor(Color.WHITE);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("notlar");

        // dbConnection = new DBConnection(this);
        notlarInterface = ApiUtils.getKelimelerInterface();

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

        /*
        Kayıtlı ders notlarının SİLME ve GÜNCELLEME işlemleri FIREBASE DB yöntemi ile yapılıyor.
         */
        String dersAd = bind.editTxtDetayDersAdi.getText().toString().trim();
        String dersNot1 = bind.editTxtDetayNot1.getText().toString().trim();
        String dersNot2 = bind.editTxtDetayNot2.getText().toString().trim();

        if (item.getItemId() == R.id.action_Edit) {

            if (dersAd.isEmpty() || dersNot1.isEmpty() || dersNot2.isEmpty()) {

                bind.inputDetayDersAdi.setError("Lütfen bu alanı doldurun!");
                bind.inputDetayNot1.setError("Lütfen bu alanı doldurun!");
                bind.inputDetayNot2.setError("Lütfen bu alanı doldurun!");
            } else {

                AlertDialog.Builder ad = new AlertDialog.Builder(this);
                ad.setIcon(R.drawable.question_mark_24);
                ad.setTitle("Ders Notu Düzenleme");
                ad.setMessage(dersAd + " İsimli ders notunun bilgilerini güncellemek istiyor musunuz?");

                ad.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Map<String, Object> values = new HashMap<>();
                        values.put("dersAdi", dersAd);
                        values.put("not1", Integer.parseInt(dersNot1));
                        values.put("not2", Integer.parseInt(dersNot2));

                        myRef.child(not.getNotID()).updateChildren(values);

                        startActivity(new Intent(NotDetayActivity.this, MainActivity.class));
                        Toast.makeText(NotDetayActivity.this, "İlgili ders başarıyla güncellendi.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                ad.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
            }
        } else if (item.getItemId() == R.id.action_Delete) {

            AlertDialog.Builder AD = new AlertDialog.Builder(this);
            AD.setTitle("Ders Notu Silme");
            AD.setIcon(R.drawable.question_mark_24);
            AD.setMessage(dersAd + " İsimli ders notu bilgisini silmek istediğinizden emin misiniz?");

            AD.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    myRef.child(not.getNotID()).removeValue();

                    startActivity(new Intent(NotDetayActivity.this, MainActivity.class));
                    Toast.makeText(NotDetayActivity.this, "Ders notu başarıyla silindi.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
            AD.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        }

        return true;
    }


    void notRetrofitIslemler() {
        /*
        RETROFIT kütüphanesi kullanılarak web servisten gelen ders notu verilerinin silme ve güncelleme işlemleri.

        if (item.getItemId() == R.id.action_Delete) {

            Snackbar.make(bind.detayToolbar, "Seçili ders notunu silmeyi onaylıyor musunuz?", Snackbar.LENGTH_LONG).setAction("Evet", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notlarInterface.notSil(Integer.parseInt(not.getNotID())).enqueue(new Callback<CRUDCevap>() {
                        @Override
                        public void onResponse(Call<CRUDCevap> call, Response<CRUDCevap> response) {

                            startActivity(new Intent(NotDetayActivity.this, MainActivity.class));
                            finish();
                            Toast.makeText(NotDetayActivity.this, "Ders notu başarıyla silindi.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<CRUDCevap> call, Throwable t) {

                            Log.e("RETROFIT SİLME HATASI", "Ders notu silinirken hata oluştu." + t);
                        }
                    });
                }
            }).show();

        } else if (item.getItemId() == R.id.action_Edit) {

            String dersAd = bind.editTxtDetayDersAdi.getText().toString().trim();
            String dersNot1 = bind.editTxtDetayNot1.getText().toString().trim();
            String dersNot2 = bind.editTxtDetayNot2.getText().toString().trim();

            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setIcon(R.drawable.question_mark_24);
            ad.setTitle("Ders Notu Güncelleme");
            ad.setMessage(dersAd + " İsimli dersin bilgilerini güncellemek istiyor musunuz?");

            ad.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    notlarInterface.notGuncelle(Integer.parseInt(not.getNotID()), dersAd, Integer.parseInt(dersNot1), Integer.parseInt(dersNot2)).enqueue(new Callback<CRUDCevap>() {
                        @Override
                        public void onResponse(Call<CRUDCevap> call, Response<CRUDCevap> response) {

                            startActivity(new Intent(NotDetayActivity.this, MainActivity.class));
                            finish();
                            Toast.makeText(NotDetayActivity.this, "İlgili dersin bilgileri başarıyla güncellendi.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<CRUDCevap> call, Throwable t) {

                            Log.e("RETROFIT NOT GÜNCELLEME", "Ders bilgileri güncellenirken hata oluştu. // " + t);
                        }
                    });
                }
            });
            ad.setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        }
         */
    }

    void notSilGuncelle() {
        /* VOLLEY kullanarak web servis ile ders notu SİLME işlemi yapılıyor.

         *Response methodu, web servisten veya sunucudan gelen cevabı alır ve "POST" işlemi gerçekleştikten sonra yapılacak işlemler yazılır.
         *ErrorResponse methodu, "POST" işlemi gerçekleşirken veya gerçekleştikten sonra web servisten hatalı cevap geldiği zaman yapılacak işlemler yazılır.
         *getParams methodu, web serviste bulunan koşullar için gerekli sorgulamalar ve işlemler yazılır.
         */

        String dersAd = bind.editTxtDetayDersAdi.getText().toString().trim();
        String dersNot1 = bind.editTxtDetayNot1.getText().toString().trim();
        String dersNot2 = bind.editTxtDetayNot2.getText().toString().trim();

        String url = "https://restfuldb.onatsomer.com/notlar/";

        Snackbar.make(bind.detayToolbar, "Seçilen ders notu silinsin mi?", Snackbar.LENGTH_LONG).setAction("Evet", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringRequest req = new StringRequest(Request.Method.POST, url + "delete_not.php", new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("GELEN CEVAP - SİLME", response);
                        startActivity(new Intent(NotDetayActivity.this, MainActivity.class));
                        finish();
                        Toast.makeText(NotDetayActivity.this, "Seçili ders notu başarıyla silindi.", Toast.LENGTH_LONG).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
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
//----------------------------------------------------------------------------------------------------------------------------------------------//

        /* VOLLEY kullanarak web servis ile ders notu güncelleme işlemi yapılıyor.

         *Response methodu, web servisten veya sunucudan gelen cevabı alır ve "POST" işlemi gerçekleştikten sonra yapılacak işlemler yazılır.
         *ErrorResponse methodu, "POST" işlemi gerçekleşirken veya gerçekleştikten sonra web servisten hatalı cevap geldiği zaman yapılacak işlemler yazılır.
         *getParams methodu, web serviste bulunan koşullar için gerekli sorgulamalar ve işlemler yazılır.
         */

        StringRequest req = new StringRequest(Request.Method.POST, url + "update_not.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("GELEN CEVAP - GÜNCELLEME", response);
                startActivity(new Intent(NotDetayActivity.this, MainActivity.class));
                finish();
                Toast.makeText(NotDetayActivity.this, "Seçili ders notu başarıyla güncellendi.", Toast.LENGTH_LONG).show();
            }
        }, new com.android.volley.Response.ErrorListener() {
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
}