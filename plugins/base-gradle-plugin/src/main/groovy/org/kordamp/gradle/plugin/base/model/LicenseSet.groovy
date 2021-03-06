/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2020 Andres Almiray.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kordamp.gradle.plugin.base.model

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.kordamp.gradle.plugin.base.ProjectConfigurationExtension
import org.kordamp.gradle.util.ConfigureUtil

/**
 * @author Andres Almiray
 * @since 0.8.0
 */
@CompileStatic
class LicenseSet {
    final List<License> licenses = []

    @CompileDynamic
    Map<String, Map<String, Object>> toMap() {
        if (isEmpty()) return [:]

        licenses.collectEntries { License license ->
            [(license.id ?: license.name): license.toMap()]
        }
    }

    void license(Action<? super License> action) {
        License license = new License()
        action.execute(license)
        licenses << license
    }

    void license(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = License) Closure<Void> action) {
        License license = new License()
        ConfigureUtil.configure(action, license)
        licenses << license
    }

    static void merge(LicenseSet o1, LicenseSet o2) {
        Map<String, License> a = o1.licenses.collectEntries { [(it.name): it] }
        Map<String, License> b = o2.licenses.collectEntries { [(it.name): it] }

        a.each { k, license ->
            License.merge(license, b.remove(k))
        }
        a.putAll(b)
        o1.licenses.clear()
        o1.licenses.addAll(a.values())
    }

    void forEach(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = License) Closure<Void> action) {
        licenses.each(action)
    }

    List<String> validate(ProjectConfigurationExtension extension) {
        List<String> errors = []

        if (licenses.isEmpty()) {
            errors << "[${extension.project.name}] No licenses have been defined".toString()
        }

        errors
    }

    boolean isEmpty() {
        licenses.isEmpty()
    }
}
