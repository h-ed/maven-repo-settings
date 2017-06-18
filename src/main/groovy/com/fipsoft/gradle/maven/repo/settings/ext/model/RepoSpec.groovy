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

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.gradle.api.Incubating

/**
 * @author Edgar Harutyunyan
 * @since 0.1
 */
@Incubating
@ToString(includes = 'repoId', includePackage = false)
@EqualsAndHashCode(includes = ['repoId', 'repoUrl'])
class RepoSpec {
    String repoId
    transient String repoUrl

    def id(String repoId) {
        this.repoId = repoId
    }

    def url(String url) {
        this.repoUrl = url
    }
}
