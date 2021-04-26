package com.example.homeassistant.helpers;

import com.bumptech.glide.load.Key;

import java.nio.ByteBuffer;

import java.security.MessageDigest;

public class GlideSignature implements Key {
    private int currentVersion;

    public GlideSignature(int currentVersion) {
        this.currentVersion = currentVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GlideSignature) {
            GlideSignature other = (GlideSignature) o;
            return (currentVersion == other.currentVersion);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return currentVersion;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest md) {
        md.update(ByteBuffer.allocate(Integer.SIZE).putInt(currentVersion).array());
    }
}
