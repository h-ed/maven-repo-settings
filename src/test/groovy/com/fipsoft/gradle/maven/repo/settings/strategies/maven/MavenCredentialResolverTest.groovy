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

import com.fipsoft.gradle.maven.repo.settings.ext.MavenRepoSettingsExtension
import com.fipsoft.gradle.maven.repo.settings.ext.api.CredentialProvider
import com.fipsoft.gradle.maven.repo.settings.ext.model.DefaultSettingSourceResolver
import com.fipsoft.gradle.maven.repo.settings.ext.model.RepoSpec
import com.fipsoft.gradle.maven.repo.settings.utils.ServerNodeNotFoundException
import org.gradle.api.GradleException
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

/**
 * @author Edgar Harutyunyan
 */
class MavenCredentialResolverTest extends CoreTests {

    CredentialProvider credentialProvider

    @Rule
    public ExpectedException thrown = ExpectedException.none()


    @Before
    void setCredentialProvider() {
        MavenRepoSettingsExtension ext = project.extensions.mavenInternal
        credentialProvider = ext.settingAdapter.resolveRepoSourceAware().credentialProvider
    }


    @Test
    void assertCredentialsAreCorrectlyResolving() {
        [1, 2].each { n ->
            withRepoSpec(n) { RepoSpec r ->
                Tuple2<String, String> credentials = credentialProvider
                        .resolveCredentials(r)

                String resolvedUsername = credentials.first
                String resolvePassword = credentials.second

                Assert.assertTrue("username${n}" == resolvedUsername)
                Assert.assertTrue("password${n}" == resolvePassword)
            }
        }
    }


    @Test
    void testWrongServerIdRequest() {
        withRepoSpec(-1) { RepoSpec r ->
            thrown.expect(ServerNodeNotFoundException.class)
            thrown.expectMessage("server node for ${r.repoId} not found")
            credentialProvider.resolveCredentials(r)
        }
    }

    @Test
    void testForInvalidSettingFile() {
        String invalidSettingFilePath = ''
        thrown.expect(GradleException.class)
        thrown.expectMessage("something wrong with settings file: ${invalidSettingFilePath}")
        DefaultSettingSourceResolver.setMavenSettingsFileName(invalidSettingFilePath)

        withRepoSpec(-1) { RepoSpec r ->
            credentialProvider.resolveCredentials(r)
        }
    }
}
