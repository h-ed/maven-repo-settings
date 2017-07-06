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
package com.fipsoft.gradle.maven.repo.settings.api

import com.fipsoft.gradle.maven.repo.settings.model.SourceStrategy

/**
 * @author Edgar Harutyunyan
 * @since 0.3.0
 */
interface MavenRepoSettingsAware<T extends MavenRepo> {

    /**
     * Resolve username and password tuple for the given repository entry
     * @param repo {@code MavenRepo} object which must provide target {@code repoId}
     * @return {@code Tuple2} object consisting of username(left) and password(right)
     * @throws {@code ServerNodeNotFoundException} exception if, given repoId is not found in its source files
     */
    Tuple2<String, String> resolveCredentials(T repo)

    /**
     * Each Implementation has its own target file from which credentials must be resolved
     * @return valid {@code File}
     */
    File getSettingsSourceFile()

    /**
     * Utility method, for distinguishing what's the strategy for resolver implementation
     * @return {@code SourceStrategy} strategy type
     */
    SourceStrategy getSourceStrategy()
}
