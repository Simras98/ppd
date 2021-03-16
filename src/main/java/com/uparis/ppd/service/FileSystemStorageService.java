package com.uparis.ppd.service;

import com.uparis.ppd.exception.StorageException;
import com.uparis.ppd.exception.StorageFileNotFoundException;
import com.uparis.ppd.properties.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Objects;

@Service
public class FileSystemStorageService {

  private final Path rootLocation;

  @Autowired
  public FileSystemStorageService(StorageProperties properties) {
    this.rootLocation = Paths.get(properties.getLocation());
  }

  public String store(MultipartFile file) {
    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
    fileName =
        fileName.substring(0, fileName.lastIndexOf('.'))
            + "_"
            + (new Date()).getTime()
            + fileName.substring(fileName.lastIndexOf('.'));
    try {
      if (file.isEmpty()) {
        throw new StorageException("Failed to store empty file " + fileName);
      }
      if (fileName.contains("..")) {
        throw new StorageException(
            "Cannot store file with relative path outside current directory " + fileName);
      }
      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(
            inputStream, this.rootLocation.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
      }
    } catch (IOException e) {
      throw new StorageException("Failed to store file " + fileName, e);
    }
  }

  public Path load(String filename) {
    return rootLocation.resolve(filename);
  }

  public Resource loadAsResource(String filename) {
    try {
      Path file = load(filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new StorageFileNotFoundException("Could not read file: " + filename);
      }
    } catch (MalformedURLException e) {
      throw new StorageFileNotFoundException("Could not read file: " + filename, e);
    }
  }
}
