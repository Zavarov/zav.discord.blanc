
package zav.discord.blanc.command._factory;

import java.lang.Class;
import java.lang.InterruptedException;
import java.lang.Object;
import java.lang.Runnable;
import java.lang.String;
import java.lang.Throwable;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import zav.discord.blanc.Guild;
import zav.discord.blanc.Member;
import zav.discord.blanc.Message;
import zav.discord.blanc.MessageChannel;
import zav.discord.blanc.MessageEmbed;
import zav.discord.blanc.Permission;
import zav.discord.blanc.Role;
import zav.discord.blanc.SelfUser;
import zav.discord.blanc.Shard;
import zav.discord.blanc.TextChannel;
import zav.discord.blanc.User;
import zav.discord.blanc.command.*;
import zav.discord.blanc.parser.AbstractTypeResolver;
import zav.discord.blanc.parser.Argument;



 public  class MessageCommandFactory   {
     public  static  MessageCommand create( Supplier<? extends MessageCommand> _factoryMessageCommand,  User $Author,  MessageChannel $MessageChannel,  Shard $Shard,  Message $Message){

        MessageCommand _MessageCommandInstance = _factoryMessageCommand.get();
        _MessageCommandInstance.set$Author($Author);
        _MessageCommandInstance.set$MessageChannel($MessageChannel);
        _MessageCommandInstance.set$Shard($Shard);
        _MessageCommandInstance.set$Message($Message);
        return _MessageCommandInstance;
    }

     public  static  MessageCommand create( Supplier<? extends MessageCommand> _factoryMessageCommand,  User $Author,  MessageChannel $MessageChannel,  Shard $Shard,  Message $Message,  List<String> $Flags){

        MessageCommand _MessageCommandInstance = _factoryMessageCommand.get();
        _MessageCommandInstance.set$Author($Author);
        _MessageCommandInstance.set$MessageChannel($MessageChannel);
        _MessageCommandInstance.set$Shard($Shard);
        _MessageCommandInstance.set$Message($Message);
        _MessageCommandInstance.set$Flags($Flags);
        return _MessageCommandInstance;
    }

}