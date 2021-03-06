package eu.rafalolszewski.simplyweather.api;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import eu.rafalolszewski.simplyweather.api.callback.WeatherApiCallback;
import eu.rafalolszewski.simplyweather.model.openweather.WeatherCurrentData;
import eu.rafalolszewski.simplyweather.model.openweather.WeatherFiveDaysData;
import eu.rafalolszewski.simplyweather.util.StringsProvider;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by rafal on 12.03.16.
 */
public class OpenWeatherApi {

    private static final String KEY_OF_METADATA_OPENWEATHER_KEY = "openweather.API_KEY";
    private static final String TAG = "OpenWeatherApi";

    private final String key;
    private OpenWeatherService service;
    private WeatherApiCallback callback;

    public OpenWeatherApi(OpenWeatherService service, Application application){
        this.service  = service;
        key = getKeyFromMetaData(application);
    }


    /**
     * get key from AndroidManifest metadata
     */
    private String getKeyFromMetaData(Application application) {
        try{
            ApplicationInfo appInfo = application.getPackageManager().getApplicationInfo(
                    application.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(KEY_OF_METADATA_OPENWEATHER_KEY);
            }else {
                return null;
            }
        }catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }


    public void getCurrentWeather(double lat, double lon) {
        Call<WeatherCurrentData> call = service.getCurrentWeather(
                StringsProvider.latOrLongToString(lat),
                StringsProvider.latOrLongToString(lon), key);
        Log.d(TAG, "getCurrentWeather: lat = " + StringsProvider.latOrLongToString(lat)
                + ", lon = " + StringsProvider.latOrLongToString(lon));
        call.enqueue(new CallbackCurrentWeather());
    }

    public void getCurrentWeather(int id) {
        Call<WeatherCurrentData> call = service.getCurrentWeather(id, key);
        call.enqueue(new CallbackCurrentWeather());
    }

    public void getFiveDaysWeather(double lat, double lon){
        Call<WeatherFiveDaysData> call = service.getWeatherForFiveDays(
                StringsProvider.latOrLongToString(lat),
                StringsProvider.latOrLongToString(lon), key);
        Log.d(TAG, "getFiveDaysWeather: lat = " + StringsProvider.latOrLongToString(lat)
                + ", lon = " + StringsProvider.latOrLongToString(lon));
        call.enqueue(new CallbackFiveDaysWeather());
    }

    public void getFiveDaysWeather(int id){
        Call<WeatherFiveDaysData> call = service.getWeatherForFiveDays(id, key);
        call.enqueue(new CallbackFiveDaysWeather());
    }


    private class CallbackCurrentWeather implements Callback<WeatherCurrentData> {
        @Override
        public void onResponse(Response<WeatherCurrentData> response, Retrofit retrofit) {
            if (!response.isSuccess()) {
                callback.onGetCurrentWeather(null);
            }else {
                WeatherCurrentData weatherCurrentData = response.body();
                callback.onGetCurrentWeather(weatherCurrentData);
            }
        }
        @Override
        public void onFailure(Throwable t) {
            callback.onGetCurrentWeatherFailure(t);
        }
    }

    private class CallbackFiveDaysWeather implements Callback<WeatherFiveDaysData>{
        @Override
        public void onResponse(Response<WeatherFiveDaysData> response, Retrofit retrofit) {
            if (!response.isSuccess()) {
                callback.onGetFiveDaysWeather(null);
            }else {
                WeatherFiveDaysData weatherFiveDaysData = response.body();
                callback.onGetFiveDaysWeather(weatherFiveDaysData);
            }
        }
        @Override
        public void onFailure(Throwable t) {
            callback.onGetFiveDaysWeatherFailure(t);
        }
    }

    public void setCallback(WeatherApiCallback callback) {
        this.callback = callback;
    }

}
