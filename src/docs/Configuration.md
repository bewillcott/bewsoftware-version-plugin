@@@
use : articles2
title: ${document.title} | Configuration
@@@


## Configuration

These settings are available for both the **build** and **release** goals.

### keep

To _keep_ the current version number:  
`<keep>true</keep>`

Can be useful when making a minor fix to a release version without incrementing
the patch number. Or for testing a fix during development to see how the
Release Build will be affected, but keeping the "-SNAPSHOT" extension in
the version number.

### skip

To _skip_ an execution set this to true:  
`<skip>true</skip>`

**Default:** `false`.


### finalBaseNamePropertyName

Set the property name of the returned value for the final base filename string.

This is an alternative to the `project.build.finalName` property which in some 
parts of the running build, cannot be updated by this plugin.  So, by using the 
property name you set here, you get the new filename version text that the 
_finalName_ property is set to.

For example to set it to `finalBaseName`:  
`<finalBaseNamePropertyName>finalBaseName</finalBaseNamePropertyName>`

which could then be used like this:  
`${project.build.directory}/${finalBaseName}.jar`

**Default:** no default.



@@@[#navbar]
- [Home]
- [build]
- [help]
- [release]
- [@right] [About]
    - [License]
- [@right] [Example]
- [@right active] [Configuration](#)


[About]:About.html
[build]:Build.html
[Configuration]:Configuration.html
[help]:Help.html
[Home]:index.html
[release]:Release.html
[License]:LICENSE.html
[Example]:Example.html
@@@
