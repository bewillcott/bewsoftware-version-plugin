@@@
use : articles2
title: ${document.title} | release
@@@


## release goal

The **release** goal is used to set the project version in accordance with the examples
in the following table:

[**Examples**]
|Current Version|Release Version|[2]
|---|---|[2]
|`0.1-SNAPSHOT`|`0.1.0`|[]
|`0.1`|`0.1.0`|
|`0.1.0-SNAPSHOT`|`0.1.0`|
|`0.1.0`|`0.1.0`|
|`0.1.1`|`0.1.1`|
|`2`|`2.0.0`|

As you can see, it does one of two things:

1. If the current version is not of the form: `<major>.<minor>.<increment>`  
   then it will append `.0` sequences to fill out the string. Then, if it is there,
   remove the suffix: `-SNAPSHOT`.  However,
2. If the current version is of the form: `<major>.<minor>.<increment>`  
   then it will do nothing.

===

### Setup

To use this goal in your Maven project, include the following in your `pom.xml` file:

```
<project>
    <profiles>
        <profile>
            <id>release-profile</id>
            <build>
                <plugins>                        
                    <plugin>                    
                        <groupId>com.bewsoftware.mojo</groupId>
                        <artifactId>bewsoftware-version-plugin</artifactId>
                        <version>1.0.0</version>
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
</project>

```

I recommend using the profile: `release-profile`.  By doing so, and by setting up
the **build** goal as I have suggested, you will no longer need to even think about
the version numbering, unless you are needing to change a different part of the
form.  Check out the [example][ex].



[ex]:Example.html


@@@[#navbar]
- [Home]
- [build]
- [help]
- [@active] [release](#)
- [@right] [About]
    - [License]
- [@right] [Example]
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
