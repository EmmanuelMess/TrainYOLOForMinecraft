package net.fabricmc.example;

import com.mojang.blaze3d.systems.*;
import net.minecraft.client.*;
import net.minecraft.client.gl.*;
import net.minecraft.client.texture.*;
import net.minecraft.util.*;

import java.io.*;

import static net.minecraft.client.util.ScreenshotUtils.*;

public final class SaveDataScreenshot {

    public static void saveScreenshot(
            MinecraftClient client,
            int id,
            String label,
            int startX,
            int startY,
            int width,
            int height
    ) {
        File dir = new File(client.runDirectory, "data");
        dir.mkdir();

        saveScreenshot(
                dir,
                id,
                client.getWindow().getFramebufferWidth(),
                client.getWindow().getFramebufferHeight(),
                client.getFramebuffer(),
                () -> saveMetadata(dir, id, label, startX, startY, width, height)
        );
    }

    private static void saveScreenshot(
            File dataDir, int id, int framebufferWidth, int framebufferHeight, Framebuffer framebuffer, Runnable messageReceiver) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() ->
                    saveScreenshotInner(dataDir, id, framebufferWidth, framebufferHeight, framebuffer, messageReceiver));
        } else {
            saveScreenshotInner(dataDir, id, framebufferWidth, framebufferHeight, framebuffer, messageReceiver);
        }

    }

    private static void saveScreenshotInner(
            File dataDir,
            int id,
            int framebufferWidth,
            int framebufferHeight,
            Framebuffer framebuffer,
            Runnable messageReceiver
    ) {
        NativeImage nativeImage = takeScreenshot(framebufferWidth, framebufferHeight, framebuffer);
        File file = new File(dataDir, "images");
        file.mkdir();
        File file3 = new File(file, id + ".png");

        Util.method_27958().execute(() -> {
            try {
                nativeImage.writeFile(file3);
                messageReceiver.run();
            } catch (IOException var7) {

            } finally {
                nativeImage.close();
            }

        });
    }

    private static void saveMetadata(
            File gameDirectory,
            int id,
            String label,
            int startX,
            int startY,
            int width,
            int height
    ) {
        File file = new File(gameDirectory, "labels");
        file.mkdir();
        File file3 = new File(file, id + ".txt");

        Util.method_27958().execute(() -> {
            FileWriter writer;
            try {
                writer = new FileWriter(file3);
                writer.write(String.format("%s %d %d %d %d", label, startX, startY, width, height));
                writer.close();
            } catch (Exception var7) {
                //fail
            }
        });
    }

    private SaveDataScreenshot() {
    }
}
