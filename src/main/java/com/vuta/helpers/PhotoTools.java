package com.vuta.helpers;

import com.google.common.base.Strings;

import com.vuta.model.PhotoModel;
import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MultivaluedMap;
import java.awt.image.BufferedImage;
import java.io.*;
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
                Strings.isNullOrEmpty(photo.getUrl()) ||
                photo.getUserId() == 0 || photo.getGalleryId() == 0) {
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
    private static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Write file to upload directory
     */
    private static void writeFile(byte[] content, String filename) throws IOException {

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
     * Map input fields to PhotoModel Object
     */
    public static PhotoModel mapFormToPhoto(MultipartFormDataInput input) throws IOException {
        PhotoModel photo = new PhotoModel();
        photo.setName(input.getFormDataPart("name", String.class, null));
        photo.setDescription(input.getFormDataPart("description", String.class, null));
        photo.setGalleryId(input.getFormDataPart("galleryId", Integer.class, null));
        photo.setUserId(input.getFormDataPart("userId", Integer.class, null));
        photo.setPrivate(input.getFormDataPart("isPrivate", Boolean.class, null));

        return photo;
    }

    /**
     * Manipulate image file
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
            final InputStream inputStream = inputPart.getBody(InputStream.class, null);

            // write file to server: original format
            writeFile(IOUtils.toByteArray(inputStream), servletPath + id + "." + getFileExtension(header));

            // prepare BAOS
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            // get image buffer from form input part
            BufferedImage srcImage = ImageIO.read(inputPart.getBody(InputStream.class, null));

            // resize and write image to server: 600 px
            ImageIO.write(Scalr.resize(srcImage, 600), getFileExtension(header), os);
            writeFile(IOUtils.toByteArray(new ByteArrayInputStream(os.toByteArray())), servletPath + "600-" + id + "." + getFileExtension(header));
            os.flush();
            os.close();

            // resize and write image to server: 1200 px
            os = new ByteArrayOutputStream();
            ImageIO.write(Scalr.resize(srcImage, 1200), getFileExtension(header), os);
            writeFile(IOUtils.toByteArray(new ByteArrayInputStream(os.toByteArray())), servletPath + "1200-" +  id + "." + getFileExtension(header));
            os.flush();
            os.close();

            files.add(id + "." + getFileExtension(header));
        }

        return files;
    }
}
