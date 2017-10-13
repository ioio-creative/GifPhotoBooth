import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;

public class UiImage {
    private PApplet applet;
    private String imgUrl;
    private PImage pImg;

    public UiImage(PApplet anApplet, String imageUrl) {
        this.applet = anApplet;
        this.setPImage(imageUrl);
    }

    public UiImage(PApplet anApplet, PImage pImage) {
        this.applet = anApplet;
        this.setPImage(pImage);
    }

    public void setPImage(String imageUrl) {
        this.imgUrl = imageUrl;
        this.pImg = this.applet.loadImage(imageUrl);
    }

    public void setPImage(PImage pImage) {
        this.imgUrl = null;
        this.pImg = pImage;
    }

    public PImage getPImage() {
        return this.pImg;
    }

    public PApplet getApplet() {
        return this.applet;
    } 

    public int getWidth() {
        return this.pImg.width;
    }

    public int getHeight() {
        return this.pImg.height;
    }

    public void showImageInApplet(float x, float y, float w, float h) {
        this.applet.image(this.pImg, x, y, w, h);
    }

    public void save(String path) {        
        this.pImg.save(path);
        this.imgUrl = path;
    }

    public void invertLeftRight() {
        int height = this.pImg.height;
        int width = this.pImg.width;        
        this.pImg.loadPixels();
        int[] originalPixels = this.copyNewPixels(this);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {                
                this.pImg.pixels[row * width + col] = 
                    originalPixels[row * width + width - 1 - col];
            }
        }
        this.pImg.updatePixels();
    }

    public void invertTopBottom() {
        int height = this.pImg.height;
        int width = this.pImg.width;
        this.pImg.loadPixels();
        int[] originalPixels = this.copyNewPixels(this);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                this.pImg.pixels[row * width + col] = 
                    originalPixels[(height - 1 - row) * width + col];
            }
        }
        this.pImg.updatePixels();
    }

    public void invert() {
        int height = this.pImg.height;
        int width = this.pImg.width;
        this.pImg.loadPixels();
        int[] originalPixels = this.copyNewPixels(this);  
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                this.pImg.pixels[row * width + col] = 
                    originalPixels[(height - 1 - row) * width + width - 1 - col];
            }
        }
        this.pImg.updatePixels();
    }

    // https://processing.org/reference/color_datatype.html
    private static int[] copyNewPixels(UiImage uiImg) {
        PImage myPImg = uiImg.getPImage(); 
        myPImg.loadPixels();
        int[] newPixels = new int[myPImg.pixels.length];
        for (int i = 0; i < myPImg.pixels.length; i++) {
            newPixels[i] = myPImg.pixels[i];
        }
        return newPixels;
    }

    // http://stackoverflow.com/questions/19617476/adding-one-image-on-top-of-another-in-processing
    public void copy(UiImage src, int sx, int sy, int sw, int sh,
        int dx, int dy, int dw, int dh) {
        
        this.getPImage().copy(src.getPImage(), sx, sy, sw, sh,
            dx, dy, dw, dh);
    }

    public void resize(int w, int h) {
        this.pImg.resize(w, h);
    }


    /* check mouse overs */

    // https://processing.org/examples/button.html
    public boolean overRect(int x, int y)  {
        return (this.applet.mouseX >= x && this.applet.mouseX <= x + this.pImg.width && 
            this.applet.mouseY >= y && this.applet.mouseY <= y + this.pImg.height);
    }

    /* end of check mouse overs */


    /* filter effects */
    /* https://processing.org/reference/PImage_filter_.html */

    // 0.0 <= param <= 1.0
    public static UiImage getNewImgWithFilterByThreshold(UiImage img, float param) {
        PImage clone = img.getPImage().copy();
        clone.filter(PImage.THRESHOLD, param);
        return new UiImage(img.getApplet(), clone);
    }

    public static UiImage getNewImgWithFilterByGray(UiImage img) {
        PImage clone = img.getPImage().copy();
        clone.filter(PImage.GRAY);
        return new UiImage(img.getApplet(), clone);
    }

    public static UiImage getNewImgWithFilterByInvert(UiImage img) {
        PImage clone = img.getPImage().copy();
        clone.filter(PImage.INVERT);
        return new UiImage(img.getApplet(), clone);
    }

    // 2 <= param <= 255
    public static UiImage getNewImgWithFilterByPosterize(UiImage img, int param) {
        PImage clone = img.getPImage().copy();
        clone.filter(PImage.POSTERIZE, param);
        return new UiImage(img.getApplet(), clone);
    }

    public static UiImage getNewImgWithFilterByBlur(UiImage img, int param) {
        PImage clone = img.getPImage().copy();
        clone.filter(PImage.BLUR, param);
        return new UiImage(img.getApplet(), clone);
    }

    public static UiImage getNewImgWithFilterByErode(UiImage img) {
        PImage clone = img.getPImage().copy();
        clone.filter(PImage.ERODE);
        return new UiImage(img.getApplet(), clone);
    }

    public static UiImage getNewImgWithFilterByDilate(UiImage img) {
        PImage clone = img.getPImage().copy();
        clone.filter(PImage.DILATE);
        return new UiImage(img.getApplet(), clone);
    }

    public static UiImage getNewImgWithRandomFilterEffect(UiImage img) {
        UiImage uiImgToReturn = null;
        UiImageFilterEffect randEffectSelected = UiImageFilterEffect.getRandom(img.getApplet());

        switch (randEffectSelected) {
            case THRESHOLD:
                uiImgToReturn = getNewImgWithFilterByThreshold(img, 0.7f);
                break;
            case GRAY:
                uiImgToReturn = getNewImgWithFilterByGray(img);
                break;
            case INVERT:
                uiImgToReturn = getNewImgWithFilterByInvert(img);
                break;
            case POSTERIZE:
                uiImgToReturn = getNewImgWithFilterByPosterize(img, 3);
                break;
            case BLUR:
                uiImgToReturn = getNewImgWithFilterByBlur(img, 2);
                break;
            case ERODE:
                uiImgToReturn = getNewImgWithFilterByErode(img);
                break;
            case DILATE:
                uiImgToReturn = getNewImgWithFilterByDilate(img);
                break;
        }

        return uiImgToReturn;
    }
    
    /* end of filter effects */
}