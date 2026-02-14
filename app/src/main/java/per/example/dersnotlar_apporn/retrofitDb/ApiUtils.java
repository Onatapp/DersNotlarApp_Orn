package per.example.dersnotlar_apporn.retrofitDb;

public class ApiUtils {
    /*
    Retrofit kurulum aşaması parçası.
    ApiUtils, BaseURL (Ana link) ile web servis/hosting arasında bağlantıyı sağlayıp NotlarInterface sınıfının alındığı sınıftır.
    BaseURL olarak yazılacak link, mutlaka çalışan web servisin ana linki olmalı.
     */

    public static final String BaseURL = "https://restfuldb.onatsomer.com";

    public static NotlarInterface getKelimelerInterface(){
        return RetrofitClient.getClient(BaseURL).create(NotlarInterface.class);
    }
}