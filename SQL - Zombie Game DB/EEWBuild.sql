-- Table: eewc
CREATE TABLE eewc (
	eewcID		INTEGER PRIMARY KEY,
	bloodline	REAL,
	dragonline	REAL
);

-- Table: creature
-- Calculated Columns:
-- factor_BA	SUM(value of items in possession) + age^2 + SUM(value of conquered kingdoms) + health
-- strength		factor_BA * scale_EPIC
-- VP			((Pounds of flesh / 20) * factor_BA) / age
-- intelligence	(VP + factor_ES) * scale_INT
CREATE TABLE creature (
	cID			INTEGER PRIMARY KEY,
	eewcID		INTEGER,	-- Epic Evil World Coordinates. For massive creatures, refer to center of mass
	factor_ES	REAL,		-- Einstein Factor
	health		INTEGER,
	scale_EPIC	REAL,
	scale_INT	REAL,
	bID			INTEGER,	-- Building/Nature creature resides in
	FOREIGN KEY (eewcID) REFERENCES eewc (eewcID),
	FOREIGN KEY (bID) REFERENCES building (bID)
);

-- Table: human
-- Human VP are calculated differently
-- hVP		SUM(scale_EPIC of creatures killed)
CREATE TABLE human (
	hID			INTEGER PRIMARY KEY,
	hFirst		TEXT NOT NULL,
	hLast		TEXT NOT NULL,
	slain		INTEGER,
	marshID		INTEGER, -- ID of the Lord Marshall the human serves
	isHunter	BOOLEAN,
	FOREIGN KEY (hID) REFERENCES creature (cID),
	FOREIGN KEY (marshID) REFERENCES human (hID)
);

-- Table: zombie
-- Zombie VP are calculated differently
-- zVP		(VP) + oldVP
CREATE TABLE zombie (
	zID				INTEGER PRIMARY KEY,
	zombieName		TEXT UNIQUE NOT NULL,
	zVirus			TEXT NOT NULL,
	fleshConsumed	REAL,
	brainsConsumed	REAL,
	oldVP			REAL, -- VP from previous life as human
	kingID			INTEGER,
	FOREIGN KEY (zID) REFERENCES creature (cID),
	FOREIGN KEY (kingID) REFERENCES zombie (zID)
);

-- Table: pack
-- Packs are considered creatures because they act as a single creature during a fight
-- Also because it's way easier to make the database this way
CREATE TABLE pack (
	packID	INTEGER PRIMARY KEY,
	FOREIGN KEY (packID) REFERENCES creature (cID)
);

-- Table: vampire
-- Vampire VP are calculated differently
-- vVP		((drained * this.factor_BA) / age) + oldVP
CREATE TABLE vampire (
	vID		INTEGER PRIMARY KEY,
	vName	TEXT UNIQUE NOT NULL,
	drained	INTEGER,
	oldVP	REAL,
	FOREIGN KEY (vID) REFERENCES creature (cID)
);

-- Table: dragon
CREATE TABLE dragon (
	dID		INTEGER PRIMARY KEY,
	dName	TEXT,
	dBrood	TEXT,
	dTitle	TEXT,
	FOREIGN KEY (dID) REFERENCES creature (cID)
);

-- Table: ghoul
CREATE TABLE ghoul (
	gID		INTEGER PRIMARY KEY,
	gName	TEXT,
	FOREIGN KEY (gID) REFERENCES creature (cID)
);

-- Table: witch
CREATE TABLE witch (
	wID		INTEGER PRIMARY KEY,
	wFirst	TEXT,
	wLast	TEXT,
	mPower	REAL,
	FOREIGN KEY (wID) REFERENCES creature (cID)
);

-- Table: battle
CREATE TABLE battle (
	battleID	INTEGER PRIMARY KEY,
	time		INTEGER,
	eewcID		INTEGER,
	Losses		INTEGER,
	FOREIGN KEY (eewcID) REFERENCES eewc (eewcID)
);

-- Relation: slay
CREATE TABLE slay (
	slayerID	INTEGER,
	slainID		INTEGER,
	battleID	INTEGER,
	FOREIGN KEY (battleID) REFERENCES battle (battleID),
	FOREIGN KEY (slayerID) REFERENCES creature (cID),
	FOREIGN KEY (slainID) REFERENCES creature (cID),
	PRIMARY KEY (slayerID, slainID)
);

-- Relation: member
CREATE TABLE member (
	zID			INTEGER,
	packID		INTEGER,
	FOREIGN KEY (zID) REFERENCES zombie (zID),
	FOREIGN KEY (packID) REFERENCES pack (packID),
	PRIMARY KEY (zID, packID)
);

-- Table: building
CREATE TABLE building (
	bID		INTEGER PRIMARY KEY
);

-- Table: nature
CREATE TABLE nature (
	nType	TEXT,
	bID		INTEGER,
	FOREIGN KEY (bID) REFERENCES building (bID),
	PRIMARY KEY (nType, bID)
);

-- Table: nullBuild
-- Used to describe the nature creatures are in while outside buildings
CREATE TABLE nullBuild (
	nbID	INTEGER PRIMARY KEY,
	FOREIGN KEY (nbID) REFERENCES building (bID)
);

-- Table: kingdom
-- Calculated Columns:
-- kValue	(kPop / kMaxPop) * kWorth
CREATE TABLE kingdom (
	kID			INTEGER PRIMARY KEY,
	rulerID		INTEGER,
	kName		TEXT,
	kPop		INTEGER,
	kMaxPop		INTEGER,
	kWorth		INTEGER,
	FOREIGN KEY (kID) REFERENCES building (bID)
);

-- Table: castle
CREATE TABLE castle (
	cID		INTEGER PRIMARY KEY,
	kID		INTEGER,
	FOREIGN KEY (cID) REFERENCES building (bID),
	FOREIGN KEY (kID) REFERENCES kingdom (kID)
);

-- Table: room
CREATE TABLE room (
	rID		INTEGER PRIMARY KEY,
	cID		INTEGER,
	FOREIGN KEY (rID) REFERENCES building (bID),
	FOREIGN KEY (cID) REFERENCES castle (cID)
);

-- Table: pathway
CREATE TABLE pathway (
	pID		INTEGER PRIMARY KEY,
	inID	INTEGER,
	outID	INTEGER,
	nature	INTEGER,	-- negative for harming paths, 0 for neutral paths, positive for healing paths
						-- size of int references intensity of path nature
	twoWay	BOOLEAN,	-- whether or not a path goes both ways
	FOREIGN KEY (inID) REFERENCES room (rID),
	FOREIGN KEY (outID) REFERENCES room (rID)
);
-- Table: hut
CREATE TABLE hut (
	hID		INTEGER PRIMARY KEY,
	FOREIGN KEY (hID) REFERENCES building (bID)
);

-- Table: item
CREATE TABLE item (
	iID		INTEGER PRIMARY KEY,
	worth	REAL
);

-- Table: weapon
CREATE TABLE weapon (
	wID		INTEGER PRIMARY KEY,
	attack	REAL,
	FOREIGN KEY (wID) REFERENCES item (iID)
);

-- Table: artifact
CREATE TABLE artifact (
	aID		INTEGER PRIMARY KEY,
	effect	TEXT,
	FOREIGN KEY (aID) REFERENCES item (iID)
);

-- Relation: inventory
CREATE TABLE inventory (
	iID		INTEGER,
	cID		INTEGER,
	FOREIGN KEY (iID) REFERENCES item (iID),
	FOREIGN KEY (cID) REFERENCES creature (cID)
);
