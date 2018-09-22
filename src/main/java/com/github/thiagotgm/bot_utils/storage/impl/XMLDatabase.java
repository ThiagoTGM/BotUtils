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

package com.github.thiagotgm.bot_utils.storage.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thiagotgm.bot_utils.SaveManager;
import com.github.thiagotgm.bot_utils.SaveManager.Saveable;
import com.github.thiagotgm.bot_utils.storage.TranslationException;
import com.github.thiagotgm.bot_utils.storage.Translator;
import com.github.thiagotgm.bot_utils.storage.xml.XMLElement;
import com.github.thiagotgm.bot_utils.storage.xml.XMLTranslator;
import com.github.thiagotgm.bot_utils.storage.xml.translate.XMLData;
import com.github.thiagotgm.bot_utils.storage.xml.translate.XMLMap;
import com.github.thiagotgm.bot_utils.utils.Utils;
import com.github.thiagotgm.bot_utils.utils.graph.Tree;
import com.github.thiagotgm.bot_utils.utils.graph.XMLHashTree;

/**
 * Database that saves data in local XML files.
 * 
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-07-16
 */
public class XMLDatabase extends AbstractDatabase implements Saveable {
	
	@SuppressWarnings("rawtypes")
	private static final Class<? extends Map> MAP_CLASS = HashMap.class;
	
	private static final List<Parameter> loadParams = Collections.unmodifiableList(
			Arrays.asList( new Parameter( "Directory path" ) ) );
	private static final Logger LOG = LoggerFactory.getLogger( XMLDatabase.class );
	
	/**
	 * Path were database files are stored.
	 */
	private Path path;
	/**
	 * Trees managed by this database.
	 */
	private final Map<String,XMLEntry> storage = new HashMap<>();
	
	/**
	 * Determines the file that stores data under the given name.
	 *
	 * @param dataName The name the data is stored under.
	 * @return The storage file.
	 */
	private File getFile( String dataName ) {
        
	    String filename = dataName + ".xml";
        return path.resolve( filename ).toFile();
        
    }
	
	@Override
	public List<Parameter> getLoadParams() {

		return new ArrayList<>( loadParams );
		
	}

	/**
	 * Loads the database. The only argument is the path where the data files are stored.
	 */
	@Override
	public synchronized boolean load( List<String> args ) throws IllegalStateException, IllegalArgumentException {

		if ( loaded ) {
			throw new IllegalStateException( "Database is already loaded." );
		}
		
		if ( args.size() != loadParams.size() ) {
			throw new IllegalStateException( "Parameter list size does not match expectation." );
		}
		
		LOG.info( "Loading database." );
		
		path = Paths.get( args.get( 0 ) );
		
		LOG.debug( "Requested path: {}", path );
		
		if ( path.toFile().exists() ) { // Path already exists.
            if ( !path.toFile().isDirectory() ) { // Check if path is directory.
                LOG.error( "Database path is not a directory." );
                return false;
            }
        } else { // Create path.
            try {
                Files.createDirectories( path );
            } catch ( IOException e ) {
                LOG.error( "Could not create database directory.", e );
                return false;
            }
        }
		
		LOG.info( "Database path: {}", path );
		
		loaded = true;
		
		SaveManager.registerListener( this ); // Register for autosave events.
		
		return true;
		
	}

	@Override
	public synchronized void close() throws IllegalStateException {
		
		LOG.info( "Closing database." );
		
		SaveManager.unregisterListener( this ); // Unregister for autosave events.

		save(); // Save state.
		
		closed = true;
		
	}
	
	private <T> XMLTranslator<T> getXMLTranslator( Translator<T> translator ) {
		
		if ( translator instanceof com.github.thiagotgm.bot_utils.storage.translate.XMLTranslator ) {
			return ( (com.github.thiagotgm.bot_utils.storage.translate.XMLTranslator<T>) translator )
					.getXMLTranslator();
		} else {
			return new CompoundTranslator<>( translator );
		}
		
	}
	
	/**
	 * Loads data stored under the given name using the given XML element.
	 * 
	 * @param dataName The name the data is registered under.
	 * @param element The element to be load data into.
	 * @return The entry that represents the loaded data.
	 * @throws DatabaseException if an error occurred while loading.
	 */
	private XMLEntry load( String dataName, XMLElement element ) throws DatabaseException {
		
	    File file = getFile( dataName );
		LOG.debug( "Loading file {}.", file.getPath() );
		if ( file.exists() ) {
			FileInputStream in;
			try {
				in = new FileInputStream( file );
				Utils.readXMLDocument( in, element );
			} catch ( FileNotFoundException | XMLStreamException e ) {
				throw new DatabaseException( "Could not read data file.", e );
			}
		}
		return new XMLEntry( dataName, element );
		
	}
	
	/**
	 * Removes the data stored under the given name. Prevents it from being autosaved in the
	 * future, and deletes the existing file if one exists.
	 *
	 * @param dataName The name that the data is stored under.
	 */
	private void delete( String dataName ) {
	    
	    if ( storage.remove( dataName ) == null ) {
	        LOG.error( "Attempted to delete nonexistent data '{}'.", dataName );
	        return; // Data not found.
	    }
	    File file = getFile( dataName );
        LOG.debug( "Deleting file {}.", file.getPath() );
        try {
            Files.deleteIfExists( path ); // Try to delete file.
        } catch ( IOException e ) {
            LOG.error( "Could not delete data file.", e );
        }
	    
	}

