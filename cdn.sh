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

#download jquery-ui-1.12.1 http://jqueryui.com/
if [[ ! -e $1/jquery-ui-1.12.1 ]]; then
    wget http://resali.huobanplus.com/cdn/jquery-ui/jquery-ui-1.12.1.eggplant.zip
    unzip jquery-ui-1.12.1.eggplant.zip
    rm jquery-ui-1.12.1.eggplant.zip
    mv jquery-ui-1.12.1.custom $1/jquery-ui-1.12.1
fi


#download bootstrap https://github.com/twbs/bootstrap/releases/download/v3.3.7/bootstrap-3.3.7-dist.zip
if [[ ! -e $1/bootstrap-3.3.7 ]]; then
    wget https://github.com/twbs/bootstrap/releases/download/v3.3.7/bootstrap-3.3.7-dist.zip
    unzip bootstrap-3.3.7-dist.zip
    rm bootstrap-3.3.7-dist.zip
    mv bootstrap-3.3.7-dist $1/bootstrap-3.3.7
fi

#download bootstrap table https://github.com/wenzhixin/bootstrap-table/archive/1.11.0.zip
if [[ ! -e $1/bootstrap-table-1.11.0 ]]; then
    wget https://github.com/wenzhixin/bootstrap-table/archive/1.11.0.zip
    unzip 1.11.0.zip
    rm 1.11.0.zip
    mv bootstrap-table-1.11.0/dist $1/bootstrap-table-1.11.0
    rm -rf bootstrap-table-1.11.0
fi

if [[ ! -e $1/jquery-3.1.1.min.js ]]; then
    wget http://code.jquery.com/jquery-3.1.1.min.js -O $1/jquery-3.1.1.min.js
fi

if [[ ! -e $1/jquery.fileupload.js ]]; then
    wget https://blueimp.github.io/jQuery-File-Upload/js/jquery.fileupload.js -O $1/jquery.fileupload.js
fi

#if [[ ! -e $1/jquery-ui.css ]]; then
#    wget http://code.jquery.com/ui/1.12.1/themes/eggplant/jquery-ui.css -O $1/jquery-ui.css
#fi
#if [[ ! -e $1/jquery-ui.min.js ]]; then
#    wget http://code.jquery.com/ui/1.12.1/jquery-ui.min.js -O $1/jquery-ui.min.js
#fi
#if [[ ! -e $1/bootstrap.min.css ]]; then
#    wget http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css -O $1/bootstrap.min.css
#fi
#if [[ ! -e $1/bootstrap-theme.min.css ]]; then
#    wget http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css -O $1/bootstrap-theme.min.css
#fi
#if [[ ! -e $1/bootstrap.min.js ]]; then
#    wget http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js -O $1/bootstrap.min.js
#fi
#if [[ ! -e $1/bootstrap-table.min.css ]]; then
#    wget http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.0/bootstrap-table.min.css -O $1/bootstrap-table.min.css
#fi
#if [[ ! -e $1/bootstrap-table.min.js ]]; then
#    wget http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.0/bootstrap-table.min.js -O $1/bootstrap-table.min.js
#fi
#if [[ ! -e $1/bootstrap-table-locale-all.min.js ]]; then
#    wget http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.0/bootstrap-table-locale-all.min.js -O $1/bootstrap-table-locale-all.min.js
#fi
#if [[ ! -e $1/bootstrap-table-editable.min.js ]]; then
#    wget http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.0/extensions/editable/bootstrap-table-editable.min.js -O $1/bootstrap-table-editable.min.js
#fi
#if [[ ! -e $1/bootstrap-table-export.min.js ]]; then
#    wget http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.0/extensions/export/bootstrap-table-export.min.js -O $1/bootstrap-table-export.min.js
#fi



