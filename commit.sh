#!/bin/bash

git pull origin master
git add *
if [ -z "$1" ]; then
	git commit -m "Commited"
else
	git commit -m $*
fi

git push origin master
git show
echo Commit Successful
