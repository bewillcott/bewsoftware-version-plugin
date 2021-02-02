@@@
use : articles2
title: ${document.title} | Configuration
@@@


## Configuration

These settings are available for both the **build** and **release** goals.

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


**Limitations**  
For some reason that I have as yet been unable to fathom, when my plugin is run
within a profile and sets the property `project.build.finalName` to a new value,
that value is **not** available to any plugins in the same profile.  However,
plugins in the _default_ profile are able to access the updated information.

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
