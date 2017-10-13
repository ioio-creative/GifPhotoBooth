import g4p_controls.GImageButton;
import g4p_controls.GEvent;

public class PageInstruction extends PageBase {
    private UiImageButton btnTakePhoto;

    public PageInstruction(PageId aPageId, PageCollection aPageCollection, PhotoBooth aPhotoBooth, Language aLang) {
        super(aPageId, aPageCollection, aPhotoBooth, aLang);
        
        String bgImgUrl = aPhotoBooth.getBackgroundImageFolderPath() + "photoBooth_ui_render-02.png";
        this.setBgImg(bgImgUrl);

        String[] btnImgUrl = { aPhotoBooth.getButtonImageFolderPath() + "photoBooth_ui_render-08.png" };
        this.btnTakePhoto = new UiImageButton(this,
            aPhotoBooth.DEFAULT_SINGLE_BTN_X, aPhotoBooth.DEFAULT_BTN_Y,
            aPhotoBooth.DEFAULT_SINGLE_BTN_WIDTH, aPhotoBooth.DEFAULT_SINGLE_BTN_HEIGHT,
            btnImgUrl, "handleBtnTakePhotoEvent");   
        this.setPageButtons(new UiAbstractButton[] { this.btnTakePhoto });
    }

    /* event handlers */

    public void handleBtnTakePhotoEvent(GImageButton source, GEvent event) {
        try {                        
            this.setNextPage(PageId.TakePhoto);
            this.writeInfoLog("Page Instruction btnTakePhoto clicked.");
        } catch (Exception ex) {
            this.handleGeneralException(ex);
        }
    }

    /* end of event handlers */

    @Override
    public void updatePage() throws Exception {
        if (this.getIsIni() == false) {
            this.setPhotoBoothBgImg();
            this.setPageButtonsVisible(true);

            // PhotoBooth myPhotoBooth = this.getPhotoBooth();
            // String s = "1. 按下拍照" + PhotoBooth.LINE_BREAK +
            //     "2. 開始倒數" + PhotoBooth.LINE_BREAK +
            //     "3. 拍下６張照片，製成你的創意動態照" + PhotoBooth.LINE_BREAK +
            //     "4. 列印照片，留住美好回憶！";
            // myPhotoBooth.fill(50);
            // myPhotoBooth.text(s, 0, 0, myPhotoBooth.width, myPhotoBooth.height);  // Text wraps within text box

            this.setIsIni(true);
            this.writeInfoLog("Page Instruction setup finished.");
        }
    }
}