/*
* CS 3250
* Project 03
* Author: Jacob Christensen
* Date: 2014-09-23
*/

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.event.*;

public class Adventure {
	
    public static void main(String[] args) {
		
		
		MainFrame main;
		GameChar player = new GameChar();
		// Initialization
		if (args.length < 1)
		{
			System.out.println("No file submitted");
			System.exit(0);
		}
		
		else
		{
			main = new MainFrame(args[0], player);
			main.setVisible(true);
			
		}
		
    }	

}

class MainFrame extends JFrame {
	
	private static final int DEFAULT_WIDTH = 480;
	private static final int DEFAULT_HEIGHT = 480;
	private static final int VIEWDIST = 2;
	private static final int GRID_WIDTH = (2 * VIEWDIST) + 1;
	private static final int GRID_HEIGHT = GRID_WIDTH;
	private static final int GRID_GAP = 1;
	private static final int LOG_SIZE = 360;
	protected Scanner inFile = null;
	protected GameChar player;
	protected Map map;
	// terrainIcons is the image version of Map.terrainList and must be filled with data in matching positions
	protected ArrayList<Image> terrainIcons;
	protected int iconWidth, iconHeight, realWidth, realHeight;
	protected boolean recentSave;
	protected char PLAYER_CHAR = '1';
	protected String savePath = null;
	
	protected JPanel mPanel;
	protected MapPanel iPanel;
	protected ControlPanel cPanel, lPanel, gPanel;
	protected JScrollPane logPane;
	protected JTextArea logWindow;
	protected JTextField inputField;
	protected JButton bOpen, bSave, bHelp, bQuit;
	protected JFileChooser chooser;
	protected ArrayList<ImgPanel> mapGrid;
	protected JSplitPane sPane;
	
	MainFrame(String path, GameChar p)
	{
		// loading in the map file
		map = new Map(path);
		terrainIcons = new ArrayList<Image>();
		iconWidth = map.imgX();
		iconHeight = map.imgY();
		player = p;
		player.goToMap(map);
		recentSave = false;
		
		for (int i = 0; i < map.terrainList.size(); i++)
		{
			terrainIcons.add(new ImageIcon(map.terrainList.get(i)[2]).getImage());
		}
		
		// yelling at Swing until it gives you the right size. ALL CAPS.
		realWidth = (iconWidth * GRID_WIDTH + LOG_SIZE);
		realHeight = (iconHeight * GRID_WIDTH);
		setSize(realWidth, realHeight);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Adventure Game");
		
		mPanel = new MainPanel();
		mPanel.setOpaque(true);
		this.add(mPanel);
		
	}	
	
	
	// Contains scrolling text output of game (center) and input field (south/bottom)
	// Put in East/Right of main frame
	class MainPanel extends JPanel implements ActionListener {
	
		public MainPanel()
		{
		
			// drawing the GUI
			this.setLayout(new BorderLayout());
			
			chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File("."));
			
			lPanel = new ControlPanel();
			lPanel.setOpaque(true);
			lPanel.setLayout(new BorderLayout());
			//add(lPanel, BorderLayout.EAST);
			
			gPanel = new ControlPanel();
			gPanel.setOpaque(true);
			gPanel.setLayout(new BorderLayout());
			
			mapGrid = new ArrayList<ImgPanel>();
			iPanel = new MapPanel();
			iPanel.setOpaque(true);
			iPanel.setLayout(new GridLayout(GRID_WIDTH, GRID_HEIGHT, GRID_GAP, GRID_GAP));
			gPanel.add(iPanel, BorderLayout.CENTER);
			
			sPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gPanel, lPanel);
			sPane.setOneTouchExpandable(false);
			sPane.setDividerLocation(realWidth - (LOG_SIZE + GRID_WIDTH * GRID_GAP));
			add(sPane, BorderLayout.CENTER);
			
			cPanel = new ControlPanel();
			cPanel.setOpaque(true);
			lPanel.add(cPanel, BorderLayout.NORTH);
			
			bOpen = new JButton("Open");
			bOpen.setActionCommand("open");
			bOpen.addActionListener(this);
			bOpen.setToolTipText("Open a saved game");
			cPanel.add(bOpen);
			
			bSave = new JButton("Save");
			bSave.setActionCommand("save");
			bSave.addActionListener(this);
			bSave.setToolTipText("Save the game");
			cPanel.add(bSave);
			
			bHelp = new JButton("Help");
			bHelp.setActionCommand("help");
			bHelp.addActionListener(this);
			bHelp.setToolTipText("Show Commands");
			cPanel.add(bHelp);
			
