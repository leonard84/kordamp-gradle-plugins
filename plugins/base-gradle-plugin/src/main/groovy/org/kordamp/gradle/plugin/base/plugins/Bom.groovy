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
package org.kordamp.gradle.plugin.base.plugins

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.kordamp.gradle.plugin.base.ProjectConfigurationExtension
import org.kordamp.gradle.plugin.base.model.PomOptions
import org.kordamp.gradle.plugin.base.model.artifact.Dependency
import org.kordamp.gradle.plugin.base.model.artifact.DependencySpec
import org.kordamp.gradle.plugin.base.model.artifact.HasModulesSpec
import org.kordamp.gradle.plugin.base.model.artifact.internal.DependencySpecImpl
import org.kordamp.gradle.util.CollectionUtils
import org.kordamp.gradle.util.ConfigureUtil

import static org.kordamp.gradle.util.StringUtils.isBlank
import static org.kordamp.gradle.util.StringUtils.isNotBlank

/**
 * @author Andres Almiray
 * @since 0.9.0
 */
@CompileStatic
class Bom extends AbstractFeature implements PomOptions {
    static final String PLUGIN_ID = 'org.kordamp.gradle.bom'

    final String packaging = 'pom'

    private final Map<String, Dependency> dependencies = [:]
    Set<String> excludes = new LinkedHashSet<>()
    Set<String> includes = new LinkedHashSet<>()

    Map<String, String> properties = new LinkedHashMap<>()

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
    boolean overwriteIssueManagement
    boolean overwriteCiManagement
    boolean overwriteMailingLists

    private boolean overwriteInceptionYearSet
    private boolean overwriteUrlSet
    private boolean overwriteLicensesSet
    private boolean overwriteScmSet
    private boolean overwriteOrganizationSet
    private boolean overwriteDevelopersSet
    private boolean overwriteContributorsSet
    private boolean overwriteIssueManagementSet
    private boolean overwriteCiManagementSet
    private boolean overwriteMailingListsSet

    Bom(ProjectConfigurationExtension config, Project project) {
        super(config, project, PLUGIN_ID)
    }

    @Override
    protected AbstractFeature getParentFeature() {
        return project.rootProject.extensions.getByType(ProjectConfigurationExtension).bom
    }

    @Override
    protected void normalizeEnabled() {
        if (!enabledSet) {
            setEnabled(isApplied())
            if (isApplied()) {
                ProjectConfigurationExtension config = project.extensions.getByType(ProjectConfigurationExtension)
                config.docs.javadoc.enabled = false
                config.docs.groovydoc.enabled = false
                config.docs.kotlindoc.enabled = false
                config.docs.scaladoc.enabled = false
                config.docs.sourceHtml.enabled = false
                config.docs.sourceXref.enabled = false
                config.artifacts.source.enabled = false
            }
        }
    }

    Map<String, Dependency> getDependencies() {
        Collections.unmodifiableMap(dependencies)
    }

    @Override
    Map<String, Map<String, Object>> toMap() {
        Map<String, Object> map = new LinkedHashMap<String, Object>(enabled: enabled)

        map.autoIncludes = autoIncludes
        map.dependencies = dependencies
        map.excludes = excludes
        map.includes = includes
        if (isNotBlank(parent)) {
            map.parent = parent
            map.overwriteInceptionYear = overwriteInceptionYear
            map.overwriteUrl = overwriteUrl
            map.overwriteLicenses = overwriteLicenses
            map.overwriteScm = overwriteScm
            map.overwriteOrganization = overwriteOrganization
            map.overwriteDevelopers = overwriteDevelopers
            map.overwriteContributors = overwriteContributors
            map.overwriteIssueManagement = overwriteIssueManagement
            map.overwriteCiManagement = overwriteCiManagement
            map.overwriteMailingLists = overwriteMailingLists
        }
        map.properties = properties

        new LinkedHashMap<>('bom': map)
    }

    void exclude(String str) {
        if (isNotBlank(str)) excludes << str.toString()
    }

