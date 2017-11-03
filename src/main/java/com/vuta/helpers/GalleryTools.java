package com.vuta.helpers;

import com.google.common.base.Strings;
import com.vuta.model.GalleryModel;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */
public class GalleryTools {

    public static boolean checkInsert(GalleryModel gallery) {
        if(gallery == null)
            return false;

        if(Strings.isNullOrEmpty(gallery.getName()) || Strings.isNullOrEmpty(gallery.getDescription())
               )
            return false;

        return true;
    }

    public static boolean checkUpdate(GalleryModel gallery, int oldGalleryId) {
        if(gallery == null || oldGalleryId == 0)
            return false;

        if(Strings.isNullOrEmpty(gallery.getName()) || Strings.isNullOrEmpty(gallery.getDescription()))
            return false;

        return true;
    }
}
