package com.dji.DDS;

import android.util.Base64;

import com.dji.DDS.Exceptions.GetTokenException;
import com.dji.DDS.Exceptions.RequestException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import dji.thirdparty.okhttp3.OkHttpClient;
import dji.thirdparty.okhttp3.Request;
import dji.thirdparty.okhttp3.Response;

/**
 * Created by JORGE on 28/09/2017.
 */

public  class HttpConnection {
    static String token = "";
    static String tokenUrl = "";
    static String email = "";
    static String password = "";



    public static String request(String URL) throws IOException, JSONException, GetTokenException, RequestException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("Autorization","Bearer "+getToken(email,password))
                .build();


        Response response = client.newCall(request).execute();

        if (response.code() != 200) {
            throw new RequestException();
            // ERRO
        } else {
            return response.body().string();
            // Usar o body retornado pelo servidor...
        }
    }

    public static String getToken(String userEmail, String userPassword) throws IOException, JSONException, GetTokenException {

        if(!token.equals("")){
            return token;
        }

        email = userEmail;
        password = userPassword;

        String credentials = null;

        try{
            credentials = email+":"+password;
            byte[] data = credentials.getBytes("UTF-8");

            // FIXME: remover quebra de linha ou corrigir quebra de linha no final do base64
            credentials = Base64.encodeToString(data, Base64.DEFAULT);
        }catch(Exception a) {
            System.err.println(a);

        }
            OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(tokenUrl)
                .addHeader("Autorization","Basic "+credentials)
                .build();


        Response response = client.newCall(request).execute();

        if (response.code() != 200) {
            throw new GetTokenException();
            // ERRO
        } else {
            String body = response.body().string();

            JSONObject o = new JSONObject(body);
            token = o.optString("token");

            // Usar o body retornado pelo servidor...
            return token;
        }
    }

}
