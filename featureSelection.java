import java.util.*;

public class featureSelection {

    /*
    public static double leave_one_out_cross_validation(data,Set<Integer> current_set_of_features,int k){
        
    }
     */
    
    public static void main(String[] args){
        StdOut.println("Reading lines from file...");
        
        int numFeatures = 0;
        String first_line = StdIn.readLine();
        String delims = "[ ]+";
        String[] tokens = first_line.split(delims);
        
        if (tokens.length != 0){
            numFeatures = tokens.length - 1;
        }
        
        Set<Integer> current_set_of_features = new HashSet<Integer>();
        
        int meanValue = 0;
        int stdDev= 0;
        List<List<String>> dataList = new ArrayList<List<String>>(numFeatures);
        
        for(int i = 0; i < numFeatures; ++i){
            
        }
        
        
        for(int i = 0; i < numFeatures; ++i){
            StdOut.println("On the " + (i+1) +"th level of the search tree");
            int feature_to_add_at_this_level = 0;
            double best_so_far_accuracy = 0.0;
            
            for(int j = 0; j < numFeatures; ++j){
                
                if(!current_set_of_features.contains(j+1)){
                    StdOut.println("--Considering adding the " + (j+1) + " feature");
                    double accuracy = 0.0; // leave_one_out_cross_validation(data,current_set_of_features,k+1);
                    
                
                    if(accuracy > best_so_far_accuracy){
                        best_so_far_accuracy = accuracy;
                        feature_to_add_at_this_level = (j+1);
                    }
                }
            }
            
            current_set_of_features.add(feature_to_add_at_this_level);
            StdOut.println("On level " + (i+1) + " i added feature " + feature_to_add_at_this_level + " to current set");
        }
    }
}
