import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//import javafx.scene.input.KeyEvent;

public class ImageEditorPanel extends JPanel implements MouseListener {

    Color[][] pixels;
    int globalWidth;
    int globalHeight;
    final int TOP_EDGE = 0;
    final int HORI_OFFSET = 20;
    final int VERT_SPACE = 20;
    final int BOX_WIDTH = 60;
    final int BOX_HEIGHT = 20;
    final int SIDE_PANEL_WIDTH = 100;
    int boxStartX;
    int boxStartY;
    Rectangle rotateButton;
    Rectangle horizontalFlipButton;
    Rectangle verticalFlipButton;
    Rectangle posterizeButton;
    Rectangle blurButton;
    Rectangle grayButton;
    Rectangle undoButton;
    Rectangle uploadButton;
    Rectangle saveAsButton;
    Rectangle exitButton;
    Rectangle contrastButton;
    Color[][][] undoArr = new Color[0][0][0];

    public ImageEditorPanel() {

        BufferedImage imageIn = null;
        try {
            // the image should be in the main project folder, not in \src or \bin
            //imageIn = ImageIO.read(new File("CAT789D.jpg"));
            imageIn = ImageIO.read(searchFile());
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        pixels = makeColorArray(imageIn);
        globalHeight = pixels.length;
        globalWidth = pixels[0].length;
        boxStartX = globalWidth + HORI_OFFSET;
        boxStartY = TOP_EDGE + VERT_SPACE;
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(globalWidth + SIDE_PANEL_WIDTH, globalHeight));
        addMouseListener(this);
        updateButtons();
        undoStore(pixels);
    }

    public void updateButtons() {
        rotateButton = new Rectangle(boxStartX, boxStartY, BOX_WIDTH, BOX_HEIGHT);
        horizontalFlipButton = new Rectangle(boxStartX, boxStartY * 3, BOX_WIDTH, BOX_HEIGHT);
        verticalFlipButton = new Rectangle(boxStartX, boxStartY * 5, BOX_WIDTH, BOX_HEIGHT);
        posterizeButton = new Rectangle(boxStartX, boxStartY * 7, BOX_WIDTH, BOX_HEIGHT);
        blurButton = new Rectangle(boxStartX, boxStartY * 9, BOX_WIDTH, BOX_HEIGHT);
        grayButton = new Rectangle(boxStartX, boxStartY * 11, BOX_WIDTH, BOX_HEIGHT);
        contrastButton = new Rectangle(boxStartX, boxStartY * 13, BOX_WIDTH, BOX_HEIGHT);
        undoButton = new Rectangle(boxStartX, boxStartY * 15, BOX_WIDTH, BOX_HEIGHT);
        uploadButton = new Rectangle(boxStartX, boxStartY * 17, BOX_WIDTH, BOX_HEIGHT);
        saveAsButton = new Rectangle(boxStartX, boxStartY * 19, BOX_WIDTH, BOX_HEIGHT);
        exitButton = new Rectangle(boxStartX, boxStartY * 21, BOX_WIDTH, BOX_HEIGHT);
    }

    public Color[][] undoDisplay() {
        return undoArr[undoArr.length - 1];
    }

