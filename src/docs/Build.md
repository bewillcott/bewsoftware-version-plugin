@@@
use : articles2
title: ${document.title} | build
@@@


## build goal

The **build** goal is used to set the project version in accordance with the examples
in the following table:

[**Examples**]
|Current Version|Build Version|Increment|[2]
|---|---|-:-|[2]
|`0.1-SNAPSHOT`|`0.1.0-SNAPSHOT`|No|[]
|`0.1`|`0.1.1-SNAPSHOT`|Yes|
|`0.1.0-SNAPSHOT`|`0.1.0-SNAPSHOT`|No|
|`0.1.0`|`0.1.1-SNAPSHOT`|Yes|
|`0.1.1`|`0.1.2-SNAPSHOT`|Yes|
|`2`|`2.0.1-SNAPSHOT`|Yes|

As you can see, it does one of three things:

1. If the current version is not of the form: `<major>.<minor>.<increment>-SNAPSHOT`  
   then it will append `.0` sequences to fill out the string, then append the suffix:
   `-SNAPSHOT` if it is not already there.
2. If the current version if of the form: `<major>.<minor>.<increment>`  
   then it will increment the `<increment>` component and append the suffix: `-SNAPSHOT`.
3. If the current version is of the form: `<major>.<minor>.<increment>-SNAPSHOT`  
   then it will do nothing.

===

### Setup

To use this goal in your Maven project, include the following in your `pom.xml` file:

```
<project>
    <build>
        <plugins>
            <plugin>                    
                <groupId>com.bewsoftware.mojo</groupId>
                <artifactId>bewsoftware-version-plugin</artifactId>
                <version>1.0.0</version>
                <executions>
                    <execution>
                        <id>Build</id>
                        <goals>
                            <goal>build</goal>
                        </goals>
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
- [@active] [build](#)
- [help]
- [release]
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
