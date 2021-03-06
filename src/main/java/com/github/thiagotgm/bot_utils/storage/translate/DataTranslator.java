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
import com.github.thiagotgm.bot_utils.storage.TranslationException;
import com.github.thiagotgm.bot_utils.storage.Translator;

/**
 * Translator for Data objects that just acts as a pass-through.
 * <p>
 * However, if {@link #toData(Data)} is given <tt>null</tt> as
 * argument, the return will be a {@link Data#nullData() NULL-valued}
 * Data instead of <tt>null</tt> itself. It will <b>not</b> be unwrapped
 * when calling {@link #fromData(Data)} afterwards.
 * 
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-08-30
 */
public class DataTranslator implements Translator<Data> {

	@Override
	public Data toData( Data obj ) throws TranslationException {
		
		return obj == null ? Data.nullData() : obj;
		
	}

	@Override
	public Data fromData( Data data ) throws TranslationException {
		
		return data;
		
	}

}
