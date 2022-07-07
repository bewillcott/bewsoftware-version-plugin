/*
 * 'bewsoftware-version-plugin' provides Maven style version number
 * incrementing.
 *
 * Copyright (C) 2021-2022 Bradley Willcott <mailto:bw.opensource@yahoo.com>
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

import com.bewsoftware.utils.io.ConsoleIO;
import com.bewsoftware.utils.io.Display;
import com.bewsoftware.utils.struct.Ref;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import static java.nio.file.Path.of;
import static java.util.regex.Pattern.compile;

/**
 * This class contain static helper methods.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.1.0
 */
public final class Utils
{
    private static final Display DISPLAY = ConsoleIO.consoleDisplay("> ");

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
     * Not meant to be instantiated.
     */
    private Utils()
    {
    }

    /**
     * Set the verbosity of the information being displayed.
     *
     * @param verbosity new level (range 0..3)
     */
    public static void setVerbosity(int verbosity)
    {
        if (verbosity > 0)
        {
            DISPLAY.debugLevel(verbosity);
        }
    }

    /**
     * Update build number component of the Project Version number in the
     * 'pom.xml' file.
     * <p>
     * Also update whether or not there is to be a suffix: '-SNAPSHOT'.
     *
     * @param callback   Goal callback.
     * @param oldVersion To hold the old Project Version text to be
     *                   returned.
     * @param newVersion To hold the new Project Version text to be
     *                   returned.
     *
     * @return {@code true} if version text changed, {@code false} otherwise.
     *
     * @throws MojoExecutionException if any.
     * @throws MojoFailureException   if any.
     */
    public static boolean processPom(
            final Callback callback,
            final Ref<String> oldVersion,
            final Ref<String> newVersion
    )
            throws MojoExecutionException, MojoFailureException
    {
        DISPLAY.level(2).println("Entry: processPom()");
        boolean changed = false;

        Ref<File> inputFile = Ref.val();
        Document document = loadDocument(FILE_NAME, inputFile);

        if (document != null)
        {
            changed = processVersion(document, oldVersion, callback,
                    inputFile, newVersion);
            processModules(document, newVersion);

        }

        DISPLAY.level(2).println("Exit: processPom()");

        return changed;
    }

    /**
     * Update the /project/build/finalName.<br>
     * Updated text returned in {@code finalName}.
     *
     * @param finalName  Current text.
     * @param oldVersion Old version text.
     * @param newVersion New version text.
     */
    public static void updateFinalName(
            final Ref<String> finalName,
            final Ref<String> oldVersion,
            final Ref<String> newVersion
    )
    {

        DISPLAY.level(3).println("Entry: updateFinalName()");
        DISPLAY.level(3).println("projectVersion: " + newVersion);

        String regex = "(?<text>" + oldVersion + ")";
        StringBuilder sb = new StringBuilder();

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(finalName.val);

        if (m.find())
        {
            m.appendReplacement(sb, newVersion.val);
        } else
        {
            DISPLAY.level(1).println("WARNING: project.build.finalName - Not Updated");
        }

        m.appendTail(sb);
        DISPLAY.level(3).println("sb:\n" + sb);

        DISPLAY.level(3).println("Exit: updateFinalName()");
        finalName.val = sb.toString();
    }

    /**
     * Update "project.version".
     *
     * @param project    the Maven project
     * @param oldVersion old version text
     * @param newVersion new version text
     * @param finalName  /build/finalName text
     */
    public static void updateProjectVersion(
            final MavenProject project,
            final Ref<String> oldVersion,
            final Ref<String> newVersion,
            final Ref<String> finalName
    )
    {
        DISPLAY.level(3).appendln("Entry: updateProjectVersion()");

        DISPLAY.level(3).println("\n[OLD] project.artifact().getVersion(): "
                + project.getArtifact().getVersion());

        project.getArtifact().selectVersion(newVersion.val);

        DISPLAY.level(3).appendln("[NEW] project.artifact().getVersion(): "
                + project.getArtifact().getVersion() + "\n");
        DISPLAY.level(3).println("[OLD] project.getModel().getVersion(): "
                + project.getModel().getVersion());

        project.getModel().setVersion(newVersion.val);

        DISPLAY.level(3).appendln("[NEW] project.getModel().getVersion(): "
                + project.getModel().getVersion() + "\n");

        // Final name of files
        DISPLAY.level(3).println("[OLD] theProject.getBuild().getFinalName(): "
                + project.getBuild().getFinalName());

        // Update /project/build/finalName.
        updateFinalName(finalName, oldVersion, newVersion);
        project.getBuild().setFinalName(finalName.val);

        DISPLAY.level(3).appendln("[NEW] theProject.getBuild().getFinalName(): "
                + project.getBuild().getFinalName());
        DISPLAY.level(3).println("Exit: updateProjectVersion()");
    }