			bQuit = new JButton("Quit");
			bQuit.setActionCommand("quit");
			bQuit.addActionListener(this);
			bQuit.setToolTipText("Exit the game");
			cPanel.add(bQuit);
			
			logWindow = new JTextArea();
			logWindow.setEditable(false);
			logWindow.setLineWrap(true);
			logWindow.setWrapStyleWord(true);
			logPane = new JScrollPane(logWindow);
			lPanel.add(logPane, BorderLayout.CENTER);
			
			inputField = new JTextField();
			inputField.setActionCommand("command");
			inputField.addActionListener(this);
			lPanel.add(inputField, BorderLayout.SOUTH);
		
		}
		
		
	
		public void actionPerformed(ActionEvent e)
		{
			String inputRaw;
			String[] input;
		
			if ("command".equals(e.getActionCommand()))
			{
				inputRaw = inputField.getText();
				if (inputRaw.length() < 1)
					{
						logPrint("No command entered");
					}
				inputRaw = inputRaw.toLowerCase();
				input = inputRaw.split(" +");
				
				switch (input[0].charAt(0))
				{
					case 'g':	if (input.length > 1)
								{
									logPrint(player.commandGo(input[1]));
									iPanel.update();
								}
								else
									logPrint("You must specify a direction");
								break;
					case 'i':	logPrint(player.commandInv());
								break;
					case 't':	if (input.length > 1)
									logPrint(player.commandTake(input));
								else
									logPrint("Take what?");
								break;
					case 'd':	if (input.length > 1)
									logPrint(player.commandDrop(input));
								else
									logPrint("Drop what?");
								break;
					case 'l':	logPrint(player.commandLook());
								break;
					case 'q':	exitGame();
								break;
					default:	logPrint("Invalid command: " + inputRaw);
								break;
				}
				
				inputField.setText("");
				recentSave = false;
			}
			else if ("save".equals(e.getActionCommand()))
			{
				saveGame();
			}
			else if ("open".equals(e.getActionCommand()))
			{
				loadGame();
			}
			else if ("help".equals(e.getActionCommand()))
			{
				logPrint("Commands: [G]o, [L]ook, [I]nventory, [T]ake, [D]rop, [Q]uit");
			}
			else
			{
				exitGame();
			}

		}
		
		protected boolean savePrompt(String s)
		{
			if (!recentSave)
			{
				Object[] options = {"Save First",
						s + " without Saving",
						"Cancel"};
				int n = JOptionPane.showOptionDialog(this,
					"You haven't saved recently\n" +
					"Are you sure you want to " + s + " without saving?",
					"You haven't saved recently",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE,
					null,
					options,
					options[2]);
				
				switch (n)
				{
					case JOptionPane.YES_OPTION:	saveGame();
													return true;
													
					case JOptionPane.NO_OPTION:		return true;
													
					case JOptionPane.CANCEL_OPTION:	return false;
													
					case JOptionPane.CLOSED_OPTION:	return false;
													
					default:						return false;
													
				}
			}
			else
			{
				return true;
			}
			
		}
		
		public void exitGame()
		{
			if (savePrompt("Quit"))
			{
				System.exit(0);
			}
		}
		
		public void saveGame()
		{
			if (savePath == null)
			{
				if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
				{
					savePath = chooser.getSelectedFile().getPath();
				}
				else
				{
					return;
				}
			}
			try
			{
				FileOutputStream fOut = new FileOutputStream(savePath);
				ObjectOutputStream oOut = new ObjectOutputStream(fOut);
				oOut.writeObject(player);
				oOut.close();
				logPrint("Saving Game...");
				recentSave = true;
			}
			catch (IOException e)
			{
				logPrint("--ERROR-- UNABLE TO SAVE DATA");
			}
			
		}
		
		public void loadGame()
		{
			if (savePrompt("Load"))
			{
				if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				{
					savePath = chooser.getSelectedFile().getPath();
				}
				else
				{
					return;
				}
				
				try
				{
					FileInputStream fIn = new FileInputStream(savePath);
					ObjectInputStream oIn = new ObjectInputStream(fIn);
					GameChar p = (GameChar) oIn.readObject();
					oIn.close();
					player = p;
					map = new Map(player.getMapPath());
					player.goToMap(map, player.getHPos(), player.getVPos());
					iPanel.update();
					logClear();
					logPrint("Game Loaded");
					recentSave = true;
				}
				catch (IOException e)
				{
					logPrint("--ERROR-- UNABLE TO LOAD DATA");
				}
				catch (ClassNotFoundException e)
				{
					logPrint("--ERROR-- NOT A VALID SAVE FILE");
				}
			}
		}
		
		public void logPrint(String s)
		{
			logWindow.append(s + "\n");
		}
		
		public void logClear()
		{
			logWindow.setText(null);
		}

	}
	
	// Contains image grid of map, refreshes each time a move command returns successfully
	// Put in West/Left of main frame
	class MapPanel extends JPanel {
	
		public MapPanel()
		{
			int i = 0;
			for (int j = (player.getVPos() - VIEWDIST); j <= (player.getVPos() + VIEWDIST); j++)
			{
				for (int k = (player.getHPos() - VIEWDIST); k <= (player.getHPos() + VIEWDIST); k++)
				{
					if (i == (((GRID_HEIGHT * GRID_WIDTH)) / 2))
					{
						mapGrid.add(new ImgPanel(getImg(k,j),getPlayerImg()));
					}
					else
					{
						mapGrid.add(new ImgPanel(getImg(k,j)));
					}
					mapGrid.get(i).setOpaque(true);
					add(mapGrid.get(i));
					i++;
				}
			}
			
		}
		
		public void update()
		{
			int i = 0;
			for (int j = (player.getVPos() - VIEWDIST); j <= (player.getVPos() + VIEWDIST); j++)
			{
				for (int k = (player.getHPos() - VIEWDIST); k <= (player.getHPos() + VIEWDIST); k++)
				{
					mapGrid.get(i).setImg(getImg(k,j));
					mapGrid.get(i).repaint();
					i++;
				}
			}
		}
	}
	
	class ImgPanel extends JPanel {
		
		Image img = null;
		Image img2 = null;
		
		public ImgPanel()
		{
		
		}
		
		public ImgPanel(String s)
		{
			img = new ImageIcon(s).getImage();
		}
		
		public ImgPanel(Image i)
		{
			img = i;
		}
		
		public ImgPanel(Image i, Image i2)
		{
			img = i;
			img2 = i2;
		}
		
		public void paintComponent(Graphics g)
		{
			if (img == null)
			{
				return;
			}
			
			super.paintComponent(g);
			
			g.drawImage(img, 0, 0, iconWidth, iconHeight, this);
			
			if (img2 != null)
			{
				g.drawImage(img2, 0, 0, iconWidth, iconHeight, this);
			}
			
		}
		
		public void setImg(Image i)
		{
			img = i;
		}
		
		public void setImg2(Image i)
		{
			img2 = i;
		}
	}
	
	// Contains buttons for save/open/etc
	// Put in North/Top of main frame
	class ControlPanel extends JPanel 
	{
	
		public ControlPanel()
		{
		
		}
	
	}
	
	private void refreshImgPanel()
	{
		// refreshes the display on the image panel
		// called when a move command returns successfully
	}
	
	private Image getImg(int x, int y)
	{
		return terrainIcons.get(map.getTerrainIndex(x,y));
	}
	
	private Image getPlayerImg()
	{
		return terrainIcons.get(map.getTerrainIndex(PLAYER_CHAR));
	}
}


