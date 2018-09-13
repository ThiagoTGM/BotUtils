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

import java.io.NotSerializableException;
import java.io.Serializable;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.github.thiagotgm.bot_utils.utils.Utils;

/**
 * XML Wrapper for {@link Serializable} objects.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-08-18
 * @param <T> The type being serialized.
 */
public class XMLSerializer<T extends Serializable> extends AbstractXMLTranslator<T> {

    /**
     * UID that represents this class.
     */
    private static final long serialVersionUID = -3875253346127541487L;

    /**
     * Local XML name that identifies this element.
     */
    public static final String SERIALIZED_TAG = "serialized";
    
    @Override
    public String getTag() {
    	
    	return SERIALIZED_TAG;
    	
    }

    @Override
    public T readContent( XMLStreamReader in ) throws XMLStreamException {
        
        String encoded = in.getElementText(); // Get encoded text.
        
        if ( ( in.getEventType() != XMLStreamConstants.END_ELEMENT ) ||
                in.getLocalName().equals( SERIALIZED_TAG ) ) { // Check end tag.
            throw new XMLStreamException( "Did not find element end." );
        }
        
        try {
            @SuppressWarnings( "unchecked" ) // Decode from string and check if castable
            T obj = (T) Utils.stringToSerializable( encoded );      // to expected type.
            return obj;
        } catch ( ClassCastException e ) {
            throw new XMLStreamException( "Encoded object does not correspond to expected type." );
        }
        
    }
    
    @Override
    public void writeContent( XMLStreamWriter out, T instance ) throws XMLStreamException {
        
        try { // Encode into a Serializable string.
            out.writeCharacters( Utils.encode( instance ) );
        } catch ( NotSerializableException e ) {
            throw new XMLStreamException( "Element could not be serialized for encoding." );
        }
        
    }

}
