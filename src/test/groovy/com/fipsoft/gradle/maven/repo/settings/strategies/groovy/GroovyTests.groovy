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
package com.fipsoft.gradle.maven.repo.settings.strategies.groovy

import com.fipsoft.gradle.maven.repo.settings.MavenRepoSettingsPlugin
import com.fipsoft.gradle.maven.repo.settings.adapter.MavenRepoSettingsAdapter
import com.fipsoft.gradle.maven.repo.settings.api.MavenRepo
import com.fipsoft.gradle.maven.repo.settings.ext.MavenRepoSettingsExtension
import com.fipsoft.gradle.maven.repo.settings.model.SourceStrategy
import com.fipsoft.gradle.maven.repo.settings.strategies.base.TestBase
import com.fipsoft.gradle.maven.repo.settings.utils.ServerNodeNotFoundException
import org.hamcrest.CoreMatchers
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import static org.junit.Assert.*

/**
 * @author Edgar Harutyunyan
 */
class GroovyTests extends TestBase {
    @Rule
    public ExpectedException thrown = ExpectedException.none()

    GroovyTests() {
        super(SourceStrategy.GROOVY)

        project.with {
            apply plugin: 'com.fipsoft.maven-repo-settings'
        }

        project.with {
            repositories {
                mavenCentral()

                def filePath = mockSettingsFilePath

                mavenInternal {
                    conf {
                        source filePath
                    }

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

        withMockRepo(1) {
            assertThat(ext.repos, CoreMatchers.hasItem(it))
        }

        withMockRepo(2) {
            assertThat(ext.repos, CoreMatchers.hasItem(it))
        }
    }

    @Test
    void assertCredentialsAreCorrect() {
        MavenRepoSettingsExtension ext = project.extensions.mavenInternal
        MavenRepoSettingsAdapter adapter = ext.adapter

        withMockRepo(1) { MavenRepo repo ->
            def credentials = adapter.settingsAware.resolveCredentials(repo)

            assert 'username1' == credentials.first
            assert 'password1' == credentials.second
        }

    }


    @Test
    void nonExistingRepoShouldThrowServerNodeNotFound() {
        MavenRepoSettingsExtension ext = project.extensions.mavenInternal
        MavenRepoSettingsAdapter adapter = ext.adapter

        withMockRepo(9999999) { MavenRepo r ->
            thrown.expect(ServerNodeNotFoundException.class)
            thrown.expectMessage("server node for ${r.id} not found")
            adapter.settingsAware.resolveCredentials(r)
        }
    }
}
