package selling_electronic_devices.back_end.Dto;

public class LoginResponse {
    private String jwt;
    private long id;

    public LoginResponse(String jwt, long id) {
        this.jwt = jwt;
        this.id = id;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
