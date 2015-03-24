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

package sabina.route;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.logging.Logger.getLogger;
import static java.util.stream.Collectors.toList;
import static sabina.route.MimeParse.bestMatch;

import java.util.*;
import java.util.logging.Logger;

import sabina.Fault;
import sabina.HttpMethod;

/**
 * Simple route matcher that is supposed to work exactly as Sinatra's
 *
 * @author Per Wendel
 */
final class SimpleRouteMatcher implements RouteMatcher {
    private static final Logger LOG = getLogger (SimpleRouteMatcher.class.getName ());
    private static final char SINGLE_QUOTE = '\'';

    private final List<RouteEntry> routes = new ArrayList<> ();

    /** Holds a map of Exception classes and associated handlers. */
    private final Map<Class<? extends Exception>, Fault<?>> exceptionMap = new HashMap<> ();

    SimpleRouteMatcher () {
        LOG.fine ("RouteMatcher created");
    }

    /**
     * Parse and validates a route and adds it
     *
     * @param route the route path
     * @param acceptType the accept type
     * @param target the invocation target
     */
    @Override public void processRoute (String route, String acceptType, Object target) {
        try {
            int singleQuoteIndex = route.indexOf (SINGLE_QUOTE);
            String httpMethod =
                route.substring (0, singleQuoteIndex).trim ().toLowerCase (); // NOSONAR
            String url =
                route.substring (singleQuoteIndex + 1, route.length () - 1).trim (); // NOSONAR

            // Use special enum stuff to get from value
            HttpMethod method;
            try {
                method = HttpMethod.valueOf (httpMethod);
            }
            catch (IllegalArgumentException e) {
                LOG.severe (
                    "The @Route value: "
                        + route
                        + " has an invalid HTTP method part: "
                        + httpMethod
                        + ".");
                return;
            }
            addRoute (method, url, acceptType, target);
        }
        catch (Exception e) {
            String msg = e.getMessage ();
            LOG.severe (
                format ("The @Route value: %s is not in the correct format: %s", route, msg));
        }
    }

    /**
     * finds target for a requested route
     *
     * @param httpMethod the http method
     * @param path the path
     * @param acceptType the accept type
     *
     * @return the target
     */
    @Override public RouteMatch findTarget (
        HttpMethod httpMethod, String path, String acceptType) {

        final List<RouteEntry> routeEntries = this.findTargetsForRequestedRoute (httpMethod, path);
        final RouteEntry entry = findTargetWithGivenAcceptType (routeEntries, acceptType);
        return entry != null?
            new RouteMatch (entry.target, entry.path, path) : null;
    }

    /**
     * Finds multiple targets for a requested route.
     *
     * @param httpMethod the http method
     * @param path the route path
     * @param acceptType the accept type
     *
     * @return the targets
     */
    @Override public List<RouteMatch> findTargets (
        final HttpMethod httpMethod,
        final String path,
        final String acceptType) {

        final List<RouteMatch> matchSet = new ArrayList<> ();
        final List<RouteEntry> routeEntries = findTargetsForRequestedRoute (httpMethod, path);

        for (RouteEntry routeEntry : routeEntries) {
            if (acceptType != null) {
                String bestMatch = bestMatch (asList (routeEntry.acceptedType), acceptType);

                if (routeWithGivenAcceptType (bestMatch))
                    matchSet.add (new RouteMatch (routeEntry.target, routeEntry.path, path));
            }
            else {
                matchSet.add (new RouteMatch (routeEntry.target, routeEntry.path, path));
            }
        }

        return matchSet;
    }

    /**
     * Returns the handler associated with the provided exception class
     *
     * @param exceptionClass Type of exception
     * @return Associated handler
     */
    @SuppressWarnings ("unchecked")
    @Override
    public Fault<? extends Exception> findHandler(Class<? extends Exception> exceptionClass) {
        // If the exception map does not contain the provided exception class, it might
        // still be that a superclass of the exception class is.
        if (!exceptionMap.containsKey(exceptionClass)) {

            Class<? extends Exception> superclass =
                (Class<? extends Exception>)exceptionClass.getSuperclass();
            do {
                // Is the superclass mapped?
                if (exceptionMap.containsKey(superclass)) {
                    // Use the handler for the mapped superclass, and cache handler
                    // for this exception class
                    Fault handler = exceptionMap.get(superclass);
                    exceptionMap.put(exceptionClass, handler);
                    return handler;
                }

                // Iteratively walk through the exception class's superclasses
                superclass = (Class<? extends Exception>)superclass.getSuperclass();
            } while (superclass != null);

            // No handler found either for the superclasses of the exception class
            // We cache the null value to prevent future
            exceptionMap.put(exceptionClass, null);
            return null;
        }

        // Direct map
        return exceptionMap.get (exceptionClass);
    }

    /**
     * Maps the given handler to the provided exception type. If a handler was already registered to the same type, the
     * handler is overwritten.
     *
     * @param handler        Handler to map to exception
     */
    @Override public <T extends Exception> void processFault (final Fault<T> handler) {
        exceptionMap.put(handler.exception, handler);
    }

    private void addRoute (HttpMethod method, String url, String acceptedType, Object target) {
        RouteEntry entry = new RouteEntry ();
        entry.httpMethod = method;
        entry.path = url;
        entry.target = target;
        entry.acceptedType = acceptedType;
        LOG.fine ("Adds route: " + entry);
        // Adds to end of list
        routes.add (entry);
    }

    //can be cached? I don't think so.
    private Map<String, RouteEntry> getAcceptedMimeTypes (List<RouteEntry> routes) {
        Map<String, RouteEntry> acceptedTypes = new HashMap<> ();

        routes.stream ()
            .filter (routeEntry -> !acceptedTypes.containsKey (routeEntry.acceptedType))
            .forEach (routeEntry -> acceptedTypes.put (routeEntry.acceptedType, routeEntry));

        return acceptedTypes;
    }

    private boolean routeWithGivenAcceptType (String bestMatch) {
        return !MimeParse.NO_MIME_TYPE.equals (bestMatch);
    }

    private List<RouteEntry> findTargetsForRequestedRoute (
        HttpMethod httpMethod, String path) {

        return routes.stream ()
            .filter (entry -> entry.matches (httpMethod, path))
            .collect (toList ());
    }

    // TODO: I believe this feature has impacted performance. Optimization?
    private RouteEntry findTargetWithGivenAcceptType (
        final List<RouteEntry> routeMatches, final String acceptType) {

        if (acceptType != null && routeMatches.size () > 0) {
            final Map<String, RouteEntry> acceptedMimeTypes = getAcceptedMimeTypes (routeMatches);
            final String bestMatch = bestMatch (acceptedMimeTypes.keySet (), acceptType);

            return routeWithGivenAcceptType (bestMatch)? acceptedMimeTypes.get (bestMatch) : null;
        }
        else {
            if (routeMatches.size () > 0)
                return routeMatches.get (0);
        }

        return null;
    }
}