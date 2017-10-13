import g4p_controls.GImageButton;
import g4p_controls.GEvent;

public class PageShowGif extends PageBase {    
    public final int NUM_OF_PHOTOS_TO_LOAD = PageTakePhoto.NUM_OF_PHOTOS_TO_TAKE;
    private final boolean IS_USE_TRANSPARENT_TINT_IN_GIF_GRID = true;
    private final boolean IS_USE_RAND_FILTER_IN_GIF_GRID = false;
    private UiImage[] camPics;

    private UiImageButton btnPreparePrint;
    private UiImageButton btnRestart;
    
    private int drawFrameCounter;      

    public PageShowGif(PageId aPageId, PageCollection aPageCollection, PhotoBooth aPhotoBooth, Language aLang) {
        super(aPageId, aPageCollection, aPhotoBooth, aLang);
        
        String bgImgUrl = aPhotoBooth.getBackgroundImageFolderPath() + "photoBooth_ui_render-04.png";
        this.setBgImg(bgImgUrl);

        String[] btnRestartImgUrl = { aPhotoBooth.getButtonImageFolderPath() + "photoBooth_ui_render-09.png" };
        this.btnRestart = new UiImageButton(this,
            aPhotoBooth.DEFAULT_DOUBLE_BTN_1_X, aPhotoBooth.DEFAULT_BTN_Y,
            aPhotoBooth.DEFAULT_DOUBLE_BTN_WIDTH, aPhotoBooth.DEFAULT_DOUBLE_BTN_HEIGHT,
            btnRestartImgUrl, "handleBtnRestartEvent");

        String[] btnPreparePrintImgUrl = { aPhotoBooth.getButtonImageFolderPath() + "photoBooth_ui_render-10.png" };
        this.btnPreparePrint = new UiImageButton(this,
            aPhotoBooth.DEFAULT_DOUBLE_BTN_2_X, aPhotoBooth.DEFAULT_BTN_Y,
            aPhotoBooth.DEFAULT_DOUBLE_BTN_WIDTH, aPhotoBooth.DEFAULT_DOUBLE_BTN_HEIGHT,
            btnPreparePrintImgUrl, "handleBtnPreparePrintEvent");
        this.setPageButtons(new UiAbstractButton[] { this.btnRestart, this.btnPreparePrint });
    }

    /* event handlers */

    public void handleBtnRestartEvent(GImageButton source, GEvent event) {
        try {
            this.setNextPage(PageId.Start);
            this.writeInfoLog("Page ShowGif btnRestart clicked.");
        } catch (Exception ex) {
            this.handleGeneralException(ex);
        }
    }

    public void handleBtnPreparePrintEvent(GImageButton source, GEvent event) {
        try {
            this.setNextPage(PageId.PreparePrint);
            this.writeInfoLog("Page ShowGif btnPreparePrint clicked.");
        } catch (Exception ex) {
            this.handleGeneralException(ex);
        }
    }

    /* end of event handlers */

    @Override
    public void updatePage() throws Exception {
        PhotoBooth myPhotoBooth = this.getPhotoBooth();

        if (this.getIsIni() == false) {
            this.setPhotoBoothBgImg();
            this.setPageButtonsVisible(true);
            this.camPics = myPhotoBooth.loadCameraPicturesFromTempFolder(this.NUM_OF_PHOTOS_TO_LOAD);        
            this.drawFrameCounter = 0;
            this.setIsIni(true);
            this.writeInfoLog("Page ShowGif setup finished.");
        }

        // change gif grid row and column
        int changeGridSizePeriod = 50;
        if (this.drawFrameCounter % changeGridSizePeriod == 0) {
            myPhotoBooth.setGifGridRowAndColumnByCounter();
        }

        // change cam pics in gif grid
        int changeCamPicPeriod = 10;
        if (this.drawFrameCounter % changeCamPicPeriod == 0) {
            myPhotoBooth.showImagesInGifGrid(this.camPics, this.IS_USE_TRANSPARENT_TINT_IN_GIF_GRID,
                this.IS_USE_RAND_FILTER_IN_GIF_GRID, false);
        }        

        // increment counter        
        this.drawFrameCounter++;
    }
}