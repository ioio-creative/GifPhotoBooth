public abstract class PageBase {
    private PhotoBooth photoBooth;
    private boolean isIni;
    private UiImage bgImg;
    private Language lang;
    private PageId pageID;
    private PageCollection hostPageCollection;
    private UiAbstractButton[] buttons;

    public PageBase(PageId anID, PageCollection aCollection, PhotoBooth aPhotoBooth, Language aLang) {
        this.pageID = anID;
        this.hostPageCollection = aCollection;
        this.photoBooth = aPhotoBooth;
        this.isIni = false;
        this.lang = aLang;        
    }    

    public PageId getPageID() {
        return this.pageID;
    }

    public PageCollection getHostPageCollection() {
        return this.hostPageCollection;
    }

    public PhotoBooth getPhotoBooth() {
        return this.photoBooth;
    }    

    public boolean getIsIni() {
        return this.isIni;
    }

    public void setIsIni(boolean isIni) {
        this.isIni = isIni;
    }

    public Language getLang() {
        return this.lang;
    }

    public void setLang(Language aLang) {
        this.lang = aLang;
    }

    public void setBgImg(String imageUrl) {
        this.bgImg = new UiImage(this.photoBooth, imageUrl);
    }

    public UiImage getBgImg() {
        return this.bgImg;
    }

    public void setPhotoBoothBgImg() {
        this.photoBooth.setBackgroundImage(this.getBgImg());
    }

    public void setNextPage(PageId pID) throws Exception {
        this.setPageButtonsVisible(false);
        this.setIsIni(false);
        this.hostPageCollection.setActivePageID(pID);        
    }

    /* button helpers */

    public void setPageButtons(UiAbstractButton[] btns) {
        this.buttons = btns;
    }

    public void disposePageButtons() {
        if (this.buttons != null) {  // some pages may not have any buttons
            for (UiAbstractButton button : this.buttons) {
                button.disposeButtonSafe();
            }
        }
    }

    public void setPageButtonsVisible(boolean isVisible) {
        if (this.buttons != null) {  // some pages may not have any buttons
            for (UiAbstractButton button : this.buttons) {                
                button.setVisible(isVisible);
            }
        }
    }

    /* end of button helpers */

    public void handlePageMousePressed() throws Exception {}
    
    public abstract void updatePage() throws Exception;   

    /* writing logs */

    public void writeInfoLog(String str) {
        this.photoBooth.writeInfoLog(str);
    }

    public void writeWarningLog(String str) {
        this.photoBooth.writeWarningLog(str);
    }

    public void writeSevereLog(String str) {
        this.photoBooth.writeSevereLog(str);
    }

    /* end of writing logs */

    public void handleGeneralException(Exception ex) {
        this.photoBooth.handleGeneralException(ex);
    }
}