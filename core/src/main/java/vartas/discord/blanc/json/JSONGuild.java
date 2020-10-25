/*
 * Copyright (c) 2020 Zavarov
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

package vartas.discord.blanc.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.Errors;
import vartas.discord.blanc.Guild;
import vartas.discord.blanc.Role;
import vartas.discord.blanc.TextChannel;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class JSONGuild extends JSONGuildTOP {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    public static final String PREFIX = "prefix";
    public static final String CHANNELS = "channels";
    public static final String ROLES = "roles";
    public static final String BLACKLIST = "blacklist";

    @Override
    protected void $fromPrefix(JSONObject source, Guild target){
        target.setPrefix(Optional.ofNullable(source.optString(PREFIX)));
    }

    @Override
    protected void $toPrefix(Guild source, JSONObject target){
        source.ifPresentPrefix(prefix -> target.put(PREFIX, prefix));
    }

    @Override
    protected void $fromMembers(JSONObject source, Guild target){
        //Omitted
    }

    @Override
    protected void $toMembers(Guild source, JSONObject target){
        //Omitted
    }

    @Override
    protected void $fromSelfMember(JSONObject source, Guild target){
        //Omitted
    }

    @Override
    protected void $toSelfMember(Guild source, JSONObject target){
        //Omitted
    }

    @Override
    protected void $fromChannels(JSONObject source, Guild target){
        JSONArray jsonChannels = source.optJSONArray(CHANNELS);
        if(jsonChannels != null) {
            for (int i = 0; i < jsonChannels.length(); ++i) {
                try {
                    JSONObject jsonChannel = jsonChannels.getJSONObject(i);
                    //TODO Magic key label
                    TextChannel textChannel = target.getChannels(jsonChannel.getLong("id"));
                    target.putChannels(textChannel.getId(), JSONTextChannel.fromJson(textChannel, jsonChannel));
                }catch(ExecutionException e){
                    //The text channel may no longer exist.
                    log.warn(Errors.UNKNOWN_TEXTCHANNEL.toString(), e);
                }
            }
        }
    }

    @Override
    protected void $toChannels(Guild source, JSONObject target){
        JSONArray jsonChannels = new JSONArray();
        for(TextChannel channel : source.valuesChannels())
            jsonChannels.put(JSONTextChannel.toJson(channel, new JSONObject()));
        target.put(CHANNELS, jsonChannels);
    }

    @Override
    protected void $fromRoles(JSONObject source, Guild target){
        JSONArray jsonRoles = source.optJSONArray(ROLES);
        if(jsonRoles != null) {
            for (int i = 0; i < jsonRoles.length(); ++i) {
                try{
                    JSONObject jsonRole = jsonRoles.getJSONObject(i);
                    //TODO Magic key label
                    Role role = target.getRoles(jsonRole.getLong("id"));
                    target.putRoles(role.getId(), JSONRole.fromJson(role, jsonRole));
                }catch(ExecutionException e){
                    //The role may no longer exist.
                    log.warn(Errors.UNKNOWN_ROLE.toString(), e);
                }
            }
        }
    }

    @Override
    protected void $toRoles(Guild source, JSONObject target){
        JSONArray jsonRoles = new JSONArray();
        for(Role role : source.valuesRoles())
            jsonRoles.put(JSONRole.toJson(role, new JSONObject()));
        target.put(ROLES, jsonRoles);
    }

    @Override
    protected void $fromBlacklist(JSONObject source, Guild target){
        JSONArray jsonBlacklist = source.optJSONArray(BLACKLIST);
        if(jsonBlacklist != null) {
            for (int i = 0; i < jsonBlacklist.length(); ++i) {
                target.addBlacklist(jsonBlacklist.getString(i));
            }
            target.compilePattern();
        }
    }

    @Override
    protected void $toBlacklist(Guild source, JSONObject target){
        JSONArray jsonBlacklist = new JSONArray();
        for(String entry : source.getBlacklist())
            jsonBlacklist.put(entry);
        target.put(BLACKLIST, jsonBlacklist);
    }
}
