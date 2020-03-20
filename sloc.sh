find app/src/main -type f \( -name '*.kt' -o -name '*.xml' \) -exec grep '[a-zA-Z0-9\{\}\(\)<\>\/\*\.]' {} \; | wc -l
