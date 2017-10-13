import g4p_controls.GButton;
import g4p_controls.GEvent;

import gifAnimation.GifMaker;

import processing.core.PImage;

public class PagePrint extends PageBase {
    private final boolean IS_USE_TRANSPARENT_TINT_IN_GIF_GRID = false;
    private final boolean IS_USE_RAND_FILTER_IN_GIF_GRID = true;
    private final int NUM_OF_FRAMES_IN_GIF = 30;
    private final int NUM_OF_PHOTOS_TO_LOAD = PageTakePhoto.NUM_OF_PHOTOS_TO_TAKE;
    private final int NUM_OF_PRINT_TRIALS = 3;

    private GifMaker gifExport;  // this page is responsible for outputing the gif file as well
    private String gifExportFileFullPath;
    private int drawFrameCounter;
    private int numOfFramesAddedToGifExport;
    private UiImage[] camPics;
    private UiImage[] loadingTextImages = new UiImage[18];
    private boolean isPhotoPrinted;    
    private boolean isGifExportFinished;
    private int numOfPrintTrialsLeft;

    public PagePrint(PageId aPageId, PageCollection aPageCollection, PhotoBooth aPhotoBooth, Language aLang) {
        super(aPageId, aPageCollection, aPhotoBooth, aLang);
        
        String bgImgUrl = aPhotoBooth.getBackgroundImageFolderPath() + "photoBooth_ui_render-05.png";
        this.setBgImg(bgImgUrl);
        
        for (int i = 0; i < loadingTextImages.length; i++) {
            loadingTextImages[i] = new UiImage(aPhotoBooth,
                aPhotoBooth.getLoadingImageFolderPath() + "loading_" + aPhotoBooth.nf(i, 5) + ".png");
        }
    }

    /* event handlers */

    
    /* end of event handlers */

    @Override
    public void updatePage() throws Exception {
        PhotoBooth myPhotoBooth = this.getPhotoBooth();

        if (this.getIsIni() == false) {
            this.setPhotoBoothBgImg();
            this.setPageButtonsVisible(true);
            this.camPics = myPhotoBooth.loadCameraPicturesFromTempFolder(this.NUM_OF_PHOTOS_TO_LOAD);
            this.drawFrameCounter = 0;
            this.numOfFramesAddedToGifExport = 0;
            this.isPhotoPrinted = false;
            this.isGifExportFinished = false;
            this.numOfPrintTrialsLeft = this.NUM_OF_PRINT_TRIALS;            
            this.setIsIni(true);        
            this.writeInfoLog("Page Print setup finished.");
            
            // set up gif export
            this.setUpGifExport(myPhotoBooth);
        }

        // change gif grid row and column
        int changeGridSizePeriod = 50;
        if (this.drawFrameCounter % changeGridSizePeriod == 0) {
            myPhotoBooth.setGifGridRowAndColumnByCounter();
        }             

        // change cam pics in gif grid
        int changeCamPicPeriod = 10;
        if (this.drawFrameCounter % changeCamPicPeriod == 0) {
            this.addScreenCapToGifExport(myPhotoBooth);
            this.numOfFramesAddedToGifExport++;

            // if gif contains sufficient frames
            if (this.numOfFramesAddedToGifExport == this.NUM_OF_FRAMES_IN_GIF) {
                this.finishMakingGifExport();
                this.isGifExportFinished = true;
                this.writeInfoLog("Page Print gif export finished.");
            }

            myPhotoBooth.showImagesInGifGrid(camPics, this.IS_USE_TRANSPARENT_TINT_IN_GIF_GRID,
                this.IS_USE_RAND_FILTER_IN_GIF_GRID, false);
        }

        if (this.isKeepOnAttemptingPrint()) {
            this.isPhotoPrinted = myPhotoBooth.printPhoto(myPhotoBooth.getNameOfFileToPrint());
            if (this.isPhotoPrinted) {
                this.writeInfoLog("Page Print printing photo finished");
            } else {
                this.writeWarningLog("Page Print printing photo failed." + myPhotoBooth.LINE_BREAK +
                    "Number of print trials left: " + (this.numOfPrintTrialsLeft - 1));
            }
            this.numOfPrintTrialsLeft--;
        }        

        if (this.isKeepOnAttemptingPrint() == false && this.isGifExportFinished) {
            this.setNextPage(PageId.Start);
        }

        // show loading text
        this.showLoadingTextByCounter(myPhotoBooth);

        // increment counter
        this.drawFrameCounter++;
    }


    /* gif helpers */

    private void setUpGifExport(PhotoBooth photoBooth) {
        this.gifExportFileFullPath = photoBooth.generateOutputGifFileFullPath();
        this.gifExport = new GifMaker(photoBooth, gifExportFileFullPath);
        this.gifExport.setRepeat(0);
        this.gifExport.setDelay(1000 / 8);
        this.gifExport.setQuality(10);    
    }

    private void addScreenCapToGifExport(PhotoBooth photoBooth) throws Exception {
        UiImage fakeScreenCap = photoBooth.produceScreenCapForGifExport(this.camPics);
        fakeScreenCap.resize(360, 640);
        this.gifExport.addFrame(fakeScreenCap.getPImage());
    }

    private void finishMakingGifExport() {
        this.gifExport.finish();
    }

    /* end of gif helpers */


    private void showLoadingTextByCounter(PhotoBooth photoBooth) {
        this.loadingTextImages[this.drawFrameCounter % this.loadingTextImages.length]
            .showImageInApplet(0.269f * 1.3f * photoBooth.width,
                               0.35f * 1.1f * photoBooth.height,
                               0.463f * 0.6f * photoBooth.width,
                               0.167f * 0.6f * photoBooth.height);
    }

    private boolean isKeepOnAttemptingPrint() {
        return !this.isPhotoPrinted && this.numOfPrintTrialsLeft > 0;
    }    
}