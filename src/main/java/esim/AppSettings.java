package esim;

public class AppSettings {
    public String botToken;
    public String securaLogin;
    public String securaPassword;

    public AppSettings(String botToken, String securaLogin, String securaPassword) {
        this.botToken = botToken;
        this.securaLogin = securaLogin;
        this.securaPassword = securaPassword;
    }
}
