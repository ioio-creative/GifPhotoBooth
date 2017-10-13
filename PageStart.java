import g4p_controls.GImageButton;
import g4p_controls.GEvent;

public class PageStart extends PageBase {
    private UiImageButton btnStart;    
        
    public PageStart(PageId aPageId, PageCollection aPageCollection, PhotoBooth aPhotoBooth, Language aLang) {
        super(aPageId, aPageCollection, aPhotoBooth, aLang);
        
        String bgImgUrl = aPhotoBooth.getBackgroundImageFolderPath() + "photoBooth_ui_render-01.png";
        this.setBgImg(bgImgUrl);        
        
        String[] btnImgUrl = { aPhotoBooth.getButtonImageFolderPath() + "photoBooth_ui_render_revised-07.png" };
        this.btnStart = new UiImageButton(this,
            aPhotoBooth.DEFAULT_SINGLE_BTN_X, aPhotoBooth.DEFAULT_BTN_Y,
            aPhotoBooth.DEFAULT_SINGLE_BTN_WIDTH, aPhotoBooth.DEFAULT_SINGLE_BTN_HEIGHT,
            btnImgUrl, "handleBtnStartEvent");
        this.setPageButtons(new UiAbstractButton[] { this.btnStart });
    }

    /* event handlers */

    public void handleBtnStartEvent(GImageButton source, GEvent event) {
        try {
            this.setNextPage(PageId.Instruction);
            this.writeInfoLog("Page Start btnStart clicked.");
        } catch (Exception ex) {
            this.handleGeneralException(ex);
        }
    }

    /* end of event handlers */

    @Override
    public void updatePage() throws Exception {
        if (this.getIsIni() == false) {          
          this.getHostPageCollection().resetAllIsInis();        

          this.setPhotoBoothBgImg();
          this.setPageButtonsVisible(true);

          this.setIsIni(true);
          this.writeInfoLog("Page 0 setup finished.");
        }
    }
}