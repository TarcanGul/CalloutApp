#!/bin/bash

git pull origin master
git add *
if [ -z "$1" ]; then
	git commit -m "Commit"
else
	git commit -m $1
fi

git push origin master

