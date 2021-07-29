import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static String read_csv(String path) {
        String s;
        String final_str = "";
        Scanner sc = null;
        try {
            sc = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            System.out.println("arg0 File not exists.");
            System.exit(0);
        }
        while (sc.hasNext()) {
            s = sc.next();
            final_str = final_str + s;
        }
        sc.close();  //closes the scanner
        return final_str;
    }
    public static void run_csv(String path, Node my_tree) {
        String s;
        Scanner sc = null;
        try {
            sc = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            System.out.println("arg1 File not exists.");
            System.exit(0);
        }
        sc.useDelimiter(",");
        int[] curr_data = new int[784];
        int curr_label;
        int i= 0 ;
        int curr_val;
        double succeeded = 0;
                                            double Alpha = 0.1; //for NoiseChecks, 0 means no Noise
                                                    int n = 9; //for ConfusionMatrix
        int s_digit_ocuurences = 0;
        int[] arr = new int[10]; //for ConfusionMatrix
        for (int k=0; k<10; k++) {
            arr[k] = 0;
        }
        while (sc.hasNext()) {
            i = i+ 1;
            s = sc.next();
            s = s.replaceAll("\\s+", "");
            if (! s.equals("")) {
                curr_label = Integer.parseInt(s);
                for (int j = 1; j < 784; j++) {
                    s = sc.next();
                    curr_data[j - 1] = Integer.parseInt(s);
                }
                sc.useDelimiter("\n");
                s = sc.next();
                sc.useDelimiter(",");
                curr_data[783] = Integer.parseInt(s.substring(1));
                //Noise(Alpha, curr_data);
                Image curImage = new Image(curr_data, curr_label);
                curr_val = check(curImage, my_tree);
                System.out.println(curr_val);
                //if (curr_val == curr_label) { succeeded = succeeded+1; }
                //if (curr_label ==n) {
                //    s_digit_ocuurences ++;
                //    arr[curr_val] = arr [curr_val] + 1; };
            }

        }
        //System.out.println("Error rate is:  "+ (100.0-(succeeded/100.0)));
        //System.out.println (n +  " Appered: " + s_digit_ocuurences);
        //for (int k=0; k<10; k++) {
        //    System.out.println("Digit " + k + ": " + arr[k]);
        //}

        sc.close();  //closes the scanner
    }
    public static int check(Image im, Node Tree) {
        if (Tree.isLeaf()) {
            return Tree.getData();
        }
        int curCond = Tree.getData();
        int curpixel = im.getPixel(curCond);
        if (curpixel > 128)//TODO 128
            return check(im, Tree.getRight());
        return check(im, Tree.getLeft());
    }
    public static void Noise(double Alpha, int[] im) {
        for (int i=0; i<784; i++) {
            double r = Math.random();
            if (r <=Alpha) {
                im[i] = (im[i] - 255);
                if (im[i]<0) { im[i] = im[i]*(-1); };
            }
        }
    }
    public static void main(String[] args) {
        String tree_string=read_csv(args[0]);
        String tests_path=args[1];
        Node my_tree = new Node(5, null, null);
        my_tree = my_tree.NodeFromString(tree_string);
        run_csv(tests_path, my_tree);
    }
}


