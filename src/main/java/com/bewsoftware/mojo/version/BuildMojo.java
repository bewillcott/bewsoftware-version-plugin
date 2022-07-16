/*
 * 'bewsoftware-version-plugin' provides Maven style version number
 * incrementing.
 *
 * Copyright (C) 2021, 2022 Bradley Willcott <mailto:bw.opensource@yahoo.com>
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import static org.apache.maven.plugins.annotations.LifecyclePhase.VALIDATE;

/**
 * BuildMojo class updates the {@code project.version} text, possibly
 * incrementing the
 * patch level.
 * <p>
 * The format is: &lt;major&gt;.&lt;minor&gt;.&lt;patch&gt;-SNAPSHOT<br>
 * Example: {@code 0.1.0-SNAPSHOT}
 * <p>
 * The conditional processing logic is as follows:
 * <ul>
 * <li>If '.&lt;minor&gt;' is missing then &lt;minor&gt; is set to: '0'
 * <li>If '.&lt;patch&gt;' is missing then &lt;patch&gt; is set to: '0'
 * <li>If '-SNAPSHOT' is missing, then
 * <ul>
 * <li>&lt;patch&gt; is incremented
 * <li>and '-SNAPSHOT' is appended
 * </ul>
 * </ul><br>
 * <table style="border: 2px solid black;border-collapse: collapse;padding: 5px">
 * <caption style="border-left: 2px solid black;border-top: 2px solid black;border-right: 2px solid black;padding: 5px">
 * <strong>Examples</strong>
 * </caption>
 * <thead>
 * <tr>
 * <th style="border: 2px solid black;padding: 5px;">
 * Current Version
 * </th>
 * <th style="border: 2px solid black;padding: 5px;">
 * New Version
 * </th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td style="border: 1px solid black;padding: 5px;">
 * <code>0.1-SNAPSHOT</code>
 * </td>
 * <td style="border: 1px solid black;padding: 5px;">
 * <code>0.1.0-SNAPSHOT</code>
 * </td>
 * </tr>
 * <tr>
 * <td style="border: 1px solid black;padding: 5px;">
 * <code>0.1</code>
 * </td>
 * <td style="border: 1px solid black;padding: 5px;">
 * <code>0.1.1-SNAPSHOT</code>
 * </td>
 * </tr>
 * <tr>
 * <td style="border: 1px solid black;padding: 5px;">
 * <code>0.1.0-SNAPSHOT</code>
 * </td>
 * <td style="border: 1px solid black;padding: 5px;">
 * <code>0.1.0-SNAPSHOT</code>
 * </td>
 * </tr>
 * <tr>
 * <td style="border: 1px solid black;padding: 5px;">
 * <code>0.1.0</code>
 * </td>
 * <td style="border: 1px solid black;padding: 5px;">
 * <code>0.1.1-SNAPSHOT</code>
 * </td>
 * </tr>
 * <tr>
 * <td style="border: 1px solid black;padding: 5px;">
 * <code>0.1.1</code>
 * </td>
 * <td style="border: 1px solid black;padding: 5px;">
 * <code>0.1.2-SNAPSHOT</code>
 * </td>
 * </tr>
 * <tr>
 * <td style="border: 1px solid black;padding: 5px;">
 * <code>2</code>
 * </td>
 * <td style="border: 1px solid black;padding: 5px;">
 * <code>2.0.1-SNAPSHOT</code>
 * </td>
 * </tr>
 * </tbody>
 * </table>
 *
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.2.0
 */
@Mojo(name = "build", defaultPhase = VALIDATE, requiresProject = true,
        threadSafe = false, executionStrategy = "once-per-session")
public class BuildMojo extends AbstractVersionMojo
{

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {

        DISPLAY.level(2).println("Entry: execute()");
        run();
        DISPLAY.level(2).println("Exit: execute()");
    }

    @Override
    @SuppressWarnings("ValueOfIncrementOrDecrementUsed")
    public boolean processVersion(final Version version)
    {

        boolean changed = false;

        if (keep)
        {
            version.processNulls();

            // Should never be false
        } else if (version.major != null)
        {
            if (version.minor == null)
            {
                version.minor = "0";
                changed = true;
            }

            if (version.patch == null)
            {
                version.patch = "0";
                changed = true;
            }

            if (version.snapshot == null)
            {
                version.snapshot = SUFFIX;

                int patch = Integer.parseInt(version.patch);
                version.patch = "" + ++patch;
                changed = true;
            }
        }

        return changed;
    }
}
