# Discord First-Bot
A discord bot for recording statistics of who was the FIRST poster for each day. The time zone is currently set to finnish summer time but this may be updated in the future to accomodate daylight savings time and other time zones. A simpler implementation in python can be found [here](https://github.com/massamasa/ekabotti-python).
## How to set up
 This software is meant to be run on heroku, scheduled to run 30 minutes prior to midnight using the command `` ./gradlew run `` and will terminate immediately after the first post, thus saving heroku dyno hours. The Postgresql heroku add-on must be setup and database intialized with the schema found in the file ``schema.sql``. Environment variables ``timezone``, ``channelid``(Snowflake of message channel), ``discordbottoken`` and ``postgresqladdress`` on heroku must be set prior to running the bot.

## How to use in Discord
Invite the bot to the server through the Discord Developer Portal and be the first poster of the day. The bot will publish the number of firsts for each first poster and the first posters of the previous week. Then the program will terminate to be run the next day through the heroku scheduler.

## Demonstration
![Demonstration](https://github.com/massamasa/discord-first-bot/blob/main/DiscordScreenshot.png)
