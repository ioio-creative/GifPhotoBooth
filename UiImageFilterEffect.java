import processing.core.PApplet;

public enum UiImageFilterEffect {
    THRESHOLD,
    GRAY,
    INVERT,
    POSTERIZE,
    BLUR,
    ERODE,
    DILATE;

    private static final int size = UiImageFilterEffect.values().length;

    public static UiImageFilterEffect getRandom(PApplet applet) {
        UiImageFilterEffect effectToReturn = POSTERIZE;
        int rand = (int)applet.random(UiImageFilterEffect.size);
        for (UiImageFilterEffect effect : UiImageFilterEffect.values()) {
            if (effect.ordinal() == rand) {
                effectToReturn = effect;
                break;
            }
        }        
        return effectToReturn;
    }
}