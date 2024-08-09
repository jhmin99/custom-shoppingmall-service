package jihong99.shoppingmall.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class LocalFileService {
    @Value("${file.local.upload-dir}")
    private String localUploadDir;

    /**
     * Uploads a file to the local file system.
     *
     * <p>This method saves the file to the local directory specified by the `localUploadDir` configuration.
     * The file's content is read from the provided InputStream and written to the target location.</p>
     *
     * @param fileName The name of the file to be saved
     * @param fileStream The InputStream containing the file's data
     * @return The absolute path of the saved file on the local file system
     * @throws IOException If an I/O error occurs during file upload
     */
    public String uploadFile(String fileName, InputStream fileStream) throws IOException {
        File targetFile = new File(localUploadDir + File.separator + fileName);
        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = fileStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
        return targetFile.getAbsolutePath();
    }
}
