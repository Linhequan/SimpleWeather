package com.example.simpleweather;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private final String KEY = "666c31798b52428da4e3d57d1c7184c1";
    private TextView tv_werther_conten;
    private EditText et_cityname;
    private Button btn_select_weather;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Werther werther= (Werther) msg.obj;
            switch(msg.what){
                case 1:
                    tv_werther_conten.setText("时间：" + String.valueOf(werther.getHeWeather6().get(0).getUpdate().getLoc()) +
                            "、地点：" + werther.getHeWeather6().get(0).getBasic().getLocation() +
                            "、天气：" + werther.getHeWeather6().get(0).getNow().getCond_txt() +
                            "、风向：" + werther.getHeWeather6().get(0).getNow().getWind_dir() +
                            "、气温：" + werther.getHeWeather6().get(0).getNow().getTmp());
                    break;
            }
            Log.d(TAG, "onResponse: " + String.valueOf(werther.getHeWeather6().get(0).getUpdate().getLoc()));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initDate();
        btn_select_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityname = String.valueOf(et_cityname.getText());
                if (cityname.equals("")) {
                    Toast.makeText(MainActivity.this, "城市名不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    String url = "https://free-api.heweather.com/s6/weather/now?key=666c31798b52428da4e3d57d1c7184c1&location=" + cityname;
                    new MyAsyncTask().execute(url);

                }
            }
        });
    }

    private void initDate() {

    }

    private void initView() {
        tv_werther_conten = (TextView) this.findViewById(R.id.tv_werther_conten);
        et_cityname = (EditText) this.findViewById(R.id.et_cityname);
        btn_select_weather = (Button) this.findViewById(R.id.btn_select_weather);
    }

    private void getWerther(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Log.d(TAG, "url: " + url);
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.d(TAG, "onFailure: ");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d(TAG, "onResponse: " + responseData);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                Werther werther = gson.fromJson(responseData, Werther.class);
                Message message=new Message();
                message.what=1;
                message.obj=werther;
                handler.sendMessage(message);
            }
        });

    }

    class MyAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String url = strings[0];
            getWerther(url);
            return null;
        }
    }
}

