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

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.github.thiagotgm.bot_utils.storage.xml.XMLTranslator;

/**
 * Shared pieces of implementation for translators.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-09-12
 * @param <T> The type of object that can be translated.
 */
public abstract class AbstractXMLTranslator<T> implements XMLTranslator<T> {
	
	/**
	 * UID that represents this class.
	 */
	private static final long serialVersionUID = 1365836807934317943L;
	private static final String NULL_ATTRIBUTE = "null";

	/**
     * Retrieves the tag that identifies the object.
     *
     * @return The object tag.
     */
    public abstract String getTag();
    
    /**
     * Reads the content of an instance from the stream (as written by
     * {@link #write(XMLStreamWriter, Object) write(XMLStreamWriter, T)}). It is not
     * necessary to check that the stream starts in the correct element, as that is
     * automatically checked by {@link #read(XMLStreamReader)} before calling this.
     * It is also not necessary to worry about <tt>null</tt> instances, as that case is also
	 * handled automatically by <tt>read()</tt>.
     * 
     * @param in The stream to read from.
     * @return The read instance.
     * @throws XMLStreamException if an error was encountered while decoding.
     */
    protected abstract T readContent( XMLStreamReader in ) throws XMLStreamException;
	
	@Override
	public T read( XMLStreamReader in ) throws XMLStreamException {

		if ( ( in.getEventType() != XMLStreamConstants.START_ELEMENT ) ||
	              !in.getLocalName().equals( getTag() ) ) {
			throw new XMLStreamException( "Did not find element start." );
        }
        
		// Check if null.
        if ( Boolean.parseBoolean( in.getAttributeValue( null, NULL_ATTRIBUTE ) ) ) {
        	while ( in.hasNext() ) { // Skip until end of element.
        		
        		if ( ( in.next() == XMLStreamConstants.END_ELEMENT ) &&
        				in.getLocalName().equals( getTag() ) ) {
        			return null;
        		}
        		
        	}
        	return null; // Is a null value.
        }
        
        return readContent( in );
		
	}
	
	/**
	 * Writes the content of the instance to the stream. The opening and closing elements
	 * should not be written (they are written automatically by
	 * {@link #write(XMLStreamWriter,Object) write(XMLStreamWriter,T)}).
	 * It is also not necessary to worry about <tt>null</tt> instances, as that case is also
	 * handled automatically by <tt>write()</tt>.
	 * 
	 * @param out The stream to write to.
	 * @param instance The instance to encode the contents of.
	 * @throws XMLStreamException if an error was encountered while encoding.
	 */
	protected abstract void writeContent( XMLStreamWriter out, T instance ) throws XMLStreamException;

	@Override
	public void write( XMLStreamWriter out, T instance ) throws XMLStreamException {

		out.writeStartElement( getTag() );
        if ( instance == null ) { // Instance is null.
        	out.writeAttribute( NULL_ATTRIBUTE, "true" );
        } else {
        	writeContent( out, instance );
        }
        out.writeEndElement();

	}

}
