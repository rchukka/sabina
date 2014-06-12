/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spark.webserver;

/**
 * @author Per Wendel
 */
public final class SparkServerFactory {
    private static final int IMPL = 0;

    private SparkServerFactory () {
        throw new IllegalStateException ();
    }

    private static SparkServer createJetty (boolean hasMultipleHandler) {
        MatcherFilter matcherFilter = new MatcherFilter (false, hasMultipleHandler);
        matcherFilter.init (null); // init is empty (left here in case is implemented)
        return new JettyServer (new JettyHandler (matcherFilter));
    }

    private static SparkServer createUndertow (boolean hasMultipleHandler) {
        return new UndertowServer ();
    }

    public static SparkServer create (boolean hasMultipleHandler) {
        switch (IMPL) {
            case 0:
                return createJetty (hasMultipleHandler);
            case 1:
                return createUndertow (hasMultipleHandler);
            default:
                throw new IllegalStateException ();
        }
    }
}
