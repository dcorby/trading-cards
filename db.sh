adb exec-out run-as com.example.tradingcards cat databases/tradingCards.db > tradingCards.db
# These files disappear after copy, but you must copy them. Is SQLite running a process or something?
adb exec-out run-as com.example.tradingcards cat databases/tradingCards.db-shm > tradingCards.db-shm
adb exec-out run-as com.example.tradingcards cat databases/tradingCards.db-wal > tradingCards.db-wal
