package puzzle;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Driver {

    public static void main(String[] args) {
    	
    	if(args.length != 0)
    	{
    	StringBuilder sb = new StringBuilder();
    	
    	
    	for(String s : args)
    	{
    		sb.append(s);
    		sb.append(" ");
    	}
    	
    	System.out.println(sb.toString().trim());
        PuzzleSolver.Run(sb.toString().trim());
        
    	return;
    	}
    	
    	
    	System.out.println("Input start state");
    	Scanner sc = new Scanner(System.in);
    	
    	String str = sc.nextLine();
    	
    	
        PuzzleSolver.Run(str.trim());

//        PuzzleSolver.generatePatternDatabase();
    }

}