    void include(String str) {
        if (isNotBlank(str)) includes << str.toString()
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

    boolean isOverwriteIssueManagementSet() {
        return overwriteIssueManagementSet
    }

    boolean isOverwriteCiManagementSet() {
        return overwriteCiManagementSet
    }

    boolean isOverwriteMailingListsSet() {
        return overwriteMailingListsSet
    }

    static void merge(Bom o1, Bom o2) {
        AbstractFeature.merge(o1, o2)
        CollectionUtils.merge(o1.@dependencies, o2.@dependencies)
        o1.excludes = CollectionUtils.merge(o1.excludes, o2.excludes, false)
        o1.includes = CollectionUtils.merge(o1.includes, o2.includes, false)
        o1.properties = CollectionUtils.merge(o1.properties, o2.properties, false)
        o1.setAutoIncludes((boolean) (o1.autoIncludesSet ? o1.autoIncludes : o2.autoIncludes))

        o1.parent = o1.parent ?: o2?.parent
        o1.setOverwriteInceptionYear((boolean) (o1.overwriteInceptionYearSet ? o1.overwriteInceptionYear : o2.overwriteInceptionYear))
        o1.setOverwriteUrl((boolean) (o1.overwriteUrlSet ? o1.overwriteUrl : o2.overwriteUrl))
        o1.setOverwriteLicenses((boolean) (o1.overwriteLicensesSet ? o1.overwriteLicenses : o2.overwriteLicenses))
        o1.setOverwriteScm((boolean) (o1.overwriteScmSet ? o1.overwriteScm : o2.overwriteScm))
        o1.setOverwriteOrganization((boolean) (o1.overwriteOrganizationSet ? o1.overwriteOrganization : o2.overwriteOrganization))
        o1.setOverwriteDevelopers((boolean) (o1.overwriteDevelopersSet ? o1.overwriteDevelopers : o2.overwriteDevelopers))
        o1.setOverwriteContributors((boolean) (o1.overwriteContributorsSet ? o1.overwriteContributors : o2.overwriteContributors))
        o1.setOverwriteIssueManagement((boolean) (o1.overwriteIssueManagementSet ? o1.overwriteIssueManagement : o2.overwriteIssueManagement))
        o1.setOverwriteCiManagement((boolean) (o1.overwriteCiManagementSet ? o1.overwriteCiManagement : o2.overwriteCiManagement))
        o1.setOverwriteMailingLists((boolean) (o1.overwriteMailingListsSet ? o1.overwriteMailingLists : o2.overwriteMailingLists))
    }

    List<String> validate(ProjectConfigurationExtension extension) {
        List<String> errors = []

        if (!enabled) return errors

        errors
    }

    void dependency(Dependency dependency) {
        if (dependency) {
            dependencies[dependency.name] = dependency
        }
    }

    /**
     * Defines a dependency.</p>
     * Dependency name will be equal to parsed {@code artifactId}.</p>
     * Example:
     *
     * <pre>
     * dependency('com.googlecode.guava:guava:29.0-jre')
     * </pre>
     * @param gavNotation must be in the {@code groupId:artifactId:version} notation
     */
    Dependency dependency(String gavNotation) {
        if (isBlank(gavNotation)) {
            throw new IllegalArgumentException('Dependency notation cannot be blank.')
        }
        DependencySpecImpl d = DependencySpecImpl.parsePartial(project.rootProject, gavNotation)
        d.validate(project)
        dependencies[d.name] = d.asDependency()
    }

    /**
     * Defines a dependency.</p>
     * Example:
     *
     * <pre>
     * dependency('guava', 'com.googlecode.guava:guava:29.0-jre')
     * </pre>
     *
     * @param name the logical name of the dependency
     * @param gavNotation  must be in the {@code groupId:artifactId:version} notation
     */
    Dependency dependency(String name, String gavNotation) {
        if (isBlank(name)) {
            throw new IllegalArgumentException('Dependency name cannot be blank.')
        }
        DependencySpecImpl d = DependencySpecImpl.parse(project.rootProject, name.trim(), gavNotation)
        d.validate(project)
        dependencies[d.name] = d.asDependency()
    }

    /**
     * Defines and configures a dependency.</p>
     * Example:
     *
     * <pre>
     * dependency('groovy', 'org.codehaus.groovy:groovy:3.0.6') {
     *     modules = [
     *         'groovy-test',
     *         'groovy-json',
     *         'groovy-xml'
     *     ]
     * }
     * </pre>
     *
     * @param name the logical name of the dependency
     * @param gavNotation  must be in the {@code groupId:artifactId:version} notation
     */
    Dependency dependency(String name, String gavNotation, Action<? super HasModulesSpec> action) {
        if (isBlank(name)) {
            throw new IllegalArgumentException('Dependency name cannot be blank.')
        }
        DependencySpecImpl d = DependencySpecImpl.parse(project.rootProject, name.trim(), gavNotation)
        action.execute(d)
        d.validate(project)
        dependencies[d.name] = d.asDependency()
    }

    /**
     * Defines and configures a dependency.</p>
     * Example:
     *
     * <pre>
     * dependency('groovy', 'org.codehaus.groovy:groovy:3.0.6') {
     *     modules = [
     *         'groovy-test',
     *         'groovy-json',
     *         'groovy-xml'
     *     ]
     * }
     * </pre>
     *
     * @param name the logical name of the dependency
     * @param gavNotation  must be in the {@code groupId:artifactId:version} notation
     */
    Dependency dependency(String name, String gavNotation, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DependencySpec) Closure<Void> action) {
        if (isBlank(name)) {
            throw new IllegalArgumentException('Dependency name cannot be blank.')
        }
        DependencySpecImpl d = DependencySpecImpl.parse(project.rootProject, name.trim(), gavNotation)
        ConfigureUtil.configure(action, d)
        d.validate(project)
        dependencies[d.name] = d.asDependency()
    }

