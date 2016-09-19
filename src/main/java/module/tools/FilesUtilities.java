package module.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
/**
 * FileUtilities group several static method in order to manipulate the file system during the different process of
 * TermithText
 * @author Simon Meoni
 * Created on 16/08/16.
 */
public class FilesUtilities {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilesUtilities.class.getName());

    /**
     * Create temporary folder with a name
     * @param path the name of the temporary folder
     * @return return the path created
     * @throws IOException throw an exception if the name of the path is incorrect
     */
    public static String createTemporaryFolder(String path) throws IOException {
        Path tempDir = Files.createTempDirectory(path);
        return tempDir.toString();
    }

    /**
     * create files with the content of a stringBuffer
     * @param path the path of the folder
     * @param corpus the corpus with the name of the file and his content
     * @param extension the extension expected of the created file
     */
    public static void createFiles(String path, Map<String, StringBuffer> corpus, String extension) {
        corpus.forEach((filename, content) -> {
            try (BufferedWriter writer =
                         Files.newBufferedWriter(Paths.get(path + "/" + filename + "." + extension))){
                LOGGER.debug("write file: " + path + "/" + filename + "." + extension);
                writer.write(String.valueOf(content));

            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    public static String nameNormalizer(String path){
        String name = Paths.get(path).getFileName().toString();
        return name.split("\\.")[0];
    }
}
