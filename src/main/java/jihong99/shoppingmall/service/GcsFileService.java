package jihong99.shoppingmall.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class GcsFileService {
    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    public GcsFileService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    public String uploadFile(String fileName, InputStream fileStream, String contentType) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(contentType)
                .build();
        storage.create(blobInfo, fileStream);
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }
}
