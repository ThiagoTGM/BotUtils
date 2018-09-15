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

/**
 * Exception that indicates that an error occurred while encoding or decoding a value.
 * 
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-08-29
 */
public class TranslationException extends RuntimeException {
	
	/**
	 * UID that represents this class.
	 */
	private static final long serialVersionUID = 1126112129198492514L;

	/**
	 * Constructs a new translation exception with no cause.
	 * 
	 * @see RuntimeException#RuntimeException()
     */
	public TranslationException() {
		
		super();

	}

	/**
	 * Constructs a new translation exception with the given detail message and cause.
	 * 
	 * @param message The detail message.
	 * @param cause The cause of this exception.
	 * @see RuntimeException#RuntimeException(String, Throwable)
	 */
	public TranslationException( String message, Throwable cause ) {
		
		super( message, cause );
		
	}

	/**
	 * Constructs a new translation exception with the given detail message and no cause.
	 * 
	 * @param message The detail message.
	 * @see RuntimeException#RuntimeException(String)
	 */
	public TranslationException( String message ) {
		
		super( message );

	}

	/**
	 * Constructs a new translation exception with the given cause.
	 * 
	 * @param cause The cause of this exception.
	 * @see RuntimeException#RuntimeException(Throwable)
	 */
	public TranslationException( Throwable cause ) {
		
		super( cause );

	}
	
}