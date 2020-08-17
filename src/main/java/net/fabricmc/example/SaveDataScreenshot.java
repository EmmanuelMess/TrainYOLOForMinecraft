package net.fabricmc.example;

import com.mojang.blaze3d.systems.*;
import net.minecraft.client.*;
import net.minecraft.client.gl.*;
import net.minecraft.client.texture.*;
import net.minecraft.util.*;

import javax.imageio.*;
import java.io.*;
import java.util.function.*;

public final class SaveDataScreenshot {

    public static void saveScreenshot(
            File dataDir,
            int id,
            Framebuffer framebuffer,
            Consumer<File> callback
    ) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() ->
                    saveScreenshotInner(dataDir, id, framebuffer, callback));
        } else {
            saveScreenshotInner(dataDir, id, framebuffer, callback);
        }
    }

    private static void saveScreenshotInner(
            File dataDir,
            int id,
            Framebuffer framebuffer,
            Consumer<File> callback
    ) {

        NativeImage nativeImage = takeScreenshot(framebuffer);
        File file3 = new File(dataDir, id + ".png");

        Util.method_27958().execute(() -> {
            try {
                nativeImage.writeFile(file3);
                callback.accept(file3);
            } catch (IOException var7) {
                var7.printStackTrace();
            }
        });
    }

    private static NativeImage takeScreenshot(Framebuffer framebuffer) {
        int width = framebuffer.textureWidth;
        int height = framebuffer.textureHeight;
        NativeImage nativeImage = new NativeImage(width, height, false);
        RenderSystem.bindTexture(framebuffer.colorAttachment);
        nativeImage.loadFromTextureImage(0, true);
        nativeImage.mirrorVertically();
        return nativeImage;
    }

    private SaveDataScreenshot() {
    }
}
