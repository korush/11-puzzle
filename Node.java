package puzzle;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Node {

    public Node(String state, Node parent, Actions action, Heuristics heuristic) {
        this.setHeuristic(heuristic);
        this.setState(state);
        this.setParent(parent);
        this.setAction(action);
        if (parent != null)
            this.depth = parent.getDepth() + 1;
        this.children = new ArrayList<>();
    }

    private static HashMap<String, Integer> patterns;
    static
    {

        System.out.println("Loading pattern database...");
        patterns = new HashMap<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("patterns.txt"));
            String st;
            while ((st = br.readLine()) != null) {
                String[] temp = st.split(",");
                patterns.put(temp[0], Integer.parseInt(temp[1]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Pattern database loaded.");

    }

    private String state;

    private Node parent;

    private ArrayList<Node> children;

    private Actions action;

    private int depth;

    private Integer cost;

    private Heuristics heuristic;

    public Heuristics getHeuristic()
    {
        return this.heuristic;
    }

    private void setHeuristic(Heuristics heuristics)
    {
        this.heuristic = heuristics;
    }


    public Integer getCost()
    {
        return this.cost;
    }

    private void setCost(int cost)
    {
        this.cost = cost;
    }

    public int getDepth() {
        return this.depth;
    }

    public String getState() {
        return state;
    }

    private void setState(String state) {
        this.state = state;
        switch (this.getHeuristic())
        {
            case Hamming:
                this.setCost(this.calculateHamming());
                break;
            case HammingA:
                this.setCost(this.getDepth() + this.calculateHamming());
                break;
            case DatabasePattern:
                this.setCost(this.calculateDatabasePattern());
                break;
            case DatabasePatternA:
                this.setCost(this.getDepth() + this.calculateDatabasePattern());
                break;
            case Manhattan:
                this.setCost(this.calculateManhattanDistance());
                break;
            case ManhattanA:
                this.setCost(this.getDepth() + this.calculateManhattanDistance());
                break;
            case LinearConflict:
            	this.setCost(this.calculatelinearConflict());
            case LinearConflictA:
            	this.setCost(this.getDepth() + this.calculatelinearConflict());

                default:
                    this.setCost(0);
                    break;
        }

    }

    public Node getParent() {
        return parent;
    }

    private void setParent(Node parent) {
        this.parent = parent;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    private void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public Actions getAction() {
        return action;
    }

    public void setAction(Actions action) {
        this.action = action;
    }

    public void addChild(Node n) {
        this.children.add(n);
        n.parent = this;
    }

    public int calculateHamming()
    {
        int r = this.state.indexOf("0");
        int cost =  0;
        for (int i = 0; i < Constants.PuzzleRowsCount; i++)
            for (int j = 0; j < Constants.PuzzleColumnsCount; j++) {
                int k = i * Constants.PuzzleColumnsCount + j;
                String temp = Character.toString(this.state.charAt(k));
                if(temp.equalsIgnoreCase("A"))
                    temp = "10";
                else if(temp.equalsIgnoreCase("B"))
                    temp = "11";
                if(temp.equals("0"))
                	continue;
                if( !Integer.toString(k + 1).equalsIgnoreCase(temp))
                    ++cost;
            }

            return cost;
    }

    public int calculateDatabasePattern()
    {
        String s = this.getState().replace("7", "*")
                .replace("8", "*")
                .replace("9", "*")
                .replace("A", "*")
                .replace("B", "*");
        Integer cost =  patterns.getOrDefault(s, -1);

        if(cost == -1)
            return this.calculateManhattanDistance();

        return cost;
    }

    private int calculateManhattanDistance() {
        int sum = 0, r = 0;
        for (int i = 0; i < Constants.PuzzleRowsCount; i++)
            for (int j = 0; j < Constants.PuzzleColumnsCount; j++) {
            	String temp = Character.toString(this.state.charAt(r++));
                if(temp.equalsIgnoreCase("A"))
                    temp = "10";
                else if(temp.equalsIgnoreCase("B"))
                    temp = "11";
                
            	int value = Integer.parseInt(temp);
                if(value == 0)
                    continue;
                	
                    int x = (value - 1) / Constants.PuzzleColumnsCount;
                    int y = (value - 1) % Constants.PuzzleColumnsCount;
                    
                    
//                    sum += Math.abs(i - x) + Math.abs(j - y);
                    
                    sum += Math.abs(i-x);
                    if(Math.abs(y - j) != 1)
                    {
                    int k = j == y ? j : j + i-x;
                    sum += Math.abs(k-y);
                    }

            }
        
        
        return sum;
    }

    private int calculatelinearConflict()
    {
    	int lc = 0;
    	
    	for (int i = 0; i < Constants.PuzzleRowsCount; i++)
    	{
    		int max = -1;
            for (int j = 0; j < Constants.PuzzleColumnsCount; j++) {
                int k = i * Constants.PuzzleColumnsCount + j;
                String temp = Character.toString(this.state.charAt(k));
                if(temp.equalsIgnoreCase("A"))
                    temp = "10";
                else if(temp.equalsIgnoreCase("B"))
                    temp = "11";
                
            	int value = Integer.parseInt(temp);
                
            	if (value != 0 && ((value -1) / Constants.PuzzleColumnsCount) == i){
					if (value > max){
						max = value;
					}else {
						lc += 2;
					}
				}
            }
    	}
    	
    	for (int j = 0; j < Constants.PuzzleColumnsCount; j++)
    	{
    		int max = -1;
        	for (int i = 0; i < Constants.PuzzleRowsCount; i++) {
                int k = i * Constants.PuzzleColumnsCount + j;
                String temp = Character.toString(this.state.charAt(k));
                if(temp.equalsIgnoreCase("A"))
                    temp = "10";
                else if(temp.equalsIgnoreCase("B"))
                    temp = "11";
                
            	int value = Integer.parseInt(temp);

                
            	if (value != 0 && ((value -1) % Constants.PuzzleColumnsCount) == j){
					if (value > max){
						max = value;
					}else {
						lc += 2;
					}
				}
            }
    	}
    	
    	if(lc == 0)
        	return calculateManhattanDistance();

    	return lc;
    }
    
    private String swapTiles(int firstIndex, int secondIndex) {
        StringBuilder sb = new StringBuilder(this.state);
        char first, second;
        first = sb.charAt(firstIndex);
        second = sb.charAt(secondIndex);

        sb.setCharAt(firstIndex, second);
        sb.setCharAt(secondIndex, first);

        return sb.toString();
    }

    public ArrayList<Node> generateChildren() {
        ArrayList<Node> list = new ArrayList<Node>();
        int r = this.state.indexOf("0");
        int i = r / Constants.PuzzleColumnsCount;
        int j = r % Constants.PuzzleColumnsCount;

        if (i != Constants.PuzzleRowsCount - 1 && j != Constants.PuzzleColumnsCount - 1)
            list.add(new Node(this.swapTiles(r, (i + 1) * Constants.PuzzleColumnsCount + j + 1), this, Actions.UpLeft, this.getHeuristic()));

        if (j != Constants.PuzzleColumnsCount - 1)
            list.add(new Node(this.swapTiles(r, i * Constants.PuzzleColumnsCount + j + 1), this, Actions.Left, this.getHeuristic()));

        if (i != 0 && j != Constants.PuzzleColumnsCount - 1)
            list.add(new Node(this.swapTiles(r, (i - 1) * Constants.PuzzleColumnsCount + j + 1), this, Actions.DownLeft, this.getHeuristic()));

        if (i != 0)
            list.add(new Node(this.swapTiles(r, (i - 1) * Constants.PuzzleColumnsCount + j), this, Actions.Down, this.getHeuristic()));

        if (i != 0 && j != 0)
            list.add(new Node(this.swapTiles(r, (i - 1) * Constants.PuzzleColumnsCount + j - 1), this, Actions.DownRight, this.getHeuristic()));

        if (j != 0)
            list.add(new Node(this.swapTiles(r, i * Constants.PuzzleColumnsCount + j - 1), this, Actions.Right, this.getHeuristic()));

        if (i != Constants.PuzzleRowsCount - 1 && j != 0)
            list.add(new Node(this.swapTiles(r, (i + 1) * Constants.PuzzleColumnsCount + j - 1), this, Actions.UpRight, this.getHeuristic()));

        if (i != Constants.PuzzleRowsCount - 1)
            list.add(new Node(this.swapTiles(r, (i + 1) * Constants.PuzzleColumnsCount + j), this, Actions.Up, this.getHeuristic()));


        return list;
    }

    public boolean isGoal() {
        return this.getState().equals(Constants.GoalState);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (this.getParent() == null)
            sb.append("0");
        else {
            int r = this.state.indexOf("0");
            switch (r) {
                case 0:
                    sb.append("a");
                    break;
                case 1:
                    sb.append("b");
                    break;
                case 2:
                    sb.append("c");
                    break;
                case 3:
                    sb.append("d");
                    break;
                case 4:
                    sb.append("e");
                    break;
                case 5:
                    sb.append("f");
                    break;
                case 6:
                    sb.append("g");
                    break;
                case 7:
                    sb.append("h");
                    break;
                case 8:
                    sb.append("i");
                    break;
                case 9:
                    sb.append("j");
                    break;
                case 10:
                    sb.append("k");
                    break;
                case 11:
                    sb.append("l");
                    break; 

            }
        }


        sb.append(" [");
        for (int i = 0; i < Constants.PuzzleRowsCount; i++) {
            for (int j = 0; j < Constants.PuzzleColumnsCount; j++) {
                char temp = this.state.charAt(i * Constants.PuzzleColumnsCount + j);

                sb.append((temp == 'A' ? "10" : temp == 'B' ? "11" : temp) + ((i == Constants.PuzzleRowsCount - 1 && j == Constants.PuzzleColumnsCount - 1) ? "]" : ", "));
            }


        }
        
//        sb.append(this.getCost());

        return sb.toString();
    }



}
