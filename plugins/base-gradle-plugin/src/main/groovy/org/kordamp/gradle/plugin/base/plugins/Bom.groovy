/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2019 Andres Almiray.
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
package org.kordamp.gradle.plugin.base.plugins

import groovy.transform.Canonical
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.kordamp.gradle.plugin.base.ProjectConfigurationExtension
import org.kordamp.gradle.plugin.base.model.PomOptions

import static org.kordamp.gradle.StringUtils.isNotBlank

/**
 * @author Andres Almiray
 * @since 0.9.0
 */
@CompileStatic
@Canonical
class Bom extends AbstractFeature implements PomOptions {
    Set<String> compile = new LinkedHashSet<>()
    Set<String> runtime = new LinkedHashSet<>()
    Set<String> test = new LinkedHashSet<>()
    Set<String> excludes = new LinkedHashSet<>()

    boolean autoIncludes = true
    private boolean autoIncludesSet

    String parent
    boolean overwriteInceptionYear
    boolean overwriteUrl
    boolean overwriteLicenses
    boolean overwriteScm
    boolean overwriteOrganization
    boolean overwriteDevelopers
    boolean overwriteContributors

    private boolean overwriteInceptionYearSet
    private boolean overwriteUrlSet
    private boolean overwriteLicensesSet
    private boolean overwriteScmSet
    private boolean overwriteOrganizationSet
    private boolean overwriteDevelopersSet
    private boolean overwriteContributorsSet

    Bom(Project project) {
        super(project)
    }

    @Override
    String toString() {
        toMap().toString()
    }

    @Override
    @CompileDynamic
    Map<String, Map<String, Object>> toMap() {
        if (!isRoot()) return [:]

        Map map = [enabled: enabled]

        if (enabled) {
            map.autoIncludes = autoIncludes
            map.compile = compile
            map.runtime = runtime
            map.test = test
            map.excludes = excludes
            if (isNotBlank(parent)) {
                map.parent = parent
                map.overwriteInceptionYear = overwriteInceptionYear
                map.overwriteUrl = overwriteUrl
                map.overwriteLicenses = overwriteLicenses
                map.overwriteScm = overwriteScm
                map.overwriteOrganization = overwriteOrganization
                map.overwriteDevelopers = overwriteDevelopers
                map.overwriteContributors = overwriteContributors
            }
        }

        ['bom': map]
    }

    void compile(String str) {
        compile << str
    }

    void runtime(String str) {
        runtime << str
    }

    void test(String str) {
        test << str
    }

    void exclude(String str) {
        excludes << str
    }

    void setAutoIncludes(boolean autoIncludes) {
        this.autoIncludes = autoIncludes
        this.autoIncludesSet = true
    }

    boolean isAutoIncludesSet() {
        this.autoIncludesSet
    }

    boolean isOverwriteInceptionYearSet() {
        return overwriteInceptionYearSet
    }

    boolean isOverwriteUrlSet() {
        return overwriteUrlSet
    }

    boolean isOverwriteLicensesSet() {
        return overwriteLicensesSet
    }

    boolean isOverwriteScmSet() {
        return overwriteScmSet
    }

    boolean isOverwriteOrganizationSet() {
        return overwriteOrganizationSet
    }

    boolean isOverwriteDevelopersSet() {
        return overwriteDevelopersSet
    }

    boolean isOverwriteContributorsSet() {
        return overwriteContributorsSet
    }

    void copyInto(Bom copy) {
        super.copyInto(copy)
        copy.compile.addAll(compile)
        copy.runtime.addAll(runtime)
        copy.test.addAll(test)
        copy.excludes.addAll(excludes)
        copy.@autoIncludes = this.autoIncludes
        copy.@autoIncludesSet = this.autoIncludesSet

        copy.parent = this.parent
        copy.@overwriteInceptionYear = this.overwriteInceptionYear
        copy.@overwriteInceptionYearSet = this.overwriteInceptionYearSet
        copy.@overwriteUrl = this.overwriteUrl
        copy.@overwriteUrlSet = this.overwriteUrlSet
        copy.@overwriteLicenses = this.overwriteLicenses
        copy.@overwriteLicensesSet = this.overwriteLicensesSet
        copy.@overwriteScm = this.overwriteScm
        copy.@overwriteScmSet = this.overwriteScmSet
        copy.@overwriteOrganization = this.overwriteOrganization
        copy.@overwriteOrganizationSet = this.overwriteOrganizationSet
        copy.@overwriteDevelopers = this.overwriteDevelopers
        copy.@overwriteDevelopersSet = this.overwriteDevelopersSet
        copy.@overwriteContributors = this.overwriteContributors
        copy.@overwriteContributorsSet = this.overwriteContributorsSet
    }

    @CompileDynamic
    static void merge(Bom o1, Bom o2) {
        AbstractFeature.merge(o1, o2)
        o1.compile.addAll(((o1.compile ?: []) + (o2.compile ?: [])).unique())
        o1.runtime.addAll(((o1.runtime ?: []) + (o2.runtime ?: [])).unique())
        o1.test.addAll(((o1.test ?: []) + (o2.test ?: [])).unique())
        o1.excludes.addAll(((o1.excludes ?: []) + (o2.excludes ?: [])).unique())
        o1.setAutoIncludes((boolean) (o1.autoIncludesSet ? o1.autoIncludes : o2.autoIncludes))

        o1.parent = o1.parent ?: o2?.parent
        o1.setOverwriteInceptionYear((boolean) (o1.overwriteInceptionYearSet ? o1.overwriteInceptionYear : o2.overwriteInceptionYear))
        o1.setOverwriteUrl((boolean) (o1.overwriteUrlSet ? o1.overwriteUrl : o2.overwriteUrl))
        o1.setOverwriteLicenses((boolean) (o1.overwriteLicensesSet ? o1.overwriteLicenses : o2.overwriteLicenses))
        o1.setOverwriteScm((boolean) (o1.overwriteScmSet ? o1.overwriteScm : o2.overwriteScm))
        o1.setOverwriteOrganization((boolean) (o1.overwriteOrganizationSet ? o1.overwriteOrganization : o2.overwriteOrganization))
        o1.setOverwriteDevelopers((boolean) (o1.overwriteDevelopersSet ? o1.overwriteDevelopers : o2.overwriteDevelopers))
        o1.setOverwriteContributors((boolean) (o1.overwriteContributorsSet ? o1.overwriteContributors : o2.overwriteContributors))
    }

    List<String> validate(ProjectConfigurationExtension extension) {
        List<String> errors = []

        if (!enabled) return errors

        errors
    }
}
