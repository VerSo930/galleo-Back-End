package com.vuta.helpers;

import com.google.common.base.Strings;

import com.vuta.model.PhotoModel;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by vuta on 25/09/2017.
 */
public class PhotoTools {

    public static boolean checkInsert(PhotoModel photo) {

        if (photo == null) {
            return false;
        }

        if (Strings.isNullOrEmpty(photo.getName()) ||
                Strings.isNullOrEmpty(photo.getDescription()) ||
                Strings.isNullOrEmpty(photo.getUrl())) {
            return false;
        }
        return true;
    }

    /**
     * Get file extension from file name
     */
    public static String getFileExtension(MultivaluedMap<String, String> header) {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");
                String[] extension = name[1].trim().replaceAll("\"", "").split("\\.");
               // System.out.println(extension[1]);
                return extension[1].trim();
            }
        }
        return "unknown";
    }

    /**
     * Generate unique id
     */
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Write file to upload directory
     */
    public static void writeFile(byte[] content, String filename) throws IOException {

        File file = new File(filename);

        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fop = new FileOutputStream(file);

        fop.write(content);
        fop.flush();
        fop.close();

    }

    /**
     * Upload files
     */
    public static List<String> uploadPhoto(MultipartFormDataInput input, String servletPath) throws Exception {


        List<String> files = new ArrayList<>();
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("image");

        // loop trough forms parts
        for (InputPart inputPart : inputParts) {

                // generate unique id
                String id = generateUniqueId();

                // get headers map
                MultivaluedMap<String, String> header = inputPart.getHeaders();

                // convert the uploaded file to input stream
                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                // write file to server
                writeFile(IOUtils.toByteArray(inputStream),  servletPath  + id + "." + getFileExtension(header));
            files.add(id + "." + getFileExtension(header));
        }

        return files;
    }

}
