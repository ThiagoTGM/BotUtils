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

package com.github.thiagotgm.bot_utils.storage.translate;

import com.github.thiagotgm.bot_utils.storage.Data;
import com.github.thiagotgm.bot_utils.storage.Translator;

/**
 * Translator for Strings.
 * 
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-07-23
 */
public class StringTranslator implements Translator<String> {

	@Override
	public Data toData( String obj ) throws TranslationException {

		return Data.stringData( obj );
		
	}
	
	/**
	 * Simply returns the given string.
	 */
	@Override
	public String encode( String obj ) {
		
		return obj;
		
	}

	@Override
	public String fromData( Data data ) throws TranslationException {
		
		if ( data.isNull() ) {
			return null; // Null instance.
		}

		if ( !data.isString() ) {
			throw new TranslationException( "Given data is not a String." );
		}
		
		return data.getString();
		
	}
	
	/**
	 * Simply returns the given string.
	 */
	@Override
	public String decode( String str ) {
		
		return str;
		
	}

}
