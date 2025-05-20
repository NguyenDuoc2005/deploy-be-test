package udpm.hn.server.infrastructure.job.todolist.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import udpm.hn.server.infrastructure.job.todolist.service.JobTodoListService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;


@Service
@Slf4j
public class ImportTodoList implements JobTodoListService {

    @Value("${file.upload.todolist.path}")
    private String FILE_UPLOAD_TODO_LIST_PATH;

    private Path root;

    @PostConstruct
    public void init() {
        root = Paths.get(FILE_UPLOAD_TODO_LIST_PATH);
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!", e);
        }
    }

    @Override
    public String save(MultipartFile file) {
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            log.info("Đã chạy vào service");
            String fileExtension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
            String newFileName = generateUUIDFromTimestamp(timestamp) + fileExtension;
            Files.copy(file.getInputStream(), this.root.resolve(newFileName));
            return newFileName;
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.'));
    }

    private String generateUUIDFromTimestamp(Timestamp timestamp) {
        UUID uuid = UUID.nameUUIDFromBytes(ByteBuffer.allocate(16).putLong(timestamp.getTime()).array());
        return uuid.toString();
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try (Stream<Path> paths = Files.walk(this.root, 1)) {
            return paths.filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load the files!");
        }
    }
}
