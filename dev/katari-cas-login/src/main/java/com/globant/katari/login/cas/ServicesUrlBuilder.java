/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.cas;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;

/** Builds the urls of the different application services, such as web and CAS
 * related urls.
 *
 * The possible urls are:
 *
 * - The main application url (the 'service', in CAS parlance). This url
 *   belongs to the application that needs an authenticated user.
 *
 * - The login url. This is served by CAS. It is the url where the main service
 *   is redirected if the user is not authenticated.
 *
 * - The cas validator url. This is served by CAS. It is used to verify if a
 *   token was generated by the expected server.
 */
public class ServicesUrlBuilder {

  /** The url of the CAS server.
   *
   * This is usually of the form https://cas-server/login. It is never null.
   */
  private String casUrl;

  /** The service url fragment.
   *
   * The service corresponds to the url that the cas authentication service
   * redirects the client to after a succesful authentication. It is never
   * null.
   */
  private String serviceUrl;

  /** Builds a new service builder.
   *
   * @param theCasUrl The url of the CAS server. This is usually of the form
   * https://cas-server. It cannot be null.
   *
   * @param theServiceUrl The fragment of the url that cas redirects after a
   * succesful login. It cannot be null.
   */
  public ServicesUrlBuilder(final String theCasUrl,
      final String theServiceUrl) {

    Validate.notNull(theCasUrl, "The cas url cannot be null");
    Validate.notNull(theServiceUrl, "The service url cannot be null");

    casUrl = theCasUrl;
    serviceUrl = theServiceUrl;
  }

  /** Builds the application service url.
   *
   * Usually something like
   * <code>http://..../j_acegi_cas_security_check</code>. This is the url that
   * cas redirects to after a successful login.
   *
   * @param request The http request object use to construct the full service
   * url. It cannot be null.
   *
   * @return the service url. It never returns null.
   */
  public String buildServiceUrl(final HttpServletRequest request) {
    Validate.notNull(request, "The request cannot be null");
    return createUrl(request.getScheme() + "://" + request.getServerName()
      + ":" + request.getServerPort(), request.getContextPath(), serviceUrl);
  }

  /** Builds the CAS login full URL.
   *
   * Usually something like <code>https://..../login</code>.
   *
   * @return the login URL. It never returns null.
   */
  public String buildCasLoginUrl() {
    return createUrl(casUrl, "login");
  }

  /** Builds the CAS logout full URL.
   *
   * Usually something like <code>https://..../logout</code>.
   *
   * @return the login URL. It never returns null.
   */
  public String buildCasLogoutUrl() {
    return createUrl(casUrl, "logout");
  }

  /** Builds the CAS ticket validator full URL.
   *
   * Usually something like <code>http://..../cas/proxyValidate</code>. It is
   * used to verify if a ticket was generated by the expected cas server.
   *
   * @return the login URL. It never returns null.
   */
  public String buildCasValidatorUrl() {
    return createUrl(casUrl, "serviceValidate");
  }

  /** Creates a new url based on a base url and a path fragment.
   *
   * @param base The base url. It cannot be null.
   *
   * @param paths The path fragments to add. All must be not null.
   *
   * @return Returns the new url formed by the concatenation of the base url
   * and the path fragments, including the '/' if necessary.
   */
  private String createUrl(final String base, final String ... paths) {
    Validate.notNull(base, "The base url cannot be null");

    StringBuilder result = new StringBuilder(base);
    for (String path : paths) {
      boolean endsWithSlash = (result.charAt(result.length() - 1) == '/');
      if (!endsWithSlash && !path.startsWith("/")) {
        result.append("/");
      }
      result.append(path);
    }
    return result.toString();
  }
}

