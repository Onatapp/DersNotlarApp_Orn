package per.example.dersnotlar_apporn;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import per.example.dersnotlar_apporn.databinding.ActivityMainBinding;
import per.example.dersnotlar_apporn.retrofitDb.ApiUtils;
import per.example.dersnotlar_apporn.retrofitDb.NotlarCevap;
import per.example.dersnotlar_apporn.retrofitDb.NotlarInterface;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Notlar> notlarArrayList;
    private DBConnection dbConnection;
    private ActivityMainBinding bind;
    private String txtDersAdi, txtNot1, txtNot2;
    private NotlarInterface notlarInterface;
    private NotlarCardAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        dbConnection = new DBConnection(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("notlar");

        // notlarArrayList = new NotlarDao().tumNotlar(dbConnection); //-- VOLLEY Kullanımı
        notlarInterface = ApiUtils.getKelimelerInterface(); //-- RETROFIT Kullanımı

        setSupportActionBar(bind.toolbar);
        bind.rv.setHasFixedSize(true);
        bind.rv.setLayoutManager(new LinearLayoutManager(this));

        notlarArrayList = new ArrayList<>();
        adapter = new NotlarCardAdapter(this, notlarArrayList);
        bind.rv.setAdapter(adapter);

        notlarListele();

        bind.fabAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                RETROFIT kütüphanesi kullanılarak yeni ders notu kaydetme kodları
                 */
                BottomSheetDialog bottomSheet = new BottomSheetDialog(MainActivity.this);
                bottomSheet.requestWindowFeature(Window.FEATURE_NO_TITLE);
                v = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottom_sheet, null);
                bottomSheet.setContentView(v);

                TextInputLayout txtLayoutDersAd = v.findViewById(R.id.txtLayoutDersAd);
                TextInputEditText editTxtDersAd = v.findViewById(R.id.editTxtDersAd);
                TextInputLayout txtLayoutNot1 = v.findViewById(R.id.txtLayoutNot1);
                TextInputEditText editTxtNot1 = v.findViewById(R.id.editTxtNot1);
                TextInputLayout txtLayoutNot2 = v.findViewById(R.id.txtLayoutNot2);
                TextInputEditText editTxtNot2 = v.findViewById(R.id.editTxtNot2);
                Button btnKaydet = v.findViewById(R.id.btnKaydet);

                bottomSheet.getWindow().setGravity(Gravity.BOTTOM);
                bottomSheet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                bottomSheet.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                btnKaydet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        txtDersAdi = editTxtDersAd.getText().toString().trim();
                        txtNot1 = editTxtNot1.getText().toString().trim();
                        txtNot2 = editTxtNot2.getText().toString().trim();

                        //-- Ders notunu FIREBASE DB'ye Bottom Sheet üzerinden kayıt işlemi. --
                        if (txtDersAdi.isEmpty() || txtNot1.isEmpty() || txtNot2.isEmpty()) {

                            txtLayoutDersAd.setError("Bu alanı lütfen doldurun!");
                            txtLayoutNot1.setError("Bu alanı lütfen doldurun!");
                            txtLayoutNot2.setError("Bu alanı lütfen doldurun!");
                        } else {

                            Notlar dersNot = new Notlar("", txtDersAdi, Integer.parseInt(txtNot1), Integer.parseInt(txtNot2));
                            myRef.push().setValue(dersNot);

                            Toast.makeText(btnKaydet.getContext(), "Ders notu başarıyla kaydedildi.", Toast.LENGTH_LONG).show();
                            bottomSheet.dismiss();
                            notlarListele();
                        }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------

                         /*--- Ders notunu RETROFIT ile web servis DB'ye Bottom Sheet üzerinden kayıt işlemi. ---
                         if (txtDersAdi.isEmpty() || txtNot1.isEmpty() || txtNot2.isEmpty()) {

                            txtLayoutDersAd.setError("Bu alanı lütfen doldurun!");
                            txtLayoutNot1.setError("Bu alanı lütfen doldurun!");
                            txtLayoutNot2.setError("Bu alanı lütfen doldurun!");
                        } else {

                            notlarInterface.notKaydet(txtDersAdi, Integer.parseInt(txtNot1), Integer.parseInt(txtNot2)).enqueue(new Callback<CRUDCevap>() {
                                @Override
                                public void onResponse(Call<CRUDCevap> call, Response<CRUDCevap> response) {

                                    Toast.makeText(btnKaydet.getContext(), "Ders notu başarıyla kaydedildi.", Toast.LENGTH_LONG).show();
                                    Log.e("RETROFIT KAYDETME", "Girilen not başarıyla kaydedildi.");
                                    bottomSheet.dismiss();
                                    notlarListele();
                                }

                                @Override
                                public void onFailure(Call<CRUDCevap> call, Throwable t) {

                                    Log.e("RETROFIT KAYDETME HATA", "Not kaydederken hata oluştu. // " + t);
                                }
                            });
                        }
                        */
                    }
                });
                bottomSheet.show();

                bottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        //* Bu back pressed methoduyla Main Activity'den geri tuşuna tıklandığında her türlü uygulamadan çıkmasını sağlıyor.
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    void notlarListele() {

        /*
        --- FIREBASE DB kullanarak kayıtlı ders notlarını listeleme yapılıyor.
        Son kısımda eklenen notların ortalaması hesaplanıyor.
         */
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                notlarArrayList.clear();
                double toplam = 0;

                for (DataSnapshot d : snapshot.getChildren()) {

                    Notlar not = d.getValue(Notlar.class);
                    not.setNotID(d.getKey());
                    notlarArrayList.add(not);

                    toplam = toplam + (not.getNot1() + not.getNot2()) / 2;
                }

                adapter.notifyDataSetChanged();
                bind.toolbar.setSubtitle("Ortalama: " + toplam / notlarArrayList.size());
                bind.toolbar.setSubtitleTextColor(Color.WHITE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.e("FIREBASE LİSTELEME HATA", "Firebase'den veriler listelenirken hata oluştu." + error);
            }
        });


        /*
        --- RETROFIT kütüphanesi kullanılarak web servisten gelen ders notu verilerini listeleme işlemi.

        notlarInterface.notListele().enqueue(new Callback<NotlarCevap>() {
            @Override
            public void onResponse(Call<NotlarCevap> call, retrofit2.Response<NotlarCevap> response) {

                double toplam = 0;
                List<Notlar> liste = response.body().getNotlar();

                for (Notlar n : liste) {
                    //--- Ortalama hesaplama
                    toplam = toplam + (double) (n.getNot1() + n.getNot2()) / 2;
                }

                /* adapter = new NotlarCardAdapter(MainActivity.this, liste);
                bind.rv.setAdapter(adapter);
                Log.e("RETROFIT LİSTELEME", "Not listesi başarıyla yüklendi.");
                bind.toolbar.setSubtitle("Ortalama: " + toplam / liste.size());
                bind.toolbar.setSubtitleTextColor(Color.WHITE);
            }

            @Override
            public void onFailure(Call<NotlarCevap> call, Throwable t) {

                Log.e("RETROFIT LİSTELEME HATA", "Notlar listelenirken hata oluştu. // " + t);
            }
        }); */
    }

    void notListeleVolley() {
        /* VOLLEY kullanarak web servise kaydedilen verileri arayüzde LİSTELEME işlemi yapılıyor.

         *Response methodu, web servisten veya sunucudan gelen cevabı ilk JSON türünde alır ve
         "GET" işlemiyle kayıtlı veriler try-catch bloğu içerisinde JSON türünden ayıklanarak sırayla ArrayList'te tutularak ve Adapter ile arayüze listeleniyor.
         *ErrorResponse methodu, "GET" işlemi gerçekleşirken veya gerçekleştikten sonra web servisten hatalı cevap geldiği zaman yapılacak işlemler yazılır.
         */

        String url = "https://restfuldb.onatsomer.com/notlar";

        StringRequest req = new StringRequest(Request.Method.GET, url + "/list_notlar.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                notlarArrayList = new ArrayList<>();
                double toplam = 0;

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonNotlar = jsonObject.getJSONArray("notlar");

                    for (int i = 0; i < jsonNotlar.length(); i++) {

                        JSONObject val = jsonNotlar.getJSONObject(i);

                        String notID = val.getString("not_id");
                        String dersAdi = val.getString("ders_adi");
                        int not1 = val.getInt("not1");
                        int not2 = val.getInt("not2");

                        toplam = toplam + (not1 + not2) / 2;

                        Notlar notlar = new Notlar(notID, dersAdi, not1, not2);
                        notlarArrayList.add(notlar);
                    }

                    NotlarCardAdapter adapter = new NotlarCardAdapter(MainActivity.this, notlarArrayList);
                    bind.rv.setAdapter(adapter);

                    bind.toolbar.setSubtitle("Ortalama = " + (toplam / notlarArrayList.size()));
                    bind.toolbar.setSubtitleTextColor(Color.WHITE);

                } catch (JSONException e) {
                    Log.e("VOLLEY LİSTELEME HATASI", e.getMessage().toString());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("LİSTELEMEDE HATA", error.getMessage().toString());
            }
        });

        Volley.newRequestQueue(this).add(req);
    }

    void notKaydetVolley() {
        /*
        VOLLEY kullanarak web servis ve Bottom Sheet ile KAYDETME işlemi yapılıyor.
         */

        BottomSheetDialog bottomSheet = new BottomSheetDialog(MainActivity.this);
        bottomSheet.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(this).inflate(R.layout.bottom_sheet, null);
        bottomSheet.setContentView(v);

        TextInputLayout txtLayoutDersAd = v.findViewById(R.id.txtLayoutDersAd);
        TextInputEditText editTxtDersAd = v.findViewById(R.id.editTxtDersAd);
        TextInputLayout txtLayoutNot1 = v.findViewById(R.id.txtLayoutNot1);
        TextInputEditText editTxtNot1 = v.findViewById(R.id.editTxtNot1);
        TextInputLayout txtLayoutNot2 = v.findViewById(R.id.txtLayoutNot2);
        TextInputEditText editTxtNot2 = v.findViewById(R.id.editTxtNot2);
        Button btnKaydet = v.findViewById(R.id.btnKaydet);

        bottomSheet.getWindow().setGravity(Gravity.BOTTOM);
        bottomSheet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheet.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        String url = "https://restfuldb.onatsomer.com/notlar";

        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtDersAdi = editTxtDersAd.getText().toString();
                txtNot1 = editTxtNot1.getText().toString();
                txtNot2 = editTxtNot2.getText().toString();

                if (txtDersAdi.isEmpty() || txtNot1.isEmpty() || txtNot2.isEmpty()) {

                    txtLayoutDersAd.setError("Bu alan boş geçilemez.");
                    txtLayoutNot1.setError("Bu alan boş geçilemez.");
                    txtLayoutNot2.setError("Bu alan boş geçilemez.");
                } else {

                    StringRequest req = new StringRequest(Request.Method.POST, url + "/insert_not.php", new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("GELEN CEVAP - KAYDETME", response);
                            Toast.makeText(MainActivity.this, "Not Başarıyla Kaydedildi.", Toast.LENGTH_LONG).show();
                            bottomSheet.dismiss();
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e("KAYDETME İŞLEMİNDE HATA", error.getMessage().toString());
                        }

                    }) {
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> params = new HashMap<>();
                            params.put("ders_adi", txtDersAdi);
                            params.put("not1", txtNot1);
                            params.put("not2", txtNot2);

                            return params;
                        }
                    };

                    Volley.newRequestQueue(MainActivity.this).add(req);
                }
            }
        });
        bottomSheet.show();

        bottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
    }
}