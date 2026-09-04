package nssoftwares.com.storemanagment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import nssoftwares.com.storemanagment.Connection.ConnectionClass;
import nssoftwares.com.storemanagment.ui.forgetactivity;

public class MainActivity extends AppCompatActivity {
    EditText email, password;
    Button loginbtn;
    TextView status;
    Connection con;
    Statement stmt;
    String username,userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPassword);
        loginbtn = findViewById(R.id.btnLogin);
        status = findViewById(R.id.status);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // Set up Enter key action for both email and password fields
        email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    password.requestFocus();
                    return true;
                }
                return false;
            }
        });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                    return true;
                }
                return false;
            }
        });
    }

    public void loginnow(View view) {
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(MainActivity.this, forgetactivity.class);
        startActivity(intent);
    }

    public void btnRegistered(View view) {
        Intent intent = new Intent(MainActivity.this, activity_register.class);
        startActivity(intent);
    }

    private void login() {
        new loginUser().execute("");
    }

    public class loginUser extends AsyncTask<String, String, String> {

        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            status.setText("Logging in...");
        }


        @Override
        protected void onPostExecute(String s) {
            if (isSuccess) {
                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                // Start HomeActivity and pass username and email
                finish(); // Optional: close the current activity
            } else {
                Toast.makeText(MainActivity.this, "Login Failed: " + z, Toast.LENGTH_SHORT).show();
            }
        }



        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.d("LoginActivity", "Attempting to connect to database...");
                con = connectionClass(ConnectionClass.un, ConnectionClass.pass, ConnectionClass.db, ConnectionClass.ip);
                if (con == null) {
                    z = "Check Your Internet Connection";
                    Log.e("LoginActivity", "Connection is null");
                } else {
                    String query = "SELECT * FROM users WHERE email = '" + email.getText().toString() + "' AND password = '" + password.getText().toString() + "'";
                    stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs.next()) {
                        isSuccess = true;
                        z = "Login successful";
                        Intent intent = new Intent(MainActivity.this, homeactivity.class);
                        intent.putExtra("Email", email.getText().toString());
                        startActivity(intent);
                        Log.d("LoginActivity", "Login successful");
                    } else {
                        z = "Invalid Credentials!";
                        Log.d("LoginActivity", "Invalid Credentials");
                        isSuccess = false;
                    }
                }
            } catch (Exception e) {
                isSuccess = false;
                z = e.getMessage();
                Log.e("LoginActivity", "Error: " + z, e);
            }

            return z;
        }
        @SuppressLint("NewApi")
        public Connection connectionClass(String user, String password, String database, String server) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Connection connection = null;
            String connectionURL;
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connectionURL = "jdbc:jtds:sqlserver://" + server + "/" + database + ";user=" + user + ";password=" + password + ";";
                connection = DriverManager.getConnection(connectionURL);
            } catch (Exception e) {
                Log.e("SQL Connection Error: ", e.getMessage());
            }
            return connection;


        }
    }
}
