package kmo2008.pl.kolos;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    String lastPass = null;

    List<Record> passwordList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MyDatabase databaseCon = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "kolosdata").allowMainThreadQueries().build();



        final Button generateButton = findViewById(R.id.generateButton);
        final Button saveButton = findViewById(R.id.saveButon);
        final TextView passList = findViewById(R.id.listOfPassword);
        final EditText titlePass = findViewById(R.id.titleBox);

        passwordList = databaseCon.rercordDAO().findAll();
        Iterator<Record> iterator = passwordList.iterator();
        String passwords= "";
        while(iterator.hasNext()){
            passwords += iterator.next().toString() +"\n";
        }
        passList.setText(passwords);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WebServiceHandler()
                        .execute("https://makemeapassword.org/api/v1/alphanumeric/json?c=1&l=15");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseCon.rercordDAO().addRec(new Record(titlePass.getText().toString(), lastPass));
                reloadView();
            }

            public void reloadView(){
                passwordList = databaseCon.rercordDAO().findAll();
                Iterator<Record> iterator = passwordList.iterator();
                String passwords= "";
                while(iterator.hasNext()){
                    passwords += iterator.next().toString();
                }
                passList.setText(passwords);
            }
        });
    }

    private class WebServiceHandler extends AsyncTask<String, Void, String> {

        // okienko dialogowe, które każe użytkownikowi czekać
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        // metoda wykonywana jest zaraz przed główną operacją (doInBackground())
        // mamy w niej dostęp do elementów UI
        @Override
        protected void onPreExecute() {
            // wyświetlamy okienko dialogowe każące czekać
            dialog.setMessage("Czekaj...");
            dialog.show();
        }

        // główna operacja, która wykona się w osobnym wątku
        // nie ma w niej dostępu do elementów UI
        @Override
        protected String doInBackground(String... urls) {

            try {
                // zakładamy, że jest tylko jeden URL
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();

                // pobranie danych do InputStream
                InputStream in = new BufferedInputStream(
                        connection.getInputStream());

                // konwersja InputStream na String
                // wynik będzie przekazany do metody onPostExecute()
                return streamToString(in);

            } catch (Exception e) {
                // obsłuż wyjątek
                Log.d(MainActivity.class.getSimpleName(), e.toString());
                return null;
            }

        }

        // metoda wykonuje się po zakończeniu metody głównej,
        // której wynik będzie przekazany;
        // w tej metodzie mamy dostęp do UI
        @Override
        protected void onPostExecute(String result) {

            // chowamy okno dialogowe
            dialog.dismiss();

            try {
                // reprezentacja obiektu JSON w Javie
                JSONObject json = new JSONObject(result);

                // pobranie pól obiektu JSON i wyświetlenie ich na ekranie
                lastPass = json.getJSONArray("pws").getString(0);
                ((TextView) findViewById(R.id.generatedPassword)).setText(lastPass);

            } catch (Exception e) {
                // obsłuż wyjątek
                Log.d(MainActivity.class.getSimpleName(), e.toString());
            }
        }
    }

    // konwersja z InputStream do String
    public static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        try {

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            reader.close();

        } catch (IOException e) {
            // obsłuż wyjątek
            Log.d(MainActivity.class.getSimpleName(), e.toString());
        }

        return stringBuilder.toString();
    }

}

