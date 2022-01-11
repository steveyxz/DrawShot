package src.view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSaver {

    private int width;
    private int height;
    private String fileType;

    public ImageSaver(int width, int height, String fileType) {
        this.height = height;
        this.width = width;
        this.fileType = fileType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BufferedImage readImage(String imageURL) {
        File file = new File(imageURL);
        BufferedImage returnedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        try {
            returnedImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnedImage;
    }

    public void writeFile(String URL, BufferedImage image) {
        File file = new File(URL);
        try {
            ImageIO.write(image, ViewManager.fileTypeToSave, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
