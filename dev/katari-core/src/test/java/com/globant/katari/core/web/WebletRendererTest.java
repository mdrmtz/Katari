/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.TreeMap;
import java.io.IOException;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import static org.easymock.EasyMock.*;

/* Tests the weblet renderer.
 */
public class WebletRendererTest {

  @Test
  public void renderWebletResponse() throws Exception {

    RequestDispatcher dispatcher = new RequestDispatcher() {
      public void include(final ServletRequest request, final
          ServletResponse response) throws IOException {
        response.getWriter().print("<html><body>content</body></html>");
      }

      public void forward(final ServletRequest request, final
          ServletResponse response) {
      }
    };

    String path = "/module/user/weblet/search.do";

    ServletContext context = createMock(ServletContext.class);
    expect(context.getRequestDispatcher(path)).andReturn(dispatcher);
    replay(context);

    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    replay(request);
    HttpServletResponse response = createNiceMock(HttpServletResponse.class);
    expect(response.getCharacterEncoding()).andReturn("utf-8");
    replay(response);

    WebletRenderer renderer = new WebletRenderer(context);

    String result = renderer.renderWebletResponse("user", "search", null,
        request, response);
    assertThat(result, is("content"));
  }

  /* The sample servlets set this variable when the dispatcher calls service on
   * them. This is used to check that the correct servlet was invoked.
   */
  private String servletCalled;

  private String pathInfo;

  private TreeMap<String, ServletAndParameters> usersMap;

  private ServletConfig config;

  @SuppressWarnings("serial")
  @Before
  public void setUp() {

    servletCalled = "";
    pathInfo = null;

    // Mocks the servlet context.
    ServletContext context = createMock(ServletContext.class);
    expect(context.getServletContextName()).andReturn("/module");
    expectLastCall().anyTimes();

    // Under some conditions, the init method asks context to log the call.
    context.log(isA(String.class));
    expectLastCall().anyTimes();
    replay(context);

    // Mocks the servlet config.
    config = createMock(ServletConfig.class);
    expect(config.getServletContext()).andReturn(context);
    expectLastCall().anyTimes();
    // Under some conditions, the init method asks the servlet for its name.
    expect(config.getServletName()).andReturn("ContainerServlet");
    expectLastCall().anyTimes();
    replay(config);

    // A sample servlet that will be mapped to *.do.
    ServletAndParameters doServlet = new ServletAndParameters(
        new HttpServlet() {
      public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletCalled = ".do";
        pathInfo = req.getPathInfo();
      }
    });

    // A sample servlet that will be mapped to *.test.
    ServletAndParameters testServlet = new ServletAndParameters(
        new HttpServlet() {
      public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletCalled = "test";
        pathInfo = req.getPathInfo();
      }
    });

    // A sample configuration mapping.
    usersMap = new TreeMap<String, ServletAndParameters>();
    usersMap.put(".*\\.do", doServlet);
    usersMap.put(".*/test", testServlet);
  }

  /* Tests if service correctly dispatches a request to *.do.
  */
  @Test
  public final void service_dotdo() throws Exception {

    // Mocks the servlet request.
    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    expect(request.getRequestURI()).andReturn(
        "/katari-i/module/user/welcome.do");
    expectLastCall().anyTimes();
    expect(request.getRequestURL()).andReturn(new StringBuffer());
    expectLastCall().anyTimes();
    expect(request.getServletPath()).andReturn("/module").anyTimes();
    expect(request.getContextPath()).andReturn("/katari-i").anyTimes();
    expect(request.getPathInfo()).andReturn("/user/welcome.do").anyTimes();
    expect(request.getMethod()).andReturn("GET").anyTimes();
    expect(request.getProtocol()).andReturn("http").anyTimes();
    replay(request);

    ModuleContainerServlet servlet = new ModuleContainerServlet();
    servlet.addModule("user", usersMap);

    servlet.init(config);
    servlet.service(request, null);

    assertThat(servletCalled, is(".do"));
    assertThat(pathInfo, is(nullValue()));
  }

  /* Tests if service correctly dispatches a request to 'test' and the pathInfo
   * is correct.
   */
  public final void testService_testPathInfo() throws Exception {

    // Mocks the servlet request.
    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    expect(request.getRequestURI()).andReturn(
        "/katari-i/module/user/welcome.do");
    expectLastCall().anyTimes();
    expect(request.getRequestURL()).andReturn(new StringBuffer());
    expectLastCall().anyTimes();
    expect(request.getServletPath()).andReturn("/module").anyTimes();
    expect(request.getContextPath()).andReturn("/katari-i").anyTimes();
    expect(request.getPathInfo()).andReturn("/user/test/user/21?action=remove");
    expectLastCall().anyTimes();
    expect(request.getMethod()).andReturn("GET").anyTimes();
    expect(request.getProtocol()).andReturn("http").anyTimes();
    replay(request);

    ModuleContainerServlet servlet = new ModuleContainerServlet();
    servlet.addModule("user", usersMap);

    servlet.init(config);
    servlet.service(request, null);

    assertThat(servletCalled, is("test"));
    assertThat(pathInfo, is("/user/21?action=remove"));
  }
}

