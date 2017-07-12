/*
 *  Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.fipsoft.gradle.maven.repo.settings.adapter

import com.fipsoft.gradle.maven.repo.settings.api.AbstractMavenRepoSettingsAware
import com.fipsoft.gradle.maven.repo.settings.api.MavenRepo
import com.fipsoft.gradle.maven.repo.settings.ext.MavenRepoSettingsExtension
import com.fipsoft.gradle.maven.repo.settings.groovy.GroovyEnvironmentSettings
import com.fipsoft.gradle.maven.repo.settings.mvn.MavenEnvironmentSettings
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.publish.PublishingExtension

/**
 * @author Edgar Harutyunyan
 */
class MavenRepoSettingsAdapter implements ProjectEvaluationListener {
    private MavenRepoSettingsExtension extension
    private AbstractMavenRepoSettingsAware settingsAware

    private MavenRepoSettingsAdapter(MavenRepoSettingsExtension extension) {
        this.extension = extension
    }

    @Override
    void beforeEvaluate(Project project) {}


    @Override
    void afterEvaluate(Project project, ProjectState projectState) {
        initEnvironmentSettings()

        extension.repos.each { MavenRepo repo ->
            def c = settingsAware.resolveCredentials(repo)
            project.repositories {
                delegate.maven {
                    credentials {
                        username "${c.getFirst()}"
                        password "${c.getSecond()}"
                    }
                    url repo.url
                }
            }
        }

        try {
            PublishingExtension publishingExt = (PublishingExtension) project.extensions.getByName('publishing')

            publishingExt.repositories.each { r ->
                def credentials = r.credentials
                if (credentials) {
                    def resolvedRepoEntry = extension.repos.find { it.id == r.name }
                    if (resolvedRepoEntry) {
                        def c = settingsAware.resolveCredentials(resolvedRepoEntry)

                        r.credentials.username = "${c.getFirst()}"
                        r.credentials.password = "${c.getSecond()}"
                    }
                }

            }
        } catch (ignore) {
            // "no publishing extension found, skipping check..."
        }
    }

    AbstractMavenRepoSettingsAware getSettingsAware() {
        return this.settingsAware
    }

    private void initEnvironmentSettings() {
        String settingsFile = extension.configObject.userSettingsFilePath

        if (settingsFile.endsWith('xml')) {
            settingsAware = MavenEnvironmentSettings.createInstance(extension)
            return
        }

        if (settingsFile.endsWith('groovy')) {
            settingsAware = GroovyEnvironmentSettings.createInstance(extension)
            return
        }

        throw new RuntimeException("Unknown settings file: ${settingsFile}")
    }


    static MavenRepoSettingsAdapter init(MavenRepoSettingsExtension extension) {
        return new MavenRepoSettingsAdapter(extension)
    }
}
