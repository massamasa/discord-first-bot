# Discord First-Bot
A discord bot for recording statistics of who was the FIRST poster for each day. The time zone is currently set to finnish summer time but this may be updated in the future to accomodate daylight savings time and other time zones.

# How to set up
 This software is meant to be run in a heroku container scheduled to run 30 minutes prior to midnight using the command `` ./gradlew run `` and will terminate immediately after the first post, thus saving heroku dyno hours. The Postgresql heroku add-on must be setup and database intialized with the schema found in the file ``schema.sql``. Environment variables ``discordbottoken`` and ``postgresqladdress`` on heroku must be set prior to running the bot.
 
 # How to use in discord.
 Invite the bot to the server through the Discord Developer Portal and be the first poster of the day. The bot will publish the number of firsts for each first poster and the first posters of the previous week.