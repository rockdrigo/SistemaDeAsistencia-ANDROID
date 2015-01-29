package mx.com.hacklab.rodrigo.controldeasistencia;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    Button btn_acreditar;
    TextView DxUsuario, MsjAcreditado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.btn_acreditar = (Button)findViewById(R.id.Btn_Acreditar);
        this.DxUsuario = (TextView)findViewById(R.id.DxUsuario);
        this.MsjAcreditado = (TextView)findViewById(R.id.MsjAcreditado);

        btn_acreditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        final IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if(scanResult.getContents() != null){
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            String Email = scanResult.getContents().toString();
            params.add(new BasicNameValuePair("Email",Email));
            String url="http://controlasistencia-hacklabmx.rhcloud.com/AppScript/Acreditar.php";
            Toast.makeText(getApplicationContext(), Email, Toast.LENGTH_LONG).show();
            consultaBdx(params, url);

        }else {
            Toast.makeText(getApplicationContext(), "No se logro escanear el Código, por favor intenta de nuevo", Toast.LENGTH_LONG).show();
        }


    }

    private void consultaBdx(List<NameValuePair> params, String url) {
        try{
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(params));
            try{
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse resp;
                resp = httpClient.execute(httppost);

                JSONObject j = new JSONObject(EntityUtils.toString(resp.getEntity()));
                String r = j.getString("Rpta");
                if (r.equals("1")){
                    this.DxUsuario.setText(j.getString("Nombre"));
                    this.MsjAcreditado.setTextColor(Color.parseColor("#00AF00"));
                    this.MsjAcreditado.setText("Usuario Acreditado Correctamente");
                }else {
                    this.DxUsuario.setText("");
                    this.MsjAcreditado.setTextColor(Color.parseColor("#FF0000"));
                    this.MsjAcreditado.setText("Usuario NO Registrado para el Evento");
                }
            } catch (ClientProtocolException e) {
                Toast.makeText(getApplicationContext(), "error HttpResponse resp =  httpclient.execute(httppost)", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "No ha sido posible la conexion al servidor, verifique la configuracion", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), " error httppost.setEntity(new UrlEncodedFormEntity(params))", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


}
