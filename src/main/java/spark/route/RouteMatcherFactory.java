/*
 * Copyright © 2011 Per Wendel. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package spark.route;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

/**
 * RouteMatcherFactory
 *
 * @author Per Wendel
 */
public final class RouteMatcherFactory {
    private static final Logger LOG = getLogger (RouteMatcherFactory.class);

    private static RouteMatcher routeMatcher;

    private RouteMatcherFactory () {
        super ();
    }

    public static synchronized RouteMatcher get () {
        if (routeMatcher == null) {
            LOG.debug ("creates RouteMatcher");
            routeMatcher = new SimpleRouteMatcher ();
        }
        return routeMatcher;
    }
}
