#! /usr/bin/env bash

LOG=/Users/davidcok/Desktop/log

echo STARTING >> $LOG

input=`cat -`

orig=`echo "$input" | sed -e 's/^.*<form[^>]*>//' -e 'sx</form>.*$xx'`

echo ORIG "$orig" >> $LOG

if echo "$input" | grep -q connection  ; then
## FIRST TIME

echo "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><form workflow=\"Echosh\"><box><control type=\"textbox\" label=\"Enter an expression to echo:\"></control></box></form>"

else 
## NOT FIRST TIME

expr=`echo "$orig" | sed -e 's/^.*<control[^>]*>//g' | sed -e 'sx</control>.*$xx'`
echo EXPR "$expr" >> $LOG

res=`echo "$expr"`
echo RES "$res" >> $LOG

echo "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><form workflow=\"Echosh\">$orig <box><control type=\"label\">$res</control><control type=\"textbox\" label=\"Enter an expression to echo:\"></control></box></form>"

fi

echo "TERMINATING" >> $LOG

