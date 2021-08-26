package domain;

import db.PsqlDb;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MessageString {


    public String firstOutput(PsqlDb db, String username, String user_id, LocalDateTime currentTime){
        StringBuilder messageOutput = new StringBuilder("**EKA** oli " + username + "!\n\n");
        messageOutput.append("**Tilastot**\n");
        ArrayList<String> statisticsList = db.getStatisticsList();
        for(String s: statisticsList){
            messageOutput.append(s);
        }
        messageOutput.append("**Viimeisen viikon ekat:** \n");
        ArrayList<String> timesList = db.getTimesList();
        for(String s: timesList){
            messageOutput.append(s);
        }
        return messageOutput.toString();
    }
}
