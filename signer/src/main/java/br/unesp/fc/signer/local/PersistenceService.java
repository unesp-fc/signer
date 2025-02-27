package br.unesp.fc.signer.local;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import javax.jnlp.JNLPRandomAccessFile;

/**
 *
 * @author demitrius
 */
public class PersistenceService implements javax.jnlp.PersistenceService {

    private final Path path = Paths.get("data");

    public static void main(String args[]) throws MalformedURLException, IOException {
        URL url = new URL("https://sgcd.fc.unesp.br/permissao");
        PersistenceService persistenceService = new PersistenceService();
        try {
            persistenceService.get(url);
        } catch (FileNotFoundException ex) {
        }
        persistenceService.create(url, 8192);
        FileContents fileContents = persistenceService.get(url);
        PrintWriter printWriter = new PrintWriter(fileContents.getOutputStream(true));
        printWriter.println("Teste");
        printWriter.println("Testando");
        printWriter.close();
        printWriter = new PrintWriter(fileContents.getOutputStream(false));
        printWriter.println("Testando");
        printWriter.close();
        printWriter = new PrintWriter(fileContents.getOutputStream(true));
        printWriter.println("real");
        printWriter.close();
        BufferedReader data = new BufferedReader(new InputStreamReader(fileContents.getInputStream()));
        System.out.println(data.readLine());
    }

    private Path encodePath(URL url) {
        return path.resolve(url.getHost()).resolve(url.getPath().substring(1));
    }

    @Override
    public long create(URL url, long l) throws MalformedURLException, IOException {
        Path filepath = encodePath(url);
        Files.createDirectories(filepath.getParent());
        if (!filepath.toFile().createNewFile()) {
            throw new IOException();
        }
        return l;
    }

    @Override
    public FileContents get(URL url) throws MalformedURLException, IOException, FileNotFoundException {
        Path filepath = encodePath(url);
        if (!filepath.toFile().exists()) {
            throw new FileNotFoundException();
        }
        return new FileContents(url, filepath);
    }

    @Override
    public void delete(URL url) throws MalformedURLException, IOException {
        Path filepath = encodePath(url);
        File file = filepath.toFile();
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        file.delete();
    }

    @Override
    public String[] getNames(URL url) throws MalformedURLException, IOException {
        Path dirpath = encodePath(url);
        return Files.list(dirpath).map(p -> p.getFileName().toString())
                .collect(Collectors.toList()).toArray(new String[0]);
    }

    @Override
    public int getTag(URL url) throws MalformedURLException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setTag(URL url, int i) throws MalformedURLException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public class FileContents implements javax.jnlp.FileContents {

        private final URL url;
        private final Path filepath;

        public FileContents(URL url, Path filepath) {
            this.url = url;
            this.filepath = filepath;
        }

        @Override
        public String getName() throws IOException {
            return filepath.getFileName().toString();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new FileInputStream(filepath.toFile());
        }

        @Override
        public OutputStream getOutputStream(boolean bln) throws IOException {
            return new FileOutputStream(filepath.toFile(), !bln);
        }

        @Override
        public long getLength() throws IOException {
            return path.toFile().length();
        }

        @Override
        public boolean canRead() throws IOException {
            return path.toFile().canRead();
        }

        @Override
        public boolean canWrite() throws IOException {
            return path.toFile().canWrite();
        }

        @Override
        public JNLPRandomAccessFile getRandomAccessFile(String string) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public long getMaxLength() throws IOException {
            return 8192;
        }

        @Override
        public long setMaxLength(long l) throws IOException {
            return 8192;
        }

    }

}
