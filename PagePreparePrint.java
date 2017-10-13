import g4p_controls.GImageButton;
import g4p_controls.GEvent;

import processing.core.PApplet;

public class PagePreparePrint extends PageBase {
    private final int NUM_OF_PHOTOS_TO_LOAD = PageTakePhoto.NUM_OF_PHOTOS_TO_TAKE;

    private UiImageButton btnRestart;

    // https://processing.org/examples/button.html
    // Use PImage instead of GImageButton to implement image buttons
    // because the images of the buttons have to be changed
    // and it seems that GImageButton does not support this use case very well
    
    private float printBtnWidth, printBtnHeight;

    private float btnPrintWithoutColorFilter_x, btnPrintWithoutColorFilter_y;
    private UiImage btnPrintWithoutColorFilter;
    
    private float btnPrintWithColorFilter_x, btnPrintWithColorFilter_y;     
    private UiImage btnPrintWithColorFilter;

    private boolean isMouseOverBtnPrintWithoutColorFilter;
    private boolean isMouseOverBtnPrintWithColorFilter;


    public PagePreparePrint(PageId aPageId, PageCollection aPageCollection, PhotoBooth aPhotoBooth, Language aLang) {
        super(aPageId, aPageCollection, aPhotoBooth, aLang);

        String bgImgUrl = aPhotoBooth.getBackgroundImageFolderPath() + "photoBooth_ui_render-04.png";
        this.setBgImg(bgImgUrl);

        String[] btnRestartImgUrl = { aPhotoBooth.getButtonImageFolderPath() + "photoBooth_ui_render-09.png" };
        this.btnRestart = new UiImageButton(this,
            aPhotoBooth.DEFAULT_SINGLE_BTN_X, aPhotoBooth.DEFAULT_BTN_Y,
            aPhotoBooth.DEFAULT_SINGLE_BTN_WIDTH, aPhotoBooth.DEFAULT_SINGLE_BTN_HEIGHT,
                btnRestartImgUrl, "handleBtnRestartEvent");

        // Note btnPrintWithoutColorFilter & btnPrintWithColorFilter are initialized
        // at the first call of updatePage() every time this page is visited
        this.printBtnWidth = aPhotoBooth.width * 0.4f;
        this.printBtnHeight = this.printBtnWidth;

        this.btnPrintWithoutColorFilter_x = aPhotoBooth.width * 0.056f;
        this.btnPrintWithoutColorFilter_y = aPhotoBooth.height * 0.45f;

        this.btnPrintWithColorFilter_x = aPhotoBooth.width * 0.55f;
        this.btnPrintWithColorFilter_y = aPhotoBooth.height * 0.2f;

        this.setPageButtons(new UiAbstractButton[] { this.btnRestart });
    }

    /* event handlers */

    private void handleBtnPrintWithoutColorFilterEvent() {
        try {
            PhotoBooth myPhotoBooth = this.getPhotoBooth();
            myPhotoBooth.setNameOfFileToPrint(myPhotoBooth.PRINT_OUT_FILE_NAMES[0]);
            this.setNextPage(PageId.Print);
            this.disposePageButtons();
            this.writeInfoLog("Page PreparePrint btnPrintWithoutColorFilter clicked.");
        } catch (Exception ex) {
            this.handleGeneralException(ex);
        }
    }

    private void handleBtnPrintWithColorFilterEvent() {
        try {
            PhotoBooth myPhotoBooth = this.getPhotoBooth();
            myPhotoBooth.setNameOfFileToPrint(myPhotoBooth.PRINT_OUT_FILE_NAMES[1]);
            this.disposePageButtons();
            this.setNextPage(PageId.Print);
            this.writeInfoLog("Page PreparePrint btnPrintWithColorFilter clicked.");
        } catch (Exception ex) {
            this.handleGeneralException(ex);
        }
    }

