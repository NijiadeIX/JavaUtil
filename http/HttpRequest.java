public class HttpRequest {
    private String url;
    private String method;

    private Map<String, String> headers;
    private byte[] requestBody;

    private int connectTimeoutMilis = 5000;
    private int readTimeoutMilis = 5000;

    private HttpRequest(String method, String url) {
        this.method = method;
        this.url = url;
    }

    /**
     * exptect response
     * @return
     * @throws IOException
     * @throws HttpStatusCodeException
     */
    public byte[] send() throws IOException, HttpStatusCodeException {
        URL uUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uUrl.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(requestBody != null && requestBody.length > 0);
        conn.setConnectTimeout(connectTimeoutMilis);
        conn.setReadTimeout(readTimeoutMilis);
        conn.setRequestMethod(method);
        if (headers != null) {
            headers.forEach(conn::addRequestProperty);
        }
        conn.connect();

        OutputStream out = null;
        InputStream in = null;
        try {
            if (requestBody != null) {
                out = conn.getOutputStream();
                out.write(requestBody);
                out.flush();
            }

            int statusCode = conn.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                int contentLength = conn.getContentLength();
                if (contentLength > 0) {
                    in = conn.getInputStream();
                    byte[] responseBody = new byte[contentLength];
                    in.read(responseBody);
                    return responseBody;
                } else {
                    return null;
                }
            } else {
                throw new HttpStatusCodeException(statusCode);
            }
        } finally {
            close(in);
            close(out);
            conn.disconnect();
        }
    }

    public HttpRequest header(String key, String value) {
        if (headers == null) headers = new HashMap<>();
        headers.put(key, value);
        return this;
    }

    public HttpRequest body(byte[] body) {
        requestBody = body;
        return this;
    }

    public HttpRequest connectTimeout(int milis) {
        connectTimeoutMilis = milis;
        return this;
    }

    public HttpRequest readTimeout(int milis) {
        readTimeoutMilis = milis;
        return this;
    }

    private void close(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

    public static HttpRequest doGet(String url) {
        return new HttpRequest("GET", url);
    }

    public static HttpRequest doPost(String url) {
        return new HttpRequest("POST", url);
    }

    public static HttpRequest doPut(String url) {
        return new HttpRequest("PUT", url);
    }

    public static HttpRequest doDelete(String url) {
        return new HttpRequest("DELETE", url);
    }
}
