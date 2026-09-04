package nssoftwares.com.storemanagment.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import nssoftwares.com.storemanagment.Connection.ConnectionClass;
import nssoftwares.com.storemanagment.MainActivity;
import nssoftwares.com.storemanagment.R;

public class forgetactivity extends AppCompatActivity {
    private EditText edtEmail;
    private Button btnSend;
    private TextView status;
    private Connection con;
    private Statement stmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetactivity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        edtEmail = findViewById(R.id.edtEmail);
        btnSend = findViewById(R.id.btnSend);
        status = findViewById(R.id.status);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                if (!email.isEmpty()) {
                    new SendPasswordTask().execute(email);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter an email address", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle the back button click here
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Finish the current activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SendPasswordTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String email = strings[0];
            String message = "";
            try {
                con = connectionClass(ConnectionClass.un, ConnectionClass.pass, ConnectionClass.db, ConnectionClass.ip);
                if (con != null) {
                    String query = "SELECT password FROM users WHERE email = '" + email + "'";
                    stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs.next()) {
                        String password = rs.getString("password");
                        // Send email with the password
                        boolean emailSent = sendEmail(email, password);
                        if (emailSent) {
                            message = "Password sent to your email.";
                        } else {
                            message = "Failed to send email.";
                        }
                    } else {
                        message = "Email not found.";
                    }
                } else {
                    message = "Database connection error.";
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                message = "Error: " + e.getMessage();
            }
            return message;
        }

        @Override
        protected void onPostExecute(String result) {
            // Display the result in the status TextView
            if (status != null) {
                status.setText(result);
            }
            // Optionally, show a Toast message
            Toast.makeText(forgetactivity.this, result, Toast.LENGTH_LONG).show();
        }

        private boolean sendEmail(String email, String password) {
            final String username = "bc200200143@vu.edu.pk";  // Your email
            final String emailPassword = "bftd opdo yalv vjej";  // Use your generated app password
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, emailPassword);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject("GROCERY STORE");
                message.setText("Your password is: " + password);
                Transport.send(message);
                edtEmail.setText("");
                return true;
            } catch (MessagingException e) {
                Log.e("Email Sending Error: ", "Error in sending email: " + e.getMessage());
                return false;
            }
        }
    }

    @SuppressLint("NewApi")
    public Connection connectionClass(String user, String password, String database, String server){
        Connection connection = null;
        String connectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + server + "/" + database + ";user=" + user + ";password=" + password + ";";
            connection = DriverManager.getConnection(connectionURL);
        } catch (Exception e) {
            Log.e("SQL Connection Error : ", e.getMessage());
        }

        return connection;
    }
}
