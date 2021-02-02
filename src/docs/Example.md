@@@
use : articles2
title: ${document.title} | Example
@@@


## Example

The following example combines the setup of both the **build** and **release** goals
into a Maven project, where they are used seamlessly as part of the normal build processes.

**The _skip_ setting is required to make this work.**  Without it, doing a "release-profile"
run will end up with the `<increment>` component being incremented each time,
because the **build** goal will be run to completion before the **release** goal.

Further, using the _skip_ setting when it's available in a plugin is a good idea.
So, when you know you don't want/need a plugin to do its thing, set it to `true`.

**Note:** The code for this example was copied directly from this project's `pom.xml` 
file.  So I know it works.

```
<project>
    <properties>
        <skip.version.build>false</skip.version.build>
        ...
    </properties>
    ...
    <profiles>
        <profile>
            <id>release-profile</id>
            <properties>
                <skip.version.build>true</skip.version.build>
            </properties>
            <build>
                <plugins>                        
                    <plugin>                    
                        <groupId>com.bewsoftware.mojo</groupId>
                        <artifactId>bewsoftware-version-plugin</artifactId>
                        <executions>
                            <execution>
                                <id>Release</id>
                                <goals>
                                    <goal>release</goal>
                                </goals>
                                <configuration>
                                    <finalBaseNamePropertyName>finalBaseName</finalBaseNamePropertyName>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                    ...
                </plugins>
                ...
            </build>
            ...
        </profile>
        ...
    </profiles>
    ...
    <build>
        <pluginManagement>
            <plugins>                 
                <plugin>
                    <groupId>com.bewsoftware.mojo</groupId>
                    <artifactId>bewsoftware-version-plugin</artifactId>
                    <version>1.0.0</version>
                </plugin>
            </plugins>                 
        </pluginManagement>
        ...
        <plugins>
            <plugin>                    
                <groupId>com.bewsoftware.mojo</groupId>
                <artifactId>bewsoftware-version-plugin</artifactId>
                <executions>
                    <execution>
                        <id>Build</id>
                        <goals>
                            <goal>build</goal>
                        </goals>
                        <configuration>
                            <skip>${skip.version.build}</skip>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            ...
        </plugins>
        ...
    </build>
    ...
</project>

```


@@@[#navbar]
- [Home]
- [build]
- [help]
- [release]
- [@right] [About]
    - [License]
- [@right active] [Example](#)
- [@right] [Configuration]


[About]:About.html
[build]:Build.html
[Configuration]:Configuration.html
[help]:Help.html
[Home]:index.html
[release]:Release.html
[License]:LICENSE.html
[Example]:Example.html
@@@
