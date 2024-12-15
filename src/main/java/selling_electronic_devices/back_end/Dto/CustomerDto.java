package selling_electronic_devices.back_end.Dto;

import org.springframework.web.multipart.MultipartFile;

public class CustomerDto {
    private String email;
    private String password;
    private String address;
    private String fullName;
    private String phone;
    private String role;
    private MultipartFile avatar;

    public CustomerDto(String email, String password, String address, String fullName, String phone, String role, MultipartFile avatar) {
        this.email = email;
        this.password = password;
        this.address = address;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public MultipartFile getAvatar() {
        return avatar;
    }

    public void setAvatar(MultipartFile avatar) {
        this.avatar = avatar;
    }
}
