package selling_electronic_devices.back_end.Dto;

public class LoginResponse {
    private String token;
    private String id;
    private String email;
    private String role;
    private String avatar;
    private Object user;
    private String chatBoxId;

    public LoginResponse(String token, String id, String email, String role, String avatar, Object user, String chatBoxId) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.role = role;
        this.avatar = avatar;
        this.user = user;
        this.chatBoxId = chatBoxId;
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

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public String getChatBoxId() {
        return chatBoxId;
    }

    public void setChatBoxId(String chatBoxId) {
        this.chatBoxId = chatBoxId;
    }
}
