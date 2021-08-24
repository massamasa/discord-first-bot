package botti;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ekabotti {
    public static void main(final String[] args) {
        final int[] day = {LocalDateTime.now(ZoneId.of("Etc/GMT-3")).getDayOfYear()};
        final String token = System.getenv("discordbottoken");
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();
        final String url = System.getenv("postgresqladdress");
        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Etc/GMT-3"));
            int currentDay = currentTime.getDayOfYear();
            if (currentDay != day[0]){
            //if (message.getContent().equals("!eka")){
                day[0] = currentDay;
                final MessageChannel channel = message.getChannel().block();
                String tulostus = "**EKA** oli " + event.getMember().get().getDisplayName()+ "!\n\n";
                try {
                    Connection conn = DriverManager.getConnection(url);
                    PreparedStatement preparedStatement = conn.prepareStatement("SELECT user_id FROM statistics WHERE user_id = ?");
                    preparedStatement.setString(1, event.getMember().get().getId().toString());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (!resultSet.next()) {
                        preparedStatement = conn.prepareStatement("INSERT INTO statistics (user_id, username, score) VALUES (?, ?, ?)");
                        preparedStatement.setString(1, event.getMember().get().getId().toString());
                        preparedStatement.setString(2, event.getMember().get().getDisplayName().toString());
                        preparedStatement.setInt(3, 1);
                        preparedStatement.execute();
                    } else {
                        preparedStatement = conn.prepareStatement("UPDATE statistics SET  score = (score + 1) WHERE user_id = ?");
                        preparedStatement.setString(1, event.getMember().get().getId().toString());
                        preparedStatement.execute();
                    }
                    preparedStatement = conn.prepareStatement("INSERT INTO times (user_id, username, time) VALUES (?, ?, ?)");
                    preparedStatement.setString(1, event.getMember().get().getId().toString());
                    preparedStatement.setString(2, event.getMember().get().getDisplayName().toString());
                    preparedStatement.setTimestamp(3, Timestamp.valueOf((currentTime)));
                    preparedStatement.execute();


                    preparedStatement = conn.prepareStatement("SELECT * FROM statistics");
                    resultSet = preparedStatement.executeQuery();
                    tulostus += "**Tilastot**\n";
                    while(resultSet.next()){
                        tulostus+=resultSet.getString(2) +":  "+ resultSet.getInt(3)+ "\n";
                    }
                    preparedStatement = conn.prepareStatement("SELECT * FROM times ORDER BY time DESC");
                    resultSet = preparedStatement.executeQuery();
                    tulostus+= "**Viimeisen viikon ekat:** \n";
                    int laskuri = 0;
                    while(resultSet.next() && laskuri != 7){
                        tulostus+=resultSet.getString(2) +":  "+ resultSet.getTimestamp(3)+ "\n";
                        laskuri++;
                    }
                    channel.createMessage(tulostus).block();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });

        gateway.onDisconnect().block();
    }
}
