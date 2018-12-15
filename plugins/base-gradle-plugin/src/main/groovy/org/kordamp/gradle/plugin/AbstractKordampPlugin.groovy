/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018 Andres Almiray.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kordamp.gradle.plugin

import org.gradle.api.Project

/**
 * @author Andres Almiray
 * @since 0.11.0
 */
abstract class AbstractKordampPlugin implements KordampPlugin {
    boolean enabled = true

    private final String visitedKey

    AbstractKordampPlugin() {
        visitedKey = this.class.name.replace('.', '_') + '_VISITED'
    }

    protected boolean hasBeenVisited(Project project) {
        return project.findProperty(visitedKey + '_' + project.name)
    }

    protected void setVisited(Project project, boolean visited) {
        project.ext.set(visitedKey + '_' + project.name, visited)
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled
    }
}
