package xyz.imxqd.lib;



import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Main {


    public static void main(String... args) throws IOException {
        if(args.length != 3) {
            System.out.println("用法：");
            System.out.println("java -jar img.jar [image_1] [image_2] [output]");
            System.out.println("用法例子：");
            System.out.println("java -jar img.jar 1.jpg 2.jpg 3");
            return;
        }
        BufferedImage ima = ImageIO.read(new File(args[0]));
        BufferedImage ima2 = ImageIO.read(new File(args[1]));
        BufferedImage img = compositePicture(ima, ima2);
        writeToFile(img, "png", new File(args[2] + ".png"));
    }

    public static BufferedImage compositePicture(BufferedImage img1, BufferedImage img2) throws IOException {
        int width = img2.getWidth() < img1.getWidth() ? img2.getWidth() : img1.getWidth();
        int height = img2.getHeight() < img1.getHeight() ? img2.getHeight() : img1.getHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int[] a = getARGB(img1.getRGB(x, y));
                int[] b = getARGB(img2.getRGB(x, y));
                int g1 = RGB2Gray(a[1], a[2], a[3]) / 2;
                if (g1 >= 64) {
                    a[0] = 0;
                    a[1] = 0;
                    a[2] = 0;
                    a[3] = 0;
                } else {
                    a[0] = 255;
                    a[1] = 0;
                    a[2] = 0;
                    a[3] = 0;
                }
                int g2 = RGB2Gray(b[1], b[2], b[3]) / 2;
                if (g2 >= 64) {
                    b[0] = 255;
                    b[1] = 255;
                    b[2] = 255;
                    b[3] = 255;
                } else {
                    b[0] = 0;
                    b[1] = 0;
                    b[2] = 0;
                    b[3] = 0;
                }

                image.setRGB(x, y, getColor(getCARGB(a[0], a[1], a[2], a[3]), getCARGB(b[0], b[1], b[2], b[3])));
            }
        }
        return image;
    }

    public static int RGB2Gray(int r, int g, int b) {
        return (r * 299 + g * 587 + b * 114) / 1000;
    }

    public static void writeToFile(BufferedImage image, String format, File file)
            throws IOException {
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    public static int getColor(int color1, int color2) {
        int[] c1 = getARGB(color1);
        int[] c2 = getARGB(color2);
        int[] c3 = new int[2];
        if(c1[0] >= 250 && c2[0] <= 5) {
            c3[0] = 128;
            c3[1] = 0;
        } else if (c1[0] >= 250 && c2[0] >= 250) {
            c3[0] = 255;
            c3[1] = 128;
        } else if (c1[0] <= 5 && c2[0] >= 250) {
            c3[0] = 128;
            c3[1] = 255;
        } else if (c1[0] <= 5 && c2[0] <= 5) {
            c3[0] = 0;
            c3[1] = 128;
        }
        return getCARGB(c3[0], c3[1], c3[1], c3[1]);
    }

    public static int getCARGB(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red<< 16) | (green << 8) | blue;
    }

    public static int[] getARGB(int cARGB) {
        int alpha = (cARGB >> 24)& 0xff; //透明度通道
        int red = (cARGB >> 16) &0xff;
        int green = (cARGB >> 8) &0xff;
        int blue = cARGB & 0xff;
        return new int[]{alpha, red, green, blue};
    }
}
