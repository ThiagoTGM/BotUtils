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

import static org.junit.jupiter.api.Assumptions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import com.github.thiagotgm.bot_utils.storage.Database;

/**
 * Unit tests for {@link XMLDatabase}.
 *
 * @version 2.0
 * @author ThiagoTGM
 * @since 2018-08-30
 */
@DisplayName( "XML database test" )
public class XMLDatabaseTest extends DatabaseTest {

    private static final String PATH = "test";

    private static Database db;

    /**
     * Loads the database before testing starts.
     */
    @BeforeAll
    public static void load() {

        Database db = new XMLDatabase();
        assumeTrue( db.load( Arrays.asList( PATH ) ) );
        XMLDatabaseTest.db = db;

    }

    /**
     * Closes the database after all tests are done.
     * 
     * @throws IOException
     *             if an error happened while deleting the temporary storage folder.
     */
    @AfterAll
    public static void close() throws IOException {

        if ( db != null ) {
            db.close();
            db = null;
        }
        Files.deleteIfExists( Paths.get( PATH ) );

    }

    @Override
    protected Database getDatabase() {

        return db;

    }

    @Override
    protected boolean closeAfterTest() {

        return false;

    }

}
