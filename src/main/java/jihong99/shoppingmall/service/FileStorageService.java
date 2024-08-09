package jihong99.shoppingmall.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

import static jihong99.shoppingmall.constants.Constants.MESSAGE_400_InvalidStorageType;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final GcsFileService gcsFileService;
    private final LocalFileService localFileService;

    @Value("${file.storage.type}")
    private String storageType;

    /**
     * Uploads a file. Depending on the configured storage type, the file will be stored in GCS or on the local file system.
     *
     * @param fileName The name of the file
     * @param fileStream An InputStream containing the file's content
     * @param contentType The MIME type of the file (e.g., "image/jpeg")
     * @return The URL or path where the file was stored
     * @throws IOException If an I/O error occurs during file upload
     * @throws IllegalArgumentException If an unknown storage type is specified
     */
    public String uploadFile(String fileName, InputStream fileStream, String contentType) throws IOException {
        if ("gcs".equalsIgnoreCase(storageType)) {
            return gcsFileService.uploadFile(fileName, fileStream, contentType);
        } else if ("local".equalsIgnoreCase(storageType)) {
            return localFileService.uploadFile(fileName, fileStream);
        } else {
            throw new IllegalArgumentException(MESSAGE_400_InvalidStorageType + storageType);
        }
    }
}
