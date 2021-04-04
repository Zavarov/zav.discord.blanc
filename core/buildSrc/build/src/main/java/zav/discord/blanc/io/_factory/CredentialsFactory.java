
package zav.discord.blanc.io._factory;

import com.google.common.collect.Multimap;
import java.lang.String;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;
import zav.discord.blanc.Rank;
import zav.discord.blanc.io.*;



 public  class CredentialsFactory   {
     public  static  Credentials create( int statusMessageUpdateInterval,  int interactiveMessageLifetime,  int activityUpdateInterval,  String botName,  String globalPrefix,  int shardCount,  int imageWidth,  int imageHeight,  String inviteSupportServer,  String wikiUrl,  String discordToken,  String redditAccount,  String redditId,  String redditSecret,  Path jsonDirectory){

        return create(() -> new Credentials(),statusMessageUpdateInterval,interactiveMessageLifetime,activityUpdateInterval,botName,globalPrefix,shardCount,imageWidth,imageHeight,inviteSupportServer,wikiUrl,discordToken,redditAccount,redditId,redditSecret,jsonDirectory);
    }

     public  static  Credentials create( Supplier<? extends Credentials> _factoryCredentials,  int statusMessageUpdateInterval,  int interactiveMessageLifetime,  int activityUpdateInterval,  String botName,  String globalPrefix,  int shardCount,  int imageWidth,  int imageHeight,  String inviteSupportServer,  String wikiUrl,  String discordToken,  String redditAccount,  String redditId,  String redditSecret,  Path jsonDirectory){

        Credentials _CredentialsInstance = _factoryCredentials.get();
        _CredentialsInstance.setStatusMessageUpdateInterval(statusMessageUpdateInterval);
        _CredentialsInstance.setInteractiveMessageLifetime(interactiveMessageLifetime);
        _CredentialsInstance.setActivityUpdateInterval(activityUpdateInterval);
        _CredentialsInstance.setBotName(botName);
        _CredentialsInstance.setGlobalPrefix(globalPrefix);
        _CredentialsInstance.setShardCount(shardCount);
        _CredentialsInstance.setImageWidth(imageWidth);
        _CredentialsInstance.setImageHeight(imageHeight);
        _CredentialsInstance.setInviteSupportServer(inviteSupportServer);
        _CredentialsInstance.setWikiUrl(wikiUrl);
        _CredentialsInstance.setDiscordToken(discordToken);
        _CredentialsInstance.setRedditAccount(redditAccount);
        _CredentialsInstance.setRedditId(redditId);
        _CredentialsInstance.setRedditSecret(redditSecret);
        _CredentialsInstance.setJsonDirectory(jsonDirectory);
        return _CredentialsInstance;
    }

}