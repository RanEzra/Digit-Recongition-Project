

public class Image {
    private int[] pixels;
    private int label;

    public Image(int[] data,int label) {
        this.pixels=data.clone();
        this.label=label;
    }
    public int getPixel(int pixel) {
        return this.pixels[pixel];
    }
    public int getLabel() {
        return this.label;
    }
    public void printImage() {
        System.out.println("label: "+label);
        for(int i=0;i<pixels.length;i++)
            System.out.print(pixels[i]);
    }

}
