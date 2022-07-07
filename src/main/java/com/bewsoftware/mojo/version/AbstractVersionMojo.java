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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static com.bewsoftware.mojo.version.Utils.processPom;
import static com.bewsoftware.mojo.version.Utils.updateProjectVersion;

/**
 * AbstractVersionMojo class is the parent of both of the classes:
 * {@link BuildMojo} and {@link ReleaseMojo}.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.1.0
 */
@SuppressWarnings("ProtectedField")
public abstract class AbstractVersionMojo extends AbstractMojo implements Callback
{
    public static final Display DISPLAY = ConsoleIO.consoleDisplay("");

    /**
     * Project version suffix.
     */
    protected static final String SUFFIX = "-SNAPSHOT";

    /**
     * Set the property name of the returned value for the final base filename
     * string.
     */
    @Parameter
    private String finalBaseNamePropertyName;

    /**
     * Whether or not to skip this execution.
     */
    @Parameter(defaultValue = "false")
    private boolean skip;

    /**
     * Set the level of verbosity.
     * <ul>
     * <li>'0' is off.</li>
     * <li>'1' - '3' are active levels, from limited
     * information to a lot of information.</li>
     * </ul>
     */
    @Parameter(defaultValue = "0")
    private int verbosity;

    /**
     * The maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    /**
     * Set pom property key/value pair.
     *
     * @param property name.
     * @param value    text.
     */
    private void setProperty(String property, String value)
    {
        if (value != null)
        {
            project.getProperties().setProperty(property, value);
        }
    }

    /**
     * Main method of Mojo.
     * <p>
     * Called by subclasses.
     *
     * @return {@code true} if completed, {@code false} otherwise.
     *
     * @throws MojoFailureException   if any.
     * @throws MojoExecutionException if any.
     */
    protected boolean run() throws MojoFailureException, MojoExecutionException
    {
        if (verbosity > 0)
        {
            DISPLAY.debugLevel(verbosity);
            Utils.setVerbosity(verbosity);
        }

        DISPLAY.level(0).appendln("BEWSoftware Versioning Maven Plugin");
        DISPLAY.level(0).println("===================================");
        DISPLAY.level(1).println("\nverbosity: " + verbosity);

        if (skip)
        {
            DISPLAY.level(0).println("\nSkipping execution.");
            return false;
        }

        if (project != null)
        {
            final Ref<String> finalName = Ref.val();
            final Ref<String> oldVersion = Ref.val();
            final Ref<String> newVersion = Ref.val();

            finalName.val = project.getBuild().getFinalName();
            DISPLAY.level(1).println("\nproject: " + project.toString());

            if (processPom(this, oldVersion, newVersion))
            {
                updateProjectVersion(project, oldVersion, newVersion, finalName);
            }

            DISPLAY.level(1).println("finalBaseNamePropertyName: " + finalBaseNamePropertyName);

            if (finalBaseNamePropertyName != null)
            {
                setProperty(finalBaseNamePropertyName, finalName.val);
                DISPLAY.level(1).println("finalName: " + finalName);
            }

            DISPLAY.level(0).println("\nVersion: " + newVersion.val);
        }

        return true;
    }
}
