package com.example.unternehmenshandbuch.helper;

import java.util.UUID;

public class Helper {

    public static String generateOrRetrievePublicId(String publicId) {
        return (publicId == null) ? UUID.randomUUID().toString() : publicId;
    }

    public static Integer generateOrRetrieveVersion(Integer version) {
        return (version == null) ? 0 : version;
    }

}
