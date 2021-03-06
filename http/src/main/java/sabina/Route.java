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

package sabina;

import static java.lang.String.format;
import static sabina.HttpMethod.AFTER;
import static sabina.HttpMethod.BEFORE;
import static sabina.util.Checks.checkArgument;
import static sabina.util.Strings.isNullOrEmpty;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A Route is built up by a path (for url-matching) and the implementation of the 'handle'
 * method.
 * When a request is made, if present, the matching routes 'handle' method is invoked. The
 * object that is returned from 'handle' will be set to the response body (toString()).
 *
 * @author Per Wendel
 */
public final class Route {
    /** This is just a "type alias". */
    public interface Handler extends Function<Request, Object> {}
    /** This is just a "type alias". */
    public interface VoidHandler extends Consumer<Request> {}

    public static final String ALL_PATHS = "+/*paths";
    private static final String DEFAULT_ACCEPT_TYPE = "*/*";

    public final String path;
    public final String acceptType;
    public final HttpMethod method;
    private final Handler handler;

    /**
     * Constructor.
     *
     * TODO Maybe this is only intended for filters!!!
     *
     * @param method .
     * @param handler .
     */
    Route (final HttpMethod method, final Handler handler) {
        this (method, ALL_PATHS, handler);
    }

    /**
     * Constructor.
     *
     * @param method .
     * @param path The route path which is used for matching. (e.g. /hello, users/:name).
     * @param handler .
     */
    Route (final HttpMethod method, final String path, final Handler handler) {
        this (
            method,
            path,
            method == AFTER || method == BEFORE? "text/html" : DEFAULT_ACCEPT_TYPE,
            handler);
    }

    /**
     * Constructor.
     *
     * @param method .
     * @param path The route path which is used for matching. (e.g. /hello, users/:name).
     * @param acceptType The accept type which is used for matching.
     * @param handler .
     */
    public Route (
        final HttpMethod method,
        final String path,
        final String acceptType,
        final Handler handler) {

        checkArgument (!isNullOrEmpty (path) && !isNullOrEmpty (acceptType));
        checkArgument (handler != null && method != null);

        this.path = path;
        this.acceptType = acceptType;
        this.method = method;
        this.handler = handler;
    }

    public boolean isFilter () {
        return method == AFTER || method == BEFORE;
    }

    /**
     * Invoked when a req is made on this route's corresponding path e.g. '/hello'.
     *
     * @param req The request object providing information about the HTTP request.
     *
     * @return The content to be set in the response.
     */
    public Object handle (final Request req) {
        return handler.apply (req);
    }

    @Override public String toString () {
        return format ("%s %s [%s]", method, path, acceptType);
    }
}
