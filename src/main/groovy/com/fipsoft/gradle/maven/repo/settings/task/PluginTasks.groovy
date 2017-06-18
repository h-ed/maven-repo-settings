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
package com.fipsoft.gradle.maven.repo.settings.task

import com.fipsoft.gradle.maven.repo.settings.ext.MavenRepoSettingsExtension
import com.fipsoft.gradle.maven.repo.settings.utils.SourceStrategy
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Edgar Harutyunyan
 * @since 0.1
 */
class PluginTasks extends DefaultTask {

    @TaskAction
    void showInternalRepos() {
        MavenRepoSettingsExtension ext = (MavenRepoSettingsExtension) project.mavenInternal
        def sourceAware = ext.settingAdapter.resolveRepoSourceAware()

        if (SourceStrategy.MAVEN.name() == sourceAware.strategyName) {
            def parsed_settings = new XmlSlurper().parse(sourceAware.settingsFilePath)

            println "Custom repositories found (Total: ${parsed_settings.servers.server.size()})"
            parsed_settings.servers.server.each {
                println "+---${it.id}"
            }
        }
    }

}
