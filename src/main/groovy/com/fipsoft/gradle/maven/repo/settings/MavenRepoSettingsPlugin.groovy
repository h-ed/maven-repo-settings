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
package com.fipsoft.gradle.maven.repo.settings

import com.fipsoft.gradle.maven.repo.settings.ext.MavenRepoSettingsExtension
import com.fipsoft.gradle.maven.repo.settings.task.PluginTasks
import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Edgar Harutyunyan
 */
@Incubating
class MavenRepoSettingsPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        createPluginTasks(project)

        createPluginExtension(project)
    }

    private static void createPluginExtension(Project project) {
        project.extensions.create(MavenRepoSettingsExtension.INTERNAL_MAVEN_REPO_EXT,
                MavenRepoSettingsExtension.class, project)
    }

    private static void createPluginTasks(Project project) {
        project.getTasks().create('showInternalRepos', PluginTasks.class)
    }
}
