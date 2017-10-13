import g4p_controls.GImageButton;

public class UiImageButton implements UiAbstractButton {
    private PageBase hostPage;    
    private float coorX;
    private float coorY;
    private float width;
    private float height;

    private GImageButton gBtn;  
    private String[] imgUrls;

    public UiImageButton(PageBase anHostPage, float coor_x, float coor_y,
        float aWidth, float aHeight, String[] someImgUrls, String eventHandlerNameInHostPage) {
        
        this.hostPage = anHostPage;
        this.coorX = coor_x;
        this.coorY = coor_y;
        this.width = aWidth;
        this.height = aHeight;
        this.imgUrls = someImgUrls;

        this.gBtn = new GImageButton(anHostPage.getPhotoBooth(), coor_x, coor_y,
            width, height, imgUrls);        
        this.gBtn.addEventHandler(anHostPage, eventHandlerNameInHostPage);
    }

    public void setVisible(boolean isVisible) {
        this.gBtn.setVisible(isVisible);
    }

    public void disposeButtonSafe() {
        if (this.gBtn != null) {
            this.gBtn.dispose();            
        }        
    }
}