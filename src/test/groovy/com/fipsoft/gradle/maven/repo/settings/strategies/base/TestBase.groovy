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
package com.fipsoft.gradle.maven.repo.settings.strategies.base

import com.fipsoft.gradle.maven.repo.settings.model.DefaultMavenRepoEntry
import com.fipsoft.gradle.maven.repo.settings.model.SourceStrategy
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.AfterClass

import static SourceStrategy.MAVEN

/**
 * @author Edgar Harutyunyan
 */
class TestBase {
    static final String VALID_MAVEN_SETTINGS_XML =
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


    static final String VALID_GROOVY_SETTINGS =
            '''
//maven authentication
servers = [

        [
                id      : 'test1',
                username: 'username1',
                password: 'password1'
        ],

        [
                id      : 'test2',
                username: 'username2',
                password: 'password2'
        ]
]
'''


    private static final String settingsFileDirectory = "build/tmp"
    private SourceStrategy strategy
    static File mockSettingsSourceFile
    static Project project


    TestBase(SourceStrategy strategy) {
        this.strategy = strategy
        println "initializing test project with strategy: ${strategy}..."

        final File dir = new File(settingsFileDirectory)
        if (dir.exists()) {
            dir.delete()
        }

        dir.mkdirs()

        mockSettingsSourceFile = new File(dir, "settings." + (strategy == MAVEN ? 'xml' : 'groovy'))
        mockSettingsSourceFile.setText('')
        mockSettingsSourceFile << (strategy == MAVEN ? VALID_MAVEN_SETTINGS_XML : VALID_GROOVY_SETTINGS)
        project = new ProjectBuilder().build()
    }


    @AfterClass
    static void destroy() {
        mockSettingsSourceFile.delete()
        project = null
    }

    static String getMockSettingsFilePath() {
        return mockSettingsSourceFile.toURI().path
    }


    static withMockRepo(int repoNum, Closure c) {
        def repo = new DefaultMavenRepoEntry(serverId: "test${repoNum}", serverUrl: "https://example${repoNum}.com/")
        c.call(repo)
    }
}
