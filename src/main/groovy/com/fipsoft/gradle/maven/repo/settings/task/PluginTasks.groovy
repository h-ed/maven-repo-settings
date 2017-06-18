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
        Set<String> serversFound = []

        if (SourceStrategy.MAVEN.name() == sourceAware.strategyName) {
            File home_m2_settings = new File(sourceAware.settingsFilePath)

            if (home_m2_settings.exists()) {
                def extractedServers = getServersFromXml(new XmlSlurper().parse(home_m2_settings))
                serversFound.addAll(extractedServers)
            }

            File m2_home_settings = new File(sourceAware.maven_home_settings)
            if (m2_home_settings.exists()) {
                def extractedServers = getServersFromXml(new XmlSlurper().parse(m2_home_settings))
                serversFound.addAll(extractedServers)
            }

            println "Custom repositories found (Total: ${serversFound.size()})"
            serversFound.each {
                println "+---${it}"
            }
        }
    }

    private static List<String> getServersFromXml(def entry) {
        entry.servers.server.collect { (String) it.id }
    }
}
