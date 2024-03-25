#! /usr/bin/env bash

input=`cat -`

orig=`echo "$input" | sed -e 's/^.*<form [^>]*>//' -e 'sx</form>.*$xx'`

if ! echo "$input" | grep -q control  ; then
## FIRST TIME

echo "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><form workflow=\"Checkbox\"><box><control type=\"checkbox\" >Check me</control></box></form>"

else 
## NOT FIRST TIME

echo ORIG "$orig" >> /Users/davidcok/Desktop/log

if echo "$orig" | sed -e 's/.*<control//g' | grep -q checked ; then
   expr='checked="checked"'
   lab="Toggle checked"
else
   expr=""
   lab="Toggle not checked"
fi
echo EXPR "$expr" >> /Users/davidcok/Desktop/log

echo "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><form workflow=\"Checkbox\">$orig <box><control type=\"label\">$lab</control><control type=\"checkbox\"  $expr >Check me again</control></box></form>"
fi
