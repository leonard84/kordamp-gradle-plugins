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
package org.kordamp.gradle.plugin.insight.reports

import groovy.transform.CompileStatic
import org.gradle.api.invocation.Gradle
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.kordamp.gradle.plugin.insight.InsightExtension
import org.kordamp.gradle.plugin.insight.internal.InsightExtensionImpl
import org.kordamp.gradle.plugin.insight.model.Build
import org.kordamp.gradle.plugin.insight.model.BuildReport
import org.kordamp.gradle.plugin.insight.model.Project
import org.kordamp.gradle.plugin.insight.model.Task

import javax.inject.Inject

/**
 *
 * @author Andres Almiray
 * @since 0.40.0
 */
@CompileStatic
class SummaryBuildReport implements BuildReport {
    final Property<String> format
    final Property<Boolean> enabled
    final Property<Boolean> zeroPadding
    final Property<Integer> maxProjectPathSize
    final Property<Double> confTimeThreshold
    final Property<Double> execTimeThreshold

    @Inject
    SummaryBuildReport(ObjectFactory objects) {
        this.format = objects.property(String).convention('short')
        this.enabled = objects.property(Boolean).convention(true)
        this.zeroPadding = objects.property(Boolean).convention(false)
        this.maxProjectPathSize = objects.property(Integer).convention(36)
        this.confTimeThreshold = objects.property(Double).convention(0.5d)
        this.execTimeThreshold = objects.property(Double).convention(120d)
    }

    @Override
    void report(Gradle gradle, InsightExtension extension, Build build) throws Exception {
        if (build.projects.values().every { it.state == Project.State.HIDDEN }) return

        List<Project> projects = []
        projects.addAll(build.projects.values())

        InsightExtensionImpl x = (InsightExtensionImpl) extension

        int paddingSize = Math.max(10, maxProjectPathSize.get())
        String padding = ' ' * paddingSize
        String header = '             CONF        EXEC   '
        if ('long'.equalsIgnoreCase(format.get())) {
            header += ' ' + ['TOT', 'EXE', 'FLD', 'SKP', 'UTD', 'WRK', 'CHD', 'NSR', 'ACT'].join(' ')
        }

        String separator = '-' * (padding.size() + header.size())
        println('\n' + separator)
        println(padding + header)
        println(separator)

        double totalConfDuration = 0
        double totalExecDuration = 0

        int[] totals = new int[9]
        Arrays.fill(totals, 0)

        for (Project project : projects) {
            if (project.state == Project.State.HIDDEN) continue

            totalConfDuration += project.confDuration
            totalExecDuration += project.execDuration

            String confDuration = formatDuration(project.confDuration)
            String execDuration = formatDuration(project.execDuration)
            if (confTimeThreshold.get() < project.confDuration) {
                confDuration = x.colors.failure(confDuration)
            }
            if (execTimeThreshold.get() < project.execDuration) {
                execDuration = x.colors.failure(execDuration)
            }

            String row = (project.path + ' ').padRight(paddingSize, '.') +
                ' ' + x.colors.state(project.state) + ' ' +
                '[' + confDuration + '] ' +
                '[' + execDuration + ']'
            String details = ''

            if ('long'.equalsIgnoreCase(format.get())) {
                int[] values = new int[9]
                Arrays.fill(values, 0)

                for (Task task : project.tasks.values()) {
                    values[0]++
                    values[1] += task.isExecuted() ? 1 : 0
                    values[2] += task.isFailed() ? 1 : 0
                    values[3] += task.isSkipped() ? 1 : 0
                    values[4] += task.isUpToDate() ? 1 : 0
                    values[5] += task.isDidWork() ? 1 : 0
                    values[6] += task.isFromCache() ? 1 : 0
                    values[7] += task.isNoSource() ? 1 : 0
                    values[8] += task.isActionable() ? 1 : 0
                }

                int index = 0
                details = values.collect { v ->
                    index++
                    String.valueOf(v == 0 ? (index == 1 || zeroPadding.get() ? v : ' ') : v).padLeft(3, ' ')
                }.join(' ')

                (0..8).each { i -> totals[i] += values[i] }
            }

            println(row + ' ' + details)
        }

        println(separator)

        /*
        String row = padding +
            ' ' + x.colors.result(build.failure) + ' ' +
            '[' + formatDuration(totalConfDuration) + '] ' +
            '[' + formatDuration(totalExecDuration) + ']'
        String details = ''

        if ('long'.equalsIgnoreCase(format.get())) {
            int index = 0
            details = totals.collect { v ->
                index++
                String.valueOf(v == 0 ? (index == 1 || zeroPadding.get() ? v : ' ') : v).padLeft(3, ' ')
            }.join(' ')
        }
        println(row + ' ' + details)

        println(separator)
        */
    }

    private static String formatDuration(double time) {
        String formatted = String.format('%.3f', time) + ' s'
        if (time >= 60d) {
            int seconds = (int) time
            String m = String.valueOf((int) (seconds / 60))
            String s = String.valueOf(seconds % 60)
            formatted = "${m.padLeft(2, '0')}:${s.padLeft(2, '0')}  m".toString()
        }

        formatted.padLeft(9, ' ')
    }
}