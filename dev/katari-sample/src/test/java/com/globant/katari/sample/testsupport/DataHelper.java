/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.testsupport;

import java.util.Date;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.globant.katari.hibernate.role.domain.Role;
import com.globant.katari.hibernate.role.domain.RoleRepository;
import com.globant.katari.sample.time.domain.Activity;
import com.globant.katari.sample.time.domain.Project;
import com.globant.katari.sample.time.domain.TimeEntry;
import com.globant.katari.sample.time.domain.TimePeriod;
import com.globant.katari.sample.time.domain.TimeRepository;
import com.globant.katari.sample.user.domain.User;
import com.globant.katari.sample.user.domain.UserRepository;

/** Utility class to help in the setup of test cases that need to interact with
 * the database.
 */
public final class DataHelper {

  /** A logger.
   */
  private static Log log = LogFactory.getLog(DataHelper.class);

  /** A private constructor so no instances are created.
   */
  private DataHelper() {
  }

  /** Removes the extra users created by tests.
   *
   * It removes users with names that do not start with 'base-'. It also keeps
   * the administrator.
   *
   * @param repository The user repository. It cannot be null.
   */
  public static void removeExtraUsers(final UserRepository repository) {
    Validate.notNull(repository, "The user repository cannot be null");
    //  Removes the unneeded users.
    for (User user : repository.getUsers()) {
      log.debug("Found user " + user.getName());
      boolean canDelete = !user.getName().equals("admin");
      canDelete = canDelete & !user.getName().startsWith("base-");
      if (canDelete) {
        repository.remove(user);
      }
    }
  }

  /** Removes the extra roles created by tests.
   *
   * It removes roles with names that do not start with 'base-'. It also keeps
   * the administrator role.
   *
   * @param repository The user repository. It cannot be null.
   */
  public static void removeExtraRoles(final RoleRepository repository) {
    Validate.notNull(repository, "The user repository cannot be null");
    //  Removes the unneeded users.
    for (Role role : repository.getRoles()) {
      log.debug("Found role " + role.getName());
      boolean canDelete = !role.getName().equals("ADMINISTRATOR");
      canDelete = canDelete & !role.getName().startsWith("base-");
      if (canDelete) {
        repository.remove(role);
      }
    }
  }

  /** Removes the extra time entries created by tests.
   *
   * @param repository The time entry repository. It cannot be null.
   */
  public static void removeExtraTimeEntries(final TimeRepository repository) {
    Validate.notNull(repository, "The time entry repository cannot be null");
    // Removes the unneeded time entries.
    for (TimeEntry timeEntry : repository.getTimeEntries()) {
      log.debug("Found time entry " + timeEntry.getId());
      repository.remove(timeEntry);
    }
  }

  /** Creates a time entry for a certain date.
   *
   * @param repository The time entry repository. It cannot be null.
   * @param user The user of the time entry.
   * @param date The date of the time entry.
   */
  public static void createTimeEntry(final TimeRepository repository,
      final User user, final Date date) {
    Validate.notNull(repository, "The time entry repository cannot be null");
    Activity activity = repository.findActivity(1);
    Project project = repository.findProject(1);
    TimePeriod period = new TimePeriod("00:00", 1);
    TimeEntry timeEntry = new TimeEntry(activity, user, project,
        date, period, "Test note");
    repository.save(timeEntry);
  }

  /** Creates a time entry for today.
  *
  * @param repository The time entry repository. It cannot be null.
  *
  * @param userId The user id of the time entry.
  */
 public static void createTimeEntry(final TimeRepository repository,
     final User user) {
   createTimeEntry(repository, user, new Date());
 }
}
