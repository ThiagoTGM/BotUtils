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

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.thiagotgm.bot_utils.storage.xml.XMLTestHelper;
import com.github.thiagotgm.bot_utils.storage.xml.translate.XMLInteger;
import com.github.thiagotgm.bot_utils.storage.xml.translate.XMLMap;
import com.github.thiagotgm.bot_utils.storage.xml.translate.XMLString;
import com.github.thiagotgm.bot_utils.utils.Utils;

/**
 * Unit tests for {@link XMLMap}.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-09-11
 */
public class XMLMapTest {
	
	private static final Map<String,Integer> EXPECTED = new HashMap<>( 3 );

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		
		EXPECTED.put( "hi", 2523 );
		EXPECTED.put( "thing", -33 );
		EXPECTED.put( "aMap", 0 );
		
	}
	
	/**
	 * Reads a map from a resource.
	 * 
	 * @param resource The path to the resource.
	 * @param translator The translator to use.
	 * @param <K> The type of the map keys.
	 * @param <V> The type of the map values.
	 * @return The map in the resource.
	 * @throws XMLStreamException if an error occurred.
	 */
	private <K,V> Map<K,V> readResource( String resource, XMLMap<K,V> translator )
			throws XMLStreamException {
		
		InputStream in = this.getClass().getResourceAsStream( resource );
		return Utils.readXMLDocument( in, translator );
		
	}

	@Test
	public void testRead() throws XMLStreamException {

        @SuppressWarnings("unchecked")
		Map<String,Integer> map = readResource( "/storage/xml/translate/Map.xml",
        		new XMLMap<>( (Class<Map<String,Integer>>) (Class<?>) HashMap.class,
        		new XMLString(), new XMLInteger() ) );
        
        assertEquals( EXPECTED, map, "Read map is not correct." );
		
	}
	
	@Test
    @SuppressWarnings("unchecked")
    public void testWrite() throws XMLStreamException, IOException {
        
        XMLTestHelper.testReadWrite( EXPECTED, new XMLMap<>
        		( (Class<Map<String,Integer>>) (Class<?>) HashMap.class, new XMLString(),
        		new XMLInteger() ) );
        
    }
	
	@Test
	public void testException() {
		
		@SuppressWarnings("unchecked")
		XMLMap<String,Integer> translator = new XMLMap<>(
				(Class<Map<String,Integer>>) (Class<?>) HashMap.class, new XMLString(), new XMLInteger() );
		
		try {
			readResource( "/storage/xml/translate/MapNoKey.xml", translator );
			fail( "Should have thrown an exception." );
		} catch ( XMLStreamException e ) {
			// Expected.
		}
		
		try {
			readResource( "/storage/xml/translate/MapNoValue.xml", translator );
			fail( "Should have thrown an exception." );
		} catch ( XMLStreamException e ) {
			// Expected.
		}
		
		try {
			readResource( "/storage/xml/translate/MapTwoKeys.xml", translator );
			fail( "Should have thrown an exception." );
		} catch ( XMLStreamException e ) {
			// Expected.
		}
		
		try {
			readResource( "/storage/xml/translate/MapTwoValues.xml", translator );
			fail( "Should have thrown an exception." );
		} catch ( XMLStreamException e ) {
			// Expected.
		}
		
	}

}
