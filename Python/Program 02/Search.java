import java.util.*;
import java.io.*;

public class Search {

    public static void main(String[] args) {
        
		if (args.length < 1)
			System.out.println("No file submitted");
		else
		{
			Scanner inFile = null;
			try
			{
				inFile = new Scanner(new File(args[0]));
			}
			catch (FileNotFoundException e)
			{
				System.out.println("File open failed");
				e.printStackTrace();
				System.exit(0);
			}
			
			Grid grid = new Grid(inFile);
			
			grid.printGrid();
			System.out.println();
			grid.printWords();
			
			
		}
    }
    
	
}

class Grid {

	final private int DEFAULT_MAX_WIDTH = 4;
	final private int DEFAULT_MAX_HEIGHT = 4;
	private char[][] grid;
	final private int MAX_WIDTH, MAX_HEIGHT;
	private ArrayList<String> words = new ArrayList<String>();
	private ArrayList<int[]> found = new ArrayList<>();
	private boolean notSearched = true;

	public Grid()
	{
		grid = new char[DEFAULT_MAX_WIDTH][DEFAULT_MAX_HEIGHT];
		MAX_WIDTH = DEFAULT_MAX_WIDTH;
		MAX_HEIGHT = DEFAULT_MAX_HEIGHT;
	}
	
	public Grid(int x)
	{
		grid = new char[x][x];
		MAX_WIDTH = x;
		MAX_HEIGHT = x;
	}
	
	public Grid(int x, int y)
	{
		grid = new char[x][y];
		MAX_WIDTH = x;
		MAX_HEIGHT = y;
	}
	
	public Grid(Scanner fin)
	{
		String input;
		char c;
		char[] temp;
		while (fin.hasNextLine())
		{
			input = fin.nextLine();
			if (input.length() < 1)
				break;
			words.add(input);
		}
		MAX_WIDTH = words.get(0).length();
		MAX_HEIGHT = words.size();
		grid = new char[MAX_WIDTH][MAX_HEIGHT];
		for (int i = 0; i < words.size() & i < MAX_HEIGHT; i++)
		{
			for (int j = 0; j < words.get(i).length() & j < MAX_WIDTH; j++)
			{
				c = words.get(i).charAt(j);
				this.addItem(i,j,c);
			}
		}
		words = new ArrayList<String>();
		while (fin.hasNextLine())
		{
			input = fin.nextLine();
			if (input.length() < 1)
				break;
			this.addWord(input);
		}
	}
	
	public void addItem(int x, int y, char c)
	{
		if ((x < MAX_WIDTH) & (x >= 0) & (y < MAX_HEIGHT) & (y >= 0))
			grid[x][y] = c;
	}
	
	public void printGrid()
	{
		for (int i = 0; i < MAX_HEIGHT; i++)
		{
			for (int j = 0; j < MAX_WIDTH; j++)
			{
				System.out.print(grid[i][j]);
			}
			System.out.print('\n');
		}
	}
	
	public void addWord(String s)
	{
		if (s.length() > 0)
		{
			words.add(s);
			int[] a = {-1,-1,-1,-1};
			found.add(a);
		}
	}
	
	public void printWords()
	{
		if (notSearched)
			this.search();
		
		for (int i = 0; i < words.size(); i++)
		{
			System.out.print(words.get(i));
			if (found.get(i)[0] < 0)
				System.out.print(" not found");
			else
			{
				System.out.print(" found at ");
				System.out.print("start: " + found.get(i)[0] + "," + found.get(i)[1]);
				System.out.print(" end: " + found.get(i)[2] + "," + found.get(i)[3]);
			}
			System.out.print('\n');
		}
	}
	
	public void search()
	{
		for (int i = 0; i < MAX_WIDTH; i++)
		{
			for (int j = 0; j < MAX_HEIGHT; j++)
			{
				for (int k = -1; k <= 1; k++)
				{
					for (int l = -1; l <= 1; l++)
					{
						if (k != 0 | l != 0)
							singleSearch(i,j,k,l);
					}
				}
			}
		}
		notSearched = false;
	}
	
	// four integers
	// x,y are starting position in the grid
	// v,h are the current direction of search
	// (for example: 1,0 means searching horizontally right, -1,-1 means searching diagonally down-left)
	public void singleSearch(int x, int y, int v, int h)
	{
		// pull from the words list
		// check the found list before searching
		// create a new local array with size matching the found list
		// fill it with true or false, depending on if words already found
		// start at given position
		// increment position by i and j, check boundaries
		// check each word in the list before incrementing
		// if a letter doesn't match the word, change the local array of the word's position to false
		// keep a separate marker, if all bools in the local array are false stop searching
		// if a word is found, change the found array to include the position
		
		int startPosX = x;
		int startPosY = y;
		int posX = x;
		int posY = y;
		boolean[] local = new boolean[words.size()];
		boolean keepGoing = true;
		int[] a;
		
		// initialize the local array, to keep track of which words we're searching for
		for (int i = 0; i < found.size(); i++)
		{
			if (found.get(i)[0] == -1)
				local[i] = true;
			else
				local[i] = false;
		}
		
		// compare the letter at the current position to the nth letter in each word
		// if it matches leave local[n] true, otherwise set local[n] false
		// if the current position leaves the grid, set border to false and break the loop
		
		// word position counter
		// note to self, declare this OUTSIDE the loop
		int n = 0;
		
		while (keepGoing)
		{
			// search logic here
			for (int i = 0; i < words.size(); i++)
			{
				if (local[i])
				{
					// checks to see if the word is still findable
					if (grid[posX][posY] != words.get(i).charAt(n))
					{
						local[i] = false;
					}
					// checks to see if the word is finished
					else if (words.get(i).length() == (n + 1)) // using n+1 because the counter will reach the end of the word AFTER the next loop
					{
						local[i] = false;
						a = new int[]{startPosX,startPosY,posX,posY};
						found.set(i,a);
						continue;
					}
				}
			}
			
			// checking to see if we've hit the edge of the grid
			posX += v;
			posY += h;
			if ((posX >= MAX_WIDTH) | (posX < 0) | (posY >= MAX_HEIGHT) | (posY < 0))
			{
				keepGoing = false;
				break;
			}
			
			// checking to see if there are any words left to find in this direction
			keepGoing = false;
			for (int i = 0; i < local.length; i++)
			{
				if (local[i])
					keepGoing = true;
			}
			
			n++;
		}
		
	}
	
	public void DEBUG()
	{
		int[] a = {0,0,0,0};
		found.set(0, a);
	}
}