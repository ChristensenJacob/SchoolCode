/*
Jacob Christensen
CS2550
Images from OpenClipart.org
*/

var gameState = null;
var flagging;
var Gx, Gy, Gm;

function validGrid(a) {
	var MINGRID = 0;
	var MAXGRID = 100;
	return !isNaN(a) && MINGRID < a && a < MAXGRID;
}

function validMine(a) {
	var MINMINE = 0;
	var MAXMINE = 1000;
	return !isNaN(a) && MINMINE < a && a < MAXMINE;
}

function callGrid() {
	Gx = "10";
	Gy = "10";
	Gm = "10";
	var input = "";
	flagging = true;
	input = document.getElementById("grXVal").value
	if (validGrid(input))
		Gx = input;
	input = document.getElementById("grYVal").value
	if (validGrid(input))
		Gy = input;
	input = document.getElementById("grMVal").value
	if (validMine(input))
		Gm = input;
	gameState = new minesweeper(Gx,Gy,Gm);
	gridSize(Gx);
	grid(Gx,Gy);
	flagHandler();
	divTrack(-2,-2);
}

function gridSize(x) {
	var tile = 22;
	var div = document.getElementById("grGrid");
	var siz = 800;
	if (tile * x > siz)
		siz = tile * x;
	siz = siz + "px";
	div.style.width = siz;
}

function grid(x,y) {
	var gridDiv = document.getElementById("grGrid");
	var html = "";
	var i = 0;
	var j = 0;
	var gr = 0;
	
	html += "<p>Total Mines: <span class=\"red\">";
	html += gameState.m;
	html += "</span> Mines Flagged: <span class=\"blu\">";
	html += gameState.numFlags;
	html += "</span></p>";
	
	// IMPORTANT - This draws row-by-row. j is x-coord, i is y-coord.
	html += "<table><tbody>";
	for (i = 0; i < y; i++)
	{
		html += "<tr>";
		for (j = 0; j < x; j++)
		{
			gr = gameState.drawTile(j,i);
			html += "<td class = \"";
			html += "gr" + gr + "\">";
			switch (gr)
			{
				case -1:
					html += "<img src=\"img/mine.png\" />";
					break;
				case -2:
					html += "<img src=\"img/flag.png\" />";
					break;
				case -3:
					html += "&nbsp";
					break;
				default:
					html += gr;
			}
			html += "</td>";
		}
		html += "</tr>";
	}
	html += "</tbody></table>";

	gridDiv.innerHTML = html;

	cells = document.getElementsByTagName("td");
	for (var i = 0; i < cells.length; i++)
		cells[i].onclick = cellHandler;
}

function cellHandler() {
	var col = this.cellIndex;
	var row = this.parentNode.rowIndex;
	if (flagging)
		gameState.flag(col,row);
	else
		gameState.revealTile(col,row);
	divTrack(col,row);
	updateGame();
}

function divTrack(c,r) {
	switch (c)
	{
		case -1:
			var output = "Game Loaded";
			break;
		case -2:
			var output = "Game Generated";
			break;
		default:
			var output = "cell " + c + "," + r;
			if (flagging)
				output += " was flagged";
			else
				output += " was revealed";
			break;
	}
		
	switch (gameState.gameWin)
	{
		case 0:
			break;
		case -1:
			output += "<br /><img src=\"img/Dead-Banana.png\" width=100 height=100 /><br /><span class = \"red\">DEFEAT</span>";
			break;
		case 1:
			output += "<br /><img src=\"img/Smiling-Banana.png\" width=100 height=100 /><br /><span class = \"blu\">VICTORY</span>";
			break;
	}
	div = document.getElementById("grTrack");
	div.innerHTML = "<p>" + output + "</p>";
}

function blank() {}

function updateGame() {
	
	grid(gameState.x,gameState.y);
	
	switch (gameState.gameWin)
	{
		case 0:
			//normal conditions
			break;
		case -1:
			// lock grid, defeat
			for (var i = 0; i < cells.length; i++)
				cells[i].onclick = blank;
			break;
		case 1:
			// lock grid, victory
			for (var i = 0; i < cells.length; i++)
				cells[i].onclick = blank;
			break;
	}
};

function flagHandler() {
	var div = document.getElementById("grFlag");
	var html = "";
	if (flagging)
	{
		flagging = false;
		html = "<p>You are now <b><span class=\"red\">Revealing Tiles</span></b><br /><button onclick=\"flagHandler()\">Place Flags</button></p>";		
	}
	else
	{
		flagging = true;
		html = "<p>You are now <i><span class=\"blu\">Placing Flags</span></i><br /><button onclick=\"flagHandler()\">Reveal Tiles</button></p>";
	}
	div.innerHTML = html;
};

function grSave() {
	if (gameState !== null)
	{
		var save = prompt("Enter name of game to save:", "default");
		if (save.length < 1)
			save = "default";
		save = "cs2550_10479695_final_" + save;
		if (localStorage.getItem(save) !== null)
		{
			if (!confirm("Save exists, overwrite?"))
				return;
		}
		var obj = JSON.stringify(gameState);
		localStorage.setItem(save, obj);
		alert("Game Saved");
	}
	else
		alert("No Game Data to Save");
}

function grLoad() {
	var save = prompt("Enter name of game to load:", "default");
	if (save.length < 1)
		save = "default";
	save = "cs2550_10479695_final_" + save;
	obj = localStorage.getItem(save);
	if (obj != null)
	{
		// create a blank grid if none exists
		if (gameState == null)
			callGrid();
		gameState.importJSON(JSON.parse(obj));
		gridSize(gameState.x);
		updateGame();
		divTrack(-1,-1);
		alert("Game Loaded");
	}
	else
		alert("No Game Data to Load");
}

function grTest() {
	//CURRENT FUNCTION
	//Load json data from disk
	var xml = new XMLHttpRequest();
	xml.open("GET", "saves/test.json", false);
	xml.send(null);
	var obj = xml.responseText;
	if (obj != null)
	{
		// create a blank grid if none exists
		if (gameState == null)
			callGrid();
		gameState.importJSON(JSON.parse(obj));
		gridSize(gameState.x);
		updateGame();
		divTrack(-1,-1);
		alert("Game Loaded from Disk");
	}
	else
		alert("No Game Data to Load from Disk");
}