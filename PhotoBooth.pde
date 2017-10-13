/*****
 *
 *  This is a photo booth app developed to celebrate 45th anniversary of STGSS.
 *  It uses a web cam to take photos.
 *  Its output is a photo print-out and an animated gif composed of the photos taken.
 *  Source code adapted from the K11 photo booth project by Hochi Lau, IOIO
 *  by Christopher Wong, IOIO
 *  21st April, 2017
 *
 *****/

import gifAnimation.GifMaker;

import g4p_controls.GButton;
import g4p_controls.GEvent;
import g4p_controls.GImageButton;

import processing.video.Capture;

import java.util.List;
import java.util.ArrayList;


/* constants */

// http://www.ultimateprogrammingtutorials.info/2014/01/new-line-break-java.html
public static String LINE_BREAK = System.getProperty("line.separator");
// https://stackoverflow.com/questions/8075373/file-separator-vs-filesystem-getseparator-vs-system-getpropertyfile-separato
public static String FILE_SEPARATOR = System.getProperty("file.separator");

private final int PAPER_COUNT_RESET_VALUE = 20;
private final int LOAD_COUNT_RESET_VALUE = 0;

public final String BACKGROUND_IMAGE_FOLDER_PATH_UNDER_DATA = "UI/backgrounds/";
public final String BUTTON_IMAGE_FOLDER_PATH_UNDER_DATA = "UI/buttons/";
public final String LOADING_IMAGE_FOLDER_PATH_UNDER_DATA = "UI/loading/";
public final String PRINT_BG_FILE_PATH_UNDER_DATA = "UI/print/print_photobooth_without_grid-01.png";
public final String FONT_FILE_PATH_UNDER_DATA = "font/DINCond-Medium.otf";
public final String TMPPHOTOOUT_FOLDER_PATH_UNDER_DATA = "output/tmpPhotoOut/";
public final String TMPPRINTOUT_FOLDER_PATH_UNDER_DATA = "output/tmpPrintOut/";
public final String GIFOUT_FOLDER_PATH_UNDER_DATA = "output/gifOut/";
public final String LOG_FILE_PATH_UNDER_DATA = "log/stgss_photo_booth.log";

public final String[] PRINT_OUT_FILE_NAMES = { "printResult1.jpg", "printResult2.jpg" };
public final String[] PRINT_GRID_OUT_FILE_NAMES = { "printGrid1.jpg", "printGrid2.jpg" };

/* end of constants */


/* globals */

public float CAM_PIC_SHOW_X;
public float CAM_PIC_SHOW_Y;
public float CAM_PIC_SHOW_WIDTH;
public float CAM_PIC_SHOW_HEIGHT;

public float CAM_PIC_SHOW_Y_OFFSET_FACTOR_IN_OUTPUT = 1.3f;

public float DEFAULT_SINGLE_BTN_HEIGHT;
public float DEFAULT_SINGLE_BTN_WIDTH;
public float DEFAULT_DOUBLE_BTN_HEIGHT;
public float DEFAULT_DOUBLE_BTN_WIDTH;

public float DEFAULT_BTN_Y;
public float DEFAULT_SINGLE_BTN_X;
public float DEFAULT_DOUBLE_BTN_1_X;
public float DEFAULT_DOUBLE_BTN_2_X;

/* end of globals */


/* privates */

private SimpleLogger logger;
private PrinterControl printer;
private Capture cam;
private PageCollection myPageCollection;
private UiImage camPI;
private PFont font;
private Language lang = Language.ENG;

private int gifGridChangeCounter = 0;
private int gifGridRow;
private int gifGridColumn;

private int[][] gifGridCellColors = {  // RGBA
    { 255, 204, 0, 126 },  // yello        
    { 76, 213, 231, 126 },  // blue
    { 255, 114, 117, 126 },  // red
    { 217, 179, 255, 126 }  // purple
};

