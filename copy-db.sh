adb push tradingCards.db /sdcard/tradingCards.db
cat <<EOF | adb shell
run-as com.example.tradingcards
cat /sdcard/tradingCards.db > /data/data/com.example.tradingcards/databases/tradingCards.db
exit
exit
EOF
