#! /bin/sh

SIZE=25

generate() {
	FILE=$1
	FUNC=$2
	printf '<table id="%s-map">' $FUNC > ${FILE}
	ROWS=0

	while [ $ROWS -lt ${SIZE} ]
	do
		COLS=0
		echo "<tr>\c" >> ${FILE}
		while [ $COLS -lt ${SIZE} ]
		do
			#echo "<td><input type='checkbox' onclick='fire(\c" >> ${FILE}
			printf "<td><input id='%s-%s-%s' type='radio' name='%s' onclick='%s(%2d,%2d)'></td>" $FUNC $ROWS $COLS $FUNC $FUNC $ROWS $COLS >> ${FILE}
			COLS=$[ $COLS + 1 ]
		done
		ROWS=$[ $ROWS + 1 ]
		echo "</tr>\c" >> ${FILE}
	done
	echo "</table>" >> ${FILE}
}

generate src/main/webapp/opptable.html fire
generate src/main/webapp/owntable.html place