    public void paintComponent(Graphics g) {

        // paints the array pixels onto the screen
        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[0].length; col++) {
                g.setColor(pixels[row][col]);
                g.fillRect(col, row, 1, 1);
            }
        }

        g.setColor(Color.BLACK);
        final int NUM_OF_BOX = 11;
        for (int i = 1; i < NUM_OF_BOX * 2; i += 2) {
            g.drawRect(boxStartX, boxStartY * i, BOX_WIDTH, BOX_HEIGHT);
        }

        g.drawString("Rotate", globalWidth + 30, TOP_EDGE + 35);
        g.drawString("H-Flip", globalWidth + 34, TOP_EDGE + 75);
        g.drawString("V-Flip", globalWidth + 34, TOP_EDGE + 115);
        g.drawString("Posterize", globalWidth + 25, TOP_EDGE + 155);
        g.drawString("Blur", globalWidth + 40, TOP_EDGE + 195);
        g.drawString("Gray Scale", globalWidth + 21, TOP_EDGE + 235);
        g.drawString("Contrast", globalWidth + 26, TOP_EDGE + 275);
        g.drawString("Undo", globalWidth + 36, TOP_EDGE + 315);
        g.drawString("Upload", globalWidth + 30, TOP_EDGE + 355);
        g.drawString("Save as...", globalWidth + 25, TOP_EDGE + 395);
        g.drawString("Exit", globalWidth + 38, TOP_EDGE + 435);

    }

    // call your image-processing methods here OR call them from keyboard event
    // handling methods
    public void run() {

        undoStore(pixels);
        repaint();

    }

    public File searchFile() {
        JFileChooser fileUpload = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "JPG, PNG & GIF Images", "png", "jpg", "gif");
        fileUpload.setFileFilter(filter);
        int res = fileUpload.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file_Path = new File(fileUpload.getSelectedFile().getAbsolutePath());
            return file_Path;
        }
        return null;

    }

    public void undoStore(Color[][] oldArr) {
        Color[][][] interArr = new Color[undoArr.length][oldArr.length][oldArr[0].length];
        
        for (int i = 0; i < undoArr.length; i++) {
            interArr[i] = undoArr[i];
        }

        undoArr = new Color[undoArr.length + 1][oldArr.length][oldArr[0].length];
        for (int i = 0; i < interArr.length - 1; i++) {
            undoArr[i] = interArr[i];
        }
        undoArr[undoArr.length - 1] = oldArr;

    }

    public Color[][] grayScale(Color[][] oldArr) {
        Color[][] newArr = new Color[oldArr.length][oldArr[0].length];
        for (int r = 0; r < oldArr.length; r++) {
            for (int c = 0; c < oldArr[r].length; c++) {
                int gray = (oldArr[r][c].getRed() + oldArr[r][c].getGreen() + oldArr[r][c].getBlue()) / 3;
                newArr[r][c] = new Color(gray, gray, gray);
            }
        }
        return newArr;
    }

    public Color[][] rotateCCW(Color[][] oldArr) {
        Color[][] newArr = new Color[oldArr[0].length][oldArr.length];
        for (int r = 0; r < newArr[0].length; r++) {
            for (int c = 0; c < newArr.length; c++) {
                newArr[c][r] = oldArr[r][c];
            }
        }
        globalWidth = newArr[0].length;
        globalHeight = newArr.length;
        boxStartX = globalWidth + HORI_OFFSET;
        boxStartY = TOP_EDGE + VERT_SPACE;
        updateButtons();
        setPreferredSize(new Dimension(globalWidth + 100, globalHeight));
        JFrame jf = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        jf.pack();
        return newArr;
    }

    public Color[][] posterize(Color[][] oldArr) {
        Color blue = new Color(43, 74, 120);
        Color yellow = new Color(212, 211, 195);
        Color green = new Color(112, 135, 124);
        Color lightBlue = new Color(76, 116, 122);
        int blDist = 0;
        int yDist = 0;
        int gDist = 0;
        int lBDist = 0;

        Color[][] newArr = new Color[oldArr.length][oldArr[0].length];
        for (int r = 0; r < newArr.length; r++) {
            for (int c = 0; c < newArr[r].length; c++) {

                blDist = (int) (Math.sqrt((oldArr[r][c].getRed() - blue.getRed()) * (oldArr[r][c].getRed() - blue.getRed()) +
                (oldArr[r][c].getGreen() - blue.getGreen()) * (oldArr[r][c].getGreen() - blue.getGreen())+
                (oldArr[r][c].getBlue() - blue.getBlue()) * (oldArr[r][c].getBlue() - blue.getBlue())));

                yDist = (int) (Math.sqrt((oldArr[r][c].getRed() - yellow.getRed()) * (oldArr[r][c].getRed() - yellow.getRed()) +
                (oldArr[r][c].getGreen() - yellow.getGreen()) * (oldArr[r][c].getGreen() - yellow.getGreen()) +
                (oldArr[r][c].getBlue() - yellow.getBlue()) * (oldArr[r][c].getBlue() - yellow.getBlue())));

                gDist = (int) (Math.sqrt((oldArr[r][c].getRed() - green.getRed()) * (oldArr[r][c].getRed() - green.getRed()) +
                (oldArr[r][c].getGreen() - green.getGreen()) * (oldArr[r][c].getGreen() - green.getGreen()) +
                (oldArr[r][c].getBlue() - green.getBlue()) * (oldArr[r][c].getBlue() - green.getBlue())));

                lBDist = (int) (Math.sqrt((oldArr[r][c].getRed() - lightBlue.getRed()) * (oldArr[r][c].getRed() - lightBlue.getRed()) +
                (oldArr[r][c].getGreen() - lightBlue.getGreen()) * (oldArr[r][c].getGreen() - lightBlue.getGreen()) +
                (oldArr[r][c].getBlue() - lightBlue.getBlue()) * (oldArr[r][c].getBlue() - lightBlue.getBlue())));

                if (blDist <= yDist && blDist <= gDist && blDist <= lBDist) {
                    newArr[r][c] = blue;
                }
                if (yDist <= blDist && yDist <= gDist && yDist <= lBDist) {
                    newArr[r][c] = yellow;
                }
                if (gDist <= yDist && gDist <= blDist && gDist <= lBDist) {
                    newArr[r][c] = green;
                }
                if (lBDist <= yDist && lBDist <= gDist && lBDist <= blDist) {
                    newArr[r][c] = lightBlue;
                }

            }
        }
        return newArr;
    }

    public Color[][] flipHori(Color[][] oldArr) {
        Color[][] newArr = new Color[oldArr.length][oldArr[0].length];
        for (int r = 0; r < oldArr.length; r++) {
            for (int c = 0; c < oldArr[r].length; c++) {
                newArr[r][newArr[r].length - 1 - c] = oldArr[r][c];
            }
        }
        return newArr;
    }

    public Color[][] flipVert(Color[][] oldArr) {
        Color[][] newArr = new Color[oldArr.length][oldArr[0].length];
        for (int r = 0; r < oldArr.length; r++) {
            for (int c = 0; c < oldArr[r].length; c++) {
                newArr[newArr.length - 1 - r][c] = oldArr[r][c];
            }
        }
        return newArr;
    }

    public Color[][] makeColorArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] result = new Color[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color c = new Color(image.getRGB(col, row), true);
                result[row][col] = c;
            }
        }
        // System.out.println("Loaded image: width: " +width + " height: " + height);
        return result;
    }

    

    public void mouseClicked(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {
        
    }

    public void mouseExited(MouseEvent e) {
        
    }

    public void mousePressed(MouseEvent e) {
        int pressX = e.getX();
        int pressY = e.getY();

        if (rotateButton.contains(pressX, pressY)) {
            pixels = rotateCCW(pixels);
        }
        if (horizontalFlipButton.contains(pressX, pressY)) {
            pixels = flipHori(pixels);
        }
        if (verticalFlipButton.contains(pressX, pressY)) {
            pixels = flipVert(pixels);
        }
        if (posterizeButton.contains(pressX, pressY)) {
            pixels = posterize(pixels);
        }
        if (blurButton.contains(pressX, pressY)) {
            pixels = rotateCCW(pixels);
        }
        if (grayButton.contains(pressX, pressY)) {
            pixels = grayScale(pixels);
        }
        if (undoButton.contains(pressX, pressY)) {
            pixels = undoDisplay();
        }
        if (grayButton.contains(pressX, pressY)) {
            pixels = grayScale(pixels);
        }
        if (uploadButton.contains(pressX, pressY)) {
            BufferedImage imageIn = null;
            try {
                imageIn = ImageIO.read(searchFile());
            } catch (IOException r) {
                System.out.println(r);
                System.exit(1);
            }
        }
        if (exitButton.contains(pressX, pressY)){
            System.exit(0);
        }
        undoStore(pixels);
        repaint();
    }

    

    public void mouseReleased(MouseEvent e) {

    }
}
