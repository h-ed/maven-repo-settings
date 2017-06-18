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
package com.fipsoft.gradle.maven.repo.settings.ext.adapter

import com.fipsoft.gradle.maven.repo.settings.ext.api.CredentialProvider
import com.fipsoft.gradle.maven.repo.settings.ext.api.MavenRepoSettingSourceAware
import com.fipsoft.gradle.maven.repo.settings.ext.model.RepoSpec
import com.fipsoft.gradle.maven.repo.settings.utils.ServerNodeNotFoundException
import org.gradle.api.GradleException
import org.xml.sax.SAXException

/**
 * @author Edgar Harutyunyan
 * @since 0.1
 */
class DefaultCredentialProvider implements CredentialProvider {

    private final MavenRepoSettingSourceAware sourceResolver

    private DefaultCredentialProvider(MavenRepoSettingSourceAware sourceResolver) {
        this.sourceResolver = sourceResolver
    }

    @Override
    Tuple2<String, String> resolveCredentials(RepoSpec repo) {
        def foundServerNode = resolveServerNode(repo.repoId, sourceResolver.settingsFilePath)

        if (!foundServerNode || !foundServerNode.present) {
            foundServerNode = resolveServerNode(repo.repoId, sourceResolver.maven_home_settings)
        }

        if (foundServerNode?.present) {
            def extracted = foundServerNode.get()
            return new Tuple2<>(extracted.username, extracted.password)
        } else {
            throw new ServerNodeNotFoundException("server node for ${repo.repoId} not found")
        }
    }

    static DefaultCredentialProvider newInstance(MavenRepoSettingSourceAware sourceResolver) {
        return new DefaultCredentialProvider(sourceResolver)
    }


    private static Optional<Object> resolveServerNode(String repoId, String filePath) {
        def foundServerNode

        try {
            if (!filePath) {
                //intended for test cases
                throw new IOException()
            }

            File fileToLookup = new File(filePath)
            if (fileToLookup.exists()) {
                def parsed_settings = new XmlSlurper().parse(fileToLookup)
                foundServerNode = parsed_settings.servers.server.find { it.id == repoId }

                if (foundServerNode.size() == 0) {
                    return Optional.empty()
                }

                return Optional.ofNullable(foundServerNode)
            }
        } catch (IOException | SAXException e) {
            throw new GradleException("something wrong with settings file: ${filePath}", e)
        }
    }

}