    public void handleBtnRestartEvent(GImageButton source, GEvent event) {
        try {
            this.setNextPage(PageId.Start);
            this.writeInfoLog("Page PreparePrint btnRestart clicked.");
        } catch (Exception ex) {
            this.handleGeneralException(ex);
        }
    }

    public void handlePageMousePressed() {
        if (this.isMouseOverBtnPrintWithoutColorFilter) {
            this.handleBtnPrintWithoutColorFilterEvent();
        } else if (this.isMouseOverBtnPrintWithColorFilter) {
            this.handleBtnPrintWithColorFilterEvent();
        }
    }

    /* end of event handlers */

    @Override
    public void updatePage() throws Exception {    
        if (this.getIsIni() == false) {
            PhotoBooth myPhotoBooth = this.getPhotoBooth();

            // Save print-out files
            this.prepareImagesForButtons(myPhotoBooth);

            // Set page background
            this.setPhotoBoothBgImg();

            // Set images of btnPrintWithoutColorFilter & btnPrintWithColorFilter from print-out files
            this.setupPrintButtons(myPhotoBooth);

            this.isMouseOverBtnPrintWithoutColorFilter = false;
            this.isMouseOverBtnPrintWithColorFilter = false;

            this.setPageButtons(new UiAbstractButton[] { this.btnRestart });
            this.setPageButtonsVisible(true);
            this.setIsIni(true);
            this.writeInfoLog("Page PreparePrint setup finished");
        }

        this.checkMouseOverPrintBtns();
    }

    private void prepareImagesForButtons(PhotoBooth myPhotoBooth) throws Exception {
        UiImage[] camPics = myPhotoBooth.loadCameraPicturesFromTempFolder(this.NUM_OF_PHOTOS_TO_LOAD);
        myPhotoBooth.savePhotoToPrint(camPics, false, false,
            myPhotoBooth.PRINT_GRID_OUT_FILE_NAMES[0], myPhotoBooth.PRINT_OUT_FILE_NAMES[0]);
        myPhotoBooth.savePhotoToPrint(camPics, true, false,
            myPhotoBooth.PRINT_GRID_OUT_FILE_NAMES[1], myPhotoBooth.PRINT_OUT_FILE_NAMES[1]);   
    }

    private void setupPrintButtons(PhotoBooth myPhotoBooth) {
        String btnPrintWithoutColorFilterImgUrl =
            myPhotoBooth.getPrintResultFileFullPath(myPhotoBooth.PRINT_GRID_OUT_FILE_NAMES[0]);
        this.btnPrintWithoutColorFilter = new UiImage(myPhotoBooth, btnPrintWithoutColorFilterImgUrl);
        this.btnPrintWithoutColorFilter
            .showImageInApplet(this.btnPrintWithoutColorFilter_x, this.btnPrintWithoutColorFilter_y,
                this.printBtnWidth, this.printBtnHeight);

        String btnPrintWithColorFilterImgUrl =
            myPhotoBooth.getPrintResultFileFullPath(myPhotoBooth.PRINT_GRID_OUT_FILE_NAMES[1]);
        this.btnPrintWithColorFilter = new UiImage(myPhotoBooth, btnPrintWithColorFilterImgUrl);
        this.btnPrintWithColorFilter
            .showImageInApplet(this.btnPrintWithColorFilter_x, this.btnPrintWithColorFilter_y,
                this.printBtnWidth, this.printBtnHeight);
    }


    /* check mouse overs */

    // https://processing.org/examples/button.html
    private void checkMouseOverPrintBtns() {
        isMouseOverBtnPrintWithoutColorFilter = btnPrintWithoutColorFilter.overRect((int)btnPrintWithoutColorFilter_x, (int)btnPrintWithoutColorFilter_y);
        isMouseOverBtnPrintWithColorFilter = btnPrintWithColorFilter.overRect((int)btnPrintWithColorFilter_x, (int)btnPrintWithColorFilter_y);
    }

    /* end of check mouse overs */
}