import g4p_controls.GButton;

public class UiTextButton implements UiAbstractButton {
    private PageBase hostPage;    
    private float coorX;
    private float coorY;
    private float width;
    private float height;

    private GButton gBtn;  
    private String btnText;    

    public UiTextButton(PageBase anHostPage, float coor_x, float coor_y,
        float aWidth, float aHeight, String aText, String eventHandlerNameInHostPage) {
        
        this.hostPage = anHostPage;
        this.coorX = coor_x;
        this.coorY = coor_y;
        this.width = aWidth;
        this.height = aHeight;
        this.btnText = aText;

        this.gBtn = new GButton(anHostPage.getPhotoBooth(), coorX, coorY,
            aWidth, aHeight, aText);
        this.gBtn.addEventHandler(anHostPage, eventHandlerNameInHostPage);
    }

    public void setVisible(boolean isVisible) {
        gBtn.setVisible(isVisible);
    }

    public void disposeButtonSafe() {
        if (gBtn != null) {
            gBtn.dispose();
        }
    }
}