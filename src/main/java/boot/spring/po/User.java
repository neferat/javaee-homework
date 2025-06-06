package boot.spring.po;

import java.io.Serializable;
import java.util.Date;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "用户ID", width = 10)
    private Long userId;

    @Excel(name = "用户名", width = 20)
    private String username;

    @Excel(name = "密码", width = 20)
    private String password;

    @Excel(name = "邮箱", width = 30)
    private String email;

    @Excel(name = "手机号码", width = 20)
    private String phoneNumber;

    @Excel(name = "用户头像", width = 30)
    private String avatarUrl;

    @Excel(name = "用户简介", width = 50)
    private String bio;

    @Excel(name = "注册时间", width = 20)
    private Date registrationDate;

    @Excel(name = "积分", width = 10)
    private Integer points;

    @Excel(name = "用户等级", width = 10)
    private Integer userLevel;

    @Excel(name = "用户状态", width = 20)
    private String status;

    @Excel(name = "第三方登录平台", width = 20)
    private String thirdPartyPlatform;

    @Excel(name = "第三方登录ID", width = 30)
    private String thirdPartyId;

    public User() {
        super();
    }

    public User(String username, String password, String email, String phoneNumber, String avatarUrl, String bio,
                Date registrationDate, Integer points, Integer userLevel, String status, String thirdPartyPlatform,
                String thirdPartyId) {
        super();
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.registrationDate = registrationDate;
        this.points = points;
        this.userLevel = userLevel;
        this.status = status;
        this.thirdPartyPlatform = thirdPartyPlatform;
        this.thirdPartyId = thirdPartyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThirdPartyPlatform() {
        return thirdPartyPlatform;
    }

    public void setThirdPartyPlatform(String thirdPartyPlatform) {
        this.thirdPartyPlatform = thirdPartyPlatform;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", username=" + username + ", password=" + password + ", email=" + email
                + ", phoneNumber=" + phoneNumber + ", avatarUrl=" + avatarUrl + ", bio=" + bio + ", registrationDate="
                + registrationDate + ", points=" + points + ", userLevel=" + userLevel + ", status=" + status
                + ", thirdPartyPlatform=" + thirdPartyPlatform + ", thirdPartyId=" + thirdPartyId + "]";
    }
}