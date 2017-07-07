# Gradle maven-repo-settings plugin
_Resolve maven repository credentials from custom files_

Inspired by [maven-settings](https://github.com/mark-vieira/gradle-maven-settings-plugin), this plugin 
aims to facilitate authentication for the enterprise/internal maven repositories.

 
## Usage
To use the plugin, add the following to your `build.gradle` file.
```groovy
    buildscript {
        
        repositories {
            maven {
                url "https://plugins.gradle.org/m2/"
            }
        }
        
        dependencies {
            classpath 'com.fipsoft.gradle:maven-repo-settings:0.3.0'
        }
    }
    
    apply plugin: 'com.fipsoft.maven-repo-settings'
```
For Gradle 2.1+ you can use the new plugin mechanism to download the plugin from the 
[Gradle Plugin Portal](http://plugins.gradle.org/).
    
    plugins {
      id "com.fipsoft.maven-repo-settings" version "0.3.0"
    }


## Internal (custom) repositories
To define *internal* repository, just declare `mavenInternal` repository in your `repositories` section

```
    repositories {
       
         mavenCentral()
         //..other  standards repositories   
         
         mavenInternal {
              repo {
                 id 'yourRepo1Id'
                 url 'yourRepo1Url'
              }
             
              repo {
                 id 'yourRepo2Id'
                 url 'yourRepo2Url'
              }
         }
    }
```

You can also customize the *source* file for configuration with following entry in your _mavenInternal_

```groovy
  conf {
        source  //your settings file path to resolve repository credentials
     }
```

By default plugin will apply MAVEN mode for you and as a source file will attempt to find repository definition first from `user.home/.m2/settings.xml`, and then
`$M2_HOME/conf/settings.xml` or `$MAVEN_HOME` for old maven usages

### Example of server definition in settings.xml

      <server>
            <id>yourRepoId</id>
            <username>repositoryUsername</username>
            <password>repositoryPassword</password>
       </server>
       


> Note: _yourRepoId_ defined in settings.xml must match with the one defined 
in _mavenInternal_ closure

              
> *Note: the order of looking for xml file is based on maven convention:*

>`user.home/.m2/settings.xml`

>`$M2_HOME/conf/settings.xml` (or $MAVEN_HOME for an older versions of Maven)

## Alternative sources for repo definition

In often cases you might define your internal repository credentials other place than `settings.xml` file.
For example in [Grails 2](http://docs.grails.org/2.5.x/guide/conf.html#dependencyRepositories) authentication done by
`aether` or `ivy` requires to define your repository credentials in `${user.home}/.grails/settings.groovy`. To avoid duplicated
definitions *MAVEN-REPO-SETTINGS* plugin gives appropriate api to define custom source from which authentication 
credentials will be resolved.

## Usage with custom credential source

To instruct plugin for scanning authentication credentials in different source file just define `credentialSource`
in your `mavenInternal` block

```
    repositories {
       
         mavenCentral()

         
         mavenInternal {
              conf {
                 source  "${user.home}/.grails/settings.groovy"
              }
              
              repo {
                 id 'yourRepo1Id'
                 url 'yourRepo1Url'
              }
             
              repo {
                 id 'yourRepo2Id'
                 url 'yourRepo2Url'
              }
         }
    }
```

And in your settings.groovy file

```groovy
servers = [

        [
                id      : 'test1',
                username: 'username1',
                password: 'password1'
        ],

        [
                id      : 'test2',
                username: 'username2',
                password: 'password2'
        ]
]
```