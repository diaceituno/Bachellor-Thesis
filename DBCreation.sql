create new DATABASE("eval-test");

use `eval-test`;

create TABLE branches(
	branchName VARCHAR(50) not NULL,
	pimary key(branchName);
);

create TABLE polls(
	branchName VARCHAR(50) not NULL,
	pollName varchar(50) not NULL,
	fxml TEXT,
	
	FOREIGN KEY (branchName) REFERENCES branches(branchName);
	primary KEY(branchName,pollName)
);

create TABLE groups(
	branchName VARCHAR(50) NOT NULL,
	groupName VARCHAR(50) not NULL,
	ldapPath VARCHAR(500),	
	FOREIGN KEY(branchName) REFERENCES branches(branchName),
	PRIMARY KEY(branchName, groupName)
);

create TABLE groupspolls(
	branchName VARCHAR(50) not NULL,
	groupName VARCHAR(50) not NULL,
	pollName VARCHAR(50) not NULL,
	
	FOREIGN KEY(branchName, groupName) REFERENCES groups(branchName,groupName),
	PRIMARY KEY(branchName,groupName,pollName)
);

create table users(
	branchName VARCHAR(50) not NULL,
	groupName VARCHAR(50) not NULL,
	userName VARCHAR(100) not NULL,
	
	FOREIGN KEY(branchName,groupName) REFERENCES groups(branchName,groupName) on DELETE CASCADE,
	PRIMARY KEY(branchName,groupName,userName);
);

create table userpolls(
	branchName VARCHAR(50) not NULL,
	groupName VARCHAR (50) not NULL,
	userName VARCHAR(100) not NULL,
	pollName VARCHAR(50) not NULL,
	
	FOREIGN KEY(branchName,groupName,userName) REFERENCES users(branchName,groupName,pollName),
	FOREIGN KEY (branchName, pollName) REFERENCES polls(branchName,pollName),
	PRIMARY KEY(branchName,groupName,userName,pollName)
);
