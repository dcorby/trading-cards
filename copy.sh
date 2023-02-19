adb push foo.txt /sdcard/foo.txt
cat <<EOF | adb shell
run-as com.example.tradingcards
cat /sdcard/foo.txt > /data/data/com.example.tradingcards/databases/foo.txt
exit
exit
EOF
