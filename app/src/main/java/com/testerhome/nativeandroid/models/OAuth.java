package com.testerhome.nativeandroid.models;

/**
 * Created by vclub on 15/10/7.
 */
public class OAuth {
    private String access_token;
    private String token_type;
    private long expires_in;
    private String refresh_token;
    private long create_at;
    private long expireDate;

    // TODO: 2016/11/20 [avatar_url] [id] [login] [name]

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public long getCraete_at() {
        return create_at;
    }

    public void setCraete_at(long craete_at) {
        this.create_at = craete_at;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }

    @Override
    public String toString() {
        return "OAuth{" +
                "access_token='" + access_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", expires_in=" + expires_in +
                ", refresh_token='" + refresh_token + '\'' +
                ", create_at=" + create_at +
                ", expireDate=" + expireDate +
                '}';
    }
}
