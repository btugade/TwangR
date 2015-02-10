package net.gavinpower.twangr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.gavinpower.Models.User;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import static net.gavinpower.twangr.TwangR.HubConnection;
import static net.gavinpower.twangr.TwangR.currentUser;
import static net.gavinpower.Security.AESEncrypt.generateKeyFromPassword;
import static net.gavinpower.Security.AESEncrypt.generateSalt;
import static net.gavinpower.Security.AESEncrypt.encrypt;
import static net.gavinpower.Security.AESEncrypt.generateKey;
import static net.gavinpower.Security.AESEncrypt.saltString;
import static net.gavinpower.Security.AESEncrypt.PASSWORD;
import static net.gavinpower.Security.AESEncrypt.PASSWORD_BASED_KEY;
import static net.gavinpower.Security.AESEncrypt.key;


public class LoginActivity extends Activity {

    private TwangR TwangR;

    private EditText Username;
    private EditText Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Username = (EditText) findViewById(R.id.UserNameEdit);
        Password = (EditText) findViewById(R.id.passwordEdit);

        TwangR = (TwangR) getApplication();
        TwangR.setActivity(this);

        if(currentUser == null) {

            try {
                if (PASSWORD_BASED_KEY) {
                    String salt = saltString(generateSalt());
                    key = generateKeyFromPassword(PASSWORD, salt);
                } else {
                    key = generateKey();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else
        {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        TwangR.setActivity(this);
    }


    public void Login(View loginButton)
    {
        String username = Username.getText().toString();
        String password = Password.getText().toString();
        try
        {
            if(!username.equals("GavinAdmin")) { // GavinAdmin is a seeded account to test login before the implementation of registration
                password = encrypt(password, key).toString();
            }

            HubConnection.login(username, password);
        }
        catch(UnsupportedEncodingException | GeneralSecurityException ex )
        {
            ex.printStackTrace();
        }
    }

    public void register(View registerButton)
    {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void loginSuccess(User user)
    {
        TwangR.setCurrentUser(user);
        startActivity(new Intent(this, MainActivity.class));
    }

    public void loginFailure(String status)
    {
        switch(status)
        {
            case "PasswordIncorrect": status = "Your password is incorrect please try again"; break;
            case "UserNotFound": status = "Incorrect username or password please try again"; break;
        }
        Toast.makeText(this, status, Toast.LENGTH_LONG).show();

    }


}
