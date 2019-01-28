/*
 * Copyright (C) 2017 u/Zavarov
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

package vartas.discordbot;

import ch.qos.logback.core.AppenderBase;
import com.google.common.collect.EvictingQueue;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;

/**
 * An implementation of a logger that adds every event to a public list.
 * The list has a fixed size and old events will eventually be overwritten.
 * @author u/Zavarov
 * @param <T> the type of objects that are stored.
 */
public class LogListener<T> extends AppenderBase<T>{
    /**
     * The size of the list.
     */
    private static final int SIZE = 200;
    /**
     * The length of the date
     */
    private static final int DATE_LENGTH = 32;
    /**
     * The maximum size of a log event, excluding the date.
     */
    public static final int MAX_LENGTH = 100 - DATE_LENGTH;
    /**
     * The internal storage for the most recent events.
     */
    public static final EvictingQueue<Object> MEMORY = EvictingQueue.create(SIZE);
    /**
     * Adds a new event to the internal queue. If the message is too long,
     * it'll be truncated.
     * @param eventObject the new event.
     */
    @Override
    protected void append(T eventObject) {
        StringBuilder builder = new StringBuilder();
        builder.append("[")
               .append(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now()))
               .append("]")
               .append(" ");
        
        if(eventObject.toString().length() <= MAX_LENGTH){
            builder.append(eventObject.toString());
        }else{
            //Reserve some space for the dots.
            builder.append(StringUtils.truncate(eventObject.toString(), MAX_LENGTH-3));
            builder.append("...");
        }
        MEMORY.add(builder.toString());
    }

}
