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

package com.github.thiagotgm.bot_utils.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Translates (converts) objects of a certain type to a JSON-compatible format
 * or a string, and vice-versa.
 * <p>
 * Whether <tt>null</tt> instances can be encoded and decoded is up to the
 * implementation.
 * 
 * @version 2.0
 * @author ThiagoTGM
 * @since 2018-07-16
 * @param <T> The type of object to be translated.
 */
public interface Translator<T> {
	
	/**
	 * Used to convert Data to strings (and vice versa) in the default implementations of
	 * {@link #encode(Object) encode(T)} and {@link #decode(String)}.
	 */
	static final Gson GSON = new GsonBuilder().serializeNulls()
			.registerTypeAdapter( Data.class, new Data.DataAdapter() ).create();
	
	/**
	 * Converts the given object into a Data format.
	 * 
	 * @param obj The object to be encoded.
	 * @return The data encoding of the object.
	 * @throws TranslationException if an error was encountered while encoding.
	 * @throws NullPointerException if <tt>obj</tt> is <tt>null</tt> and the
	 *                              implementation in use does not support
	 *                              <tt>null</tt> instances.
	 */
	Data toData( T obj ) throws TranslationException, NullPointerException;
	
	/**
	 * Attempts to convert the given object into a Data format. If the given
	 * object is not of the type that can be translated by this Translator,
	 * returns <tt>null</tt>.
	 * 
	 * @param obj The object to be encoded.
	 * @return The Data encoding of the object, or <tt>null</tt> if the given
	 *         object is not of the supported type.
	 * @throws TranslationException if an error was encountered while encoding.
	 * @throws NullPointerException if <tt>obj</tt> is <tt>null</tt> and the
	 *                              implementation in use does not support
	 *                              <tt>null</tt> instances.
	 */
	@SuppressWarnings("unchecked")
	default Data objToData( Object obj ) throws TranslationException, NullPointerException {
		
		try {
			return toData( (T) obj ); // Attempt to translate.
		} catch ( ClassCastException e ) {
			return null; // Not the correct type.
		}
		
	}
	
	/**
	 * Converts the given object into a String format.
	 * <p>
	 * By default, this just encodes the return of {@link #toData(Object) toData(T)}
	 * into a JSON format. If this is overriden, {@link #decode(String)} must be
	 * overriden as well.
	 * 
	 * @param obj The object to be encoded.
	 * @return The String encoding of the object.
	 * @throws TranslationException if an error was encountered while encoding.
	 * @throws NullPointerException if <tt>obj</tt> is <tt>null</tt> and the
	 *                              implementation in use does not support
	 *                              <tt>null</tt> instances.
	 */
	default String encode( T obj ) throws TranslationException, NullPointerException {
		
		return GSON.toJson( toData( obj ) );
		
	}
	
	/**
	 * Attempts to convert the given object into a String format. If the given
	 * object is not of the type that can be translated by this Translator,
	 * returns <tt>null</tt>.
	 * 
	 * @param obj The object to be encoded.
	 * @return The String encoding of the object, or <tt>null</tt> if the given
	 *         object is not of the supported type.
	 * @throws TranslationException if an error was encountered while encoding.
	 */
	@SuppressWarnings("unchecked")
	default String encodeObj( Object obj ) throws TranslationException {
		
		try {
			return encode( (T) obj ); // Attempt to translate.
		} catch ( ClassCastException e ) {
			return null; // Not the correct type.
		}
		
	}
	
	/**
	 * Restores an object from data created using {@link #toData(Object)}.
	 * 
	 * @param data The data to be decoded.
	 * @return The translated object.
	 * @throws TranslationException if an error was encountered while decoding.
	 * @throws NullPointerException if the given data is <tt>null</tt>.
	 */
	T fromData( Data data ) throws TranslationException, NullPointerException;
	
	/**
	 * Restores an object from a String created using {@link #encode(Object)}.
	 * 
	 * @param str The string to be decoded.
	 * @return The translated object.
	 * @throws TranslationException if an error was encountered while decoding.
	 * @throws NullPointerException if the given string is <tt>null</tt>.
	 */
	default T decode( String str ) throws TranslationException, NullPointerException {
		
		if ( str == null ) {
			throw new NullPointerException( "String cannot be null." );
		}
		
		return fromData( GSON.fromJson( str, Data.class ) );
		
	}

}
