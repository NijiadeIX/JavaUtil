public class HttpStatusCodeException extends Exception {
    private int statusCode;

    public HttpStatusCodeException(int statusCode) {
        super("statusCode:" + statusCode);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
