package order.model.request;

public class cancelReq {
    // we're going to define some attributes which required in order to perform cancel action

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;

}
