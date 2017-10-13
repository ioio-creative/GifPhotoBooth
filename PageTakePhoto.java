public class PageTakePhoto extends PageBase {
    public static final int NUM_OF_PHOTOS_TO_TAKE = 6;
    private final int NUM_OF_COUNT_DOWN_TO_MAKE = 6;    
    
    private int numOfPhotosTaken;
    private int countDown;

    private SimpleTimer secTickTimer = new SimpleTimer(1000);  // ticks every second
    
    private boolean isWhiteFlashShownInLastFrame;
    private SimpleTimer whiteFlashLastingTimer = new SimpleTimer(100);  // ticks every 0.1s, makes white flash last for some time

    public PageTakePhoto(PageId aPageId, PageCollection aPageCollection, PhotoBooth aPhotoBooth, Language aLang) {
        super(aPageId, aPageCollection, aPhotoBooth, aLang);
        
        String bgImgUrl = aPhotoBooth.getBackgroundImageFolderPath() + "photoBooth_ui_render-03.png";
        this.setBgImg(bgImgUrl);        
    }

    @Override
    public void updatePage() throws Exception {            
        if (this.getIsIni() == false) {
            this.setPhotoBoothBgImg();
            this.numOfPhotosTaken = 0;
            this.countDown = this.NUM_OF_COUNT_DOWN_TO_MAKE;
            //this.isWhiteFlashShownInLastFrame = false;
            this.secTickTimer.reset();  
            this.setIsIni(true);            
            this.writeInfoLog("Page TakePhoto setup finished.");
        }

        // switch-to-next-page condition
        if (this.numOfPhotosTaken == this.NUM_OF_PHOTOS_TO_TAKE) {
            this.setNextPage(PageId.ShowGif);
            return;
        }

        PhotoBooth myPhotoBooth = this.getPhotoBooth();

        boolean isSecTickTimerTicked = this.secTickTimer.check();
        boolean isInCountDownPhase = this.countDown > 0;

        myPhotoBooth.setCameraPicture();

        if (isInCountDownPhase) {  // count-down phase            
            if (isSecTickTimerTicked) {  // skip one frame of showing count down text when count down text changes
                this.countDown--;

                // take first photo when count down to 0
                if (this.countDown == 0) {
                    this.showWhiteFlashAndTakePhoto(myPhotoBooth);
                }
            } else {                
                this.drawCountDownText(myPhotoBooth);  // draw count down text on top of camera picture
            }
        } else {  // take-photo phase
            if (isSecTickTimerTicked) {         
                this.showWhiteFlashAndTakePhoto(myPhotoBooth);
            }
        }

        // make white flash last for some time
        if (this.isWhiteFlashShownInLastFrame) {
            if (this.whiteFlashLastingTimer.check() == false) {  // timer not ticked yet
                this.showWhiteFlash(myPhotoBooth);
            } else {
                myPhotoBooth.writeInfoLog("Page Take Photo white flash faded.");
                this.isWhiteFlashShownInLastFrame = false;
            }
        }
    }

    /* draw UIs */

    private void drawCountDownText(PhotoBooth myPhotoBooth) {        
        myPhotoBooth.textSize(360f * myPhotoBooth.width / 540);  // to cater for different screen size
        //myPhotoBooth.fill(255);
        //myPhotoBooth.fill(155, 111, 59);
        myPhotoBooth.fill(229, 194, 23);        
        myPhotoBooth.text(this.countDown,
            myPhotoBooth.width * 0.5f, myPhotoBooth.height * 0.4f);
    }

    private void showWhiteFlash(PhotoBooth myPhotoBooth) {
        myPhotoBooth.fill(255);
        myPhotoBooth.rect(myPhotoBooth.CAM_PIC_SHOW_X, myPhotoBooth.CAM_PIC_SHOW_Y,
            myPhotoBooth.CAM_PIC_SHOW_WIDTH, myPhotoBooth.CAM_PIC_SHOW_HEIGHT);

        // make white flash last for some time
        this.isWhiteFlashShownInLastFrame = true;        
    }

    private void showWhiteFlashAndTakePhoto(PhotoBooth myPhotoBooth) {
        this.showWhiteFlash(myPhotoBooth);
        myPhotoBooth.saveCameraPictureToTempFolder(this.numOfPhotosTaken++);

        // make white flash last for some time
        this.whiteFlashLastingTimer.reset();
    }
    
    /* end of draw UIs */
}