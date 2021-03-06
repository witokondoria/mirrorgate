/*
 * Copyright 2017 Banco Bilbao Vizcaya Argentaria, S.A..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bbva.arq.devops.ae.mirrorgate.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.bbva.arq.devops.ae.mirrorgate.core.dto.BuildDTO;
import com.bbva.arq.devops.ae.mirrorgate.core.dto.BuildStats;
import com.bbva.arq.devops.ae.mirrorgate.core.dto.FailureTendency;
import com.bbva.arq.devops.ae.mirrorgate.utils.BuildStatsUtils;
import com.bbva.arq.devops.ae.mirrorgate.core.utils.BuildStatus;
import com.bbva.arq.devops.ae.mirrorgate.model.Build;
import com.bbva.arq.devops.ae.mirrorgate.service.BuildService;
import com.bbva.arq.devops.ae.mirrorgate.service.DashboardService;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Build controller.
 */
@RestController
public class BuildController {

    private static final long DAY_IN_MS = (long) 1000 * 60 * 60 * 24;

    private final BuildService buildService;
    private final DashboardService dashboardService;

    @Autowired
    public BuildController(BuildService buildService, DashboardService dashboardService) {
        this.buildService = buildService;
        this.dashboardService = dashboardService;
    }

    @RequestMapping(value = "/dashboards/{name}/builds", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> getBuildsByBoardName(@PathVariable("name") String name) {

        Map<String, Object> response = new HashMap<>();
        List<String> repos = dashboardService.getReposByDashboardName(name);

        if (repos == null) {
            return null;
        }

        List<Build> builds = buildService.getAllBranchesLastByReposName(repos);

        response.put("lastBuilds", builds);
        response.put("stats", getStats(name));

        return response;
    }

    private BuildStats getStatsWithoutFailureTendency(@PathVariable("name") String name, int daysBefore) {

        List<String> repos = dashboardService.getReposByDashboardName(name);

        if (repos == null) {
            return null;
        }

        Date numberOfDaysBefore = new Date(System.currentTimeMillis() - (daysBefore * DAY_IN_MS));
        Map<BuildStatus, BuildStats> info = buildService.getBuildStatusStatsAfterTimestamp(repos, numberOfDaysBefore.getTime());

        return BuildStatsUtils.combineBuildStats(info.values().toArray(new BuildStats[]{}));
      }

    @RequestMapping(value = "/dashboards/{name}/builds/rate", method = GET,
        produces = APPLICATION_JSON_VALUE)
    public BuildStats getStats(@PathVariable("name") String name) {

        BuildStats statsSevenDaysBefore = getStatsWithoutFailureTendency(name, 7);
        BuildStats statsFifteenDaysBefore = getStatsWithoutFailureTendency(name, 15);

        FailureTendency failureTendency = BuildStatsUtils.failureTendency(statsSevenDaysBefore.getFailureRate(), statsFifteenDaysBefore.getFailureRate());

        statsSevenDaysBefore.setFailureTendency(failureTendency);

        return statsSevenDaysBefore;
    }

    @RequestMapping(value = "/api/builds", method = POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createBuilds(@Valid @RequestBody BuildDTO request) {

        String response = buildService.createOrUpdate(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}