
import javax.swing.text.html.HTMLDocument;
import java.util.Iterator;
import java.util.LinkedList;

public class Node {
    private int data; //leaf- the number its represent. notLeaf- the pixel condition.
    private Image[] allimages;
    private Node left;
    private Node right;
    private LinkedList<Integer> images;
    private LinkedList<Integer> c;
    private double ig;
    private int best_condition;
    private LinkedList<Integer> la;
    private LinkedList<Integer> lb;


    public Node(int data,LinkedList<Integer> images,LinkedList<Integer> c,Image[] allimages) {
        this.data=data;
        this.left=null;
        this.right=null;
        this.images=images;
        this.c=c;
        this.allimages=allimages;
        this.calculateIG(images,c);
    }
    public Node(int data,Node left,Node right) {
        this.data=data;
        this.left=left;
        this.right=right;
    }

    private void calculateIG(LinkedList<Integer> images, LinkedList<Integer> c) {
        //int[] allNumbers = new int[10];
        int NL=images.size();
        double HL=calculateHL(images,NL);
        //HERE WE HAVE THE HL !!!
        LinkedList<Integer> La=new LinkedList<>();//who dont stands in the condition
        LinkedList<Integer> Lb=new LinkedList<>();//who stands in the condition
        LinkedList<Integer> Best_La=new LinkedList<>();
        LinkedList<Integer> Best_Lb=new LinkedList<>();

        int Nla=0;
        int Nlb=0;
        double Hla=0;
        double Hlb=0;
        double minHX=-1;
        double curHX=0;
        int minX=0;
        Image curImage;
        int curPixel=0;
        int i=-1;
        int curPixelImage=0;
        Iterator itC=c.iterator();
        while (itC.hasNext()){
            i++;
            curPixel= (int) itC.next();
            La.clear();
            Lb.clear();
            Iterator it=images.iterator();
            while(it.hasNext()){
                int curIndex=(int)it.next();
                curImage=allimages[curIndex];
                curPixelImage=curImage.getPixel(curPixel);
                if(curPixelImage>128)
                    Lb.add(curIndex);
                else
                    La.add(curIndex);
            }
            Nla=La.size();
            Nlb=Lb.size();
            Hla=calculateHL(La,Nla);
            Hlb=calculateHL(Lb,Nlb);
            curHX=(((Nla*Hla)/NL)+((Nlb*Hlb)/NL));
            if((curHX < minHX)||(minHX==-1)) {
                minHX = curHX;
                minX=i;
                Best_La= (LinkedList<Integer>) La.clone();
                Best_Lb=(LinkedList<Integer>) Lb.clone();
            }
        }
        this.setIG((HL-minHX)*NL);
        this.setBest_condition(minX);
        this.setLa(Best_La);
        this.setLb(Best_Lb);
    }
    private double calculateHL(LinkedList<Integer> images,int NL){
        int[] allNumbers = new int[10];
        for (int i = 0; i < 10; i++)//init the array
            allNumbers[i] = 0;
        Iterator it=images.iterator();
        while (it.hasNext())//sum the mofaim of i
            allNumbers[allimages[(int)it.next()].getLabel()]++;
        double HL = 0;
        double log=0;
        double curAdd=0;
        for (int i = 0; i < 10; i++) {//calc the HL
            if(allNumbers[i] !=0){
                log=Math.log10(NL/allNumbers[i]);
                curAdd=(log*(allNumbers[i])/NL);
                HL=HL+curAdd;
            }
        }
        return HL;
    }
    //Getters
    public int getData(){
        return data;
    }
    public int getBest_condition(){
        return best_condition;
    }
    public Node getLeft(){
        return left;
    }
    public Node getRight(){
        return right;
    }
    public LinkedList<Integer> getImages(){
        return images;
    }
    public LinkedList<Integer> getC(){
        return c;
    }
    public double getIG(){
        return ig;
    }
    public LinkedList<Integer> getLa(){
        return la;
    }
    public LinkedList<Integer> getLb(){
        return lb;
    }
    public int sizeImages(){
        return images.size();
    }

