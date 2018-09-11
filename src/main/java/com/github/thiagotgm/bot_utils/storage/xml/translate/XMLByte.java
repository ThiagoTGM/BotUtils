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

/**
 * XML translator for <tt>byte</tt>s.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-08-29
 */
public class XMLByte extends XMLTextData<Byte> {
    
    /**
     * UID that represents this class.
     */
    private static final long serialVersionUID = 7275877933820716020L;
    
    /**
     * Local name of the XML element.
     */
    public static final String TAG = "byte";
    
    @Override
    public String getTag() {
        
        return TAG;
        
    }

    @Override
    protected Byte fromString( String str ) {

        try {
            return Byte.valueOf( str );
        } catch ( NumberFormatException e ) {
            return null;
        }
        
    }

    @Override
    protected String toString( Byte obj ) {

        return String.valueOf( obj );
        
    }

}
