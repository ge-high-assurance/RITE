#! /usr/bin/env bash

##LOG=/Users/davidcok/Desktop/log

##echo STARTING >> $LOG

input=`cat -`

orig=`echo "$input" | sed -e 's/^.*<form workflow=\"Delay\">//' -e 'sx</form>.*$xx'`


if ! echo "$input" | grep -q textbox  ; then
## FIRST TIME

echo "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><form workflow=\"Delay\"><box><control type=\"textbox\" lines=\"3\" label=\"Enter number of seconds to delay:\"></control></box></form>"

else 
## NOT FIRST TIME

expr=`echo "$orig" | sed -e 's/^.*<control[^>]*>//g' | sed -e 'sx</control>.*$xx'`
##echo EXPR "$expr" >> $LOG

if [ "$expr" -lt "0" ]; then

echo "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><form workflow=\"Delay\" ><box><control type=\"label\" error=\"error\">The value given is negative: $expr</control><control type=\"textbox\" lines=\"3\" label=\"Enter number of seconds to delay:\"></control></box></form>"

else 

error=

sleep $expr
echo "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><form workflow=\"Delay\" ><box><control type=\"label\">$expr</control><control type=\"textbox\" lines=\"3\" label=\"Enter number of seconds to delay:\"></control></box></form>"

fi


fi

##echo "TERMINATING" >> $LOG

