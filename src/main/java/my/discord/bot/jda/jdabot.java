package my.discord.bot.jda;


import com.sun.org.apache.bcel.internal.generic.RETURN;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class jdabot extends ListenerAdapter
{
    public static void main(String[] args)


            throws LoginException, RateLimitedException, InterruptedException
    {
        JDA jda = new JDABuilder(AccountType.BOT).setToken("***REMOVED***").buildBlocking();
        jda.addEventListener(new jdabot());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.isFromType(ChannelType.PRIVATE))
        {
            System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
                    event.getMessage().getContent());
        }
        else
        {
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                    event.getTextChannel().getName(), event.getMember().getEffectiveName(),
                    event.getMessage().getContent());

            String message=event.getMessage().getContent();
            String name =event.getMember().getEffectiveName();
            MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.


            if(message.charAt(0) == '.') {
                String cmd  = "(^\\.[a-z]{1,10})"; // " -q[0-5](?![^ ])"
                Pattern p = Pattern.compile(cmd);
                Matcher m = p.matcher(message);
                m.find();

                if(m.group().equals(".help")){
                    channel.sendMessage("\"Dostępne komendy: .s, .licz, .link, .dmg, .today, .eq, .spec, .h, .noslap, .home => Uwagi do DR4KA (e-sim).\"").queue();
                }else if(m.group().equals(".licz")) {
                    String nickR  = "\\s[a-z]{3,}";
                    String  weaponQualityR ="\\s[0-9]{1,3} ";
                    String amountR = "\\s-[0-9]{1,3}";
                    String nick,weaponQuality,amount;
                    p=Pattern.compile(nickR);
                    m = p.matcher(message);
                    if(m.find()){ nick=m.group();
                  //  Pattern p1=Pattern.compile(nickR);
                   // Matcher m1 = p1.matcher(message);
                  //  if(m1.find()){ nick=m1.group();
                    }else{nick=name;}

                    p=Pattern.compile(weaponQualityR);
                    m = p.matcher(message);

                    if(m.find()){ weaponQuality=m.group();

                    }else{weaponQuality="domyślne";}

                    p=Pattern.compile(amountR);
                    m = p.matcher(message);

                    if(m.find()){ amount=m.group();

                    }else{amount="domyślne";}

                    channel.sendMessage("komenda .licz która przyjmuje wprowadzone arg- Nick: "+nick + ", weaponQuality: "+weaponQuality+", Amount: "+amount).queue();

                }else if(m.group().equals(".link")){

                    String nick;
                    String nickR  = "\\s[a-z]{3,}";
                    p=Pattern.compile(nickR);
                    m = p.matcher(message);
                    if(m.find()){ nick=m.group();
                        //  Pattern p1=Pattern.compile(nickR);
                        // Matcher m1 = p1.matcher(message);
                        //  if(m1.find()){ nick=m1.group();
                    }else{nick=name;}

                    channel.sendMessage("link do profilu uż"+ nick+":  tu bedzie link z serwera").queue();

                }

            }
        }
    }
}