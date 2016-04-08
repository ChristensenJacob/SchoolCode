/*
Jacob Christensen
CS2550
*/

var CONST_MINE = -1;
var CONST_FLAG = -2;
var CONST_BLNK = -3;

function tile() {
	this.mine = false;
	this.revealed = false;
	this.flagged = false;
	this.count = 0;
	//this.toJSON = function() {
	//	return {"mine":this.mine, "revealed":this.revealed, "flagged":this.flagged, "count":this.count};
	//}
}

function minesweeper(X,Y,M) {
	// VARIABLES
	this.x = X;
	this.y = Y;
	this.m = M;
	this.grid = {};
	var i = 0;
	var j = 0;
	this.numTiles = 0;
	this.numMines = 0;
	this.numFlags = 0;
	this.gameWin = 0;
	
	// FUNCTIONS
	this.importJSON = function(obj) {
		this.x = obj["x"];
		this.y = obj["y"];
		this.m = obj["m"];
		this.numTiles = obj["numTiles"];
		this.numMines = obj["numMines"];
		this.numFlags = obj["numFlags"];
		this.gameWin = obj["gameWin"];
		this.grid = {};
		for (var i = 0; i < this.x; i++)
		{
			grid[i] = {};
			for (var j = 0; j < this.y; j++)
				grid[i][j] = obj["grid"][i][j];
		}
	}
	
	this.toJSON = function() {
		out = {"x":this.x, "y":this.y, "m":this.m, "numTiles":this.numTiles, "numMines":this.numMines, "numFlags":this.numFlags, "gameWin":this.gameWin};
		out["grid"] = {};
		for (var i = 0; i < this.x; i++)
		{
			out["grid"][i] = {};
			for (var j = 0; j < this.y; j++)
				out["grid"][i][j] = grid[i][j];
		}
		return out;
	}
	
	this.setMine = function(a,b) {
		if (grid[a][b].mine)
			return false;
		grid[a][b].mine = true;
		var k = 0;
		var l = 0;
		for (k = -1; k <= 1; k++)
		{
			for (l = -1; l <= 1; l++)
			{
				if ((k != 0 || l != 0) && (a + k < this.x) && (a + k >= 0) && (b + l < this.y) && (b + l >= 0))
					grid[a + k][b + l].count = grid[a + k][b + l].count + 1;
			}
		}
		this.numTiles--;
		this.numMines++;
		return true;
	}
	
	this.revealTile = function(a,b) {
		if (grid[a][b].revealed)
			return grid[a][b].count;
		if (grid[a][b].flagged)
			return CONST_FLAG;
		grid[a][b].revealed = true;
		if (grid[a][b].mine)
		{
			this.gameWin = -1;
			return CONST_MINE;
		}
		else
		{
			if (grid[a][b].count == 0)
			{
				var k = 0;
				var l = 0;
				for (k = -1; k <= 1; k++)
				{
					for (l = -1; l <= 1; l++)
					{
						if ((k != 0 || l != 0) && (a + k < this.x) && (a + k >= 0) && (b + l < this.y) && (b + l >= 0))
						{
							if (!grid[a + k][b + l].revealed && !grid[a + k][b + l].flagged)
							{
								this.revealTile(a + k, b + l);
							}
						}
					}
				}
			}
			this.numTiles--;
			this.victoryCheck();
			return grid[a][b].count;
		}
	}
	
	this.drawTile = function(a,b) {
		if (grid[a][b].revealed)
		{
			if (grid[a][b].mine)
				return CONST_MINE;
			else
				return grid[a][b].count;
		}
		else if (grid[a][b].flagged)
			return CONST_FLAG;
		else
			return CONST_BLNK;
	}
	
	this.flag = function(a,b) {
		if (!grid[a][b].revealed)
		{
			if (grid[a][b].flagged)
			{
				grid[a][b].flagged = false;
				this.numFlags--;
				if (grid[a][b].mine)
					this.numMines++;
			}
			else
			{
				grid[a][b].flagged = true;
				this.numFlags++;
				if (grid[a][b].mine)
				{
					this.numMines--;
					this.victoryCheck();
				}
			}
		}
	}
	
	this.testgrid = function() {
		var i = 0;
		for (i = 0; i < 10; i++)
			this.revealTile(Math.floor(Math.random()*this.x),Math.floor(Math.random()*this.y));
		for (i = 0; i < 10; i++)
			this.flag(Math.floor(Math.random()*this.x),Math.floor(Math.random()*this.y));
	}
	
	this.victoryCheck = function() {
		if (this.numMines == 0 && this.numTiles == 0)
			this.gameWin = 1;
	}
	
	// END OF FUNCTIONS, BEGIN MAIN CONSTRUCTOR
	for (i = 0; i < this.x; i++)
	{
		grid[i] = {};
		for (j = 0; j < this.y; j++)
		{
			grid[i][j] = new tile();
			this.numTiles++;
		}
	}
	i = 0;
	j = 0;
	while (i < this.m && j < 10)
	{
		if (this.setMine(Math.floor(Math.random()*this.x),Math.floor(Math.random()*this.y)))
		{
			i++;
			j = 0;
		}
		else
			j++;
	}
	this.m = this.numMines;
}