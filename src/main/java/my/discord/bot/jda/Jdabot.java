package my.discord.bot.jda;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import esim.AppSettings;
import esim.ESimClient;
import net.dv8tion.jda.core.AccountType;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;

public class Jdabot extends ListenerAdapter {
    public static AppSettings APP_SETTINGS;
    public static ESimClient eSimClient;

    public static void main(String[] args) throws LoginException, RateLimitedException, InterruptedException, IOException {
        JsonReader reader = new JsonReader(new FileReader("settings.json"));
        Gson gson = new Gson();
        APP_SETTINGS = gson.fromJson(reader, AppSettings.class);
        JDA jda = new JDABuilder(AccountType.BOT).setToken(APP_SETTINGS.botToken).buildBlocking();
        eSimClient = new ESimClient();
        jda.addEventListener(new Jdabot());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
                    event.getMessage().getContent());
        } else {
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                    event.getTextChannel().getName(), event.getMember().getEffectiveName(),
                    event.getMessage().getContent());


            String message = event.getMessage().getContent();
            String name = event.getMember().getEffectiveName();
            MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.
            if (message.isEmpty()) return;
            if ( message.charAt(0) == '.' ) {
                try {
                    eSimClient.processCommands(message, name, channel);
//                    EmbedBuilder eb = new EmbedBuilder();
//                    eb.setColor(Color.red);
//                    eb.setTitle("ESIM BOT ELOELO!!!");
//                    eb.setDescription(str);
//                    channel.sendMessage(eb.build()).queue();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}