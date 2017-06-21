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

/**
 * BugsController - Controller to handle bugs
 *
 */
var BugsController = (function(dashboardId) {

  var observable = new Event('BugsController');
  var service = Service.get(Service.types.bugs, dashboardId);

  function getBugs(response) {
  
    var bugs;

    if(response) {
      bugs = [];
      response = JSON.parse(response);
      for(var index in response) {
          var bug = new Bug(
            response[index].id,
            response[index].priority,
            response[index].status
          );
          bugs.push(bug);
      }
    }

    observable.notify(bugs);
  }

  this.observable = observable;
  this.dispose = function() {
    this.observable.reset();
    service.removeListener(getBugs);
  };
  this.init = function() { 
    service.addListener(getBugs); 
  };

});