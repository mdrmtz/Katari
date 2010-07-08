/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig;

import org.apache.commons.lang.Validate;
import org.apache.shindig.auth.SecurityTokenDecoder;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.MessageService;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.sample.oauth.SampleOAuthDataStore;
import org.apache.shindig.social.sample.spi.JsonDbOpensocialService;

import com.google.inject.name.Names;

/** Bindings for katari implementation of shindig services.
 *
 * This Guice module implements ApplicationContextAware to be notified by the
 * GuiceInitializerListener of the spring application context.
 */
public class ShindigServicesModule extends ShindigSocialApiGuiceModule {

  /** The implementation of the activity service, never null.
   */
  private final ActivityService activityService;

  /** Security token decoder. 
   */
  private final SecurityTokenDecoder tokenDecoder;

  /** Crypter implementation to crypt the tokens.
   */
  private final BlobCrypter crypter;

  /** Constructor.
   *
   * @param activityService The implementation of the activity service.
   * It cannot be null.
   *
   * @param decoder The implementation of the SecurityTokenDecoder.
   * It cannot be null.
   *
   * @param decoder The implementation of the BlobCrypter.
   * It cannot be null.
   */
  public ShindigServicesModule(final ActivityService activityServiceImpl,
      final SecurityTokenDecoder decoder, final BlobCrypter blobCrypter) {
    Validate.notNull(activityServiceImpl,
        "The activity service implementation cannot be null.");
    Validate.notNull(decoder,
        "The token decoder implementation cannot be null.");
    Validate.notNull(blobCrypter,
    "The blob crypter implementation cannot be null.");
    activityService = activityServiceImpl;
    tokenDecoder = decoder;
    crypter = blobCrypter;
  }

  /** Wires the shindig services to the corresponding Katari implementations.
   * 
   * Katari services are obtained from the application context.
   * 
   * This implementation only wires the ActivityService to Katari, the other
   * services get wired to the shindig provided mock.
   */
  @Override
  protected void configure() {
    super.configure();

    bind(ActivityService.class).toInstance(activityService);
    bind(SecurityTokenDecoder.class).toInstance(tokenDecoder);
    bind(BlobCrypter.class).toInstance(crypter);

    bind(String.class).annotatedWith(Names.named("shindig.canonical.json.db"))
        .toInstance("sampledata/canonicaldb.json");
    bind(AppDataService.class).to(JsonDbOpensocialService.class);
    bind(PersonService.class).to(JsonDbOpensocialService.class);
    bind(MessageService.class).to(JsonDbOpensocialService.class);

    bind(OAuthDataStore.class).to(SampleOAuthDataStore.class);

  }
}