class Map {

	// REMEMBER - when directly accessing the array, position is [height][width], not the other way around
	private char[][] map;
	final private int MAX_WIDTH, MAX_HEIGHT;
	final private int IMGSIZ_X, IMGSIZ_Y;
	private String itemFilePath;
	private String NO_ITEM = "nothing of interest";
	private ArrayList<Item> itemList = new ArrayList<Item>();
	private Scanner itemFile;
	private String[] itemInput;
	final private String SPLITSTRING = ";";
	protected ArrayList<String[]> terrainList = new ArrayList<String[]>();
	final private String PATH;
	
	public Map(String path)
	{
		Scanner fin = null;
		String input;
		char c;
		char[] temp;
		int x = 0;
		int y = 0;
		PATH = path;
				
		try
		{
			fin = new Scanner(new File(path));	
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File open failed");
			e.printStackTrace();
			System.exit(0);
		}
		
		// getting the map dimensions
		try
		{
			// grabs two numbers, then puts the rest of the line into input to be wiped
			y = fin.nextInt();
			x = fin.nextInt();
			input = fin.nextLine();
		}
		catch (Exception e)
		{
			System.out.println("Map file contains bad data (Map Res)");
			e.printStackTrace();
			System.exit(0);
		}
		
		MAX_WIDTH = x;
		MAX_HEIGHT = y;
		map = new char[MAX_HEIGHT][MAX_WIDTH];
		
		int i = 0;
		while (fin.hasNextLine() & i < MAX_HEIGHT)
		{
			input = fin.nextLine();
			for (int j = 0; j < MAX_WIDTH; j++)
			{
				c = input.charAt(j);
				this.addItem(j,i,c);
			}
			
			i++;
		}
		
		// getting the image dimensions for the tile map
		try
		{
			// grabs two numbers, then puts the rest of the line into input to be wiped
			y = fin.nextInt();
			x = fin.nextInt();
			input = fin.nextLine();
		}
		catch (Exception e)
		{
			System.out.println("Map file contains bad data (Tile Res)");
			e.printStackTrace();
			System.exit(0);
		}
		
		IMGSIZ_X = x;
		IMGSIZ_Y = y;
		
		// opening the item file
		try
		{
			itemFilePath = fin.nextLine();
			itemFile = new Scanner(new File(itemFilePath));
			
			while (itemFile.hasNextLine())
			{
				itemInput = itemFile.nextLine().split(SPLITSTRING);
				itemList.add(new Item(Integer.parseInt(itemInput[0]),Integer.parseInt(itemInput[1]),itemInput[2]));
			}
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Item file open failed");
			e.printStackTrace();
			System.exit(0);
		}
		catch (Exception e)
		{
			System.out.println("Item file contains bad data");
			e.printStackTrace();
			System.exit(0);
		}
		
		// parsing terrain types
		while (fin.hasNextLine())
		{
			input = fin.nextLine();
			terrainList.add(input.split(SPLITSTRING));
		}
	}
	
