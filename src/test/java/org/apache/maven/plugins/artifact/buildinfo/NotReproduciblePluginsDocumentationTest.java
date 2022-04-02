package org.apache.maven.plugins.artifact.buildinfo;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.FileUtils;

/**
 * Test class to update src/site/apt/plugin-issues.apt with content extracted from
 * src/main/resources/org/apache/maven/plugins/artifact/buildinfo/not-reproducible-plugins.properties
 */
public class NotReproduciblePluginsDocumentationTest
{
    private static final String LS = System.lineSeparator();
    private static final String DELIMITER = "~~ content generated by NotReproduciblePluginsDocumentationTest";

    public void testBasic() throws IOException
    {
        File pluginIssuesApt = new File( "src/site/apt/plugin-issues.apt" );
        String content = FileUtils.fileRead( pluginIssuesApt, "UTF-8" );
        content = content.substring( 0, content.indexOf( DELIMITER ) + DELIMITER.length() );

        StringBuilder sb = new StringBuilder( content );
        sb.append( LS );
        sb.append( "*---------+-------------------------------------------------------------------+-------+--------------+" + LS );
        sb.append( "|  | <<plugin>>                                                 | <<minimum version>> | <<comments>>" );
        String groupId = null;
        for ( String line : FileUtils.loadFile( new File( "src/main/resources/org/apache/maven/plugins/artifact/buildinfo/not-reproducible-plugins.properties" ) ) )
        {
            if ( !line.startsWith( "#" ) )
            {
                sb.append( LS + "*--------+--------------------------------------------------------------------+-------+--------------+" + LS );
                int index = line.indexOf( '=' );
                String plugin = line.substring( 0, index );
                String status = line.substring( index + 1 );

                index = plugin.indexOf( '+' );
                if ( index < 0 )
                {
                    groupId = "org.apache.maven.plugins";
                    sb.append( "| org.apache.maven.plugins | {{{/plugins/" + plugin + "/}" + plugin + "}} " );
                }
                else
                {
                    groupId = plugin.substring( 0, index );
                    plugin = plugin.substring( index + 1 );
                    sb.append( "| " + groupId + " | " + plugin + " " );
                }
                if ( status.startsWith( "fail:" ) )
                {
                    sb.append( "| - | no fixed release available, see {{{" + status.substring( 5 ) + "}reference}}" );
                }
                else
                {
                    sb.append( "| " + status + " | ");
                }
                continue;
            }
            if ( groupId == null )
            {
                continue;
            }
            sb.append( line.substring( 1 ) );
        }
        sb.append( LS + "*----------+------------------------------------------------------------------+-------+--------------+" + LS );

        FileUtils.fileWrite( pluginIssuesApt, "UTF-8", sb.toString() );
    }
}
