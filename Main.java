import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;
import java.util.*;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;   // Import the FileWriter class


import static java.lang.Integer.parseInt;

public class Main {
    public static Image[] allImages;
    public static LinkedList<Integer> training_Images=new LinkedList<>();
    public static LinkedList<Integer> validation_set;
    public static LinkedList<Integer> C=new LinkedList<>();
    public static int type_run;
    public static int sizeInputToRead;
    public static int[][] data_base_cond1;
    public static int[][] data_base_cond0;
    public static int[][] data_base_cond_mix;

    public static int get_size(String path) {
        String s;
        Scanner sc = null;
        try {
            sc = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            System.err.print("arg3-path wrong");
            System.exit(0);
        }
        sc.useDelimiter(",");
        int[] curr_data = new int[784];
        int curr_label = -1;
        int i = -1;
        while (sc.hasNext()&(i<40000)) {
            //for (int i = 0; i < n; i++) {
            i++;
            s = sc.next();
            s = s.replaceAll("\\s+", "");
            if (!s.equals("")) {
                curr_label = Integer.parseInt(s);
                for (int j = 1; j < 784; j++) {
                    s = sc.next();
                }
                sc.useDelimiter("\n");
                s = sc.next();
                sc.useDelimiter(",");
            }
        }
        return i;
    }
    public static void test(){
                PriorityQueue<Integer> l = new PriorityQueue<>((a,b) -> (int) (b-a));
                l.add(1);
                l.add(2);
                l.add(3);
                l.add(4);
                l.add(5);
                l.add(6);
                System.out.println(l.poll());
                System.out.println(l.poll());
                System.out.println(l.poll());
                System.out.println(l.poll());


    }
    public static void read_csv(String path, int n)  {

        //Image[] data = new Image[n];
        ArrayList<Image> data = new ArrayList<>();
        String s;
        Scanner sc = null;
        try {
            sc = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            System.err.print("arg3-path wrong");
            System.exit(0);
        }
        sc.useDelimiter(",");
        int[] curr_data = new int[784];
        int curr_label=-1;
        int i=-1;
        while(sc.hasNext()) {
        //while((sc.hasNext())&(i<60000)) {
        //for (int i = 0; i < n; i++) {
            i++;
            s = sc.next();
            s = s.replaceAll("\\s+", "");
            if (!s.equals("")) {
                curr_label = Integer.parseInt(s);
                for (int j = 1; j < 784; j++) {
                    s = sc.next();
                    curr_data[j - 1] = Integer.parseInt(s);
                }
                sc.useDelimiter("\n");
                s = sc.next();
                sc.useDelimiter(",");
                curr_data[783] = Integer.parseInt(s.substring(1));

                                            double Alpha = 0.1; //for NoiseChecks, 0 means no Noise
                //Noise(Alpha, curr_data);

                data.add(new Image(curr_data, curr_label));
                //if (!validation_set.contains(i))
                    //training_Images.addLast(i);
            }
        }
        sc.close();  //closes the scanner
        sizeInputToRead=i;
        int size=data.size();
        allImages = new Image[size];
        for (int k=0; k<size; k++)
            allImages[k]=data.get(k);
        //allImages=data;
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
    public static int mostCommon(LinkedList<Integer> images){
        int maxNumber=0;
        int[] allNumbers=new int[10];
        int curNumber;
        for(int j=0;j<10;j++)
            allNumbers[j]=0;
        Iterator it=images.iterator();
        while(it.hasNext()) {
            Image curimage=allImages[(int)it.next()];
            curNumber = curimage.getLabel();
            allNumbers[curNumber] = (allNumbers[curNumber]) + 1;
        }
        for(int j2=0;j2<10;j2++)
            if(allNumbers[j2]>allNumbers[maxNumber])
                maxNumber=j2;
        return maxNumber;
    }
    public static LinkedList<Integer> random_P(int p,int allLength){
        int how_much=p*allLength/100;
        int randomNum;
        LinkedList<Integer> generatedNumbers = new LinkedList<Integer>();
        while (generatedNumbers.size() < how_much){
            randomNum=(int) (Math.random() * (allLength));
            if(! (generatedNumbers.contains(randomNum)))
                generatedNumbers.add(randomNum);
        }
//        while( ! generatedNumbers.isEmpty())
//            System.out.println(generatedNumbers.pop());
        return generatedNumbers;
    }
    public static void make_conditions(int length){
        for(int i=0;i<length;i++)
            C.addLast(i);
    }
    public static void expandNode(Node cur){
        //TODO we can remove the current condition to save RUNTIME
        int mostCommon_A=mostCommon(cur.getLa());
        int mostCommon_B=mostCommon(cur.getLb());

        Node la=new Node(mostCommon_A,cur.getLa(), cur.getC(),allImages);
        Node lb=new Node(mostCommon_B,cur.getLb(), cur.getC(),allImages);
        cur.setLeft(la);
        cur.setRight(lb);
        cur.setData(cur.getBest_condition());
    }
    public static void makeTraining(){
        for (int i=0;i<sizeInputToRead;i++){
            if (!validation_set.contains(i))
                training_Images.addLast(i);
        }

    }
    public static int buildTree(Node root,int L){
        //LinkedList<Node> leafs=new LinkedList<>();//TODO can be better runtime with heap
        PriorityQueue<Node> leafs = new PriorityQueue<>((a,b) -> (int) (b.getIG()-a.getIG()));

        int totalValidationSize=validation_set.size();
        int finalresults=0;
        int best_T=0;
        int curresult=0;
        int T = (int) Math.pow(2, L);
        Node root_to_return=root;
        leafs.add(root_to_return);
        for(int i=1;i<=T;i++){
            //int indexMaxIG=0;
            double MaxIG=0;
            Node max_Node=null;
            Node cur_Node;
            /*Iterator itLeafs=leafs.iterator();
            while(itLeafs.hasNext()){
                cur_Node= (Node) itLeafs.next();
                if(cur_Node.getIG()>MaxIG){
                    MaxIG=cur_Node.getIG();
                    //indexMaxIG=j;
                    max_Node=cur_Node;
                }
            }*/
            max_Node=leafs.poll();

            //we have the most IG leaf, now we need to replace it with the most good cond.
            // attach to him create 2 child Nodes, delete it from the leafs and add the childs

            if(max_Node==null)
                i=T+1;
            else {
                expandNode(max_Node);
                //leafs.remove(max_Node);

                //leafs.addLast(max_Node.getLeft());
                //leafs.addLast(max_Node.getRight());
                leafs.add(max_Node.getLeft());
                leafs.add(max_Node.getRight());
            }

            double curRound=Math.log(i) / Math.log(2);
            if((curRound%1==0)||(max_Node==null)) {//means its log of 2
                //if(i==T-2) {
                    curresult = validate(root_to_return, totalValidationSize);
                    //System.out.println("");
                   // System.out.println("L= " + curRound + " cur round= " + i + " and the result is " + curresult + " % ------------------");
                //}
                if (finalresults < curresult) {
                    best_T = i;
                    finalresults = curresult;
                }
            }
        }
    return best_T;
    }
    public static Node buildTree2(Node root,int rounds){
        //LinkedList<Node> leafs=new LinkedList<>();//TODO can be better runtime with heap
        PriorityQueue<Node> leafs = new PriorityQueue<>((a,b) -> (int) (b.getIG()-a.getIG()));

        Node root_to_return=root;
        leafs.add(root_to_return);
        for(int i=1;i<=rounds;i++){
            //int indexMaxIG=0;
            double MaxIG=0;
            Node max_Node=null;
            Node cur_Node;
            /*for (Node leaf : leafs) {
                cur_Node = leaf;
                if (cur_Node.getIG() > MaxIG) {
                    MaxIG = cur_Node.getIG();
                    //indexMaxIG=j;
                    max_Node = cur_Node;
                }
            }*/

            max_Node=leafs.poll();
            //we have the most IG leaf, now we need to replace it with the most good cond.
            // attach to him create 2 child Nodes, delete it from the leafs and add the childs

            if(max_Node==null)
                i=rounds+1;
            else {
                expandNode(max_Node);
                //leafs.remove(max_Node);
                leafs.add(max_Node.getLeft());
                leafs.add(max_Node.getRight());
                //leafs.addLast(max_Node.getLeft());
                //leafs.addLast(max_Node.getRight());
            }

        }
        return root_to_return;
    }
    public static int validate(Node Tree,int totalValidationSize) {
        int output = 0;
        Image cur_test=null;
        for (Integer integer : validation_set) {
            cur_test = allImages[(int) integer];
            if (check(cur_test, Tree) == cur_test.getLabel()) {
                //System.out.println("we entered: " + cur_test.getLabel());
                //System.out.println("returned from check: " + check(cur_test, Tree));
                output = output + 1;
            } else {
                //System.out.println("we entered: " + cur_test.getLabel());
                //System.out.println("returned from check: " + check(cur_test, Tree));
            }
        }
        return (output*100/totalValidationSize);
    }
    public static double validate_final(Node Tree) {
        int output = 0;
        int num_checks=allImages.length;
        Image cur_test=null;
        for (Image allImage : allImages) {
            cur_test = allImage;
            if (check(cur_test, Tree) == cur_test.getLabel()) {
                output = output + 1;
                //System.out.println("we entered: " + cur_test.getLabel());
                //System.out.println("returned from check: " + check(cur_test, Tree));
            } else {
                //System.out.println("we entered: " + cur_test.getLabel());
                //System.out.println("returned from check: " + check(cur_test, Tree));
            }
        }
        double res=(((double)output)*100/((double)num_checks));
        return res;
    }
    public static int check(Image im, Node Tree){
        if (Tree.isLeaf()) {
            return Tree.getData();
        }
        int curCond=Tree.getData();
        int curpixel=im.getPixel(curCond);
        if (curpixel > 128)//TODO 128
            return check (im, Tree.getRight());
        return check(im, Tree.getLeft());
    }
    public static void check_inputs(String[] args){

        try {
            parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.print("arg0 wrong");
            System.exit(0);
        }
        int type_run=parseInt(args[0]);
        if(type_run!=1 & type_run!=2){
            System.err.print("arg0 wrong");
            System.exit(0); }

        try {
            parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.print("arg1 wrong");
            System.exit(0);
        }
        int p=parseInt(args[1]);
        if(p<0 | p>99){
            System.err.print("arg1 wrong");
            System.exit(0); }

        try {
            parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.print("arg2 wrong");
            System.exit(0);
        }
        int L=parseInt(args[2]);
        if(L<1){
            System.err.print("arg2 wrong");
            System.exit(0); }

        try {
            File myObj = new File(args[4]);
            if (myObj.createNewFile()) {
                int y=1;
               //System.out.println("File created:");
            } else {
                int y=2;
                //System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.err.print("arg4 wrong");
            System.exit(0);
        }
    }
    public static void write_to_file(String output_path,String string_tree){
        try {
            FileWriter myWriter = new FileWriter(output_path);
            myWriter.write(string_tree);
            myWriter.close();
            //System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            System.exit(0);
        }
    }
    public static void make_data_base_cond1(){
        Node test=new Node(0,null,null);
        data_base_cond1=new int[sizeInputToRead][784];
        for(int i=0;i<sizeInputToRead;i++)
            for(int j=0;j<784;j++)
                if(test.check_around(j,allImages[i]))
                    data_base_cond1[i][j]=1;
                else
                    data_base_cond1[i][j]=0;
    }
    public static void make_data_base_cond0(){
        Node test=new Node(0,null,null);
        data_base_cond0=new int[sizeInputToRead][784];
        for(int i=0;i<sizeInputToRead;i++)
            for(int j=0;j<784;j++)
                if(allImages[i].getPixel(j)>128)
                    data_base_cond0[i][j]=1;
                else
                    data_base_cond0[i][j]=0;
    }
    public static void main(String[] args) {
        //Instant starts = Instant.now();

        check_inputs(args);
        int totalPixelsinImage=784;
        //int L=5;
        //int p=10;
        //String path="C:\\Users\\User\\IdeaProjects\\Digital_Recognition\\mnist_train.csv";
        //String output_path="C:\\Users\\User\\IdeaProjects\\Digital_Recognition\\tree_file.csv";
        type_run=parseInt(args[0]);
        if(type_run!=1 & type_run!=2){
            System.err.print("arg0 wrong");
            System.exit(0); }
        int p=parseInt(args[1]);
        if(p<0 | p>99){
            System.err.print("arg1 wrong");
            System.exit(0); }
        int L=parseInt(args[2]);
        if(L<1){
            System.err.print("arg2 wrong");
            System.exit(0); }
        String path=args[3];//TODO open and check input
        String output_path=args[4];//TODO open and check input

        //int sizeInputToRead=get_size(path);
        //System.out.println(sizeInputToRead);
        //int sizeInputToRead=40000;

        read_csv(path,sizeInputToRead);
        validation_set=random_P(p,sizeInputToRead);
        makeTraining();
        make_conditions(totalPixelsinImage);
        int most_Common=mostCommon(training_Images);

        //Instant ends2 = Instant.now();
        //System.out.println("finish first part"+Duration.between(starts, ends2));

        if(type_run==1) {
            make_data_base_cond0();
            //Instant ends3 = Instant.now();
            //System.out.println("finish build the data base 0  " + Duration.between(starts, ends3));
        }
        else{
            //make_data_base_cond0();
            make_data_base_cond1();
            //Instant ends3 = Instant.now();
            //System.out.println("finish build the data base 1  " + Duration.between(starts, ends3));
        }

        Node myTree= new Node(most_Common,training_Images,C,allImages);
        int best_T=0;

        //System.out.println("-1");
            best_T = buildTree(myTree,L);
        //System.out.println("0");
            //cur_Tree.printNode();
        //System.out.println("best T is: "+best_T);
        LinkedList<Integer> allImages2=new LinkedList<>();
        for(int i=0;i<sizeInputToRead;i++)
            allImages2.add(i);

        //System.out.println("1");
        Node myBestTree= new Node(most_Common,allImages2,C,allImages);
        //System.out.println("2");

        myBestTree=buildTree2(myBestTree,best_T);//TODO update the root

        double best_result = validate_final(myBestTree);
        //System.out.println("---"+best_result);
        //System.out.println("------------------------------");
        System.out.println("num: "+sizeInputToRead);
        System.out.println("error: "+(int)(100-best_result));
        System.out.println("size: "+best_T);

        //System.out.println("------------------------------");
        String my_string_tree=myBestTree.NodeToString();
        //System.out.println(my_string_tree);
        write_to_file(output_path,my_string_tree);
        //Instant ends = Instant.now();
        //System.out.println(Duration.between(starts, ends));
        }
}


