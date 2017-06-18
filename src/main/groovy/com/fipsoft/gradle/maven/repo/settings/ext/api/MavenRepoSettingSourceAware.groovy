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
package com.fipsoft.gradle.maven.repo.settings.ext.api

import com.fipsoft.gradle.maven.repo.settings.ext.model.RepoSpec

/**
 * @author Edgar Harutyunyan
 * @since 0.1
 */
interface MavenRepoSettingSourceAware {

    //first see if older version of maven_home
    static String maven_home_settings = (System.getenv("MAVEN_HOME") ?:
            System.getenv("M2_HOME")) + "/conf/settings.xml"

    String getStrategyName()

    String getSettingsFilePath()

    CredentialProvider getCredentialProvider()

    void validateEntries(Collection<? extends RepoSpec> repoSpec)
}