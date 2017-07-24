/*
 * Copyright 2017 Banco Bilbao Vizcaya Argentaria, S.A.
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
package com.bbva.arq.devops.ae.mirrorgate.utils;

import com.bbva.arq.devops.ae.mirrorgate.core.dto.BuildStats;
import com.bbva.arq.devops.ae.mirrorgate.core.dto.FailureTendency;

import static com.bbva.arq.devops.ae.mirrorgate.core.dto.FailureTendency.*;

public class BuildStatsUtils {

    private BuildStatsUtils() {

    }

    public static BuildStats combineBuildStats(BuildStats... stats) {
        BuildStats result = new BuildStats();

        for(BuildStats stat : stats) {
            result
                    .setCount(stat.getCount() + result.getCount())
                    .setDuration(stat.getCount() * stat.getDuration() + result.getDuration())
                    .setFailureRate(stat.getCount() * stat.getFailureRate() + result.getFailureRate());
        }

        if(result.getCount() > 0) {
            result.setDuration(result.getDuration() / result.getCount());
            result.setFailureRate(result.getFailureRate() / result.getCount());
        }

        return result;
    }

    public static FailureTendency failureTendency(long failureRateSevenDaysBefore, long failureRateFifteenDaysBefore){
        if(failureRateSevenDaysBefore == 0) {
            return failureRateFifteenDaysBefore == 0 ? equal : down;
        }

        long failureTendencyPercentage =
                (100 * (failureRateSevenDaysBefore - failureRateFifteenDaysBefore))
                / failureRateSevenDaysBefore;
        return Math.abs(failureTendencyPercentage) < 5 ? equal : failureTendencyPercentage <= 0 ? up : down;
    }
}