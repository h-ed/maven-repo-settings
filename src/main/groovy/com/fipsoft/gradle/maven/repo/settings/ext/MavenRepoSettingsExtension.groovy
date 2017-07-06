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
package com.fipsoft.gradle.maven.repo.settings.ext

import com.fipsoft.gradle.maven.repo.settings.adapter.MavenRepoSettingsAdapter
import com.fipsoft.gradle.maven.repo.settings.api.MavenRepo
import com.fipsoft.gradle.maven.repo.settings.api.MavenRepoConfig
import com.fipsoft.gradle.maven.repo.settings.model.DefaultMavenRepoConfig
import com.fipsoft.gradle.maven.repo.settings.model.DefaultMavenRepoEntry
import org.gradle.api.Incubating
import org.gradle.api.Project

/**
 * @author Edgar Harutyunyan
 * @since 0.1
 */
@Incubating
class MavenRepoSettingsExtension {
    static final String INTERNAL_MAVEN_REPO_EXT = 'mavenInternal'

    MavenRepoSettingsAdapter adapter

    Project project

    List<? extends MavenRepo> repos = []

    MavenRepoConfig configObject = new DefaultMavenRepoConfig()

    MavenRepoSettingsExtension(Project project) {
        this.project = project
        this.adapter = MavenRepoSettingsAdapter.init(this)
        project.gradle.addListener(adapter)
    }

    def repo(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DefaultMavenRepoEntry) Closure c) {
        def repoSpec = new DefaultMavenRepoEntry()
        def body = c.rehydrate(repoSpec, this, this)
        body.resolveStrategy = Closure.DELEGATE_ONLY
        body()
        repos.add(repoSpec)
    }

    def conf(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DefaultMavenRepoConfig) Closure c) {
        this.configObject = new DefaultMavenRepoConfig()
        def body = c.rehydrate(configObject, this, this)
        body.resolveStrategy = Closure.DELEGATE_ONLY
        body()
    }

    File getUserSettingsFile() {
        return project.file(configObject.customSettingsFilePath)
    }
}
