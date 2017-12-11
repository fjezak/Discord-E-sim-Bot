package esim;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IESim {
    @GET("apiCitizenByName.html")
    Call<ESimCitizen> citizenInfo(@Query("name") String name);

    @GET("battle.html")
    Call<ResponseBody> battle(@Query("id") int id);

    @GET("events.html")
    Call<ResponseBody> events();

    @GET("index.html")
    Call<ResponseBody> index();

    @POST("login.html")
    Call<ResponseBody> login(@Body RequestBody body);
}
