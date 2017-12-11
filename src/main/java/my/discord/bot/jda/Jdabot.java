package my.discord.bot.jda;


import esim.ESimClient;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Jdabot extends ListenerAdapter {
    public static ESimClient eSimClient;
    public static void main(String[] args) throws LoginException, RateLimitedException, InterruptedException, IOException {
        JDA jda = new JDABuilder(AccountType.BOT).setToken("***REMOVED***").buildBlocking();
        eSimClient = new ESimClient();
        jda.addEventListener(new Jdabot());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
                    event.getMessage().getContent());
        }
        else {
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                    event.getTextChannel().getName(), event.getMember().getEffectiveName(),
                    event.getMessage().getContent());

            String message=event.getMessage().getContent();
            String name =event.getMember().getEffectiveName();
            MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.

            if(message.charAt(0) == '.') {
                try {
                    channel.sendMessage(eSimClient.processCommands(message, name)).queue();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}