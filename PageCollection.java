public class PageCollection {
    private PhotoBooth photoBooth;
    private Language lang;
    private PageBase[] pages;
    private PageId activePageID;

    public PageCollection(PhotoBooth aPhotoBooth, Language aLang) {
        this.photoBooth = aPhotoBooth;
        this.lang = aLang;        
        this.setupPages();
    }

    private void setupPages() {
        pages = new PageBase[] {
            new PageStart(PageId.Start, this, this.photoBooth, this.lang),
            new PageInstruction(PageId.Instruction, this, this.photoBooth, this.lang),
            new PageTakePhoto(PageId.TakePhoto, this, this.photoBooth, this.lang),
            new PageShowGif(PageId.ShowGif, this, this.photoBooth, this.lang),
            new PagePreparePrint(PageId.PreparePrint, this, this.photoBooth, this.lang),
            new PagePrint(PageId.Print, this, this.photoBooth, this.lang)
        };
    }

    public void setActivePageID(PageId pID) {
        this.activePageID = pID;        
    }

    public PageId getActivePageID() {
        return this.activePageID;
    }

    private PageBase getPageById(PageId anID) throws Exception {
        for (PageBase page : this.pages) {
            if (page.getPageID() == anID) {
                return page;
            }
        }

        throw new Exception("No page associated with the ID '" + anID + "' found.");
    }

    public PageBase getActivePage() throws Exception {
        return this.getPageById(this.activePageID);
    }

    public void resetAllIsInis() {
        for (PageBase page : this.pages) {
            page.setIsIni(false);
        }
    }

    public void disposeAllPageButtons() {
        for (PageBase page : this.pages) {
            page.disposePageButtons();
        }
    }

    public void setAllPageButtonsVisible(boolean isVisible) {
        for (PageBase page : this.pages) {
            page.setPageButtonsVisible(isVisible);
        }
    }

    public void setAllPageLanguage(Language aLang) {
        for (PageBase page : this.pages) {
            page.setLang(aLang);
        }
    }

    public void updateActivePage() throws Exception {
        this.getActivePage().updatePage();
    }

    public void handleMousePressed() throws Exception {
        this.getActivePage().handlePageMousePressed();
    }
}