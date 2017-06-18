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
package com.fipsoft.gradle.maven.repo.settings.ext.model

import com.fipsoft.gradle.maven.repo.settings.ext.MavenRepoSettingsExtension
import com.fipsoft.gradle.maven.repo.settings.ext.adapter.DefaultCredentialProvider
import com.fipsoft.gradle.maven.repo.settings.ext.api.CredentialProvider
import com.fipsoft.gradle.maven.repo.settings.ext.api.MavenRepoSettingSourceAware
import com.fipsoft.gradle.maven.repo.settings.utils.SourceStrategy

/**
 * @author Edgar Harutyunyan
 * @since 0.1
 */
class DefaultSettingSourceResolver implements MavenRepoSettingSourceAware {

    static String maven_settings = System.getProperty("user.home") + '/.m2/settings.xml'

    private final MavenRepoSettingsExtension extension

    private final CredentialProvider credentialProvider


    DefaultSettingSourceResolver(MavenRepoSettingsExtension ext) {
        this.extension = ext
        this.credentialProvider = DefaultCredentialProvider.newInstance(this)
    }

    @Override
    String getStrategyName() {
        return SourceStrategy.MAVEN
    }

    @Override
    String getSettingsFilePath() {
        return maven_settings
    }

    @Override
    CredentialProvider getCredentialProvider() {
        return credentialProvider
    }

    @Override
    void validateEntries(Collection<? extends RepoSpec> repos) {
        repos.each {
            String missingField = !it.repoId ? 'id' : !it.repoUrl ? 'url' : null

            if (missingField) {
                throw new RuntimeException("field [$missingField] is required for repo definition")
            }
        }
    }

    //test scope only
    static void setMavenSettingsFileName(String fileName) {
        maven_settings = fileName
    }

}