    /**
     * Load a pom.xml file into a Document object.
     *
     * @param filename  of pom.xml file to load
     * @param inputFile File object that was loaded
     *
     * @return new document object
     *
     * @throws MojoExecutionException if any
     */
    private static Document loadDocument(final String filename, final Ref<File> inputFile)
            throws MojoExecutionException
    {
        Document document = null;
        inputFile.val = new File(filename);
        SAXReader reader = new SAXReader();

        // Read file.
        try
        {
            document = reader.read(inputFile.val);
        } catch (DocumentException ex)
        {
            throw new MojoExecutionException("\nError reading from '" + filename + "'", ex);
        }

        return document;
    }

    /**
     * Process the data found in regex search.
     *
     * @param data           found data
     * @param callback       Goal callback
     * @param newVersion     new version text
     * @param versionElement to process
     * @param outputFile     to write to
     * @param document       The pom document
     *
     * @return 'true' if changed, 'false' otherwise
     *
     * @throws MojoExecutionException if any
     */
    private static boolean processFoundData(
            final Matcher data,
            final Callback callback,
            final Ref<String> newVersion,
            final Element versionElement,
            final Ref<File> outputFile,
            final Document document
    ) throws MojoExecutionException
    {
        boolean changed = false;

        Version version = new Version();
        version.major = data.group("major");
        version.minor = data.group("minor");
        version.patch = data.group("patch");
        version.snapshot = data.group("snapshot");
        DISPLAY.level(2).println(version.toString());

        // If changed, update file.
        if (changed = callback.processVersion(version))
        {
            // Build new version text string.
            newVersion.val = version.format();
            DISPLAY.level(3).println("output : " + newVersion.val);

            versionElement.setText(newVersion.val);
            writePOM(outputFile, document);
        } else
        {
            newVersion.val = version.format();
        }

        return changed;
    }

    /**
     * Process a single module, updating its version element text.
     *
     * @param module     to be updated
     * @param newVersion new version text
     *
     * @return 'true' if successful, 'false' otherwise
     */
    private static boolean processModule(final Element module, final Ref<String> newVersion)
            throws MojoExecutionException, MojoFailureException
    {
        DISPLAY.level(3).println("Entry: processModule()");
        boolean rtn = false;
        Element versionElement;
        Ref<File> inputFile = Ref.val();

        Path filePath = of(module.getText(), FILE_NAME);
        DISPLAY.level(3).println("\nfilePath : " + filePath);

        Document document = loadDocument(filePath.toString(), inputFile);
        Element parentElement = document.getRootElement().element("parent");
        DISPLAY.level(3).println("\nparentElement : " + parentElement);

        // Do we have a parent pom?
        if (parentElement != null)
        {
            versionElement = parentElement.element("version");
            DISPLAY.level(3).println("\nversionElement : " + versionElement);

            // This should NEVER be false
            if (versionElement != null)
            {
                String veText = versionElement.getText();
                DISPLAY.level(3).appendln("\nparent.version : " + veText);
                DISPLAY.level(3).println("\nnewVersion : " + newVersion.val);

                if (!versionElement.getText().equals(newVersion.val))
                {
                    versionElement.setText(newVersion.val);
                    writePOM(inputFile, document);
                    DISPLAY.level(3).println("\nparent.version updated in file.");
                }

                rtn = true;
            } else
            {
                throw new MojoFailureException("\nWarning: No parent version element found!");
            }
        }

        DISPLAY.level(3).println("Exit: processModule()");

        return rtn;
    }

    /**
     * Process any modules if this is a 'pom' project.
     *
     * @param document   The pom document
     * @param newVersion new version text
     *
     * @return 'true' if successful, 'false' otherwise
     */
    private static boolean processModules(final Document document, final Ref<String> newVersion)
            throws MojoExecutionException, MojoFailureException
    {
        boolean rtn = true;
        DISPLAY.level(3).println("Entry: processModules()");

        // root is 'project'.
        Element modulesElement = document.getRootElement().element("modules");
        DISPLAY.level(3).println("\nmodulesElement : " + modulesElement);

        if (modulesElement != null)
        {
            for (Element moduleElement : modulesElement.elements())
            {
                if (!processModule(moduleElement, newVersion))
                {
                    rtn = false;
                    break;
                }
            }
        }

        DISPLAY.level(3).println("Exit: processModules()");

        return rtn;
    }