UiImage printBg; 

private String nameOfFileToPrint;

/* end of privates */


public void settings() {    
    //size(540, 960);
    size(750, 1333);
    //fullScreen(1);
}

public void setup() {
    try {
        logger = new SimpleLogger(getLogFilePath());
        logger.info("Setup started.");

        printer = new PrinterControl(this);

        // set cam pic show parameters
        this.CAM_PIC_SHOW_X = this.width * 0.056f;
        this.CAM_PIC_SHOW_Y = this.height * 0.184f;
        this.CAM_PIC_SHOW_WIDTH = this.width - 2 * this.CAM_PIC_SHOW_X;
        this.CAM_PIC_SHOW_HEIGHT = this.CAM_PIC_SHOW_WIDTH;

        // set default button parameters        
        this.DEFAULT_SINGLE_BTN_WIDTH = 0.2f * this.width;        
        this.DEFAULT_SINGLE_BTN_HEIGHT = this.DEFAULT_SINGLE_BTN_WIDTH;
        this.DEFAULT_DOUBLE_BTN_WIDTH = 0.2f * this.width;
        this.DEFAULT_DOUBLE_BTN_HEIGHT = this.DEFAULT_DOUBLE_BTN_WIDTH;

        this.DEFAULT_BTN_Y = 0.75f * this.height;
        this.DEFAULT_SINGLE_BTN_X = 0.5f * (this.width - this.DEFAULT_SINGLE_BTN_WIDTH);
        this.DEFAULT_DOUBLE_BTN_1_X = 0.5f * this.width - this.DEFAULT_DOUBLE_BTN_WIDTH - 0.03f * this.width;
        this.DEFAULT_DOUBLE_BTN_2_X = 0.5f * this.width  + 0.03f * this.width;        

        // load background image used for photo printing
        printBg = new UiImage(this, this.getPrintBgFilePath());

        // set frame title, rate and font
        surface.setTitle("STGSS Photo Booth");
        frameRate(30);
        font = createFont(this.getFontFilePath(), 32);
        textFont(font);
        textAlign(CENTER, CENTER);

        // look for available cameras
        // The camera can be initialized directly using an 
        // element from the array returned by list():
        String[] cameras = Capture.list();
    
        if (cameras.length == 0) {
            throw new Exception("No cameras available for capture.");            
        } else {
            StringBuilder camList = new StringBuilder("Available cameras:" + LINE_BREAK);
            for (int i = 0; i < cameras.length; i++) {
                camList.append("cam no." + i + " " + cameras[i] + LINE_BREAK);
            }
            logger.info(camList.toString());
            
            //int camSelectedIdx = 1;
            //int camSelectedIdx = 15;
            //int camSelectedIdx = 35;                                          
            //cam = new Capture(this, cameras[camSelectedIdx]);
            cam = new Capture(this, 640, 480, "Logitech HD Pro Webcam C920", 30);
            cam.start();
            //logger.info("Camera " + camSelectedIdx + " started.");
            logger.info("Camera " + cam + " started.");
        }

        // load the pages
        logger.info("Loading page objects started.");
        myPageCollection = new PageCollection(this, lang);
        logger.info("Loading page objects finished.");
        
        myPageCollection.setAllPageButtonsVisible(false);
        
        // Set start page
        myPageCollection.setActivePageID(PageId.Start);

        logger.info("Setup finished.");
    } catch (Exception ex) {
        this.handleGeneralException(ex);
        exit();
    }
}

public void draw() {
    try {                   
        myPageCollection.updateActivePage();
    } catch (Exception ex) {
        this.handleGeneralException(ex);
    }
}


/* path helpers */

public String getDataFolderPath() {
    return dataPath("") + FILE_SEPARATOR;
}

public String getSketchFolderPath() {
    return sketchPath("") + FILE_SEPARATOR;
}

