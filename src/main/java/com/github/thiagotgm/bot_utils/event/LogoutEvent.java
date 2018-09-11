/*
 * This file is part of BotUtils.
 *
 * BotUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BotUtils is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BotUtils. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.thiagotgm.bot_utils.event;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.Event;

/**
 * Event fired after the bot attempted to log out.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-07-29
 */
public abstract class LogoutEvent extends Event {
    
    /**
     * Builds a new instance fired by the given client.
     *
     * @param client The client that is being logged out.
     */
    public LogoutEvent( IDiscordClient client ) {
        
        this.client = client;
        
    }

}
