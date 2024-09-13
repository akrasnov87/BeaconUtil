package com.mobwal.walker.beautil.v1.model;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Класс расширения мета информации для авторизации
 */
public class AuthorizationMeta extends Meta {
    /**
     * токен авторизации
     */
    private final String mToken;
    /**
     * список ролей
     */
    private final String mClaims;
    /**
     * идентификатор пользователя
     */
    private final Long mUserId;

    private final String mLogin;

    private String mResponse;

    public AuthorizationMeta(int status, String message, String token, String claims, Long userId, String login, String response) {
        super(status, message);

        mToken = token;
        mClaims = claims;
        mUserId = userId;
        mLogin = login;
        mResponse = response;
    }

    public AuthorizationMeta(int status, String message, String token, String claims, Long userId, String login) {
        super(status, message);

        mToken = token;
        mClaims = claims;
        mUserId = userId;
        mLogin = login;
    }


    public AuthorizationMeta(int status, String message) {
        this(status, message, null, null, null, null, null);
    }

    public String getToken() {
        return mToken;
    }

    public String getClaims() {
        return mClaims;
    }

    public Long getUserId() {
        return mUserId;
    }

    public String getLogin() {
        return mLogin;
    }

    public String getResponse() { return  mResponse; }
}
