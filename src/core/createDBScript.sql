create database FMS;
use FMS;
create user java@'%' identified by 'java';
grant all privileges on FMS.* to java@'%';

create table branches(

	branchName varchar(50) not null,
	adPath varchar(200),

	primary key(branchName)
);

create table polls(

	branchName varchar(50) not null,
	pollName varchar(50) not null,
	
	primary key(branchName, pollName),
	foreign key(branchName) references branches(branchName)
);

create table groups(

	branchName varchar(50) not null,
	groupName varchar(50) not null,

	primary key(branchName,groupName),
	foreign key(branchName) references branches(branchName)
);

create table pollspages(
	
	branchName varchar(50) not null,
	pollName varchar(50) not null,
	pageNumber int not null,
	fxml varchar(50),	

	foreign key(branchName, pollName) references polls(branchName, pollName),
	primary Key(pollName, pageNumber)
);

create table groupspolls(
	
	groupName varchar(50) not null,
	pollName varchar(50) not null,
	
	primary key(groupName,pollName)
);
