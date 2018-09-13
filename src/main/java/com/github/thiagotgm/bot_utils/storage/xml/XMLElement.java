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

package com.github.thiagotgm.bot_utils.storage.xml;

import java.io.Serializable;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.github.thiagotgm.bot_utils.storage.xml.translate.AbstractXMLTranslator;

/**
 * Type of object that can be stored in an XML format.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-08-18
 */
public interface XMLElement extends Serializable {
    
    /**
     * Reads instance data from an XML stream.
     * <p>
     * The position of the stream cursor when the method starts should be the opening tag
     * of the element to read, and after the method ends its position is the closing tag of
     * the element to read.
     *
     * @param in The stream to read the instance data from.
     * @throws XMLStreamException if an error occurred while reading.
     */
    void read( XMLStreamReader in ) throws XMLStreamException;
    
    /**
     * Writes the instance to an XML stream.
     *
     * @param out The stream to write data to.
     * @throws XMLStreamException if an error occurred while writing.
     */
    void write( XMLStreamWriter out ) throws XMLStreamException;
    
    /**
     * Translator for objects that are already capable of being written to/read from an XML
     * stream, that delegates reading and writing to the natural methods of the object.
     * <p>
     * Only necessary operation is creating new instances of the object.
     * <p>
     * This translator supports <tt>null</tt> instances.
     *
     * @version 1.0
     * @author ThiagoTGM
     * @since 2017-08-19
     * @param <T> The type of element that is translated.
     */
    @FunctionalInterface
    static interface Translator<T extends XMLElement> extends XMLTranslator<T> {
    	
    	/**
    	 * Local name that indicates a <tt>null</tt> instance. If a class that implements
    	 * XMLElement also uses this as the tag, then its translator cannot use this
    	 * interface and must implement XMLTranslator (or extend
    	 * {@link AbstractXMLTranslator}) directly.
    	 */
    	static final String NULL_TAG = "null";
        
        /**
         * Creates a new instance.
         *
         * @return A new instance.
         */
        T newInstance();
        
        @Override
        default T read( XMLStreamReader in ) throws XMLStreamException {
        	
        	if ( in.isStartElement() && in.getLocalName().equals( NULL_TAG ) ) {
        		if ( in.next() != XMLStreamConstants.END_ELEMENT ) {
        			throw new XMLStreamException( "Null element is not empty." );
        		}
        		if ( !in.getLocalName().equals( NULL_TAG ) ) {
        			throw new XMLStreamException( "Null element not closed." );
        		}
        		return null; // Null element.
        	}
        	
        	T instance = newInstance();
        	instance.read( in );
        	
        	return instance;
        	
        }
        
        @Override
        default void write( XMLStreamWriter out, T instance ) throws XMLStreamException {
        	
        	if ( instance == null ) {
        		out.writeEmptyElement( NULL_TAG );
        	} else {
        		instance.write( out );
        	}
        	
        }
        
    }

}
