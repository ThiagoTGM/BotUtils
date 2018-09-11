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

package com.github.thiagotgm.bot_utils.storage.xml.translate;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;

/**
 * XML translator for <tt>IGuild</tt> objects.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-09-02
 */
public class XMLGuild extends AbstractXMLIDLinkedTranslator<IGuild> {
    
    /**
     * UID that represents this class.
     */
    private static final long serialVersionUID = -2877808979476994881L;
    
    /**
     * Local name of the XML element.
     */
    public static final String TAG = "guild";

    /**
     * Instantiates a translator.
     *
     * @param client Client to use to obtain guilds.
     */
    public XMLGuild( IDiscordClient client ) {
        
        super( client );
        
    }

    @Override
    protected IGuild getObject( long id, IGuild guild ) {

        return client.getGuildByID( id );
        
    }

    @Override
    public String getTag() {

        return TAG;
        
    }
    
    @Override
    public Class<IGuild> getTranslatedClass() {
    	
    	return IGuild.class;
    	
    }

    @Override
    protected IGuild getGuild( IGuild obj ) {

        return null;
        
    }

}
