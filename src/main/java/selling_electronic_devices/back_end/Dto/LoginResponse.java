package selling_electronic_devices.back_end.Dto;

public class LoginResponse {
    private String token;
    private String id;
    private String email;
    private String role;
    private String avatar;

    public LoginResponse(String token, String id, String email, String role, String avatar) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.role = role;
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
