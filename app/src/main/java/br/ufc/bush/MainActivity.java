package br.ufc.bush;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import br.ufc.utils.ActivityRecognitionIntentService;

public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String HOST = "http://www.sefiro.com.br/";
    private static final String FILE = "bush/location.php";
    private static final String TAG = "BuSH";

    private Context mContext;
    private GoogleApiClient mGApiClient;
    private BroadcastReceiver receiver;
    private TextView tvName;

    String  id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvName = (TextView) findViewById(R.id.tvName);

        //Set the context
        mContext = this;

        //Check Google Play Service Available
        if(isPlayServiceAvailable()) {
            mGApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addApi(ActivityRecognition.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            //Connect to gPlay
            mGApiClient.connect();
        }else{
            Log.d(TAG, "Google Play Service not Available");
        }

        //Broadcast receiver
        receiver  = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String v =  "Activity :" +
                        intent.getStringExtra("act") + " " +
                        "Confidence : " + intent.getExtras().getInt("confidence") + "n";

                v += tvName.getText();
                tvName.setText(v);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("SAVVY");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new RegisterTask().execute(HOST+FILE, "anderson");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGApiClient.disconnect();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Intent i = new Intent(this, ActivityRecognitionIntentService.class);
        PendingIntent mActivityRecongPendingIntent = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d(TAG, "onConnected: " + mActivityRecongPendingIntent.toString() );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGApiClient, 0, mActivityRecongPendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    private boolean isPlayServiceAvailable() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS;
    }

    // Classe para conexao Android_PHP
    private class RegisterTask extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute() {
            dialog.setMessage("Loading ...");
            dialog.show();
        }

        protected Boolean doInBackground(String... params) {
            String result = null;
            InputStream is = null;
            StringBuilder sb = null;

            HttpClient httpclient = new DefaultHttpClient();
            // Cria um HttpPost com a URL para realização do Post
            HttpPost httppost = new HttpPost(params[0]);

            try {
                // Adiciona os parâmetros
                List nameValuePairs = new ArrayList();
                nameValuePairs.add(new BasicNameValuePair("latitude", params[1]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Executa
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            } catch(Exception e) {
                e.printStackTrace();
            }

            //convert response to string
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
                sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");

                String line="0";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result=sb.toString();
            } catch(Exception e) {
                e.printStackTrace();
                return false;
            }

            //paring data
            try {
                JSONArray jArray = new JSONArray(result);
                JSONObject json_data = null;

                for(int i = 0; i < jArray.length(); i++) {
                    json_data = jArray.getJSONObject(i);
                    id = json_data.getString("id");
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        protected void onPostExecute(Boolean result) {
            if(dialog.isShowing()) {
                dialog.dismiss();
            }
            if(result.booleanValue()) {
                Log.d(TAG, "Id do usuario: " + id);
            }
        }
    }
}
