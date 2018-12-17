package pers.acp.springcloud.gateway.vo;

/**
 * @author zhangbin by 26/04/2018 21:35
 * @since JDK 11
 */
public class ErrorVO {

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    private String error;

    private String errorDescription;

}
