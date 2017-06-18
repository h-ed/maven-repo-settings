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

import com.fipsoft.gradle.maven.repo.settings.ext.model.RepoSpec
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before

/**
 * @author Edgar Harutyunyan
 */
class MavenTestBase {
    private static final String VALID_MAVEN_SETTINGS_XML =
            '''<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <servers>

        <server>
            <id>test1</id>
            <username>username1</username>
            <password>password1</password>
        </server>

        <server>
            <id>test2</id>
            <username>username2</username>
            <password>password2</password>
        </server>

    </servers>

</settings>'''


    File settingsFile
    Project project

    @Before
    void createCredentialsSource() {
        initSettingsFile()
        project = new ProjectBuilder().build()
    }

    @After
    void deleteSettingsXml() {
        settingsFile.delete()
        project = null
    }

    protected void initSettingsFile() {
        settingsFile = new File('build/tmp/', 'settings.xml')
        settingsFile << VALID_MAVEN_SETTINGS_XML
    }

    static withRepoSpec(int num, Closure c) {
        def r = new RepoSpec(repoId: "test${num}", repoUrl: "https://example${num}.com/")
        c.call(r)
    }
}
