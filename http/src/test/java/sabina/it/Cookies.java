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

package sabina.it;

import static sabina.Sabina.*;

import sabina.util.TestUtil;

/**
 * System tests for the Cookies support.
 *
 * @author dreambrother
 */
public class Cookies {
    public static TestUtil testUtil = new TestUtil ();

    public static void setup () {
        post ("assertNoCookies", it -> {
            if (!it.cookies ().isEmpty ()) {
                it.halt (500);
            }
            return "";
        });

        post ("setCookie", it -> {
            it.cookie (it.queryParams ("cookieName"), it.queryParams ("cookieValue"));
            return "";
        });

        post ("assertHasCookie", it -> {
            String cookieValue = it.cookie (it.queryParams ("cookieName"));
            if (!it.queryParams ("cookieValue").equals (cookieValue))
                it.halt (500);
            return "";
        });

        post ("removeCookie", it -> {
            String cookieName = it.queryParams ("cookieName");
            String cookieValue = it.cookie (cookieName);
            if (!it.queryParams ("cookieValue").equals (cookieValue))
                it.halt (500);
            it.removeCookie (cookieName);
            return "";
        });
    }

    public static void emptyCookies () {
        testUtil.doPost ("/assertNoCookies");
    }

    public static void createCookie () {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        String cookie = cookieName + "&cookieValue=" + cookieValue;
        testUtil.doPost ("/setCookie?cookieName=" + cookie);
        testUtil.doPost ("/assertHasCookie?cookieName=" + cookie);
    }

    public static void removeCookie () {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        String cookie = cookieName + "&cookieValue=" + cookieValue;
        testUtil.doPost ("/setCookie?cookieName=" + cookie);
        testUtil.doPost ("/removeCookie?cookieName=" + cookie);
        testUtil.doPost ("/assertNoCookies");
    }
}