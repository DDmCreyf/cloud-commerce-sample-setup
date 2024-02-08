package com.mirakl.hybris.core.util;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.EXTENSIONNAME;
import static de.hybris.platform.persistence.VersionProvider.getBuildVersion;
import static java.lang.Double.parseDouble;

import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.hybris.platform.core.MasterTenant;
import de.hybris.platform.core.Registry;
import de.hybris.platform.persistence.VersionProvider;

public class HybrisVersionUtils {

  private static final Logger LOG = Logger.getLogger(HybrisVersionUtils.class);
  private static String miraklConnectorVersion;
  private static String hybrisVersion;
  private static final Pattern VERSION_PATTERN = Pattern.compile("^[0-9]*\\.[0-9]*");

  private HybrisVersionUtils() {}

  public static class VersionChecker {

    private double buildVersion;
    private Double minimumVersion;
    private Double maximumVersion;

    private VersionChecker(String buildVersion) {
      Matcher matcher = VERSION_PATTERN.matcher(buildVersion);
      if(matcher.find()) {
        this.buildVersion = Double.parseDouble(matcher.group(0));
      } else {
        throw new IllegalStateException(String.format("Impossible to parse version number [build=%s]", buildVersion));
      }
    }

    public VersionChecker maximumVersion(double maximumVersion) {
      this.maximumVersion = maximumVersion;
      return this;
    }

    public VersionChecker minimumVersion(double minimumVersion) {
      this.minimumVersion = minimumVersion;
      return this;
    }

    public boolean isValid() {
      return (minimumVersion == null || buildVersion >= minimumVersion)
              && (maximumVersion == null || buildVersion <= maximumVersion);
    }

    public Double getVersion() {
      return buildVersion;
    }
  }

  public static VersionChecker versionChecker() {
    return new VersionChecker(VersionProvider.getBuildVersion());
  }

  public static VersionChecker versionChecker(String buildVersion) {
    return new VersionChecker(buildVersion);
  }

  public static String getMiraklConnectorVersion() {
    if (miraklConnectorVersion == null) {
      miraklConnectorVersion= readMiraklConnectorVersion();
    }
    return miraklConnectorVersion;
  }

  public static String getHybrisVersion() {
    if (hybrisVersion == null && Registry.hasCurrentTenant()) {
      hybrisVersion = getBuildVersion();
    }
    return hybrisVersion;
  }

  private static String readMiraklConnectorVersion() {
    try (InputStream input = MasterTenant.class.getResourceAsStream("/" + EXTENSIONNAME + ".build.number")) {
      Properties props = new Properties();
      if (input != null) {
        props.load(input);
      }
      return props.getProperty("version");
    } catch (Exception e) {
      LOG.error("Unable to determine the Mirakl Connector Version.", e);
      return null;
    }
  }

}
