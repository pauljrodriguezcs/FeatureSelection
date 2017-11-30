import java.util.*;
import java.lang.Math;
import java.io.*;

public class featureSelection {
    
    //This class will hold all global variables needed to run the program
    public static class gv {
        public static int numFeatures = 0;  //number of features(x value)
        public static int numItems = 0;     //number of items in our data set
        public static int numLines = 0;     //number of lines(y value)
        public static double totalValue = 0.0;  //sum of items in our data set
        public static double meanValue = 0.0;   //average value of items
        public static double variance = 0.0;
        public static double stdDev= 0.0;
        public static boolean numFeatureSet = false;    //will be used to distinguish first line when reading the file
        public static String delims = "[ ]+";   //used to split the line
        public static String[] tokens;      //will hold the items that are read in
        
        public static List<List<Double>> dataList = new ArrayList<List<Double>>(numFeatures);
    }
    
    public static class best{
        public static Set<Integer> featureSelection = new HashSet<Integer>();
        public static double accuracy = 0.0;
    }
    
    //The list of data will be filled up in here
    public static void fill_array(String textFile){
        try {
            File file = new File(textFile);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            
            while ((line = bufferedReader.readLine()) != null) {
                ++gv.numLines;
                gv.tokens = line.split(gv.delims);  //parse the line based on the delims
                
                if(!gv.numFeatureSet){              //will only go in here when the first line is read in. Sets the number of features available
                    gv.numFeatures = gv.tokens.length - 2;  //subtract the whitespace and class identifier
                    gv.numFeatureSet = true;        //prevent from entering this if statement in the future
                    
                    for(int i = 0; i < gv.numFeatures + 1; ++i){
                        gv.dataList.add(new ArrayList<Double>());
                        double tmpDouble = Double.parseDouble(gv.tokens[i+1]);
                        
                        if(i != 0){                 //prevents adding the class identifer into totalValue
                            gv.totalValue = gv.totalValue + tmpDouble;
                            ++gv.numItems;
                        }
                        gv.dataList.get(i).add(tmpDouble);
                    }//end for loop
                }//end if statement
                
                else{   //will go in here for the rest of the file
                    for(int i = 0; i < gv.numFeatures+1; ++i){
                        double tmpDouble = Double.parseDouble(gv.tokens[i+1]);
                        if(i != 0){
                            gv.totalValue = gv.totalValue + tmpDouble;
                            ++gv.numItems;
                        }
                        gv.dataList.get(i).add(tmpDouble);
                    }//end for loop
                }//end else statement
            }//end while loop
            fileReader.close();
        }//end try
        catch (IOException e) {
            e.printStackTrace();
        }//end catch
    }
    
    public static void normalize_data(){
        gv.meanValue = gv.totalValue / gv.numItems; //calculate the meanvalue
        
        for(int i = 1; i < gv.numFeatures+1; ++i){  //iterate through dataset and find variance sum
            for(int j = 0; j < gv.numLines; ++j){
                gv.variance = gv.variance + Math.pow((gv.dataList.get(i).get(j) - gv.meanValue),2);
            }
        }
        
        gv.variance = gv.variance/(gv.numItems-1);
        gv.stdDev = Math.sqrt(gv.variance);
        
        for(int i = 1; i < gv.numFeatures+1; ++i){  //z-normalize the data (x-mean)/stdDev
            for(int j = 0; j < gv.numLines; ++j){
                gv.dataList.get(i).set(j,(gv.dataList.get(i).get(j) - gv.meanValue)/gv.stdDev);
            }
        }
    }
    