public String getBackgroundImageFolderPath() {
    return dataPath(BACKGROUND_IMAGE_FOLDER_PATH_UNDER_DATA) + FILE_SEPARATOR;
}

public String getButtonImageFolderPath() {
    return dataPath(BUTTON_IMAGE_FOLDER_PATH_UNDER_DATA) + FILE_SEPARATOR;
}

public String getLoadingImageFolderPath() {
    return dataPath(LOADING_IMAGE_FOLDER_PATH_UNDER_DATA) + FILE_SEPARATOR;
}

public String getPrintBgFilePath() {
    return dataPath(PRINT_BG_FILE_PATH_UNDER_DATA);
}

public String getFontFilePath() {
    return dataPath(FONT_FILE_PATH_UNDER_DATA);
}

public String getTmpPhotoOutFolderPath() {
    return dataPath(TMPPHOTOOUT_FOLDER_PATH_UNDER_DATA) + FILE_SEPARATOR;
}

public String getGifOutFolderPath() {
    return dataPath(GIFOUT_FOLDER_PATH_UNDER_DATA) + FILE_SEPARATOR;
}

public String getTmpPrintOutFolderPath() {
    return dataPath(TMPPRINTOUT_FOLDER_PATH_UNDER_DATA) + FILE_SEPARATOR;
}

public String getLogFilePath() {
    return dataPath(LOG_FILE_PATH_UNDER_DATA);
}

/* end of path helpers */


/* draw UI helpers */

public UiImage getFullScreenScreenCap() {
    return new UiImage(this, this.get(0, 0, this.width, this.height));
}

public void setBackgroundImage(UiImage anImg) {
    anImg.showImageInApplet(0, 0, width, height);
}

public void setCameraPicture() {
    if (this.cam.available()) {
        this.cam.read();

        // crop cam pic for display
        // aspect ratio 1:1 for square image
        this.camPI = 
            this.cam.width > this.cam.height ?
            new UiImage(this, cam.get(0, 0, cam.height, cam.height)) :
            new UiImage(this, cam.get(0, 0, cam.width, cam.width));

        pushMatrix();
        scale(-1.0, 1.0);  // flip camera picuter left-right
        this.camPI.showImageInApplet(-CAM_PIC_SHOW_X, CAM_PIC_SHOW_Y,
            -CAM_PIC_SHOW_WIDTH, CAM_PIC_SHOW_HEIGHT);

        popMatrix();
    }    
}

/* end of draw UI helpers */


/* saving and loading camera photos */

public void saveCameraPictureToTempFolder(int camPicIdx) {
    this.camPI.invertLeftRight();
    this.camPI.save(this.getTmpPhotoOutFileFullPath(camPicIdx));
}

public UiImage[] loadCameraPicturesFromTempFolder(int noOfPhotosTaken) {
    UiImage[] pictures = new UiImage[noOfPhotosTaken];
    for (int i = 0; i < noOfPhotosTaken; i++) {
        pictures[i] = new UiImage(this, this.getTmpPhotoOutFileFullPath(i));
    }
    return pictures;
}

private String getTmpPhotoOutFileFullPath(int camPicIdx) {
    return this.getTmpPhotoOutFolderPath() + camPicIdx + ".jpg";
}

/* end of saving and loading camera photos */


/* output gif */

private String generateOutputGifNameWithoutExt() {
    return "stgss" + PApplet.year() + PApplet.month() + PApplet.day() + PApplet.hour() + PApplet.minute() + PApplet.second();    
}

public String generateOutputGifFileFullPath() {
    return this.getGifOutFolderPath() + this.generateOutputGifNameWithoutExt() + ".gif";
}

/* end of output gif */


/* gif grid helpers */

