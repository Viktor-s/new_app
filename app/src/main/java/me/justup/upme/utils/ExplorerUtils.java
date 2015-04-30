package me.justup.upme.utils;

import java.util.Comparator;

import me.justup.upme.entity.FileEntity;


public class ExplorerUtils {
    public static final int LOCAL_FILE = 1;
    public static final int CLOUD_FILE = 2;
    public static final int SHARE_FILE = 3;
    public static final int LOCAL_AND_CLOUD_FILE = 4;

    public static final int IMAGE = 1;
    public static final int PDF = 2;
    public static final int VIDEO = 3;
    public static final int FILE = 4;

    public static final String JPG = ".jpg";
    public static final String JPEG = ".jpeg";
    public static final String PNG = ".png";
    public static final String DOT_PDF = ".pdf";
    public static final String MP4 = ".mp4";
    public static final String AVI = ".avi";
    public static final String GP = ".3gp";


    public static Comparator<FileEntity> COMPARE_BY_FAVOR_ASC = new Comparator<FileEntity>() {
        @Override
        public int compare(FileEntity lhs, FileEntity rhs) {
            return (lhs.isFavorite() == rhs.isFavorite() ? 0 : (lhs.isFavorite() ? 1 : -1));
        }
    };
    public static Comparator<FileEntity> COMPARE_BY_FAVOR_DESC = new Comparator<FileEntity>() {
        @Override
        public int compare(FileEntity lhs, FileEntity rhs) {
            return (rhs.isFavorite() == lhs.isFavorite() ? 0 : (rhs.isFavorite() ? 1 : -1));
        }
    };

    public static Comparator<FileEntity> COMPARE_BY_TYPE_ASC = new Comparator<FileEntity>() {
        @Override
        public int compare(FileEntity lhs, FileEntity rhs) {
            return Integer.valueOf(lhs.getFileType()).compareTo(rhs.getFileType());
        }
    };
    public static Comparator<FileEntity> COMPARE_BY_TYPE_DESC = new Comparator<FileEntity>() {
        @Override
        public int compare(FileEntity lhs, FileEntity rhs) {
            return Integer.valueOf(rhs.getFileType()).compareTo(lhs.getFileType());
        }
    };

    public static Comparator<FileEntity> COMPARE_BY_NAME_ASC = new Comparator<FileEntity>() {
        @Override
        public int compare(FileEntity lhs, FileEntity rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    };
    public static Comparator<FileEntity> COMPARE_BY_NAME_DESC = new Comparator<FileEntity>() {
        @Override
        public int compare(FileEntity lhs, FileEntity rhs) {
            return rhs.getName().compareTo(lhs.getName());
        }
    };

    public static Comparator<FileEntity> COMPARE_BY_SIZE_ASC = new Comparator<FileEntity>() {
        @Override
        public int compare(FileEntity lhs, FileEntity rhs) {
            return Long.valueOf(lhs.getSize()).compareTo(rhs.getSize());
        }
    };
    public static Comparator<FileEntity> COMPARE_BY_SIZE_DESC = new Comparator<FileEntity>() {
        @Override
        public int compare(FileEntity lhs, FileEntity rhs) {
            return Long.valueOf(rhs.getSize()).compareTo(lhs.getSize());
        }
    };

    public static Comparator<FileEntity> COMPARE_BY_DATE_ASC = new Comparator<FileEntity>() {
        @Override
        public int compare(FileEntity lhs, FileEntity rhs) {
            return Long.valueOf(lhs.getDate()).compareTo(rhs.getDate());
        }
    };
    public static Comparator<FileEntity> COMPARE_BY_DATE_DESC = new Comparator<FileEntity>() {
        @Override
        public int compare(FileEntity lhs, FileEntity rhs) {
            return Long.valueOf(rhs.getDate()).compareTo(lhs.getDate());
        }
    };

    public static Comparator<FileEntity> COMPARE_BY_IN_TAB_ASC = new Comparator<FileEntity>() {
        @Override
        public int compare(FileEntity lhs, FileEntity rhs) {
            return (lhs.isOnTablet() == rhs.isOnTablet() ? 0 : (lhs.isOnTablet() ? 1 : -1));
        }
    };
    public static Comparator<FileEntity> COMPARE_BY_IN_TAB_DESC = new Comparator<FileEntity>() {
        @Override
        public int compare(FileEntity lhs, FileEntity rhs) {
            return (rhs.isOnTablet() == lhs.isOnTablet() ? 0 : (rhs.isOnTablet() ? 1 : -1));
        }
    };

    public static Comparator<FileEntity> COMPARE_BY_IN_CLOUD_ASC = new Comparator<FileEntity>() {
        @Override
        public int compare(FileEntity lhs, FileEntity rhs) {
            return (lhs.isOnCloud() == rhs.isOnCloud() ? 0 : (lhs.isOnCloud() ? 1 : -1));
        }
    };
    public static Comparator<FileEntity> COMPARE_BY_IN_CLOUD_DESC = new Comparator<FileEntity>() {
        @Override
        public int compare(FileEntity lhs, FileEntity rhs) {
            return (rhs.isOnCloud() == lhs.isOnCloud() ? 0 : (rhs.isOnCloud() ? 1 : -1));
        }
    };

}
