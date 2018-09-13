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

import com.github.thiagotgm.bot_utils.storage.Data;
import com.github.thiagotgm.bot_utils.storage.xml.XMLTranslator;

/**
 * Translator that stores and loads Data instances to/from an XML format.
 * <p>
 * This translator does <i>not</i> allow encoding of <tt>null</tt> instances,
 * although {@link Data#nullData() <tt>null</tt>-<i>valued</i>} Data instances
 * are acceptable. Due to this, decoding will never return <tt>null</tt>, but
 * may return <tt>null</tt>-valued Data.
 * 
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-08-30
 */
public class XMLData implements XMLTranslator<Data> {
	
	/**
	 * UID that represents this class.
	 */
	private static final long serialVersionUID = 7699167724386036367L;

	private static final String NULL_TAG = "null";
	
	private final XMLList<Data> listTranslator = new XMLList<>( this );
	private final XMLMap<String,Data> mapTranslator = new XMLMap<>( new XMLString(), this );

	@Override
	public Data read( XMLStreamReader in ) throws XMLStreamException {

		if ( in.getEventType() != XMLStreamConstants.START_ELEMENT ) {
			throw new XMLStreamException( "Stream not in start element." );
		}
		
		switch ( in.getLocalName() ) { // Identify data type.
			
			case XMLString.TAG:
				return Data.stringData( new XMLString().read( in ) );
				
			case XMLDouble.TAG:
				return Data.numberData( new XMLDouble().read( in ) );
				
			case XMLLong.TAG:
				return Data.numberData( new XMLLong().read( in ) );
				
			case XMLBoolean.TAG:
				return Data.booleanData( new XMLBoolean().read( in ) );
				
			case NULL_TAG:
				if ( !( in.hasNext() && 
					  ( in.next() == XMLStreamConstants.END_ELEMENT ) && 
					    in.getLocalName().equals( NULL_TAG ) ) ) {
					throw new XMLStreamException( "Malformed NULL element." );
				}
				return Data.nullData();
				
			case XMLList.TAG:
				return Data.listData( listTranslator.read( in ) );
				
			case XMLMap.TAG:
				return Data.mapData( mapTranslator.read( in ) );
				
			default:
				throw new XMLStreamException( "Unrecognized element." );
		
		}
		
	}

	/**
	 * @throws NullPointerException if the given instance is <tt>null</tt>.
	 */
	@Override
	public void write( XMLStreamWriter out, Data instance )
			throws XMLStreamException, NullPointerException {
		
		switch ( instance.getType() ) { // Write depending on data type.
		
			case STRING:
				new XMLString().write( out, instance.getString() );
				break;
				
			case NUMBER:
				if ( instance.isFloat() ) { // Write as float.
					new XMLDouble().write( out, instance.getNumberFloat() );
				} else { // Write as integer.
					new XMLLong().write( out, instance.getNumberInteger() );
				}
				break;
		
			case BOOLEAN:
				new XMLBoolean().write( out, instance.getBoolean() );
				break;
				
			case NULL:
				out.writeStartElement( NULL_TAG );
				out.writeEndElement();
				break;
				
			case LIST:
				listTranslator.write( out, instance.getList() );
				break;
				
			case MAP:
				mapTranslator.write( out, instance.getMap() );
				break;

		}
		
	}

}