	public void addItem(int x, int y, char c)
	{
		if ((x < MAX_WIDTH) & (x >= 0) & (y < MAX_HEIGHT) & (y >= 0))
			map[y][x] = c;
	}
	
	public void printMap()
	{
		int i;
		for (i = 0; i < MAX_HEIGHT; i++)
		{
			for (int j = 0; j < MAX_WIDTH; j++)
			{
				System.out.print(map[i][j]);
			}
			System.out.print('\n');
		}
		System.out.println("----------------");
		for (i = 0; i < itemList.size(); i++)
		{
			System.out.println(itemList.get(i));
		}
		System.out.println("----------------");
		for (i = 0; i < terrainList.size(); i++)
		{
			System.out.println(Arrays.deepToString(terrainList.get(i)));
		}
	}
	
	// returns the terrain at the given location
	// returns '-' if the location is outside the map area
	public char getTerrainSymbol(int x, int y)
	{
		if ((x < MAX_WIDTH) & (x >= 0) & (y < MAX_HEIGHT) & (y >= 0))
			return map[y][x];
		else
			return '-';
	}
	
	// returns the index position of a given terrain for use in the terrain image list
	// defaults to 0
	public int getTerrainIndex(char c)
	{
		String s = String.valueOf(c);
		for(int i = 0; i < terrainList.size(); i++)
		{
			if (terrainList.get(i)[0].equals(s))
			{
				return i;
			}
		}
		return 0;
	}
	
	public int getTerrainIndex(int x, int y)
	{
		return getTerrainIndex(getTerrainSymbol(x, y));
	}
	
	public String getTerrainPath(int x, int y)
	{
		return terrainList.get(getTerrainIndex(x,y))[2];
	}
	
	public String getTerrain(int x, int y)
	{
		return terrainList.get(getTerrainIndex(x,y))[1];
	}
	
