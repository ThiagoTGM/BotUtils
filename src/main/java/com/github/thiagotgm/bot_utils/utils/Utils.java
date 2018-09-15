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
import java.util.List;
import java.util.Map;
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
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

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
     * @param obj
     *            The object to be encoded.
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
     * @param str
     *            The String to decode.
     * @param <T>
     *            Type of the object being deserialized.
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
     * Encodes an object into a String. The object <i>must</i> be
     * {@link Serializable}.
     *
     * @param obj
     *            The object to be encoded.
     * @return A String with the encoded object.
     * @throws NotSerializableException
     *             if the object does not implement the java.io.Serializable
     *             interface.
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
     * @param str
     *            The String with the encoded object.
     * @param <T>
     *            Type of the object being decoded.
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
     * Writes an XML document to a stream, where the content of the document is the
     * given object, translated using the given XML translator.
     *
     * @param out
     *            The stream to write to.
     * @param content
     *            The content of the document to be written.
     * @param translator
     *            The translator to use to encode the content.
     * @param encoding
     *            The character encoding to use.
     * @param <T>
     *            The type of object to be encoded.
     * @throws XMLStreamException
     *             if an error is encountered while writing.
     */
    public static <T> void writeXMLDocument( OutputStream out, T content, XMLTranslator<T> translator, String encoding )
            throws XMLStreamException {

        XMLStreamWriter outStream = XMLOutputFactory.newFactory().createXMLStreamWriter( out, encoding );
        outStream.writeStartDocument();
        translator.write( outStream, content );
        outStream.writeEndDocument();

    }

    /**
     * Writes an XML document to a stream, where the content of the document is the
     * given object, translated using the given XML translator. Uses the
     * {@link #DEFAULT_ENCODING default character encoding}.
     * <p>
     * Same as calling
     * {@link #writeXMLDocument(OutputStream, Object, XMLTranslator, String)
     * writeXMLDocument(OutputStream, T, XMLTranslator, String)} with fourth
     * parameter {@value #DEFAULT_ENCODING}.
     *
     * @param out
     *            The stream to write to.
     * @param content
     *            The content of the document to be written.
     * @param translator
     *            The translator to use to encode the content.
     * @param <T>
     *            The type of object to be encoded.
     * @throws XMLStreamException
     *             if an error is encountered while writing.
     */
    public static <T> void writeXMLDocument( OutputStream out, T content, XMLTranslator<T> translator )
            throws XMLStreamException {

        writeXMLDocument( out, content, translator, DEFAULT_ENCODING );

    }

    /**
     * Writes an XML document to a stream, where the content of the document is the
     * XMLElement given.
     *
     * @param out
     *            The stream to write to.
     * @param content
     *            The content of the document to be written.
     * @param encoding
     *            The character encoding to use.
     * @throws XMLStreamException
     *             if an error is encountered while writing.
     */
    public static void writeXMLDocument( OutputStream out, XMLElement content, String encoding )
            throws XMLStreamException {

        writeXMLDocument( out, content, (XMLElement.Translator<XMLElement>) () -> {

            return null;

        }, encoding );

    }

    /**
     * Writes an XML document to a stream, where the content of the document is the
     * XMLElement given. Uses the {@link #DEFAULT_ENCODING default character
     * encoding}.
     * <p>
     * Same as calling {@link #writeXMLDocument(OutputStream, XMLElement, String)}
     * with third parameter {@value #DEFAULT_ENCODING}.
     *
     * @param out
     *            The stream to write to.
     * @param content
     *            The content of the document to be written.
     * @throws XMLStreamException
     *             if an error is encountered while writing.
     */
    public static void writeXMLDocument( OutputStream out, XMLElement content ) throws XMLStreamException {

        writeXMLDocument( out, content, DEFAULT_ENCODING );

    }

    /**
     * Reads an XML document from a stream, using the given translator the decode
     * the content.
     * <p>
     * If there is any content after what is read by the translator, that extra
     * content is ignored. This means that the stream will always be read until the
     * end of the document is found.
     *
     * @param in
     *            The stream to read to.
     * @param translator
     *            The translator that will decode the document's content.
     * @param encoding
     *            The character encoding of the stream.
     * @param <T>
     *            The type of element that will be read.
     * @return The read element.
     * @throws XMLStreamException
     *             if an error is encountered while reading.
     */
    public static <T> T readXMLDocument( InputStream in, XMLTranslator<T> translator, String encoding )
            throws XMLStreamException {

        XMLStreamReader inStream = XMLInputFactory.newFactory().createXMLStreamReader( in, encoding );
        while ( inStream.next() != XMLStreamConstants.START_ELEMENT ) {
        } // Skip comments.
        T content = translator.read( inStream );
        while ( inStream.hasNext() ) {
            inStream.next();
        } // Go to end of document.

        return content;

    }

    /**
     * Reads an XML document from a stream, using the given translator the decode
     * the content. Uses the {@link #DEFAULT_ENCODING default character encoding}.
     * <p>
     * Same as calling {@link #readXMLDocument(InputStream, XMLTranslator, String)}
     * with third parameter {@value #DEFAULT_ENCODING}.
     * <p>
     * If there is any content after what is read by the translator, that extra
     * content is ignored. This means that the stream will always be read until the
     * end of the document is found.
     *
     * @param in
     *            The stream to read to.
     * @param translator
     *            The translator that will decode the document's content.
     * @param <T>
     *            The type of element that will be read.
     * @return The read element.
     * @throws XMLStreamException
     *             if an error is encountered while reading.
     */
    public static <T> T readXMLDocument( InputStream in, XMLTranslator<T> translator ) throws XMLStreamException {

        return readXMLDocument( in, translator, DEFAULT_ENCODING );

    }

    /**
     * Reads an XML document from a stream, using the given translator the decode
     * the content.
     * <p>
     * If there is any content after what is read by the translator, that extra
     * content is ignored. This means that the stream will always be read until the
     * end of the document is found.
     * <p>
     * Convenience method to allow the use of lambdas for the translator of an XML
     * element.
     *
     * @param in
     *            The stream to read to.
     * @param translator
     *            The translator that will decode the document's content.
     * @param encoding
     *            The character encoding of the stream.
     * @param <T>
     *            The type of XML element that will be read.
     * @return The read element.
     * @throws XMLStreamException
     *             if an error is encountered while reading.
     */
    public static <T extends XMLElement> T readXMLDocument( InputStream in, XMLElement.Translator<T> translator,
            String encoding ) throws XMLStreamException {

        return readXMLDocument( in, (XMLTranslator<T>) translator, encoding );

    }

    /**
     * Reads an XML document from a stream, using the given translator the decode
     * the content. Uses the {@link #DEFAULT_ENCODING default character encoding}.
     * <p>
     * Same as calling {@link #readXMLDocument(InputStream, XMLTranslator, String)}
     * with third parameter {@value #DEFAULT_ENCODING}.
     * <p>
     * If there is any content after what is read by the translator, that extra
     * content is ignored. This means that the stream will always be read until the
     * end of the document is found.
     * <p>
     * Convenience method to allow the use of lambdas for the translator of an XML
     * element.
     *
     * @param in
     *            The stream to read to.
     * @param translator
     *            The translator that will decode the document's content.
     * @param <T>
     *            The type of XML element that will be read.
     * @return The read element.
     * @throws XMLStreamException
     *             if an error is encountered while reading.
     */
    public static <T extends XMLElement> T readXMLDocument( InputStream in, XMLElement.Translator<T> translator )
            throws XMLStreamException {

        return readXMLDocument( in, (XMLTranslator<T>) translator, DEFAULT_ENCODING );

    }

    /**
     * Reads an XML document from a stream, where the content of the document is to
     * be read by the given XMLElement.
     * <p>
     * If there is any content after what is read by the given XMLElement, that
     * extra content is ignored. This means that the stream will always be read
     * until the end of the document is found.
     *
     * @param in
     *            The stream to read to.
     * @param content
     *            The element that will read the document's content.
     * @param encoding
     *            The character encoding of the stream.
     * @param <T>
     *            The type of the XML element.
     * @return The read element (the <tt>content</tt> argument).
     * @throws XMLStreamException
     *             if an error is encountered while reading.
     */
    public static <T extends XMLElement> T readXMLDocument( InputStream in, T content, String encoding )
            throws XMLStreamException {

        return readXMLDocument( in, (XMLElement.Translator<T>) () -> {

            return content;

        }, encoding );

    }

    /**
     * Reads an XML document from a stream, where the content of the document is to
     * be read by the given XMLElement. Uses the {@link #DEFAULT_ENCODING default
     * character encoding}.
     * <p>
     * Same as calling {@link #readXMLDocument(InputStream, XMLTranslator, String)}
     * with third parameter {@value #DEFAULT_ENCODING}.
     * <p>
     * If there is any content after what is read by the given XMLElement, that
     * extra content is ignored. This means that the stream will always be read
     * until the end of the document is found.
     *
     * @param in
     *            The stream to read to.
     * @param content
     *            The element that will read the document's content.
     * @param <T>
     *            The type of the XML element.
     * @return The read element (the <tt>content</tt> argument).
     * @throws XMLStreamException
     *             if an error is encountered while reading.
     */
    public static <T extends XMLElement> T readXMLDocument( InputStream in, T content ) throws XMLStreamException {

        return readXMLDocument( in, content, DEFAULT_ENCODING );

    }

    /* General purpose stuff */

    /**
     * Creates a string that represents the given object, using the type of the
     * object (user, guild, channel, etc) and its ID.
     * 
     * @param obj
     *            The object to get an ID string for.
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
     * @param arr
     *            The array of objects to get ID strings for.
     * @param <T>
     *            The type of the objects in the array.
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
     * Character used to mark the beginning of special expressions in sanitized
     * strings.
     */
    public static final char EXPRESSION_CHARACTER = '&';
    /**
     * Needs to exist so javadoc stops fucking over itself.
     */
    private static final String EXPRESSION_CHARACTER_S = EXPRESSION_CHARACTER + "";
    /**
     * Expression that marks an occurrence of {@value #EXPRESSION_CHARACTER} in a
     * sanitized string.
     */
    public static final String EXPRESSION_CHARACTER_MARKER = "&amp;";
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
     * strings) into normal strings, and encoding occurrences of the special
     * character ({@value #EXPRESSION_CHARACTER}).
     * <p>
     * The original string may be obtained afterwards using
     * {@link #desanitize(String)}.
     * 
     * @param str
     *            The string to sanitize.
     * @return The sanitized string.
     */
    public static String sanitize( String str ) {

        if ( str == null ) { // Null element.
            return NULL_MARKER;
        } else if ( str.isEmpty() ) { // Empty string.
            return EMPTY_MARKER;
        } else { // Regular string.
            return str.replace( EXPRESSION_CHARACTER_S, EXPRESSION_CHARACTER_MARKER ); // Replace expression char.
        }

    }

    /**
     * Sanitizes a string, converting typical edge cases (<tt>null</tt>, empty
     * strings) into normal strings, encoding occurrences of the special character
     * ({@value #EXPRESSION_CHARACTER}), and encoding occurrences of the given
     * characters with their mapped expressions.
     * <p>
     * The original string may be obtained afterwards using
     * {@link #desanitize(String, BiMap)} with the same expression map.
     * <p>
     * <b>NOTE:</b> The special characters will be encoded in the order that they
     * are returned by the iterator of the given map's entry set. Upon calling
     * {@link #desanitize(String, BiMap)}, they will be decoded in the same order,
     * so using expressions that contain special characters should be avoided.
     * 
     * @param str
     *            The string to sanitize.
     * @param specialExpressions
     *            The map that indicates what the special characters are and the
     *            expression that each of them should be encoded as. The format of
     *            an expression should be similar to
     *            {@value #EXPRESSION_CHARACTER_MARKER}: a
     *            '{@value #EXPRESSION_CHARACTER}', followed by a shortened name
     *            (only lowercase letters), followed by a ';'.
     * @return The sanitized string.
     */
    public static String sanitize( String str, BiMap<Character, String> specialExpressions ) {

        String sanitized = sanitize( str );
        if ( sanitized.equals( NULL_MARKER ) || sanitized.equals( EMPTY_MARKER ) ) {
            return sanitized; // Edge case.
        }

        for ( Map.Entry<Character, String> expression : specialExpressions.entrySet() ) {

            sanitized = sanitized.replaceAll( String.valueOf( expression.getKey() ), expression.getValue() );

        }
        return sanitized;

    }

    /**
     * Desanitizes a string, obtaining the original string that was given to
     * {@link #sanitize(String)}.
     * 
     * @param str
     *            The sanitized string.
     * @return The original string.
     */
    public static String desanitize( String str ) {

        if ( str.equals( NULL_MARKER ) ) { // Null element.
            return null;
        } else if ( str.equals( EMPTY_MARKER ) ) { // Empty string.
            return "";
        } else { // Regular string.
            return str.replace( EXPRESSION_CHARACTER_MARKER, EXPRESSION_CHARACTER_S ); // Restore expression char.
        }

    }

    /**
     * Desanitizes a string, obtaining the original string that was given to
     * {@link #sanitize(String,BiMap)}.
     * <p>
     * <b>NOTE:</b> The special characters will be decoded in the order that they
     * are returned by the iterator of the given map's entry set.
     * 
     * @param str
     *            The sanitized string.
     * @param specialExpressions
     *            The map that indicates what the special characters are and the
     *            expression that each of them should be encoded as. This should
     *            contain the same mappings as the one used to sanitize.
     * @return The original string.
     */
    public static String desanitize( String str, BiMap<Character, String> specialExpressions ) {

        if ( str.equals( NULL_MARKER ) || str.equals( EMPTY_MARKER ) ) {
            return desanitize( str ); // Edge case.
        }

        String sanitized = str;

        for ( Map.Entry<Character, String> expression : specialExpressions.entrySet() ) {

            sanitized = sanitized.replaceAll( expression.getValue(), String.valueOf( expression.getKey() ) );

        }
        return desanitize( sanitized );

    }

    /**
     * Character used to join lists of strings.
     */
    public static final char SEPARATOR = ':';
    /**
     * To use in String methods.
     */
    private static final String SEPARATOR_S = SEPARATOR + "";
    /**
     * Expression that marks an occurrence of {@value #SEPARATOR} in a sanitized
     * string.
     */
    public static final String SEPARATOR_MARKER = "&cln;";
    private static final BiMap<Character, String> JOIN_SPECIAL_CHARACTERS = ImmutableBiMap.<Character, String>builder()
            .put( SEPARATOR, SEPARATOR_MARKER ).build();

    /**
     * Encodes a list of strings into a single string, that can later be decoded
     * using {@link #decode(String)}.
     * <p>
     * Each element of the list is sanitized using {@link #sanitize(String)} before
     * encoding.
     * 
     * @param list
     *            The list to be encoded.
     * @return The encoded version of the list.
     */
    public static String encodeList( List<String> list ) {

        List<String> sanitized = new ArrayList<>( list.size() );
        for ( String elem : list ) { // Sanitize each element of the list.

            sanitized.add( sanitize( elem, JOIN_SPECIAL_CHARACTERS ) );

        }
        return String.join( SEPARATOR_S, sanitized ); // Join sanitized strings.

    }

    /**
     * Decodes a string that represents a list, as encoded by
     * {@link #encodeList(List)}.
     * 
     * @param str
     *            The encoded version of the list.
     * @return The decoded list.
     */
    public static List<String> decodeList( String str ) {

        List<String> sanitized = Arrays.asList( str.split( SEPARATOR_S ) ); // Split sanitized strings.
        List<String> list = new ArrayList<>( sanitized.size() );
        for ( String elem : sanitized ) { // Un-sanitize each element.

            list.add( desanitize( elem, JOIN_SPECIAL_CHARACTERS ) );

        }
        return list;

    }

    private static final Pattern USER_PATTERN = Pattern.compile( "(.+)#(\\d{4})" );

    /**
     * Parses a user from a string in the format <tt>[Name]#[Discriminator]</tt>.
     * 
     * @param str
     *            The string to parse.
     * @param client
     *            The client to use.
     * @return The user specified in <tt>str</tt>, or <tt>null</tt> if the format of
     *         the string was invalid or no user was found with that name and
     *         discriminator.
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

}
