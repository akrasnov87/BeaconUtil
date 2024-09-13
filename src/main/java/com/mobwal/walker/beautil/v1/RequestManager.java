package com.mobwal.walker.beautil.v1;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobwal.walker.beautil.v1.model.AuthorizationMeta;
import com.mobwal.walker.beautil.v1.model.Meta;
import com.mobwal.walker.beautil.v1.model.RPCItem;
import com.mobwal.walker.beautil.v1.model.RPCResult;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class RequestManager {
    /**
     * заголовок для авторизации по умолчанию
     */
    public final static String AUTHORIZATION_HEADER = "rpc-authorization";

    /**
     * время на проверку подключения к серверу в милисекундах
     */
    public static int SERVER_CONNECTION_TIMEOUT = 3000;

    /**
     * авторизация
     *
     * @param context контекст
     * @param login логин
     * @param password пароль
     *
     * @return результат авторизации
     */
    public AuthorizationMeta authorization(@NonNull Context context, @NonNull String baseUrl, @NonNull String login, @NonNull String password, @NonNull String version) {
        try {
            String urlParams = "UserName=" + encodeValue(login) + "&Password=" + encodeValue(password) + "&Version=" + version;
            byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            URL url = new URL(baseUrl + "/auth");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postDataLength));
                urlConnection.setDoOutput(true);
                urlConnection.setInstanceFollowRedirects(false);
                urlConnection.setUseCaches(false);
                urlConnection.getOutputStream().write(postData);
                final InputStream stream;
                if (urlConnection.getResponseCode() == Meta.OK) {
                    stream = urlConnection.getInputStream();
                } else {
                    stream = urlConnection.getErrorStream();
                }

                InputStream in = new BufferedInputStream(stream);
                Scanner s = new Scanner(in).useDelimiter("\\A");
                String responseText = s.hasNext() ? s.next() : "";
                try {
                    return convertResponseToMeta(context, responseText, urlConnection.getResponseCode());
                } catch (Exception formatExc) {
                    return new AuthorizationMeta(Meta.ERROR_SERVER, "Ошибка в преобразовании ответа на авторизацию.");
                }
            } catch (Exception innerErr) {
                return new AuthorizationMeta(Meta.ERROR_SERVER, "Ошибка создания запроса на авторизацию.");
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            return new AuthorizationMeta(Meta.ERROR_SERVER, "Общая ошибка авторизации.");
        }
    }

    /**
     * Преобразование статуса ответа в мета-информацию
     *
     * @param response ответ от сервера в формате JSON
     * @return мета информация
     */
    public AuthorizationMeta convertResponseToMeta(@NonNull Context context, @NonNull String response, int code) {
        int status;
        String token = null;
        Long userId = null;
        String claims = null;
        String message;
        String login = "";

        try {
            JSONObject jsonObject = new JSONObject(response);
            if(code == Meta.OK) {
                status = code;
                message = "SUCCESS";
                token = jsonObject.getString("token");
                userId = jsonObject.getJSONObject("user").getLong("id");
                claims = jsonObject.getJSONObject("user").getString("claims");
                login = jsonObject.getJSONObject("user").getString("login");
            } else {
                status = Meta.NOT_AUTHORIZATION;
                message = jsonObject.getJSONObject("meta").getString("msg");
            }
        } catch (Exception e) {
            status = Meta.ERROR_SERVER;
            message = "FAILED";
        }
        return new AuthorizationMeta(status, message, token, claims, userId, login, response);
    }

    /**
     * Экранирование данных в запросе
     * @param value значение
     * @return результат
     */
    private String encodeValue(final @NonNull String value) {
        try {
            return URLEncoder.encode(value, String.valueOf(StandardCharsets.UTF_8));
        } catch (UnsupportedEncodingException | IllegalCharsetNameException e) {
            return value;
        }
    }

    /**
     * Выполнение RPC запроса
     * @param baseUrl настройки соединения
     * @param token токен-авторизация
     * @param postData входные данные
     * @return возвращается строка если возникла ошибка, либо объект RPCResult[]
     * @throws IOException общая ошибка
     */
    @Nullable
    public static RPCResult[] rpc(@NonNull String baseUrl, @NonNull String token, @NonNull byte[] postData) throws IOException {
        URL url = new URL(baseUrl + "/rpc");
        RPCResult[] rpcResults = null;
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            urlConnection.setRequestProperty("Accept","application/json");

            if(!StringUtil.isEmptyOrNull(token))
                urlConnection.setRequestProperty(AUTHORIZATION_HEADER, token);

            urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
            urlConnection.setDoOutput(true);
            urlConnection.setInstanceFollowRedirects( false );
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(SERVER_CONNECTION_TIMEOUT);

            urlConnection.getOutputStream().write(postData);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String serverResult = s.hasNext() ? s.next() : "";

            try {
                rpcResults = RPCResult.createInstanceByGson(serverResult);
            } catch (Exception formatExc) {
                Log.e("", formatExc.toString());
            }
        }catch (Exception innerErr) {
            Log.e("", "Ошибка создания запроса RPC.", innerErr);
        }finally {
            urlConnection.disconnect();
        }

        return rpcResults;
    }
}
