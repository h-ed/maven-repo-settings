# Gradle maven-repo-settings
_Gradle Custom Maven Repository Settings Plugin_

Inspired by [maven-repo-settings](https://github.com/mark-vieira/gradle-maven-settings-plugin), this plugin 
aims to facilitate dependency management from the enterprise/internal maven repositories.

 
## Usage
To use the plugin, add the following to your `build.gradle` file.

    buildscript {
        
        repositories {
            maven {
                url "https://plugins.gradle.org/m2/"
            }
        }
        
        dependencies {
            classpath 'com.fipsoft.gradle:maven-repo-settings:0.1'
        }
    }

    apply plugin: 'maven-repo-settings'
    
For Gradle 2.1+ you can use the new plugin mechanism to download the plugin from the 
[Gradle Plugin Portal](http://plugins.gradle.org/).
    
    plugins {
      id "maven-repo-settings" version "0.1"
    }


## Internal (custom) repositories
To define *internal* repository, just declare `mavenInternal` repository in your `repositories` section

```
    repositories {
       
         mavenCentral()

         
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
The plugin will search repository definition first from `user.home/.m2/settings.xml`, and then
`$M2_HOME/conf/settings.xml` or `$MAVEN_HOME` for old maven usages

### Example of server definition in settings.xml

      <server>
            <id>yourRepoId</id>
            <username>repositoryUsername</username>
            <password>repositoryPassword</password>
       </server>
       


              
> *Note: the order of looking for xml file is based on maven convention:*

>`user.home/.m2/settings.xml`

>`$M2_HOME/conf/settings.xml` (or $MAVEN_HOME for an older versions of Maven)

## Alternative sources for repo definition
todo: in progres...

In often cases you might define your internal repository credentials other place than `settings.xml` file.
For example in [Grails 2](http://docs.grails.org/2.5.x/guide/conf.html#dependencyRepositories) authentication done by
`aether` or `ivy` requires to define your repository credentials in `${user.home}/.grails/settings.xml`. To avoid duplicated
definitions *MAVEN-REPO-SETTINGS* plugin gives appropriate api to define custom source from which authentication 
credentials will be resolved.

## Usage with custom credential source

To instruct plugin for scanning authentication credentials in different source file just define `credentialSource`
in your `mavenInternal` block

```
    repositories {
       
         mavenCentral()

         
         mavenInternal {
              credentialSource = "${user.home}/.grails/settings.groovy"
              
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
    servers {
        
        server {
            id = yourRepoId
            username = repoUsername
            password = repoPassword
        }
    }
```