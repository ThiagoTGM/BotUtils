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
import sx.blah.discord.handle.obj.IMessage;

/**
 * XML translator for <tt>IMessage</tt> objects.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-09-02
 */
public class XMLMessage extends AbstractXMLIDLinkedTranslator<IMessage> {
    
    /**
     * UID that represents this class.
     */
    private static final long serialVersionUID = -3490312124600527913L;
    
    /**
     * Local name of the XML element.
     */
    public static final String TAG = "message";

    /**
     * Instantiates a translator.
     *
     * @param client Client to use to obtain messages.
     */
    public XMLMessage( IDiscordClient client ) {
        
        super( client );
        
    }

    @Override
    protected IMessage getObject( long id, IGuild guild ) {

        return client.getMessageByID( id );
        
    }

    @Override
    public String getTag() {

        return TAG;
        
    }
    
    @Override
    public Class<IMessage> getTranslatedClass() {
    	
    	return IMessage.class;
    	
    }

    @Override
    protected IGuild getGuild( IMessage obj ) {

        return null;
        
    }

}
