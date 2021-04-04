
package zav.discord.blanc;

import com.google.common.cache.Cache;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Object;
import java.lang.RuntimeException;
import java.lang.String;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import zav.discord.blanc._visitor.ArchitectureVisitor;
import zav.discord.blanc.activity.Activity;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 public  enum Permission{
    CREATE_INSTANT_INVITE,KICK_MEMBERS,BAN_MEMBERS,ADMINISTRATOR,MANAGE_CHANNELS,MANAGE_GUILD,ADD_REACTIONS,VIEW_AUDIT_LOG,PRIORITY_SPEAKER,STREAM,VIEW_CHANNEL,SEND_MESSAGES,SEND_TTS_MESSAGES,MANAGE_MESSAGES,EMBED_LINKS,ATTACH_FILES,READ_MESSAGE_HISTORY,MENTION_EVERYONE,USE_EXTERNAL_EMOJIS,VIEW_GUILD_INSIGHTS,CONNECT,SPEAK,MUTE_MEMBERS,DEAFEN_MEMBERS,MOVE_MEMBERS,USE_VAD,CHANGE_NICKNAME,MANAGE_NICKNAMES,MANAGE_ROLES,MANAGE_WEBHOOKS,MANAGE_EMOJIS;
     public  void accept( ArchitectureVisitor visitor){

        visitor.handle(getRealThis());
    }

     public  Permission getRealThis(){
        return this;
    }

}