package bot;

import db.PsqlDb;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import domain.MessageString;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Firstbot {
    public static void main(final String[] args) {
        final String timeZone = System.getenv("timezone");
        final String token = System.getenv("discordbottoken");
        final String postgresqladdress = System.getenv("postgresqladdress");
        final int[] day = {LocalDateTime.now(ZoneId.of(timeZone)).getDayOfYear()};
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();
        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            LocalDateTime currentTime = LocalDateTime.now(ZoneId.of(timeZone));
            int currentDay = currentTime.getDayOfYear();
            /*if (message.getContent().equals("!testi")){
                System.out.println(currentTime);
                final MessageChannel channel = message.getChannel().block();
                PsqlDb db = new PsqlDb(postgresqladdress);
                channel.createMessage(new messageString().firstOutput(db, "testi","testi", currentTime)).block();
            } else */
            if (currentDay != day[0]){
                day[0] = currentDay;
                PsqlDb db = new PsqlDb(postgresqladdress);
                final MessageChannel channel = message.getChannel().block();
                String user_id = event.getMember().get().getId().toString();
                String username = event.getMember().get().getDisplayName();
                db.addFirst(user_id, username, currentTime);
                channel.createMessage(new MessageString().firstOutput(db, username, user_id, currentTime)).block();
                System.exit(0);
            }
        });

        gateway.onDisconnect().block();
    }
}
