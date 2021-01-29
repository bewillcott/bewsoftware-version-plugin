/*
 * Copyright (C) 2021 Bradley Willcott
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
package com.bewsoftware.mojo.versioning;

import com.bewsoftware.utils.struct.StringReturn;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static com.bewsoftware.mojo.versioning.Utils.updateFinalName;
import static com.bewsoftware.mojo.versioning.Utils.processPom;

/**
 * AbstractVersioningMojo class is the parent of both of the classes:
 * {@link BuildMojo} and {@link ReleaseMojo}.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 0.1
 */
public abstract class AbstractVersioningMojo extends AbstractMojo implements Callback {

    /**
     * Project version suffix.
     */
    protected static final String SUFFIX = "-SNAPSHOT";

    /**
     * Whether or not to skip this execution.
     */
    @Parameter(defaultValue = "false")
    private boolean skip;

    /**
     * The maven project.
     */
    @Parameter(property = "project", readonly = true, required = true)
    protected MavenProject theProject;

    /**
     * Update "project.version".
     */
    private void updateProjectVersion(final StringReturn newVersion, final StringReturn oldVersion) {

        getLog().debug("\n[OLD] project.artifact().getVersion(): " + theProject.getArtifact().getVersion());
        theProject.getArtifact().selectVersion(newVersion.val);
        getLog().debug("[NEW] project.artifact().getVersion(): " + theProject.getArtifact().getVersion() + "\n");
        getLog().debug("[OLD] project.getModel().getVersion(): " + theProject.getModel().getVersion());
        theProject.getModel().setVersion(newVersion.val);
        getLog().debug("[NEW] project.getModel().getVersion(): " + theProject.getModel().getVersion() + "\n");

        // Final name of files
        getLog().debug("[OLD] theProject.getBuild().getFinalName(): " + theProject.getBuild().getFinalName());

        // Update /project/build/finalName.
        theProject.getBuild().setFinalName(
                updateFinalName(theProject.getBuild().getFinalName(),
                                oldVersion.val, newVersion.val, getLog()));

        getLog().debug("[NEW] theProject.getBuild().getFinalName(): " + theProject.getBuild().getFinalName());
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

        if (theProject != null)
        {
            getLog().debug("\ntheProject: " + theProject.toString());

            final StringReturn oldVersion = new StringReturn();
            final StringReturn newVersion = new StringReturn();

            if (processPom(this, getLog(), oldVersion, newVersion))
            {
                updateProjectVersion(newVersion, oldVersion);
            }
        }

        return true;
    }

}