    /**
     * Process the parent artifact element's version data.
     *
     * @param document   The pom document
     * @param newVersion new version text
     *
     * @return 'true' if changed, 'false' otherwise
     *
     * @throws MojoFailureException if any
     */
    private static boolean processParentElement(
            final Document document,
            final Ref<String> newVersion
    ) throws MojoFailureException
    {
        DISPLAY.level(3).println("Entry: processParentElement()");
        boolean changed = false;
        Element versionElement;

        // root is 'project'.
        Element parentElement = document.getRootElement().element("parent");
        DISPLAY.level(3).println("\nparentElement : " + parentElement);

        // Do we have a parent pom?
        if (parentElement != null)
        {
            versionElement = parentElement.element("version");

            // This should NEVER be false
            if (versionElement != null)
            {
                newVersion.val = versionElement.getText();
//                changed = true;
            } else
            {
                throw new MojoFailureException("\nWarning: No parent version element found!");
            }
        }

        DISPLAY.level(3).println("\nparent.version: " + newVersion.val);
        DISPLAY.level(3).println("Exit: processParentElement()");

        return changed;
    }

    /**
     * Process the version number.
     *
     * @param document   The pom document
     * @param oldVersion old version text
     * @param callback   Goal callback
     * @param outputFile to write to
     * @param newVersion new version text
     *
     * @return 'true' if changed, 'false' otherwise
     *
     * @throws MojoExecutionException if any
     * @throws MojoFailureException   if any
     */
    private static boolean processVersion(
            final Document document,
            final Ref<String> oldVersion,
            final Callback callback,
            final Ref<File> outputFile,
            final Ref<String> newVersion
    )
            throws MojoExecutionException, MojoFailureException
    {
        DISPLAY.level(3).println("Entry: processVersion()");
        boolean changed = false;

        // root is 'project'.
        Element versionElement = document.getRootElement().element("version");
        DISPLAY.level(1).println("\nversionElement : " + versionElement);

        // Do we have our own version number?
        if (versionElement != null)
        {
            changed = processVersionElement(versionElement, oldVersion, callback,
                    newVersion, outputFile, document);
        } else
        {
            changed = processParentElement(document, newVersion);
        }

        DISPLAY.level(3).println("project.version : " + newVersion.val);
        DISPLAY.level(3).println("Exit: processVersion()");

        return changed;
    }

    /**
     * Process the version element.
     *
     * @param versionElement to process
     * @param oldVersion     old version text
     * @param callback       Goal callback
     * @param newVersion     new version text
     * @param outputFile     to write to
     * @param document       The pom document
     *
     * @return 'true' if changed, 'false' otherwise
     *
     * @throws MojoFailureException   if any
     * @throws MojoExecutionException if any
     */
    @SuppressWarnings("AssignmentToMethodParameter")
    private static boolean processVersionElement(
            final Element versionElement,
            final Ref<String> oldVersion,
            final Callback callback,
            final Ref<String> newVersion,
            final Ref<File> outputFile,
            final Document document
    ) throws MojoFailureException, MojoExecutionException
    {
        boolean changed = false;

        oldVersion.val = versionElement.getTextTrim();
        DISPLAY.level(1).println("\nproject.version : " + oldVersion.val);

        // Prepare regex.
        Matcher m = compile(MAVEN).matcher(oldVersion.val);

        // Process found data.
        if (m.find())
        {
            changed = processFoundData(m, callback, newVersion, versionElement,
                    outputFile, document);
        } else
        {
            throw new MojoFailureException("\nWarning: No valid version text found: " + oldVersion.val);
        }

        return changed;
    }

    /**
     * Write updated document to the pom.xml file.
     *
     * @param outputFile to write to
     * @param document   to output
     *
     * @throws MojoExecutionException if any
     */
    private static void writePOM(final Ref<File> outputFile, final Document document)
            throws MojoExecutionException
    {
        try
        {
            XMLWriter writer = new XMLWriter(
                    Files.newBufferedWriter(outputFile.val.toPath(),
                            StandardOpenOption.WRITE,
                            StandardOpenOption.TRUNCATE_EXISTING));
            writer.write(document);
            writer.flush();
            writer.close();
        } catch (IOException ex)
        {
            throw new MojoExecutionException("\nError writing to '"
                    + outputFile.val.toPath().toString() + "'", ex);
        }
    }
}
