package esim;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ESimClient {
    private IESim esim;

    public ESimClient() {
        CookieJar cookieJar = new MyCookieJar();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://secura.e-sim.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        esim = retrofit.create(IESim.class);
    }

    public String processCommands(String text, String chanNick) throws IOException {
        Pattern p = Pattern.compile("(^\\.[a-z]{1,10})");
        Matcher m = p.matcher(text);
        m.find();
        String cmd = m.group();
        text = text.replaceFirst(cmd, "");
        String msg = "Nieznana komenda.";
        String nick = chanNick;
        switch(cmd) {
            case ".help":
                msg = "Dostępne komendy: .licz, .link, .dmg, .today, .eq, .spec => Uwagi do DR4KA (e-sim).";
                break;
            case ".licz":
                double amount = 30;
                int weaponQuality = 1;

                if(text.isEmpty()) {
                    m = Pattern.compile(" \\d+[\\.,]?\\d*(?![^ ])").matcher(text);
                    if(m.find()) {
                        text = text.replaceFirst(m.group(), "");
                        amount = Double.parseDouble(m.group());
                    }

                    m = Pattern.compile("(?i) -q[0-5](?![^ ])").matcher(text);
                    if(m.find()) {
                        text = text.replaceFirst(m.group(), "");
                        weaponQuality = Integer.parseInt(m.group());
                    }

                    m = Pattern.compile("[\\w- ]{3,}").matcher(text);
                    if(m.find()) {
                        nick = m.group();
                    }
                }
                msg = this.getCitizenInfo(nick).printLicz(amount,weaponQuality);
                break;
            case ".link":
                m = Pattern.compile("[\\w- ]{3,}").matcher(text);
                if(m.find()) {
                    nick = m.group();
                }
                System.out.println(nick);
                msg = this.getCitizenInfo(nick).printLink("secura");
                break;
            default:
                break;
        }
        return msg;
    }

    public void signIn() throws IOException {
        Call<ResponseBody> call = esim.index();
        call.execute();
        RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "login="+ "***REMOVED***" +"&password="+ "***REMOVED***" +"&remember=true&facebookAdId=");
        call = esim.login(body);
        call.execute();
        //System.out.println(call.execute().raw().request());
    }

    public String getBattleSpects(int id) throws IOException {
        Call<ResponseBody> call = esim.battle(id);
        String html = call.execute().body().string();
        return ESimClient.getSpects(html);
    }

    public String getGlobalMilitaryEvents() throws IOException {
        Call<ResponseBody> call = esim.events();
        String html = call.execute().body().string();
        return ESimClient.extractInfo(Jsoup.parse(html));
    }

    public ESimCitizen getCitizenInfo(String nick) throws IOException {
        Call<ESimCitizen> call = esim.citizenInfo(nick.replaceAll(" ", "-").toLowerCase());
        ESimCitizen citizen = call.execute().body();
        //TimeUnit.MILLISECONDS.sleep(200);
        return citizen;
    }

    static String getSpects(String html) {
        Document document = Jsoup.parse(html);
        Element div = document.getElementById("spectatorsMenu");
        if(div == null) {
            return "Nie rozpoznano bitwy, lub brak aktywnej opcji premium.";
        }
        String spects = div.text().replaceAll("<.*?>", "").trim().replaceAll("\\s+(?=\\d+)", ", ").replaceAll("\\s{2,}", " ");
        String fightName = document.getElementById("fightName").getElementsByTag("span").first().getElementsByTag("a").first().text();
        if(fightName.isEmpty()) fightName = document.getElementById("fightName").getElementsByTag("span").first().getElementsByTag("a").get(1).text();
        String msg = fightName + " spects: " + spects;
        return msg;
    }

    static String extractInfo(Document document) {
        //el.innerHTML = txt;
        Element table = document.getElementsByClass("dataTable paddedTable").first();

//        if(tables.) {
//            System.out.println("*** Nie pobrała się tabela w newsach.");
//            return "";
//        }
        Element data = table.getElementsByTag("tr").get(1);
        Element div = data.getElementsByTag("div").get(1);
        String[] span = div.getElementsByTag("span").first().attr("title").split("<br/>");
        String[] date = span[0].replaceAll("[:-]", " ").split(" ");

        //String d = new Date(date[2], date[1]-1, date[0], date[3], date[4], date[5]).getTime();


        Element a = div.getElementsByTag("a").first();
        String msg = a.text();
        if(msg.startsWith(" People of")) {
            Pattern p = Pattern.compile("flags-small ([A-Za-z]+?)(?=\">)");
            Matcher m = p.matcher(msg);
            m.find();
            String country = m.group(1);
            msg = msg.replace("<.*?>", country);
        } else if (msg.matches("was attacked")) {
            Pattern p = Pattern.compile("flags-small ([A-Za-z]+?)(?=\">)");
            Matcher m = p.matcher(msg);
            m.find();
            String country = m.group(1);
            msg = msg.replace("<.*?>", " ("+country+")");
        }
        msg = "New event: " + msg.replaceAll("<.*?>", "").replaceAll(" {2,}"," ").trim() + " " + span[1];
        return msg;
    }
}
