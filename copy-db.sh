adb push tradingCards.db /sdcard/tradingCards.db
adb push tradingCards.db-shm /sdcard/tradingCards.db-shm
adb push tradingCards.db-wal /sdcard/tradingCards.db-wal
cat <<EOF | adb shell
run-as com.example.tradingcards
cat /sdcard/tradingCards.db > /data/data/com.example.tradingcards/databases/tradingCards.db
cat /sdcard/tradingCards.db-shm > /data/data/com.example.tradingcards/databases/tradingCards.db-shm
cat /sdcard/tradingCards.db-wal > /data/data/com.example.tradingcards/databases/tradingCards.db-wal
exit
exit
EOF
