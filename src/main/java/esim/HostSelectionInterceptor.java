package esim;

import java.io.IOException;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

/** An interceptor that allows runtime changes to the URL hostname. */
public final class HostSelectionInterceptor implements Interceptor {
    private volatile String host;

    public void setHost(String server) {
        this.host = Objects.requireNonNull(HttpUrl.parse("http://" + server + ".e-sim.org/")).host();
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String host = this.host;
        if (host != null) {
            HttpUrl newUrl = request.url().newBuilder()
                    .host(host)
                    .build();
            request = request.newBuilder()
                    .url(newUrl)
                    .build();
        }
        return chain.proceed(request);
    }
}