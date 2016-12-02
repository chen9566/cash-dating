#!/usr/bin/env bash

if [[ ! $1 ]]; then
    echo "$0 TargetHome"
    exit 1
fi
if [[ ! -d $1 ]]; then
    echo "$1 must be a folder"
    exit 1
fi
if [[ ! -w $1 ]]; then
    echo "can not write into $1"
    exit 1
fi

if [[ ! -e $1/jquery-3.1.1.min.js ]]; then
    wget http://code.jquery.com/jquery-3.1.1.min.js -O $1/jquery-3.1.1.min.js
fi
if [[ ! -e $1/jquery-ui.css ]]; then
    wget http://code.jquery.com/ui/1.12.1/themes/eggplant/jquery-ui.css -O $1/jquery-ui.css
fi
if [[ ! -e $1/jquery-ui.min.js ]]; then
    wget http://code.jquery.com/ui/1.12.1/jquery-ui.min.js -O $1/jquery-ui.min.js
fi
if [[ ! -e $1/bootstrap.min.css ]]; then
    wget http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css -O $1/bootstrap.min.css
fi
if [[ ! -e $1/bootstrap-theme.min.css ]]; then
    wget http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css -O $1/bootstrap-theme.min.css
fi
if [[ ! -e $1/bootstrap.min.js ]]; then
    wget http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js -O $1/bootstrap.min.js
fi
if [[ ! -e $1/bootstrap-table.min.css ]]; then
    wget http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.0/bootstrap-table.min.css -O $1/bootstrap-table.min.css
fi
if [[ ! -e $1/bootstrap-table.min.js ]]; then
    wget http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.0/bootstrap-table.min.js -O $1/bootstrap-table.min.js
fi
if [[ ! -e $1/bootstrap-table-locale-all.min.js ]]; then
    wget http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.0/bootstrap-table-locale-all.min.js -O $1/bootstrap-table-locale-all.min.js
fi
if [[ ! -e $1/bootstrap-table-editable.min.js ]]; then
    wget http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.0/extensions/editable/bootstrap-table-editable.min.js -O $1/bootstrap-table-editable.min.js
fi
if [[ ! -e $1/bootstrap-table-export.min.js ]]; then
    wget http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.0/extensions/export/bootstrap-table-export.min.js -O $1/bootstrap-table-export.min.js
fi



