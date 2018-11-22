package puzzle;

import java.io.*;
import java.util.*;


public class PuzzleSolver
{

    private boolean stateExists(ArrayList<Node> closed, Collection<Node> open, Node node)
    {
        return closed.stream().map(Node::getState).anyMatch(node.getState()::equalsIgnoreCase)
                    || open.stream().map(Node::getState).anyMatch(node.getState()::equalsIgnoreCase);
    	
    }

    private String solutionToString(Node n)
    {
       StringBuilder sb = new StringBuilder();
       solutionToString(n, sb);
       return sb.toString();

    }
    
    private void solutionToString(Node n, StringBuilder sb)
    {
        if( n == null)
            return;

        if(n.getParent() != null)
        	solutionToString(n.getParent(), sb);

        sb.append(n.toString());
        sb.append('\n');

    }


    private  String formatState(String state)
    {
        return state.replace("10", "A").replace("11", "B").replace(" ", "");
    }
    
  
    public Node dfsSolver(String state)
    {
        Stack<Node> open = new Stack<Node>();
        ArrayList<Node> closed = new ArrayList<>();

        Node initialNode = new Node(state, null, Actions.None, Heuristics.None);

        open.push(initialNode);

        
        while(!open.empty())
        {
            Node current = open.pop();

            if(current.isGoal())
            {
                System.out.println("Found!");
                return current;
            }
            if(current.getDepth() > 8 )
                continue;

            closed.add(current);



            ArrayList<Node> children = current.generateChildren();

            for(Node n : children)
            {
                if (!stateExists(closed, open, n)) {
                    current.addChild(n);
                    open.push(n);
                }

            }

        }

        return null;
    }


    private  Node bfsSolver(String state, Heuristics heuristic)
    {
        PriorityQueue<Node> open = new PriorityQueue<Node>((a,b) ->
        {
            if( a.getCost() == b.getCost())
                return a.getAction().compareTo(b.getAction());

            return a.getCost().compareTo(b.getCost());
        });

        ArrayList<Node> closed = new ArrayList<>();

        Node initialNode = new Node(state, null, Actions.None, heuristic);

        open.add(initialNode);


        while(!open.isEmpty())
        {
            Node current = open.poll();

            if(current.isGoal())
            {
                System.out.println("Found!");
                return current;
            }

            closed.add(current);


            ArrayList<Node> children = current.generateChildren();

//            for(Node n : children)
//            {
//                if (!stateExists(closed, open, n)) {
//                    current.addChild(n);
//                    open.add(n);
//                }
//
//            }
            
            for(int i = children.size() -1; i>=0; i--)
            {
            	Node n  = children.get(i);
                if (!stateExists(closed, open, n)) {
                    current.addChild(n);
                    open.add(n);
                }

            }

        }

        return null;
    }


    private  Node aStartSolver(String state, Heuristics heuristic)
    {
        return bfsSolver(state, heuristic);
    }
    
    
    
    public static void Run(String state)
    {
    	PuzzleSolver s = new PuzzleSolver();
    	state = s.formatState(state);
    	s.WriteOutput("puzzleDFS.txt", s.dfsSolver(state));
    	
    	s.WriteOutput("puzzleBFS-h1.txt", s.bfsSolver(state, Heuristics.DatabasePattern));
    	s.WriteOutput("puzzleBFS-h2.txt", s.bfsSolver(state, Heuristics.Manhattan));
//    	s.WriteOutput("puzzleBFS-h4.txt", s.bfsSolver(state, Heuristics.LinearConflict));
    	s.WriteOutput("puzzleBFS-h3.txt", s.bfsSolver(state, Heuristics.Hamming));
    	
    	s.WriteOutput("puzzleAs-h1.txt", s.aStartSolver(state, Heuristics.DatabasePatternA));
    	s.WriteOutput("puzzleAs-h2.txt", s.aStartSolver(state, Heuristics.ManhattanA));
//    	s.WriteOutput("puzzleAs-h4.txt", s.aStartSolver(state, Heuristics.LinearConflict));
    	s.WriteOutput("puzzleAs-h3.txt", s.aStartSolver(state, Heuristics.HammingA));
    	
    	
    }
    
    private void WriteOutput(String filename, Node n)
    {
    	try {
			FileWriter writer = new FileWriter(filename, false);
			String content = n == null ? "There is no solution!" : solutionToString(n) ;
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    public void generatePatternDatabase() {
        Node initialNode = new Node(Constants.GoalState, null, Actions.None, Heuristics.None);

        Queue<Node> open = new LinkedList<>();
        HashMap<String, Integer> patterns = new HashMap<>();

        ArrayList<Node> closed = new ArrayList<>();

        open.add(initialNode);

        int d = 0;
        while (!open.isEmpty()) {


            Node current = open.poll();

            String s = current.getState()
                    .replace("7", "*")
                    .replace("8", "*")
                    .replace("9", "*")
                    .replace("A", "*")
                    .replace("B", "*");

            if(patterns.containsKey(s))
                continue;

            closed.add(current);

            patterns.put(s, current.getDepth());


            ArrayList<Node> children = current.generateChildren();

            for (Node n : children) {
                if (!stateExists(closed, open, n)) {
                    current.addChild(n);
                    open.add(n);
                }

            }

            System.out.println(patterns.size());
            if(patterns.size() > 100000)
                break;
        }

        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("patterns.txt"), "utf-8"))) {


            for (Map.Entry<String, Integer> entry : patterns.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
