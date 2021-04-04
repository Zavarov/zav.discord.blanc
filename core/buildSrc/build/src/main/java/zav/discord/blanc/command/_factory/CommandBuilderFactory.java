
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



 public  class CommandBuilderFactory   {
     public  static  CommandBuilder create( Supplier<? extends CommandBuilder> _factoryCommandBuilder,  AbstractTypeResolver typeResolver,  Shard shard){

        CommandBuilder _CommandBuilderInstance = _factoryCommandBuilder.get();
        _CommandBuilderInstance.setTypeResolver(typeResolver);
        _CommandBuilderInstance.setShard(shard);
        return _CommandBuilderInstance;
    }

}