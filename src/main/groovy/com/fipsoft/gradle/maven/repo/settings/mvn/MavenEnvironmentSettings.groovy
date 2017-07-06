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
package com.fipsoft.gradle.maven.repo.settings.mvn

import com.fipsoft.gradle.maven.repo.settings.api.AbstractMavenRepoSettingsAware
import com.fipsoft.gradle.maven.repo.settings.ext.MavenRepoSettingsExtension
import com.fipsoft.gradle.maven.repo.settings.model.DefaultMavenRepoEntry
import com.fipsoft.gradle.maven.repo.settings.model.SourceStrategy
import com.fipsoft.gradle.maven.repo.settings.utils.ServerNodeNotFoundException
import org.gradle.api.Incubating

/**
 * @author Edgar Harutyunyan
 * @since 0.3.0
 */
@Incubating
class MavenEnvironmentSettings extends AbstractMavenRepoSettingsAware<DefaultMavenRepoEntry> {
    private Map<String, Tuple2<String, String>> repoCredentials = [:]

    private MavenEnvironmentSettings(MavenRepoSettingsExtension ext) {
        super(ext, SourceStrategy.MAVEN)
    }

    @Override
    Tuple2<String, String> resolveCredentials(DefaultMavenRepoEntry repo) {
        def foundCredentialPair = repoCredentials.get(repo.id)

        if (!foundCredentialPair) {
            throw new ServerNodeNotFoundException("server node for ${repo.serverId} not found")
        }

        return foundCredentialPair
    }


    @Override
    File getSettingsSourceFile() {
        return customSettingsFile.exists() ? customSettingsFile : globalSettingsFile
    }


    static MavenEnvironmentSettings createInstance(MavenRepoSettingsExtension ext) {
        MavenEnvironmentSettings mvn = new MavenEnvironmentSettings(ext)

        withAggregatedRepoCredentials(mvn) { resolvedRepos ->
            mvn.repoCredentials = resolvedRepos
        }

        return mvn
    }


    private static void withAggregatedRepoCredentials(MavenEnvironmentSettings mvn, Closure c) {
        Map<String, Tuple2<String, String>> aggregated = [:]

        File mavenSettingsFile = mvn.globalSettingsFile
        if (mavenSettingsFile && mavenSettingsFile.exists()) {
            extractServers(mavenSettingsFile, aggregated)
        }

        File customSource = mvn.customSettingsFile
        if (customSource && customSource.exists()) {
            extractServers(customSource, aggregated)
        }

        c.call(aggregated)
    }


    private static void extractServers(File file, Map<String, Tuple2<String, String>> container) {
        if (file && file.exists()) {
            def xmlRoot = new XmlSlurper().parse(file)

            xmlRoot.servers.server.each {
                String serverId = it.id
                String username = it.username
                String password = it.password

                if (serverId) {
                    container.put(serverId, new Tuple2<String, String>(username, password))
                }
            }
        }
    }

    @Override
    void printFoundRepos() {
        println "Custom repositories found (Total: ${repoCredentials.size()})"
        repoCredentials.each { k, v ->
            println "+---${k}"
        }
    }
}
