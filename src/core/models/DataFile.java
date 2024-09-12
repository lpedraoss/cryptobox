package core.models;

import java.io.File;

public class DataFile {
    private String extension;
    private File file;

    public DataFile(String extension, File file) {
        this.extension = extension;
        this.file = file;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


}