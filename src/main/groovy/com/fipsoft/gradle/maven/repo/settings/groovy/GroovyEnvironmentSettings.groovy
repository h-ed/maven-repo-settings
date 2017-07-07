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
package com.fipsoft.gradle.maven.repo.settings.groovy

import com.fipsoft.gradle.maven.repo.settings.api.AbstractMavenRepoSettingsAware
import com.fipsoft.gradle.maven.repo.settings.ext.MavenRepoSettingsExtension
import com.fipsoft.gradle.maven.repo.settings.model.DefaultMavenRepoEntry
import com.fipsoft.gradle.maven.repo.settings.utils.ServerNodeNotFoundException

import static com.fipsoft.gradle.maven.repo.settings.model.SourceStrategy.GROOVY

/**
 * @author Edgar Harutyunyan
 * @since 0.3.0
 */
class GroovyEnvironmentSettings extends AbstractMavenRepoSettingsAware<DefaultMavenRepoEntry> {
    private Map<String, Tuple2<String, String>> repoCredentials = [:]

    private GroovyEnvironmentSettings(MavenRepoSettingsExtension ext) {
        super(ext, GROOVY)

        if (!customSettingsFile || !customSettingsFile.exists() || !customSettingsFile.name.endsWith('groovy')) {
            throw new RuntimeException("not valid source file found, consider specifying one in conf{} entry...")
        }
    }

    @Override
    void printFoundRepos() {
        println "Custom repositories found (Total: ${repoCredentials.size()})"
        repoCredentials.each { k, v ->
            println "+---${k}"
        }
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
        return customSettingsFile
    }


    static GroovyEnvironmentSettings createInstance(MavenRepoSettingsExtension ext) {
        GroovyEnvironmentSettings g = new GroovyEnvironmentSettings(ext)
        withAggregatedServers(ext) {
            g.repoCredentials = it
        }

        return g
    }

    private static void withAggregatedServers(MavenRepoSettingsExtension ext, Closure c) {
        ConfigObject conf = new ConfigSlurper().parse(ext.userSettingsFile.toURI().toURL())
        Map<String, Tuple2<String, String>> aggregated = [:]

        conf.servers.each {
            aggregated.put(it.id, new Tuple2<String, String>(it.username, it.password))
        }

        c.call(aggregated)
    }
}
