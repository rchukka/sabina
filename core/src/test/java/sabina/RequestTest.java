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

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.testng.annotations.Test;
import sabina.route.RouteMatch;

public class RequestTest {

    private static final String THE_SERVLET_PATH = "/the/servlet/path";
    private static final String THE_CONTEXT_PATH = "/the/context/path";

    RouteMatch match = new RouteMatch (HttpMethod.get, null, "/hi", "/hi", "text/html");

    @Test public void queryParamShouldReturnsParametersFromQueryString () {
        Map<String, String[]> params = new HashMap<> ();
        params.put ("name", new String[] { "Federico" });
        HttpServletRequest servletRequest = new MockedHttpServletRequest (params);
        Request request = new Request (match, servletRequest);
        String name = request.queryParams ("name");
        assertEquals (name, "Federico", "Invalid name in query string");
    }

    @Test (enabled = false) public void queryParamShouldBeParsedAsHashMap () {
        Map<String, String[]> params = new HashMap<> ();
        params.put ("user[name]", new String[] { "Federico" });
        HttpServletRequest servletRequest = new MockedHttpServletRequest (params);
        Request request = new Request (match, servletRequest);
        String name = request.queryMap ("user").value ("name");
        assertEquals (name, "Federico", "Invalid name in query string");
    }

    @Test public void shouldBeAbleToGetTheServletPath () {
        HttpServletRequest servletRequest = new MockedHttpServletRequest (new HashMap<> ()) {
            @Override public String getServletPath () {
                return THE_SERVLET_PATH;
            }
        };
        Request request = new Request (match, servletRequest);
        assertEquals (
            request.servletPath (), THE_SERVLET_PATH,
            "Should have delegated getting the servlet path");
    }

    @Test public void shouldBeAbleToGetTheContextPath () {
        HttpServletRequest servletRequest = new MockedHttpServletRequest (new HashMap<> ()) {
            @Override public String getContextPath () {
                return THE_CONTEXT_PATH;
            }
        };
        Request request = new Request (match, servletRequest);
        assertEquals (
            request.contextPath (), THE_CONTEXT_PATH,
            "Should have delegated getting the context path");
    }

    public static class MockedHttpServletRequest implements HttpServletRequest {
        private Map<String, String[]> params;

        public MockedHttpServletRequest (Map<String, String[]> params) {
            this.params = params;
        }

        @Override public String getAuthType () { return null; }
        @Override public String getContextPath () { return null; }
        @Override public Cookie[] getCookies () { return null; }
        @Override public long getDateHeader (String name) { return 0; }
        @Override public String getHeader (String name) { return null; }
        @Override public Enumeration<String> getHeaderNames () { return null; }
        @Override public Enumeration<String> getHeaders (String name) { return null; }
        @Override public int getIntHeader (String name) { return 0; }
        @Override public String getMethod () { return null; }
        @Override public String getPathInfo () { return null; }
        @Override public String getPathTranslated () { return null; }
        @Override public String getQueryString () { return null; }
        @Override public String getRemoteUser () { return null; }
        @Override public String getRequestURI () { return null; }
        @Override public StringBuffer getRequestURL () { return null; }
        @Override public String getRequestedSessionId () { return null; }
        @Override public String getServletPath () { return null; }
        @Override public HttpSession getSession () { return null; }
        @Override public HttpSession getSession (boolean create) { return null; }
        @Override public Principal getUserPrincipal () { return null; }
        @Override public boolean isRequestedSessionIdFromCookie () { return false; }
        @Override public boolean isRequestedSessionIdFromURL () { return false; }
        @Deprecated @Override public boolean isRequestedSessionIdFromUrl () { return false; }
        @Override public boolean isRequestedSessionIdValid () { return false; }
        @Override public boolean isUserInRole (String role) { return false; }
        @Override public Object getAttribute (String name) { return null; }
        @Override public Enumeration<String> getAttributeNames () { return null; }
        @Override public String getCharacterEncoding () { return null; }
        @Override public int getContentLength () { return 0; }
        @Override public String getContentType () { return null; }
        @Override public ServletInputStream getInputStream ()
            throws IOException { return null; }

        @Override public String getLocalAddr () { return null; }
        @Override public String getLocalName () { return null; }
        @Override public int getLocalPort () { return 0; }
        @Override public Locale getLocale () { return null; }
        @Override public Enumeration<Locale> getLocales () { return null; }
        @Override public String getParameter (String name) {
            return this.params.get (
                name)[0];
        }

        @Override public Map<String, String[]> getParameterMap () { return this.params; }
        @Override public Enumeration<String> getParameterNames () { return null; }
        @Override public String[] getParameterValues (String name) { return null; }
        @Override public String getProtocol () { return null; }
        @Override public BufferedReader getReader () throws IOException { return null; }
        @Deprecated @Override public String getRealPath (String path) { return null; }
        @Override public String getRemoteAddr () { return null; }
        @Override public String getRemoteHost () { return null; }
        @Override public int getRemotePort () { return 0; }
        @Override public RequestDispatcher getRequestDispatcher (String path) { return null; }
        @Override public String getScheme () { return null; }
        @Override public String getServerName () { return null; }
        @Override public int getServerPort () { return 0; }
        @Override public boolean isSecure () { return false; }
        @Override public void removeAttribute (String name) { /* do nothing */ }
        @Override public void setAttribute (String name, Object o) { /* do nothing */ }
        @Override public void setCharacterEncoding (String env)
            throws UnsupportedEncodingException { /* do nothing */ }

        @Override public ServletContext getServletContext () { return null; }
        @Override public AsyncContext startAsync ()
            throws IllegalStateException { return null; }

        @Override public AsyncContext startAsync (
            ServletRequest servletRequest, ServletResponse servletResponse)
            throws IllegalStateException { return null; }

        @Override public boolean isAsyncStarted () { return false; }
        @Override public boolean isAsyncSupported () { return false; }
        @Override public AsyncContext getAsyncContext () { return null; }
        @Override public DispatcherType getDispatcherType () { return null; }
        @Override public boolean authenticate (HttpServletResponse response)
            throws IOException, ServletException { return false; }

        @Override public void login (String username, String password)
            throws ServletException { /* do nothing */ }

        @Override public void logout () throws ServletException { /* do nothing */ }
        @Override public Collection<Part> getParts ()
            throws IOException, ServletException { return null; }

        @Override public Part getPart (String name)
            throws IOException, ServletException { return null; }

        @Override public String changeSessionId () { return null; }
        @Override public <T extends HttpUpgradeHandler> T upgrade (Class<T> handlerClass)
            throws IOException, ServletException { return null; }

        @Override public long getContentLengthLong () { return 0; }
    }
}