    /**
     * Defines a platform dependency.</p>
     * Dependency name will be equal to parsed {@code artifactId}.</p>
     * Example:
     *
     * <pre>
     * platform('io.micronaut:micronaut-bom:2.0.2')
     * </pre>
     *
     * @param gavNotation  must be in the {@code groupId:artifactId:version} notation
     */
    Dependency platform(String gavNotation) {
        if (isBlank(gavNotation)) {
            throw new IllegalArgumentException('Platform notation cannot be blank.')
        }
        DependencySpecImpl d = DependencySpecImpl.parsePartial(project.rootProject, gavNotation)
        d.platform = true
        d.validate(project)
        dependencies[d.name] = d.asDependency()
    }

    /**
     * Defines a platform dependency.</p>
     * Example:
     *
     * <pre>
     * platform('micronaut', 'io.micronaut:micronaut-bom:2.0.2')
     * </pre>
     *
     * @param name the logical name of the dependency
     * @param gavNotation  must be in the {@code groupId:artifactId:version} notation
     */
    Dependency platform(String name, String gavNotation) {
        if (isBlank(name)) {
            throw new IllegalArgumentException('Platform name cannot be blank.')
        }
        DependencySpecImpl d = DependencySpecImpl.parse(project.rootProject, name.trim(), gavNotation)
        d.platform = true
        d.validate(project)
        dependencies[d.name] = d.asDependency()
    }

    /**
     * Defines and configures a platform dependency.</p>
     * Example:
     *
     * <pre>
     * platform('micronaut', 'io.micronaut:micronaut-bom:2.0.2') {
     *     modules = [
     *         'micronaut-core',
     *         'micronaut-inject',
     *         'micronaut-validation'
     *     ]
     * }
     * </pre>
     *
     * @param name the logical name of the dependency
     * @param gavNotation  must be in the {@code groupId:artifactId:version} notation
     */
    Dependency platform(String name, String gavNotation, Action<? super HasModulesSpec> action) {
        if (isBlank(name)) {
            throw new IllegalArgumentException('Platform name cannot be blank.')
        }
        DependencySpecImpl d = DependencySpecImpl.parse(project.rootProject, name.trim(), gavNotation)
        d.platform = true
        action.execute(d)
        d.validate(project)
        dependencies[d.name] = d.asDependency()
    }

    /**
     * Defines and configures a platform dependency.</p>
     * Example:
     *
     * <pre>
     * platform('micronaut', 'io.micronaut:micronaut-bom:2.0.2') {
     *     modules = [
     *         'micronaut-core',
     *         'micronaut-inject',
     *         'micronaut-validation'
     *     ]
     * }
     * </pre>
     *
     * @param name the logical name of the dependency
     * @param gavNotation  must be in the {@code groupId:artifactId:version} notation
     */
    Dependency platform(String name, String gavNotation, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DependencySpec) Closure<Void> action) {
        if (isBlank(name)) {
            throw new IllegalArgumentException('Platform name cannot be blank.')
        }
        DependencySpecImpl d = DependencySpecImpl.parse(project.rootProject, name.trim(), gavNotation)
        d.platform = true
        ConfigureUtil.configure(action, d)
        d.validate(project)
        dependencies[d.name] = d.asDependency()
    }

    /**
     * Defines and configures a dependency by gavNotation.</p>
     * Example:
     *
     * <pre>
     * dependency('org.codehaus.groovy:groovy:3.0.6') {
     *     modules = [
     *         'groovy-test',
     *         'groovy-json',
     *         'groovy-xml'
     *     ]
     * }
     * </pre>
     *
     * @param gavNotation  must be in the {@code groupId:artifactId:version} notation
     */
    Dependency dependency(String gavNotation, Action<? super DependencySpec> action) {
        if (isBlank(gavNotation)) {
            throw new IllegalArgumentException('Dependency gavNotation cannot be blank.')
        }
        DependencySpecImpl d = DependencySpecImpl.parsePartial(project.rootProject, gavNotation.trim())
        action.execute(d)
        d.validate(project)
        dependencies[d.name] = d.asDependency()
    }

    /**
     * Defines and configures a dependency by gavNotation.</p>
     * Example:
     *
     * <pre>
     * dependency('org.codehaus.groovy:groovy:3.0.6') {
     *     modules = [
     *         'groovy-test',
     *         'groovy-json',
     *         'groovy-xml'
     *     ]
     * }
     * </pre>
     *
     * @param gavNotation  must be in the {@code groupId:artifactId:version} notation
     */
    Dependency dependency(String gavNotation, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DependencySpec) Closure<Void> action) {
        if (isBlank(gavNotation)) {
            throw new IllegalArgumentException('Dependency gavNotation cannot be blank.')
        }
        DependencySpecImpl d = DependencySpecImpl.parsePartial(project.rootProject, gavNotation.trim())
        ConfigureUtil.configure(action, d)
        d.validate(project)
        dependencies[d.name] = d.asDependency()
    }

    Dependency getDependency(String name) {
        if (isBlank(name)) {
            throw new IllegalArgumentException('Dependency name cannot be blank.')
        }

        if (dependencies.containsKey(name)) {
            return dependencies.get(name)
        }
        throw new IllegalArgumentException("Undeclared dependency ${name}.")
    }
}
