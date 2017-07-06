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
package com.fipsoft.gradle.maven.repo.settings.api;

import com.fipsoft.gradle.maven.repo.settings.ext.MavenRepoSettingsExtension;
import com.fipsoft.gradle.maven.repo.settings.model.SourceStrategy;

import java.io.File;

/**
 * @author Edgar Harutyunyan
 * @since 0.3.0
 */
public abstract class AbstractMavenRepoSettingsAware<T extends MavenRepo> implements MavenRepoSettingsAware<T> {
    private static final String GLOBAL_SETTINGS_FILE_PATH;
    private final String customSettingsSource;
    private final SourceStrategy sourceStrategy;

    static {
        String mvn_home = System.getenv("M2_HOME");

        if (mvn_home == null) {
            //old maven
            mvn_home = System.getenv("MAVEN_HOME");
        }

        GLOBAL_SETTINGS_FILE_PATH = mvn_home;
    }

    public AbstractMavenRepoSettingsAware(MavenRepoSettingsExtension ext, SourceStrategy strategy) {
        this.sourceStrategy = strategy;
        this.customSettingsSource = ext.getConfigObject().getCustomSettingsFilePath();
    }

    public File getGlobalSettingsFile() {
        return new File(GLOBAL_SETTINGS_FILE_PATH, "conf/settings.xml");
    }

    public File getCustomSettingsFile() {
        return new File(customSettingsSource);
    }

    @Override
    public SourceStrategy getSourceStrategy() {
        return sourceStrategy;
    }

    public abstract void printFoundRepos();
}
