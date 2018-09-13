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

package com.github.thiagotgm.bot_utils.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thiagotgm.bot_utils.storage.xml.XMLElement;
import com.github.thiagotgm.bot_utils.storage.xml.XMLTranslator;
import com.github.thiagotgm.bot_utils.utils.graph.Graph;
import com.github.thiagotgm.bot_utils.utils.graph.Tree;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.ICategory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IIDLinkedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.IVoiceState;
import sx.blah.discord.handle.obj.IWebhook;

/**
 * General purpose utilities.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-08-18
 */
public abstract class Utils {
    
    private static final Logger LOG = LoggerFactory.getLogger( Utils.class );
    
    /* Serializable-String methods */
    
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();

    /**
     * Encodes a Serializable into a String.
     *
     * @param obj The object to be encoded.
     * @return The String with the encoded object, or null if the encoding failed.
     * @see #stringToSerializable(String)
     */
    public static String serializableToString( Serializable obj ) {
        
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream( bytes ).writeObject( obj );
        } catch ( IOException e ) {
            LOG.error( "Error on encoding Serializable to String.", e );
            return null;
        }
        return encoder.encodeToString( bytes.toByteArray() );
        
    }
    
    /**
     * Decodes a Serializable from a String.
     *
     * @param str The String to decode.
     * @param <T> Type of the object being deserialized.
     * @return The decoded object, or null if decoding failed.
     */
    public static <T extends Serializable> T stringToSerializable( String str ) {
        
        ByteArrayInputStream bytes;
        bytes = new ByteArrayInputStream( decoder.decode( str ) );
        try {
            @SuppressWarnings( "unchecked" )
            T decoded = (T) new ObjectInputStream( bytes ).readObject();
            return decoded;
        } catch ( ClassNotFoundException e ) {
            LOG.error( "Class of encoded Serializable not found.", e );
        } catch ( IOException e ) {
            LOG.error( "Error on decoding Serializable from String.", e );
            e.printStackTrace();
        } catch ( ClassCastException e ) {
            LOG.error( "Deserialized object is not of the expected type.", e );
        }
        return null; // Error encountered.
        
    }
    
    /**
     * Encodes an object into a String. The object <i>must</i> be {@link Serializable}.
     *
     * @param obj The object to be encoded.
     * @return A String with the encoded object.
     * @throws NotSerializableException if the object does not implement the
     *                                  java.io.Serializable interface.
     */
    public static String encode( Object obj ) throws NotSerializableException {
        
        if ( obj instanceof Serializable ) {
            return serializableToString( (Serializable) obj );
        } else {
            throw new NotSerializableException( obj.getClass().getName() );
        }
        
    }
    
    /**
     * Decodes an object that was encoded with {@link #encode(Object)}.
     *
     * @param str The String with the encoded object.
     * @param <T> Type of the object being decoded.
     * @return The decoded object, or null if decoding failed.
     */
    public static <T> T decode( String str ) {
        
        try {
            @SuppressWarnings( "unchecked" )
            T obj = (T) stringToSerializable( str );
            return obj;
        } catch ( ClassCastException e ) {
            LOG.error( "Decoded object is not of the expected type.", e );
        }
        return null;
        
    }
    
    /* XML writing/reading methods */
    
    /**
     * Character encoding used by default for reading and writing XML streams.
     */
    public static final String DEFAULT_ENCODING = "UTF-8";
    
    /**
     * Writes an XML document to a stream, where the content of the document is the given object,
     * translated using the given XML translator.
     *
     * @param out The stream to write to.
     * @param content The content of the document to be written.
     * @param translator The translator to use to encode the content.
     * @param encoding The character encoding to use.
     * @param <T> The type of object to be encoded.
     * @throws XMLStreamException if an error is encountered while writing.
     */
    public static <T> void writeXMLDocument( OutputStream out, T content, XMLTranslator<T> translator,
    		String encoding ) throws XMLStreamException {
    	
    	XMLStreamWriter outStream = XMLOutputFactory.newFactory().createXMLStreamWriter( out, encoding );
        outStream.writeStartDocument();
        translator.write( outStream, content );
        outStream.writeEndDocument();
    	
    }
    
    /**
     * Writes an XML document to a stream, where the content of the document is the given object,
     * translated using the given XML translator. Uses the
     * {@link #DEFAULT_ENCODING default character encoding}.
     * <p>
     * Same as calling {@link #writeXMLDocument(OutputStream, Object, XMLTranslator, String)
     * writeXMLDocument(OutputStream, T, XMLTranslator, String)} with
     * fourth parameter {@value #DEFAULT_ENCODING}.
     *
     * @param out The stream to write to.
     * @param content The content of the document to be written.
     * @param translator The translator to use to encode the content.
     * @param <T> The type of object to be encoded.
     * @throws XMLStreamException if an error is encountered while writing.
     */
    public static <T> void writeXMLDocument( OutputStream out, T content, XMLTranslator<T> translator )
    		throws XMLStreamException {
    	
    	writeXMLDocument( out, content, translator, DEFAULT_ENCODING );
    	
    }
    
    /**
     * Writes an XML document to a stream, where the content of the document is the XMLElement
     * given.
     *
     * @param out The stream to write to.
     * @param content The content of the document to be written.
     * @param encoding The character encoding to use.
     * @throws XMLStreamException if an error is encountered while writing.
     */
    public static void writeXMLDocument( OutputStream out, XMLElement content, String encoding )
            throws XMLStreamException {
        
        writeXMLDocument( out, content, (XMLElement.Translator<XMLElement>) () -> {
        	
        	return null;
        	
        }, encoding );
        
    }
    
    /**
     * Writes an XML document to a stream, where the content of the document is the XMLElement
     * given. Uses the {@link #DEFAULT_ENCODING default character encoding}.
     * <p>
     * Same as calling {@link #writeXMLDocument(OutputStream, XMLElement, String)} with third parameter
     * {@value #DEFAULT_ENCODING}.
     *
     * @param out The stream to write to.
     * @param content The content of the document to be written.
     * @throws XMLStreamException if an error is encountered while writing.
     */
    public static void writeXMLDocument( OutputStream out, XMLElement content ) throws XMLStreamException {
        
        writeXMLDocument( out, content, DEFAULT_ENCODING );
        
    }
    
    /**
     * Reads an XML document from a stream, using the given translator the decode the content.
     * <p>
     * If there is any content after what is read by the translator, that extra content
     * is ignored. This means that the stream will always be read until the end of the document
     * is found.
     *
     * @param in The stream to read to.
     * @param translator The translator that will decode the document's content.
     * @param encoding The character encoding of the stream.
     * @param <T> The type of element that will be read.
     * @return The read element.
     * @throws XMLStreamException if an error is encountered while reading.
     */
    public static <T> T readXMLDocument( InputStream in, XMLTranslator<T> translator, String encoding )
            throws XMLStreamException {
        
        XMLStreamReader inStream = XMLInputFactory.newFactory().createXMLStreamReader( in, encoding );
        while ( inStream.next() != XMLStreamConstants.START_ELEMENT ) {} // Skip comments.
        T content = translator.read( inStream );
        while ( inStream.hasNext() ) { inStream.next(); } // Go to end of document.
        
        return content;
        
    }
    
    /**
     * Reads an XML document from a stream, using the given translator the decode the content. 
     * Uses the {@link #DEFAULT_ENCODING default character encoding}.
     * <p>
     * Same as calling {@link #readXMLDocument(InputStream, XMLTranslator, String)} with third parameter
     * {@value #DEFAULT_ENCODING}.
     * <p>
     * If there is any content after what is read by the translator, that extra content
     * is ignored. This means that the stream will always be read until the end of the document
     * is found.
     *
     * @param in The stream to read to.
     * @param translator The translator that will decode the document's content.
     * @param <T> The type of element that will be read.
     * @return The read element.
     * @throws XMLStreamException if an error is encountered while reading.
     */
    public static <T> T readXMLDocument( InputStream in, XMLTranslator<T> translator )
            throws XMLStreamException {
    	
    	return readXMLDocument( in, translator, DEFAULT_ENCODING );
    	
    }
    
    /**
     * Reads an XML document from a stream, using the given translator the decode the content.
     * <p>
     * If there is any content after what is read by the translator, that extra content
     * is ignored. This means that the stream will always be read until the end of the document
     * is found.
     * <p>
     * Convenience method to allow the use of lambdas for the translator of an XML element.
     *
     * @param in The stream to read to.
     * @param translator The translator that will decode the document's content.
     * @param encoding The character encoding of the stream.
     * @param <T> The type of XML element that will be read.
     * @return The read element.
     * @throws XMLStreamException if an error is encountered while reading.
     */
    public static <T extends XMLElement> T readXMLDocument( InputStream in, XMLElement.Translator<T> translator,
    		String encoding ) throws XMLStreamException {
    	
    	return readXMLDocument( in, (XMLTranslator<T>) translator, encoding );
    	
    }
    
    /**
     * Reads an XML document from a stream, using the given translator the decode the content. 
     * Uses the {@link #DEFAULT_ENCODING default character encoding}.
     * <p>
     * Same as calling {@link #readXMLDocument(InputStream, XMLTranslator, String)} with third parameter
     * {@value #DEFAULT_ENCODING}.
     * <p>
     * If there is any content after what is read by the translator, that extra content
     * is ignored. This means that the stream will always be read until the end of the document
     * is found.
     * <p>
     * Convenience method to allow the use of lambdas for the translator of an XML element.
     *
     * @param in The stream to read to.
     * @param translator The translator that will decode the document's content.
     * @param <T> The type of XML element that will be read.
     * @return The read element.
     * @throws XMLStreamException if an error is encountered while reading.
     */
    public static <T extends XMLElement> T readXMLDocument( InputStream in, XMLElement.Translator<T> translator )
            throws XMLStreamException {
    	
    	return readXMLDocument( in, (XMLTranslator<T>) translator, DEFAULT_ENCODING );
    	
    }
    
    /**
     * Reads an XML document from a stream, where the content of the document is to be read
     * by the given XMLElement.
     * <p>
     * If there is any content after what is read by the given XMLElement, that extra content
     * is ignored. This means that the stream will always be read until the end of the document
     * is found.
     *
     * @param in The stream to read to.
     * @param content The element that will read the document's content.
     * @param encoding The character encoding of the stream.
     * @param <T> The type of the XML element.
     * @return The read element (the <tt>content</tt> argument).
     * @throws XMLStreamException if an error is encountered while reading.
     */
    public static <T extends XMLElement> T readXMLDocument( InputStream in, T content, String encoding )
            throws XMLStreamException {
        
        return readXMLDocument( in, (XMLElement.Translator<T>) () -> {
        	
        	return content;
        	
        }, encoding );
        
    }
    
    /**
     * Reads an XML document from a stream, where the content of the document is to be read
     * by the given XMLElement. Uses the {@link #DEFAULT_ENCODING default character encoding}.
     * <p>
     * Same as calling {@link #readXMLDocument(InputStream, XMLTranslator, String)} with
     * third parameter {@value #DEFAULT_ENCODING}.
     * <p>
     * If there is any content after what is read by the given XMLElement, that extra content
     * is ignored. This means that the stream will always be read until the end of the document
     * is found.
     *
     * @param in The stream to read to.
     * @param content The element that will read the document's content.
     * @param <T> The type of the XML element.
     * @return The read element (the <tt>content</tt> argument).
     * @throws XMLStreamException if an error is encountered while reading.
     */
    public static <T extends XMLElement> T readXMLDocument( InputStream in, T content )
    		throws XMLStreamException {
        
        return readXMLDocument( in, content, DEFAULT_ENCODING );
        
    }
    
    /* General purpose stuff */
    
    /**
     * Creates a string that represents the given object, using the type
     * of the object (user, guild, channel, etc) and its ID.
     * 
     * @param obj The object to get an ID string for.
     * @return The ID string.
     */
    public static String idString( IIDLinkedObject obj ) {
    	
    	StringBuilder builder = new StringBuilder();
    	
    	// Add identifier for object type.
    	if ( obj instanceof ICategory ) {
    		builder.append( "category" );
    	} else if ( obj instanceof IPrivateChannel ) {
    		builder.append( "privateChannel" );
    	} else if ( obj instanceof IVoiceChannel ) {
    		builder.append( "voiceChannel" );
    	} else if ( obj instanceof IChannel ) {
    		builder.append( "channel" );
    	} else if ( obj instanceof IEmoji ) {
    		builder.append( "emoji" );
    	} else if ( obj instanceof IGuild ) {
    		builder.append( "guild" );
    	} else if ( obj instanceof IMessage ) {
    		builder.append( "message" );
    	} else if ( obj instanceof IRole ) {
    		builder.append( "role" );
    	} else if ( obj instanceof IUser ) {
    		builder.append( "user" );
    	} else if ( obj instanceof IVoiceState ) {
    		builder.append( "voiceState" );
    	} else if ( obj instanceof IWebhook ) {
    		builder.append( "webhook" );
    	} else {
    		builder.append( '?' );
    	}
    	
    	builder.append( '#' ); // Add separator.
    	
    	builder.append( obj.getStringID() ); // Add ID.
    	
    	return builder.toString();
    	
    }
    
    /**
     * Creates ID strings for an array of objects.
     * 
     * @param arr The array of objects to get ID strings for.
     * @param <T> The type of the objects in the array.
     * @return The array with the ID strings for each object.
     * @see #idString(IIDLinkedObject)
     */
    public static <T extends IIDLinkedObject> String[] idString( T[] arr ) {
    	
    	String[] newArr = new String[arr.length];
    	
    	for ( int i = 0; i < arr.length; i++ ) {
    		
    		newArr[i] = idString( arr[i] ); // Get id string for each element.
    		
    	}
    	
    	return newArr;
    	
    }    
    
    /**
     * Character used to mark the beginning of special expressions in
     * sanitized strings.
     */
    public static final char SPECIAL_CHARACTER = '&';
    /**
     * Needs to exist so javadoc stops fucking over itself.
     */
    private static final String SPECIAL_CHARACTER_S = SPECIAL_CHARACTER + "";
    /**
     * Expression that marks an occurrence of {@value #SPECIAL_CHARACTER} in a 
     * sanitized string.
     */
    public static final String SPECIAL_CHARACTER_MARKER = "&amp;";
    /**
     * Character used to join lists of strings.
     */
    public static final char SEPARATOR = ':';
    /**
     * Did it for {@link #SPECIAL_CHARACTER}, might as well keep it
     * consistent.
     */
    private static final String SEPARATOR_S = SEPARATOR + "";
    /**
     * Expression that marks an occurrence of {@value #SEPARATOR} in a 
     * sanitized string.
     */
    public static final String SEPARATOR_MARKER = "&cln;";
    /**
     * Expression that marks that a sanitized string is the empty string.
     */
    public static final String EMPTY_MARKER = "&empty;";
    /**
     * Expression that marks that a sanitized string is <tt>null</tt>.
     */
    public static final String NULL_MARKER = "&null;";
    
    /**
     * Sanitizes a string, converting typical edge cases (<tt>null</tt>, empty
     * strings) into normal strings, and encoding occurrences of other special characters
     * ({@value #SPECIAL_CHARACTER}, {@value #SEPARATOR}).
     * <p>
     * The original string may be obtained afterwards using {@link #desanitize(String)}.
     * 
     * @param str The string to sanitize.
     * @return The sanitized string.
     */
    public static String sanitize( String str ) {
    	
    	if ( str == null ) { // Null element.
			return NULL_MARKER;
		} else if ( str.isEmpty() ) { // Empty string.
			return EMPTY_MARKER;
		} else { // Regular string.
    		return str.replace( SPECIAL_CHARACTER_S, SPECIAL_CHARACTER_MARKER ) // Replace special char.
    				  .replace( SEPARATOR_S, SEPARATOR_MARKER ); // Replace separator.
		}
    	
    }
    
    /**
     * Desanitizes a string, obtaining the original string that
     * was given to {@link #sanitize(String)}.
     * 
     * @param str The sanitized string.
     * @return The original string.
     */
    public static String desanitize( String str ) {
    	
    	if ( str.equals( NULL_MARKER ) ) { // Null element.
			return null;
		} else if ( str.equals( EMPTY_MARKER ) ) { // Empty string.
			return "";
		} else { // Regular string.
			return str.replace( SEPARATOR_MARKER, SEPARATOR_S ) // Restore separator.
					  .replace( SPECIAL_CHARACTER_MARKER, SPECIAL_CHARACTER_S ); // Restore special char.
		}
    	
    }
    
    /**
     * Encodes a list of strings into a single string, that can later be decoded using
     * {@link #decode(String)}.
     * <p>
     * Each element of the list is sanitized using {@link #sanitize(String)} before
     * encoding.
     * 
     * @param list The list to be encoded.
     * @return The encoded version of the list.
     */
    public static String encodeList( List<String> list ) {
    	
    	List<String> sanitized = new ArrayList<>( list.size() );
    	for ( String elem : list ) { // Sanitize each element of the list.
    		
    		sanitized.add( sanitize( elem ) );
    		
    	}
    	return String.join( SEPARATOR_S, sanitized ); // Join sanitized strings.
    	
    }
    
    /**
     * Decodes a string that represents a list, as encoded by {@link #encodeList(List)}.
     * 
     * @param str The encoded version of the list.
     * @return The decoded list.
     */
    public static List<String> decodeList( String str ) {
    	
    	List<String> sanitized = Arrays.asList( str.split( SEPARATOR_S ) ); // Split sanitized strings.
    	List<String> list = new ArrayList<>( sanitized.size() );
    	for ( String elem : sanitized ) { // Un-sanitize each element.
    		
    		list.add( desanitize( elem ) );
    		
    	}
    	return list;
    	
    }
    
    private static final Pattern USER_PATTERN = Pattern.compile( "(.+)#(\\d{4})" );
    
    /**
     * Parses a user from a string in the format <tt>[Name]#[Discriminator]</tt>.
     * 
     * @param str The string to parse.
     * @param client The client to use.
     * @return The user specified in <tt>str</tt>, or <tt>null</tt> if the
     *         format of the string was invalid or no user was found with
     *         that name and discriminator.
     */
    public static IUser getUser( String str, IDiscordClient client ) {
    	
    	Matcher match = USER_PATTERN.matcher( str );
		if ( !match.matches() ) {
			return null; // Did not match format.
		}
		
		String name = match.group( 1 );
		String discriminator = match.group( 2 );
		for ( IUser option : client.getUsersByName( name ) ) {
			// Look for user with the right name and discriminator.
			if ( option.getDiscriminator().matches( discriminator ) ) {
				return option; // Found user.
			}
			
		}
		return null; // Didn't find user.
    	
    }
    
    /* Special wrappers for Graphs */
    
    // Unmodifiable wrappers.
    
    /**
     * Wrapper for a graph that passes through most operations, but throws an exception on operations
     * that modify the graph.
     * 
     * @author ThiagoTGM
	 * @version 1.0
	 * @since 2018-09-07
     * @param <K> Type of keys in a path of the graph.
     * @param <V> Type of values stored in the graph.
     */
    private static class UnmodifiableGraph<K,V> implements Graph<K,V>, Serializable {
    	
    	/**
		 * UID that represents this class.
		 */
		private static final long serialVersionUID = -6974332055825955444L;
		
		private final Graph<K,V> backing;
    	
    	/**
    	 * Creates an instance that wraps the given graph.
    	 * 
    	 * @param backing The graph to be wrapped.
    	 */
    	public UnmodifiableGraph( Graph<K,V> backing ) {
    		
    		this.backing = backing;
    		
    	}
    	
    	@Override
    	public boolean containsPath( List<K> path ) {
    		
    		return backing.containsPath( path );
    		
    	}

		@Override
		public boolean containsValue( V value ) {

			return backing.containsValue( value );
			
		}

		@Override
		public V get( List<K> path ) throws IllegalArgumentException {

			return backing.get( path );
			
		}

		@Override
		public List<V> getAll( List<K> path ) throws IllegalArgumentException {

			return backing.getAll( path );
			
		}

		@Override
		public V set( V value, List<K> path )
				throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

			throw new UnsupportedOperationException( "Graph is unmodifiable." );
			
		}

		@Override
		public boolean add( V value, List<K> path )
				throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

			throw new UnsupportedOperationException( "Graph is unmodifiable." );
			
		}

		@Override
		public V remove( List<K> path ) throws UnsupportedOperationException, IllegalArgumentException {

			throw new UnsupportedOperationException( "Graph is unmodifiable." );
			
		}

		@Override
		public Set<List<K>> pathSet() {

			return Collections.unmodifiableSet( backing.pathSet() );
			
		}

		@Override
		public Collection<V> values() {
			
			return Collections.unmodifiableCollection( backing.values() );
			
		}

		@Override
		public Set<Entry<K, V>> entrySet() {

			return Collections.unmodifiableSet( backing.entrySet() );
			
		}

		@Override
		public int size() {

			return backing.size();
			
		}

		@Override
		public void clear() {

			backing.clear();
			
		}
    	
    }
    
    /**
     * Wrapper for a tree that passes through most operations, but throws an exception on operations
     * that modify the tree.
     * 
     * @author ThiagoTGM
	 * @version 1.0
	 * @since 2018-09-07
     * @param <K> Type of keys in a path of the tree.
     * @param <V> Type of values stored in the tree.
     */
    private static class UnmodifiableTree<K,V> extends UnmodifiableGraph<K,V> implements Tree<K,V> {
    	
    	/**
		 * UID that represents this class.
		 */
		private static final long serialVersionUID = -6137572584756055579L;

		/**
    	 * Creates an instance that wraps the given tree.
    	 * 
    	 * @param backing The tree to be wrapped.
    	 */
    	public UnmodifiableTree( Tree<K,V> backing ) {
    		
    		super( backing );
    		
    	}
    	
    }
    
    /**
     * Returns an unmodifiable view of the specified graph. This method allows modules to provide
     * users with "read-only" access to internal graphs. Query operations on the returned graph
     * "read through" to the specified graph, and attempts to modify the returned graph, whether
     * direct or via its collection views, result in an UnsupportedOperationException.
     * <p>
     * The returned graph will be serializable if the specified graph is serializable.
     * 
     * @param <K> The type of keys that make a path in the graph.
     * @param <V> The type of values stored in the graph.
     * @param g The graph for which an unmodifiable view is to be returned.
     * @return An unmodifiable view of the specified graph.
     * @throws NullPointerException if the given graph is <tt>null</tt>.
     */
    public static <K,V> Graph<K,V> unmodifiableGraph( Graph<K,V> g ) throws NullPointerException {
    	
    	if ( g == null ) {
    		throw new NullPointerException( "Argument cannot be null." );
    	}
    	
    	return new UnmodifiableGraph<>( g );
    	
    }
    
    /**
     * Returns an unmodifiable view of the specified tree. This method allows modules to provide
     * users with "read-only" access to internal trees. Query operations on the returned tree
     * "read through" to the specified tree, and attempts to modify the returned tree, whether
     * direct or via its collection views, result in an UnsupportedOperationException.
     * <p>
     * The returned tree will be serializable if the specified tree is serializable.
     * 
     * @param <K> The type of keys that make a path in the tree.
     * @param <V> The type of values stored in the tree.
     * @param t The tree for which an unmodifiable view is to be returned.
     * @return An unmodifiable view of the specified tree.
     * @throws NullPointerException if the given tree is <tt>null</tt>.
     */
    public static <K,V> Tree<K,V> unmodifiableTree( Tree<K,V> t ) throws NullPointerException {
    	
    	if ( t == null ) {
    		throw new NullPointerException( "Argument cannot be null." );
    	}
    	
    	return new UnmodifiableTree<>( t );
    	
    }
    
    // Synchronized wrappers.
    
    /**
     * Wrapper for a graph that passes through all operations, and synchronizes
     * all method calls.
     * 
     * @author ThiagoTGM
	 * @version 1.0
	 * @since 2018-09-07
     * @param <K> Type of keys in a path of the graph.
     * @param <V> Type of values stored in the graph.
     */
    private static class SynchronizedGraph<K,V> implements Graph<K,V>, Serializable {
    	
		/**
		 * UID that represents this class.
		 */
		private static final long serialVersionUID = -6618016289702403440L;
		
		private final Graph<K,V> backing;
    	
    	/**
    	 * Creates an instance that wraps the given graph.
    	 * 
    	 * @param backing The graph to be wrapped.
    	 */
    	public SynchronizedGraph( Graph<K,V> backing ) {
    		
    		this.backing = backing;
    		
    	}
    	
    	@Override
    	public synchronized boolean containsPath( List<K> path ) {
    		
    		return backing.containsPath( path );
    		
    	}

		@Override
		public synchronized boolean containsValue( V value ) {

			return backing.containsValue( value );
			
		}

		@Override
		public synchronized V get( List<K> path ) throws IllegalArgumentException {

			return backing.get( path );
			
		}

		@Override
		public synchronized List<V> getAll( List<K> path ) throws IllegalArgumentException {

			return backing.getAll( path );
			
		}

		@Override
		public synchronized V set( V value, List<K> path )
				throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

			return backing.set( value, path );
			
		}

		@Override
		public synchronized boolean add( V value, List<K> path )
				throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

			return backing.add( value, path );
			
		}

		@Override
		public synchronized V remove( List<K> path ) throws UnsupportedOperationException, IllegalArgumentException {

			return backing.remove( path );
			
		}

		@Override
		public synchronized Set<List<K>> pathSet() {

			return Collections.synchronizedSet( backing.pathSet() );
			
		}

		@Override
		public synchronized Collection<V> values() {
			
			return Collections.synchronizedCollection( backing.values() );
			
		}

		@Override
		public synchronized Set<Entry<K, V>> entrySet() {

			return Collections.synchronizedSet( backing.entrySet() );
			
		}

		@Override
		public synchronized int size() {

			return backing.size();
			
		}

		@Override
		public synchronized void clear() {

			backing.clear();
			
		}
    	
    }
    
    /**
     * Wrapper for a tree that passes through all operations, and synchronizes
     * all method calls.
     * 
     * @author ThiagoTGM
	 * @version 1.0
	 * @since 2018-09-07
     * @param <K> Type of keys in a path of the tree.
     * @param <V> Type of values stored in the tree.
     */
    private static class SynchronizedTree<K,V> extends SynchronizedGraph<K,V> implements Tree<K,V> {
    	
    	/**
		 * UID that represents this class.
		 */
		private static final long serialVersionUID = -4615164564699994857L;

		/**
    	 * Creates an instance that wraps the given tree.
    	 * 
    	 * @param backing The tree to be wrapped.
    	 */
    	public SynchronizedTree( Tree<K,V> backing ) {
    		
    		super( backing );
    		
    	}
    	
    }
    
    /**
     * Returns a synchronized (thread-safe) graph backed by the specified graph. In order to
     * guarantee serial access, it is critical that <b>all</b> access to the backing graph is
     * accomplished through the returned graph.
     * <p>
     * It is imperative that the user manually synchronize on the returned graph when
     * iterating over any of its collection views: 
     * <pre>
     *  Graph g = Utils.synchronizedGraph(new TreeGraph());
     *      ...
     *  Set s = g.pathSet();  // Needn't be in synchronized block
     *      ...
     *  synchronized (g) {  // Synchronizing on g, not s!
     *      Iterator i = s.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *  }
     * </pre>
     * Failure to follow this advice may result in non-deterministic behavior. 
     * <p>
     * The returned graph will be serializable if the specified graph is serializable.
     * 
     * @param <K> The type of keys that make a path in the graph.
     * @param <V> The type of values stored in the graph.
     * @param g The graph to be "wrapped" in a synchronized graph.
     * @return A synchronized view of the specified graph.
     * @throws NullPointerException if the given graph is <tt>null</tt>.
     */
    public static <K,V> Graph<K,V> synchronizedGraph( Graph<K,V> g ) throws NullPointerException {
    	
    	if ( g == null ) {
    		throw new NullPointerException( "Argument cannot be null." );
    	}
    	
    	return new SynchronizedGraph<>( g );
    	
    }
    
    /**
     * Returns a synchronized (thread-safe) tree backed by the specified tree. In order to
     * guarantee serial access, it is critical that <b>all</b> access to the backing tree is
     * accomplished through the returned tree.
     * <p>
     * It is imperative that the user manually synchronize on the returned tree when
     * iterating over any of its collection views: 
     * <pre>
     *  Tree t = Utils.synchronizedTree(new TreeGraph());
     *      ...
     *  Set s = t.pathSet();  // Needn't be in synchronized block
     *      ...
     *  synchronized (t) {  // Synchronizing on t, not s!
     *      Iterator i = s.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *  }
     * </pre>
     * Failure to follow this advice may result in non-deterministic behavior. 
     * <p>
     * The returned tree will be serializable if the specified tree is serializable.
     * 
     * @param <K> The type of keys that make a path in the tree.
     * @param <V> The type of values stored in the tree.
     * @param t The tree to be "wrapped" in a synchronized tree.
     * @return A synchronized view of the specified tree.
     * @throws NullPointerException if the given tree is <tt>null</tt>.
     */
    public static <K,V> Tree<K,V> synchronizedTree( Tree<K,V> t ) throws NullPointerException {
    	
    	if ( t == null ) {
    		throw new NullPointerException( "Argument cannot be null." );
    	}
    	
    	return new SynchronizedTree<>( t );
    	
    }

}
