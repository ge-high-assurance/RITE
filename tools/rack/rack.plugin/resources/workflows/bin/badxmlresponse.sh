#! /usr/bin/env bash

input=`cat -`

orig=`echo $input | sed -e 's/^.*<form[^>]*>//' -e 'sx</form>.*$xx'`

if echo "$input" | grep -q connection  ; then
## FIRST TIME

echo "zzzzz"

else 
## NOT FIRST TIME

expr=`echo "$orig" | sed -e 's/^.*<control[^>]*>//g' | sed -e 'sx</control>.*$xx'`

res=`echo $expr`

echo "zzzzz"

fi


