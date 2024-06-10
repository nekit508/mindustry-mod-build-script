package nekit508.updater;

import arc.util.Log;
import org.kohsuke.github.GHRepository;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.Objects;

public class NetJavaFileObject implements JavaFileObject {
    public GHRepository provider;
    public String path;
    public String file;

    @Override
    public Kind getKind() {
        return Kind.SOURCE;
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {
        Log.info(simpleName, this.getName());

        Objects.requireNonNull(simpleName);
        Objects.requireNonNull(kind);

        if (kind != this.getKind())
            return false;

        String sn = simpleName + kind.extension;
        if ((path + file).replaceAll("[\\\\/]", ".").equals(sn)) {
            return true;
        }

        return false;
    }

    @Override
    public NestingKind getNestingKind() {
        return null;
    }

    @Override
    public Modifier getAccessLevel() {
        return null;
    }

    @Override
    public URI toUri() {
        try {
            Log.info(provider.getFileContent(path + file).getUrl());
            return URI.create(provider.getFileContent(path + file).getUrl());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return toUri().getPath();
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return provider.getFileContent(path + file).read();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        throw new RuntimeException("Net objects cannot be written.");
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        CharSequence charContent = getCharContent(ignoreEncodingErrors);
        if (charContent == null)
            throw new UnsupportedOperationException();
        if (charContent instanceof CharBuffer buffer) {
            if (buffer.hasArray())
                return new CharArrayReader(buffer.array());
        }
        return new StringReader(charContent.toString());
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        throw  new RuntimeException("Net object don't privide chars content.");
    }

    @Override
    public Writer openWriter() throws IOException {
        return new OutputStreamWriter(openOutputStream());
    }

    @Override
    public long getLastModified() {
        return 0L;
    }

    @Override
    public boolean delete() {
        return false;
    }
}