	@Override
	protected synchronized <K,V> Tree<K,V> newTree( String dataName, Translator<K> keyTranslator,
			Translator<V> valueTranslator ) throws DatabaseException {

		// Get XML translators.
		XMLTranslator<K> keyXMLTranslator = getXMLTranslator( keyTranslator );
		XMLTranslator<V> valueXMLTranslator = getXMLTranslator( valueTranslator );
		
		// Instantiate tree.
		XMLHashTree<K,V> tree = new XMLHashTree<>( keyXMLTranslator, valueXMLTranslator );
		
		// Load and register tree.
		storage.put( dataName, load( dataName, tree ) );
		
		return tree;
		
	}
	
	@Override
	protected void deleteTree( String treeName ) {
	    
	    delete( treeName );
	    
	}

	@Override
	protected synchronized <K,V> Map<K, V> newMap( String dataName, Translator<K> keyTranslator,
			Translator<V> valueTranslator ) throws DatabaseException {
		
		// Get XML translators.
		XMLTranslator<K> keyXMLTranslator = getXMLTranslator( keyTranslator );
		XMLTranslator<V> valueXMLTranslator = getXMLTranslator( valueTranslator );
		
		@SuppressWarnings("unchecked") // Instantiate map.
		Class<? extends Map<K,V>> mapClass = (Class<? extends Map<K,V>>) MAP_CLASS;
		XMLMap.WrappedMap<K,V> map = new XMLMap.WrappedMap<>( mapClass, keyXMLTranslator, valueXMLTranslator );
		
		// Load and register map.
		storage.put( dataName, load( dataName, map ) );
		
		return map;
		
	}
	
	@Override
    protected void deleteMap( String mapName ) {
        
        delete( mapName );
        
    }
	
	@Override
	public synchronized void save() {
		
		if ( closed ) {
			return; // Already closed, abort.
		}
		
		LOG.info( "Saving database files." );
		
		for ( XMLEntry data : storage.values() ) { // Save each storage element.
			
	        File file = getFile( data.getName() );
			LOG.debug( "Saving file {}.", file.getPath() );
			try {
				FileOutputStream out = new FileOutputStream( file );
				Utils.writeXMLDocument( out, data.getElement() );
				out.close();
			} catch ( FileNotFoundException e1 ) {
				LOG.error( "Could not open database file " + file.toString() + ".", e1 );
			} catch ( XMLStreamException e2 ) {
				LOG.error( "Could not save database file " + file.toString() + ".", e2 );
			} catch ( IOException e3 ) {
				LOG.error( "Could not close database file " + file.toString() + ".", e3 );
			}
			
		}
		
	}
	
	/* Full translator for XML */
	
	/**
	 * Compound translator that cascade a T-Data translator with a
	 * Data-XML translator to translate to XML format objects that
	 * do not have an XML translator, but have a Data translator.
	 * 
	 * @version 1.1
	 * @author ThiagoTGM
	 * @since 2018-07-27
	 * @param <T> The type of object that is to be translated.
	 */
	public class CompoundTranslator<T> implements XMLTranslator<T> {
		
		/**
		 * UID that represents this class.
		 */
		private static final long serialVersionUID = 7676473726689961837L;
		
		private final Translator<T> dataTranslator;
		private final XMLData xmlTranslator;
		
		/**
		 * Initializes a cascaded translator that uses the given translator
		 * and a {@link XMLData Data-XML translator}.
		 * 
		 * @param dataTranslator The translator to encode objects with.
		 */
		public CompoundTranslator( Translator<T> dataTranslator ) {
			
			this.dataTranslator = dataTranslator;
			this.xmlTranslator = new XMLData();
			
		}

		@Override
		public T read( XMLStreamReader in ) throws XMLStreamException {

			try {
				return dataTranslator.fromData( xmlTranslator.read( in ) );
			} catch ( TranslationException e ) {
				throw new XMLStreamException( "Could not translate data.", e );
			}
			
		}

		@Override
		public void write( XMLStreamWriter out, T instance ) throws XMLStreamException {

			try {
				xmlTranslator.write( out, dataTranslator.toData( instance ) );
			} catch ( TranslationException e ) {
				throw new XMLStreamException( "Could not translate data.", e );
			}
			
		}
		
	}
	
	/**
	 * Database entry for XML storage elements.
	 * 
	 * @version 1.0
	 * @author ThiagoTGM
	 * @since 2018-08-08
	 */
	private class XMLEntry {
		
		private final String name;
		private final XMLElement element;
		
		/**
		 * Initializes an XML entry for the given storage element, under the given name.
		 * 
		 * @param name The name that the storage element is registered under.
		 * @param element The storage element.
		 * @throws NullPointerException If any of the arguments is <tt>null</tt>.
		 */
		public XMLEntry( String name, XMLElement element ) throws NullPointerException {
			
			if ( ( name == null ) || ( element == null ) ) {
				throw new NullPointerException( "Arguments cannot be null." );
			}
			
			this.name = name;
			this.element = element;
			
		}
		
		/**
		 * Retrieves the name of this storage element.
		 * 
		 * @return The name.
		 */
		public String getName() {
			
			return name;
			
		}
		
		/**
		 * Retrieves the storage element this represents.
		 * 
		 * @return The element.
		 */
		public XMLElement getElement() {
			
			return element;
			
		}
		
	}
	
}
