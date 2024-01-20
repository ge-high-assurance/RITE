#! /usr/bin/env bash

LOG=/Users/davidcok/Desktop/log

echo STARTING >> $LOG

input=`cat -`

orig=`echo $input | sed -e 's/^.*<form[^>]*>//' -e 'sx</form>.*$xx'`

echo ORIG "$orig" >> $LOG

if echo "$input" | grep -q connection  ; then
## FIRST TIME

echo "zzzzz"

else 
## NOT FIRST TIME

expr=`echo "$orig" | sed -e 's/^.*<control[^>]*>//g' | sed -e 'sx</control>.*$xx'`
echo EXPR "$expr" >> $LOG

res=`echo $expr`
echo RES "$res" >> $LOG

echo "zzzzz"

fi

echo "TERMINATING" >> $LOG

