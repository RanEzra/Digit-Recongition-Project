

public class Image {
    private int[][] pixels;
    private int label;

    public Image(int[] data,int label) {
        this.pixels=new int[28][28];
        for(int i=0;i<28;i++)
            for(int j=0;j<28;j++)
                this.pixels[i][j]=data[(28*i)+j];
        this.label=label;
    }
    public int getPixel(int pixel) {
        return this.pixels[pixel/28][pixel%28];
    }
    public int getPixel_ran(int i,int j) {
        return this.pixels[i][j];
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