    public static double leave_one_out_cross_validation(Set<Integer> current_set_of_features,int k){
        ArrayList<Integer> feats = new ArrayList<Integer>();
        feats.add(k);
        
        if(!current_set_of_features.isEmpty()){
            Iterator<Integer> it = current_set_of_features.iterator();
            //feats.add(it.next());
            while(it.hasNext()){
                feats.add(it.next());
            }
        }
        
        double correct_classification = 0.0;
        
        for(int i = 0; i < gv.numLines; ++i){
            int nn_index = 0;
            double nearest_neighbor = Double.POSITIVE_INFINITY;
            
            for(int j = 0; j < gv.numLines; ++j){
                double diff1 = 0.0;
                double diff2 = 0.0;
                double diff_sqr = 0.0;
                double tmp_dist = 0.0;
                if(i != j){
                    for(int m = 0; m < feats.size(); ++m){
                        diff1 = gv.dataList.get(feats.get(m)).get(i);
                        diff2 = gv.dataList.get(feats.get(m)).get(j);
                        diff_sqr = diff_sqr + Math.pow(diff2 - diff1,2);
                    }
                    tmp_dist = Math.sqrt(diff_sqr);
                    if(tmp_dist < nearest_neighbor){
                        nearest_neighbor = tmp_dist;
                        nn_index = j;
                    }
                }
            }

            if(Math.floor(gv.dataList.get(0).get(i)) == Math.floor(gv.dataList.get(0).get(nn_index))){
                ++correct_classification;
            }
        }
        
        
        /*
        if(current_set_of_features.isEmpty()){
            for(int i = 0; i < gv.numLines; ++i){
                for(int j = 0; j < gv.numLines; ++j){
                    if(i != j){
                        double tmp_nn = Math.abs(gv.dataList.get(k).get(j) - gv.dataList.get(k).get(i));
                        if(tmp_nn < nearest_neighbor){
                            nearest_neighbor = tmp_nn;
                            nn_index = j;
                        } //end if
                    } // end if
                } //end for
                
                if(Math.floor(gv.dataList.get(0).get(i)) == Math.floor(gv.dataList.get(0).get(nn_index))){
                    ++correct_classification;
                } // end if
                
                nearest_neighbor = Double.POSITIVE_INFINITY;
            }// end for
        }
        
        else{
            Iterator<Integer> it = current_set_of_features.iterator();
            Integer[] cf = new Integer[current_set_of_features.size()];
            for(int i = 0; i < cf.length; ++i){
                cf[i] = it.next();
            }
            ArrayList<Double> sums = new ArrayList<Double>();
            
            for(int i = 0; i < cf.length; ++i){
                for(int j = 0; j < gv.numLines; ++j){
                    if(i == 0){
                       // StdOut.print("i = 0: ");
                        sums.add(gv.dataList.get(cf[i]).get(j));
                        //StdOut.println(sums.get(j));
                        
                    }
                    //StdOut.println("i = " + i);
                    sums.set(j,sums.get(j) + gv.dataList.get(k).get(j));
                }
                
            }
            
            for(int i = 0; i < gv.numLines; ++i){
                for(int j = 0; j < gv.numLines; ++j){
                    if(i != j){
                        double tmp_nn = Math.abs(sums.get(j) - sums.get(i));
                        if(tmp_nn < nearest_neighbor){
                            nearest_neighbor = tmp_nn;
                            nn_index = j;
                        } //end if
                    } // end if
                } //end for
                
                if(Math.floor(gv.dataList.get(0).get(i)) == Math.floor(gv.dataList.get(0).get(nn_index))){
                    ++correct_classification;
                } // end if
                
                nearest_neighbor = Double.POSITIVE_INFINITY;
            }// end for
            
            
            
            
        }
        */
        return correct_classification/gv.numLines;
        
        
        
    }
    
    
    public static void main(String[] args){
        StdOut.println('\n' + "Welcome to Paul's Feature Selection Algorithm.");
        StdOut.print('\n' + "Type in the name of the file to test: ");
        String textFileName = StdIn.readLine();     //read in name of file
        fill_array(textFileName);                   //fill list with file values
        StdOut.println('\n' + "This dataset has " + gv.numFeatures + " features (not including the class attribute), with " + gv.numLines + " instances.");
        StdOut.print('\n' + "Please wait while I normalize the data…   ");
        normalize_data();                           //normalize the data
        StdOut.println("Done!" + '\n');
        
        
        
        Set<Integer> current_set_of_features = new HashSet<Integer>();
        for(int i = 0; i < gv.numFeatures; ++i){
            StdOut.println("On the " + (i+1) +"th level of the search tree");
            int feature_to_add_at_this_level = 0;
            double best_so_far_accuracy = 0.0;
            
            for(int j = 0; j < gv.numFeatures; ++j){
                if(!current_set_of_features.contains(j+1)){
                    StdOut.print("--Considering adding the " + (j+1) + " feature: ");
                    double accuracy = leave_one_out_cross_validation(current_set_of_features,j+1);
                    StdOut.println(accuracy + "% accuracy");
                    
                    if(accuracy > best_so_far_accuracy){
                        best_so_far_accuracy = accuracy;
                        feature_to_add_at_this_level = (j+1);
                    }
                }
            }
            current_set_of_features.add(feature_to_add_at_this_level);
            if(best_so_far_accuracy > best.accuracy){
                best.accuracy = best_so_far_accuracy;
                if(!best.featureSelection.isEmpty()){
                    best.featureSelection.clear();
                    Iterator<Integer> it = current_set_of_features.iterator();
                    while(it.hasNext()){
                        best.featureSelection.add(it.next());
                    }
                    
                }
                
                else{
                    best.featureSelection.add(feature_to_add_at_this_level);
                }
            }
            StdOut.println("On level " + (i+1) + " i added feature " + feature_to_add_at_this_level + " to current set");
        }
        StdOut.println(best.featureSelection.size());
        StdOut.print("Finished! The best feature subset is {");
        Iterator<Integer> it = best.featureSelection.iterator();
        StdOut.print(it.next());
        while(it.hasNext()){
            StdOut.print(" " + it.next());
        }
        StdOut.println("} with accuracy " + best.accuracy);
        
        //CS170Smalltestdata__53.txt {1 2} with accuracy 0.96 =     2 7 1
        //CS170BIGtestdata__98.txt {19 39 44} with accuracy 0.95 =  19 9 39
        
        
    }
}
