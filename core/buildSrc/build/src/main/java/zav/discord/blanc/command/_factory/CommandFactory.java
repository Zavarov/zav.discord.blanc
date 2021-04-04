
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



 public  class CommandFactory   {
     public  static  Command create( Supplier<? extends Command> _factoryCommand,  Shard $Shard,  Message $Message){

        Command _CommandInstance = _factoryCommand.get();
        _CommandInstance.set$Shard($Shard);
        _CommandInstance.set$Message($Message);
        return _CommandInstance;
    }

     public  static  Command create( Supplier<? extends Command> _factoryCommand,  Shard $Shard,  Message $Message,  List<String> $Flags){

        Command _CommandInstance = _factoryCommand.get();
        _CommandInstance.set$Shard($Shard);
        _CommandInstance.set$Message($Message);
        _CommandInstance.set$Flags($Flags);
        return _CommandInstance;
    }

}