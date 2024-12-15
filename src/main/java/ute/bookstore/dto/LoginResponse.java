package ute.bookstore.dto;

public class LoginResponse {
    private String token;
    private String redirectUrl;

    // Constructor
    public LoginResponse(String token, String redirectUrl) {
        this.token = token;
        this.redirectUrl = redirectUrl;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
 // Optional: Override toString() for easy logging or testing
    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                '}';
    }
}


