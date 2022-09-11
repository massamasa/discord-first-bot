package bot;

import db.PsqlDb;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import domain.MessageString;

import java.security.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.time.Instant.now;

public class Firstbot {
    public static void main(final String[] args) {
        final String timeZone = System.getenv("timezone");
        final String token = System.getenv("discordbottoken");
        final String postgresqladdress = System.getenv("postgresqladdress");

        final String channelid = System.getenv("channelid");
        final int[] day = {LocalDateTime.now(ZoneId.of(timeZone)).getDayOfYear()};
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();
        LocalDateTime currentTime = now().atZone(ZoneId.of(timeZone)).toLocalDateTime();
        int currentDay = currentTime.getDayOfYear();
        day[0] = currentDay;
        PsqlDb db = new PsqlDb(postgresqladdress);
        final MessageChannel channel  = (MessageChannel) gateway.getChannelById(Snowflake.of(channelid)).block();
        Message message1 = null;
        while(true) {
            try {
                message1 = channel.
                        getMessagesAfter(Snowflake.of(now().minusSeconds(84600))).
                        toStream().
                        filter(message2 -> message2.getTimestamp().atZone(ZoneId.of(timeZone)).
                                getDayOfYear() == currentDay).
                        sorted((o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp()))
                        .findFirst().get();
            } catch (Exception e) {
                message1 = null;
            }
            if (message1 != null) {
                String user_id = message1.getAuthor().get().getId().toString();
                String username = message1.getAuthor().get().getUsername().toString();
                db.addFirst(user_id, username, message1.getTimestamp().atZone(ZoneId.of(timeZone)).toLocalDateTime());
                channel.createMessage(new MessageString().firstOutput(db, username, user_id, currentTime)).block();
                System.exit(0);
                gateway.onDisconnect().block();
            }
        }
    }
}
