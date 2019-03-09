package pers.acp.admin.oauth.token;

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
public class LoginLog {

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    private String appid;

    private String date;

    private String userid;

}
