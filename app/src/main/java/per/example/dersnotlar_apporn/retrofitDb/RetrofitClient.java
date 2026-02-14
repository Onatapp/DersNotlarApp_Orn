package per.example.dersnotlar_apporn.retrofitDb;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    /*
    Retrofit kurulum aşaması parçası.
    RetrofitClient sınıfı, web servis ile bağlantıyı sağlayan bir sınıftır.
    */

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
