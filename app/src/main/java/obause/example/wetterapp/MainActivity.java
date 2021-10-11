package obause.example.wetterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityEditText;
    TextView resultTextView;

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.i("Wetter:", s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("current");
                Log.i("Wetter:", weatherInfo);

                JSONObject currentJSONObject = new JSONObject(weatherInfo);
                String temperature = currentJSONObject.getString("temperature") + "°C";

                if (!temperature.equals("")) {
                    resultTextView.setText(temperature);
                    resultTextView.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(MainActivity.this, "Temperatur konnte nicht gefunden werden!", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                resultTextView.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Stadt konnte nicht gefunden werden!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getWeather(View view) {

        try {
            DownloadTask task = new DownloadTask();

            String cityName = URLEncoder.encode(cityEditText.getText().toString());
            task.execute("http://api.weatherstack.com/current?access_key=5de785fe92496534cfa2b75d2133482e&query=" + cityName);
            InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(cityEditText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        resultTextView = findViewById(R.id.resultTextView);
    }
}