public void setGifGridRowAndColumnByCounter() {    
    switch (this.gifGridChangeCounter % 3) {  // 3 modes
        case 0:
            this.setGifGrid(2, 2);
            this.gifGridChangeCounter = 0;  // prevent it from growing too large
            break;
        case 1:
            this.setGifGrid(4, 4);
            break;
        case 2:
            this.setGifGrid(1, 1);
            break;
        default:
            this.setGifGrid(4, 4);
            break;
    }
    this.gifGridChangeCounter++;
}

private void setGifGrid(int row, int col) {
    this.gifGridColumn = row;
    this.gifGridRow = col;
}

public void showImagesInGifGrid(UiImage[] images, boolean isUseTransparentTint, boolean isUseRandFilter,
    boolean isOffsetCamPicY) {
    int randColorIdx, randImageIdx;
    float imgX, imgY;
    float gifGridStartPosX = this.CAM_PIC_SHOW_X;
    float gifGridStartPosY = 
        isOffsetCamPicY ? this.CAM_PIC_SHOW_Y * this.CAM_PIC_SHOW_Y_OFFSET_FACTOR_IN_OUTPUT : this.CAM_PIC_SHOW_Y;
    float imgW = this.CAM_PIC_SHOW_WIDTH / this.gifGridColumn;
    float imgH = this.CAM_PIC_SHOW_HEIGHT / this.gifGridRow;
    UiImage imgToUse;
    for (int i = 0; i < this.gifGridRow; i++) {    
        for (int j = 0; j < this.gifGridColumn; j++) {
            // set image coordinates
            imgX = gifGridStartPosX + (imgW * j);
            imgY = gifGridStartPosY + (imgH * i);
            
            // randomly choose color
            randColorIdx = (int)random(gifGridCellColors.length);

            if (isUseTransparentTint) {
                tint(gifGridCellColors[randColorIdx][0],
                    gifGridCellColors[randColorIdx][1],
                    gifGridCellColors[randColorIdx][2],                    
                    gifGridCellColors[randColorIdx][3]);  //random(255));                    
            } else {
                tint(gifGridCellColors[randColorIdx][0],
                    gifGridCellColors[randColorIdx][1],
                    gifGridCellColors[randColorIdx][2]);
            }

            // randomly choose cam pic to show
            randImageIdx = (int)random(images.length);
            imgToUse = images[randImageIdx];

            if (isUseRandFilter) {
                imgToUse = UiImage.getNewImgWithRandomFilterEffect(imgToUse);
            }

            imgToUse.showImageInApplet(imgX, imgY, imgW, imgH);
        }
    }
    //this.tint(255);  // reset tint
    this.noTint();
}

public void savePhotoToPrint(UiImage[] gridImages, boolean isUseTint,
    boolean isUseRandFilter, String printGridOutFileName, String printOutFileName) throws Exception {
    // paste gridImages on top of printBg
    this.setBackgroundImage(printBg);    

    int randImageIdx, colorIdx;
    int numOfGridRows = 2;
    int numOfGridCols = 2;
    float imgX, imgY;
    float gifGridStartPosX = this.CAM_PIC_SHOW_X;
    float gifGridStartPosY = this.CAM_PIC_SHOW_Y * this.CAM_PIC_SHOW_Y_OFFSET_FACTOR_IN_OUTPUT;
    float imgW = this.CAM_PIC_SHOW_WIDTH / numOfGridRows;
    float imgH = this.CAM_PIC_SHOW_HEIGHT / numOfGridCols;
    
    UiImage imgToUse;

    for (int row = 0; row < numOfGridRows; row++) {
        for (int col = 0; col < numOfGridCols; col++) {
            imgX = gifGridStartPosX + (imgW * col);
            imgY = gifGridStartPosY + (imgH * row);
             
            if (row == 1 && col == 0) {  // bottom left
                randImageIdx = (int)random(2, 4) % gridImages.length;
            } else if (row == 1 && col == 1) {  // bottom right
                randImageIdx = (int)random(4, 6) % gridImages.length;
            } else {  // top row
                randImageIdx = (row * numOfGridCols + col) % gridImages.length;
            }

            if (isUseTint) {
                colorIdx = (row * numOfGridCols + col) % gifGridCellColors.length;
                this.tint(gifGridCellColors[colorIdx][0],
                    gifGridCellColors[colorIdx][1],
                    gifGridCellColors[colorIdx][2]);                    
            }

            println(randImageIdx);
            imgToUse = gridImages[randImageIdx];

            if (isUseRandFilter) {
                imgToUse = UiImage.getNewImgWithRandomFilterEffect(imgToUse);
            }

            imgToUse.showImageInApplet(imgX, imgY, imgW, imgH);            
        } 
    }

    this.getGifGridAreaFromPrintScreen(true).save(this.getPrintResultFileFullPath(printGridOutFileName));    
    this.getFullScreenScreenCap().save(this.getPrintResultFileFullPath(printOutFileName));

    // reset to background image of active page
    this.noTint();
    myPageCollection.getActivePage().setPhotoBoothBgImg();
}

