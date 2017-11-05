package com.vuta.helpers;

import com.google.common.base.Strings;

import com.vuta.Constants;
import com.vuta.model.PhotoModel;
import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MultivaluedMap;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private static void writeFile(byte[] content, String servletPath, String userId, String galleryId, String filename) throws Exception {

        // check if directory for this user id exists. If not create it
        File file = new File(servletPath + userId);
        if (!file.exists()) {
            file.mkdir();
        }

        // check if directory for this gallery id exists. If not create it
        file = new File(servletPath + userId + "/" + galleryId);
        if (!file.exists()) {
            file.mkdir();
        }

        // check if file name already exists
        file = new File(servletPath + userId + "/" + galleryId + "/" + filename);
        if (!file.exists()) {
            file.createNewFile();
        }

        // continue and write bytes to file
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
    public static void uploadPhoto(MultipartFormDataInput input, PhotoModel photo) throws Exception {


        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("image");

        // loop trough forms parts
        for (InputPart inputPart : inputParts) {

            String userId = null;
            final InputStream inputStream;
            String uniqueId = null;
            String galleryId = null;
            MultivaluedMap<String, String> header;

            try {
                userId = input.getFormDataPart("userId", String.class, null);

                galleryId = input.getFormDataPart("galleryId", String.class, null);

                // get headers map
                header = inputPart.getHeaders();
                uniqueId = generateUniqueId();
                // convert the uploaded file to input stream
                inputStream = inputPart.getBody(InputStream.class, null);
            } catch (IOException e) {
                throw new Exception(e.getMessage());
            }

            try {
                // write file to server: original format
                writeFile(IOUtils.toByteArray(inputStream), Constants.PHOTO_UPLOAD_PATH, userId, galleryId,
                        uniqueId + "." + getFileExtension(header));

                // prepare BAOS
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                // get image buffer from form input part
                BufferedImage srcImage = ImageIO.read(inputPart.getBody(InputStream.class, null));

                // resize and write image to server: 450 px
                ImageIO.write(generateThumbnail(srcImage, 640, 480), getFileExtension(header), os);
                writeFile(IOUtils.toByteArray(new ByteArrayInputStream(os.toByteArray())), Constants.PHOTO_UPLOAD_PATH, userId,
                        galleryId,
                        "450-" + uniqueId + "." + getFileExtension(header));
                os.flush();
                os.close();

                // resize and write image to server: 1200 px
                os = new ByteArrayOutputStream();
                ImageIO.write(Scalr.resize(srcImage, 1200), getFileExtension(header), os);
                writeFile(IOUtils.toByteArray(new ByteArrayInputStream(os.toByteArray())), Constants.PHOTO_UPLOAD_PATH, userId,
                        galleryId,
                        "1200-" + uniqueId + "." + getFileExtension(header));
                os.flush();
                os.close();
            } catch (Exception e) {
                throw  new Exception(e.getMessage());
            }

            photo.setUrl(uniqueId + "." + getFileExtension(header));
        }
    }

    public static File getImage(String quality, int userId, int galleryId, String fileName) {

        String imgFormat = "";

        switch (quality) {
            case "small":
                imgFormat = "450-";
                break;
            case "medium":
                imgFormat = "1200-";
                break;
            case "hd":
                imgFormat = "";
        }

        File file = new File(Constants.PHOTO_UPLOAD_PATH + "/" + userId + "/" + galleryId + "/" + imgFormat + fileName);

        if (file.exists() && !file.isDirectory()) {
            return file;
        } else {
            return new File(Constants.PHOTO_UPLOAD_PATH + "file-not-found.jpg");
        }
    }

    public static void movePhotoToGallery(String photoName, int oldGalleryId, int newGalleryId, int userId) throws Exception {

        moveFile(Constants.PHOTO_UPLOAD_PATH + "/" + userId + "/" + oldGalleryId,
                Constants.PHOTO_UPLOAD_PATH + "/" + userId + "/" + newGalleryId, "450-" + photoName);
        moveFile(Constants.PHOTO_UPLOAD_PATH + "/" + userId + "/" + oldGalleryId,
                Constants.PHOTO_UPLOAD_PATH + "/" + userId + "/" + newGalleryId, "1200-" + photoName);
        moveFile(Constants.PHOTO_UPLOAD_PATH + "/" + userId + "/" + oldGalleryId,
                Constants.PHOTO_UPLOAD_PATH + "/" + userId + "/" + newGalleryId, photoName);

    }

    /**
     * Move a file to a new path, and delete the old file
     *
     * @param oldPath  the current directory
     * @param newPath  the new directory
     * @param fileName file name
     * @return {@code boolean} true if success, false if not
     */
    public static void moveFile(String oldPath, String newPath, String fileName) throws IOException {

        // initialize streams
        InputStream inStream = null;
        OutputStream outStream = null;

        // create new Path directory if not exists
        Files.createDirectories(Paths.get(newPath));

        // create Files
        File oldFile = new File(oldPath + "/" + fileName);
        File newFile = new File(newPath + "/" + fileName);

        // create streams
        inStream = new FileInputStream(oldFile);
        outStream = new FileOutputStream(newFile);

        byte[] buffer = new byte[1024];

        int length;
        //copy the file content in bytes
        while ((length = inStream.read(buffer)) > 0) {
            outStream.write(buffer, 0, length);
        }

        // close streams
        inStream.close();
        outStream.close();

        //delete the original file
        oldFile.delete();

        System.out.println("File is copied successful!");

    }

    private static BufferedImage generateThumbnail(BufferedImage inputImage, int resultWidth, int resultHeight) {
        // first get the width and the height of the image
        int originWidth = inputImage.getWidth();
        int originHeight = inputImage.getHeight();

        // let us check if we have to scale the image
        if (originWidth <= resultWidth && originHeight <= resultHeight) {
            // we don't have to scale the image, just return the origin
            return inputImage;
        }

        // Scale in respect to width or height?
        Scalr.Mode scaleMode = Scalr.Mode.AUTOMATIC;

        // find out which side is the shortest
        int maxSize = 0;
        if (originHeight > originWidth) {
            // scale to width
            scaleMode = Scalr.Mode.FIT_TO_WIDTH;
            maxSize = resultWidth;
        } else if (originWidth >= originHeight) {
            scaleMode = Scalr.Mode.FIT_TO_HEIGHT;
            maxSize = resultHeight;
        }

        // Scale the image to given size
        BufferedImage outputImage = Scalr.resize(inputImage, Scalr.Method.QUALITY, scaleMode, maxSize);

        // okay, now let us check that both sides are fitting to our result scaling
        if (scaleMode.equals(Scalr.Mode.FIT_TO_WIDTH) && outputImage.getHeight() > resultHeight) {
            // the height is too large, resize again
            outputImage = Scalr.resize(outputImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_HEIGHT, resultHeight);
        } else if (scaleMode.equals(Scalr.Mode.FIT_TO_HEIGHT) && outputImage.getWidth() > resultWidth) {
            // the width is too large, resize again
            outputImage = Scalr.resize(outputImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, resultWidth);
        }

        // now we have an image that is definitely equal or smaller to the given size
        // Now let us check, which side needs black lines
        int paddingSize = 0;
        if (outputImage.getWidth() != resultWidth) {
            // we need padding on the width axis
            paddingSize = (resultWidth - outputImage.getWidth()) / 2;
        } else if (outputImage.getHeight() != resultHeight) {
            // we need padding on the height axis
            paddingSize = (resultHeight - outputImage.getHeight()) / 2;
        }

        // we need padding?
        if (paddingSize > 0) {
            // add the padding to the image
            outputImage = Scalr.pad(outputImage, paddingSize);

            // now we have to crop the image because the padding was added to all sides
            int x = 0, y = 0, width = 0, height = 0;
            if (outputImage.getWidth() > resultWidth) {
                // set the correct range
                x = paddingSize;
                y = 0;
                width = outputImage.getWidth() - (2 * paddingSize);
                height = outputImage.getHeight();
            } else if (outputImage.getHeight() > resultHeight) {
                // set the correct range
                x = 0;
                y = paddingSize;
                width = outputImage.getWidth();
                height = outputImage.getHeight() - (2 * paddingSize);
            }

            // Crop the image
            if (width > 0 && height > 0) {
                outputImage = Scalr.crop(outputImage, x, y, width, height);
            }
        }

        // flush both images
        inputImage.flush();
        outputImage.flush();

        // return the final image
        return outputImage;
    }
}
