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
package com.fipsoft.gradle.maven.repo.settings.strategies.maven

import com.fipsoft.gradle.maven.repo.settings.MavenRepoSettingsPlugin
import com.fipsoft.gradle.maven.repo.settings.ext.MavenRepoSettingsExtension
import com.fipsoft.gradle.maven.repo.settings.ext.model.DefaultSettingSourceResolver
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

/**
 * @author Edgar Harutyunyan
 */
class CoreTests extends MavenTestBase {

    @Before
    void applyMavenRepoSettingsPlugin() {
        project.with {
            apply plugin: 'com.fipsoft.maven-repo-settings'
        }

        project.with {
            repositories {
                mavenInternal {
                    repo {
                        id 'test1'
                        url 'https://example1.com/'
                    }

                    repo {
                        id 'test2'
                        url 'https://example2.com/'
                    }
                }
            }
        }

        DefaultSettingSourceResolver.setMavenSettingsFileName(settingsFile.absolutePath)
        project.evaluate()
    }

    @Test
    void assertPluginApplies() {
        assertTrue(project.plugins.hasPlugin(MavenRepoSettingsPlugin.class))
    }


    @Test
    void assertDefaultSettings() {
        MavenRepoSettingsExtension ext = project.extensions.mavenInternal

        assertNotNull(ext)

        withRepoSpec(1) {
            assertThat(ext.repos, CoreMatchers.hasItem(it))
        }

        withRepoSpec(2) {
            assertThat(ext.repos, CoreMatchers.hasItem(it))
        }
    }
}