    //Setters
    public void setData(int new_data){
        this.data=new_data;
    }
    public void setLeft(Node left){
        this.left=left;
    }
    public void setRight(Node right){
        this.right=right;
    }
    public void setImages(LinkedList images){
        this.images=images;
    }
    public void setIG(double ig){
        this.ig=ig;
    }
    public void setBest_condition(int cond){
        this.best_condition=cond;
    }
    public void setLa(LinkedList<Integer> la){
        this.la=la;
    }
    public void setLb(LinkedList<Integer> lb){
        this.lb=lb;
    }
    public boolean isLeaf(){
        return ((left==null)&&(right==null));
    }
    public void printNode(){
        if(this.isLeaf())
            System.out.print("LEAF: "+data+" ");
        else
            System.out.print("COND: " +data+" ");
        if(left!=null)
            left.printNode();
        if(right!=null)
            right.printNode();
    }
    public String NodeToString(){
        if(this.isLeaf())
            return "("+String.valueOf(data)+")";
        return ("("+String.valueOf(data)+","+this.left.NodeToString()+","+this.right.NodeToString()+")");
    }

    public Node NodeFromString(String str){
        str=str.substring(1,str.length());
        int next_open=str.indexOf('(');
        int next_close=str.indexOf(')');
        if((next_close<=next_open)||(next_open==-1)){
            String my_num=str.substring(0,next_close);
            return new Node(Integer.parseInt(my_num),null,null);
        }
        else {
            String my_num = str.substring(0, next_open-1);
            String str_left;
            String str_right;
            int i;
            int counter = 0;
            boolean not_found = true;
            for (i = next_open; i < str.length() & not_found; i++) {        //find left side
                if (str.charAt(i) == '(') counter++;
                else if (str.charAt(i) == ')') counter--;
                if (counter == 0)
                    not_found = false;
            }
            str_left=str.substring(next_open,i);

            next_open=i+1;
            not_found = true;
            for (i = next_open; i < str.length() & not_found; i++) {        //find right side
                if (str.charAt(i) == '(') counter++;
                else if (str.charAt(i) == ')') counter--;
                if (counter == 0)
                    not_found = false;
            }
            str_right=str.substring(next_open,i);
            return new Node(Integer.parseInt(my_num),(NodeFromString(str_left)),NodeFromString(str_right));
        }
    }


    public static int get_RowCol_TO_pixel(int[] row_col) {
        return ((row_col[0] * 28)+ row_col[1]);
    }
    public static int[] get_row_col (int pixel) {
        //int normalized = pixel - 1;
        int row = (pixel / 28);
        int col = (pixel % 28);
        int [] output = {row, col};
        return output;
    }
    public static LinkedList<Integer> around_pixel(int p) {
        int[] row_col = get_row_col(p);
        int cur_Row=row_col[0];
        int cur_Col=row_col[1];
        LinkedList<Integer> out = new LinkedList<>();
        if ((cur_Row > 0) && (cur_Col > 0)) {
            int[] temp = {cur_Row - 1, cur_Col - 1};
            out.add(get_RowCol_TO_pixel(temp));
        }
        if ((cur_Row > 0)) {
            int[] temp = {cur_Row - 1, cur_Col};
            out.add(get_RowCol_TO_pixel(temp));
        }
        if ((cur_Row > 0) && (cur_Col < 27)) {
            int[] temp = {cur_Row - 1, cur_Col + 1};
            out.add(get_RowCol_TO_pixel(temp));
        }
        if ((cur_Col > 0)) {
            int[] temp = {cur_Row, cur_Col - 1};
            out.add(get_RowCol_TO_pixel(temp));
        }

        if ((cur_Col < 27)) {
            int[] temp = {cur_Row, cur_Col + 1};
            out.add(get_RowCol_TO_pixel(temp));
        }
        if ((cur_Row < 27) && (cur_Col > 0)) {
            int[] temp = {cur_Row + 1, cur_Col - 1};
            out.add(get_RowCol_TO_pixel(temp));
        }
        if ((cur_Row < 27)) {
            int[] temp = {cur_Row + 1, cur_Col};
            out.add(get_RowCol_TO_pixel(temp));
        }
        if ((cur_Row < 27) && (cur_Col < 27)) {
            int[] temp = {cur_Row + 1, cur_Col + 1};
            out.add(get_RowCol_TO_pixel(temp));
        }
        return out;
    }
    public static boolean check_around (int pixel, Image curr) {
        int y;
        int sum=0;
        int passed = 0;
        LinkedList<Integer> pixel_list = around_pixel(pixel);
        if(pixel==207)
            y=9;
        int half = (pixel_list.size())/2;
        Iterator it = pixel_list.iterator();
        while (it.hasNext()) {
            Integer x = (Integer) it.next();
            //if (curr.getPixel(x)>128)  passed ++;
            sum+=curr.getPixel(x);
        }
        return (sum/(pixel_list.size())>128);
    }

}
