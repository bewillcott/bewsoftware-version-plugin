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
 * @version 1.0.2
 */
public abstract class AbstractVersionMojo extends AbstractMojo implements Callback {

    /**
     * Project version suffix.
     */
    protected static final String SUFFIX = "-SNAPSHOT";

    /**
     * Set the property name of the returned value for the final base filename string.
     */
    @Parameter
    private String finalBaseNamePropertyName;

    /**
     * Whether or not to skip this execution.
     */
    @Parameter(defaultValue = "false")
    private boolean skip;

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
    private void setProperty(String property, String value) {
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
    protected boolean run() throws MojoFailureException, MojoExecutionException {
        getLog().info("BEWSoftware Versioning Maven Plugin");
        getLog().info("===================================");

        if (skip)
        {
            getLog().info("\nSkipping execution.");
            return false;
        }

        if (project != null)
        {
            final StringReturn finalName = new StringReturn();
            final StringReturn oldVersion = new StringReturn();
            final StringReturn newVersion = new StringReturn();

            finalName.val = project.getBuild().getFinalName();
            getLog().debug("\nproject: " + project.toString());

            if (processPom(this, getLog(), oldVersion, newVersion))
            {
                updateProjectVersion(project, getLog(), oldVersion, newVersion, finalName);
            }

            getLog().debug("finalBaseNamePropertyName: " + finalBaseNamePropertyName);

            if (finalBaseNamePropertyName != null)
            {
                setProperty(finalBaseNamePropertyName, finalName.val);
                getLog().debug("finalName: " + finalName);
            }
        }

        return true;
    }
}
