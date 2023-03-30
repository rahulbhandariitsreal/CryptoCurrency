package com.example.new_cryptoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    ArrayList<CryptoCurrency> cryptoCurrencies;

    private NetworkInfo networkInfo;
    private ConnectivityManager connectivityManager;

    private TextView textView;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    private Adapter adapter;
    public static final String URL_LINK = "https://rest.coinapi.io/v1/exchangerate/USD?apikey=9656ECFB-5CC7-48F9-8C3F-6E02AF523CF9&invert=true&output_format=json";

    public static String JSON_STRING;

    private  URL url_link;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo=connectivityManager.getActiveNetworkInfo();
        progressBar = findViewById(R.id.loading_metre);
        textView = findViewById(R.id.nodataAvaliable);
        cryptoCurrencies = new ArrayList<>();
         url_link = getURL(URL_LINK);

        recyclerView = findViewById(R.id.recyclerview);
        adapter = new Adapter(cryptoCurrencies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        if(networkInfo != null && networkInfo.isConnected()){
            executoreservice();
        }else{
            progressBar.setVisibility(View.GONE);
            textView.setText("No internet connection");
        }






    }

    private void executoreservice(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    JSON_STRING = getJsonResponse(url_link);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject root = new JSONObject(JSON_STRING);
                            JSONArray rates = root.getJSONArray("rates");

                            cryptoCurrencies.clear();
                            for (int i = 0; i < rates.length(); i++) {
                                JSONObject crypto = rates.getJSONObject(i);
                                CryptoCurrency currency = new CryptoCurrency();
                                currency.setName_crupto(crypto.getString("asset_id_quote"));
                                currency.setRate(crypto.getDouble("rate"));
                                cryptoCurrencies.add(currency);

                            }

                            setrecyclerview();

  /*
    {
    "asset_id_base": "USD",
    "rates": [
        {
            "time": "2023-03-30T11:17:11.0000000Z",
            "asset_id_quote": "$PAC",
            "rate": 0.000052002869169063860677057
        },
        {
            "time": "2023-03-30T11:17:11.0000000Z",
            "asset_id_quote": "1000SHIB",
            "rate": 0.009000396076256713779260178
        },
        ]
        }

     */

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }

    private void setrecyclerview() {
        progressBar.setVisibility(View.GONE);
        if (cryptoCurrencies == null) {
            textView.setText("No data Avaliable");
        }else {
            adapter.addCryptocurrency(cryptoCurrencies);
        }

    }

    private String getJsonResponse(URL url_link) throws IOException {

        String jsonresponse = null;
        if (url_link == null) {
            return null;
        }
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url_link.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonresponse = readfomstream(inputStream);
            }else{
                textView.setText("No data Avaliable");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonresponse;
    }

    private String readfomstream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }

        }

        return output.toString();
    }

    private URL getURL(String uri) {
        URL url = null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }
}