public UiImage produceScreenCapForGifExport(UiImage[] camPics) throws Exception {
    UiImage screenCapForGifExport;

    // Use printBg, instead of page background
    this.setBackgroundImage(printBg);

    boolean isUseTransparentTintInGifGrid = false;
    boolean isUseRandFilterInGifGrid = false;
    this.showImagesInGifGrid(camPics, isUseTransparentTintInGifGrid,
        isUseRandFilterInGifGrid, true);

    screenCapForGifExport = this.getFullScreenScreenCap();

    // reset to background image of active page
    this.noTint();
    myPageCollection.getActivePage().setPhotoBoothBgImg();

    return screenCapForGifExport;
}

private UiImage getGifGridAreaFromPrintScreen(boolean isOffsetCamPicY) {
    PImage gifGridAreaImg = this.get((int)CAM_PIC_SHOW_X, isOffsetCamPicY ? (int)(CAM_PIC_SHOW_Y * CAM_PIC_SHOW_Y_OFFSET_FACTOR_IN_OUTPUT) : (int)CAM_PIC_SHOW_Y,
        (int)CAM_PIC_SHOW_WIDTH, (int)CAM_PIC_SHOW_HEIGHT);
    return new UiImage(this, gifGridAreaImg);
}

public boolean printPhoto(String outputFileName) {
    return this.printer.printFile(this.getPrintResultFileFullPath(outputFileName));
}

public String getPrintResultFileFullPath(String outputFileName) {
    return this.getTmpPrintOutFolderPath() + outputFileName;
}

/* end of gif grid helpers */


/* print helpers */

public void setNameOfFileToPrint(String fileName) {
    this.nameOfFileToPrint = fileName;
}

public String getNameOfFileToPrint() {
    return this.nameOfFileToPrint;
}

/* end of print helpers */


/* helpers */

public int getCurrentTimeInMilliSecs() {
    //return hour()*3600+minute()*60+second();
    //return System.currentTimeMillis() / 1000;
    //return System.currentTimeMillis();
    return millis();
}

public void handleGeneralException(Exception ex) {
    logger.severe(StackTraceUtils.getStackTrace(ex));
}

/* end of helpers */

/* writing logs */

public void writeInfoLog(String str) {
    logger.info(str);
}

public void writeWarningLog(String str) {
    logger.warning(str);
}

public void writeSevereLog(String str) {
    logger.severe(str);
}

/* end of writing logs */


/* event handlers */

// dummy
public void handleButtonEvents(GImageButton button, GEvent event) {}

public void mousePressed() {
    try {
        myPageCollection.handleMousePressed();
    } catch (Exception ex) {
        handleGeneralException(ex);
    }
}

// https://forum.processing.org/two/discussion/579/run-code-on-exit-follow-up
public void exit() {
    // put code to run on exit here
    logger.info("Photo booth finished.");
    super.exit();
}

/* end of event handlers */