	public boolean takeItem(Item item)
	{
		for (int i = 0; i < itemList.size(); i++)
		{
			if (itemList.get(i).equals(item))
			{
				itemList.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	public String look(int x, int y)
	{
		String output = "";
		for (int i = 0; i < itemList.size(); i++)
		{
			if (x == itemList.get(i).getXpos())
			{
				if (y == itemList.get(i).getYpos())
				{
					output += "\nYou see " + itemList.get(i).toString();
				}
			}
		}
		
		return output;
	}
	
	public void dropItem(Item item)
	{
		itemList.add(item);
	}
	
	public int maxHeight()
	{
		return MAX_HEIGHT - 1;
	}
	
	public int maxWidth()
	{
		return MAX_WIDTH - 1;
	}
	
	public int imgX()
	{
		return IMGSIZ_X;
	}
	
	public int imgY()
	{
		return IMGSIZ_Y;
	}
	
	public String getPath()
	{
		return PATH;
	}
	
	
	public void DEBUG()
	{
		System.out.println("--DEBUG COMMAND--");
	}
}

class GameChar implements Serializable {
	
	private transient Map map;
	private int vPos, hPos;
	private ArrayList<Item> inventory;
	private String mapPath;
	
	// always-execute block
	{
		inventory = new ArrayList<Item>();
	}
	
	public GameChar()
	{
		map = null;
		vPos = hPos = 0;
	}
	
	public GameChar(Map m)
	{
		map = m;
		vPos = hPos = 0;
		mapPath = map.getPath();
	}
	
	public GameChar(Map m, int x, int y)
	{
		map = m;
		hPos = x;
		vPos = y;
		mapPath = map.getPath();
	}

	public String commandInv()
	{
		String output = "You are carrying ";
		
		if (inventory.size() <= 0)
		{
			output += "nothing";
		}
		else
		{
			for (int i = 0; i < inventory.size(); i++)
			{
				output += inventory.get(i);
				output += "\n";
			}
			// removes trailing newline
			output = output.substring(0, output.length() - 1);
		}
		
		return output;
	}
    
	public String commandGo(String dir)
	{
		switch (dir.charAt(0))
		{
			case 'n':	if (vPos > 0)
						{
							vPos--;
							return "Moving north...";							
						}
						else
							return "You can't go that far north";
						//break;
			case 's':	if (vPos < map.maxHeight())
						{
							vPos++;
							return "Moving south...";							
						}
						else
							return "You can't go that far south";
						//break;
			case 'e':	if (hPos < map.maxWidth())
						{
							hPos++;
							return "Moving east...";							
						}
						else
							return "You can't go that far east";
						//break;
			case 'w':	if (hPos > 0)
						{
							hPos--;
							return "Moving west...";							
						}
						else
							return "You can't go that far west";
						//break;
			default:	return "You can't go that way";
						//break;
		}
		
		//return "You broke something";
	}
	
	public String commandLook()
	{
		String output = "You are at location " + hPos + "," + vPos + " in terrain " + map.getTerrain(hPos, vPos);
		output += map.look(hPos, vPos);
		return output;
	}
	
	public String commandTake(String[] sRaw)
	{
		String s = "";
		Item item;
		for (int i = 1; i < sRaw.length; i++)
			s = s + sRaw[i] + " ";
		// removes the trailing space
		s = s.substring(0, s.length() - 1);
		
		item = new Item(hPos, vPos, s);
		
		if (map.takeItem(item))
		{
			item.setPos(-1, -1);
			inventory.add(item);
			return "You took " + item.toString();
		}
		else
		{
			return "You can't take that";
		}
		
	}
	
	public String commandDrop(String[] sRaw)
	{
		String s = "";
		Item item;
		for (int i = 1; i < sRaw.length; i++)
			s = s + sRaw[i] + " ";
		// removes the trailing space
		s = s.substring(0, s.length() - 1);
		
		item = new Item(hPos, vPos, s);
		
		for (int i = 0; i < inventory.size(); i++)
		{
			if (inventory.get(i).equals(item))
			{
				map.dropItem(item);
				inventory.remove(i);
				return "You dropped " + item.toString();
			}
		}
		
		return "You can't drop that";
	}
	
	public void goToMap(Map m)
	{
		map = m;
		hPos = 0;
		vPos = 0;
		mapPath = map.getPath();
	}
	
	public void goToMap(Map m, int x, int y)
	{
		map = m;
		hPos = x;
		vPos = y;
		mapPath = map.getPath();
	}
	
	public int getHPos()
	{
		return hPos;
	}
	
	public int getVPos()
	{
		return vPos;
	}
	
	public String getMapPath()
	{
		return mapPath;
	}
	
	private void writeObject(ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();
	}
	
	private void readObject(ObjectInputStream in)
		throws IOException
	{
		try
		{
			in.defaultReadObject();
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Object read failed (Item Class)");
			e.printStackTrace();
			System.exit(0);
		}
	}
}

class Item implements Serializable {

	private int xPos;
	private int yPos;
	private String name;
	
	public Item(int x, int y, String n)
	{
		xPos = x;
		yPos = y;
		name = n;
	}
	
	public Item(String n)
	{
		xPos = -1;
		yPos = -1;
		name = n;
	}
	
	public String toString()
	{
		return name;
	}
	
	public int getXpos()
	{
		return xPos;
	}
	
	public int getYpos()
	{
		return yPos;
	}
	
	public String getName()
	{
		return this.toString();
	}

	public void setPos(int x, int y)
	{
		xPos = x;
		yPos = y;
	}
	
	public boolean equals(Object o)
	{
		if(!(o instanceof Item))
			return false;
		else
		{
			Item item = (Item)o;
			return name.toLowerCase().equals(item.getName().toLowerCase());
		}
	}
	
	private void writeObject(ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();
	}
	
	private void readObject(ObjectInputStream in)
		throws IOException
	{
		try
		{
			in.defaultReadObject();
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Object read failed (Item Class)");
			e.printStackTrace();
			System.exit(0);
		}
	}
}