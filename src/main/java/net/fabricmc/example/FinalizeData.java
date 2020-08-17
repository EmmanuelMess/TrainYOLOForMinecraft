package net.fabricmc.example;

import javax.imageio.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class FinalizeData {

    public static void finalizeData(File finalDir, List<String> classes, List<LearnableData> images) throws IOException {
        File dataDir = new File(finalDir, "data");
        dataDir.mkdir();
        File namesFile = new File(dataDir, "obj.names");
        {
            FileWriter trainFileWriter = new FileWriter(namesFile);
            trainFileWriter.write(String.join("\n", classes));
            trainFileWriter.close();
        }
        {
            File dataFile = new File(dataDir, "obj.data");
            FileWriter dataWriter = new FileWriter(dataFile);
            dataWriter.write(String.format(
                    "classes = %d\n" +
                    "train  = data/train.txt\n" +
                    "valid = data/test.txt\n" +
                    "names = data/obj.names\n" +
                    "backup = backup/",
                    classes.size()
            ));
            dataWriter.close();
        }
        ArrayList<File> imageFiles = new ArrayList<>();
        {
            File objDir = new File(dataDir, "obj");
            objDir.mkdir();
            for(LearnableData data : images) {
                {
                    File jpegFile = new File(objDir, String.format("%d.jpg", data.id));
                    imageFiles.add(jpegFile);
                    ImageIO.write(ImageIO.read(data.image), "jpg", jpegFile);
                }

                {
                    File boundingBoxFile = new File(objDir, String.format("%d.txt", data.id));
                    FileWriter trainFileWriter = new FileWriter(boundingBoxFile);
                    trainFileWriter.write(String.format(
                            "%d %d %d %d %d",
                            data.id,
                            data.box.x,
                            data.box.y,
                            data.box.width,
                            data.box.height
                    ));
                    trainFileWriter.close();
                }
            }
        }
        {
            File trainFile = new File(dataDir, "train.txt");
            FileWriter trainFileWriter = new FileWriter(trainFile);
            trainFileWriter.write(
                    imageFiles.stream()
                            .map(File::getName)
                            .map(name -> "data/obj/" + name)
                            .collect(Collectors.joining("\n"))
            );
            trainFileWriter.close();
        }
        {
            File trainFile = new File(dataDir, "test.txt");
            FileWriter trainFileWriter = new FileWriter(trainFile);
            trainFileWriter.write(
                    imageFiles.stream()
                            .map(File::getName)
                            .map(name -> "data/obj/" + name)
                            .collect(Collectors.joining("\n"))
            );
            trainFileWriter.close();
        }
    }

    private FinalizeData() {}

    public static final class LearnableData {
        public final int id;
        public final File image;
        public final BoundingBox box;

        public LearnableData(int id, File image, BoundingBox box) {
            this.id = id;
            this.image = image;
            this.box = box;
        }
    }

    public static final class BoundingBox {
        public final int x;
        public final int y;
        public final int width;
        public final int height;

        public BoundingBox(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
