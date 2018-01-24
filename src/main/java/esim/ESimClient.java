package esim;

import net.dv8tion.jda.core.entities.MessageChannel;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static my.discord.bot.jda.Jdabot.APP_SETTINGS;

public class ESimClient {
    private static final List<String> SERVERS = Arrays.asList("primera", "secura", "suna", "suburbia");
    private IESim esim;
    private HostSelectionInterceptor interceptor;

    public ESimClient() throws IOException {
        interceptor = new HostSelectionInterceptor();
        CookieJar cookieJar = new MyCookieJar();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://secura.e-sim.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        esim = retrofit.create(IESim.class);
        this.signIn();
    }

    public void processCommands(String text, String chanNick, MessageChannel channel) throws IOException {
        String cmd;
        Matcher m = Pattern.compile("(^\\.[a-z]{1,10})").matcher(text);
        if (m.find()) {
            text = text.replaceFirst(m.group(), "");
            cmd = m.group();
        } else {
            channel.sendMessage("Coś poszło nie tak.").queue();
            return;
        }

        String msg;
        switch(cmd) {
            case ".help":
                msg = "`Dostępne komendy:**\n.licz\t  .link\n.dmg\t.today\n.eq\t   .spec\n** *Uwagi do DR4KA (e-sim)*`";
                break;
            case ".format":
                msg = "Dostępne komendy:**\n.licz\t  .link\n.dmg\t.today\n.eq\t   .spec\n** *Uwagi do DR4KA (e-sim)*";
                break;
            case ".licz":
            case ".link":
            case ".dmg":
            case ".today":
            case ".eq":
                this.getCitizenInfo(cmd, text, chanNick, channel);
                return;
            case ".spec":
                this.getBattleSpects(text, channel);
                return;
            case ".news":
                this.getGlobalMilitaryEvents(channel);
                return;
            default:
                msg = "Nieobsługiwana komenda. Aby sprawdzić dostępne komendy wpisz **.help**";
        }
        channel.sendMessage(msg).queue();
    }

    private void signIn() throws IOException {
        Call<ResponseBody> call = esim.index();
        call.execute();
        RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "login="+ APP_SETTINGS.securaLogin +"&password="+ APP_SETTINGS.securaPassword +"&remember=true&facebookAdId=");
        call = esim.login(body);
        call.execute();
        //System.out.println(call.execute().raw().request());
    }

    private void getBattleSpects(String text, MessageChannel channel) {
        int battleId = 0;
        Matcher m = Pattern.compile("\\d{3,}").matcher(text);
        if (m.find()) {
            battleId = Integer.parseInt(m.group());
        }
        if (battleId == 0) {
            channel.sendMessage("Podano błedne ID bitwy.").queue();
            return;
        }
        interceptor.setHost("secura");
        Call<ResponseBody> call = esim.battle(battleId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    channel.sendMessage(ESimClient.extractSpects(Jsoup.parse(response.body().string()))).queue();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                channel.sendMessage("Wystąpił błąd serwera.").queue();
            }
        });
    }

    private void getGlobalMilitaryEvents(MessageChannel channel) {
        interceptor.setHost("secura");
        Call<ResponseBody> call = esim.events();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    channel.sendMessage(ESimClient.extractNews(Jsoup.parse(response.body().string()))).queue();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                channel.sendMessage("Wystąpił błąd serwera.").queue();
            }
        });
    }

    private void getCitizenInfo(String cmd, String text, String chanNick, MessageChannel channel) throws IOException {
        Matcher m;
        String nick = chanNick;
        String server = "secura";
        double amount = 30;
        int weaponQuality = 1;

        switch (cmd) {
            case ".licz":
                if (!text.isEmpty()) {
                    m = Pattern.compile(" \\d+[.,]?\\d*(?![^ ])").matcher(text);
                    if (m.find()) {
                        text = text.replaceFirst(m.group(), "");
                        amount = Double.parseDouble(m.group());
                    }

                    m = Pattern.compile("(?i) -q[0-5](?![^ ])").matcher(text);
                    if (m.find()) {
                        text = text.replaceFirst(m.group(), "");
                        weaponQuality = Integer.parseInt(m.group());
                    }
                }
            case ".link":
            case ".dmg":
            case ".today":
            case ".eq":
                if (!text.isEmpty()) {
                    m = Pattern.compile("(?i) -([a-z]{2,10})(?![^ ])").matcher(text);
                    if (m.find()) {
                        text = text.replaceFirst(m.group(), "");
                        for (String v : SERVERS) {
                            if (v.startsWith(m.group(1))) {
                                server = v;
                                break;
                            }
                        }
                    }

                    m = Pattern.compile("[\\w- ]{3,}").matcher(text);
                    if (m.find()) {
                        nick = m.group().trim();
                    }
                }
                break;
        }

        double finalAmount = amount;
        int finalWeaponQuality = weaponQuality;
        String finalServer = server;
        String finalNick = nick;

        interceptor.setHost(server);
        Call<ESimCitizen> call = esim.citizenInfo(nick.replaceAll(" ", "-").toLowerCase());
        call.enqueue(new Callback<ESimCitizen>() {
            @Override
            public void onResponse(Call<ESimCitizen> call, Response<ESimCitizen> response) {
                String msg = null;
                switch (cmd) {
                    case ".licz":
                        msg = response.body().printLicz(finalAmount, finalWeaponQuality);
                        break;
                    case ".link":
                        msg = response.body().printLink(finalServer);
                        break;
                    case ".dmg":
                        msg = response.body().printDmg();
                        break;
                    case ".today":
                        msg = response.body().printToday();
                        break;
                    case ".eq":
                        msg = response.body().printEq();
                        break;
                }
                channel.sendMessage(msg != null ? msg : finalNick + "? Nie znam typa...").queue();
            }

            @Override
            public void onFailure(Call<ESimCitizen> call, Throwable throwable) {
                channel.sendMessage("Wystąpił błąd serwera.");
            }
        });
    }

    private static String extractSpects(Document document) {
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

    private static String extractNews(Document document) {
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
