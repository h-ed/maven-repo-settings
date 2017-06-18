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

import com.fipsoft.gradle.maven.repo.settings.ext.MavenRepoSettingsExtension
import com.fipsoft.gradle.maven.repo.settings.ext.api.MavenRepoSettingSourceAware
import com.fipsoft.gradle.maven.repo.settings.ext.api.SettingAdapter
import com.fipsoft.gradle.maven.repo.settings.ext.model.DefaultSettingSourceResolver
import com.fipsoft.gradle.maven.repo.settings.ext.model.RepoSpec
import org.gradle.api.Incubating
import org.gradle.api.Project
import org.gradle.api.ProjectState


/**
 * @author Edgar Harutyunyan
 * @since 0.1
 */
@Incubating
class MavenRepoSettingsAdapter implements SettingAdapter {

    @Delegate
    private final MavenRepoSettingsExtension extension

    final MavenRepoSettingSourceAware sourceResolver


    MavenRepoSettingsAdapter(MavenRepoSettingsExtension ext) {
        this.extension = ext
        this.sourceResolver = new DefaultSettingSourceResolver(ext)
    }

    @Override
    MavenRepoSettingSourceAware resolveRepoSourceAware() {
        return sourceResolver
    }

    @Override
    void afterEvaluate(Project project, ProjectState projectState) {
        sourceResolver.validateEntries(repos)

        repos.each { RepoSpec repo ->
            def auth = sourceResolver.credentialProvider.resolveCredentials(repo)

            project.repositories {
                delegate.maven {
                    credentials {
                        username "${auth.getFirst()}"
                        password "${auth.getSecond()}"
                    }
                    url repo.repoUrl
                }
            }
        }
    }
}
