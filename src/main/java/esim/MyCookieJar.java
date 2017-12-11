package esim;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCookieJar implements CookieJar {
    private final HashMap<String, HashMap<String, Cookie>> sessionStorage = new HashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        HashMap<String, Cookie> temp = sessionStorage.containsKey(url.host()) ? sessionStorage.get(url.host()) : new HashMap<>();
        for (Cookie cookie : cookies) {
            if(temp.containsKey(cookie.name())) {
                temp.replace(cookie.name(), cookie);
            } else {
                temp.put(cookie.name(), cookie);
            }
        }
        sessionStorage.putIfAbsent(url.host(), temp);
        //System.out.println(sessionStorage.get(url.host()));
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = new ArrayList<>();
        if(sessionStorage.containsKey(url.host())) {
            HashMap<String, Cookie> temp = sessionStorage.get(url.host());
            for(Map.Entry<String, Cookie> cookie : temp.entrySet()) {
                cookies.add(cookie.getValue());
            }
        }
        //System.out.println(cookies);
        return cookies;
    }
}
