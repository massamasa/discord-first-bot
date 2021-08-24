package bot;

import db.PsqlDb;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class Firstbot {
    public static void main(final String[] args) {
        String timeZone = System.getenv("timezone");
        String token = System.getenv("discordbottoken");
        String postgresqladdress = System.getenv("postgresqladdress");
        final int[] day = {LocalDateTime.now(ZoneId.of(timeZone)).getDayOfYear()};
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();
        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            LocalDateTime currentTime = LocalDateTime.now(ZoneId.of(timeZone));
            int currentDay = currentTime.getDayOfYear();
            if (message.getContent().equals("!testi")){
                System.out.println(currentTime);
                final MessageChannel channel = message.getChannel().block();
                channel.createMessage("toimii").block();
                PsqlDb db = new PsqlDb(postgresqladdress);
                String messageOutput = "";
                ArrayList<String> timesList = db.getTimesList();
                for(String s: timesList){
                    messageOutput+=s;
                }
                channel.createMessage(messageOutput).block();
            } else if (currentDay != day[0]){
                day[0] = currentDay;
                PsqlDb db = new PsqlDb(postgresqladdress);
                final MessageChannel channel = message.getChannel().block();
                String user_id = event.getMember().get().getId().toString();
                String username = event.getMember().get().getDisplayName();
                String messageOutput = "**EKA** oli " + username + "!\n\n";
                if (!db.userExists(user_id)) {
                    db.addUser(user_id, event.getMember().get().getDisplayName());
                } else {
                    db.addOneToScore(user_id);
                }
                db.addToTimes(user_id, username, Timestamp.valueOf(currentTime));
                db.getStatisticsList();
                messageOutput += "**Tilastot**\n";
                ArrayList<String> statisticsList = db.getStatisticsList();
                for(String s: statisticsList){
                    messageOutput+=s;
                }
                messageOutput+= "**Viimeisen viikon ekat:** \n";
                ArrayList<String> timesList = db.getTimesList();
                for(String s: timesList){
                    messageOutput+=s;
                }
                channel.createMessage(messageOutput).block();
                System.exit(0);
            }
        });

        gateway.onDisconnect().block();
    }
}
