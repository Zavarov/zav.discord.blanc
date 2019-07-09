package vartas.discord.bot.io.config._ast;

import vartas.discord.bot.io.config._symboltable.*;

import java.io.File;
import java.util.List;
import java.util.Optional;

/*
 * Copyright (C) 2019 Zavarov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class ASTConfigArtifact extends ASTConfigArtifactTOP{
    protected ASTConfigArtifact(){
        super();
    }
    protected ASTConfigArtifact(List<ASTEntry> entryList){
        super(entryList);
    }

    public int getStatusMessageUpdateInterval(){
        Optional<StatusMessageUpdateIntervalSymbol> symbol = getEnclosingScope().resolve("statusMessageUpdateInterval", StatusMessageUpdateIntervalSymbol.KIND);
        return symbol.get().getStatusMessageUpdateIntervalNode().get().getNatLiteral().getValue();
    }

    public int getInteractiveMessageLifetime(){
        Optional<InteractiveMessageLifetimeSymbol> symbol = getEnclosingScope().resolve("interactiveMessageLifetime", InteractiveMessageLifetimeSymbol.KIND);
        return symbol.get().getInteractiveMessageLifetimeNode().get().getNatLiteral().getValue();
    }

    public int getDiscordShards(){
        Optional<DiscordShardsSymbol> symbol = getEnclosingScope().resolve("discordShards", DiscordShardsSymbol.KIND);
        return symbol.get().getDiscordShardsNode().get().getNatLiteral().getValue();
    }

    public int getActivityUpdateInterval(){
        Optional<ActivityUpdateIntervalSymbol> symbol = getEnclosingScope().resolve("activityUpdateInterval", ActivityUpdateIntervalSymbol.KIND);
        return symbol.get().getActivityUpdateIntervalNode().get().getNatLiteral().getValue();
    }

    public String getInviteSupportServer(){
        Optional<InviteSupportServerSymbol> symbol = getEnclosingScope().resolve("inviteSupportServer", InviteSupportServerSymbol.KIND);
        return symbol.get().getInviteSupportServerNode().get().getStringLiteral().getValue();
    }

    public String getBotName(){
        Optional<BotNameSymbol> symbol = getEnclosingScope().resolve("botName", BotNameSymbol.KIND);
        return symbol.get().getBotNameNode().get().getStringLiteral().getValue();
    }

    public String getVersion(){
        Optional<VersionSymbol> symbol = getEnclosingScope().resolve("version", VersionSymbol.KIND);
        return symbol.get().getVersionNode().get().getStringLiteral().getValue();
    }

    public String getGlobalPrefix(){
        Optional<GlobalPrefixSymbol> symbol = getEnclosingScope().resolve("globalPrefix", GlobalPrefixSymbol.KIND);
        return symbol.get().getGlobalPrefixNode().get().getStringLiteral().getValue();
    }

    public String getWikiLink(){
        Optional<WikiLinkSymbol> symbol = getEnclosingScope().resolve("wikiLink", WikiLinkSymbol.KIND);
        return symbol.get().getWikiLinkNode().get().getStringLiteral().getValue();
    }

    public int getImageWidth(){
        Optional<ImageWidthSymbol> symbol = getEnclosingScope().resolve("imageWidth", ImageWidthSymbol.KIND);
        return symbol.get().getImageWidthNode().get().getNatLiteral().getValue();
    }

    public int getImageHeight(){
        Optional<ImageHeightSymbol> symbol = getEnclosingScope().resolve("imageHeight", ImageHeightSymbol.KIND);
        return symbol.get().getImageHeightNode().get().getNatLiteral().getValue();
    }

    public String getDiscordToken(){
        Optional<DiscordTokenSymbol> symbol = getEnclosingScope().resolve("discordToken", DiscordTokenSymbol.KIND);
        return symbol.get().getDiscordTokenNode().get().getStringLiteral().getValue();
    }

    public String getRedditAccount(){
        Optional<RedditAccountSymbol> symbol = getEnclosingScope().resolve("redditAccount", RedditAccountSymbol.KIND);
        return symbol.get().getRedditAccountNode().get().getStringLiteral().getValue();
    }

    public String getRedditId(){
        Optional<RedditIdSymbol> symbol = getEnclosingScope().resolve("redditId", RedditIdSymbol.KIND);
        return symbol.get().getRedditIdNode().get().getStringLiteral().getValue();
    }

    public String getRedditSecret(){
        Optional<RedditSecretSymbol> symbol = getEnclosingScope().resolve("redditSecret", RedditSecretSymbol.KIND);
        return symbol.get().getRedditSecretNode().get().getStringLiteral().getValue();
    }
}