/*
 * 'bewsoftware-version-plugin' provides Maven style version number
 * incrementing.
 *
 * Copyright (C) 2021 Bradley Willcott <mailto:bw.opensource@yahoo.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bewsoftware.mojo.version;

import com.bewsoftware.utils.struct.StringReturn;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import static java.util.regex.Pattern.compile;

/**
 * Utils class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 0.1
 */
public final class Utils {

    /**
     * Name of file to operate on.
     */
    private static final String FILE_NAME = "pom.xml";

    /**
     * RegEx string for Maven style version numbering.
     */
    private static final String MAVEN = "^(?<major>0|[1-9]\\d*)"
                                        + "(\\."
                                        + "(?<minor>0|[1-9]\\d*)"
                                        + "(\\."
                                        + "(?<patch>0|[1-9]\\d*))?)?"
                                        + "(?<snapshot>-SNAPSHOT)?$";

    /**
     * Update build number component of the Project Version number in the 'pom.xml' file.
     * <p>
     * Also update whether or not there is to be a suffix: '-SNAPSHOT'.
     *
     * @param callback       Goal callback.
     * @param log            Maven logging.
     * @param oldVersion     To hold the old Project Version text to be returned.
     * @param projectVersion To hold the new Project Version text to be returned.
     *
     * @return {@code true} if version text changed, {@code false} otherwise.
     *
     * @throws MojoExecutionException if any.
     * @throws MojoFailureException   if any.
     */
    public static boolean processPom(final Callback callback, final Log log,
                                     final StringReturn oldVersion,
                                     final StringReturn projectVersion)
            throws MojoExecutionException, MojoFailureException {

        log.debug("Entry: processPom()");
        boolean rtn = false;

        File inputFile = new File(FILE_NAME);
        SAXReader reader = new SAXReader();
        Document document = null;

        // Read file.
        try
        {
            document = reader.read(inputFile);
        } catch (DocumentException ex)
        {
            throw new MojoExecutionException("\nError reading from '" + FILE_NAME + "'", ex);
        }

        // root is 'project'.
        Element versionElement = document.getRootElement().element("version");
        oldVersion.val = versionElement.getTextTrim();

        log.debug("\n/project/version : " + oldVersion.val);

        // Prepare regex.
        Matcher m = compile(MAVEN).matcher(oldVersion.val);

        String output = "";

        // Process found data.
        if (m.find())
        {
            Version version = new Version();
            version.major = m.group("major");
            version.minor = m.group("minor");
            version.patch = m.group("patch");
            version.snapshot = m.group("snapshot");

            log.debug(version.toString());

            // If changed, update file.
            if (callback.processVersion(version))
            {
                // Build new version text string.
                output = version.format();
                log.debug("output : " + output);

                versionElement.setText(output);

                try
                {
                    XMLWriter writer = new XMLWriter(
                            Files.newBufferedWriter(inputFile.toPath(),
                                                    StandardOpenOption.WRITE,
                                                    StandardOpenOption.TRUNCATE_EXISTING));
                    writer.write(document);
                    writer.flush();
                    writer.close();
                } catch (IOException ex)
                {
                    throw new MojoExecutionException("\nError writing to '" + FILE_NAME + "'", ex);
                }

                rtn = true;
            }
        } else
        {
            throw new MojoFailureException("\nWarning: No valid version text found: " + oldVersion.val);
        }

        projectVersion.val = output;
        log.info("project.version: " + (!output.isBlank() ? output : oldVersion.val));
        log.debug("Exit: processPom()");
        return rtn;
    }

    /**
     * Update the /project/build/finalName.
     *
     * @param finalName  Current text.
     * @param oldVersion Old version text.
     * @param newVersion New version text.
     * @param log        Maven logging.
     *
     * @return new finalName text.
     */
    public static String updateFinalName(final String finalName, final String oldVersion,
                                         final String newVersion, final Log log) {

        log.debug("Entry: updateFinalName()");
        log.debug("projectVersion: " + newVersion);

        String regex = "(?<text>" + oldVersion + ")";
        StringBuilder sb = new StringBuilder();

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(finalName);

        if (m.find())
        {
            m.appendReplacement(sb, newVersion);
        } else
        {
            log.warn("WARNING: project.build.finalName - Not Updated");
        }

        m.appendTail(sb);
        log.debug("sb:\n" + sb);

        log.debug("Exit: updateFinalName()");
        return sb.toString();
    }

    /**
     * Not meant to be instantiated.
     */
    private Utils() {
    }
}
