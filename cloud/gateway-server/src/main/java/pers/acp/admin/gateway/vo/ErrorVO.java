package pers.acp.admin.gateway.vo;

/**
 * @author zhang by 27/12/2018 13:07
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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private int code;

    private String error;

    private String errorDescription;

}
