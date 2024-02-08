package com.mirakl.hybris.core.util;

import static java.lang.String.format;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.log4j.Logger;

public class CronJobUtils {

  private static final Logger LOG = Logger.getLogger(CronJobUtils.class);

  private CronJobUtils() {}

  public static File[] fetchInputFiles(final File inputDirectory) {
    return fetchInputFiles(".+", inputDirectory);
  }

  public static File[] fetchInputFiles(String inputFilenamePattern, final File inputDirectory) {
    final FileFilter fileFilter = new RegexFileFilter(inputFilenamePattern);
    final File[] inputFiles = inputDirectory.listFiles(fileFilter);
    if (inputFiles != null) {
      Arrays.sort(inputFiles);
    }
    return inputFiles;
  }

  public static void archiveFile(String archiveDirectoryPath, String baseDirectoryPath, final File inputFile) {
    final File archiveDirectory = getOrCreateDirectory(archiveDirectoryPath, baseDirectoryPath);
    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug(format("Moving the input file [%s] to archive directory [%s].", inputFile, archiveDirectory));
      }
      moveFileToDirectory(inputFile, archiveDirectory);
    } catch (final IOException e) {
      LOG.warn(format("Unable to move input file [%s] to archive directory [%s].", inputFile, archiveDirectory), e);
    }
  }

  public static File getOrCreateDirectory(String pathname, String baseDirectoryPath) {
    Path path = Paths.get(pathname);
    Path absolutePath = path.isAbsolute() ? path : Paths.get(baseDirectoryPath, pathname);
    File directory = absolutePath.toFile();

    if (!directory.exists()) {
      boolean isCreated = directory.mkdirs();
      if (!isCreated) {
        LOG.error(format("Unable to create directory [%s].", absolutePath));
      }
    }
    return directory;
  }

  public static void moveFileToDirectory(File sourceFile, File targetDirectory) throws IOException {
    Path targetDirectoryPath = targetDirectory.toPath();
    if (!Files.exists(targetDirectoryPath)) {
      Files.createDirectories(targetDirectoryPath);
    }
    Files.move(Paths.get(sourceFile.getAbsolutePath()), Paths.get(targetDirectory.getAbsolutePath(), sourceFile.getName()),
        REPLACE_EXISTING);
  }

}
